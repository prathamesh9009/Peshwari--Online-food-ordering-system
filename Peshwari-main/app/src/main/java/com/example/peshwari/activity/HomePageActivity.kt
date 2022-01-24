package com.example.peshwari.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.peshwari.*
import com.example.peshwari.fragment.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class HomePageActivity : AppCompatActivity() {
    lateinit var navigationView: NavigationView
    lateinit var frameLayout: FrameLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtprofname: TextView
    lateinit var txtprofmobile: TextView
    private var backpressed: Long = 0
    lateinit var backSnackbar: Snackbar
    private var title: String? = ""
    var previousitem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        toolbar = findViewById(R.id.toolbar)

        drawerLayout = findViewById(R.id.drawerlayout)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)


        setUpToolbar()
        openDefaultFrag()
        updateNav()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@HomePageActivity,
            drawerLayout,
            R.string.opendrawer,
            R.string.closedrawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {


            if (previousitem != null) {
                previousitem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousitem = it


            when (it.itemId) {
                R.id.ic_home -> {
                    openDefaultFrag()
                    drawerLayout.closeDrawers()
                }
                R.id.ic_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()

                }

                R.id.ic_favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouriteFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Favourites Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.ic_orderhistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderHistoryFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Order history"
                    drawerLayout.closeDrawers()
                }
                R.id.ic_faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FaqFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Frequently asked questions"
                    drawerLayout.closeDrawers()
                }
                R.id.ic_logout -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Log out")
                    dialog.setMessage("Are you sure you want to logout")
                    dialog.setIcon(R.drawable.ic_alert)
                    dialog.setPositiveButton("ok") { _, _ ->
                        startActivity(
                            Intent(
                                this@HomePageActivity,
                                LoginActivity::class.java
                            )
                        )
                        sharedPreferences.edit().clear().apply()
                        finish()
                    }
                    dialog.setNegativeButton("cancle") { _, _ ->

                        openDefaultFrag()
                    }
                    drawerLayout.closeDrawers()
                    dialog.create()
                    dialog.show()
                }

            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    fun openDefaultFrag() {


        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame,
                HomeFragment()
            )
            .commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.ic_home)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when (frag) {
            !is HomeFragment -> openDefaultFrag()
            else -> {
                if (backpressed + 2000 > System.currentTimeMillis()) {
                    backSnackbar.dismiss()
                    finishAffinity()
                    return super.onBackPressed()
                } else {
                    backSnackbar = Snackbar.make(
                        findViewById(R.id.drawerlayout),
                        "Press back again to exit",
                        Snackbar.LENGTH_LONG
                    )
                    backSnackbar.show()

                }
                backpressed = System.currentTimeMillis()

            }
        }
    }

    /*fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.radioButton6 ->
                    if (checked) {
                        Toast.makeText(this@HomePageActivity, "price", Toast.LENGTH_SHORT).show()
                    }

                R.id.radioButton7 ->
                    if (checked) {
                        Toast.makeText(this@HomePageActivity, "rating", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }*/

    fun updateNav() {
        val header: View
        header = navigationView.getHeaderView(0)
        txtprofname = header.findViewById(R.id.txtprofname)
        txtprofmobile = header.findViewById(R.id.txtprofmobile)

        txtprofname.text = sharedPreferences.getString("user_name", null)
        val mobile = "+91-${sharedPreferences.getString("user_mobile_number", null)}"
        txtprofmobile.text = mobile
    }
}