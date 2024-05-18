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
        @Part("mrp") mrp: RequestBody,
        @Part("seller") seller: Int,
        @Part("selling_prize") selling_prize: RequestBody,
        @Part("buying_prize") buying_prize: RequestBody,
        @Part("hsn_code") hsn_code: RequestBody,
        @Part("gst_percentage") gst_percentage: RequestBody,
        @Part("unit") unit: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("standardized") standardized: Int,
        @Part("category") category: RequestBody,
        @Part("mapped_to_master") mapped_to_master: Int,
        @Part product_image_1: MultipartBody.Part?,
        @Part product_image_2: MultipartBody.Part?,
        @Part product_image_3: MultipartBody.Part?,
        @Part product_image_4: MultipartBody.Part?,
        @Part product_image_5: MultipartBody.Part?
    ): Response<Any>
}