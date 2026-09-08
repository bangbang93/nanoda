package com.bangbang93.nanoda.spring.data.mongodb.aggregation

import kotlin.reflect.KProperty
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation

/** $project 阶段作用域：仅支持包含/排除字段 */
class ProjectScope {
  private val includes = mutableListOf<String>()
  private val excludes = mutableListOf<String>()

  /** 仅返回给定属性 */
  fun include(vararg properties: KProperty<*>) {
    includes += properties.map { it.toDotPath() }
  }

  /** 排除给定属性 */
  fun exclude(vararg properties: KProperty<*>) {
    excludes += properties.map { it.toDotPath() }
  }

  /** 构建 [ProjectionOperation]；无任何 include/exclude 时返回 null */
  internal fun build(): ProjectionOperation? {
    if (includes.isEmpty() && excludes.isEmpty()) return null
    var op =
        if (includes.isEmpty()) ProjectionOperation()
        else Aggregation.project(*includes.toTypedArray())
    if (excludes.isNotEmpty()) {
      op = op.andExclude(*excludes.toTypedArray())
    }
    return op
  }
}
