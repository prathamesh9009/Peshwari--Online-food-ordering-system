package com.example.peshwari.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foodItems")
data class OrderEntity (
    @PrimaryKey val resId : String,
    @ColumnInfo (name = "food_items") val foodItems : String
)