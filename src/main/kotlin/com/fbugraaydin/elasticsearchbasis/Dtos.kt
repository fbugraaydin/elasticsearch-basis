package com.fbugraaydin.elasticsearchbasis

import java.math.BigDecimal

data class ProductFilterDto(
    var id: Long? = null,
    val title: String,
    val image: String,
    val price: BigDecimal
)

data class ProductDto(
    val id: Long? = null,
    val title: String,
    val image: String,
    val price: BigDecimal,
    val rate: Float,
    val seller: String
)