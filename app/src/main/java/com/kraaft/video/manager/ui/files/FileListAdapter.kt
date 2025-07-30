package com.kraaft.video.manager.ui.files

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
import com.kraaft.video.manager.utils.onSingleClick

class FileListAdapter(val context: Context, val onClickListener: (FileModel, Int) -> Unit) :
    RecyclerView.Adapter<FileListAdapter.FileHolder>() {

    private var list = mutableListOf<FileModel>()
    val height = context.resources.displayMetrics.widthPixels / 3

    inner class FileHolder(val binding: ItemFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cvRoot.layoutParams = FrameLayout.LayoutParams(height, height)
            binding.cvRoot.requestLayout()
        }
    }

    fun refreshData(newList: MutableList<FileModel>) {
        DiffUtil.calculateDiff(FileDiffCallback(this.list, newList)).dispatchUpdatesTo(this)
        list = newList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        return FileHolder(ItemFileBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        holder.binding.apply {
            Glide.with(context).load(list[position].filePath)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(ivFile)
            root.onSingleClick {
                onClickListener.invoke(list[position], position)
            }
        }
    }

    class FileDiffCallback(list: List<FileModel>, list2: List<FileModel>) : DiffUtil.Callback() {
        private val newList: List<FileModel> = list2

        private val oldList: List<FileModel> = list

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(i: Int, i2: Int): Boolean {
            return oldList[i].filePath == newList[i2].filePath
        }

        override fun areContentsTheSame(i: Int, i2: Int): Boolean {
            return oldList[i].filePath == newList[i2].filePath
        }

        override fun getChangePayload(i: Int, i2: Int): Any? {
            return super.getChangePayload(i, i2)
        }
    }

}