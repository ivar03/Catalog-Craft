package com.ivar7284.catalogcraft.RetorfitApi

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("catalogue/create/")
    suspend fun uploadData(
        @Part("product_name") product_name: RequestBody,
        @Part("upc") upc: RequestBody,
        @Part("seller_sku") seller_sku: RequestBody,
        @Part("selling_prize") selling_prize: RequestBody,
        @Part("hsn_code") hsn_code: RequestBody,
        @Part("gst_percentage") gst_percentage: RequestBody,
        @Part("description") description: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("additional_description") additional_description: RequestBody,
        @Part("selling_offer") product_tax_report: RequestBody,
        @Part("category") category: RequestBody?,
        @Part product_image_1: MultipartBody.Part?,
        @Part product_image_2: MultipartBody.Part?,
        @Part product_image_3: MultipartBody.Part?,
        @Part product_image_4: MultipartBody.Part?,
        @Part product_image_5: MultipartBody.Part?,
        @Part product_image_6: MultipartBody.Part
    ): Response<Any>
}