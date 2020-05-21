package com.travel.travelapi.services

import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface ParkingPlaceService{

    fun selectByPlaceId(@Param("id") id: Int): List<Parking>

    fun insertParkingForPlace(@Param("p") p: ParkingPlace)

    fun deleteParkingForPlace(@Param("p") p: ParkingPlace)

    fun deleteParkingForPlaceById(@Param("id") id: Int)

}