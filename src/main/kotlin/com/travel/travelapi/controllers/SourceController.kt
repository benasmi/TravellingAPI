package com.travel.travelapi.controllers

import com.travel.travelapi.models.Source
import com.travel.travelapi.services.SourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/source")
class SourceController (@Autowired private val sourceService: SourceService){

    @GetMapping("/all")
    fun selectSources(): List<Source>{
        return sourceService.selectAll()
    }

    @PostMapping("/insert")
    fun insertSource(@RequestBody s: Source): Source{
        sourceService.insertSource(s)
        return s
    }

}