package com.fbugraaydin.elasticsearchbasis

import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ProductMapper {

    fun convertToDto(product: Product): ProductDto

    fun convertToEntity(productDto: ProductDto): Product

    fun convertToProductFilterDto(productDto: ProductDto): ProductFilterDto
}
