package com.example.rythmcloud.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.rythmcloud.other.Constants.MEDIA_ROOT_ID
import com.example.rythmcloud.other.Constants.NETWORK_ERROR
import com.example.rythmcloud.player.callbacks.MusicPlaybackPreparer
import com.example.rythmcloud.player.callbacks.MusicPlayerEventListener
import com.example.rythmcloud.player.callbacks.MusicPlayerNotificationListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var backendMusicSource: BackendMusicSource


    private lateinit var   musicNotificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val servicescope  = CoroutineScope(Dispatchers.Main + serviceJob)


    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector


    var isForegroundService = false


    private var curPlayingSong: MediaMetadataCompat? = null
    private var isPlayerInitialized = false

    private lateinit var MusicPlayerEventListener: MusicPlayerEventListener

    companion object {
        var curSongDuration  =0L
            private set
    }

    override fun onCreate() {
        super.onCreate()

        servicescope.launch {
            backendMusicSource.fetchMediaData()
        }

        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }


        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }


        sessionToken = mediaSession.sessionToken

        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)
        ) {
            curSongDuration = exoPlayer.duration
        }

        val musicPlaybackPreparer = MusicPlaybackPreparer(backendMusicSource){
            servicescope.launch {
                curPlayingSong = it
                preparePlayer(
                    backendMusicSource.songs,
                    it,
                    true
                )
            }
        }



        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)
        MusicPlayerEventListener = MusicPlayerEventListener(this)
        exoPlayer.addListener(MusicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }


    private inner class MusicQueueNavigator: TimelineQueueNavigator(mediaSession){
        override fun getMediaDescription(
            player: Player,
            windowIndex: Int
        ): MediaDescriptionCompat {
//            TODO("Not yet implemented")

            return backendMusicSource.songs[windowIndex].description


        }
    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        val curSongIndex = if(curPlayingSong == null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.prepare(backendMusicSource.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(curSongIndex, 0L)
        exoPlayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        exoPlayer.stop()

    }


    override fun onDestroy() {
        super.onDestroy()
        servicescope.cancel()
        exoPlayer.removeListener(MusicPlayerEventListener)
        exoPlayer.release()
    }


    //
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem?>?>
    ) {
        when(parentId){
            MEDIA_ROOT_ID -> {
                val resultSent = backendMusicSource.whenReady { isIni ->
                    servicescope.launch {
                        if(isIni){
                            result.sendResult(backendMusicSource.asMediaItems())
                            if(!isPlayerInitialized && backendMusicSource.songs.isNotEmpty()){
                                preparePlayer(backendMusicSource.songs, backendMusicSource.songs[0], false)
                                isPlayerInitialized = true
                            }
                        }
                        else{
                            mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                            result.sendResult(null)
                        }
                    }

                }
                if(!resultSent){
                    result.detach()
                }
            }
        }
    }
}