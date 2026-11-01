package com.example.rythmcloud.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rythmcloud.player.isPlaying
import com.example.rythmcloud.player.toSong
import com.example.rythmcloud.ui.components.BottomPlayerBar
import com.example.rythmcloud.ui.screens.HomeScreen
import com.example.rythmcloud.ui.screens.LoginScreen
import com.example.rythmcloud.ui.screens.RegisterScreen
import com.example.rythmcloud.ui.screens.SongScreen
import com.example.rythmcloud.ui.viewmodels.AuthState
import com.example.rythmcloud.ui.viewmodels.AuthViewModel
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
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.state.collectAsState()

            when (val state = authState) {
                AuthState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                AuthState.Unauthenticated -> {
                    AuthNavHost(authViewModel = authViewModel)
                }

                is AuthState.Error -> {
                    AuthNavHost(
                        authViewModel = authViewModel,
                        errorMessage = state.message
                    )
                }

                is AuthState.Authenticated -> {
                    val navController = rememberNavController()
                    MusicNavHost(
                        navController = navController,
                        mainViewModel = mainViewModel,
                        songViewModel = songViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthNavHost(
    authViewModel: AuthViewModel,
    errorMessage: String? = null
) {
    val navController = rememberNavController()
    val authState by authViewModel.state.collectAsState()
    val isLoading = authState == AuthState.Loading

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onLogin = authViewModel::login,
                onRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                onRegister = authViewModel::register,
                onLogin = { navController.popBackStack() }
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
    val currentRoute by navController.currentBackStackEntryAsState()
    val isSongScreen = currentRoute?.destination?.route == "song"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (!isSongScreen) {
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
