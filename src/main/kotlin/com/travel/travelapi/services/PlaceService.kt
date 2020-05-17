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
    fun search(@Param("ids") ids: List<String>): Page<PlaceLocal>

    @Select("SELECT * FROM PLACE")
    fun selectAll(): Page<PlaceLocal>

    @Select("SELECT * FROM PLACE WHERE placeId=#{id}")
    fun selectById(@Param("id") id: Int): PlaceLocal

    @Update("UPDATE PLACE SET name=#{p.name}, description=#{p.description}," +
            " city=#{p.city}, country=#{p.country}, latitude=#{p.latitude}, longitude=#{p.longitude}, address=#{p.address}," +
            " phoneNumber=#{p.phoneNumber}, website=#{p.website} WHERE placeId=#{p.placeId}")
    fun updatePlace(@Param("p") p: PlaceLocal)

    @Insert("INSERT INTO PLACE (description, averageTimeSpent, latitude, longitude, address, country, city, phoneNumber, website)" +
            "VALUES (#{p.description}, #{p.averageTimeSpent}, #{p.latitude}, #{p.longitude}, #{p.address}, #{p.country}, #{p.city}, #{p.phoneNumber}, #{p.website})")
    @Options(useGeneratedKeys = true, keyProperty = "placeId", keyColumn = "placeId")
    fun insertPlace(@Param("p") p: PlaceLocal)
}