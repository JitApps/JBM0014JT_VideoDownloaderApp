package com.kraaft.video.manager.utils

import androidx.recyclerview.widget.DiffUtil
import com.google.gson.Gson

class DiffCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val areItemsTheSame: (oldItem: T, newItem: T) -> Boolean
) : DiffUtil.Callback() {

    private val gson = Gson()

    private val oldJsonCache: List<String> = oldList.map { gson.toJson(it) }
    private val newJsonCache: List<String> = newList.map { gson.toJson(it) }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldJsonCache[oldItemPosition] == newJsonCache[newItemPosition]
    }
}