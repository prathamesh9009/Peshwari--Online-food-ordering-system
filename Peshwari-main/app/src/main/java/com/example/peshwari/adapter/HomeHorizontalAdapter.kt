package com.example.peshwari.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.peshwari.R
import com.example.peshwari.fragment.RestaurantMenuFragment
import com.example.peshwari.model.ListOfDetails
import com.example.peshwari.model.Restaurants
import com.facebook.shimmer.Shimmer
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class HomeHorizontalAdapter(val context: Context,val home : ArrayList<Restaurants>,val details : ArrayList<ListOfDetails>) : RecyclerView.Adapter<HomeHorizontalAdapter.HomeViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_row_home_fragment_horizontal,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
       return home.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val homeobject = home[position]
        val listobject = details[position]
        Picasso.get().load(homeobject.img_url).error(R.drawable.ic_food).into(holder.img)
        holder.txtresname.text = homeobject.name
        holder.txtgoahead.setOnClickListener {
            val fragment = RestaurantMenuFragment()
            val args = Bundle()
            args.putInt("id",homeobject.id)
            args.putString("name",homeobject.name)
            fragment.arguments = args
            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame,fragment)
            transaction.commit()
        }
        holder.txtdetails.text = listobject.listdetails
        holder.txtoffer.text = listobject.listofoffers

        holder.img.setOnClickListener {
            holder.img.animate().apply {
                duration = 1000
                rotationXBy(360f)
            }.start()

        }
    }
    class HomeViewHolder (view:View):RecyclerView.ViewHolder(view)
    {
        val img : ImageView = view.findViewById(R.id.img)
        val txtgoahead : TextView = view.findViewById(R.id.txtgoahead)
        val txtresname : TextView = view.findViewById(R.id.txtResName)
        val txtdetails : TextView = view.findViewById(R.id.txtdetails)
        val txtoffer : TextView = view.findViewById(R.id.txtoffer)


    }

}