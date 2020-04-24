package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.Tag
import com.travel.travelapi.services.TagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tags")
class TagController(@Autowired private val tagService: TagService) {

    /**
     * Get all tags
     */
    @GetMapping("/all")
    fun getAllTags(): List<Tag>{
        return tagService.selectAllTags()
    }

    /**
     * Insert tags
     */
    @PostMapping("/insert")
    fun insertTag(@RequestBody tags: List<Tag>){
        for(t: Tag in tags){
            tagService.insertTag(t)
        }
    }

}