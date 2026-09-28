package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft
import com.example.ui.theme.UrduFontFamily
import com.example.util.AppTimeHelper
import java.util.TimeZone

@Composable
fun TimezoneSelectionDialog(
  selectedTimezoneId: String,
  onDismissRequest: () -> Unit,
  onSelectTimezone: (String) -> Unit
) {
  val effectiveTz = AppTimeHelper.getEffectiveTimeZone(selectedTimezoneId)
  val currentTimeStr = AppTimeHelper.formatCurrentTime(effectiveTz)
  val (urduGreeting, _) = AppTimeHelper.getDynamicGreeting(effectiveTz)

  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = ParchmentCard),
      border = BorderStroke(1.5.dp, BronzeGold),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 20.dp)
    ) {
      Column(
        modifier = Modifier.padding(18.dp)
      ) {
        // Title Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(InkTeal.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Public,
                contentDescription = null,
                tint = InkTeal,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "وقت اور ٹائم زون (Time & Timezone)",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = InkTeal
              )
              Text(
                text = "سلام اور اوقاتِ نماز کی درستگی",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.5.sp,
                color = TextSoft
              )
            }
          }

          IconButton(
            onClick = onDismissRequest,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextSoft, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Time & Greeting Preview Box
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = InkTeal.copy(alpha = 0.08f),
          border = BorderStroke(1.dp, InkTeal.copy(alpha = 0.25f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "موجودہ فعال وقت (Active Clock)",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.5.sp,
                color = TextSoft
              )
              Text(
                text = currentTimeStr,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = InkTeal
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = BronzeGold.copy(alpha = 0.18f),
              border = BorderStroke(0.8.dp, BronzeGold)
            ) {
              Text(
                text = urduGreeting,
                fontFamily = UrduFontFamily,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = BronzeGold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "اپنا ملک یا شہر منتخب کریں تاکہ سلام (Good morning/Good night) اور نماز کے اوقات بالکل درست رہیں:",
          fontFamily = UrduFontFamily,
          fontSize = 11.sp,
          color = TextPrimary,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Timezones List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 340.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(AppTimeHelper.supportedTimezones) { option ->
            val isSelected = option.id == selectedTimezoneId ||
                (selectedTimezoneId.isEmpty() && option.id == "Asia/Karachi")

            val optionTz = AppTimeHelper.getEffectiveTimeZone(option.id)
            val optionTimeStr = AppTimeHelper.formatCurrentTime(optionTz)

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) InkTeal.copy(alpha = 0.09f) else Color.White,
              border = BorderStroke(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) InkTeal else ParchmentBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  onSelectTimezone(option.id)
                  onDismissRequest()
                }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  Text(
                    text = option.flagEmoji,
                    fontSize = 20.sp
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = option.urduName,
                      fontFamily = UrduFontFamily,
                      fontSize = 12.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) InkTeal else TextPrimary
                    )
                    Text(
                      text = "${option.englishName} • ${option.offsetLabel}",
                      fontFamily = FontFamily.SansSerif,
                      fontSize = 9.5.sp,
                      color = TextSoft
                    )
                  }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ParchmentSubtle
                  ) {
                    Text(
                      text = optionTimeStr,
                      fontFamily = FontFamily.SansSerif,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = InkTeal,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(6.dp))

                  RadioButton(
                    selected = isSelected,
                    onClick = {
                      onSelectTimezone(option.id)
                      onDismissRequest()
                    },
                    colors = RadioButtonDefaults.colors(
                      selectedColor = InkTeal,
                      unselectedColor = TextSoft.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.size(24.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "✓ تبدیلی کے فوراً بعد سلام، تاریخ اور نماز کے اوقات خودبخود اپڈیٹ ہو جائیں گے۔",
          fontFamily = FontFamily.SansSerif,
          fontSize = 10.sp,
          color = TextSoft,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}
