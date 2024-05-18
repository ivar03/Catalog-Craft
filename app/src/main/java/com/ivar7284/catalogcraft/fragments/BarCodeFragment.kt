package com.ivar7284.catalogcraft.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.ivar7284.catalogcraft.BarCodeScanningActivity
import com.ivar7284.catalogcraft.CameraActivity
import com.ivar7284.catalogcraft.R

class BarCodeFragment : Fragment() {
    private lateinit var barcodeBtn: Button
    private lateinit var cameraBtn: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bar_code, container, false)

        //top navigation
        val form = view.findViewById<LinearLayout>(R.id.form_fill_ll)
        val barCode = view.findViewById<LinearLayout>(R.id.bar_code_ll)
        form.setOnClickListener {
            val addCatalogItemFragment = AddCatalogItemFragment()
            val fragmentManager: FragmentManager? = fragmentManager
            fragmentManager?.beginTransaction()?.replace(R.id.homeFrame, addCatalogItemFragment)?.commit()
        }
        barCode.setOnClickListener {
            val barcodeFragment = BarCodeFragment()
            val fragmentManager: FragmentManager? = fragmentManager
            fragmentManager?.beginTransaction()?.replace(R.id.homeFrame, barcodeFragment)?.commit()
        }

        barcodeBtn = view.findViewById(R.id.bar_code_btn)
        barcodeBtn.setOnClickListener {
            startActivity(Intent(requireContext(), BarCodeScanningActivity::class.java))
        }

        cameraBtn = view.findViewById(R.id.camera_btn)
        cameraBtn.setOnClickListener {
            startActivity(Intent(requireContext(), CameraActivity::class.java))
        }

        return view
    }

}