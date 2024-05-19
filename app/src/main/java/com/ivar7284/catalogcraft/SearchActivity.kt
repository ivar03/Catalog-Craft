package com.ivar7284.catalogcraft

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.findViewTreeViewModelStoreOwner

class SearchActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var heading: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        overridePendingTransition(0,0)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchBar  = findViewById(R.id.search_catalog_et)
        heading = findViewById(R.id.heading)
        searchBar.hasFocus()
        searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                heading.visibility = View.VISIBLE
            }
        }

    }
}