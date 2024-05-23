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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ivar7284.catalogcraft.R
import com.ivar7284.catalogcraft.SearchActivity
import com.ivar7284.catalogcraft.utils.ApiService
import com.ivar7284.catalogcraft.utils.VolleyMultipartRequest.DataPart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.Locale

class AddCatalogItemFragment : Fragment() {

    private var clicked: Boolean = false

    private lateinit var searchBar: TextView

    private lateinit var quantity_layout: TextInputLayout
    private lateinit var gst_layout: TextInputLayout
    private lateinit var additionalDescription_layout: TextInputLayout
    private lateinit var hsnCode_layout: TextInputLayout
    private lateinit var yourPrice_layout: TextInputLayout
    private lateinit var upc_layout: TextInputLayout
    private lateinit var mrp_layout: TextInputLayout
    private lateinit var sellerSku_layout: TextInputLayout
    private lateinit var productName_layout: TextInputLayout
    private lateinit var description_layout: TextInputLayout
    private lateinit var sellingOffer_layout: TextInputLayout

    private lateinit var quantity: TextInputEditText
    private lateinit var gst: TextInputEditText
    private lateinit var additionalDescription: TextInputEditText
    private lateinit var hsnCode: TextInputEditText
    private lateinit var yourPrice: TextInputEditText
    private lateinit var sellerSku: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var mrp: TextInputEditText
    private lateinit var sellingOffer: TextInputEditText
    private lateinit var upc: TextInputEditText
    private lateinit var productName: TextInputEditText

    private lateinit var dropdown: TextView

    private lateinit var pnameMic: View
    private lateinit var sellerskuMic: View
    private lateinit var yourpriceMic: View
    private lateinit var hsncodeMic: View
    private lateinit var mrpMic: View
    private lateinit var gstMic: View
    private lateinit var additionaldescriptionMic: View
    private lateinit var quantityMic: View
    private lateinit var sellingofferMic: View
    private lateinit var descriptionMic: View
    private lateinit var upcMic: View

    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var image4: ImageView
    private lateinit var image5: ImageView
    private lateinit var image6: ImageView
    private lateinit var uploadImg: CircularProgressButton

    private lateinit var sharedPreferences: SharedPreferences
    private val URL = "http://panel.mait.ac.in:8012/catalogue/create/"

    private val PICK_IMAGES_REQUEST = 123
    private val Max_IMAGES = 6

    private val REQUEST_CODE_SPEECH_INPUT = 1

    private var selectedCategory: String? = null

    private val selectedImages = mutableListOf<Uri>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_catalog_item, container, false)

        //save category to variable
        arguments?.let {
            selectedCategory = it.getString("selected_category")
        }
        Log.i("selectedCategory", selectedCategory.toString())

        //top navigation
        searchBar = view.findViewById(R.id.search_catalog_et)
        searchBar.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        val formView = view.findViewById<LinearLayout>(R.id.form_ll)
        formView.setOnClickListener {
            val barcodeFragment = BarCodeFragment()
            val fragmentManager: FragmentManager? = fragmentManager
            fragmentManager?.beginTransaction()?.replace(R.id.homeFrame, barcodeFragment)?.commit()
        }

        //form stuff
        quantity_layout = view.findViewById(R.id.quantityInputLayout)
        gst_layout = view.findViewById(R.id.gstInputLayout)
        description_layout = view.findViewById(R.id.descriptionInputLayout)
        hsnCode_layout = view.findViewById(R.id.hsnCodeInputLayout)
        yourPrice_layout = view.findViewById(R.id.yourPriceInputLayout)
        sellerSku_layout = view.findViewById(R.id.sellerSkuInputLayout)
        upc_layout = view.findViewById(R.id.upcInputLayout)
        mrp_layout = view.findViewById(R.id.mrpInputLayout)
        sellingOffer_layout = view.findViewById(R.id.sellingOfferInputLayout)
        additionalDescription_layout = view.findViewById(R.id.additionalDescriptionInputLayout)
        productName_layout = view.findViewById(R.id.productNameInputLayout)

        quantity = view.findViewById(R.id.quantity)
        gst = view.findViewById(R.id.gst)
        yourPrice = view.findViewById(R.id.yourPrice)
        hsnCode = view.findViewById(R.id.hsnCode)
        sellerSku = view.findViewById(R.id.sellerSku)
        upc = view.findViewById(R.id.UPC)
        mrp = view.findViewById(R.id.mrp)
        description = view.findViewById(R.id.description)
        productName = view.findViewById(R.id.productName)
        additionalDescription = view.findViewById(R.id.additionalDescription)
        sellingOffer = view.findViewById(R.id.sellingOffer)

        pnameMic = view.findViewById(R.id.mic_pname)
        upcMic = view.findViewById(R.id.mic_upc)
        sellerskuMic = view.findViewById(R.id.mic_sellerSku)
        yourpriceMic = view.findViewById(R.id.mic_yourPrice)
        hsncodeMic = view.findViewById(R.id.mic_hsncode)
        gstMic = view.findViewById(R.id.mic_gst)
        mrpMic = view.findViewById(R.id.mic_mrp)
        descriptionMic = view.findViewById(R.id.mic_description)
        quantityMic = view.findViewById(R.id.mic_quantity)
        sellingofferMic = view.findViewById(R.id.mic_sellingOffer)
        additionaldescriptionMic = view.findViewById(R.id.mic_additionalDescription)
        quantityMic = view.findViewById(R.id.mic_quantity)
