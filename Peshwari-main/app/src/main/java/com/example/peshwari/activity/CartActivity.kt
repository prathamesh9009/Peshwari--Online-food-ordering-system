package com.example.peshwari.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.peshwari.R
import com.example.peshwari.adapter.CartAdapter
import com.example.peshwari.adapter.RestaurantMenuAdapter
import com.example.peshwari.database.OrderEntity
import com.example.peshwari.database.OrdersDatabase
import com.example.peshwari.fragment.RestaurantMenuFragment
import com.example.peshwari.model.FoodItems
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CartActivity : AppCompatActivity() {
    lateinit var recyclerPay: RecyclerView
    lateinit var remptylayout: RelativeLayout
    lateinit var rheader: RelativeLayout
    lateinit var rbottom: RelativeLayout
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var btnBack: Button
    lateinit var btnproceed: Button
    lateinit var txtclearall : TextView
    lateinit var txtitem : TextView
    lateinit var txtresname: TextView
    lateinit var cartAdapter: CartAdapter
    private var orderList = ArrayList<FoodItems>()
    lateinit var txttotal: TextView
    lateinit var txtItemsTotals : TextView
    lateinit var txtToPay : TextView
    lateinit var resadapter : RestaurantMenuAdapter
    private var resId: Int = 0
    private var resName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        init()
        setUpCart()
        placeOrder()

    }
    private fun init()
    {
        btnBack = findViewById(R.id.btnback)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        remptylayout = findViewById(R.id.remptylayout)
        rheader = findViewById(R.id.rheader)
        txttotal =findViewById(R.id.txttotal)
        rbottom = findViewById(R.id.rbottom)
        txtresname = findViewById(R.id.txtResName)
        btnproceed = findViewById(R.id.btnPay)
        txtitem = findViewById(R.id.txtItemCount)
        txttotal = findViewById(R.id.txttotal)
        txtItemsTotals = findViewById(R.id.txtItemTotals)
        txtToPay = findViewById(R.id.txtToPayD)
        progressLayout = findViewById(R.id.progressLayout)
        txtresname.text = RestaurantMenuFragment.resName
        val bundle = intent.getBundleExtra("data")
        resId = bundle?.getInt("resId",0) as Int
        resName = bundle.getString("resName","") as String

    }
    private fun setUpCart()
    {
        recyclerPay = findViewById(R.id.recyclerviewPay)
        val dbLists = GetItemsFromDBSync(applicationContext).execute().get()
        for(element in dbLists)
        {
            orderList.addAll(
                Gson().fromJson(element.foodItems,Array<FoodItems>::class.java).asList()
            )
        }
        if(orderList.isEmpty())
        {
            rheader.visibility = View.GONE
            rbottom.visibility  = View.GONE
            progressLayout.visibility = View.VISIBLE
        }else{
            rheader.visibility = View.VISIBLE
            rbottom.visibility  = View.VISIBLE
            progressLayout.visibility = View.GONE
        }
        cartAdapter = CartAdapter(this@CartActivity,orderList)
        val mLayoutManager = LinearLayoutManager(this@CartActivity)
        recyclerPay.layoutManager = mLayoutManager
        recyclerPay.itemAnimator = DefaultItemAnimator()
        recyclerPay.adapter = cartAdapter
    }

   /* private fun clearAllCart(){
        txtclearall = findViewById(R.id.txtclearall)
        val clearCart = ClearDbAsync(applicationContext,resId.toString()).execute().get()
        RestaurantMenuAdapter.isCartEmpty = true
    }*/
    private fun placeOrder() {

        var sum = 0
        var pay = 27
        for (i in 0 until orderList.size) {
            sum += orderList[i].cost_for_one
            pay += orderList[i].cost_for_one

        }
        val total = "Rs.$sum"
        val finalPay = "Rs.$pay"
        val itemcount = "Items:${orderList.size}"
        txtItemsTotals.text = total
        txtToPay.text = finalPay
        txttotal.text = finalPay
        txtitem.text = itemcount


        btnproceed.setOnClickListener {
            progressLayout.visibility = View.VISIBLE
            rheader.visibility = View.GONE
            rbottom.visibility = View.GONE
            sendRequestToServer()
        }

    }

    private fun sendRequestToServer() {
        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put(
            "user_id",
            this@CartActivity.getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE).getString(
                "user_id",
                null
            ) as String
        )
        jsonParams.put("restaurant_id", RestaurantMenuFragment.resId?.toString())
        var sum = 0
        for (i in 0 until orderList.size)
        {
            sum += orderList[i].cost_for_one
        }
        jsonParams.put("total_cost",sum.toString())
        val foodArray = JSONArray()
        for(i in 0 until orderList.size)
        {
            val foodId = JSONObject()
            foodId.put("food_item_id",orderList[i].id)
            foodArray.put(i,foodId)
        }
        jsonParams.put("food",foodArray)


        val jsonObejctRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {

            try {
                val data = it.getJSONObject("data")
                val success = data.getBoolean("success")
                if(success)
                {
                    val clear = ClearDbAsync(applicationContext,resId.toString()).execute().get()
                    RestaurantMenuAdapter.isCartEmpty = true
                    startActivity(Intent(this@CartActivity,
                        ConfirmActivity::class.java))
                    ActivityCompat.finishAffinity(this@CartActivity)
                }else
                {
                    rheader.visibility = View.GONE
                    rbottom.visibility = View.GONE
                    Toast.makeText(this,"Some error occured",Toast.LENGTH_LONG).show()


                }
            }catch (e : Exception)
            {
                rheader.visibility = View.VISIBLE
                rbottom.visibility = View.VISIBLE
                e.printStackTrace()
            }
        },Response.ErrorListener {
            rheader.visibility = View.VISIBLE
            rbottom.visibility = View.VISIBLE
            Toast.makeText(this@CartActivity,it.message,Toast.LENGTH_SHORT).show()
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "/token"
                return headers
            }
        }
        queue.add(jsonObejctRequest)
    }




    class GetItemsFromDBSync(context: Context) : AsyncTask<Void,Void,List<OrderEntity>>()
    {
        val db = Room.databaseBuilder(context,OrdersDatabase::class.java,"order-db").build()
        override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }

    }
    class ClearDbAsync(context: Context,val resId:String) : AsyncTask<Void,Void,Boolean>()
    {
        val db = Room.databaseBuilder(context,OrdersDatabase::class.java,"order-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }

    }

   /* override fun onSupportNavigateUp(): Boolean {
        RestaurantMenuAdapter.isCartEmpty = true
        onBackPressed()
        return true
    }*/

    override fun onBackPressed() {
        val clear = ClearDbAsync(applicationContext,resId.toString()).execute().get()
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Confirmation")
        dialog.setMessage("By clicking ok will reset your cart items. Do you still want to proceed?")
        dialog.setPositiveButton("ok"){_,_ ->
            RestaurantMenuAdapter.isCartEmpty = true
            startActivity(Intent(this,HomePageActivity::class.java))
            Toast.makeText(applicationContext,"Cart is cleared", Toast.LENGTH_SHORT).show()
        }
        dialog.setNegativeButton("cancel"){_,_ ->

        }
        dialog.create()
        dialog.show()
    }
}