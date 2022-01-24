package com.example.peshwari.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "restaurants")
data class RestaurantsEntity (
    @PrimaryKey  val res_id : Int,
    @ColumnInfo (name = "res_name")val resName : String,
    @ColumnInfo (name = "res_price")val resPrice : String,
    @ColumnInfo (name = "res_ratings")val resRatings : String,
    @ColumnInfo (name = "res_image")val resImage :String
)