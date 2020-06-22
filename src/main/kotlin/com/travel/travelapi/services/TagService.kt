package com.travel.travelapi.services

import com.travel.travelapi.models.Tag
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface TagService {
    fun selectAllTags(): List<Tag>
    fun insertTag(@Param("t") t: Tag)
    fun deleteTag(@Param("t") t: Tag)
    fun updateTag(@Param("t") t: Tag)
}