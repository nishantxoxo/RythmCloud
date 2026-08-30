package com.example.rythmcloud.ui.screens

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.Glide
import com.example.rythmcloud.data.entities.Song
import com.example.rythmcloud.player.isPlaying
import com.example.rythmcloud.player.toSong
import com.example.rythmcloud.ui.viewmodels.MainViewModel
import com.example.rythmcloud.ui.viewmodels.SongViewModel
import java.util.Locale

@Composable
fun SongScreen(
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
) {
    val songViewModel: SongViewModel = hiltViewModel()
    val currentSong by mainViewModel.curPlayingSong.observeAsState(initial = null)
    val playbackState by mainViewModel.playbackState.observeAsState(initial = null)
    val currentPosition by songViewModel.curPlayerPos.observeAsState(initial = 0L)
    val currentDuration by songViewModel.curSongDuration.observeAsState(initial = 0L)
    val song = currentSong?.toSong() ?: mainViewModel.mediaItems.value?.data?.firstOrNull()

    var isSeeking by remember { androidx.compose.runtime.mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(currentPosition.toFloat()) }

    val isPlaying = playbackState?.isPlaying == true

    if (!isSeeking) {
        sliderPosition = currentPosition.toFloat()
    }

    when {
        song == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101010)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No song selected",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101010))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AndroidView(
                        modifier = Modifier.size(260.dp),
                        factory = { context ->
                            ImageView(context).apply {
                                scaleType = ImageView.ScaleType.CENTER_CROP
                            }
                        },
                        update = { imageView ->
                            Glide.with(imageView.context)
                                .load(song.imageUri)
                                .into(imageView)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = song.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = song.subtitle,
                        color = Color.White.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Slider(
                        value = sliderPosition.coerceIn(0f, currentDuration.toFloat().coerceAtLeast(1f)),
                        onValueChange = { value ->
                            isSeeking = true
                            sliderPosition = value
                        },
                        onValueChangeFinished = {
                            isSeeking = false
                            mainViewModel.seekTo(sliderPosition.toLong())
                        },
                        valueRange = 0f..currentDuration.toFloat().coerceAtLeast(1f),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = formatDuration(currentPosition), color = Color.White)
                        Text(text = formatDuration(currentDuration), color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { mainViewModel.skipToPrevSong() }) {
                            Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", tint = Color.White)
                        }

                        IconButton(
                            onClick = { mainViewModel.playOrToggleSong(song, true) },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(onClick = { mainViewModel.skipToNextSong() }) {
                            Icon(Icons.Filled.SkipNext, contentDescription = "Next", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = (ms / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
