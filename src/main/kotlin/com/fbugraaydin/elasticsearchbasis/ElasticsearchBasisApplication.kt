package com.fbugraaydin.elasticsearchbasis

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springdoc.core.GroupedOpenApi
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.math.BigDecimal

@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
class ElasticsearchBasisApplication(
    private val client: RestHighLevelClient,
    private val productService: ProductService
) {
    private val logger: Logger =
        LoggerFactory.getLogger(ElasticsearchBasisApplication::class.java)

    @Bean
    fun initializer() = ApplicationRunner {
        logger.info("Initializing environment...")
        prepareESEnvironment()

        val products = getProducts()
        products.forEach { productService.save(it) }
        logger.info("Initialized environment!")
    }

    private fun getProducts(): List<ProductDto> {
        val product1 = ProductDto(
            title = "Iphone 12 Mini 64 GB",
            image = "https://productimages.hepsiburada.net/s/76/550/110000018213526.jpg/format:webp",
            price = BigDecimal.valueOf(11340),
            rate = 4.2f,
            seller = "ABC Tech"
        )

        val product2 = ProductDto(
            title = "Iphone 12 Mini 128 GB",
            image = "https://productimages.hepsiburada.net/s/76/550/110000018213526.jpg/format:webp",
            price = BigDecimal.valueOf(13200),
            rate = 4.8f,
            seller = "KO Tech"
        )

        val product3 = ProductDto(
            title = "Iphone 13 Pro, 128 GB",
            image = "https://productimages.hepsiburada.net/s/119/550/110000068435138.jpg/format:webp",
            price = BigDecimal.valueOf(20950),
            rate = 4.5f,
            seller = "PT Tech"
        )

        return listOf(product1, product2, product3)
    }

    private fun prepareESEnvironment() {

        val deleteRequest = DeleteIndexRequest(indexName)
        try {
            client.indices().delete(deleteRequest, RequestOptions.DEFAULT)
            logger.info("Index deleted: $indexName")
        } catch (e: Exception) {
            logger.info("There is no index: $indexName")
        }

        val request = CreateIndexRequest(indexName)

        request.settings(
            "{\n" +
                     "\"number_of_shards\" : \"3\",\n" +
                    "        \"analysis\" : {\n" +
                    "          \"analyzer\" : {\n" +
                    "            \"folding_analyzer\" : {\n" +
                    "              \"filter\" : [\n" +
                    "                \"lowercase\",\n" +
                    "                \"asciifolding\"\n" +
                    "              ],\n" +
                    "              \"type\" : \"custom\",\n" +
                    "              \"tokenizer\" : \"standard\"\n" +
                    "            }\n" +
                    "          }\n" +
                    "        },\n" +
                    "        \"number_of_replicas\" : \"2\""
                    +"  }", XContentType.JSON
        )

        request.mapping(
            "{\n" +
                    "    \"properties\": {\n" +
                    "      \"id\": {\n" +
                    "        \"type\" : \"long\"\n" +
                    "      },\n" +
                    "      \"title\": {\n" +
                    "        \"type\" : \"text\",\n" +
                    "        \"analyzer\": \"folding_analyzer\"\n" +
                    "      },\n" +
                    "      \"image\": {\n" +
                    "        \"type\": \"text\"\n" +
                    "      },\n" +
                    "      \"price\": {\n" +
                    "        \"type\": \"double\"\n" +
                    "      },\n" +
                    "      \"suggest\": {\n" +
                    "        \"type\": \"completion\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }", XContentType.JSON
        )

        client.indices().create(request, RequestOptions.DEFAULT)

    }

    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("product")
            .pathsToMatch("/products/**")
            .build()
    }

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Devs Meet ElasticSearch Basis Meetup")
                    .description("Devs Meet ElasticSearch Basis Meetup Platform APIs")
                    .version("v0.0.1")
            )
    }
}

fun main(args: Array<String>) {
    runApplication<ElasticsearchBasisApplication>(*args)
}
