package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.notification.NotificationHelper
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.FloatingReminderBanner
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MomentScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.TextSoft
import com.example.util.ShareHelper

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      NotificationHelper.scheduleReminder(this, 60L)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Ensure Notification Permission is requested on Android 13+ (API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }

    viewModel.handleIntent(intent)

    setContent {
      MyApplicationTheme {
        MainApp(viewModel = viewModel)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    viewModel.handleIntent(intent)
  }
}

@Composable
fun MainApp(viewModel: MainViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val showFloatingBanner by viewModel.showFloatingBanner.collectAsState()
  val floatingBannerDhikr by viewModel.floatingBannerDhikr.collectAsState()

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      bottomBar = {
        NavigationBar(
          containerColor = ParchmentCard,
          tonalElevation = 8.dp
        ) {
          NavItem(
            label = "ہوم",
            icon = Icons.Filled.Home,
            selected = currentScreen is Screen.Home,
            onClick = { viewModel.navigateTo(Screen.Home) }
          )
          NavItem(
            label = "15s ذکر",
            icon = Icons.Filled.Timer,
            selected = currentScreen is Screen.Moment,
            onClick = { viewModel.navigateTo(Screen.Moment) }
          )
          NavItem(
            label = "لائبریری",
            icon = Icons.Filled.Book,
            selected = currentScreen is Screen.Library,
            onClick = { viewModel.navigateTo(Screen.Library) }
          )
          NavItem(
            label = "اسٹریک",
            icon = Icons.Filled.LocalFireDepartment,
            selected = currentScreen is Screen.Insights,
            onClick = { viewModel.navigateTo(Screen.Insights) }
          )
          NavItem(
            label = "اکاؤنٹ",
            icon = Icons.Filled.Person,
            selected = currentScreen is Screen.Profile,
            onClick = { viewModel.navigateTo(Screen.Profile) }
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (currentScreen) {
          is Screen.Home -> HomeScreen(viewModel = viewModel)
          is Screen.Moment -> MomentScreen(viewModel = viewModel)
          is Screen.Library -> LibraryScreen(viewModel = viewModel)
          is Screen.Insights -> InsightsScreen(viewModel = viewModel)
          is Screen.Profile -> ProfileScreen(viewModel = viewModel)
        }
      }
    }

    // In-App 5-Second Non-Blocking Floating Top Banner
    if (showFloatingBanner) {
      val context = LocalContext.current
      androidx.compose.runtime.LaunchedEffect(showFloatingBanner) {
        kotlinx.coroutines.delay(5500L)
        viewModel.dismissFloatingBanner()
      }
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 40.dp)
      ) {
        FloatingReminderBanner(
          dhikr = floatingBannerDhikr,
          countdownSeconds = 5,
          onDismiss = { viewModel.dismissFloatingBanner() },
          onBegin = {
            viewModel.dismissFloatingBanner()
            viewModel.selectDhikrForMoment(floatingBannerDhikr, startImmediately = true)
          },
          onShare = {
            ShareHelper.shareDhikrPoster(context, floatingBannerDhikr)
          },
          onWhatsAppShare = {
            ShareHelper.shareToWhatsApp(context, floatingBannerDhikr)
          }
        )
      }
    }
  }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.NavItem(
  label: String,
  icon: ImageVector,
  selected: Boolean,
  onClick: () -> Unit
) {
  NavigationBarItem(
    selected = selected,
    onClick = onClick,
    icon = {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (selected) InkTeal else TextSoft
      )
    },
    label = {
      Text(
        text = label,
        fontSize = 11.sp,
        color = if (selected) InkTeal else TextSoft
      )
    },
    colors = NavigationBarItemDefaults.colors(
      indicatorColor = BronzeGoldLight.copy(alpha = 0.4f),
      selectedIconColor = InkTeal,
      unselectedIconColor = TextSoft
    )
  )
}
