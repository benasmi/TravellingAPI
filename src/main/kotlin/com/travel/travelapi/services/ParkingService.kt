package com.travel.travelapi.services

import com.travel.travelapi.models.Parking
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface ParkingService {

    @Select("SELECT * FROM PARKING WHERE address LIKE #{p.address}")
    fun getAllParkingInfo(): List<Parking>

    @Insert("INSERT INTO PARKING (latitude, longitude, address, priority) VALUES (#{p.latitude},#{p.longitude}, #{p.address}, #{p.priority})")
    fun insertParking(@Param("p") p: Parking)

    @Delete("DELETE FROM PARKING WHERE parkingId=#{p.parkingId}")
    fun deleteParking(@Param("p") p: Parking)

    @Update("UPDATE PARKING SET latitude=#{p.latitude}, longitude=#{p.longitude}, address=#{p.address}, priority=#{p.priority} WHERE parkingId=#{p.parkingId}")
    fun updateParking(@Param("p") p: Parking)

}