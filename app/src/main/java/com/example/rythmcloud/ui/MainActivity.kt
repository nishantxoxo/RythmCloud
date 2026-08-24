package com.example.rythmcloud.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.RequestManager
import com.example.rythmcloud.R
import com.example.rythmcloud.adapters.SwipeSongAdapter
import com.example.rythmcloud.data.entities.Song
import com.example.rythmcloud.databinding.ActivityMainBinding
import com.example.rythmcloud.other.Status
import com.example.rythmcloud.player.toSong
import com.example.rythmcloud.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter


    @Inject
    lateinit var glide: RequestManager


    private var curPlayingSong : Song?= null

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        subscribeToObservers()

        binding.vpSong.adapter = swipeSongAdapter
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
    }


}