package com.ivar7284.catalogcraft

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ivar7284.catalogcraft.databinding.ActivityBarCodeScanningBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader

class BarCodeScanningActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted: Boolean ->
            if (isGranted){
                showCamera()
            }else{
                //
            }
        }

    private lateinit var binding: ActivityBarCodeScanningBinding

    private val scanLauncher =
        registerForActivityResult(ScanContract()){ result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    setResult(result.contents)
                }

            }
        }

    @SuppressLint("SetTextI18n")
    private fun setResult(string: String) {
        binding.textresult.text = string

        val URL = "http://panel.mait.ac.in:8012/csv-response/"

        //endpoint volley code  

    }


    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE, ScanOptions.DATA_MATRIX, ScanOptions.UPC_A, ScanOptions.UPC_E, ScanOptions.EAN_8, ScanOptions.EAN_13, ScanOptions.CODE_39, ScanOptions.CODE_93, ScanOptions.CODE_128, ScanOptions.ITF,
            ScanOptions.PDF_417)
        options.setPrompt("SCAN QR CODE or BARCODE")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)
        scanLauncher.launch(options)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initBinding()

    }

    private fun initBinding() {
        binding = ActivityBarCodeScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        binding.fab.setOnClickListener {
            checkPermissionCamera(this)
        }
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