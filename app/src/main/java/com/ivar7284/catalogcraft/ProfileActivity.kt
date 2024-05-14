package com.ivar7284.catalogcraft

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    private lateinit var backBtn: LinearLayout

    private val URL = "http://panel.mait.ac.in:8012/auth/user-details/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        //back button
        backBtn = findViewById(R.id.PBackBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val accessToken = getAccessToken()

        if (accessToken.isNullOrEmpty()) {
            Log.e("fetchData", "Access token is null or empty")
            return
        }

        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, URL, null,
            { response ->
                val name = response.getString("name")
                val email = response.getString("email")
                val phone = response.getString("number")

                findViewById<TextView>(R.id.PName).text = "Name: " + name
                findViewById<TextView>(R.id.Pemail).text = "Email: " + email
                findViewById<TextView>(R.id.Pphone).text = "Mobile No.: " + phone
            },
            { error ->
                Log.i("error fetching", error.message.toString())
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }
        requestQueue.add(jsonObjectRequest)
    }

    private fun getAccessToken(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }
}
