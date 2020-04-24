package com.travel.travelapi.services

import com.travel.travelapi.models.Parking
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface ParkingService {

    //todo: change parking logic with interim tables
    @Select("SELECT * FROM PARKING WHERE fk_placeId=#{id}")
    fun selectParkingLocationsById(@Param("id") id: Int): List<Parking>

    @Insert("INSERT INTO PARKING (fk_placeId, latitude, longitude, address, priority) VALUES (#{p.fk_placeId}, #{p.latitude},#{p.longitude}, #{p.address}, #{p.priority})")
    fun insertParkingForPlace(@Param("p") p: Parking)

    @Delete("DELETE FROM PARKING WHERE parkingId=#{p.parkingId}")
    fun deleteParking(@Param("p") p: Parking)

    @Update("UPDATE PARKING SET latitude=#{p.latitude}, longitude=#{p.longitude}, address=#{p.address}, priority=#{p.priority} WHERE parkingId=#{p.parkingId}")
    fun updateParking(@Param("p") p: Parking)

}