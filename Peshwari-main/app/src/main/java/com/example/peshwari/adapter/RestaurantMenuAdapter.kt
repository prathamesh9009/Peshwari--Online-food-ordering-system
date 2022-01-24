package com.example.peshwari.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.peshwari.R
import com.example.peshwari.model.FoodItems

class RestaurantMenuAdapter(
    val context: Context,
    private val menuList : ArrayList<FoodItems>,
    private val listener : OnItemClickListener
): RecyclerView.Adapter<RestaurantMenuAdapter.MenuViewHolder>(){

    companion object{
        var isCartEmpty = true
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
      val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_single_row_menu_fragment,parent,false)
        return MenuViewHolder(itemView)
    }

    override fun getItemCount(): Int {
       return menuList.size
    }
    interface OnItemClickListener{
        fun onAddItemClickListener(foodItems: FoodItems)
        fun onRemoveClickListener(foodItems: FoodItems)
    }



    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val mObject = menuList[position]
        holder.foodname.text = mObject.name
        val prioe = "Rs.${mObject.cost_for_one}"
        holder.foodprice.text = prioe
        holder.foodcounter.text = (position + 1).toString()
        holder.btnadd.setOnClickListener {
            holder.btnadd.visibility = View.GONE
            holder.btnremove.visibility = View.VISIBLE
            listener.onAddItemClickListener(mObject)
        }

        holder.btnremove.setOnClickListener {
            holder.btnremove.visibility = View.GONE
            holder.btnadd.visibility = View.VISIBLE
            listener.onRemoveClickListener(mObject)

        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }



    class MenuViewHolder(view : View): RecyclerView.ViewHolder(view){

        val foodname : TextView = view.findViewById(R.id.txtFoodName)
        val foodprice : TextView = view.findViewById(R.id.txtFoodPrice)
        val foodcounter : TextView = view.findViewById(R.id.txtcounter)
        val btnadd : Button = view.findViewById(R.id.btnAdd)
        val btnremove  : Button = view.findViewById(R.id.btnRemove)
    }
}