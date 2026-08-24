package com.example.rythmcloud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.rythmcloud.R
import com.example.rythmcloud.data.entities.Song
import com.example.rythmcloud.databinding.ListItemBinding
import com.example.rythmcloud.databinding.SwipeItemBinding
import javax.inject.Inject

class SwipeSongAdapter: BaseSongAdapter(R.layout.swipe_item) {
//    class SongViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)


    override val differ = AsyncListDiffer(this, diffCallBack)


    override fun onBindViewHolder(
        holder: SongViewHolder,
        position: Int
    ) {
        val song = songs[position]

        val binding = SwipeItemBinding.bind(holder.itemView)
        val text = "${song.title} - ${song.subtitle}"
        binding.tvPrimary.text = song.title
//        binding.tvSecondary.text = song.subtitle


        holder.itemView.setOnClickListener {




            onItemClickListener?.let { click ->
                click(song)
            }
        }
    }

//    private var onItemClickListener: ((Song) -> Unit)? = null
//
//    fun setOnItemClickListener(listener: (Song) -> Unit) {
//        onItemClickListener = listener
//    }
//
//    override fun getItemCount(): Int {
////        TODO("Not yet implemented")
//
//        return songs.size
//    }
}