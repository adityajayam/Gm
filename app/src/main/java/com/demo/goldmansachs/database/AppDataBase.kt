package com.demo.goldmansachs.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ApodDataEntity::class, LatestApod::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun favouriteDao(): FavouriteDao

    companion object {
        private const val DATABASE_NAME = "goldman-sachs-db"
        var dataBase: AppDataBase? = null
        fun getDatabase(context: Context): AppDataBase {
            return dataBase ?: synchronized(this) {
                val db =
                    Room.databaseBuilder(context, AppDataBase::class.java, DATABASE_NAME).build()
                dataBase = db
                db
            }
        }
    }
}