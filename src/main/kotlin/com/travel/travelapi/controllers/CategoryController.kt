package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.services.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import kotlin.collections.ArrayList


@RestController
@RequestMapping("/categories")
class CategoryController(@Autowired private val categoryService: CategoryService) {


    /**
     * @return all categories
     */
    @GetMapping("/all")
    fun getAllCategories() : List<Category>{
        return categoryService.selectAllCategories()
    }

    /**
     * Insert category
     */
    @PostMapping("/insert")
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
    fun deleteCategory(@RequestBody categories: List<Category>){
        for(c: Category in categories)
            categoryService.deleteCategory(c)
    }

}