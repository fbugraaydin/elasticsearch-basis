package com.fbugraaydin.elasticsearchbasis

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
data class Product(
    @Id @GeneratedValue var id: Long? = null,
    var title: String,
    var image: String,
    var price: BigDecimal,
    var rate: Float,
    var seller: String
)
