package com.ivar7284.catalogcraft

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

class MainCatalogItemActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageButton
    private val BaseUrl: String = "http://panel.mait.ac.in:8012"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_catalog_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productDataString = intent.getStringExtra("productData")
        val productData = JSONObject(productDataString)
        Log.i("intent data", productData.toString())

        // Extract necessary details from productData
        val productName = productData.optString("product_name").uppercase()
        val mrp = productData.optString("mrp")
        val category = productData.optString("category")
        val seller = productData.optString("seller")
        val unit = productData.optString("unit")
        val quantity = productData.optString("quantity")
        val image1 = productData.optString("product_image_1")
        val image2 = productData.optString("product_image_2")
        val image3 = productData.optString("product_image_3")
        val image4 = productData.optString("product_image_4")
        val image5 = productData.optString("product_image_5")

        // Set details to corresponding views
        findViewById<TextView>(R.id.pName_tv).text = productName
        findViewById<TextView>(R.id.mrp_tv).text = "Rs. $mrp"
        val descriptionTextView = findViewById<TextView>(R.id.description_tv)
        val details = "Category: $category\n" +
                "Seller: $seller\n" +
                "Unit: $unit\n" +
                "Quantity: $quantity"
        descriptionTextView.text = details

        // Load product image using Glide
        loadProductImage(R.id.image_1, image1)
        loadProductImage(R.id.image_2, image2)
        loadProductImage(R.id.image_3, image3)
        loadProductImage(R.id.image_4, image4)
        loadProductImage(R.id.image_5, image5)

        //back button
        backBtn = findViewById(R.id.ib_backBtn_mainCatalog)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun loadProductImage(imageViewId: Int, imageUrl: String?) {
        val imageView = findViewById<ImageView>(imageViewId)
        Glide.with(this)
            .load(BaseUrl + imageUrl)
            .override(1000, 4000)
            .into(imageView)
    }

}