package com.ivar7284.catalogcraft

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ivar7284.catalogcraft.fragments.AddCatalogItemFragment
import com.ivar7284.catalogcraft.fragments.CatalogItemListFragment
import com.ivar7284.catalogcraft.fragments.CategoryFragment
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

        //passing the data from camera activity
        val catalogData = intent.getStringExtra("catalog_data")
        val barCodeData = intent.getStringExtra("bar_code")
        Log.i("intentData", catalogData.toString())
        Log.i("intentData", barCodeData.toString())
        if (catalogData != null || barCodeData != null) {
            val addCatalogItemFragment = AddCatalogItemFragment().apply {
                arguments = Bundle().apply {
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


        // nav bar
        homeIcon = findViewById(R.id.home_icon)
        plusIcon = findViewById(R.id.plus_icon)
        profileBtn = findViewById(R.id.profileLayout)
        homeBtn = findViewById(R.id.homeLayout)
        addCatalogBtn = findViewById(R.id.addCatalog)

        profileBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
        }
        addCatalogBtn.setOnClickListener {
            addCatalogBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#945133"))
            homeIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFE3E3EB"))
            plusIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFE3E3EB"))
            val templatesFrag = TemplatesFragment()
            loadFragment(templatesFrag)
        }
        homeBtn.setOnClickListener {
            addCatalogBtn.setBackgroundResource(R.drawable.circle)
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }

    private fun loadAddCatalogItemFragment() {
        val addCatalogItemFragment = AddCatalogItemFragment()
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.homeFrame, addCatalogItemFragment)
            .commit()
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
