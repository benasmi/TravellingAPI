package com.travel.travelapi.services

import com.travel.travelapi.models.Photo
import com.travel.travelapi.models.PlaceApi
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface ApiPlace {

    fun selectApiPlaceById(@Param("id") id: String): PlaceApi?
}