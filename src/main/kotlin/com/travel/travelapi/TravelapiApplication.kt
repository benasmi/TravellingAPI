package com.travel.travelapi

import com.travel.travelapi.config.FileStorageProperties
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@MapperScan("com.travel.travelapi.services")
@ComponentScan(basePackages = ["com.travel.travelapi.controllers","com.travel.travelapi.sphinx","com.travel.travelapi.facebook.places.api","com.travel.travelapi.services", "com.travel.travelapi.models", "com.travel.travelapi.config", "com.travel.travelapi.exceptions"])
@EnableConfigurationProperties(
	FileStorageProperties::class
)
@SpringBootApplication
class TravelapiApplication

fun main(args: Array<String>) {
	runApplication<TravelapiApplication>(*args)

}

