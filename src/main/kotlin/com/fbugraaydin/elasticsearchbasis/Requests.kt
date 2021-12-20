package com.fbugraaydin.elasticsearchbasis

import org.elasticsearch.search.sort.SortOrder
import java.math.BigDecimal

data class ProductFilterRequest(
    val title: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val sortOrder: SortOrder? = null
)