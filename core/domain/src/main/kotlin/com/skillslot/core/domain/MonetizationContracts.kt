package com.skillslot.core.domain

import kotlinx.coroutines.flow.Flow

interface PremiumManagerContract {
    val isPremium: Flow<Boolean>
    suspend fun refreshPurchases()
    suspend fun setPremiumForDebug(enabled: Boolean)
}

interface AdsManagerContract {
    suspend fun initialize()
    fun canShowAds(isPremium: Boolean): Boolean
    fun canShowInterstitial(isPremium: Boolean): Boolean
    suspend fun preloadAds()
    suspend fun showInterstitial(onFinished: () -> Unit)
    suspend fun showRewarded(onReward: () -> Unit, onFinished: () -> Unit)
}

interface BillingManagerContract {
    suspend fun initialize()
    suspend fun purchasePremium(): BillingResult
    suspend fun restorePurchases(): BillingResult
}

sealed interface BillingResult {
    data object Success : BillingResult
    data object Cancelled : BillingResult
    data class Error(val message: String) : BillingResult
}
