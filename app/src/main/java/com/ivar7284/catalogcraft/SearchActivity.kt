package com.ivar7284.catalogcraft

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivar7284.catalogcraft.dataclasses.Catalogue
import org.json.JSONArray
import org.json.JSONObject

class SearchActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var heading: TextView
    private lateinit var requestQueue: RequestQueue
    private lateinit var recyclerView: RecyclerView
    private lateinit var catalogueAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.search_catalog_rv)
        searchBar = findViewById(R.id.search_catalog_et)
        heading = findViewById(R.id.heading)
        requestQueue = Volley.newRequestQueue(this)
        catalogueAdapter = SearchAdapter(this, ArrayList())

        recyclerView.apply {
            adapter = catalogueAdapter
            // Set layout manager and other settings
        }

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                fetchData()
                true
            } else {
                false
            }
        }

        searchBar.setOnFocusChangeListener { _, hasFocus ->
            heading.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }
    }

    private fun fetchData() {
        val query = searchBar.text.toString()
        val url = "http://panel.mait.ac.in:8012/catalogue/search-catalogues/?query=$query"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Parse the response
                val jsonArray = JSONArray(response)
                val catalogueList = ArrayList<Catalogue>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val catalogue = Catalogue(
                        jsonObject.getString("product_name"),
                        jsonObject.getString("asin"),
                        jsonObject.getString("upc")
                    )
                    catalogueList.add(catalogue)
                }

                catalogueAdapter.setData(catalogueList)
            },
            { error ->
                Log.e("TAG", "Error fetching data: ${error.message}")
                // Handle error
            }
        )

        requestQueue.add(stringRequest)
    }
}
