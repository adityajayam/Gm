package com.demo.goldmansachs.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LatestApod(
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "explanation") val explanation: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "favourite") var isSelected: Boolean
) {
    @PrimaryKey
    var id: Int = 1
}