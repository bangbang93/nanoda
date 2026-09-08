package com.bangbang93.nanoda.spring.data.mongodb.aggregation

import kotlin.reflect.KProperty
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import org.springframework.data.mongodb.core.aggregation.GroupOperation.GroupOperationBuilder

/** $group 阶段作用域：累加器须以 [alias] 指定输出字段名后才生效 */
class GroupScope internal constructor(keys: Array<out String>) {
  private var operation: GroupOperation = Aggregation.group(*keys)

  /** 为累加器指定输出字段名（`as` 是 Kotlin 关键字，故用 alias），如 `count() alias "cnt"` */
  infix fun GroupOperationBuilder.alias(name: String) {
    operation = this.`as`(name)
  }

  /** 计数（$sum: 1） */
  fun count(): GroupOperationBuilder = operation.count()

  /** $sum 求和；仅限数值属性 */
  fun <T : Number?> sum(prop: KProperty<T>): GroupOperationBuilder = operation.sum(prop.toDotPath())

  /** $avg 平均值；仅限数值属性 */
  fun <T : Number?> avg(prop: KProperty<T>): GroupOperationBuilder = operation.avg(prop.toDotPath())

  /** $min 最小值 */
  fun min(prop: KProperty<*>): GroupOperationBuilder = operation.min(prop.toDotPath())

  /** $max 最大值 */
  fun max(prop: KProperty<*>): GroupOperationBuilder = operation.max(prop.toDotPath())

  /** $first 取分组内首个值 */
  fun first(prop: KProperty<*>): GroupOperationBuilder = operation.first(prop.toDotPath())

  /** $last 取分组内末个值 */
  fun last(prop: KProperty<*>): GroupOperationBuilder = operation.last(prop.toDotPath())

  /** $push 收集分组内所有值为数组 */
  fun push(prop: KProperty<*>): GroupOperationBuilder = operation.push(prop.toDotPath())

  /** $addToSet 去重收集分组内值为数组 */
  fun addToSet(prop: KProperty<*>): GroupOperationBuilder = operation.addToSet(prop.toDotPath())

  /** 构建 [GroupOperation] */
  internal fun build(): GroupOperation = operation
}
