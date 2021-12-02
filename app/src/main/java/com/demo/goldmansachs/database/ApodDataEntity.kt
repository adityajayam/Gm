package com.demo.goldmansachs.database

import androidx.room.*

@Entity
data class ApodDataEntity(
    @PrimaryKey @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "explanation") val explanation: String,
    @ColumnInfo(name = "title") val title: String,
) {
    @Ignore
    var isSelected: Boolean = false
}