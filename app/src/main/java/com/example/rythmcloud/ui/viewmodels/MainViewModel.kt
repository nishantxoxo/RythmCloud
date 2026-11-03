package com.example.rythmcloud.ui.viewmodels


import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rythmcloud.data.entities.Song
import com.example.rythmcloud.data.remote.RetrofitInstance
import com.example.rythmcloud.other.Constants.MEDIA_ROOT_ID
import com.example.rythmcloud.other.Resource
import com.example.rythmcloud.player.MusicServiceConnection
import com.example.rythmcloud.player.isPlayEnabled
import com.example.rythmcloud.player.isPlaying
import com.example.rythmcloud.player.isPrepared
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel(){
    private val  _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems : LiveData<Resource<List<Song>>> = _mediaItems
    private val _searchResults = MutableLiveData<Resource<List<Song>>>()
    val searchResults: LiveData<Resource<List<Song>>> = _searchResults
    private var searchJob: Job? = null


    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState

    init {
        _mediaItems.postValue(Resource.loading(null))

        musicServiceConnection.subscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: List<MediaBrowserCompat.MediaItem?>
            ) {
                super.onChildrenLoaded(parentId, children)


                val items = children.map {
                    Song(
                         it?.mediaId!!,
                         it.description.title.toString(),
                         it.description.subtitle.toString(),
                         it.description.mediaUri.toString(),
                         it.description.iconUri.toString()
                    )
                }
                _mediaItems.postValue(Resource.success(items))
            }
        })

    }


    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }
    fun skipToPrevSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }
    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }


    fun playOrToggleSong(mediaItem : Song, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId == curPlayingSong.value?.getString(
                android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
            )
        ) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if (toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }

            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    fun searchSongs(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = Resource.success(emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            _searchResults.value = Resource.loading(null)
            runCatching { RetrofitInstance.api.getAllSongs(query.trim()) }
                .onSuccess { _searchResults.value = Resource.success(it) }
                .onFailure {
                    _searchResults.value = Resource.error(
                        it.message ?: "Failed to search songs",
                        null
                    )
                }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchResults.value = Resource.success(emptyList())
    }


    override fun onCleared() {
        super.onCleared()

        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback(){})
    }






}