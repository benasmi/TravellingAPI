package com.travel.travelapi.services

import com.travel.travelapi.models.TransportFrom
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface TransportService {
    fun getLocalPlaceTransportFrom(@Param("id") id: Int): ArrayList<TransportFrom>
    fun getApiPlaceTransportFrom(@Param("id") id: Int): ArrayList<TransportFrom>

}