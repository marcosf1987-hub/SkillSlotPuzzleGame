package com.skillslot.app.ui.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillslot.core.domain.BillingManagerContract
import com.skillslot.core.domain.BillingResult
import com.skillslot.core.domain.LoadGameStateUseCase
import com.skillslot.core.domain.PremiumManagerContract
import com.skillslot.core.domain.SessionStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PremiumUiState(
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,
)

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val billingManager: BillingManagerContract,
    private val premiumManager: PremiumManagerContract,
    private val loadGameStateUseCase: LoadGameStateUseCase,
    private val sessionStateHolder: SessionStateHolder,
) : ViewModel() {
    private val loading = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PremiumUiState> = combine(
        premiumManager.isPremium,
        loading,
        message,
    ) { isPremium, isLoading, msg ->
        PremiumUiState(isPremium = isPremium, isLoading = isLoading, message = msg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PremiumUiState())

    fun purchase() {
        viewModelScope.launch {
            loading.value = true
            message.value = null
            when (val result = billingManager.purchasePremium()) {
                BillingResult.Success -> {
                    message.value = "¡Premium activado! Gracias por tu compra."
                    refreshSession()
                }
                BillingResult.Cancelled -> message.value = "Compra cancelada."
                is BillingResult.Error -> message.value = result.message
            }
            loading.value = false
        }
    }

    fun restore() {
        viewModelScope.launch {
            loading.value = true
            message.value = null
            when (val result = billingManager.restorePurchases()) {
                BillingResult.Success -> {
                    message.value = "Compras restauradas."
                    refreshSession()
                }
                is BillingResult.Error -> message.value = result.message
                BillingResult.Cancelled -> message.value = "Sin compras que restaurar."
            }
            loading.value = false
        }
    }

    private suspend fun refreshSession() {
        premiumManager.refreshPurchases()
        sessionStateHolder.replace(loadGameStateUseCase())
    }
}
