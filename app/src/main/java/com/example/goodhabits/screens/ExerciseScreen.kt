package com.example.goodhabits.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.goodhabits.dataMusic.Song
import com.example.goodhabits.viewmodel.ExerciseViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExerciseScreen(viewModel: ExerciseViewModel) {
    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    val permissionState = rememberMultiplePermissionsState(permissions = requiredPermissions)

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var userMarker: Marker? by remember { mutableStateOf(null) }
    var isFirstLocationUpdate by remember { mutableStateOf(true) }

    if (!uiState.isTracking) {
        isFirstLocationUpdate = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map View
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(18.0)
                    userMarker = Marker(this).apply { setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) }
                    overlays.add(userMarker)
                }
            },
            update = { mapView ->
                uiState.location?.let { currentLocation ->
                    val geoPoint = GeoPoint(currentLocation.latitude, currentLocation.longitude)
                    if (isFirstLocationUpdate) {
                        mapView.controller.animateTo(geoPoint)
                        isFirstLocationUpdate = false
                    }
                    userMarker?.position = geoPoint
                    mapView.invalidate()
                }
            }
        )

        // Song List Dialog
        if (uiState.showSongList) {
            SongListDialog(
                songs = uiState.songs,
                onSongSelected = { song -> viewModel.onSongSelected(context, song) },
                onDismiss = { viewModel.onDismissSongList() }
            )
        }

        // Control Buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = {
                    if (uiState.isTracking) {
                        viewModel.stopExercise(context)
                    } else {
                        if (permissionState.allPermissionsGranted) {
                            viewModel.onStartExerciseClicked(context)
                        } else {
                            permissionState.launchMultiplePermissionRequest()
                        }
                    }
                }) {
                    Text(text = if (uiState.isTracking) "Detener Ejercicio" else "Iniciar Ejercicio")
                }

                if (uiState.isTracking) {
                    IconButton(onClick = { viewModel.toggleMusic(context) }) {
                        Icon(
                            imageVector = if (uiState.isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (uiState.isMusicPlaying) "Pausar música" else "Reproducir música",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SongListDialog(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Elige una canción", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                if (songs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                        Text("Buscando música...")
                    }
                } else {
                    LazyColumn {
                        items(songs) { song ->
                            SongListItem(song = song, onClick = { onSongSelected(song) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SongListItem(song: Song, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(song.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
