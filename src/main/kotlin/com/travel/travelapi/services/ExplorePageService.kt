package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.controllers.ExplorePageController
import com.travel.travelapi.models.Recommendation
import com.travel.travelapi.models.SearchResultRegion
import com.travel.travelapi.models.Tour
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface ExplorePageService {

    fun update(@Param("recommendations") recommendations: List<Int>)

    fun selectAll(): Page<Recommendation>

    fun selectToursForPlace(@Param("placeId") placeId: Int): List<Tour>

    fun matchSearch(@Param("keyword") keyword: String): List<SearchResultRegion>

}