package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import com.travel.travelapi.models.Parking
import com.travel.travelapi.models.ParkingPlace
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface ParkingPlaceService{
    @Select("SELECT * FROM PARKING c WHERE c.parkingId IN \n" +
            "   (SELECT cp.fk_parkingId FROM PARKING_PLACE cp WHERE cp.fk_placeId=#{id})")
    fun selectByPlaceId(@Param("id") id: Int): List<Parking>

    @Insert("INSERT INTO PARKING_PLACE (fk_parkingId, fk_placeId) VALUES (#{p.fk_parkingId}, #{p.fk_placeId})")
    fun insertParkingForPlace(@Param("p") p: ParkingPlace)

    @Delete("DELETE FROM PARKING_PLACE WHERE fk_placeId=#{p.fk_placeId} AND fk_parkingId=#{p.fk_parkingId} ")
    fun deleteCategoryForPlace(@Param("p") p: ParkingPlace)
}