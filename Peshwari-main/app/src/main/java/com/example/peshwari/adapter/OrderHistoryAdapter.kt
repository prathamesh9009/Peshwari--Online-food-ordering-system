package com.example.peshwari.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peshwari.R
import com.example.peshwari.model.FoodItems
import com.example.peshwari.model.OrderDetails
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(val context: Context, val history : ArrayList<OrderDetails>) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_single_row_order_history,parent,false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
       return history.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
      val history_object = history[position]
        holder.txtResName.text = history_object.restaurantname
        val total = "Total price : Rs.${history_object.totalcost}"
        holder.txtcost.text = total
        holder.resCounter.text = (position + 1).toString()
        holder.txtDate.text = formatDate(history_object.orderPlaced)
        setUpRecycler(holder.recyclerResHistory,history_object)




    }
    class OrderHistoryViewHolder (view : View) : RecyclerView.ViewHolder(view)
    {
        val txtResName: TextView = view.findViewById(R.id.txtResHistoryResName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtcost : TextView = view.findViewById(R.id.txttotalcost)
        val recyclerResHistory: RecyclerView = view.findViewById(R.id.recyclerResHistoryItems)
        val resCounter : TextView = view.findViewById(R.id.txtcounter)
    }
    private fun setUpRecycler(recyclerResHistory: RecyclerView, orderHistoryList: OrderDetails) {
        val foodItemsList = ArrayList<FoodItems>()
        for (i in 0 until orderHistoryList.foodItems.length()) {
            val foodJson = orderHistoryList.foodItems.getJSONObject(i)
            foodItemsList.add(
                FoodItems(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost").toInt()
                )
            )
        }
        val cartItemAdapter = CartAdapter(context, foodItemsList)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerResHistory.layoutManager = mLayoutManager
        recyclerResHistory.itemAnimator = DefaultItemAnimator()
        recyclerResHistory.adapter = cartItemAdapter
    }

    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date
        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }
}
