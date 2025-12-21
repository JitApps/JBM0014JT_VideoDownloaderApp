package com.kraaft.video.manager.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ItemVideoBinding
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.ui.adapter.VideoListAdapter.VideoHolder
import com.kraaft.video.manager.utils.DiffCallback
import com.kraaft.video.manager.utils.onSingleClick

class VideoListAdapter(val context: Context, val onClickListener: (FileEntity, Int) -> Unit) :
    RecyclerView.Adapter<VideoHolder>() {

    private var videoList = mutableListOf<FileEntity>()

    val height =
        (context.resources.displayMetrics.widthPixels - context.resources.getDimension(R.dimen.item_margin).toInt()) / 2

    inner class VideoHolder(val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cvRoot.layoutParams = FrameLayout.LayoutParams(height, height)
            binding.cvRoot.requestLayout()
        }
    }

    fun refreshData(newList: MutableList<FileEntity>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldList = videoList,
                newList = newList,
                areItemsTheSame = { oldItem, newItem -> oldItem.filePath == newItem.filePath }
            )
        )

        videoList.clear()
        videoList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(ItemVideoBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int = videoList.size

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.binding.apply {
            Glide.with(context).load(videoList[position].filePath)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(ivFile)
            root.onSingleClick {
                onClickListener.invoke(videoList[position], position)
            }
        }
    }

}