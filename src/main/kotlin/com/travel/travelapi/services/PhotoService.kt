package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.Photo
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface PhotoService {

    @Insert("INSERT INTO PHOTO (url) VALUES (#{p.url})")
    @Options(useGeneratedKeys = true, keyProperty = "photoId", keyColumn = "photoId")
    fun insertPhoto(@Param("p") p: Photo)

}