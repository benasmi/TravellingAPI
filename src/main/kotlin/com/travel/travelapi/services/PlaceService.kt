package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.controllers.ExplorePageController
import com.travel.travelapi.models.Location
import com.travel.travelapi.models.Parking
import com.travel.travelapi.models.Place

import org.apache.ibatis.annotations.*

import com.travel.travelapi.models.PlaceLocal
import org.apache.ibatis.annotations.Select

import org.springframework.stereotype.Repository

@Repository
interface PlaceService {


    fun search(@Param("ids") ids: List<String>): Page<PlaceLocal>

    fun selectByCategoryAndLocation(@Param("categoryId") categoryId: Int, @Param("locationType") locationType: String, @Param("location") location: String): Page<PlaceLocal>

    fun selectAll(): Page<PlaceLocal>

    fun selectAllAdmin(@Param("keyword") keyword: String = "",
                       @Param("filterOptions") filterOptions: List<String> = arrayListOf(),
                       @Param("filterCategories") filterCategories: List<String> = arrayListOf(),
                       @Param("filterDateAdded") filterDateAdded: List<String> = arrayListOf(),
                       @Param("filterDateModified") filterDateModified: List<String> = arrayListOf(),
                       @Param("filterCountries") filterCountries: List<String> = arrayListOf(),
                       @Param("filterCities") filterCities: List<String> = arrayListOf(),
                       @Param("filterMunicipalities") filterMunicipalities: List<String> = arrayListOf(),
                       @Param("location") location: List<String> = arrayListOf(),
                       @Param("range") range: Double = 0.toDouble()): Page<PlaceLocal>

    fun selectPlacesInRadius(@Param("latitude") latitude: Double,
                             @Param("longitude") longitude: Double,
                             @Param("range") range: Double,
                             @Param("limit") limit: Int): ArrayList<PlaceLocal>

    fun searchClient(@Param("keyword") keyword: String): List<PlaceLocal>

    fun matchPlacesByLocation(@Param("location") location: String, @Param("locationType") locationType: String, @Param("distanceMax") distanceMax: Double?, @Param("sourceLat") sourceLat: Double?, @Param("sourceLon") sourceLon: Double?): List<PlaceLocal>

    fun selectById(@Param("id") id: Int): PlaceLocal
    fun updatePlace(@Param("p") p: PlaceLocal)
    fun insertPlace(@Param("p") p: PlaceLocal)
    fun deletePlace(@Param("id") id: Int)


    fun searchPlacesByLocation(@Param("latitude") latitude: Double,
                               @Param("longitude") longitude: Double,
                               @Param("range") range: Double): List<PlaceLocal>


    fun getAllCities(@Param("countryRestrictions") countryRestrictions: List<String>,
                     @Param("munRestrictions") munRestrictions: List<String>): ArrayList<String>

    fun getAllCountries(): ArrayList<String>
    fun getAllCounties(): ArrayList<String>
    fun getAllMunicipalities(@Param("restrictions") restrictions: List<String>): ArrayList<String>

    fun getPlaceLocation(@Param("id") id: Int): Location

    fun selectPlacesInBounds(@Param("minLat") minLat: Float,@Param("maxLat") maxLat: Float,@Param("minLng") minLng: Float,@Param("maxLng") maxLng: Float, @Param("limit") limit: Int): ArrayList<PlaceLocal>
}