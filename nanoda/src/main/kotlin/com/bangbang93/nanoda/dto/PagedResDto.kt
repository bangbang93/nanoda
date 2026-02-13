package com.bangbang93.nanoda.dto

import com.bangbang93.nanoda.interfaces.IPaged

open class PagedResDto<T>(
    override val data: List<T>,
    override val count: Int,
) : IPaged<T> {
  companion object {
    fun <T> fromInt(
        data: List<T>,
        count: Int,
    ): PagedResDto<T> = PagedResDto(data, count)

    fun <T> fromLong(
        data: List<T>,
        count: Long,
    ): PagedResDto<T> = PagedResDto(data, count.toInt())
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PagedResDto<*>) return false

    if (data != other.data) return false
    if (count != other.count) return false

    return true
  }

  override fun hashCode(): Int {
    var result = data.hashCode()
    result = 31 * result + count
    return result
  }
}
