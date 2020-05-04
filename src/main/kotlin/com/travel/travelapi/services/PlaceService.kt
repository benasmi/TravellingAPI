package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.models.Place

import org.apache.ibatis.annotations.*

import com.travel.travelapi.models.PlaceLocal
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface PlaceService {

    @Select("SELECT * from PLACE WHERE placeId FIND_IN_SET( placeId, #{ids}) <> 0")
    fun selectAll(@Param("ids") ids: String): Page<PlaceLocal>


    @Insert("INSERT INTO PLACE (description, averageTimeSpent, latitude, longitude, address, country, city, phoneNumber, website)" +
            "VALUES (#{description}, #{averageTimeSpent}, #{latitude}, #{longitude}, #{address}, #{country}, #{city}, #{phoneNumber}, #{website)}")
    @Options(useGeneratedKeys = true, keyProperty = "placeId", keyColumn = "placeId")
    fun insertPlace(@Param("p") p: PlaceLocal)
}