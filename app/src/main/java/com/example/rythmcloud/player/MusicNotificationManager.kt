package com.example.rythmcloud.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.example.rythmcloud.R
import com.example.rythmcloud.other.Constants.NOTIFICATIONID
import com.example.rythmcloud.other.Constants.NOTIFICATION_CHANNEL_ID
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager


class MusicNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
)
{
    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager = PlayerNotificationManager.Builder(
            context,
            NOTIFICATIONID, NOTIFICATION_CHANNEL_ID)
            .setChannelNameResourceId(R.string.notification_channel_name)
            .setChannelDescriptionResourceId(R.string.notification_channel_description)
            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            .setNotificationListener(notificationListener)
            .build()
            .apply {
                setSmallIcon(R.drawable.ic_music)
                setMediaSessionToken(sessionToken)
            }
    }

    fun showNotification(player: Player){
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(
        private val mediaController: MediaControllerCompat
    ): PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: com.google.android.exoplayer2.Player): CharSequence {
            return mediaController.metadata?.description?.title?.toString() ?: "RythmCloud"
        }

        override fun createCurrentContentIntent(player: com.google.android.exoplayer2.Player) =
            mediaController.sessionActivity

        override fun getCurrentContentText(player: com.google.android.exoplayer2.Player): CharSequence {
            return mediaController.metadata?.description?.subtitle?.toString() ?: ""
        }

        override fun getCurrentLargeIcon(
            player: com.google.android.exoplayer2.Player,
            callback: PlayerNotificationManager.BitmapCallback
        ) : Bitmap? {
            val iconUri = mediaController.metadata?.description?.iconUri
            if (iconUri == null) return null

            Glide.with(context).asBitmap()
                .load(iconUri)
                .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
            return null
        }
    }
}