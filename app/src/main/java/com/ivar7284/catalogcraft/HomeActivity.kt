package com.ivar7284.catalogcraft

import android.annotation.SuppressLint
import android.content.Intent
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

class HomeActivity : AppCompatActivity() {

    private lateinit var homeBtn: LinearLayout
    private lateinit var profileBtn: LinearLayout
    private lateinit var addCatalogBtn: View

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

        val catalogItemList = CatalogItemListFragment()
        loadfragment(catalogItemList)

        // nav bar
        profileBtn = findViewById(R.id.profileLayout)
        homeBtn = findViewById(R.id.homeLayout)
        addCatalogBtn = findViewById(R.id.addCatalog)

        profileBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
        }
        addCatalogBtn.setOnClickListener {
            val addCatalogFrag = AddCatalogItemFragment()
            loadfragment(addCatalogFrag)
        }
        homeBtn.setOnClickListener {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

    private fun loadfragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .commit()
    }
}
