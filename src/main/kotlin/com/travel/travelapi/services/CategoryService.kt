package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface CategoryService {
    @Select("SELECT * FROM CATEGORY")
    fun selectAllCategories(): List<Category>

    @Select("INSERT INTO CATEGORY (name) VALUES (#{c.name})")
    fun insertCategory(@Param("c") c: Category)

}