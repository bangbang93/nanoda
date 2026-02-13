package com.bangbang93.nanoda.dto

import org.springframework.data.domain.Sort
import org.springframework.data.mapping.toDotPath
import kotlin.reflect.KProperty

typealias SortParams = List<Pair<String, Int>>

@JvmOverloads
fun parseSort(
    sort: String,
    fieldAllowList: List<String>? = null,
): SortParams {
  if (sort.isBlank()) return emptyList()
  val sorts =
      sort.split(",").map {
        val trimmed = it.trim()
        when {
          trimmed.startsWith("-") -> trimmed.substring(1) to -1
          trimmed.startsWith("+") -> trimmed.substring(1) to 1
          else -> trimmed to 1
        }
      }
  return if (fieldAllowList == null) sorts
  else sorts.filter { (field, _) -> fieldAllowList.contains(field) }
}

@JvmName("parseSortKProperty")
fun parseSort(
    sort: String,
    fieldAllowList: List<KProperty<*>>,
): SortParams {
  val fieldAllowListNames = fieldAllowList.map { it.toDotPath() }
  return parseSort(sort, fieldAllowListNames)
}

@JvmOverloads
fun parseSortSpring(
    sort: String,
    fieldAllowList: List<String>? = null,
): Sort = parseSort(sort, fieldAllowList).toSpringSort()

@JvmName("parseSortSpringKProperty")
fun parseSortSpring(
    sort: String,
    fieldAllowList: List<KProperty<*>>,
): Sort = parseSort(sort, fieldAllowList).toSpringSort()

fun SortParams.toSpringSort(): Sort {
  if (this.isEmpty()) return Sort.unsorted()
  val orders =
      this.map { (field, direction) ->
        if (direction >= 0) Sort.Order.asc(field) else Sort.Order.desc(field)
      }
  return Sort.by(orders)
}
