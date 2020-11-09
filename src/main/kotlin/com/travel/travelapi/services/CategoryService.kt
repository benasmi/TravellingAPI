package com.travel.travelapi.services

import com.travel.travelapi.models.AbstractionCategory
import com.travel.travelapi.models.Category
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface CategoryService {
    fun selectAllCategories(): List<Category>
    fun getCategoriesForAbstracted(@Param("id") id: Int): List<Category>
    fun updateAbstractionCategory(@Param("categoryAbstraction") categoryAbstraction: AbstractionCategory)
    fun getAbstractionCategoryLeftOver(): List<Category>
    fun selectAllAbstractionCategories(): ArrayList<Category>
    fun insertCategory(@Param("c") c: Category): Int
    fun deleteCategory(@Param("c") c: Category)
    fun updateCategory(@Param("c") c: Category)
    fun allUniqueCategories(): List<Category>
}