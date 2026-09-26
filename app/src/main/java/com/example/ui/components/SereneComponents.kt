package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.InkTeal
import com.example.ui.theme.InkTealDeep
import com.example.ui.theme.ParchmentBg
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft

/**
 * Draws a subtle parchment diamond crosshatch pattern as featured in the design brief.
 */
@Composable
fun ParchmentBackground(
  modifier: Modifier = Modifier,
  isDark: Boolean = false,
  content: @Composable () -> Unit
) {
  val bgColor = if (isDark) InkTealDeep else ParchmentBg
  val lineColor = if (isDark) Color(0x18B8863B) else Color(0x12B8863B)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(bgColor)
      .drawBehind {
        val step = 32.dp.toPx()
        val width = size.width
        val height = size.height

        // Subtle 45-degree diagonal lines
        var x = -height
        while (x < width + height) {
          drawLine(
            color = lineColor,
            start = Offset(x, 0f),
            end = Offset(x + height, height),
            strokeWidth = 1f
          )
          x += step
        }
        // Opposite diagonal lines
        x = -height
        while (x < width + height) {
          drawLine(
            color = lineColor,
            start = Offset(x, height),
            end = Offset(x + height, 0f),
            strokeWidth = 1f
          )
          x += step
        }
      }
  ) {
    content()
  }
}

/**
 * The Signature 15s Circular Dial Ring from the Home Screen mockup.
 */
@Composable
fun DailyMomentRing(
  progressPercent: Float, // 0.0 to 1.0
  todayDone: Int,
  dailyGoal: Int,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val animatedProgress by animateFloatAsState(
    targetValue = progressPercent.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 800),
    label = "ring_progress"
  )

  Column(
    modifier = modifier.clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = null,
      onClick = onClick
    ),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(164.dp)
        .testTag("daily_moment_ring")
    ) {
      // Progress Arc Canvas
      Canvas(modifier = Modifier.size(164.dp)) {
        val strokeWidth = 14.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
        val arcSize = Size(radius * 2, radius * 2)

        // Background track ring
        drawArc(
          color = ParchmentBorder.copy(alpha = 0.8f),
          startAngle = -90f,
          sweepAngle = 360f,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Active Bronze progress arc
        drawArc(
          brush = Brush.sweepGradient(
            colors = listOf(BronzeGold, BronzeGoldLight, BronzeGold)
          ),
          startAngle = -90f,
          sweepAngle = 360f * animatedProgress,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }

      // Inner Dark Ink Disc
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(130.dp)
          .clip(CircleShape)
          .background(InkTeal)
          .border(1.5.dp, BronzeGoldLight.copy(alpha = 0.4f), CircleShape)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "15s",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            color = BronzeGoldLight,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "TODAY ${(animatedProgress * 100).toInt()}%",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            color = ParchmentSurface.copy(alpha = 0.85f),
            letterSpacing = 1.2.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$todayDone of $dailyGoal moments",
            fontFamily = FontFamily.SansSerif,
            fontSize = 9.sp,
            color = BronzeGoldLight.copy(alpha = 0.75f)
          )
        }
      }
    }
  }
}

/**
 * Clean Category Card with Bronze emblem from the mockup.
 */
@Composable
fun QuickCategoryCard(
  title: String,
  icon: ImageVector,
  isSelected: Boolean = false,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val borderCol = if (isSelected) BronzeGold else ParchmentBorder
  val bgCol = if (isSelected) ParchmentSurface else Color.White

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = bgCol,
    border = androidx.compose.foundation.BorderStroke(1.2.dp, borderCol),
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag("category_card_${title.lowercase()}")
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(24.dp)
          .border(1.5.dp, BronzeGold, RoundedCornerShape(6.dp))
          .padding(3.dp)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = BronzeGold,
          modifier = Modifier.size(14.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        color = if (isSelected) InkTeal else TextSoft
      )
    }
  }
}

/**
 * Editorial Bespoke Bottom Navigation Bar respecting system navigation insets.
 */
@Composable
fun SereneBottomBar(
  currentScreen: Screen,
  onNavigate: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp, horizontal = 12.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      BottomNavItem(
        screen = Screen.Home,
        label = "Home",
        activeIcon = Icons.Filled.Home,
        inactiveIcon = Icons.Outlined.Home,
        isSelected = currentScreen == Screen.Home,
        onClick = { onNavigate(Screen.Home) }
      )
      BottomNavItem(
        screen = Screen.Moment,
        label = "Moment",
        activeIcon = Icons.Filled.Timer,
        inactiveIcon = Icons.Outlined.Timer,
        isSelected = currentScreen == Screen.Moment,
        onClick = { onNavigate(Screen.Moment) }
      )
      BottomNavItem(
        screen = Screen.Library,
        label = "Library",
        activeIcon = Icons.Filled.Book,
        inactiveIcon = Icons.Outlined.Book,
        isSelected = currentScreen == Screen.Library,
        onClick = { onNavigate(Screen.Library) }
      )
      BottomNavItem(
        screen = Screen.Insights,
        label = "Insights",
        activeIcon = Icons.Filled.BarChart,
        inactiveIcon = Icons.Outlined.BarChart,
        isSelected = currentScreen == Screen.Insights,
        onClick = { onNavigate(Screen.Insights) }
      )
      BottomNavItem(
        screen = Screen.Profile,
        label = "Profile",
        activeIcon = Icons.Filled.Person,
        inactiveIcon = Icons.Outlined.Person,
        isSelected = currentScreen == Screen.Profile,
        onClick = { onNavigate(Screen.Profile) }
      )
    }
  }
}

@Composable
private fun BottomNavItem(
  screen: Screen,
  label: String,
  activeIcon: ImageVector,
  inactiveIcon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = ripple(bounded = true, radius = 24.dp),
        onClick = onClick
      )
      .padding(horizontal = 10.dp, vertical = 4.dp)
      .testTag("nav_item_${label.lowercase()}")
  ) {
    Icon(
      imageVector = if (isSelected) activeIcon else inactiveIcon,
      contentDescription = label,
      tint = if (isSelected) BronzeGold else TextSoft.copy(alpha = 0.7f),
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    // Subtle indicator dot
    Box(
      modifier = Modifier
        .size(5.dp)
        .clip(CircleShape)
        .background(if (isSelected) BronzeGold else Color.Transparent)
    )
  }
}

/**
 * Editorial Bronze/Ink button for primary actions
 */
@Composable
fun SerenePrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true
) {
  Surface(
    shape = RoundedCornerShape(24.dp),
    color = if (enabled) InkTeal else InkTeal.copy(alpha = 0.5f),
    border = androidx.compose.foundation.BorderStroke(1.dp, BronzeGoldLight.copy(alpha = 0.5f)),
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .clickable(enabled = enabled, onClick = onClick)
      .testTag("serene_primary_btn")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 14.dp, horizontal = 20.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = text,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        color = ParchmentSurface,
        letterSpacing = 0.5.sp
      )
    }
  }
}

/**
 * Elegant outlined button as shown in the design mockup
 */
@Composable
fun SereneOutlineButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(24.dp),
    color = Color.Transparent,
    border = androidx.compose.foundation.BorderStroke(1.2.dp, InkTeal),
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .clickable(onClick = onClick)
      .testTag("serene_outline_btn")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = text,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        color = InkTeal,
        letterSpacing = 0.4.sp
      )
    }
  }
}
