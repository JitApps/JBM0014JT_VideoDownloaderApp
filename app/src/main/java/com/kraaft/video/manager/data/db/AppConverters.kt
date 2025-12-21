package com.kraaft.video.manager.data.db

import androidx.room.TypeConverter

class AppConverters {

    @TypeConverter
    fun fromList(list: List<String>?): String? =
        list?.joinToString(",")

    @TypeConverter
    fun toList(data: String?): List<String> =
        data?.takeIf { it.isNotBlank() }
            ?.split(",")
            ?: emptyList()
}