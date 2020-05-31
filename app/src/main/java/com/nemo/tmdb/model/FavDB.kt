package com.nemo.tmdb.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Fav::class], version = 1)
abstract class FavDB : RoomDatabase() {
    abstract fun favDao(): FavDao?

    companion object {
        private var instance: FavDB? = null
        fun getInstance(context: Context): FavDB? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                        context.applicationContext,
                        FavDB::class.java,
                        "favdatabase")
                        .build()
            }
            return instance

        }
    }
}