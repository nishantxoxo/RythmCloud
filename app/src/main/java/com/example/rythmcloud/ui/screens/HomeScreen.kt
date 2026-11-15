package com.example.rythmcloud.ui.screens

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.example.rythmcloud.data.entities.Song
import com.example.rythmcloud.other.Status
import com.example.rythmcloud.ui.viewmodels.MainViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    onSongClick: (Song) -> Unit,
    onSettingsClick: () -> Unit,
) {
    val songsResource by mainViewModel.mediaItems.observeAsState(initial = null)
    val searchResource by mainViewModel.searchResults.observeAsState(initial = null)
    var query by remember { mutableStateOf("") }
    val isSearching = query.isNotBlank()
    val songs = if (isSearching) {
        searchResource?.data.orEmpty()
    } else {
        songsResource?.data.orEmpty()
    }
    val isLoading = songsResource == null || songsResource?.status == Status.LOADING
    val isSearchLoading = isSearching && searchResource?.status == Status.LOADING
    val searchError = isSearching && searchResource?.status == Status.ERROR

    @OptIn(FlowPreview::class)
    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(350)
            .distinctUntilChanged()
            .collectLatest { value ->
                if (value.isBlank()) {
                    mainViewModel.clearSearch()
                } else {
                    mainViewModel.searchSongs(value)
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("RythmCloud", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Settings")
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            },
            shape = RoundedCornerShape(100),
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                    }
                }
            },
            placeholder = { Text("Search songs or artists") }
        )

        when {
            isLoading || isSearchLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            searchError -> {
                SearchMessage(
                    message = "Search failed",
                    actionLabel = "Retry",
                    onAction = { mainViewModel.searchSongs(query) }
                )
            }

            songs.isEmpty() -> {
                SearchMessage(
                    message = if (isSearching) "No matching songs" else "No songs available",
                    actionLabel = if (isSearching) null else "Retry",
                    onAction = if (isSearching) null else {
                        { mainViewModel.clearSearch() }
                    }
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(songs) { song ->
                        SongListItem(song = song, onClick = { onSongClick(song) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchMessage(
    message: String,
    actionLabel: String?,
    onAction: (() -> Unit)?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (actionLabel != null && onAction != null) {
                IconButton(onClick = onAction) {
                    Icon(Icons.Filled.Refresh, contentDescription = actionLabel)
                }
            }
        }
    }
}

@Composable
fun SongListItem(
    song: Song,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(song.title, style = MaterialTheme.typography.titleMedium)
            Text(
                song.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.size(36.dp).clip(CircleShape),
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                },
                update = { imageView ->
                    Glide.with(imageView.context).load(song.imageUri).into(imageView)
                }
            )
        }
    }
}
