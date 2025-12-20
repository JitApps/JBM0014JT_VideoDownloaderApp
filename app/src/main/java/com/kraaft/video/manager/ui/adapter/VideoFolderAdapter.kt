package com.kraaft.video.manager.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kraaft.video.manager.databinding.ItemMediaFolderBinding
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.model.SoundFile
import com.kraaft.video.manager.model.VideoFile
import com.kraaft.video.manager.model.VideoModel
import com.kraaft.video.manager.utils.DiffCallback

class VideoFolderAdapter(val context: Context,val onClickListener: (FileModel, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var videoList = mutableListOf<VideoModel>()

    inner class VideoFolderHolder(val binding: ItemMediaFolderBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun refreshData(newList: List<VideoModel>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldList = videoList,
                newList = newList,
                areItemsTheSame = { oldItem, newItem -> oldItem.folderName == newItem.folderName }
            )
        )

        videoList.clear()
        videoList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return VideoFolderHolder(
            ItemMediaFolderBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = videoList[position]
        when (holder) {
            is VideoFolderHolder -> {
                holder.binding.tvName.text = item.folderName
                holder.binding.albumSize.text = "${item.videoFiles.size} Videos"
                Glide.with(context).load(item.videoFiles[0].uri)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.binding.ivAlbum)
            }

            else -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

}