package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.notification.FloatingPopupManager
import com.example.notification.NotificationHelper
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.UrduFontFamily

@Composable
fun OnboardingPermissionDialog(
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var hasNotifPerm by remember { mutableStateOf(NotificationHelper.hasNotificationPermission(context)) }
  val hasOverlayPerm = remember { FloatingPopupManager.canDrawOverlays(context) }

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasNotifPerm = isGranted
    NotificationHelper.scheduleReminder(context, 60L)
    if (isGranted) {
      onDismiss()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF061E1A)),
      border = BorderStroke(1.5.dp, BronzeGold),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("onboarding_permission_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Logo with glowing gold border
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .border(2.dp, BronzeGold, CircleShape)
            .background(Color.White)
        ) {
          Image(
            painter = painterResource(id = R.drawable.app_brand_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(68.dp),
            contentScale = ContentScale.Fit
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "السلام علیکم ورحمۃ اللہ",
          fontFamily = UrduFontFamily,
          fontSize = 19.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFFFF4D6),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "15 Seconds 4 Allah • یاد دہانی کی اجازت",
          fontFamily = FontFamily.Serif,
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Bold,
          color = BronzeGoldLight,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Feature card explaining why permission is required
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0x330E3B33),
          border = BorderStroke(1.dp, BronzeGold.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = BronzeGoldLight, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "ہر گھنٹے بعد ذکر کا مختصر پیغام",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFFFE8B2)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "دن بھر مصروفیت میں صرف 15 سیکنڈز کے لیے دل کو یادِ الٰہی سے منور رکھنے کے لیے نوٹیفکیشن کی اجازت دیجیے۔",
              fontFamily = UrduFontFamily,
              fontSize = 12.sp,
              color = Color(0xFFE2E8F0),
              lineHeight = 18.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Big Action Button: One-Tap Enable All
        Button(
          onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
              NotificationHelper.scheduleReminder(context, 60L)
              onDismiss()
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = BronzeGold,
            contentColor = Color(0xFF09221D)
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("enable_reminders_btn")
        ) {
          Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "تمام یاد دہانیاں فعال کریں (Enable Reminders)",
            fontFamily = UrduFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = {
            NotificationHelper.scheduleReminder(context, 60L)
            onDismiss()
          },
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, Color(0x55E2E8F0)),
          modifier = Modifier.fillMaxWidth().height(42.dp)
        ) {
          Text(
            text = "بعد میں (Later)",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
          )
        }
      }
    }
  }
}
