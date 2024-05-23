package com.ivar7284.catalogcraft.dataclasses

data class Catalogue(
    val product_name: String,
    val mrp: String,
    val gst_percentage: String,
    val asin: String,
    val upc: String,
    val product_image_1: String,
    val product_image_2: String,
    val product_image_3: String,
    val product_image_4: String,
    val product_image_5: String,
    val category: String,
)