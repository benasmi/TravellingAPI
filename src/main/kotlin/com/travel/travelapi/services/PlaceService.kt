package com.travel.travelapi.services

import com.github.pagehelper.Page

import org.apache.ibatis.annotations.*

import com.travel.travelapi.models.PlaceLocal
import org.apache.ibatis.annotations.Select

import org.springframework.stereotype.Repository

@Repository
interface PlaceService {


    fun search(@Param("ids") ids: List<String>): Page<PlaceLocal>

    fun selectAll(): Page<PlaceLocal>

    fun selectAllAdmin(@Param("keyword") keyword: String) : Page<PlaceLocal>

    fun selectById(@Param("id") id: Int): PlaceLocal

    fun updatePlace(@Param("p") p: PlaceLocal)

    fun insertPlace(@Param("p") p: PlaceLocal)
}