package com.example.goodhabits.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.goodhabits.R

class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackUri: Uri? = null
    private var isPaused = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val trackUri = intent?.data

        when (intent?.action) {
            ACTION_PLAY -> {
                if (trackUri != null && trackUri != currentTrackUri) {
                    stopAndRelease()
                    currentTrackUri = trackUri
                    try {
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(applicationContext, currentTrackUri!!)
                            setOnPreparedListener(this@MusicService)
                            setOnErrorListener(this@MusicService)
                            prepareAsync()
                        }
                        isPaused = false
                        startForeground(MUSIC_NOTIFICATION_ID, createMusicNotification())
                    } catch (e: Exception) {
                        Log.e("MusicService", "Error setting data source", e)
                        stopSelf()
                    }
                } else if (isPaused) {
                    mediaPlayer?.start()
                    isPaused = false
                }
            }
            ACTION_PAUSE -> {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    isPaused = true
                }
            }
            ACTION_STOP -> {
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.d("MusicService", "MediaPlayer prepared. Starting playback.")
        if (!isPaused) {
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.e("MusicService", "MediaPlayer error: what: $what, extra: $extra")
        stopSelf()
        return true // Indicates we handled the error
    }

    override fun onDestroy() {
        stopAndRelease()
        super.onDestroy()
    }

    private fun stopAndRelease() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrackUri = null
    }

    private fun createMusicNotification(): android.app.Notification {
        val notificationChannelId = "MUSIC_PLAYER_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Reproduciendo música")
            .setContentText("El ejercicio está en curso.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val MUSIC_NOTIFICATION_ID = 54321
        const val ACTION_PLAY = "com.example.goodhabits.action.PLAY"
        const val ACTION_PAUSE = "com.example.goodhabits.action.PAUSE"
        const val ACTION_STOP = "com.example.goodhabits.action.STOP"
    }
}
