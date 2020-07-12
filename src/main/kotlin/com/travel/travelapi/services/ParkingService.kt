package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.models.Parking
import com.travel.travelapi.models.PlaceLocal
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface ParkingService {



    fun search(@Param("ids") ids: List<String>): List<Parking>

    fun getAllParkingInfo(@Param("p") p: Parking): List<Parking>

    fun insertParking(@Param("p") p: Parking)

    fun deleteParking(@Param("p") p: Parking)

    fun updateParking(@Param("p") p: Parking)

    fun searchParking(@Param("latitude") latitude: Double, @Param("longitude") longitude: Double): List<Parking>

    fun getParkingById(@Param("id") id: Int): Parking

}