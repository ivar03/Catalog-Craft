package com.ivar7284.catalogcraft.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.ivar7284.catalogcraft.CameraActivity
import com.ivar7284.catalogcraft.R
import com.ivar7284.catalogcraft.SearchActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class BarCodeFragment : Fragment() {
    private lateinit var barcodeBtn: Button
    private lateinit var cameraBtn: Button
    private lateinit var searchBar: TextView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted: Boolean ->
            if (isGranted){
                showCamera()
            }else{
                //
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract()){ result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    setResult(result.contents)
//                    view?.findViewById<TextView>(R.id.bar_code_tv)?.text = result.contents.toString()
                }

            }
        }

    private fun setResult(string: String) {
        view?.findViewById<TextView>(R.id.bar_code_tv)?.text = string
        val URL = "http://panel.mait.ac.in:8012/csv-response/"

        //endpoint volley code

    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(
            ScanOptions.QR_CODE, ScanOptions.DATA_MATRIX, ScanOptions.UPC_A, ScanOptions.UPC_E, ScanOptions.EAN_8, ScanOptions.EAN_13, ScanOptions.CODE_39, ScanOptions.CODE_93, ScanOptions.CODE_128, ScanOptions.ITF,
            ScanOptions.PDF_417)
        options.setPrompt("SCAN QR CODE or BARCODE")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)
        scanLauncher.launch(options)
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bar_code, container, false)

        //top navigation
        searchBar = view.findViewById(R.id.search_catalog_et)
        searchBar.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        val formView = view.findViewById<LinearLayout>(R.id.form_ll)
        formView.setOnClickListener {
            val addCatalogItemFragment = AddCatalogItemFragment()
            val fragmentManager: FragmentManager? = fragmentManager
            fragmentManager?.beginTransaction()?.replace(R.id.homeFrame, addCatalogItemFragment)?.commit()
        }

        barcodeBtn = view.findViewById(R.id.bar_code_btn)
        barcodeBtn.setOnClickListener {
            checkPermissionCamera(requireContext())
        }

        cameraBtn = view.findViewById(R.id.camera_btn)
        cameraBtn.setOnClickListener {
            startActivity(Intent(requireContext(), CameraActivity::class.java))
        }

        return view
    }

    private fun checkPermissionCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                Toast.makeText(context, "Camera Permission required", Toast.LENGTH_SHORT).show()
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

}