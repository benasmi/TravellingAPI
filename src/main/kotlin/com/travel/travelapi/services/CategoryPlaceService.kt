package com.travel.travelapi.services

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import com.travel.travelapi.models.TagPlace
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface CategoryPlaceService {

    @Select("SELECT * FROM CATEGORY c WHERE c.categoryId IN \n" +
            "   (SELECT cp.fk_categoryId FROM CATEGORY_PLACE cp WHERE cp.fk_placeId=#{id})")
    fun selectByPlaceId(@Param("id") id: Int): List<Category>

    @Insert("INSERT INTO CATEGORY_PLACE (fk_categoryId, fk_placeId) VALUES (#{c.fk_categoryId}, #{c.fk_placeId})")
    fun insertCategoryForPlace(@Param("c") c: CategoryPlace)

    @Delete("DELETE FROM CATEGORY_PLACE WHERE fk_categoryId=#{c.fk_categoryId} AND fk_placeId=#{c.fk_placeId} ")
    fun deleteTag(@Param("c") c: CategoryPlace)
}