package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.models.Recommendation
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface ExplorePageService {

    fun update(@Param("recommendations") recommendations: List<Int>)

    fun selectAll(): Page<Recommendation>

}