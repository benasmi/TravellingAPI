package com.travel.travelapi.config

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class WebClientService(){

    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()

    @Bean
    fun facebookApiKey(): String = "220252795946754|HW8VIYvCopvi8r4VOp9gZJ8isdQ"

}