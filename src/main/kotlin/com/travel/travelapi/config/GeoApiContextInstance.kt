package com.travel.travelapi.config

import com.google.maps.GeoApiContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class GeoApiContextInstance(){
    fun getGeoApiContext() = GeoApiContext.Builder().apiKey("AIzaSyCSoLVEWMKoU6V8TM1AVAzaHhXaDr6l2Lc").build()
}