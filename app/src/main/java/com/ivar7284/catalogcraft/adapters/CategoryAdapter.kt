package com.ivar7284.catalogcraft.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivar7284.catalogcraft.R
import com.ivar7284.catalogcraft.dataclasses.Category

class CategoryAdapter(
    private val categoryList: List<Category>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(category: Category)
    }

    private var selectedItemPosition: Int = RecyclerView.NO_POSITION

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.category_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_list, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.name

        // Change background color based on selected item
        if (position == selectedItemPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#CEE5FA"))
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#CDD8E2"))
        }

        holder.itemView.setOnClickListener {
            val previousItemPosition = selectedItemPosition
            selectedItemPosition = holder.adapterPosition

            notifyItemChanged(previousItemPosition)
            notifyItemChanged(selectedItemPosition)

            itemClickListener.onItemClick(category)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
