package com.travel.travelapi.services

import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface PhotoPlaceService {

    @Select("SELECT DISTINCT p.* from PHOTO p\n" +
            "INNER JOIN PHOTO_PLACE pp on p.photoId = pp.fk_photoId WHERE pp.fk_placeId=#{id} ORDER BY pp.indexInList ASC")
    fun selectPhotosById(@Param("id") id: Int): List<Photo>

    @Insert("INSERT INTO PHOTO_PLACE (fk_photoId, fk_placeId, isFeatured, indexInList) VALUES (#{p.fk_photoId}, #{p.fk_placeId}, #{p.isFeatured}, #{p.indexInList})")
    fun insertPhotoForPlace(@Param("p") p: PhotoPlace)

    @Update("UPDATE PHOTO_PLACE SET indexInList=#{p.indexInList} WHERE fk_photoId=#{p.fk_photoId}")
    fun updatePhotoIndexing(@Param("p") p: PhotoPlace)

    @Delete("DELETE FROM PHOTO_PLACE WHERE fk_photoId=#{p.fk_photoId} AND fk_placeId=#{p.fk_placeId}")
    fun deletePhotoFromPlace(@Param("p") p: PhotoPlace)

}