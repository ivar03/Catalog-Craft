package com.ivar7284.catalogcraft

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.ivar7284.catalogcraft.fragments.AddCatalogItemFragment
import com.ivar7284.catalogcraft.fragments.CatalogItemListFragment
import com.ivar7284.catalogcraft.fragments.TemplatesFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var homeBtn: LinearLayout
    private lateinit var profileBtn: LinearLayout
    private lateinit var addCatalogBtn: View
    private lateinit var homeIcon: View
    private lateinit var plusIcon: View

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        overridePendingTransition(0, 0)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check for data from camera or barcode
        val catalogData = intent.getStringExtra("catalog_data")
        val catalogue = intent.getStringExtra("catalogue")
        val barCodeData = intent.getStringExtra("bar_code")
        if (catalogData != null || barCodeData != null || catalogue != null) {
            val addCatalogItemFragment = AddCatalogItemFragment().apply {
                arguments = Bundle().apply {
                    putString("catalogue", catalogue)
                    putString("catalog_data", catalogData)
                    putString("bar_code", barCodeData)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.homeFrame, addCatalogItemFragment)
                .commit()
        } else {
            val catalogItemList = CatalogItemListFragment()
            loadFragment(catalogItemList)
        }


        // Navigation bar setup
        homeIcon = findViewById(R.id.home_icon)
        plusIcon = findViewById(R.id.plus_icon)
        profileBtn = findViewById(R.id.profileLayout)
        homeBtn = findViewById(R.id.homeLayout)
        addCatalogBtn = findViewById(R.id.addCatalog)

        profileBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
        }
        addCatalogBtn.setOnClickListener {
            addCatalogBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#334B94"))
            homeIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEFF8FF"))
            plusIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEFF8FF"))
            val templatesFrag = TemplatesFragment()
            loadFragment(templatesFrag)
        }
        homeBtn.setOnClickListener {
            addCatalogBtn.setBackgroundResource(R.drawable.circle)
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .commit()
    }
}
