package com.fbugraaydin.elasticsearchbasis

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val productSearchService: ProductSearchService
) {

    @GetMapping("/search")
    fun search(@RequestParam query: String): List<ProductFilterDto> = productSearchService.search(query)

    @PostMapping("/filter")
    fun filter(@RequestBody request: ProductFilterRequest, @RequestParam page: Int, @RequestParam limit: Int) =
        productSearchService.filter(request, page, limit)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun saveProduct(@RequestBody product: ProductDto) = productService.save(product);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun deleteProduct(@PathVariable id: Long) = productService.delete(id)

    @PutMapping("/{id}")
    fun updateMapping(@PathVariable id: Long, @RequestBody product: ProductDto) = productService.update(id, product)

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ProductDto = productService.get(id)

    @GetMapping
    fun getAllProducts(): List<ProductDto> = productService.getAll();

}
