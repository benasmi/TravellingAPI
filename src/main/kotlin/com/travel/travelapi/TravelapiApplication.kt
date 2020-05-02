package com.travel.travelapi

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@MapperScan("com.travel.travelapi.services")
@ComponentScan(basePackages = ["com.travel.travelapi.controllers","com.travel.travelapi.facebook.places.api","com.travel.travelapi.services", "com.travel.travelapi.models", "com.travel.travelapi.config", "com.travel.travelapi.exceptions"])
@SpringBootApplication
class TravelapiApplication

fun main(args: Array<String>) {
	runApplication<TravelapiApplication>(*args)


}

