package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.Source
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.services.SourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/source")
class SourceController (@Autowired private val sourceService: SourceService){

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('source:read')")
    fun selectSources(): List<Source>{
        return sourceService.selectAll()
    }

    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('source:insert')")
    fun insertSource(@RequestBody s: Source): Source{
        sourceService.insertSource(s)
        return s
    }

}