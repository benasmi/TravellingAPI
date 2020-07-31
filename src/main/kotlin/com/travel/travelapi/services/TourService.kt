package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface TourService {
    fun getTourById(@Param("id") id: Int): Tour
    fun deleteTourById(@Param("id") id: Int)
    fun insertTour(@Param("tour") tour: Tour)
    fun updateTour(@Param("tour") tour: Tour, @Param("id") id: Int)

    fun selectAllAdmin(@Param("keyword") keyword: String,
                       @Param("filterOptions") filterOptions: List<String>) : Page<Tour>

    fun photosForTour(@Param("id") tourId: Int) : List<Photo>


    fun getCategoriesForTour(@Param("id") id: Int): ArrayList<Category>

    fun deleteCategoriesForTour(@Param("id") id: Int)

    fun addCategoryForTour(@Param("category") category: Int, @Param("id") tourId: Int)

    fun changeVerificationStatus(@Param("tourId") tourId: Int, @Param("verified") verified: Boolean)

    fun changePublicityStatus(@Param("tourId") tourId: Int, @Param("public") public: Boolean)
}