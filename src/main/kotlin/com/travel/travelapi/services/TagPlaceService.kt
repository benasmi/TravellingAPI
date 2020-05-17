package com.travel.travelapi.services

import com.travel.travelapi.models.Photo
import com.travel.travelapi.models.Tag
import com.travel.travelapi.models.TagPlace
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository


@Repository
interface TagPlaceService {

    @Select("SELECT * FROM TAG c WHERE c.tagId IN \n" +
            "   (SELECT cp.fk_tagId FROM TAG_PLACE cp WHERE cp.fk_placeId=#{id})")
    fun selectTagsById(@Param("id") id: Int): List<Tag>

    @Insert("INSERT INTO TAG_PLACE (fk_tagId, fk_placeId) VALUES (#{t.fk_tagId}, #{t.fk_placeId})")
    fun insertTagForPlace(@Param("t") t: TagPlace)

    @Delete("DELETE FROM TAG_PLACE WHERE fk_tagId=#{t.fk_tagId} AND fk_placeId=#{t.fk_placeId} ")
    fun deleteTagForPlace(@Param("t") t: TagPlace)

    @Delete("DELETE FROM TAG_PLACE WHERE fk_placeId=#{id}")
    fun deleteTagForPlaceById(@Param("id") id: Int)

}