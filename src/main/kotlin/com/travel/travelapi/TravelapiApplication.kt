package com.travel.travelapi

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


@MapperScan("com.travel.travelapi.services")
@ComponentScan(basePackages = ["com.travel.travelapi.controllers","com.travel.travelapi.services", "com.travel.travelapi.models"])
@SpringBootApplication
class TravelapiApplication

fun main(args: Array<String>) {
	runApplication<TravelapiApplication>(*args)


}

