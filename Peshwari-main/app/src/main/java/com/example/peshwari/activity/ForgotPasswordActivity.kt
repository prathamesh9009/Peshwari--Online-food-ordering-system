package com.example.peshwari.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.peshwari.util.ConnectivityManager
import com.example.peshwari.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var imgback: ImageView
    lateinit var etmobile: EditText
    lateinit var etEmail: EditText
    lateinit var btnResetPassword: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        imgback = findViewById(R.id.imgBack)
        etmobile = findViewById(R.id.etmobilenumber)
        etEmail = findViewById(R.id.etEmail)
        btnResetPassword = findViewById(R.id.btnPasswordReset)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        imgback.setOnClickListener {
            val intent = Intent(
                this@ForgotPasswordActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
        }

        btnResetPassword.setOnClickListener {


            if (ConnectivityManager().connectiityCheck(this)) {
                if (validatemobilenumber() && validateemail()) {
                    progressLayout.visibility = View.GONE
                    progressBar.visibility = View.INVISIBLE
                    sendOTP(etmobile.text.toString(), etEmail.text.toString())
                } else {
                    Snackbar.make(
                        findViewById(R.id.forgotpassword),
                        "Please check your credentials",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }else
            {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet connection not found")
                dialog.setIcon(R.drawable.ic_alert)
                dialog.setPositiveButton("Open settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(intent)
                }
                dialog.setCancelable(false)
                dialog.create()
                dialog.show()
            }
        }
    }

    private fun sendOTP(mobilenumber: String, email: String) {

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobilenumber)
        jsonParams.put("email", email)

        val jsonObejectRequest =
            object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {


                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {

                        val firsttry = data.getBoolean("first_try")
                        if (firsttry) {
                            val dialog = AlertDialog.Builder(this)
                            dialog.setTitle("Note")
                            dialog.setMessage("Please check your mail")
                            dialog.setIcon(R.drawable.ic_email)
                            dialog.setCancelable(false)
                            dialog.setPositiveButton("ok") { _, _ ->
                                val intent =
                                    Intent(this@ForgotPasswordActivity, ResetPassword::class.java)
                                intent.putExtra("user_mobile", mobilenumber)
                                startActivity(intent)
                            }
                            dialog.create()
                            dialog.show()
                        } else {
                            val dialog = AlertDialog.Builder(this)
                            dialog.setTitle("Note")
                            dialog.setMessage("Please check your previous mail")
                            dialog.setIcon(R.drawable.ic_email)
                            dialog.setCancelable(false)
                            dialog.setPositiveButton("ok") { _, _ ->
                                val intent =
                                    Intent(this@ForgotPasswordActivity, ResetPassword::class.java)
                                intent.putExtra("user_mobile", mobilenumber)
                                startActivity(intent)
                            }
                            dialog.create()
                            dialog.show()
                        }
                    } else {
                        Snackbar.make(
                            findViewById(R.id.forgotpassword),
                            "Please enter your registered mobile number",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Snackbar.make(
                        findViewById(R.id.forgotpassword),
                        "Response error :"+e.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            }, Response.ErrorListener {

                Snackbar.make(
                    findViewById(R.id.forgotpassword),
                    "Response error" +it.toString(),
                    Snackbar.LENGTH_LONG
                ).show()

            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "/token"
                    return headers
                }
            }
        queue.add(jsonObejectRequest)
    }

    private fun validatemobilenumber(): Boolean {
        val validatemobilenumber = etmobile.text.toString()
        if (validatemobilenumber.isEmpty()) {
            etmobile.setError("Please fill your mobile number")
            return false
        } else if (validatemobilenumber.length < 10) {
            etmobile.setError("Mobile number should contain 10 digits")
            return false
        } else {
            etmobile.setError(null)
            return true
        }

    }

    private fun validateemail(): Boolean {
        val validateemail = etEmail.text.toString()
        if (validateemail.isEmpty()) {
            etEmail.setError("Please fill your email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(validateemail).matches()) {
            etEmail.setError("Please enter your registered email id")
            return false
        } else {
            etEmail.setError(null)
            return true
        }
    }
}