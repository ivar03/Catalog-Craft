package com.ivar7284.catalogcraft.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ivar7284.catalogcraft.R
import com.ivar7284.catalogcraft.RetorfitApi.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.util.Locale

class AddCatalogItemFragment : Fragment() {

    private val categoryList = arrayListOf("Category1", "Category2", "Category3")

    private lateinit var quantity_layout: TextInputLayout
    private lateinit var gst_layout: TextInputLayout
    private lateinit var unit_layout: TextInputLayout
    private lateinit var hsnCode_layout: TextInputLayout
    private lateinit var buyPrice_layout: TextInputLayout
    private lateinit var sellPrice_layout: TextInputLayout
    private lateinit var MRP_layout: TextInputLayout
    private lateinit var productName_layout: TextInputLayout
    private lateinit var categoryName_layout: TextInputLayout

    private lateinit var quantity: TextInputEditText
    private lateinit var gst: TextInputEditText
    private lateinit var unit: TextInputEditText
    private lateinit var hsnCode: TextInputEditText
    private lateinit var buyPrice: TextInputEditText
    private lateinit var sellPrice: TextInputEditText
    private lateinit var MRP: TextInputEditText
    private lateinit var productName: TextInputEditText
    private lateinit var categoryName: TextInputEditText

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
    private lateinit var uploadImg: CircularProgressButton

    private lateinit var apiService: ApiService

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
        quantity_layout = view.findViewById(R.id.quantityInputLayout)
        gst_layout = view.findViewById(R.id.gstInputLayout)
        unit_layout = view.findViewById(R.id.unitInputLayout)
        hsnCode_layout = view.findViewById(R.id.hsnCodeInputLayout)
        buyPrice_layout = view.findViewById(R.id.buyPriceInputLayout)
        sellPrice_layout = view.findViewById(R.id.sellPriceInputLayout)
        MRP_layout = view.findViewById(R.id.mrpInputLayout)
        productName_layout = view.findViewById(R.id.productNameInputLayout)
        categoryName_layout = view.findViewById(R.id.categoryInputLayout)

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

