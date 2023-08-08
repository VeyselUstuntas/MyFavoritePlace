package com.vustuntas.myfavoriteplaces.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity(tableName = "Tbl_Places")
class Places (
    @ColumnInfo("PlaceName")
    var name : String,

    @ColumnInfo("PlaceLatitude")
    var latitude : Double,

    @ColumnInfo(name = "PlaceLongitude")
    var longitude : Double

) : Serializable{

    @PrimaryKey(autoGenerate = true)
    var ID : Int = 0
}