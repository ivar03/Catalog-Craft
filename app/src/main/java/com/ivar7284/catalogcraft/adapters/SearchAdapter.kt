package com.ivar7284.catalogcraft

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivar7284.catalogcraft.dataclasses.Catalogue

class SearchAdapter(private val context: Context, private var catalogueList: ArrayList<Catalogue>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val catalogue = catalogueList[position]
        holder.productName.text = catalogue.productName
    }

    override fun getItemCount(): Int {
        return catalogueList.size
    }

    fun setData(newCatalogueList: ArrayList<Catalogue>) {
        catalogueList = newCatalogueList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
    }
}
