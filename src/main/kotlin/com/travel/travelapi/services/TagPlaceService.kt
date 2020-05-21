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


    fun selectTagsById(@Param("id") id: Int): List<Tag>

    fun insertTagForPlace(@Param("t") t: TagPlace)

    fun deleteTagForPlace(@Param("t") t: TagPlace)

    fun deleteTagForPlaceById(@Param("id") id: Int)

}