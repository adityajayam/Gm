package com.demo.goldmansachs.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM ApodDataEntity ORDER BY date ASC")
    fun getAll(): Flow<List<ApodDataEntity>>

    @Delete
    suspend fun deleteMultiples(favourite: List<ApodDataEntity>): Int

    @Query("delete from ApodDataEntity where url = (:url)")
    suspend fun delete(url: String): Int

    @Insert
    suspend fun saveFavourite(favourite: ApodDataEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLatestApod(latestApod: LatestApod):Long

    @Query("SELECT * FROM LatestApod ORDER BY date ASC")
    fun getLatestApod(): Flow<List<LatestApod>>
}