package com.ivar7284.catalogcraft.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.ivar7284.catalogcraft.R
import org.json.JSONArray
import org.json.JSONObject

class CatalogItemListAdapter(private var catalogArray: JSONArray) :
    RecyclerView.Adapter<CatalogItemListAdapter.MyViewHolder>() {

        private  val URL = "http://panel.mait.ac.in:8012"

    private var mListener: onItemClickListener = object : onItemClickListener {
        override fun onItemClick(position: Int) {
        }
    }

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class MyViewHolder(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val product_image_1: ShapeableImageView = itemView.findViewById(R.id.productImageView)
        val product_name: TextView = itemView.findViewById(R.id.productNameTextView)
        val mrp: TextView = itemView.findViewById(R.id.productPriceTextView)
        val seller: TextView = itemView.findViewById(R.id.storeNameTextView)
        val category: TextView = itemView.findViewById(R.id.categoryNameTextView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.catalog_item_list_view, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return catalogArray.length()
    }

    fun getItem(position: Int): JSONObject {
        return catalogArray.getJSONObject(position)
    }

    fun updateData(newArray: JSONArray) {
        catalogArray = newArray
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = catalogArray.getJSONObject(position)

        // Retrieving data from JSONObject
        val productName = currentItem.optString("product_name")
        val mrp = "Rs. " + currentItem.optString("mrp")
        val seller = "Seller: " + currentItem.optString("seller")
        val productImage1 = URL + currentItem.optString("product_image_1")
        val category = "Category: " + currentItem.optString("category")

        Log.d("AdapterForCatalogList", "Product Image URLs:")
        Log.d("AdapterForCatalogList", "product_image_1: $productImage1")

        if (productImage1.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(productImage1)
                .thumbnail(0.1f)
                .into(holder.product_image_1)
        }

        Log.d("AdapterForCatalogList", "Image loaded successfully")

        holder.product_name.text = productName
        holder.mrp.text = mrp
        holder.seller.text = if (seller.isNotEmpty()) seller else "Unknown Store"
        holder.category.text = category
    }
}
