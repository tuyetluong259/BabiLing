package com.example.babiling.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberBitmapFromAssets(imagePath: String): Bitmap? {
    val context = LocalContext.current
    return remember(imagePath) {
        try {
            context.assets.open(imagePath).use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
