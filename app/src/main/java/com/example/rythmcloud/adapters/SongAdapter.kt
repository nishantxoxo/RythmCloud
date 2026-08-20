package com.example.rythmcloud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.rythmcloud.data.entities.Song
import com.example.rythmcloud.databinding.ListItemBinding
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
//    class SongViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)
    class SongViewHolder(
        val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)


    private val diffCallBack = object  : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(
            oldItem: Song,
            newItem: Song
        ): Boolean {
//            TODO("Not yet implemented")
            return oldItem.mediaId == newItem.mediaId
        }


        override fun areContentsTheSame(
            oldItem: Song,
            newItem: Song
        ): Boolean {
//            TODO("Not yet implemented")
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    private val differ = AsyncListDiffer(this, diffCallBack)

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SongViewHolder {
//        TODO("Not yet implemented")
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SongViewHolder(binding)


//        return SongViewHolder(
//            LayoutInflater.from(parent.context).inflate(
//                com.example.rythmcloud.R.layout.list_item,
//                parent,
//                false
//            )
//        )
    }

    override fun onBindViewHolder(
        holder: SongViewHolder,
        position: Int
    ) {

    val song = songs[position]
    holder.itemView.apply {
        holder.binding.tvPrimary.text = song.title
        holder.binding.tvSecondary.text = song.subtitle
        glide.load(song.imageUri).into(holder.binding.ivItemImage)


        setOnClickListener {
            onItemClickListener?.let { click ->
                click(song)
            }
        }
    }
    }

    private var onItemClickListener: ((Song) -> Unit)? = null

    fun setOnItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
//        TODO("Not yet implemented")

        return songs.size
    }
}