//        categoryMic = view.findViewById(R.id.mic_category)

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
                productName_layout.helperText = getString(R.string.enter_the_name_of_your_product)
            }else{
                productName_layout.helperText = null
            }
        }
        upc.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                upc_layout.helperText = getString(R.string.enter_the_upc_code_of_your_product)
            }else{
                upc_layout.helperText = null
            }
        }
        mrp.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                mrp_layout.helperText = getString(R.string.enter_the_mrp_of_your_product)
            }else{
                mrp_layout.helperText = null
            }
        }
        sellerSku.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                sellerSku_layout.helperText = getString(R.string.enter_your_sku_number)
            }else{
                sellerSku_layout.helperText = null
            }
        }
        yourPrice.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                yourPrice_layout.helperText =
                    getString(R.string.enter_the_price_at_which_you_want_to_sell_your_product)
            }else{
                yourPrice_layout.helperText = null
            }
        }
        hsnCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                hsnCode_layout.helperText = getString(R.string.enter_the_hsn_code_of_your_product)
            }else{
                hsnCode_layout.helperText = null
            }
        }
        description.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                description_layout.helperText =
                    getString(R.string.enter_a_suitable_description_of_your_product)
            }else{
                description_layout.helperText = null
            }
        }
        gst.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                gst_layout.helperText = getString(R.string.enter_the_gst_for_your_product)
            }else{
                gst_layout.helperText = null
            }
        }
        quantity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                quantity_layout.helperText = getString(R.string.enter_the_quantity_of_your_product)
            }else{
                quantity_layout.helperText = null
            }
        }
        additionalDescription.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                additionalDescription_layout.helperText =
                    getString(R.string.enter_any_other_information_you_want_to_enter_about_your_product)
            }else{
                additionalDescription_layout.helperText = null
            }
        }
        sellingOffer.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                sellingOffer_layout.helperText =
                    getString(R.string.enter_the_selling_offer_for_your_product)
            }else{
                sellingOffer_layout.helperText = null
            }
        }

        //setting up mics
        pnameMic.setOnClickListener {
            pnameMic.isActivated = true
            upcMic.isActivated = false
            sellerskuMic.isActivated = false
            yourpriceMic.isActivated = false
            gstMic.isActivated = false
            mrpMic.isActivated = false
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = false
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = false
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
        upcMic.setOnClickListener {
            pnameMic.isActivated = false
            upcMic.isActivated = true
            sellerskuMic.isActivated = false
            yourpriceMic.isActivated = false
            gstMic.isActivated = false
            mrpMic.isActivated = false
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = false
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = false
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
        sellerskuMic.setOnClickListener {
            pnameMic.isActivated = false
            upcMic.isActivated = false
            sellerskuMic.isActivated = true
            yourpriceMic.isActivated = false
            mrpMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = false
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = false
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
        yourpriceMic.setOnClickListener {
            pnameMic.isActivated = false
            upcMic.isActivated = false
            sellerskuMic.isActivated = false
            yourpriceMic.isActivated = true
            gstMic.isActivated = false
            mrpMic.isActivated = false
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = false
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = false
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
            upcMic.isActivated = false
            sellerskuMic.isActivated = false
            yourpriceMic.isActivated = false
            mrpMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = true
            descriptionMic.isActivated = false
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = false
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
            upcMic.isActivated = false
            sellerskuMic.isActivated = false
            yourpriceMic.isActivated = false
            gstMic.isActivated = true
            mrpMic.isActivated = false
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = false
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = false
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
        mrpMic.setOnClickListener {
            pnameMic.isActivated = false
            upcMic.isActivated = false
            sellerskuMic.isActivated = false
            yourpriceMic.isActivated = false
            gstMic.isActivated = false
            mrpMic.isActivated = true
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = false
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = false
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
        descriptionMic.setOnClickListener {
            pnameMic.isActivated = false
            upcMic.isActivated = false
            sellerskuMic.isActivated = false
            yourpriceMic.isActivated = false
            gstMic.isActivated = false
            mrpMic.isActivated = false
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = true
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = false
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
            upcMic.isActivated = false
            sellerskuMic.isActivated = false
            mrpMic.isActivated = false
            yourpriceMic.isActivated = false
            gstMic.isActivated = false
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = false
            quantityMic.isActivated = true
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = false
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
        additionaldescriptionMic.setOnClickListener {
            pnameMic.isActivated = false
            upcMic.isActivated = false
            sellerskuMic.isActivated = false
            yourpriceMic.isActivated = false
            gstMic.isActivated = false
            mrpMic.isActivated = false
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = false
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = true
            sellingofferMic.isActivated = false
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
        sellingofferMic.setOnClickListener {
            pnameMic.isActivated = false
            upcMic.isActivated = false
            sellerskuMic.isActivated = false
            yourpriceMic.isActivated = false
            gstMic.isActivated = false
            mrpMic.isActivated = false
            hsncodeMic.isActivated = false
            descriptionMic.isActivated = false
            quantityMic.isActivated = false
            additionaldescriptionMic.isActivated = false
            sellingofferMic.isActivated = true
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

        //additional details
        val additionalFieldsContainer: LinearLayout = view.findViewById(R.id.additional_fields_container)
        dropdown = view.findViewById(R.id.dropdown_menu)
        dropdown.setOnClickListener {
            if(!clicked) {
                additionalFieldsContainer.visibility = View.VISIBLE
                clicked = true
            }else {
                additionalFieldsContainer.visibility = View.GONE
                clicked = false
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
            if (productName.text.toString().isEmpty() || description.text.toString().isEmpty() || upc.text.toString().isEmpty() || sellerSku.text.toString().isEmpty() || mrp.text.toString().isEmpty() || yourPrice.text.toString().isEmpty() || quantity.text.toString().isEmpty() || gst.text.toString().isEmpty() || hsnCode.text.toString().isEmpty()){
                showAlertDialog()
            }else {
                uploadImg.startAnimation()
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

                //setting up retrofit
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://panel.mait.ac.in:8012/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()

                val apiService = retrofit.create(ApiService::class.java)
                Log.i("requestData", selectedImages.toString())

                val productNameRequestBody = createPartFromString(productName.text.toString())
                val mrpRequestBody = createPartFromString(mrp.text.toString())
                val descriptionRequestBody = createPartFromString(description.text.toString())
                val sellingPriceRequestBody = createPartFromString(yourPrice.text.toString())
                val upcRequestBody = createPartFromString(upc.text.toString())
                val hsnCodeRequestBody = createPartFromString(hsnCode.text.toString())
                val gstRequestBody = createPartFromString(gst.text.toString())
                val sellerSkuRequestBody = createPartFromString(sellerSku.text.toString())
                val quantityRequestBody = createPartFromString(quantity.text.toString())
                val additionalDescriptionRequestBody = createPartFromString(additionalDescription.text.toString())
                val categoryRequestBody = createPartFromString(selectedCategory.toString())
                val sellingOfferRequestBody = createPartFromString(sellingOffer.text.toString())
                val asinRequestBody = createPartFromString("123")

                val image1Part = imageToRequestBody("product_image_1",image1, "image1.jpg")
                val image2Part = imageToRequestBody("product_image_2",image2, "image2.jpg")
                val image3Part = imageToRequestBody("product_image_3",image3, "image3.jpg")
                val image4Part = imageToRequestBody("product_image_4",image4, "image4.jpg")
                val image5Part = imageToRequestBody("product_image_5",image5, "image5.jpg")
                val image6Part = imageToRequestBody("product_image_6",image6, "image6.jpg")

                // Make the API call using CoroutineScope
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = apiService.uploadData(
                            productNameRequestBody,
                            mrpRequestBody,
                            asinRequestBody,
                            descriptionRequestBody,
                            sellingPriceRequestBody,
                            upcRequestBody,
                            hsnCodeRequestBody,
                            gstRequestBody,
                            sellerSkuRequestBody,
                            quantityRequestBody,
                            additionalDescriptionRequestBody,
                            categoryRequestBody,
                            sellingOfferRequestBody,
                            image1Part,
                            image2Part,
                            image3Part,
                            image4Part,
                            image5Part,
                            image6Part
                        )

                        withContext(Dispatchers.Main) {
                            if (response != null) {
                                //code 400-bad req 500-internal server error
                                when (response.code()) {
                                    201 -> {
                                        Log.e("uploading success", "Data uploaded successfully")
                                        uploadImg.revertAnimation()
                                        Toast.makeText(requireContext(), "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        val errorBody = response.errorBody()?.string()
                                        uploadImg.revertAnimation()
                                        Log.e("uploading error", "Error Code: ${response.code()}, Message: ${response.message()}, Error Body: $errorBody")

                                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            } else {
                                uploadImg.revertAnimation()
                                Toast.makeText(requireContext(), "Error: Response is null", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            uploadImg.revertAnimation()
                            Log.e("uploading error", "Error: ${e.message.toString()}")
                            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        //data from camera activity
        arguments?.getString("catalog_data")?.let { data ->
            populateForm(data)
        }
        //data from bar code
        arguments?.getString("bar_code")?.let { data ->
            fillForm(data)
        }

        return view
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.all_field_are_required))
            .setMessage(getString(R.string.you_have_missed_one_or_more_field_please_fill_all_the_fields_as_they_are_required))
            .setPositiveButton(getString(R.string.ok)) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.getString("catalogue")?.let {data ->
            val jsonObject = JSONObject(data)
            productName.setText(jsonObject.getString("product_name"))
            mrp.setText(jsonObject.getString("mrp"))
            gst.setText(jsonObject.getString("gst_percentage"))
            upc.setText(jsonObject.getString("upc"))
            val uri_1 = jsonObject.getString("product_image_1")
            val uri_2 = jsonObject.getString("product_image_2")
            //glide first two images
            Glide.with(this)
                .load("http://panel.mait.ac.in:8012$uri_1")
                .apply(RequestOptions().override(image1.width, image1.height))
                .into(image1)
            Glide.with(this)
                .load("http://panel.mait.ac.in:8012$uri_2")
                .apply(RequestOptions().override(image2.width, image2.height))
                .into(image2)
        }
    }

    private fun fillForm(data: String) {
        val jsonObject = JSONObject(data)
        productName.setText(jsonObject.getString("name"))
        description.setText(jsonObject.getString("description"))
        yourPrice.setText(jsonObject.getString("price"))
        quantity.setText(jsonObject.getString("Qty"))
    }

    private fun populateForm(data: String) {
        try {
            val jsonArray = JSONArray(data)
            if (jsonArray.length() > 0) {
                val jsonObject = jsonArray.getJSONObject(0)
                productName.setText(jsonObject.getString("product_name"))
                mrp.setText(jsonObject.getString("mrp"))
                gst.setText(jsonObject.getString("gst_percentage"))
                upc.setText(jsonObject.getString("upc"))

            }

        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e("JSONException", "Error parsing JSON", e)
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
                    upcMic.isActivated -> {
                        // Fill mrp EditText with input as numbers
                        upc.setText(filterNumbers(res[0]))
                    }
                    mrpMic.isActivated -> {
                        // Fill mrp EditText with input as numbers
                        mrp.setText(filterNumbers(res[0]))
                    }
                    sellerskuMic.isActivated -> {
                        // Fill sell price EditText with input as numbers
                        sellerSku.setText(filterNumbers(res[0]))
                    }
                    yourpriceMic.isActivated -> {
                        // Fill buy price EditText with input as numbers
                        yourPrice.setText(filterNumbers(res[0]))
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
                    descriptionMic.isActivated -> {
                        // Fill unit EditText with input as numbers
                        description.setText(limitCharacterInput(res[0]))
                    }
                    additionaldescriptionMic.isActivated -> {
                        // Fill unit EditText with input as numbers
                        additionalDescription.setText(limitCharacterInput(res[0]))
                    }
                    quantityMic.isActivated -> {
                        // Fill quantity EditText with input as numbers
                        quantity.setText(filterNumbers(res[0]))
                    }
                    sellingofferMic.isActivated -> {
                        val filteredInput = filterNumbers(res[0])
                        if (filteredInput.isNotEmpty() && filteredInput.toInt() in 1..100) {
                            sellingOffer.setText(filteredInput)
                        } else {
                            // Handle invalid input (not a number or out of range)
                            Toast.makeText(requireContext(), "Invalid Selling Offer input", Toast.LENGTH_SHORT).show()
                        }
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
                        selectedImages.add(imageUri)
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
                    selectedImages.add(imageUri)
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

    fun createPartFromString(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }

    private fun imageToRequestBody(jangoKey: String, imageView: ImageView, imageName: String): MultipartBody.Part {
        val drawable = (imageView.drawable as? BitmapDrawable)?.bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        drawable?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        // Use the imageBytes directly to create RequestBody
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageBytes)
        Log.d("ImageRequestBody", "Converted image $imageName to RequestBody")
        //Pass the RequestBody to create MultipartBody.Part
        return MultipartBody.Part.createFormData(jangoKey, imageName, requestBody)
    }}