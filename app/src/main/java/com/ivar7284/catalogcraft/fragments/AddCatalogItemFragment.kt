package com.ivar7284.catalogcraft.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ivar7284.catalogcraft.R
import org.json.JSONObject
import java.util.Locale

class AddCatalogItemFragment : Fragment() {

    private lateinit var quantity: EditText
    private lateinit var gst: EditText
    private lateinit var unit: EditText
    private lateinit var hsnCode: EditText
    private lateinit var buyPrice: EditText
    private lateinit var sellPrice: EditText
    private lateinit var MRP: EditText
    private lateinit var productName: EditText
    private lateinit var categoryName: EditText

    private lateinit var pnameMic: View
    private lateinit var mrpMic: View
    private lateinit var spriceMic: View
    private lateinit var bpriceMic: View
    private lateinit var hsncodeMic: View
    private lateinit var gstMic: View
    private lateinit var unitMic: View
    private lateinit var quantityMic: View
    private lateinit var categoryMic: View

    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var image4: ImageView
    private lateinit var image5: ImageView
    private lateinit var image6: ImageView
    private lateinit var uploadImg: Button

    private lateinit var sharedPreferences: SharedPreferences
    private val URL = "http://panel.mait.ac.in:8012/catalogue/create/"

    private val PICK_IMAGES_REQUEST = 123
    private val Max_IMAGES = 6

    private val REQUEST_CODE_SPEECH_INPUT = 1

