package com.example.peshwari.database


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantsEntity::class],version = 1)
abstract class RestaurantsDatabase : RoomDatabase(){
    abstract fun resDao() : Dao
}