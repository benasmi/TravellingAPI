package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.controllers.ExplorePageController
import com.travel.travelapi.controllers.TypeIdPair
import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface ExplorePageService {

    fun update(@Param("recommendations") recommendations: List<Int>)

    fun selectAll(): Page<Recommendation>

    fun selectToursForPlace(@Param("placeId") placeId: Int): List<Tour>

    fun matchSearch(@Param("keyword") keyword: String): List<SearchResultRegion>

    fun selectObjectsByLocationAndCategory(@Param("location") location: String, @Param("locationType") locationType: String, @Param("categoryId") categoryId: Int): List<CollectionObject>

    fun selectObjectsByCoordsAndCategory(@Param("latitude") latitude: Float, @Param("longitude") longitude: Float, @Param("categoryId") categoryId: Int): List<CollectionObject>
}