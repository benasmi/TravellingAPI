package com.travel.travelapi.services

import com.travel.travelapi.models.Place
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface PlaceService {

    @Select("SELECT * from PLACE")
    fun selectAll(): List<Place>

    @Insert("INSERT INTO PLACE (description, averageTimeSpent, latitude, longitude, address, country, city, phoneNumber, website)" +
            "VALUES (#{description}, #{averageTimeSpent}, #{latitude}, #{longitude}, #{address}, #{country}, #{city}, #{phoneNumber}, #{website)}")
    @Options(useGeneratedKeys = true, keyProperty = "placeId", keyColumn = "placeId")
    fun insertPlace(@Param("p") p: Place)



}