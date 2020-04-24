package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.Tag
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface TagService {

    @Select("SELECT * FROM TAG")
    fun selectAllTags(): List<Tag>

    @Select("INSERT INTO TAG (name) VALUES (#{t.name})")
    fun insertTag(@Param("t") t: Tag)
}