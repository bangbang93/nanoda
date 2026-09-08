package com.bangbang93.nanoda.dto

open class SortablePagedDto(
    override val page: Int = 1,
    override val limit: Int = 10,
    /** 排序字段 例： +createdAt,"+-"代表正序倒序 */
    override val sort: String? = null,
) : ISortablePagedDto

interface ISortablePagedDto : IPagedDto {
    /** 排序字段 例： +createdAt,"+-"代表正序倒序 */
    val sort: String?
}
