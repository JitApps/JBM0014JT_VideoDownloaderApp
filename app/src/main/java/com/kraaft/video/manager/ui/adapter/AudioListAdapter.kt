package com.kraaft.video.manager.ui.adapter

import android.R.attr.height
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ItemMusicBinding
import com.kraaft.video.manager.databinding.ItemVideoBinding
import com.kraaft.video.manager.model.SoundFile
import com.kraaft.video.manager.model.VideoFile
import com.kraaft.video.manager.utils.DiffCallback
import com.kraaft.video.manager.utils.PopupMenuHelper
import com.kraaft.video.manager.utils.formatDuration
import com.kraaft.video.manager.utils.onSingleClick

class AudioListAdapter(
    val context: Context,
    val isFromPlayList: Boolean = false,
    val onMenuClick: (SoundFile, Boolean) -> Unit,
    val onClickListener: (SoundFile, Int) -> Unit
) :
    RecyclerView.Adapter<AudioListAdapter.SoundHolder>() {

    private var soundList = mutableListOf<SoundFile>()

    inner class SoundHolder(val binding: ItemMusicBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun refreshData(newList: MutableList<SoundFile>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffCallback(
                oldList = soundList,
                newList = newList,
                areItemsTheSame = { oldItem, newItem -> oldItem.uri == newItem.uri }
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
                PopupMenuHelper.show(
                    anchor = view,
                    options = listOf(
                        PopupMenuHelper.Option(
                            1,
                            if (!isFromPlayList) "Add to Playlist" else "Remove from Playlist"
                        )
                    )
                ) { option ->
                    when (option.id) {
                        1 -> {
                            onMenuClick.invoke(soundList[selPosition],!isFromPlayList)
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