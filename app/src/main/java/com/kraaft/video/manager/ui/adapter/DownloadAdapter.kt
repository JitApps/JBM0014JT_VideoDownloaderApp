package com.kraaft.video.manager.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kraaft.video.manager.databinding.ItemDownloadBinding
import com.kraaft.video.manager.model.DownloadProgress
import com.kraaft.video.manager.model.MediaUrl
import com.kraaft.video.manager.utils.DOWNLOAD_COMPLETE
import com.kraaft.video.manager.utils.DOWNLOAD_FAILED
import com.kraaft.video.manager.utils.DOWNLOAD_RUNNING
import com.kraaft.video.manager.utils.DOWNLOAD_STARTED
import com.kraaft.video.manager.utils.formatBytes
import kotlin.math.roundToInt

class DownloadAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var videoList = mutableListOf<MediaUrl>()

    inner class DownloadHolder(val binding: ItemDownloadBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun refreshData(newList: List<MediaUrl>) {
        /*  val diffResult = DiffUtil.calculateDiff(
              DiffCallback(
                  oldList = videoList,
                  newList = newList,
                  areItemsTheSame = { oldItem, newItem -> oldItem.fileName == newItem.fileName }
              )
          )*/

        videoList.clear()
        videoList.addAll(newList)
        notifyDataSetChanged()
        //  diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return DownloadHolder(
            ItemDownloadBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    fun updateProgress(progressMap: Map<String, DownloadProgress>) {
        progressMap.forEach { (fileName, progress) ->
            val index = videoList.indexOfFirst { it.fileName == fileName }
            if (index != -1) {
                videoList[index] = videoList[index].copy(
                    progress = progress
                )
                notifyItemChanged(index)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = videoList[position]
        when (holder) {
            is DownloadHolder -> {
                holder.binding.tvName.text = item.fileName

                when (item.progress.status) {
                    DOWNLOAD_COMPLETE -> {
                        holder.binding.progressBar.visibility = View.GONE
                        holder.binding.tvDuration.text = "Download Complete"
                        holder.binding.tvPercent.text = ""
                        holder.binding.progressBar.progress = 100
                    }

                    DOWNLOAD_FAILED -> {
                        holder.binding.progressBar.visibility = View.GONE
                        holder.binding.tvDuration.text = "Download Failed"
                        holder.binding.tvPercent.text = ""
                        holder.binding.progressBar.progress = 0
                    }

                    DOWNLOAD_RUNNING -> {
                        val currentText = formatBytes(item.progress.current)

                        if (item.progress.total > 0) {
                            val percent =
                                ((item.progress.current.toDouble() / item.progress.total) * 100).roundToInt()

                            val totalText = formatBytes(item.progress.total)

                            holder.binding.progressBar.visibility = View.VISIBLE
                            holder.binding.progressBar.progress = percent
                            holder.binding.tvPercent.text = "$percent%"
                            holder.binding.tvDuration.text = "$currentText / $totalText"
                        } else {
                            holder.binding.progressBar.visibility = View.GONE
                            holder.binding.tvDuration.text = currentText
                        }
                    }

                    DOWNLOAD_STARTED -> {
                        holder.binding.progressBar.visibility =
                            if (item.progress.total > 0) View.VISIBLE else View.GONE
                        holder.binding.tvDuration.text = "Download Started"
                        holder.binding.tvPercent.text = ""
                        holder.binding.progressBar.progress = 0
                    }

                    else -> {
                        holder.binding.progressBar.visibility = View.GONE
                        holder.binding.tvDuration.text = "Download Starting Soon"
                        holder.binding.tvPercent.text = ""
                        holder.binding.progressBar.progress = 0
                    }
                }
            }

            else -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

}