    private val req = JSONObject()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_catalog_item, container, false)

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

        //form stuff
        quantity = view.findViewById(R.id.quantity)
        gst = view.findViewById(R.id.gst)
        unit = view.findViewById(R.id.unit)
        hsnCode = view.findViewById(R.id.hsnCode)
        buyPrice = view.findViewById(R.id.buyPrize)
        sellPrice = view.findViewById(R.id.sellPrize)
        MRP = view.findViewById(R.id.MRP)
        productName = view.findViewById(R.id.productName)
        categoryName = view.findViewById(R.id.category)

        pnameMic = view.findViewById(R.id.mic_pname)
        mrpMic = view.findViewById(R.id.mic_mrp)
        spriceMic = view.findViewById(R.id.mic_sprice)
        bpriceMic = view.findViewById(R.id.mic_bprice)
        hsncodeMic = view.findViewById(R.id.mic_hsncode)
        gstMic = view.findViewById(R.id.mic_gst)
        unitMic = view.findViewById(R.id.mic_unit)
        quantityMic = view.findViewById(R.id.mic_quantity)
        categoryMic = view.findViewById(R.id.mic_category)

        image1 = view.findViewById(R.id.image1)
        image2 = view.findViewById(R.id.image2)
        image3 = view.findViewById(R.id.image3)
        image4 = view.findViewById(R.id.image4)
        image5 = view.findViewById(R.id.image5)
        image6 = view.findViewById(R.id.image6)
        uploadImg = view.findViewById(R.id.uploadImgBtn)

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        pnameMic.setOnClickListener {
            pnameMic.isActivated = true
            mrpMic.isActivated = false
            spriceMic.isActivated = false
            bpriceMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = false
            unitMic.isActivated = false
            quantityMic.isActivated = false
            categoryMic.isActivated = false
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        mrpMic.setOnClickListener {
            pnameMic.isActivated = false
            mrpMic.isActivated = true
            spriceMic.isActivated = false
            bpriceMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = false
            unitMic.isActivated = false
            quantityMic.isActivated = false
            categoryMic.isActivated = false
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak ttp text")
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            }catch (e:Exception){
                Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        spriceMic.setOnClickListener {
            pnameMic.isActivated = false
            mrpMic.isActivated = false
            spriceMic.isActivated = true
            bpriceMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = false
            unitMic.isActivated = false
            quantityMic.isActivated = false
            categoryMic.isActivated = false
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak ttp text")
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            }catch (e:Exception){
                Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        bpriceMic.setOnClickListener {
            pnameMic.isActivated = false
            mrpMic.isActivated = false
            spriceMic.isActivated = false
            bpriceMic.isActivated = true
            gstMic.isActivated = false
            hsncodeMic.isActivated = false
            unitMic.isActivated = false
            quantityMic.isActivated = false
            categoryMic.isActivated = false
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak ttp text")
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            }catch (e:Exception){
                Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        hsncodeMic.setOnClickListener {
            pnameMic.isActivated = false
            mrpMic.isActivated = false
            spriceMic.isActivated = false
            bpriceMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = true
            unitMic.isActivated = false
            quantityMic.isActivated = false
            categoryMic.isActivated = false
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak ttp text")
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            }catch (e:Exception){
                Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        gstMic.setOnClickListener {
            pnameMic.isActivated = false
            mrpMic.isActivated = false
            spriceMic.isActivated = false
            bpriceMic.isActivated = false
            gstMic.isActivated = true
            hsncodeMic.isActivated = false
            unitMic.isActivated = false
            quantityMic.isActivated = false
            categoryMic.isActivated = false
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak ttp text")
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            }catch (e:Exception){
                Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        unitMic.setOnClickListener {
            pnameMic.isActivated = false
            mrpMic.isActivated = false
            spriceMic.isActivated = false
            bpriceMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = false
            unitMic.isActivated = true
            quantityMic.isActivated = false
            categoryMic.isActivated = false
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak ttp text")
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            }catch (e:Exception){
                Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        quantityMic.setOnClickListener {
            pnameMic.isActivated = false
            mrpMic.isActivated = false
            spriceMic.isActivated = false
            bpriceMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = false
            unitMic.isActivated = false
            quantityMic.isActivated = true
            categoryMic.isActivated = false
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak ttp text")
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            }catch (e:Exception){
                Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
        categoryMic.setOnClickListener {
            pnameMic.isActivated = false
            mrpMic.isActivated = false
            spriceMic.isActivated = false
            bpriceMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = false
            unitMic.isActivated = false
            quantityMic.isActivated = false
            categoryMic.isActivated = true
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()
            }
        }

        image1.setOnClickListener {
            pickImages()
        }
        image2.setOnClickListener {
            pickImages()
        }
        image3.setOnClickListener {
            pickImages()
        }
        image4.setOnClickListener {
            pickImages()
        }
        image5.setOnClickListener {
            pickImages()
        }
        image6.setOnClickListener {
            pickImages()
        }
        uploadImg.setOnClickListener {
            val productNameText = productName.text.toString()
            val mrpText = MRP.text.toString()
            val sellPriceText = sellPrice.text.toString()
            val buyPriceText = buyPrice.text.toString()
            val hsnCodeText = hsnCode.text.toString()
            val gstText = gst.text.toString()
            val unitText = unit.text.toString()
            val quantityText = quantity.text.toString()
            val categoryText = categoryName.text.toString()


            req.put("product_name", productNameText)
            req.put("mrp", mrpText)
            req.put("selling_prize", sellPriceText)
            req.put("buying_prize", buyPriceText)
            req.put("gst_percentage", gstText)
            req.put("unit", unitText)
            req.put("hsn_code", hsnCodeText)
            req.put("quantity", quantityText)
            req.put("category", categoryText)
            req.put("seller", 1)
            req.put("standardized", 1)
            req.put("mapped_to_master", 1)

            if (req.has("product_image_1")) {
                uploadJson()
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Error!")
                builder.setMessage("At least one image should be selected.")
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
                val okButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                okButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            }
        }

        return view
    }

    private fun uploadJson() {
        val accessToken = sharedPreferences.getString("access_token", null)
        if (accessToken.isNullOrEmpty()) {
            Log.e("fetchData", "Access token is null or empty")
            return
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        val jsonObject = object : JsonObjectRequest(
            Request.Method.POST, URL, req,
            { response ->
                Toast.makeText(requireContext(), "Upload Successfull: ${response.getString("message")}", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(requireContext(), "Upload Unsuccessfull: ${error.message.toString()}", Toast.LENGTH_SHORT).show()
                Log.i("error fetching", error.message.toString())
            }) {
            // Override the getHeaders() method to add the access token to the request headers
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }
        requestQueue.add(jsonObject)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //speech to text
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                val res: ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        as ArrayList<String>
                when {
                    pnameMic.isActivated -> {
                        // Fill product name EditText with the limited character input
                        productName.setText(limitCharacterInput(res[0]))
                    }
                    mrpMic.isActivated -> {
                        // Fill mrp EditText with input as numbers
                        MRP.setText(filterNumbers(res[0]))
                    }
                    spriceMic.isActivated -> {
                        // Fill sell price EditText with input as numbers
                        sellPrice.setText(filterNumbers(res[0]))
                    }
                    bpriceMic.isActivated -> {
                        // Fill buy price EditText with input as numbers
                        buyPrice.setText(filterNumbers(res[0]))
                    }
                    hsncodeMic.isActivated -> {
                        // Fill hsn code EditText with the limited character input
                        hsnCode.setText(limitCharacterInput(res[0]))
                    }
                    gstMic.isActivated -> {
                        // Fill gst EditText with input as numbers ranging between 1 to 100
                        val filteredInput = filterNumbers(res[0])
                        if (filteredInput.isNotEmpty() && filteredInput.toInt() in 1..100) {
                            gst.setText(filteredInput)
                        } else {
                            // Handle invalid input (not a number or out of range)
                            Toast.makeText(requireContext(), "Invalid GST input", Toast.LENGTH_SHORT).show()
                        }
                    }
                    unitMic.isActivated -> {
                        // Fill unit EditText with input as numbers
                        unit.setText(filterNumbers(res[0]))
                    }
                    quantityMic.isActivated -> {
                        // Fill quantity EditText with input as numbers
                        quantity.setText(filterNumbers(res[0]))
                    }
                    categoryMic.isActivated -> {
                        // Fill category name EditText with the limited character input
                        categoryName.setText(limitCharacterInput(res[0]))
                    }
                }
            }
        }
        //image upload
        if(requestCode == PICK_IMAGES_REQUEST && resultCode == AppCompatActivity.RESULT_OK){
            val clipData = data?.clipData
            val imageList = mutableListOf<Uri>()

            if (clipData != null) {
                for (i in 0 until minOf(clipData.itemCount, Max_IMAGES)) {
                    val uri = clipData.getItemAt(i).uri
                    imageList.add(uri)

                    // Display thumbnails using Glide
                    when (i) {
                        0 -> Glide.with(this).load(uri).apply(RequestOptions().centerCrop()).into(image1)
                        1 -> Glide.with(this).load(uri).apply(RequestOptions().centerCrop()).into(image2)
                        2 -> Glide.with(this).load(uri).apply(RequestOptions().centerCrop()).into(image3)
                        3 -> Glide.with(this).load(uri).apply(RequestOptions().centerCrop()).into(image4)
                        4 -> Glide.with(this).load(uri).apply(RequestOptions().centerCrop()).into(image5)
                        5 -> Glide.with(this).load(uri).apply(RequestOptions().centerCrop()).into(image6)
                    }
                    if (clipData.itemCount > Max_IMAGES) {
                        Toast.makeText(requireContext(), "You can only select up to 6 images", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }
            for (i in 0 until minOf(imageList.size, Max_IMAGES)) {
                val imageUri = imageList[i]
                val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                val byteArray = inputStream?.readBytes()
                byteArray?.let {
                    val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
                    req.put("product_image_${i + 1}", base64Image)
                }
            }
        }
    }

    // This function will be used to limit the character input to 50 characters
    private fun limitCharacterInput(input: String): String {
        return if (input.length > 50) {
            input.substring(0, 50)
        } else {
            input
        }
    }

    // This function will be used to filter input as numbers
    private fun filterNumbers(input: String): String {
        return input.filter { it.isDigit() }
    }
    //pick images function
    private fun pickImages(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent,PICK_IMAGES_REQUEST)
    }
}