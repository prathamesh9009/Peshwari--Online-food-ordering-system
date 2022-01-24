package com.example.peshwari.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.peshwari.R
import com.example.peshwari.database.OrderEntity
import com.example.peshwari.model.FoodItems

class CartAdapter(val context : Context, val ordeList : ArrayList<FoodItems>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_single_row_pay,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {

        return ordeList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
       val order_object = ordeList[position]
        holder.txtfoodname.text = order_object.name
        val cost = "Rs.${order_object.cost_for_one}"
        holder.txtfoodprice.text = cost

    }
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val imgcheck : ImageView = view.findViewById(R.id.imgcheck)
        val txtfoodname : TextView = view.findViewById(R.id.txtFoodName)
        val txtfoodprice : TextView = view.findViewById(R.id.txtFoodPrice)

    }

}