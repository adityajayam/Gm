package com.demo.goldmansachs.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.demo.goldmansachs.database.FavouriteDao
import com.demo.goldmansachs.database.ApodDataEntity
import com.demo.goldmansachs.database.LatestApod
import com.demo.goldmansachs.network.GoldmanApi
import com.demo.goldmansachs.models.ApodData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository class which handles db and network operations.
 */
class GoldManRepository(private val dao: FavouriteDao) {
    /**
     * Fetches the latest Apod data from the network
     * @param date for the specific date, null for the current.
     */
    suspend fun getApodData(date: String): ApodData? {
        return withContext(Dispatchers.IO) {
            val response = GoldmanApi.RETROFIT_SERVICE.getApodByDate(date)
            if (response.isSuccessful) {
                val body = response.body()
                body
            } else {
                throw Exception(response.code().toString())
            }
        }
    }

    /**
     * Save favorite Apod in the db
     */
    suspend fun saveFavourite(favourite: ApodDataEntity): Long {
        return withContext(Dispatchers.IO) {
            try {
                dao.saveFavourite(favourite)
            } catch (e: SQLiteConstraintException) {
                Log.e(TAG, e.toString())
                1
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                -1
            }
        }
    }

    /**
     * Delete multiple favorites Aopd from the db
     */
    suspend fun deleteFavourites(favourites: List<ApodDataEntity>): Int {
        return withContext(Dispatchers.IO) {
            dao.deleteMultiples(favourites)
        }
    }

    /**
     * Delete a favorite Aopd from db
     */
    suspend fun delete(favourite: ApodDataEntity): Int {
        return withContext(Dispatchers.IO) {
            dao.delete(favourite.url)
        }
    }

    /**
     * Save the current displayed Aopd in to the db
     */
    suspend fun saveLatestApod(latestApod: LatestApod): Long {
        return withContext(Dispatchers.IO) {
            try {
                dao.saveLatestApod(latestApod)
            } catch (exp: SQLiteConstraintException) {
                Log.e(TAG, exp.toString())
                throw SQLiteConstraintException()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                throw Exception()
            }
        }
    }

    fun getLatestApod(): Flow<List<LatestApod>> = dao.getLatestApod()

    fun getAllFavourites(): Flow<List<ApodDataEntity>> = dao.getAll()

    companion object {
        private const val TAG = "GoldManRepository"
    }
}