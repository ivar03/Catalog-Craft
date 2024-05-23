package com.ivar7284.catalogcraft.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ivar7284.catalogcraft.AllCatalogActivity
import com.ivar7284.catalogcraft.MainCatalogItemActivity
import com.ivar7284.catalogcraft.R
import com.ivar7284.catalogcraft.adapter.CatalogItemListAdapter
import org.json.JSONArray
import org.json.JSONObject

class CatalogItemListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var catalogListAdapter: CatalogItemListAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var heading: TextView
    private lateinit var allCatalogBtn: Button

    val URL = "http://panel.mait.ac.in:8012/catalogue/get/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog_item_list, container, false)

        recyclerView = view.findViewById(R.id.itemList_rv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        catalogListAdapter = CatalogItemListAdapter(JSONArray())
        catalogListAdapter.setOnItemClickListener(object : CatalogItemListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val clickedItem = catalogListAdapter.getItem(position)

                val intent = Intent(requireContext(), MainCatalogItemActivity::class.java)
                intent.putExtra("productData", clickedItem.toString())
                startActivity(intent)
            }
        })
        recyclerView.adapter = catalogListAdapter

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        fetchData()
        heading = view.findViewById(R.id.heading)

        allCatalogBtn = view.findViewById(R.id.allCatalog)
        allCatalogBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AllCatalogActivity::class.java))
        }

        return view
    }

    private fun fetchData() {
        val accessToken = sharedPreferences.getString("access_token", null)
        if (accessToken.isNullOrEmpty()) {
            Log.e("fetchData", "Access token is null or empty")
            return
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        val jsonArrayRequest = object : JsonArrayRequest(Request.Method.GET, URL, null,
            { response ->
                Log.i("listingCatalog", response.toString())
                val itemCount = response.length()
                Log.i("ItemCount", "Number of items in the response: $itemCount")
                val final : String = getString(R.string.your_catalog_item_list) + getString(R.string.total) + itemCount + getString(R.string.items)
                heading.text = final
                catalogListAdapter.updateData(response)
            },
            { error ->
                Log.i("error fetching", error.message.toString())
            }) {
            // Override the getHeaders() method to add the access token to the request headers
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }
        requestQueue.add(jsonArrayRequest)
    }

}
