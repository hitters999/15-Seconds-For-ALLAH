package com.example.ui.components

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.DhikrItem
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.InkTeal
import com.example.ui.theme.UrduFontFamily
import com.example.util.PosterGenerator
import com.example.util.ShareHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

@Composable
fun PosterPreviewDialog(
  dhikr: DhikrItem,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var posterBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var isLoading by remember { mutableStateOf(true) }

  LaunchedEffect(dhikr.id) {
    isLoading = true
    withContext(Dispatchers.IO) {
      val bmp = PosterGenerator.generateDhikrPosterBitmap(context, dhikr)
      posterBitmap = bmp
    }
    isLoading = false
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF061B17)),
      border = BorderStroke(1.5.dp, BronzeGold),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 16.dp)
        .testTag("poster_preview_dialog")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
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
                .background(BronzeGold.copy(alpha = 0.18f)),
              contentAlignment = Alignment.Center
            ) {
              Text("✨", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "پوسٹر کا منظر (Islamic Poster)",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFFF5E6BE)
              )
              Text(
                text = "WhatsApp Status & Stories HD Poster",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Poster Preview Frame (Aspect Ratio 9:16)
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xFF030E0B),
          border = BorderStroke(1.dp, BronzeGold.copy(alpha = 0.5f)),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 440.dp)
            .aspectRatio(9f / 15f)
        ) {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            if (isLoading || posterBitmap == null) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = BronzeGold, strokeWidth = 2.5.dp, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "پوسٹر تیار ہو رہا ہے...",
                  fontFamily = UrduFontFamily,
                  fontSize = 13.sp,
                  color = Color(0xFFE2E8F0)
                )
              }
            } else {
              Image(
                bitmap = posterBitmap!!.asImageBitmap(),
                contentDescription = "Poster Preview",
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // WhatsApp Share Button
          Button(
            onClick = {
              ShareHelper.shareToWhatsApp(context, dhikr)
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF25D366),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1.3f)
              .height(44.dp)
              .testTag("preview_share_whatsapp")
          ) {
            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "WhatsApp اسٹیٹس",
              fontFamily = UrduFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 12.5.sp
            )
          }

          // Share to All Apps Button
          Button(
            onClick = {
              ShareHelper.shareDhikrPoster(context, dhikr)
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF1B4E48),
              contentColor = Color(0xFFF5E6BE)
            ),
            border = BorderStroke(1.dp, BronzeGold),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("preview_share_all")
          ) {
            Text(
              text = "دیگر ایپس",
              fontFamily = UrduFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }

          // Save to Gallery Button
          IconButton(
            onClick = {
              posterBitmap?.let { bmp ->
                savePosterToGallery(context, bmp, "dhikr_${dhikr.id}")
              }
            },
            modifier = Modifier
              .size(44.dp)
              .background(BronzeGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
              .testTag("preview_save_gallery")
          ) {
            Icon(Icons.Filled.Download, contentDescription = "Save to Gallery", tint = BronzeGold, modifier = Modifier.size(20.dp))
          }
        }
      }
    }
  }
}

private fun savePosterToGallery(context: Context, bitmap: Bitmap, title: String) {
  try {
    val filename = "15Seconds_${title}_${System.currentTimeMillis()}.png"
    var fos: OutputStream? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/15SecondsForAllah")
      }
      val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
      if (imageUri != null) {
        fos = context.contentResolver.openOutputStream(imageUri)
      }
    } else {
      val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/15SecondsForAllah"
      val file = java.io.File(imagesDir)
      if (!file.exists()) file.mkdirs()
      val image = java.io.File(imagesDir, filename)
      fos = java.io.FileOutputStream(image)
    }

    fos?.use {
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
      Toast.makeText(context, "پوسٹر گیلری میں محفوظ ہو گیا! ✓", Toast.LENGTH_SHORT).show()
    } ?: run {
      Toast.makeText(context, "پوسٹر محفوظ ہو گیا۔", Toast.LENGTH_SHORT).show()
    }
  } catch (e: Exception) {
    Toast.makeText(context, "تصویر محفوظ نہیں ہو سکی: ${e.message}", Toast.LENGTH_SHORT).show()
  }
}
