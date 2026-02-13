package com.bangbang93.nanoda.collections

import java.lang.reflect.Field

/**
 * 根据指定字段对可迭代集合进行排序
 *
 * @param sort 排序字符串，格式为 "+fieldName" 或 "-fieldName"
 * @return 按指定字段排序后的列表
 */
fun <T> Iterable<T>.sortedByField(sort: String): List<T> {
  val list = this.toList()
  return when {
    list.isEmpty() -> emptyList()
    else -> sortListByField(list, sort)
  }
}

private fun <T> sortListByField(
    list: List<T>,
    sort: String,
): List<T> {
  val isAscending = isAscendingSort(sort)
  val cleanFieldName = sort.removePrefix("+").removePrefix("-")
  val field = getField(list, cleanFieldName) ?: return list
  val comparator: Comparator<T> = createComparator(field, isAscending)
  return list.sortedWith(comparator)
}

private fun isAscendingSort(sort: String): Boolean =
    when {
      sort.startsWith("+") -> true
      sort.startsWith("-") -> false
      else -> true
    }

private fun <T> getField(
    list: List<T>,
    fieldName: String,
): Field? =
    try {
      list.first()?.javaClass?.getDeclaredField(fieldName)?.apply { isAccessible = true }
    } catch (_: NoSuchFieldException) {
      null
    }

@Suppress("UNCHECKED_CAST")
private fun <T> createComparator(
    field: Field,
    isAscending: Boolean,
): Comparator<T> = Comparator { o1, o2 ->
  val v1 = field[o1] as? Comparable<Any>
  val v2 = field[o2] as? Comparable<Any>

  val result =
      when {
        v1 == null && v2 == null -> 0
        v1 == null -> -1
        v2 == null -> 1
        else -> v1.compareTo(v2)
      }

  if (isAscending) result else -result
}
