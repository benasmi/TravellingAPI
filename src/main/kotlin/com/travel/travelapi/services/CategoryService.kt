package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import org.apache.ibatis.annotations.Select

interface CategoryService {
    @Select("SELECT * FROM CATEGORY")
    fun selectByPlaceId(): Category
}