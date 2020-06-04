package com.travel.travelapi.services

import com.travel.travelapi.models.Source
import com.travel.travelapi.models.SourcePlace
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface SourcePlaceService {

    fun getPlaceSourcesById(@Param("id") id: Int) : List<Source>
    fun insertSource(@Param("s") source: SourcePlace)
    fun deleteSource(@Param("s") source: SourcePlace)
    fun deleteSourcesById(@Param("id") id: Int)

}