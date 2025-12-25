package com.kraaft.video.manager.ui.adapter

import android.annotation.SuppressLint
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
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.ui.adapter.VideoListAdapter.VideoHolder
import com.kraaft.video.manager.utils.DiffCallback
import com.kraaft.video.manager.utils.onSingleClick

class VideoListAdapter(
    val context: Context,
    val isSelectionOn: Boolean = false,
    val selCallBack: (() -> Unit)? = null,
    val onClickListener: (FileEntity, Int) -> Unit
) :
    RecyclerView.Adapter<VideoHolder>() {

    private var videoList = mutableListOf<FileEntity>()
    private var isSelection = false
    var selectedList = mutableMapOf<Int, FileEntity>()

    val height =
        (context.resources.displayMetrics.widthPixels - context.resources.getDimension(R.dimen.item_margin)
            .toInt()) / 2

    inner class VideoHolder(val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cvRoot.layoutParams = FrameLayout.LayoutParams(height, height)
            binding.cvRoot.requestLayout()
        }
    }

    fun isSelectedAll(): Boolean {
        return videoList.size == selectedList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun checkAll(isCheckAll: Boolean) {
        if (isCheckAll) {
            selectedList = videoList
                .associateBy { it.id }
                .toMutableMap()
        } else {
            selectedList.clear()
            closeSelection()
        }
        notifyDataSetChanged()
    }

    fun closeSelection() {
        if (selectedList.isEmpty()) {
            isSelection = false
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
            val fileModel = videoList[holder.adapterPosition]
            Glide.with(context).load(videoList[position].filePath)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(ivFile)
            if (isSelectionOn) {
                holder.binding.ivCheckBox.isChecked = selectedList.containsKey(fileModel.id)
                holder.binding.ivCheckBox.setSafeOnCheckedChangeListener { button, isChecked ->
                    isSelection = true
                    selectedList.remove(fileModel.id)
                        ?: selectedList.put(fileModel.id, fileModel)
                    closeSelection()
                    selCallBack?.invoke()
                }
            }
            root.setOnClickListener {
                if (isSelection && isSelectionOn) {
                    selectedList.remove(fileModel.id)
                        ?: selectedList.put(fileModel.id, fileModel)
                    holder.binding.ivCheckBox.isChecked =
                        selectedList.containsKey(fileModel.id)
                    closeSelection()
                    selCallBack?.invoke()
                } else {
                    val selPosition = holder.adapterPosition
                    onClickListener.invoke(videoList[selPosition], selPosition)
                }

                onClickListener.invoke(videoList[position], position)
            }
        }
    }

}