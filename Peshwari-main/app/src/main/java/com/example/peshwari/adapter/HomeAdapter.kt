package com.example.peshwari.adapter

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.peshwari.R
import com.example.peshwari.fragment.RestaurantMenuFragment
import com.example.peshwari.database.RestaurantsDatabase
import com.example.peshwari.database.RestaurantsEntity
import com.example.peshwari.model.Offer
import com.example.peshwari.model.Restaurants
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlin.math.acos


class HomeAdapter(val context:Context,val restaurants : ArrayList<Restaurants>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
       val view = LayoutInflater.from(parent.context)
           .inflate(R.layout.recycler_single_row_home_fragment,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val res_object = restaurants[position]
        holder.txtFoodName.text = res_object.name
        holder.txtRating.text = res_object.rating
        val cost_for_one = "Rs. ${res_object.cost_for_one}/person"
        holder.txtPrice.text = cost_for_one
        Picasso.get().load(res_object.img_url).error(R.drawable.ic_food).into(holder.imgfood)


        val listoffavourties = GetAllFavAsyncTask(context).execute().get()
        if (listoffavourties.isNotEmpty() && listoffavourties.contains(res_object.id.toString())) {
            holder.imgfav.setImageResource(R.drawable.ic_checkfav)
        }   else {
            holder.imgfav.setImageResource(R.drawable.ic_fav)
        }

        holder.imgfav.setOnClickListener{
            val restaurantsEntity = RestaurantsEntity(
                res_object.id,
                res_object.name,
                res_object.cost_for_one.toString(),
                res_object.rating,
                res_object.img_url
            )
            if(!DBAsyncTask(context,restaurantsEntity,1).execute().get())
            {
                val async = DBAsyncTask(context,restaurantsEntity,2).execute()
                val result =async.get()
                if(result){
                    holder.imgfav.setImageResource(R.drawable.ic_checkfav)
                    Snackbar.make(holder.itemView,"${res_object.name} added to favourites",Snackbar.LENGTH_LONG).show()
                }

            }else{
                val async = DBAsyncTask(context,restaurantsEntity,3).execute()
                val result = async.get()
                if(result)
                {
                    holder.imgfav.setImageResource(R.drawable.ic_fav)
                    Snackbar.make(holder.itemView,"${res_object.name} removed from favourites",Snackbar.LENGTH_LONG).show()
                }
            }
        }

        holder.card.setOnClickListener {

          ///  Snackbar.make(holder.itemView.rootView,"Restaurant Name : ${res_object.name}, Rating : ${res_object.rating}",Snackbar.LENGTH_LONG).show()
            val fragment = RestaurantMenuFragment()
            val args = Bundle()
            args.putInt("id",res_object.id)
            args.putString("name",res_object.name)
            fragment.arguments = args
            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame,fragment)
            transaction.commit()
        }


    }

    class HomeViewHolder(view:View) : RecyclerView.ViewHolder(view)
    {
        val imgfood : ImageView = view.findViewById(R.id.img_food)
        val txtFoodName : TextView = view.findViewById(R.id.txtFoodName)
        val txtPrice : TextView = view.findViewById(R.id.txtFoodPrice)
        val txtRating : TextView = view.findViewById(R.id.txtRating)
        val imgfav : ImageView = view.findViewById(R.id.img_fav)
        val card : CardView =view.findViewById(R.id.cardview)

    }
    class DBAsyncTask(val context: Context,val restaurantsEntity: RestaurantsEntity,val mode : Int) : AsyncTask<Void,Void,Boolean>()
    {
        val db = Room.databaseBuilder(context,RestaurantsDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean{

            when(mode)
            {
                1 -> {
                    val restaurant :RestaurantsEntity? = db.resDao().getAllId(restaurantsEntity.res_id.toString())
                    db.close()
                    return restaurant != null
                }
                2 ->{
                    db.resDao().insertRes(restaurantsEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.resDao().deleteRes(restaurantsEntity)
                    db.close()
                    return true
                }

            }
            return false

        }

    }

        class GetAllFavAsyncTask(context: Context): AsyncTask<Void,Void,List<String>>()
    {
        val db = Room.databaseBuilder(context,RestaurantsDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg p0: Void?): List<String> {

            val list = db.resDao().getAllRes()
            val listId = arrayListOf<String>()
            for(i in list) {
                listId.add(i.res_id.toString())
            }
            return listId
        }

    }
    }