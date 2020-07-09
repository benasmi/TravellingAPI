package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import com.travel.travelapi.models.TagPlace
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository()
interface CategoryPlaceService {


    fun selectByPlaceId(@Param("id") id: Int): List<Category>

    fun insertCategoryForPlace(@Param("c") c: CategoryPlace)

    fun deleteCategoryForPlace(@Param("c") c: CategoryPlace)

    fun deleteCategoryForPlaceById(@Param("id") id: Int)

}