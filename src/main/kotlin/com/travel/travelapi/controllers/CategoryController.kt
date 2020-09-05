package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.services.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import kotlin.collections.ArrayList


@RestController
@RequestMapping("/categories")
class CategoryController(@Autowired private val categoryService: CategoryService) {


    /**
     * @return all categories
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('category:read')")
    fun getAllCategories() : List<Category>{
        return categoryService.selectAllCategories()
    }

    /**
     * Insert category
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('category:write')")
    fun insertCategory(@RequestBody categories: List<Category>): List<Int>{
        val inserted: ArrayList<Int> = ArrayList()
        for(c: Category in categories){
            categoryService.insertCategory(c)
            inserted.add(c.categoryId!!)
        }
        return inserted
    }

    /**
     * Delete categories
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('category:write')")
    fun deleteCategory(@RequestBody categories: List<Category>){
        for(c: Category in categories)
            categoryService.deleteCategory(c)
    }

    /**
     * Delete categories
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('category:write')")
    fun updateCategory(@RequestBody categories: List<Category>){
        for(c: Category in categories)
            categoryService.updateCategory(c)
    }

}