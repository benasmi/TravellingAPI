package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.Photo
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface PhotoService {

    fun insertPhoto(@Param("p") p: Photo)
    fun updatePhoto(@Param("id") id: Int, @Param("url") url: String)

}