package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface CategoryService {
    fun selectAllCategories(): List<Category>
    fun insertCategory(@Param("c") c: Category): Int
    fun deleteCategory(@Param("c") c: Category)
    fun updateCategory(@Param("c") c: Category)
}