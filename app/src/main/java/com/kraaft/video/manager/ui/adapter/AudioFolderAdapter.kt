package com.kraaft.video.manager.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kraaft.video.manager.databinding.ItemMediaFolderBinding
import com.kraaft.video.manager.model.FolderCount
import com.kraaft.video.manager.model.SoundModel
import com.kraaft.video.manager.utils.DiffCallback
import java.io.File

class AudioFolderAdapter(val context: Context, val onClickListener: (String, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var soundList = mutableListOf<FolderCount>()

    inner class AudioFolderHolder(val binding: ItemMediaFolderBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun refreshData(newList: List<FolderCount>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldList = soundList,
                newList = newList,
                areItemsTheSame = { oldItem, newItem -> oldItem.folderPath == newItem.folderPath }
            )
        )

        soundList.clear()
        soundList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return AudioFolderHolder(
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
        val item = soundList[position]
        when (holder) {
            is AudioFolderHolder -> {
                holder.binding.tvName.text = File(item.folderPath).name ?: "Unknown"
                holder.binding.albumSize.text = "${item.totalCount} Files"
            }

            else -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return soundList.size
    }

}