package com.travel.travelapi.sphinx

import org.springframework.stereotype.Repository

@Repository
class SphinxService{


    /**
     * Searching places by any keyword from tags, categories and place using sphinx search engine.
     * @param keyword to search
     * @return list of ids that match keyword(-s)
     *
     */
    fun searchPlacesByKeyword(keyword: String):List<String>{
        return  SphinxQL.executeQuery("SELECT * FROM places WHERE MATCH ('${keyword}')")
    }

    /**
     * Searching places by any keyword from tags, categories and place using sphinx search engine.
     * @param keyword to search
     * @return list of ids that match keyword(-s)
     *
     */
    fun searchParkingByLatLng(lat: Double, lng: Double):List<String>{
        val latToRad = Math.toRadians(lat)
        val lngToRad = Math.toRadians(lng)
        return  SphinxQL.executeQuery("SELECT *, GEODIST(${latToRad}, ${lngToRad}, latitude, longitude) as distance FROM parking WHERE distance < 10000 ORDER BY distance ASC LIMIT 0,100;")
    }


}