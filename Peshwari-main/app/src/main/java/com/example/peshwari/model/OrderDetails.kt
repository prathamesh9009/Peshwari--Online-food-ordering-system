package com.example.peshwari.model

import org.json.JSONArray

data class OrderDetails (
    val orderid : Int,
    val restaurantname : String,
    val totalcost : String,
    val orderPlaced : String,
    val foodItems: JSONArray
)