package com.demo.goldmansachs.network

import com.demo.goldmansachs.models.ApodData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://api.nasa.gov/planetary/"

/**
 * The Retrofit object with the Gson converter.
 */
private val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL).build()

/**
 * A public interface that exposes the [getApod] method
 */
interface GoldManApiService {
    @GET("apod?api_key=5p1arQwX1sLWLR6biGcE1XWfBDg3DOiN23U9eKeB")
    suspend fun getApod(): Response<ApodData>

    @GET("apod?api_key=5p1arQwX1sLWLR6biGcE1XWfBDg3DOiN23U9eKeB")
    suspend fun getApodByDate(@Query("date") collectionId: String): Response<ApodData>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object GoldmanApi {
    val RETROFIT_SERVICE: GoldManApiService by lazy {
        retrofit.create(GoldManApiService::class.java)
    }
}