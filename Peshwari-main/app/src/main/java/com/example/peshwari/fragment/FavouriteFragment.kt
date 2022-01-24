package com.example.peshwari.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.peshwari.R
import com.example.peshwari.adapter.HomeAdapter
import com.example.peshwari.database.RestaurantsDatabase
import com.example.peshwari.database.RestaurantsEntity
import com.example.peshwari.model.Offer
import com.example.peshwari.model.Restaurants



class FavouriteFragment : Fragment() {


    lateinit var recyclerView: RecyclerView
    lateinit var r1 : RelativeLayout
    lateinit var r2 : RelativeLayout
    lateinit var progressLayout  : RelativeLayout
    lateinit var progressBar: ProgressBar
    private var restaurantList = arrayListOf<Restaurants>()
    private lateinit var allRestaurantsAdapter : HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)


        r1 = view.findViewById(R.id.r1)
        r2 = view.findViewById(R.id.r2)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        setUpRecycelr(view)

        return view
    }

    private fun setUpRecycelr (view: View){

        recyclerView = view.findViewById(R.id.recyclerview)
        val backgroundlist =FavouritesAsyncTask(activity as Context).execute().get()
        if(backgroundlist.isEmpty())
        {
            r1.visibility = View.GONE
            progressLayout.visibility = View.GONE
            r2.visibility = View.VISIBLE

        }else{
            r2.visibility = View.GONE
            progressLayout.visibility = View.GONE
            r1.visibility= View.VISIBLE

            for (i in backgroundlist)
            {
                restaurantList.add(
                    Restaurants(
                        i.res_id,
                        i.resName,
                        i.resRatings,
                        i.resPrice.toInt(),
                        i.resImage
                    )
                )

            }

            allRestaurantsAdapter =HomeAdapter(activity as Context,restaurantList)
            val layoutManager = LinearLayoutManager(activity)
            recyclerView.layoutManager =layoutManager
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = allRestaurantsAdapter
            recyclerView.setHasFixedSize(true)
        }
    }


    class FavouritesAsyncTask(val context: Context) :AsyncTask<Void,Void,List<RestaurantsEntity>>()
    {
        val db = Room.databaseBuilder(context,RestaurantsDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg p0: Void?): List<RestaurantsEntity> {

            return db.resDao().getAllRes()
        }

    }
}