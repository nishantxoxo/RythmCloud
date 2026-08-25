package com.example.rythmcloud.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rythmcloud.other.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import com.example.rythmcloud.player.MusicService
import com.example.rythmcloud.player.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SongViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val playbackState = musicServiceConnection.playbackState

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPos = MutableLiveData<Long>()
    val curPlayerPos : LiveData<Long> = _curPlayerPos

    init {

        updateCurrentPlayerPosition()
    }


    private fun updateCurrentPlayerPosition() {
//         _curPlayerPos.postValue(playbackState.value?.position ?: 0L)
        viewModelScope.launch {
            while (true){
                    val pos = playbackState.value?.position ?: 0L
                    if (_curPlayerPos.value != pos){
                        _curPlayerPos.postValue(pos)
                        _curSongDuration.postValue(MusicService.curSongDuration)
                    }
                    delay(UPDATE_PLAYER_POSITION_INTERVAL)

            }
        }
    }

}