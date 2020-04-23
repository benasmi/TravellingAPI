package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface CategoryPlaceService {

    @Select("SELECT * FROM CATEGORY c WHERE c.categoryId IN \n" +
            "   (SELECT cp.fk_categoryId FROM CATEGORY_PLACE cp WHERE cp.fk_placeId=#{id})")
    fun selectByPlaceId(@Param("id") id: Int): List<Category>

}