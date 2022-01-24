package com.example.peshwari.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.peshwari.util.ConnectivityManager
import com.example.peshwari.R
import com.github.ybq.android.spinkit.style.Wave

class MainActivity : AppCompatActivity() {
    lateinit var txtpeshwari: TextView
    lateinit var handler: Handler
    lateinit var animation: Animation
    lateinit var progressBar: ProgressBar
    lateinit var wave: Wave
    lateinit var relativeLayout : RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        relativeLayout = findViewById(R.id.rmain)
        txtpeshwari = findViewById(R.id.txtpeshwari)
        animation = AnimationUtils.loadAnimation(
            this@MainActivity,
            R.anim.splash_transition
        )
        txtpeshwari.startAnimation(animation)
        handler = Handler()
      /*  progressBar = findViewById(R.id.spin)
        val wave = Wave()*/

        if (ConnectivityManager().connectiityCheck(this@MainActivity)) {

            relativeLayout.visibility = View.VISIBLE
            handler.postDelayed(
                {
                    //progressBar.setIndeterminateDrawable(wave)
                    val intent = Intent(
                        this@MainActivity,
                        LoginActivity::class.java
                    )
                    startActivity(intent)
                }, 3000
            )
        }
    else
    {
        relativeLayout.visibility = View.INVISIBLE
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Error")
        dialog.setMessage("Internet connection is not found")
        dialog.setIcon(R.drawable.ic_alert)
        dialog.setPositiveButton("Open settings"){_,_ ->
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            finish()

        }
        dialog.setNegativeButton("cancel"){_,_ ->
            ActivityCompat.finishAffinity(this)
        }
        dialog.create()
        dialog.show()
    }
}
    override fun onPause() {
        super.onPause()
        finish()
    }
}