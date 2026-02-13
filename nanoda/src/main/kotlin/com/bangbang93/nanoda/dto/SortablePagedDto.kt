package com.bangbang93.nanoda.dto

open class SortablePagedDto(
    /** 排序字段 例： +createdAt,"+-"代表正序倒序 */
    open var sort: String? = null,
) : PagedDto()
