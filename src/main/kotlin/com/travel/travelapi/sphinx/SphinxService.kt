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
        val ids = ArrayList<String>()
        SphinxQL.executeQuery("SELECT * FROM places WHERE MATCH ('${keyword}')")?.let {
            while (it.next()) {
                ids.add(it.getString(1))
            }
        }
        return ids
    }
}