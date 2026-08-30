package com.example.rythmcloud.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rythmcloud.player.isPlaying
import com.example.rythmcloud.player.toSong
import com.example.rythmcloud.ui.components.BottomPlayerBar
import com.example.rythmcloud.ui.screens.HomeScreen
import com.example.rythmcloud.ui.screens.SongScreen
import com.example.rythmcloud.ui.viewmodels.MainViewModel
import com.example.rythmcloud.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val songViewModel: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            MusicNavHost(
                navController = navController,
                mainViewModel = mainViewModel,
                songViewModel = songViewModel
            )
        }
    }
}

@Composable
fun MusicNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    songViewModel: SongViewModel,
) {
    val currentSong by mainViewModel.curPlayingSong.observeAsState()
    val playbackState by mainViewModel.playbackState.observeAsState()
    val currentSongModel = currentSong?.toSong()
    val currentPosition by songViewModel.curPlayerPos.observeAsState(initial = 0L)
    val currentDuration by songViewModel.curSongDuration.observeAsState(initial = 0L)
    val isPlaying = playbackState?.isPlaying == true

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomPlayerBar(
                song = currentSongModel,
                currentPosition = currentPosition,
                totalDuration = currentDuration,
                isPlaying = isPlaying,
                onPlayPause = { currentSongModel?.let { mainViewModel.playOrToggleSong(it, true) } },
                onSkipPrevious = { mainViewModel.skipToPrevSong() },
                onSkipNext = { mainViewModel.skipToNextSong() },
                onSeek = { mainViewModel.seekTo(it) }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(
                    mainViewModel = mainViewModel,
                    onSongClick = { song ->
                        mainViewModel.playOrToggleSong(song)
                        navController.navigate("song")
                    }
                )
            }

            composable("song") {
                SongScreen(
                    mainViewModel = mainViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

