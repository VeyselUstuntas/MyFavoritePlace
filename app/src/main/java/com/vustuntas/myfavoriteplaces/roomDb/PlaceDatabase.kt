package com.vustuntas.myfavoriteplaces.roomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vustuntas.myfavoriteplaces.model.Places

@Database(entities = arrayOf(Places::class), version = 1)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun placeDao() : PlaceDao
}