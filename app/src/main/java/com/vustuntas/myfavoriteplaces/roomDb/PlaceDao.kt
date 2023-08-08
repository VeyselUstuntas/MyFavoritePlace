package com.vustuntas.myfavoriteplaces.roomDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vustuntas.myfavoriteplaces.model.Places
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PlaceDao {
    @Query("SELECT * FROM Tbl_Places")
    fun getAll() : Flowable<List<Places>>

    @Query("SELECT * FROM Tbl_Places WHERE ID = :id")
    fun getIdAll(id : Int) : Flowable<List<Places>>

    @Insert()
    fun insert(place : Places) : Completable

    @Delete
    fun delete(place: Places) : Completable

    @Update()
    fun update(place : Places) : Completable
}