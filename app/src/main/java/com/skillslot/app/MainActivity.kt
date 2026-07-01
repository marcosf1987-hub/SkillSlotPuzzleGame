package com.skillslot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.skillslot.app.monetization.ActivityProvider
import com.skillslot.app.navigation.SkillSlotNavHost
import com.skillslot.app.ui.theme.SkillSlotTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var activityProvider: ActivityProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillSlotTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SkillSlotNavHost()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activityProvider.setActivity(this)
    }

    override fun onPause() {
        activityProvider.setActivity(null)
        super.onPause()
    }
}
