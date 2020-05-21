package com.travel.travelapi.services

import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface PhotoPlaceService {


    fun selectPhotosById(@Param("id") id: Int): List<Photo>

    fun insertPhotoForPlace(@Param("p") p: PhotoPlace)

    fun updatePhotoIndexing(@Param("p") p: PhotoPlace)

    fun deletePhotoFromPlace(@Param("p") p: PhotoPlace)

    fun deletePhotoForPlaceById(@Param("id") id: Int)

}