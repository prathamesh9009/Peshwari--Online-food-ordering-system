package com.example.peshwari.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.peshwari.R
import com.example.peshwari.adapter.OrderHistoryAdapter
import com.example.peshwari.model.FoodItems
import com.example.peshwari.model.OrderDetails
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONException
import java.lang.Exception


class OrderHistoryFragment : Fragment() {

    lateinit var orderHistoryAdapter: OrderHistoryAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerViewOrderHistory: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var l1hasorder : RelativeLayout
    lateinit var r2hasnoorders: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    val orderHistorList = arrayListOf<OrderDetails>()
    val orderList = arrayListOf<FoodItems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(
            getString(R.string.preferences_file_name),
            Context.MODE_PRIVATE
        )

        recyclerViewOrderHistory = view.findViewById(R.id.recyclerviewOrderH)
        l1hasorder = view.findViewById(R.id.l1Hasorders)
        r2hasnoorders = view.findViewById(R.id.r2Noorders)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        val user_id = sharedPreferences.getString("user_id", "user_id")
        layoutManager = LinearLayoutManager(activity as Context)

        progressLayout.visibility = View.VISIBLE
        sendServerRequest(user_id)

        return view
    }
    private  fun sendServerRequest(userId : String?)
    {
        val queue = Volley.newRequestQueue(activity as Context)
        val url ="http://13.235.250.119/v2/orders/fetch_result/"
        val jsonObjectRequest = object :
            JsonObjectRequest(Method.GET, url + userId, null, Response.Listener {
                progressLayout.visibility = View.GONE
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        if (resArray.length() == 0) {
                            r2hasnoorders.visibility = View.GONE
                            l1hasorder.visibility = View.VISIBLE
                        } else {
                            for (i in 0 until resArray.length()) {
                                val orderObject = resArray.getJSONObject(i)
                                val foodItems = orderObject.getJSONArray("food_items")
                                val orderDetails = OrderDetails(
                                    orderObject.getInt("order_id"),
                                    orderObject.getString("restaurant_name"),
                                    orderObject.getString("total_cost"),
                                    orderObject.getString("order_placed_at"),
                                    foodItems
                                )
                                orderHistorList.add(orderDetails)
                                if (orderHistorList.isEmpty()) {
                                    l1hasorder.visibility = View.GONE
                                    r2hasnoorders.visibility = View.VISIBLE
                                } else {
                                    l1hasorder.visibility = View.VISIBLE
                                    r2hasnoorders.visibility = View.GONE
                                    if (activity != null) {
                                        orderHistoryAdapter = OrderHistoryAdapter(
                                            activity as Context,
                                            orderHistorList
                                        )
                                        val mLayoutManager =
                                            LinearLayoutManager(activity as Context)
                                        recyclerViewOrderHistory.layoutManager = mLayoutManager
                                        recyclerViewOrderHistory.itemAnimator = DefaultItemAnimator()
                                        recyclerViewOrderHistory.adapter = orderHistoryAdapter
                                    } else {
                                        queue.cancelAll(this::class.java.simpleName)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "/token"
                return headers
            }

        }
        queue.add(jsonObjectRequest)
    }
}