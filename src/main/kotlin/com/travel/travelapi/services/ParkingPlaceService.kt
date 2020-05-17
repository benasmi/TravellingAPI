package com.travel.travelapi.services

import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface ParkingPlaceService{
    @Select("SELECT DISTINCT p.* from PARKING p \n" +
            "INNER JOIN PARKING_PLACE pp on p.parkingId = pp.fk_parkingId WHERE pp.fk_placeId=#{id} ORDER BY pp.priority ASC")
    fun selectByPlaceId(@Param("id") id: Int): List<Parking>

    @Update("UPDATE PARKING_PLACE SET priority=#{p.priority} WHERE fk_parkingId=#{p.fk_parkingId} AND fk_placeId=#{p.fk_placeId}")
    fun updateParkingIndexing(@Param("p") p: ParkingPlace)

    @Insert("INSERT INTO PARKING_PLACE (fk_parkingId, fk_placeId, priority) VALUES (#{p.fk_parkingId}, #{p.fk_placeId}, #{p.priority})")
    fun insertParkingForPlace(@Param("p") p: ParkingPlace)

    @Delete("DELETE FROM PARKING_PLACE WHERE fk_placeId=#{p.fk_placeId} AND fk_parkingId=#{p.fk_parkingId} ")
    fun deleteCategoryForPlace(@Param("p") p: ParkingPlace)

    @Delete("DELETE FROM PARKING_PLACE WHERE fk_placeId=#{id}")
    fun deleteParkingForPlaceById(@Param("id") id: Int)

}