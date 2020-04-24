package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.Tag
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface TagService {

    @Select("SELECT * FROM TAG")
    fun selectAllTags(): List<Tag>

    @Insert("INSERT INTO TAG (name) VALUES (#{t.name})")
    fun insertTag(@Param("t") t: Tag)

    @Delete("DELETE FROM TAG WHERE tagId=#{t.tagId}")
    fun deleteTag(@Param("t") t: Tag)

    @Update("UPDATE TAG SET name=#{t.tagId} WHERE tagId=#{t.tagId}")
    fun updateTag(@Param("t") t: Tag)
}