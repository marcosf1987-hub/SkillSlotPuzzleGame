package com.skillslot.app.bootstrap

import android.content.Context
import com.skillslot.core.model.ProgressionConfig
import com.skillslot.core.model.ProgressionSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import org.json.JSONObject

@Singleton
class ProgressionConfigLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun load() {
        val json = context.assets.open("progression_config.json").bufferedReader().readText()
        val obj = JSONObject(json)
        ProgressionConfig.applySettings(
            ProgressionSettings(
                baseThreshold = obj.optInt("baseThreshold", 500),
                growthFactor = obj.optDouble("growthFactor", 0.5),
                maxLives = obj.optInt("maxLives", 3),
                consumePointsOnPuzzleStart = obj.optBoolean("consumePointsOnPuzzleStart", true),
                pairPayout = obj.optInt("pairPayout", 50),
                consolationMin = obj.optInt("consolationMin", 10),
                consolationMax = obj.optInt("consolationMax", 30),
            ),
        )
    }
}
