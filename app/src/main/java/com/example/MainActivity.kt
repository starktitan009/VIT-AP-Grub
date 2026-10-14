package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AppScreen
import com.example.ui.GrubViewModel
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.main.GrubMainApp
import com.example.ui.onboarding.OnboardingFlowScreen
import com.example.ui.splash.StartupSplashScreen
import com.example.ui.theme.VitApGrubTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GrubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()

            VitApGrubTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                        when (screen) {
                            AppScreen.STARTUP_SPLASH -> {
                                StartupSplashScreen(
                                    onEnterApp = { viewModel.setStartupFinished() }
                                )
                            }
                            AppScreen.ONBOARDING -> {
                                OnboardingFlowScreen(
                                    viewModel = viewModel
                                )
                            }
                            AppScreen.MAIN_APP -> {
                                GrubMainApp(
                                    viewModel = viewModel
                                )
                            }
                            AppScreen.ADMIN_PORTAL -> {
                                BackHandler {
                                    viewModel.navigateToScreen(AppScreen.MAIN_APP)
                                }
                                AdminDashboardScreen(
                                    viewModel = viewModel,
                                    onBackToStudentApp = {
                                        viewModel.navigateToScreen(AppScreen.MAIN_APP)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
