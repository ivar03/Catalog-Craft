package com.ivar7284.catalogcraft.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ivar7284.catalogcraft.HomeActivity
import com.ivar7284.catalogcraft.R
import org.json.JSONException
import org.json.JSONObject

class LoginFragment : Fragment() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: TextView

    val URL = "http://panel.mait.ac.in:8012/auth/login/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        email = view.findViewById(R.id.et_mail)
        password = view.findViewById(R.id.et_pass)
        loginBtn = view.findViewById(R.id.login_btn)
        registerBtn = view.findViewById(R.id.register_btn)

        registerBtn.setOnClickListener {
            val registerFrag = RegisterFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.loginFrame, registerFrag)
            fragmentTransaction.commit()
        }

        loginBtn.setOnClickListener {
            loginUser()
        }

        return view
    }

    private fun loginUser() {
        val requestQueue = Volley.newRequestQueue(requireContext())
        val mail = email.text.toString()
        val pass = password.text.toString()

        val req = JSONObject()
        req.put("email", mail)
        req.put("password", pass)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, URL, req,
            { response ->
                try {
                    val token = response.getString("access")
                    saveAccessToken(token)
                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("JSON Error", e.message.toString())
                }
            },
            { error ->
                Log.e("Volley Error", error.message.toString())
                Toast.makeText(requireContext(), "Login failed!", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(jsonObjectRequest)
    }

    private fun saveAccessToken(token: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("access_token", token)
            apply()
        }
    }
}
