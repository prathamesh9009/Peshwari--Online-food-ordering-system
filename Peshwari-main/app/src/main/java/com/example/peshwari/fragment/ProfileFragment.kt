package com.example.peshwari.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.peshwari.R


class ProfileFragment : Fragment() {

    lateinit var txtusername : TextView
    lateinit var txtmobile : TextView
    lateinit var txtaddress : TextView
    lateinit var txtemail : TextView
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_profile, container, false)
            txtusername = view.findViewById(R.id.txtusername)
            txtmobile = view.findViewById(R.id.txtmobile)
            txtaddress = view.findViewById(R.id.txtaddress)
            txtemail = view.findViewById(R.id.txtemail)
            sharedPreferences = (activity as FragmentActivity).getSharedPreferences(getString(
                R.string.preferences_file_name
            ),Context.MODE_PRIVATE)

            txtusername.text = sharedPreferences.getString("user_name",null)
            txtemail.text = sharedPreferences.getString("user_email",null)
            val mobile = "+91-${sharedPreferences.getString("user_mobile_number",null)}"
            txtmobile.text = mobile
            txtaddress.text = sharedPreferences.getString("user_address",null)

            return view


        }

    }