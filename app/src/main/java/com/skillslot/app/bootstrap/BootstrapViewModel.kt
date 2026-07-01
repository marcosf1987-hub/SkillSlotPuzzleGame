package com.skillslot.app.bootstrap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillslot.core.domain.AdsManagerContract
import com.skillslot.core.domain.BillingManagerContract
import com.skillslot.core.domain.LoadGameStateUseCase
import com.skillslot.core.domain.PremiumManagerContract
import com.skillslot.core.domain.SessionStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BootstrapViewModel @Inject constructor(
    private val loadGameStateUseCase: LoadGameStateUseCase,
    private val sessionStateHolder: SessionStateHolder,
    private val premiumManager: PremiumManagerContract,
    private val adsManager: AdsManagerContract,
    private val billingManager: BillingManagerContract,
) : ViewModel() {
    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    init {
        viewModelScope.launch {
            premiumManager.refreshPurchases()
            billingManager.initialize()
            adsManager.initialize()
            val state = loadGameStateUseCase()
            sessionStateHolder.replace(state)
            _ready.value = true
        }
    }
}
