package com.travel.travelapi.services

import com.travel.travelapi.models.SearchedLocation
import com.travel.travelapi.models.SearchedPlace
import com.travel.travelapi.models.SearchedTour
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface DataCollectionService {
    fun insertSearchedPlace(@Param("searchedPlace") searchedPlace: SearchedPlace)
    fun insertSearchedTour(@Param("searchedTour") searchedTour: SearchedTour)
    fun insertSearchedLocation(@Param("searchedLocation") searchedLocation: SearchedLocation)
}