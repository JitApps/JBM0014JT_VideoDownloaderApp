package com.kraaft.video.manager.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ItemFileBinding
import com.kraaft.video.manager.model.FileModel
import com.kraaft.video.manager.utils.DiffCallback
import com.kraaft.video.manager.utils.onSingleClick

class StatusListAdapter(val context: Context, val onClickListener: (FileModel, Int) -> Unit) :
    RecyclerView.Adapter<StatusListAdapter.FileHolder>() {

    private var statusList = mutableListOf<FileModel>()
    val height =
        (context.resources.displayMetrics.widthPixels - context.resources.getDimension(R.dimen.item_margin).toInt()) / 3

    inner class FileHolder(val binding: ItemFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cvRoot.layoutParams = FrameLayout.LayoutParams(height, height)
            binding.cvRoot.requestLayout()
        }
    }

    fun refreshData(newList: MutableList<FileModel>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldList = statusList,
                newList = newList,
                areItemsTheSame = { oldItem, newItem -> oldItem.filePath == newItem.filePath }
            )
        )

        statusList.clear()
        statusList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        return FileHolder(ItemFileBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int = statusList.size

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        holder.binding.apply {
            Glide.with(context).load(statusList[position].filePath)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(ivFile)
            root.onSingleClick {
                onClickListener.invoke(statusList[position], position)
            }
        }
    }

}