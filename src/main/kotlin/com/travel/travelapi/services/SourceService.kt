package com.travel.travelapi.services

import com.travel.travelapi.models.Source
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface SourceService {

    fun selectAll(): List<Source>
    fun insertSource(@Param("s") s: Source) : Int

}