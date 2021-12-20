package com.fbugraaydin.elasticsearchbasis

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticSearchConfiguration(
    @Value("\${elasticsearch.hostname}")
    private val hostName: String,
    @Value("\${elasticsearch.port}")
    private val port: Int,
    @Value("\${elasticsearch.scheme}")
    private val scheme: String
) {

    @Bean
    fun elasticSearchClient(): RestHighLevelClient {
        return RestHighLevelClient(RestClient.builder(HttpHost(hostName, port, scheme)))
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper().registerKotlinModule()
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)

        return objectMapper
    }
}
