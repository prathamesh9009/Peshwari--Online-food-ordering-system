package com.example.peshwari.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.peshwari.util.ConnectivityManager
import com.example.peshwari.R
import com.example.peshwari.adapter.HomeAdapter
import com.example.peshwari.adapter.HomeHorizontalAdapter
import com.example.peshwari.model.ListOfDetails
import com.example.peshwari.model.Offer
import com.example.peshwari.model.Restaurants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_cart.view.*
import kotlinx.android.synthetic.main.sort_dialog.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeFragment : Fragment() {


    lateinit var recyclerViewImage: RecyclerView
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recycleradapter: HomeAdapter
    lateinit var recycleradapter1: HomeHorizontalAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var rCount : RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var btnFilter: Button
    lateinit var txtrescount: TextView
    val restaurantsList = arrayListOf<Restaurants>()
    val restaurantsCount = ArrayList<Restaurants>()
    val detailsList = ArrayList<ListOfDetails>()
    val offerList = ArrayList<Offer>()
    var ratingcomparator = Comparator<Restaurants> { res1, res2 ->
        if (res1.rating.compareTo(res2.rating, true) == 0) {
            res1.name.compareTo(res2.name, true)
        } else {
            res1.rating.compareTo(res2.rating, true)
        }
    }
    var pricecomparator = Comparator<Restaurants> { resp1, resp2 ->
        if (resp1.cost_for_one.toString().compareTo(resp2.cost_for_one.toString(), true) == 0) {
            resp1.name.compareTo(resp2.name, true)
        } else {
            resp1.cost_for_one.toString().compareTo(resp2.cost_for_one.toString(), true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerViewImage = view.findViewById(R.id.recyclerviewImage)
        layoutManager = LinearLayoutManager(activity)
        rCount = view.findViewById(R.id.rCount)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        txtrescount = view.findViewById(R.id.txtrescount)

        rCount.visibility = View.GONE
        progressLayout.visibility = View.VISIBLE
        setHasOptionsMenu(true)

        btnFilter = view.findViewById(R.id.btnfilter)


        btnFilter.setOnClickListener {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setStroke(2,resources.getColor(R.color.colororange))
            btnFilter.setBackgroundDrawable(gradientDrawable)
            btnFilter.setTextColor(resources.getColor(R.color.colororange))
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                val id = item.itemId
                when (id) {
                    R.id.ic_sortPlowtohigh -> {
                        Collections.sort(restaurantsList, pricecomparator)
                        restaurantsList
                        Snackbar.make(view,"Sorted by price (Low to High)",Snackbar.LENGTH_LONG).show()

                    }
                    R.id.ic_sortPhightolow -> {
                        Collections.sort(restaurantsList, pricecomparator)
                        restaurantsList.reverse()
                        Snackbar.make(view,"Sorted by price (High to Low)",Snackbar.LENGTH_LONG).show()

                    }
                    R.id.ic_sortR -> {
                        Collections.sort(restaurantsList, ratingcomparator)
                        restaurantsList.reverse()
                        Snackbar.make(view,"Sorted by ratings",Snackbar.LENGTH_LONG).show()

                    }
                    else -> {

                    }

                }
                recycleradapter.notifyDataSetChanged()

                true

            }

            popupMenu.inflate(R.menu.popup_sort)
            popupMenu.show()

        }

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectivityManager().connectiityCheck(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            rCount.visibility = View.VISIBLE
                            progressLayout.visibility = View.GONE
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val resjsonObject = resArray.getJSONObject(i)
                                val resobject = Restaurants(
                                    resjsonObject.getString("id").toInt(),
                                    resjsonObject.getString("name"),
                                    resjsonObject.getString("rating"),
                                    resjsonObject.getString("cost_for_one").toInt(),
                                    resjsonObject.getString("image_url")
                                )
                                val rescount = "${(resobject.id) - 1} Restaurants near your area"
                                txtrescount.text = rescount
                                restaurantsList.add(resobject)
                                listOfOrderDetails()
                                recycleradapter = HomeAdapter(activity as Context, restaurantsList)
                                recycleradapter1 = HomeHorizontalAdapter(
                                    activity as Context,
                                    restaurantsList,
                                    detailsList
                                )

                                recyclerViewImage.layoutManager = LinearLayoutManager(
                                    activity as Context,
                                    RecyclerView.HORIZONTAL,
                                    false
                                )
                                recyclerViewImage.adapter = recycleradapter1
                                recyclerView.adapter = recycleradapter
                                recyclerView.layoutManager = layoutManager
                            }
                        } else {
                            Snackbar.make(view, "", Snackbar.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Snackbar.make(view, "Error : ${e}", Snackbar.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {

                    Snackbar.make(view, "Volley error occured", Snackbar.LENGTH_SHORT).show()
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not found")
            dialog.setIcon(R.drawable.ic_alert)
            dialog.setCancelable(false)
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    private fun listOfOrderDetails() {
        detailsList.add(ListOfDetails("COOKED WITH HYGIENE", "UP TO 50% OFF"))
        detailsList.add(ListOfDetails("FOOD COOKED WITH CAFE", "UP TO 25% OFF"))
        detailsList.add(ListOfDetails("0 CONTACT DELIVERY", "UP TO 15% OFF"))
        detailsList.add(ListOfDetails("FRESHLY MADE, SAFELY PACKED", "UP TO 10% OFF"))
        detailsList.add(ListOfDetails("EAT SAFE WITH OLD FAVOURITES", "UP TO 65% OFF"))
        detailsList.add(ListOfDetails("MEALS NEAR YOU", "UP TO 5% OFF"))
        detailsList.add(ListOfDetails("THE TASTE OF ORIGINAL PIZZAS", "UP TO 35% OFF"))
        detailsList.add(ListOfDetails("FOOD COOKED WITH CARE", "UP TO 45% OFF"))
        detailsList.add(ListOfDetails("HYGIENIC", "UP TO 5% OFF"))
        detailsList.add(ListOfDetails("GET BEST COFFEES", "UP TO 25% OFF"))
        detailsList.add(ListOfDetails("COOKED WITH CARE", "UP TO 15% OFF"))
        detailsList.add(ListOfDetails("GET YOUR BEST PIZZA", "UP TO 10% OFF"))
        detailsList.add(ListOfDetails("BEAT THE WINTER COLD", "UP TO 55% OFF"))
        detailsList.add(ListOfDetails("SAFE AND HYGIENIC", "UP TO 65% OFF"))
        detailsList.add(ListOfDetails("ON SNACKS", "UP TO 10% OFF"))
        detailsList.add(ListOfDetails("FOR YOUR SWEET TOOTH", "UP TO 5% OFF"))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.ic_aboutus -> {
                val fragment = FaqFragment()
                val transaction =
                    (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, fragment)
                transaction.commit()

            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

}
