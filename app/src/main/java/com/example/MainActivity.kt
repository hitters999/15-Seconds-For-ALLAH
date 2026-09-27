package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.DhikrReminderPopupDialog
import com.example.ui.components.SereneBottomBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MomentScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private var viewModelInstance: MainViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val vm: MainViewModel = viewModel()
      viewModelInstance = vm

      // Handle any incoming intent from notifications
      LaunchedEffect(intent) {
        vm.handleIntent(intent)
      }

      MainAppContent(viewModel = vm)
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    viewModelInstance?.handleIntent(intent)
  }
}

@Composable
fun MainAppContent(viewModel: MainViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val userSettings by viewModel.userSettings.collectAsState()
  val showReminderPopup by viewModel.showReminderPopup.collectAsState()
  val popupDhikr by viewModel.popupDhikr.collectAsState()
  val showFloatingBanner by viewModel.showFloatingBanner.collectAsState()
  val floatingBannerDhikr by viewModel.floatingBannerDhikr.collectAsState()

  // Request Notification permission on Android 13+ (API 33+)
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { /* Handled */ }
  )

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
  }

  val isDark = when (userSettings.isDarkMode) {
    true -> true
    false -> false
    null -> isSystemInDarkTheme()
  }

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

        // 5-Second Non-Blocking Floating Top Banner (Matches User Design)
        androidx.compose.animation.AnimatedVisibility(
          visible = showFloatingBanner,
          enter = androidx.compose.animation.slideInVertically(initialOffsetY = { -it }) + androidx.compose.animation.fadeIn(),
          exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { -it }) + androidx.compose.animation.fadeOut(),
          modifier = Modifier.align(androidx.compose.ui.Alignment.TopCenter)
        ) {
          com.example.ui.components.FloatingReminderBanner(
            dhikr = floatingBannerDhikr,
            durationSeconds = 5,
            onDismiss = { viewModel.dismissFloatingBanner() },
            onStartMoment = {
              viewModel.dismissFloatingBanner()
              viewModel.selectDhikrForMoment(floatingBannerDhikr, startImmediately = true)
            }
          )
        }

        // Active Dhikr Reminder Popup Dialog with Brand Logo & Urdu Translation
        if (showReminderPopup) {
          DhikrReminderPopupDialog(
            dhikr = popupDhikr,
            onStartMoment = {
              viewModel.dismissReminderPopup()
              viewModel.selectDhikrForMoment(popupDhikr, startImmediately = true)
            },
            onDismiss = {
              viewModel.dismissReminderPopup()
            }
          )
        }
      }
    }
  }
}
