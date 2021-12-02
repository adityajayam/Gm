package com.demo.goldmansachs.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.goldmansachs.R
import com.demo.goldmansachs.database.ApodDataEntity
import com.demo.goldmansachs.database.LatestApod
import com.demo.goldmansachs.models.ApodData
import com.demo.goldmansachs.repository.GoldManRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class MainActivityViewModel(private val repository: GoldManRepository) : ViewModel() {

    private val _data = MutableLiveData<ApodData>()
    val data: LiveData<ApodData> = _data

    val errorData = MutableLiveData<Int>()

    private val _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap> = _image

    var selected = false
    /**
     * Fetches today's image form network
     * @param date give a specific date or null for today's image
     */
    fun getApodData(date: String?) {
        viewModelScope.launch {
            try {
                val response = repository.getApodData(date ?: "")
                _data.postValue(response!!)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                errorData.postValue(R.string.try_again)
            } catch (e: UnknownHostException) {
                Log.e(TAG, e.toString())
                errorData.postValue(R.string.try_again)
            }
        }
    }

    fun saveFavourite() {
        viewModelScope.launch {
            _data.value?.let {
                val result = repository.saveFavourite(ApodDataEntity(it.url, it.date, it.explanation, it.title))
                if (result == -1L) {
                    errorData.postValue(R.string.try_again)
                }
            }
        }
    }

    fun saveLatestApod(latestApod: LatestApod) {
        viewModelScope.launch {
            val result = repository.saveLatestApod(latestApod)
            if (result == -1L) {
                errorData.postValue(R.string.try_again)
            }
        }
    }

    fun getLatestApod(): Flow<List<LatestApod>> = repository.getLatestApod()

    fun getFavourites(): Flow<List<ApodDataEntity>> = repository.getAllFavourites()

    fun deleteFavourites(favourites: List<ApodDataEntity>) {
        viewModelScope.launch {
            val result = repository.deleteFavourites(favourites)
            if (result != favourites.size) {
                throw Exception("Delete Failure")
            }
            Log.e(TAG, result.toString())
        }
    }

    fun deleteFavourite() {
        viewModelScope.launch {
            _data.value?.let {
                repository.delete(ApodDataEntity(it.url, it.date, it.explanation, it.title))
            }
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}