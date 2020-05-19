package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.models.Parking
import com.travel.travelapi.models.PlaceLocal
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface ParkingService {


    @Select("<script>" +
            "SELECT * FROM PARKING WHERE parkingId IN " +
            "<foreach item='item' index='index' collection='ids'" +
            " open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    fun search(@Param("ids") ids: List<String>): List<Parking>

    @Select("SELECT * FROM PARKING WHERE levenshtein(#{p.address}, address) BETWEEN 0 AND 10")
    fun getAllParkingInfo(@Param("p") p: Parking): List<Parking>

    @Insert("INSERT INTO PARKING (latitude, longitude, address, city, country) VALUES (#{p.latitude},#{p.longitude}, #{p.address}, #{p.city}, #{p.country})")
    @Options(useGeneratedKeys = true, keyColumn = "parkingId", keyProperty = "parkingId")
    fun insertParking(@Param("p") p: Parking)

    @Delete("DELETE FROM PARKING WHERE parkingId=#{p.parkingId}")
    fun deleteParking(@Param("p") p: Parking)

    @Update("UPDATE PARKING SET latitude=#{p.latitude}, longitude=#{p.longitude}, address=#{p.address} WHERE parkingId=#{p.parkingId}")
    fun updateParking(@Param("p") p: Parking)

    @Select("SELECT *, ( 6371 * acos( cos( radians(#{latitude}) ) * cos( radians( latitude ) ) " +
            "* cos( radians( longitude ) - radians(#{longitude}) ) + sin( radians(#{latitude}) ) * sin(radians(latitude)) ) ) AS distance " +
            "FROM PARKING " +
            "HAVING distance < 10 " +
            "ORDER BY distance LIMIT 10")
    fun searchParking(@Param("latitude") latitude: Double, @Param("longitude") longitude: Double): List<Parking>

}