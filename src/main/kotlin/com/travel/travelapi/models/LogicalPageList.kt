package com.travel.travelapi.models

import java.lang.Math.ceil

/**
 * Logical pagination of objects
 */
class LogicalPageList<T> : ArrayList<T>() {
    fun getPage(page: Int, pageSize: Int): LogicalPage{

        var pagesCount = this.size / pageSize
        pagesCount += (this.size % pageSize).coerceAtMost(1)

        if(page+1>pagesCount){
            return LogicalPage(this.size,page,pagesCount,0,false)
        }

        val lastIndex = (page*pageSize+pageSize).coerceAtMost(this.lastIndex+1)

        val items = this.subList(
                page*pageSize,
                lastIndex)

        val hasNext = page+1 < pagesCount

        return LogicalPage(this.size,page, pagesCount, items.size, hasNext, this as Collection<ObjectCollection>)
    }

}

data class LogicalPage(
        val totalItems: Int = 0,
        val currentPage: Int = 0,
        val totalPages: Int = 0,
        val pageSize: Int = 0,
        val hasNext: Boolean = false,
        val list: Collection<ObjectCollection> = listOf()
)