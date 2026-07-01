package com.skillslot.app.monetization

import com.skillslot.core.domain.AdsManagerContract
import com.skillslot.core.domain.BillingManagerContract
import com.skillslot.core.domain.PremiumManagerContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MonetizationModule {
    @Binds
    @Singleton
    abstract fun bindAdsManager(impl: AdsManager): AdsManagerContract

    @Binds
    @Singleton
    abstract fun bindBillingManager(impl: BillingManager): BillingManagerContract

    @Binds
    @Singleton
    abstract fun bindPremiumManager(impl: PremiumManager): PremiumManagerContract
}
