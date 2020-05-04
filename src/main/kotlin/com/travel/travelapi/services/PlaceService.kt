package com.travel.travelapi.services

import com.github.pagehelper.Page

import org.apache.ibatis.annotations.*

import com.travel.travelapi.models.PlaceLocal
import org.apache.ibatis.annotations.Select

import org.springframework.stereotype.Repository

@Repository
interface PlaceService {


    @Select("<script>" +
            "SELECT * FROM PLACE WHERE placeId IN " +
            "<foreach item='item' index='index' collection='ids'" +
            " open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    fun selectAll(@Param("ids") ids: List<String>): Page<PlaceLocal>


    @Insert("INSERT INTO PLACE (description, averageTimeSpent, latitude, longitude, address, country, city, phoneNumber, website)" +
            "VALUES (#{description}, #{averageTimeSpent}, #{latitude}, #{longitude}, #{address}, #{country}, #{city}, #{phoneNumber}, #{website)}")
    @Options(useGeneratedKeys = true, keyProperty = "placeId", keyColumn = "placeId")
    fun insertPlace(@Param("p") p: PlaceLocal)
}