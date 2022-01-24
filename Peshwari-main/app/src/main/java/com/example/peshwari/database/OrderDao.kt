package com.example.peshwari.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query

@Dao
interface OrderDao {

    @Insert
    fun insertOrder(orderEntity: OrderEntity)

    @Delete
    fun deleteOrder(orderEntity: OrderEntity)

    @Query ("SELECT * FROM foodItems")
    fun getAllOrders(): List<OrderEntity>

    @Query ("DELETE FROM foodItems WHERE resId = :resId")
    fun deleteOrders(resId : String)
}
