package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface CategoryService {
    @Select("SELECT * FROM CATEGORY")
    fun selectAllCategories(): List<Category>

    @Insert("INSERT INTO CATEGORY (name) VALUES (#{c.name});")
    @Options(useGeneratedKeys = true, keyProperty = "categoryId", keyColumn = "categoryId")
    fun insertCategory(@Param("c") c: Category): Int

    @Delete("DELETE FROM CATEGORY WHERE categoryId=#{c.categoryId}")
    fun deleteCategory(@Param("c") c: Category)

    @Update("UPDATE CATEGORY SET name=#{c.name} WHERE categoryId=#{c.categoryId}")
    fun updateCategory(@Param("c") c: Category)
}