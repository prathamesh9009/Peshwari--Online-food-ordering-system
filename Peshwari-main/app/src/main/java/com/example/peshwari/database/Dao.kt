package com.example.peshwari.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Insert
    fun insertRes (restaurantsEntity: RestaurantsEntity)

    @Delete
    fun deleteRes (restaurantsEntity: RestaurantsEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRes () : List<RestaurantsEntity>

    @Query("SELECT * FROM restaurants WHERE res_id = :resid")
    fun getAllId(resid : String) : RestaurantsEntity
}