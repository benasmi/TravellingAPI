package com.travel.travelapi.services

import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface PhotoPlaceService {

    @Select("SELECT * FROM PHOTO c WHERE c.photoId IN \n" +
            "   (SELECT cp.fk_photoId FROM PHOTO_PLACE cp WHERE cp.fk_placeId=#{id})")
    fun selectPhotosById(@Param("id") id: Int): List<Photo>

    @Insert("INSERT INTO PHOTO_PLACE (fk_placeId, fk_photoId, isFeatured, indexInList) VALUES (#{p.fk_photoId}, #{p.fk_placeId}, #{p.isFeatured}, #{p.indexInList})")
    fun insertPhotoForPlace(@Param("p") p: PhotoPlace)

    @Update("UPDATE PHOTO_PLACE SET indexInList=#{p.indexInList} WHERE fk_photoId=#{p.fk_photoId}")
    fun updatePhotoIndexing(@Param("p") p: PhotoPlace)

    @Delete("DELETE FROM PHOTO_PLACE WHERE fk_photoId=#{p.fk_photoId}")
    fun deletePhotoFromPlace(@Param("p") p: PhotoPlace)

}