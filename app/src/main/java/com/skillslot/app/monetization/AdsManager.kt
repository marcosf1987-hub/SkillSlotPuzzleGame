package com.skillslot.app.monetization

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.skillslot.core.domain.AdsManagerContract
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class AdsManager @Inject constructor(
    private val activityProvider: ActivityProvider,
) : AdsManagerContract {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var lastInterstitialAtMs: Long = 0L
    private val mutex = Mutex()

    override suspend fun initialize() {
        val activity = activityProvider.currentActivity() ?: return
        suspendCancellableCoroutine { cont ->
            MobileAds.initialize(activity) {
                cont.resume(Unit)
            }
        }
        preloadAds()
    }

    override fun canShowAds(isPremium: Boolean): Boolean = !isPremium

    override fun canShowInterstitial(isPremium: Boolean): Boolean {
        if (isPremium) return false
        val elapsed = System.currentTimeMillis() - lastInterstitialAtMs
        return elapsed >= MonetizationConstants.INTERSTITIAL_COOLDOWN_MS
    }

    override suspend fun preloadAds() {
        mutex.withLock {
            loadInterstitial()
            loadRewarded()
        }
    }

    override suspend fun showInterstitial(onFinished: () -> Unit) {
        val activity = activityProvider.currentActivity()
        if (activity == null) {
            onFinished()
            return
        }
        val ad = interstitialAd
        if (ad == null) {
            onFinished()
            loadInterstitial()
            return
        }
        suspendCancellableCoroutine { cont ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastInterstitialAtMs = System.currentTimeMillis()
                    loadInterstitial()
                    if (cont.isActive) cont.resume(Unit)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadInterstitial()
                    if (cont.isActive) cont.resume(Unit)
                }
            }
            ad.show(activity)
        }
        onFinished()
    }

    override suspend fun showRewarded(onReward: () -> Unit, onFinished: () -> Unit) {
        val activity = activityProvider.currentActivity()
        if (activity == null) {
            onFinished()
            return
        }
        val ad = rewardedAd
        if (ad == null) {
            onFinished()
            loadRewarded()
            return
        }
        suspendCancellableCoroutine { cont ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewarded()
                    if (cont.isActive) cont.resume(Unit)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    rewardedAd = null
                    loadRewarded()
                    if (cont.isActive) cont.resume(Unit)
                }
            }
            ad.show(activity) { onReward() }
        }
        onFinished()
    }

    private fun loadInterstitial() {
        val activity = activityProvider.currentActivity() ?: return
        InterstitialAd.load(
            activity,
            AdUnitIds.INTERSTITIAL,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            },
        )
    }

    private fun loadRewarded() {
        val activity = activityProvider.currentActivity() ?: return
        RewardedAd.load(
            activity,
            AdUnitIds.REWARDED,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            },
        )
    }
}
