package com.demo.goldmansachs

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.demo.goldmansachs.database.AppDataBase
import com.demo.goldmansachs.repository.GoldManRepository

class GoldMacSachsApplication : Application(), ImageLoaderFactory {
    val database: AppDataBase by lazy { AppDataBase.getDatabase(this) }
    val repository by lazy { GoldManRepository(database.favouriteDao()) }
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true).build()
    }
}