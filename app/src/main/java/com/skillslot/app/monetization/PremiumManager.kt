package com.skillslot.app.monetization

import com.skillslot.core.data.preferences.UserPreferencesRepository
import com.skillslot.core.domain.BillingManagerContract
import com.skillslot.core.domain.PremiumManagerContract
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class PremiumManager @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val billingManager: BillingManagerContract,
) : PremiumManagerContract {
    override val isPremium: Flow<Boolean> =
        userPreferencesRepository.preferences.map { it.isPremium }

    override suspend fun refreshPurchases() {
        billingManager.restorePurchases()
    }

    override suspend fun setPremiumForDebug(enabled: Boolean) {
        userPreferencesRepository.setPremium(enabled)
    }
}