        //displaying info regarding fields in form
        productName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                productName_layout.helperText = "info"
            }else{
                productName_layout.helperText = null
            }
        }
        //suggestions regarding the categories we already have

        categoryName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                addCategoryIfNotExists()
                true
            } else {
                false
            }
        }


        categoryName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                categoryName_layout.helperText = categoryList.joinToString(", ")
                addCategoryIfNotExists()
            } else {
                categoryName_layout.helperText = null
            }
        }


        MRP.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                MRP_layout.helperText = "info"
            }else{
                MRP_layout.helperText = null
            }
        }
        sellPrice.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                sellPrice_layout.helperText = "info"
            }else{
                sellPrice_layout.helperText = null
            }
        }
        buyPrice.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                buyPrice_layout.helperText = "info"
            }else{
                buyPrice_layout.helperText = null
            }
        }
        hsnCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                hsnCode_layout.helperText = "info"
            }else{
                hsnCode_layout.helperText = null
            }
        }
        unit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                unit_layout.helperText = "info"
            }else{
                unit_layout.helperText = null
            }
        }
        gst.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                gst_layout.helperText = "info"
            }else{
                gst_layout.helperText = null
            }
        }
        quantity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                quantity_layout.helperText = "info"
            }else{
                quantity_layout.helperText = null
            }
        }

        //setting up mics
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

        //image picker
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
            uploadImg.startAnimation()
            uploadData()
        }

        //request
        val accessToken = sharedPreferences.getString("access_token", null)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .method(original.method, original.body)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://panel.mait.ac.in:8012/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)
        return view
    }

    private fun addCategoryIfNotExists() {
        val inputText = categoryName.text.toString().trim()
        if (inputText.isNotEmpty() && !categoryList.contains(inputText)) {
            categoryList.add(inputText)
            categoryName_layout.helperText = "Category added: $inputText\nSelect from: \n${categoryList.joinToString(", ")}\n"
        } else {
            categoryName_layout.helperText = "Select from: \n${categoryList.joinToString(", ")}\n"
        }
        categoryName.text = null
    }

    private fun uploadData() {
        val productNameText = productName.text.toString()
        val mrpText = MRP.text.toString()
        val sellPriceText = sellPrice.text.toString()
        val buyPriceText = buyPrice.text.toString()
        val hsnCodeText = hsnCode.text.toString()
        val gstText = gst.text.toString()
        val unitText = unit.text.toString()
        val quantityText = quantity.text.toString()
        val categoryText = categoryName.text.toString()

        val productNameRequestBody = createPartFromString(productNameText)
        val mrpRequestBody = createPartFromString(mrpText)
        val sellPriceRequestBody = createPartFromString(sellPriceText)
        val buyPriceRequestBody = createPartFromString(buyPriceText)
        val hsnCodeRequestBody = createPartFromString(hsnCodeText)
        val gstRequestBody = createPartFromString(gstText)
        val unitRequestBody = createPartFromString(unitText)
        val quantityRequestBody = createPartFromString(quantityText)
        val categoryNameRequestBody = createPartFromString(categoryText)

        val image1Part = imageToRequestBody("product_image_1", image1, "image1.jpg")
        val image2Part = imageToRequestBody("product_image_2", image2, "image2.jpg")
        val image3Part = imageToRequestBody("product_image_3", image3, "image3.jpg")
        val image4Part = imageToRequestBody("product_image_4", image4, "image4.jpg")
        val image5Part = imageToRequestBody("product_image_5", image5, "image5.jpg")
        val image6Part = imageToRequestBody("product_image_6", image6, "image6.jpg")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.uploadData(
                    productNameRequestBody,
                    mrpRequestBody,
                    3,
                    sellPriceRequestBody,
                    buyPriceRequestBody,
                    hsnCodeRequestBody,
                    gstRequestBody,
                    unitRequestBody,
                    quantityRequestBody,
                    1,
                    categoryNameRequestBody,
                    1,
                    image1Part,
                    image2Part,
                    image3Part,
                    image4Part,
                    image5Part
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        uploadImg.revertAnimation()
                        Toast.makeText(context, "Upload failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
        //image request
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            data?.let {
                if (it.clipData != null) {
                    val count = it.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = it.clipData!!.getItemAt(i).uri
                        when (i) {
                            0 -> Glide.with(this)
                                .load(imageUri)
                                .apply(RequestOptions().override(image1.width, image1.height))
                                .into(image1)
                            1 -> Glide.with(this)
                                .load(imageUri)
                                .apply(RequestOptions().override(image2.width, image2.height))
                                .into(image2)
                            2 -> Glide.with(this)
                                .load(imageUri)
                                .apply(RequestOptions().override(image3.width, image3.height))
                                .into(image3)
                            3 -> Glide.with(this)
                                .load(imageUri)
                                .apply(RequestOptions().override(image4.width, image4.height))
                                .into(image4)
                            4 -> Glide.with(this)
                                .load(imageUri)
                                .apply(RequestOptions().override(image5.width, image5.height))
                                .into(image5)
                            5 -> Glide.with(this)
                                .load(imageUri)
                                .apply(RequestOptions().override(image6.width, image6.height))
                                .into(image6)
                        }
                    }
                } else if (it.data != null) {
                    val imageUri = it.data!!
                    Glide.with(this).load(imageUri).into(image1)
                }
            }
        }
    }

    private fun limitCharacterInput(input: String): String {
        return if (input.length > 50) {
            input.substring(0, 50)
        } else {
            input
        }
    }

    private fun filterNumbers(input: String): String {
        return input.filter { it.isDigit() }
    }

    private fun pickImages(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent,PICK_IMAGES_REQUEST)
    }
    private fun createPartFromString(text: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), text)
    }

    private fun imageToRequestBody(jangoKey: String, imageView: ImageView, imageName: String): MultipartBody.Part {
        val drawable = (imageView.drawable as? BitmapDrawable)?.bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        drawable?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageBytes)
        return MultipartBody.Part.createFormData(jangoKey, imageName, requestBody)
    }
}