package com.travel.travelapi.services

import com.travel.travelapi.models.Parking
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface ParkingService {

    @Select("SELECT * FROM PARKING WHERE fk_placeId=#{id}")
    fun selectParkingLocationsById(@Param("id") id: Int): List<Parking>
}