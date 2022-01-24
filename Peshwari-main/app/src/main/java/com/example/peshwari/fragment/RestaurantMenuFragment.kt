
package com.example.peshwari.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.peshwari.R
import com.example.peshwari.activity.CartActivity
import com.example.peshwari.activity.HomePageActivity
import com.example.peshwari.activity.MainActivity
import com.example.peshwari.adapter.RestaurantMenuAdapter
import com.example.peshwari.database.*
import com.example.peshwari.model.FoodItems
import com.example.peshwari.util.ConnectivityManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.*
import org.json.JSONArray

class RestaurantMenuFragment : Fragment() {

    private lateinit var recyclerViewMenu: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var rvisible : RelativeLayout
    lateinit var txtitemcount : TextView
    lateinit var btnProceed: Button
    lateinit var btnBack: Button
    lateinit var layoutManager: LinearLayoutManager
    lateinit var sharedPreferences: SharedPreferences
    private var menuList = arrayListOf<FoodItems>()
    private var orderList = arrayListOf<FoodItems>()
    private lateinit var restaurantMenuAdapter: RestaurantMenuAdapter
    private var title: String? = ""


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var goToCart: Button
        var resId: Int? = 0
        var resName: String? = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_restaurant_menu, container, false)

        sharedPreferences =
            activity?.getSharedPreferences(
                getString(R.string.preferences_file_name),
                Context.MODE_PRIVATE
            ) as SharedPreferences
        resId = arguments?.getInt("id", 0)
        resName = arguments?.getString("name", "")
        (activity as AppCompatActivity).supportActionBar?.setTitle(resName)


        progressLayout = view.findViewById(R.id.progressLayout)
        txtitemcount = view.findViewById(R.id.txtnotificationcount)
        rvisible = view.findViewById(R.id.rbtnVisible)
        btnProceed = view.findViewById(R.id.btnProceed)
        layoutManager = LinearLayoutManager(activity)
        btnBack = view.findViewById(R.id.btnBack)
        progressLayout.visibility = View.VISIBLE
        rvisible.visibility = View.GONE
    //    btnProceed.visibility = View.GONE
        btnProceed.setOnClickListener {
            proceed()
        }
        setUpRestaurantMenu(view)
        back()
        setHasOptionsMenu(true)
        return view
    }


    private fun back() {

        btnBack.setOnClickListener {
            val dialog = AlertDialog.Builder(context as Activity)
            dialog.setTitle("Confirmation")
            dialog.setMessage("By clicking ok will reset your cart items. Do you still want to proceed?")
            dialog.setCancelable(false)
            dialog.setPositiveButton("ok") { _, _ ->
                Toast.makeText(context,"Cart is cleared",Toast.LENGTH_SHORT).show()
                val clearcart = CartActivity.ClearDbAsync(activity as Context, resId.toString()).execute().get()
                RestaurantMenuAdapter.isCartEmpty = true
                startActivity(Intent(context, HomePageActivity::class.java))
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun setUpRestaurantMenu(view: View) {
        recyclerViewMenu = view.findViewById(R.id.recyclerviewMenu)
        if (ConnectivityManager().connectiityCheck(activity as Context)) {

            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val jsonObjectRequest = object :
                JsonObjectRequest(Request.Method.GET, url + resId, null, Response.Listener {


                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val mObject = resArray.getJSONObject(i)
                                val foodItems = FoodItems(
                                    mObject.getString("id"),
                                    mObject.getString("name"),
                                    mObject.getString("cost_for_one").toInt()

                                )
                                menuList.add(foodItems)
                                restaurantMenuAdapter = RestaurantMenuAdapter(
                                    activity as Context,
                                    menuList,
                                    object : RestaurantMenuAdapter.OnItemClickListener {
                                        override fun onAddItemClickListener(foodItems: FoodItems) {
                                            orderList.add(foodItems)
                                            if (orderList.size > 0) {
                                                txtitemcount.text = (orderList.size).toString()
                                                rvisible.visibility = View.VISIBLE
                                              //  btnProceed.visibility = View.VISIBLE
                                                RestaurantMenuAdapter.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveClickListener(foodItems: FoodItems) {
                                            orderList.remove(foodItems)
                                            if(orderList.size > -1)
                                            {
                                                txtitemcount.text = (orderList.size).toString()
                                            }
                                            if (orderList.isEmpty()) {
                                                rvisible.visibility = View.GONE
                                                btnProceed.visibility = View.GONE
                                                RestaurantMenuAdapter.isCartEmpty = true
                                            }
                                        }
                                    }
                                )
                                val mLayoutManager = LinearLayoutManager(activity)
                                recyclerViewMenu.layoutManager = mLayoutManager
                                recyclerViewMenu.itemAnimator = DefaultItemAnimator()
                                recyclerViewMenu.adapter = restaurantMenuAdapter
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Snackbar.make(view, "Error : ${it}", Snackbar.LENGTH_LONG).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "/token"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            Snackbar.make(view, "No internet connection", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun proceed() {
        val gson = Gson()

        val foodItems = gson.toJson(orderList)
        val async = ItemsOfCart(activity as Context, resId.toString(), foodItems, 1).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putInt("resId", resId as Int)
            data.putString("resName", resName)
            val intent = Intent(activity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)

        } else {
            Toast.makeText((activity as Context), "Some unexpected error", Toast.LENGTH_SHORT)
                .show()

        }


    }

    class ItemsOfCart(
        context: Context,
        val restaurantId: String,
        val foodItems: String,
        val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, OrdersDatabase::class.java, "order-db").build()


        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }

            return false
        }

    }

}