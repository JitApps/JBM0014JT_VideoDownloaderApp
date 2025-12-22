package com.kraaft.video.manager.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kraaft.video.manager.databinding.ItemMusicBinding
import com.kraaft.video.manager.model.FileEntity
import com.kraaft.video.manager.utils.DiffCallback
import com.kraaft.video.manager.utils.PopupMenuHelper
import com.kraaft.video.manager.utils.formatDuration
import com.kraaft.video.manager.utils.onSingleClick

class AudioListAdapter(
    val context: Context,
    val onMenuClick: (FileEntity, Boolean) -> Unit,
    val onClickListener: (FileEntity, Int) -> Unit
) :
    RecyclerView.Adapter<AudioListAdapter.SoundHolder>() {

    private var soundList = mutableListOf<FileEntity>()

    inner class SoundHolder(val binding: ItemMusicBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun refreshData(newList: MutableList<FileEntity>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldList = soundList,
                newList = newList,
                areItemsTheSame = { oldItem, newItem -> oldItem.filePath == newItem.filePath }
            )
        )

        soundList.clear()
        soundList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
        return SoundHolder(ItemMusicBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int = soundList.size

    override fun onBindViewHolder(holder: SoundHolder, position: Int) {
        holder.binding.apply {
            tvName.text = soundList[position].name
            tvDuration.text = soundList[position].duration.formatDuration()
            ivMenu.onSingleClick { view ->
                val selPosition = holder.adapterPosition
                val isNotAdded = soundList[selPosition].playName.isEmpty()
                 PopupMenuHelper.show(
                    anchor = view,
                    options = listOf(
                        PopupMenuHelper.Option(
                            1,
                            if (isNotAdded) "Add to Playlist" else "Remove from Playlist"
                        )
                    )
                ) { option ->
                    when (option.id) {
                        1 -> {
                            onMenuClick.invoke(soundList[selPosition], isNotAdded)
                        }
                    }
                }
            }
            root.onSingleClick {
                val selPosition = holder.adapterPosition
                onClickListener.invoke(soundList[selPosition], selPosition)
            }
        }
    }

}