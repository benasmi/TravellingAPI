package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.models.*
import com.travel.travelapi.models.search.CategoryAbstractionInfo
import com.travel.travelapi.models.search.SearchRequest
import com.travel.travelapi.models.search.TagInfo
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface ExplorePageService {

    fun update(@Param("recommendations") recommendations: List<Int>)

    fun selectAll(): Page<Recommendation>

    fun selectToursForPlace(@Param("placeId") placeId: Int): List<Tour>

    fun matchSearch(@Param("keyword") keyword: String): List<SearchResultRegion>

    fun selectObjectsByLocationAndCategory(@Param("location") location: String, @Param("locationType") locationType: String, @Param("categoryId") categoryId: Int): List<CollectionObject>

    fun selectObjectsByCoordsAndCategory(@Param("latitude") latitude: Float, @Param("longitude") longitude: Float, @Param("categoryId") categoryId: Int, @Param("distance") distance: Float): List<CollectionObject>

    fun averageCoordinatesForLocation(@Param("location") location: String, @Param("locationType") locationType: String): LatLng

    fun previewCategories(
            @Param("request") searchPreviewRequest: SearchRequest,
            @Param("centerCoords") centerCoords: LatLng?
    ): List<CategoryAbstractionInfo>?

    fun previewTags(
            @Param("request") searchPreviewRequest: SearchRequest,
            @Param("centerCoords") centerCoords: LatLng?
    ): List<TagInfo>?

    fun searchPlaces(
            @Param("request") searchRequest: SearchRequest,
            @Param("centerCoords") centerCoords: LatLng
    ): List<PlaceLocal>
}