package com.kraaft.video.manager.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kraaft.video.manager.databinding.ItemMediaFolderBinding
import com.kraaft.video.manager.model.SoundModel
import com.kraaft.video.manager.utils.DiffCallback

class AudioFolderAdapter(val context: Context, val onClickListener: (SoundModel, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var soundList = mutableListOf<SoundModel>()

    inner class AudioFolderHolder(val binding: ItemMediaFolderBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun refreshData(newList: List<SoundModel>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldList = soundList,
                newList = newList,
                areItemsTheSame = { oldItem, newItem -> oldItem.folderName == newItem.folderName }
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
                holder.binding.tvName.text = item.folderName
                holder.binding.albumSize.text = "${item.soundFiles.size} Files"
            }

            else -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return soundList.size
    }

}