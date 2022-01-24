package com.example.peshwari.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.peshwari.R
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.lang.Exception
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" + "(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                ".{4,}" +  //at least 4 characters
                "$"
    )
    lateinit var txtsignup : TextView
    lateinit var txtforgotpassword : TextView
    lateinit var etmobilenumber : EditText
    lateinit var etUserPassword : EditText
    lateinit var btnLogin : Button
    private var misshowPassword = false
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        txtsignup = findViewById(R.id.txtsignup)
        txtforgotpassword = findViewById(R.id.txtforgotpassword)
        etmobilenumber = findViewById(R.id.etmobilenumber)
        etUserPassword = findViewById(R.id.etUserPassword)
        btnLogin = findViewById(R.id.btnLogin)
        sharedPreferences =getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)
        if(isLoggedIn)
        {
            startActivity(Intent(this@LoginActivity,
                HomePageActivity::class.java))
            finish()
        }
        title = "Log In"
        imgeye.setOnClickListener{
            misshowPassword = !misshowPassword
            showPassword(misshowPassword)
        }
        txtsignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity,
                RegistrationActivity::class.java))
        }
        txtforgotpassword.setOnClickListener{
            startActivity(Intent(this@LoginActivity,
                ForgotPasswordActivity::class.java))
        }
        btnLogin.setOnClickListener {

            if(usermobilevalidate() && userpassworovalidate()) {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val mobile_number = etmobilenumber.text.toString()
                val password = etUserPassword.text.toString()

                val jsonparams = JSONObject()
                jsonparams.put("mobile_number", mobile_number)
                jsonparams.put("password", password)

                val jsonobject = object : JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonparams,
                    Response.Listener {
                        println("Response is : $it")
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if(success)
                            {
                                savePreferences()
                                val response = data.getJSONObject("data")


                                sharedPreferences.edit().putString("user_id",response.getString("user_id")).apply()
                                sharedPreferences.edit().putString("user_name",response.getString("name")).apply()
                                sharedPreferences.edit().putString("user_email",response.getString("email")).apply()
                                sharedPreferences.edit().putString("user_mobile_number",response.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("user_address",response.getString("address")).apply()


                                startActivity(Intent(this@LoginActivity,
                                    HomePageActivity::class.java))
                            }
                            else
                            {
                                Toast.makeText(this@LoginActivity,"Please enter your registered details",Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this,"some error occured : $e",Toast.LENGTH_LONG).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(this,"Volley error occured",Toast.LENGTH_LONG).show()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String,String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "/token"
                        return headers
                    }
                }
                queue.add(jsonobject)
            }else
            {
                Toast.makeText(this@LoginActivity,"Please check your credentials",Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun usermobilevalidate() : Boolean{
        val usermobilevalidate = etmobilenumber.text.toString().trim()
        if(usermobilevalidate.isEmpty())
        {
            etmobilenumber.setError("Please fill your mobile")
            return false
        }else if(usermobilevalidate.length < 10)
        {
            etmobilenumber.setError("Mobile number should be equal to 10 digits")
            return false
        }else
        {
            etmobilenumber.setError(null)
            return true
        }
    }
    private fun userpassworovalidate() : Boolean {
        val userpasswordvalidate = etUserPassword.text.toString().trim()
        if (userpasswordvalidate.isEmpty()) {
            etUserPassword.setError("Please fill your password")
            return false
        } else if (!PASSWORD_PATTERN.matcher(userpasswordvalidate).matches()) {
            etUserPassword.setError("Password should contain special characters and digit")
            return false
        } else {
            etUserPassword.setError(null)
            return true
        }

    }
    private fun showPassword(isShow : Boolean)
    {
        if(isShow)
        {
            etUserPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            imgeye.setImageResource(R.drawable.ic_visibility_on)
        }else{
            etUserPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            imgeye.setImageResource(R.drawable.ic_visibility_off)
        }
        etUserPassword.setSelection(etUserPassword.text.toString().length)
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
    fun savePreferences ()
    {
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
    }
}