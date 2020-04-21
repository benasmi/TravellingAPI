package com.travel.travelapi.services

import com.travel.travelapi.models.Place
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface PlaceService {

    @Select("SELECT * from PLACE")
    fun selectAll(): List<Place>
}