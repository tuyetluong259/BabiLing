package com.example.babiling.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

object SoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, assetPath: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()

            val afd = context.assets.openFd(assetPath)
            mediaPlayer?.apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("SoundPlayer", "Error playing sounds: $e")
        }
    }
}
