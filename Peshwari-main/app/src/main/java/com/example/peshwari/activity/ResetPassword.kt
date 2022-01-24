package com.example.peshwari.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.peshwari.R
import org.json.JSONObject
import java.util.regex.Pattern

class ResetPassword : AppCompatActivity() {
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" + "(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                ".{4,}" +  //at least 4 characters
                "$"
    )

    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var btnProceed: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var txtmobile: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        txtmobile = findViewById(R.id.txtMobile)
        btnProceed = findViewById(R.id.btnProceed)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        if (intent != null) {
            txtmobile.text = intent.getStringExtra("user_mobile")
        }
        btnProceed.setOnClickListener {
            val otp = etOTP.text.toString()
            val newpassword = etNewPassword.text.toString()
            val mobile = txtmobile.text.toString()
            if (validateOtp() && validateNewPassword()) {
                resetPassword(mobile, newpassword, otp)
            } else {
                Toast.makeText(
                    this,
                  "Not successfull",
                    Toast.LENGTH_LONG
                ).show()

            }
        }

    }

    private fun resetPassword(mobile: String, newpassword: String, otp: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobile)
        jsonParams.put("password", newpassword)
        jsonParams.put("otp", otp)

        val jsonObejctRequest =
            object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        Toast.makeText(
                            this,
                            "Password has been successfully changed",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@ResetPassword, LoginActivity::class.java))
                        ActivityCompat.finishAffinity(this@ResetPassword)
                    } else {
                        val error = data.getString("errorMessage")
                        Toast.makeText(
                            this,
                            error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Error"+e.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(
                    this,
                    "Volley error occcured",
                    Toast.LENGTH_LONG
                ).show()

            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "/token"
                    return headers
                }
            }
        queue.add(jsonObejctRequest)
    }

    private fun validateOtp():Boolean
    {
        val validateOtp = etOTP.text.toString()
        if(validateOtp.isEmpty())
        {
            etOTP.setError("Please fill your OTP")
            return false
        }else if(validateOtp.length < 4)
        {
            etOTP.setError("OTP should contain 4 digits")
            return false
        }else
        {
            etOTP.setError(null)
            return true
        }
    }
    private fun validateNewPassword():Boolean{
        val validateNewPassword = etNewPassword.text.toString()
        if(validateNewPassword.isEmpty())
        {
            etNewPassword.setError("Please fill your password")
            return false
        }else if (!PASSWORD_PATTERN.matcher(validateNewPassword).matches())
        {
            etNewPassword.setError("Password should contain specail characters and digits")
            return false
        }else{
            etNewPassword.setError(null)
            return true
        }
    }
}