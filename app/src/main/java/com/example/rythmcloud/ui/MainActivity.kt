package com.example.rythmcloud.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.example.rythmcloud.R
import com.example.rythmcloud.adapters.SwipeSongAdapter
import com.example.rythmcloud.data.entities.Song
import com.example.rythmcloud.databinding.ActivityMainBinding
import com.example.rythmcloud.other.Status
import com.example.rythmcloud.player.isPlaying
import com.example.rythmcloud.player.toSong
import com.example.rythmcloud.ui.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter


    @Inject
    lateinit var glide: RequestManager


    private var curPlayingSong : Song?= null

    private var playbackState: PlaybackStateCompat? = null

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeToObservers()

        binding.vpSong.adapter = swipeSongAdapter
        binding.vpSong.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true){
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position], true)
                }
                else {
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })


        binding.ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        swipeSongAdapter.setItemClickListener {
            findNavController(R.id.navHostFragment).navigate(R.id.global_to_songFragment)
        }


        findNavController(R.id.navHostFragment).addOnDestinationChangedListener { _, destination, _ ->

            when(destination.id){
                R.id.songFragment -> HideBottom()
                R.id.homeFragment -> ShowBottom()

                else -> ShowBottom()
            }
        }
    }


    private fun HideBottom(){
        binding.ivCurSongImage.isVisible = false
        binding.ivPlayPause.isVisible = false
        binding.vpSong.isVisible = false


    }

    private fun ShowBottom(){
        binding.ivCurSongImage.isVisible = true
        binding.ivPlayPause.isVisible = true
        binding.vpSong.isVisible = true


    }

    private fun switchViewPagerToCurrentSong(song: Song){
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if(newItemIndex != -1){
            binding.vpSong.currentItem = newItemIndex
            curPlayingSong = song
        }

    }


    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(this){
            it?.let {
                result->
                when(result.status){
                    Status.SUCCESS -> {
                        result.data?.let {
                            songs ->
                            swipeSongAdapter.songs = songs
                            if (songs.isNotEmpty()){
                                glide.load((curPlayingSong ?: songs[0]).imageUri).into(binding.ivCurSongImage)
                            }

                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(this) {
            if (it == null) return@observe

            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUri).into(binding.ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        }

        mainViewModel.playbackState.observe(this) {
            playbackState = it
            binding.ivPlayPause.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
//            binding.vpSong.isUserInputEnabled = it?.isPlaying == true
        }
        mainViewModel.isConnected.observe(this){

            it?.getContentIfNotHandled()?.let { result ->
                when(result.status){
                    Status.SUCCESS -> Unit
                    Status.ERROR -> Snackbar.make(binding.rootLayout, result.message ?: "An Unknown error occurred",
                        Snackbar.LENGTH_LONG
                        ).show()
                    Status.LOADING -> Unit
                    else -> Unit
                }
            }
        }
    }


}