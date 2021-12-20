package com.fbugraaydin.elasticsearchbasis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.SortBuilders
import org.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import kotlin.reflect.KFunction2

@Service
@Transactional
class ProductService(
    private val productSearchService: ProductSearchService,
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper
) {

    fun save(product: ProductDto) {
        val savedProduct = productRepository.save(productMapper.convertToEntity(product))
        val productDto = productMapper.convertToProductFilterDto(product)
        productDto.id = savedProduct.id
        productSearchService.indexOrUpdate(productDto)
    }

    fun delete(id: Long) {
        isProductExist(id)
        productRepository.deleteById(id)
        productSearchService.delete(id)
    }

    fun update(id: Long, productDto: ProductDto) {
        val product = isProductExist(id)
        product.image = productDto.image
        product.price = productDto.price
        product.rate = productDto.rate
        product.seller = productDto.seller
        product.title = productDto.title
        productRepository.save(product)

        val productFilterDto = productMapper.convertToProductFilterDto(productDto)
        productFilterDto.id = id
        productSearchService.indexOrUpdate(productFilterDto)
    }

    fun get(id: Long): ProductDto {
        val event = isProductExist(id)
        return productMapper.convertToDto(event)
    }

    fun getAll(): List<ProductDto> {
        return productRepository.findAll().map {
            productMapper.convertToDto(it) }
    }

    private fun isProductExist(id: Long): Product = productRepository.findById(id).orElseThrow {
        throw ResponseStatusException(HttpStatus.NOT_FOUND, Exception_Product_Not_Found)
    }

}

@Service
class ProductSearchService(
    private val client: RestHighLevelClient,
    private val objectMapper: ObjectMapper,
    private val productFilterQueryBuilder: ProductFilterQueryBuilder
) {

    fun search(query: String): List<ProductFilterDto> {
        val searchQueryBuilder = SearchSourceBuilder()

        val searchRequest = SearchRequest(indexName)
        searchRequest.source(searchQueryBuilder)

        val response = client.search(searchRequest, RequestOptions.DEFAULT)
        return response.hits.map {
            val result:ProductFilterDto = objectMapper.readValue(it.sourceAsString)
            result
        }
    }

    fun filter(request: ProductFilterRequest, page: Int, limit: Int): List<ProductFilterDto> {
        val searchSourceBuilder = SearchSourceBuilder()
        val query = searchSourceBuilder.query(productFilterQueryBuilder.build(request))

        if (request.sortOrder != null) {
            query.sort(
                SortBuilders.fieldSort("price")
                    .order(request.sortOrder)
            )
        }
        query.from(page * limit)
        query.size(limit)

        val searchRequest = SearchRequest(indexName)
        searchRequest.source(searchSourceBuilder)

        val response = client.search(searchRequest, RequestOptions.DEFAULT)
        return response.hits.map {
            val result:ProductFilterDto = objectMapper.readValue(it.sourceAsString)
            result
        }
    }

    fun indexOrUpdate(productFilter: ProductFilterDto) {
        val body = objectMapper.convertValue(objectMapper.writeValueAsString(productFilter), JSONObject::class.java)
        val suggester = JSONObject()
        suggester.put("input", arrayOf(productFilter.title))
        body.put("suggest", suggester)

        val request = IndexRequest(indexName)
        request.id(productFilter.id!!.toString())
        request.source(body.toString(), XContentType.JSON)

        client.index(request, RequestOptions.DEFAULT)
    }

    fun delete(id: Long) {
        val request = DeleteRequest(indexName)
        request.id(id.toString())
        client.delete(request, RequestOptions.DEFAULT)
    }

}


@Component
class ProductFilterQueryBuilder {

    private val filters: List<KFunction2<BoolQueryBuilder, ProductFilterRequest, Unit>> = listOf(
        ::filterByTitle,
        ::filterByMinPrice,
        ::filterByMaxPrice
    )

    fun build(request: ProductFilterRequest): BoolQueryBuilder {
        val boolQuery = QueryBuilders.boolQuery()
        filters.forEach { it(boolQuery, request) }
        return boolQuery
    }

    private fun filterByTitle(boolQuery: BoolQueryBuilder, request: ProductFilterRequest) {
        if (request.title != null) {
            val titleQuery = QueryBuilders.matchQuery("title", request.title)
            titleQuery.fuzziness("AUTO")
            titleQuery.fuzzyTranspositions(true)
            boolQuery.must(titleQuery)
        }
    }

    private fun filterByMinPrice(boolQuery: BoolQueryBuilder, request: ProductFilterRequest) {
        if (request.minPrice != null) {
            boolQuery.must(QueryBuilders.rangeQuery("price").gte(request.minPrice))
        }
    }

    private fun filterByMaxPrice(boolQuery: BoolQueryBuilder, request: ProductFilterRequest) {
        if (request.maxPrice != null) {
            boolQuery.must(QueryBuilders.rangeQuery("price").lte(request.maxPrice))
        }
    }
}
