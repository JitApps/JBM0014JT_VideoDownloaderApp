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
import com.kraaft.video.manager.model.VideoFile
import com.kraaft.video.manager.ui.adapter.VideoListAdapter.VideoHolder
import com.kraaft.video.manager.utils.DiffCallback
import com.kraaft.video.manager.utils.onSingleClick

class VideoListAdapter(val context: Context, val onClickListener: (VideoFile, Int) -> Unit) :
    RecyclerView.Adapter<VideoHolder>() {

    private var statusList = mutableListOf<VideoFile>()
    val height =
        (context.resources.displayMetrics.widthPixels - context.resources.getDimension(R.dimen.item_margin).toInt()) / 2

    inner class VideoHolder(val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cvRoot.layoutParams = FrameLayout.LayoutParams(height, height)
            binding.cvRoot.requestLayout()
        }
    }

    fun refreshData(newList: MutableList<VideoFile>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldList = statusList,
                newList = newList,
                areItemsTheSame = { oldItem, newItem -> oldItem.uri == newItem.uri }
            )
        )

        statusList.clear()
        statusList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(ItemVideoBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int = statusList.size

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.binding.apply {
            Glide.with(context).load(statusList[position].uri)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(ivFile)
            root.onSingleClick {
                onClickListener.invoke(statusList[position], position)
            }
        }
    }

}