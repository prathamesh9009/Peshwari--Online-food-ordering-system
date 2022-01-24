
package com.example.peshwari.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.peshwari.R
import org.json.JSONObject
import java.util.regex.Pattern


class RegistrationActivity : AppCompatActivity() {

    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" + "(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                ".{4,}" +  //at least 4 characters
                "$"
    )

    lateinit var txtlogin: TextView
    lateinit var etUserName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobile: TextView
    lateinit var etAddress: TextView
    lateinit var etPassword: TextView
    lateinit var btnRegister: Button
    lateinit var imgback: ImageView
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        txtlogin = findViewById(R.id.txtlogin)
        imgback = findViewById(R.id.imgBack)

        etUserName = findViewById(R.id.etUserName)
        etEmail = findViewById(R.id.etUserEmail)
        etMobile = findViewById(R.id.etMobile)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etUserPassword)
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)


        btnRegister = findViewById(R.id.btnRegister)
        txtlogin.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        imgback.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
        }





        btnRegister.setOnClickListener {

            if (validateUserName() && validateUserEmail() && validatePassword() && validateMobile() && validateAddress()) {


                val name = etUserName.text.toString()
                val mobilenumber = etMobile.text.toString()
                val password = etPassword.text.toString()
                val address = etAddress.text.toString()
                val email = etEmail.text.toString()
                val queue = Volley.newRequestQueue(this@RegistrationActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("name", name)
                jsonParams.put("mobile_number", mobilenumber)
                jsonParams.put("password", password)
                jsonParams.put("address", address)
                jsonParams.put("email", email)


                Toast.makeText(this@RegistrationActivity,"Name : $name"+"email : $email"+"password : $password"+"mobile : $mobilenumber"+"address : $address",Toast.LENGTH_LONG).show()
                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParams,
                    Response.Listener {
                        try {
                        println("Response is : $it")
                            val data=it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if(success)
                        {
                            val response = data.getJSONObject("data")

                            sharedPreferences.edit().putString("user_id",response.getString("user_id")).apply()
                            sharedPreferences.edit().putString("user_name",response.getString("name")).apply()
                            sharedPreferences.edit().putString("user_email",response.getString("email")).apply()
                            sharedPreferences.edit().putString("user_mobile_number",response.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("user_address",response.getString("address")).apply()


                            val intent = Intent(this@RegistrationActivity,
                                LoginActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@RegistrationActivity,
                                "Error occured",
                                Toast.LENGTH_LONG
                            ).show()
                        }}catch (e:Exception){
                            Toast.makeText(this@RegistrationActivity,
                                "Some error $e",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }, Response.ErrorListener {

                        Toast.makeText(this@RegistrationActivity,
                            "Volley error occured",
                            Toast.LENGTH_LONG
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "/token"
                        return headers

                    }

                }
                queue.add(jsonRequest)

            }
        }



    }

    fun validateUserName(): Boolean {
        val validateUserName = etUserName.text.toString()
        if (validateUserName.isEmpty()) {
            etUserName.setError("please fill your name")
            return false
        } else if (validateUserName.length < 3) {
            etUserName.setError("Name should have atleast 3 characters")
            return false
        } else {
            etUserName.setError(null)
            return true

        }

    }

    fun validateUserEmail(): Boolean {
        val validateUserEmail = etEmail.text.toString()
        if (validateUserEmail.isEmpty()) {
            etEmail.setError("please fill your email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(validateUserEmail).matches()) {
            etEmail.setError("Please you valid email address")
            return false
        } else {
            etEmail.setError(null)
            return true

        }

    }

    fun validatePassword(): Boolean {
        val validatePassword = etPassword.text.toString()
        if (validatePassword.isEmpty()) {
            etPassword.setError("please fill your password")
            return false
        } else if (!PASSWORD_PATTERN.matcher(validatePassword).matches()) {
            etPassword.setError("Password should contain special Characters and digits")
            return false
        } else {
            etPassword.setError(null)
            return true

        }
    }

    fun validateMobile(): Boolean {
        val validateMobile = etMobile.text.toString()
        if (validateMobile.isEmpty()) {
            etMobile.setError("please fill your mobile number")
            return false
        }/* else if (validateMobile.length > 10 ) {
            etMobile.setError("mobile number should contain only 10 digits");
            return false;

        }*/ else if (validateMobile.length < 10) {
            etMobile.setError("mobile number should contain 10 digits");
            return false;
        } else {
            etMobile.setError(null)
            return true

        }
    }

    fun validateAddress(): Boolean {
        val validateAddress = etAddress.text.toString()
        if (validateAddress.isEmpty()) {
            etAddress.setError("please fill your adress")
            return false
        } else {
            etAddress.setError(null)
            return true

        }

    }
}