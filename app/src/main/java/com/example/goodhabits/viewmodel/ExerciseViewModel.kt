package com.example.goodhabits.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.net.Uri
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodhabits.dataMusic.MusicRepository
import com.example.goodhabits.dataMusic.Song
import com.example.goodhabits.services.LocationService
import com.example.goodhabits.services.MusicService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseUiState(
    val location: Location? = null,
    val isTracking: Boolean = false,
    val isMusicPlaying: Boolean = false,
    val songs: List<Song> = emptyList(),
    val showSongList: Boolean = false
)

class ExerciseViewModel(private val musicRepository: MusicRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private var locationService: LocationService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocationBinder
            locationService = binder.getService()
            isBound = true
            locationService?.startLocationUpdates()

            viewModelScope.launch {
                locationService?.locationUpdates?.collect { newLocation ->
                    _uiState.update { it.copy(location = newLocation) }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            isBound = false
            _uiState.update { it.copy(isTracking = false) }
        }
    }

    fun onStartExerciseClicked(context: Context) {
        if (uiState.value.songs.isEmpty()) {
            loadSongs(context)
        }
        _uiState.update { it.copy(showSongList = true) }
    }

    fun onSongSelected(context: Context, song: Song) {
        _uiState.update { it.copy(showSongList = false, isTracking = true, isMusicPlaying = true) }
        
        // Start Location Tracking
        Intent(context, LocationService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        // Play selected song
        playMusic(context, song.contentUri)
    }

    fun onDismissSongList() {
        _uiState.update { it.copy(showSongList = false) }
    }

    fun stopExercise(context: Context) {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
        locationService?.stopLocationUpdates()
        stopMusic(context)
        _uiState.update { ExerciseUiState() } // Reset state
    }

    fun toggleMusic(context: Context) {
        if (uiState.value.isMusicPlaying) {
            pauseMusic(context)
        } else {
            // We need to know which song to play. For now, we assume the service remembers.
            playMusic(context, null)
        }
    }

    private fun loadSongs(context: Context) {
        viewModelScope.launch {
            val songList = musicRepository.getAudioFiles(context)
            _uiState.update { it.copy(songs = songList) } // Changed to show all songs
        }
    }

    private fun playMusic(context: Context, songUri: Uri?) {
        Intent(context, MusicService::class.java).also { intent ->
            intent.action = MusicService.ACTION_PLAY
            intent.data = songUri
            context.startService(intent)
        }
        _uiState.update { it.copy(isMusicPlaying = true) }
    }

    private fun pauseMusic(context: Context) {
        Intent(context, MusicService::class.java).also { intent ->
            intent.action = MusicService.ACTION_PAUSE
            context.startService(intent)
        }
        _uiState.update { it.copy(isMusicPlaying = false) }
    }

    private fun stopMusic(context: Context) {
        Intent(context, MusicService::class.java).also { intent ->
            context.startService(intent.setAction(MusicService.ACTION_STOP))
        }
        _uiState.update { it.copy(isMusicPlaying = false) }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
