package com.skillslot.app.monetization

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.skillslot.core.data.preferences.UserPreferencesRepository
import com.skillslot.core.domain.BillingManagerContract
import com.skillslot.core.domain.BillingResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.android.billingclient.api.BillingResult as PlayBillingResult

@Singleton
class BillingManager @Inject constructor(
    private val activityProvider: ActivityProvider,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BillingManagerContract, PurchasesUpdatedListener {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private var billingClient: BillingClient? = null
    private var premiumProduct: ProductDetails? = null
    private var purchaseContinuation: CompletableDeferred<BillingResult>? = null

    override suspend fun initialize() {
        mutex.withLock {
            if (billingClient != null) return
            val activity = activityProvider.currentActivity() ?: return
            billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .build(),
                )
                .build()
            connectBillingClient()
            queryProductDetails()
            restorePurchasesInternal()
        }
    }

    override suspend fun purchasePremium(): BillingResult {
        val activity = activityProvider.currentActivity()
            ?: return BillingResult.Error("Actividad no disponible")
        val product = premiumProduct
            ?: return BillingResult.Error("Producto premium no disponible")
        val client = billingClient
            ?: return BillingResult.Error("Billing no inicializado")

        val deferred = CompletableDeferred<BillingResult>()
        purchaseContinuation = deferred

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product)
            .build()
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val launchResult = client.launchBillingFlow(activity, flowParams)
        if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
            purchaseContinuation = null
            return BillingResult.Error(launchResult.debugMessage)
        }
        return deferred.await()
    }

    override suspend fun restorePurchases(): BillingResult = restorePurchasesInternal()

    override fun onPurchasesUpdated(result: PlayBillingResult, purchases: MutableList<Purchase>?) {
        val continuation = purchaseContinuation
        purchaseContinuation = null
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { handlePurchase(it) }
                continuation?.complete(BillingResult.Success)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                continuation?.complete(BillingResult.Cancelled)
            }
            else -> {
                continuation?.complete(BillingResult.Error(result.debugMessage))
            }
        }
    }

    private suspend fun connectBillingClient() {
        val client = billingClient ?: return
        suspendCancellableCoroutine { cont ->
            client.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(result: PlayBillingResult) {
                        cont.resume(Unit)
                    }

                    override fun onBillingServiceDisconnected() = Unit
                },
            )
        }
    }

    private fun queryProductDetails() {
        val client = billingClient ?: return
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(MonetizationConstants.PREMIUM_SKU)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                ),
            )
            .build()
        client.queryProductDetailsAsync(params) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                premiumProduct = productDetailsList.firstOrNull()
            }
        }
    }

    private suspend fun restorePurchasesInternal(): BillingResult {
        val client = billingClient ?: return BillingResult.Error("Billing no inicializado")
        return suspendCancellableCoroutine { cont ->
            client.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
            ) { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    val hasPremium = purchases.any { purchase ->
                        purchase.products.contains(MonetizationConstants.PREMIUM_SKU) &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                    }
                    purchases.forEach { handlePurchase(it) }
                    cont.resume(
                        if (hasPremium) BillingResult.Success else BillingResult.Error("Sin compras premium"),
                    )
                } else {
                    cont.resume(BillingResult.Error(result.debugMessage))
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (!purchase.products.contains(MonetizationConstants.PREMIUM_SKU)) return
        scope.launch {
            userPreferencesRepository.setPremium(true)
        }
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient?.acknowledgePurchase(params) { }
        }
    }
}
