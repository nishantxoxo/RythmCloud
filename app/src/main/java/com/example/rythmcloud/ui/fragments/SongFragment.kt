package com.example.rythmcloud.ui.fragments

import android.app.Fragment
import android.media.session.PlaybackState
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.rythmcloud.R
import com.example.rythmcloud.data.entities.Song
import com.example.rythmcloud.databinding.ActivityMainBinding
import com.example.rythmcloud.databinding.FragmentHomeBinding
import com.example.rythmcloud.databinding.FragmentSongBinding
import com.example.rythmcloud.other.Status
import com.example.rythmcloud.player.isPlaying
import com.example.rythmcloud.player.toSong
import com.example.rythmcloud.ui.viewmodels.MainViewModel
import com.example.rythmcloud.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class SongFragment : androidx.fragment.app.Fragment(R.layout.fragment_song) {


    @Inject
    lateinit var glide: RequestManager

    private lateinit var mainViewModel: MainViewModel

    public var shouldUpdateSeek = true
    private val songViewModel: SongViewModel by viewModels()

//    private lateinit var binding: fragment_songBinding
private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!

    private var curPlayingSong: Song? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSongBinding.bind(view)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        subscribeToObservers()


        binding.ivPlayPauseDetail.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)

            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                p0: SeekBar?,
                p1: Int,
                p2: Boolean
            ) {
                if(p2){
                    setCurPlayerTimeToTextView(p1.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                shouldUpdateSeek = false

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeek = true
                }
            }
        })


        binding.ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPrevSong()
        }
        binding.ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }



    }


    private fun updateTitleAndSongImage(song: Song) {
        val title = "${song.title} - ${song.subtitle}"
        binding.tvSongName.text = title
        glide.load(song.imageUri).into(binding.ivSongImage)
    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){
            it?.let {
                result ->
                when(result.status){
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            if (curPlayingSong == null && songs.isNotEmpty()){
                                curPlayingSong = songs[0]
                                updateTitleAndSongImage(songs[0])
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(viewLifecycleOwner){
            if(it == null) return@observe
            curPlayingSong = it.toSong()
            updateTitleAndSongImage(curPlayingSong!!)
        }

        mainViewModel.playbackState.observe(viewLifecycleOwner){

            it?.let { playbackState ->
                binding.ivPlayPauseDetail.setImageResource(
                    if (playbackState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                )
                binding.seekBar.progress = playbackState.position.toInt()
            }
        }

        songViewModel.curPlayerPos.observe(viewLifecycleOwner){
            if(shouldUpdateSeek){
                binding.seekBar.progress = it.toInt()
                setCurPlayerTimeToTextView(it)
            }
        }

        songViewModel.curSongDuration.observe(viewLifecycleOwner){
            binding.seekBar.max = it.toInt()
            binding.tvSongDuration.text = formatDuration(it)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCurPlayerTimeToTextView(ms: Long){
        binding.tvCurTime.text = formatDuration(ms)
    }

    private fun formatDuration(ms: Long): String {
        val totalSeconds = (ms / 1000L).coerceAtLeast(0L)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}