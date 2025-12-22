package com.kraaft.video.manager.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kraaft.video.manager.databinding.ItemMediaFolderBinding
import com.kraaft.video.manager.model.FolderCount
import com.kraaft.video.manager.utils.DiffCallback
import java.io.File

class VideoFolderAdapter(val context: Context,val onClickListener: (FolderCount, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var videoList = mutableListOf<FolderCount>()

    inner class VideoFolderHolder(val binding: ItemMediaFolderBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun refreshData(newList: List<FolderCount>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldList = videoList,
                newList = newList,
                areItemsTheSame = { oldItem, newItem -> oldItem.folderPath == newItem.folderPath }
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
                holder.binding.tvName.text = File(item.folderPath).name
                holder.binding.albumSize.text = "${item.folderPath} Videos"
            }

            else -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

}