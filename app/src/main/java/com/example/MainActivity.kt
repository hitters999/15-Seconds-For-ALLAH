package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.SereneBottomBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MomentScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MainAppContent()
    }
  }
}

@Composable
fun MainAppContent(viewModel: MainViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val userSettings by viewModel.userSettings.collectAsState()

  val isDark = when (userSettings.isDarkMode) {
    true -> true
    false -> false
    null -> isSystemInDarkTheme()
  }

  // Handle system back navigation gracefully
  if (currentScreen != Screen.Home && currentScreen != Screen.Splash) {
    BackHandler {
      viewModel.navigateTo(Screen.Home)
    }
  }

  MyApplicationTheme(darkTheme = isDark) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = {
        if (currentScreen != Screen.Splash && currentScreen != Screen.Moment) {
          SereneBottomBar(
            currentScreen = currentScreen,
            onNavigate = { screen -> viewModel.navigateTo(screen) }
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(
            bottom = if (currentScreen != Screen.Splash && currentScreen != Screen.Moment) {
              innerPadding.calculateBottomPadding()
            } else {
              androidx.compose.ui.unit.Dp.Hairline
            }
          )
      ) {
        Crossfade(
          targetState = currentScreen,
          animationSpec = tween(durationMillis = 280),
          label = "screen_transition"
        ) { screen ->
          when (screen) {
            Screen.Splash -> SplashScreen(
              onContinue = { viewModel.navigateTo(Screen.Home) }
            )
            Screen.Home -> HomeScreen(viewModel = viewModel)
            Screen.Moment -> MomentScreen(viewModel = viewModel)
            Screen.Library -> LibraryScreen(viewModel = viewModel)
            Screen.Insights -> InsightsScreen(viewModel = viewModel)
            Screen.Profile -> ProfileScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}
