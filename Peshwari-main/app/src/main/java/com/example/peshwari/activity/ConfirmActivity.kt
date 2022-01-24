package com.example.peshwari.activity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.RemoteViews
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieDrawable
import com.example.peshwari.R
import com.example.peshwari.fragment.OrderHistoryFragment
import kotlinx.android.synthetic.main.activity_confirm.*

class ConfirmActivity : AppCompatActivity() {
    lateinit var btnOk: Button
    lateinit var handler: Handler
    lateinit var txtwait : TextView
    lateinit var relativeLayoutP: RelativeLayout
    lateinit var relativeLayoutC: RelativeLayout
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "com.example.peshwari.activity"
    private var description = "Test notificaiton"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)
        btnOk = findViewById(R.id.btnok)
        txtwait = findViewById(R.id.txtwait)
        relativeLayoutP = findViewById(R.id.rProcessing)
        relativeLayoutC = findViewById(R.id.rConfirmed)
        handler = Handler()
        relativeLayoutP.visibility = View.VISIBLE
        relativeLayoutC.visibility = View.GONE
        handler.postDelayed(
            {
                relativeLayoutP.visibility = View.GONE
                relativeLayoutC.visibility = View.VISIBLE
                txtwait.setText("Thank you for your patience.\nYour order is confirmed")
                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                btnOk.setOnClickListener {
                    val intent = Intent(this@ConfirmActivity,HomePageActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationChannel =
                            NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
                        notificationChannel.enableLights(true)
                        notificationChannel.lightColor = Color.GREEN
                        notificationChannel.enableVibration(true)
                        notificationManager.createNotificationChannel(notificationChannel)

                        builder = Notification.Builder(this,channelId)
                            .setContentTitle("Peshwari team thanks you")
                            .setContentText("Your order is confirmed")
                            .setSmallIcon(R.drawable.logo)
                            .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.logo))
                            .setContentIntent(pendingIntent)

                    }else
                    {
                        builder = Notification.Builder(this)
                            .setContentTitle("Peshwari team thanks you")
                            .setContentText("Your order is confirmed")
                            .setSmallIcon(R.drawable.logo)
                            .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.logo))
                            .setContentIntent(pendingIntent)

                    }
                    notificationManager.notify(1234,builder.build())
                    startActivity(Intent(this, HomePageActivity::class.java))
                }
            }, 4000
        )


    }
}