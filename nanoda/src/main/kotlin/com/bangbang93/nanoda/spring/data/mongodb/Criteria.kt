package com.bangbang93.nanoda.spring.data.mongodb

import kotlin.reflect.KProperty
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.inValues
import org.springframework.data.mongodb.core.query.where

/**
 * [Criteria] 构建 DSL 作用域。
 *
 * 在 [criteria] 的 lambda 中通过中缀函数累积查询条件（隐式 $and），最后由 [build] 合并为单个 [Criteria]。
 */
class CriteriaScope {
  private val criteriaList = mutableListOf<Criteria>()

  /** 是否未累积任何条件 */
  val isEmpty: Boolean
    get() = criteriaList.isEmpty()

  /** 直接添加任意 [Criteria]，作为未覆盖操作符（如 elemMatch、bits）的逃生舱 */
  fun add(criteria: Criteria) {
    criteriaList.add(criteria)
  }

  /** 等于；value 为 null 时匹配字段为 null 或缺失的文档 */
  infix fun String.eq(value: Any?) {
    criteriaList.add(Criteria.where(this).`is`(value))
  }

  /** 等于；值类型与属性类型一致，可空属性可传 null */
  infix fun <T> KProperty<T>.eq(value: T) {
    criteriaList.add(where(this).`is`(value))
  }

  /** 不等于 */
  infix fun String.ne(value: Any?) {
    criteriaList.add(Criteria.where(this).ne(value))
  }

  /** 不等于；值类型与属性类型一致 */
  infix fun <T> KProperty<T>.ne(value: T) {
    criteriaList.add(where(this).ne(value))
  }

  /** 大于 */
  infix fun String.gt(value: Any) {
    criteriaList.add(Criteria.where(this).gt(value))
  }

  /** 大于；值类型与属性类型一致，属性须非空 */
  infix fun <T : Any> KProperty<T>.gt(value: T) {
    criteriaList.add(where(this).gt(value))
  }

  /** 大于等于 */
  infix fun String.gte(value: Any) {
    criteriaList.add(Criteria.where(this).gte(value))
  }

  /** 大于等于；值类型与属性类型一致，属性须非空 */
  infix fun <T : Any> KProperty<T>.gte(value: T) {
    criteriaList.add(where(this).gte(value))
  }

  /** 小于 */
  infix fun String.lt(value: Any) {
    criteriaList.add(Criteria.where(this).lt(value))
  }

  /** 小于；值类型与属性类型一致，属性须非空 */
  infix fun <T : Any> KProperty<T>.lt(value: T) {
    criteriaList.add(where(this).lt(value))
  }

  /** 小于等于 */
  infix fun String.lte(value: Any) {
    criteriaList.add(Criteria.where(this).lte(value))
  }

  /** 小于等于；值类型与属性类型一致，属性须非空 */
  infix fun <T : Any> KProperty<T>.lte(value: T) {
    criteriaList.add(where(this).lte(value))
  }

  /** 闭区间过滤（$gte + $lte）；任一边界为 null 时跳过该条件 */
  infix fun String.between(range: Pair<*, *>) {
    val (min, max) = range
    if (min != null && max != null) {
      criteriaList.add(Criteria.where(this).gte(min).lte(max))
    }
  }

  /** 闭区间过滤（$gte + $lte） */
  infix fun String.between(range: ClosedRange<*>) {
    criteriaList.add(Criteria.where(this).gte(range.start).lte(range.endInclusive))
  }

  /** 闭区间过滤（$gte + $lte）；任一边界为 null 时跳过该条件 */
  infix fun KProperty<*>.between(range: Pair<*, *>) {
    val (min, max) = range
    if (min != null && max != null) {
      criteriaList.add(where(this).gte(min).lte(max))
    }
  }

  /** 闭区间过滤（$gte + $lte）；受 ClosedRange 自类型约束，无法对齐属性类型 */
  infix fun KProperty<*>.between(range: ClosedRange<*>) {
    criteriaList.add(where(this).gte(range.start).lte(range.endInclusive))
  }

  /** $in 匹配，命中任一值 */
  infix fun String.inValues(values: Collection<*>) {
    criteriaList.add(Criteria.where(this).inValues(values))
  }

  /** $in 匹配，命中任一值 */
  infix fun KProperty<*>.inValues(values: Collection<*>) {
    criteriaList.add(where(this).inValues(values))
  }

  /** $nin 匹配，排除所有给定值 */
  infix fun String.ninValues(values: Collection<*>) {
    criteriaList.add(Criteria.where(this).nin(values))
  }

  /** $nin 匹配，排除所有给定值 */
  infix fun KProperty<*>.ninValues(values: Collection<*>) {
    criteriaList.add(where(this).nin(values))
  }

  /** 不区分大小写的包含匹配（自动转义特殊字符） */
  infix fun String.like(value: String) {
    criteriaList.add(Criteria.where(this).regex(Regex.escape(value), "i"))
  }

  /** 不区分大小写的包含匹配（自动转义特殊字符）；仅限字符串属性 */
  infix fun <T : String?> KProperty<T>.like(value: String) {
    criteriaList.add(where(this).regex(Regex.escape(value), "i"))
  }

  /** 正则匹配；带选项调用：`"name".regex("^a", "i")` */
  fun String.regex(pattern: String, options: String = "") {
    val base = Criteria.where(this)
    criteriaList.add(if (options.isEmpty()) base.regex(pattern) else base.regex(pattern, options))
  }

  /** 正则匹配；仅限字符串属性，带选项调用：`User::name.regex("^a", "i")` */
  fun <T : String?> KProperty<T>.regex(pattern: String, options: String = "") {
    val base = where(this)
    criteriaList.add(if (options.isEmpty()) base.regex(pattern) else base.regex(pattern, options))
  }

  /**
   * 或条件组合：每个 lambda 是一个分支，分支内的多个条件按 $and 组合。
   *
   * 仅一个有效分支时直接加入该条件（不包 $or），无有效分支时不添加任何条件。
   */
  fun or(vararg alternatives: CriteriaScope.() -> Unit) {
    val subs =
        alternatives.map { CriteriaScope().apply(it) }.filter { it.criteriaList.isNotEmpty() }
    if (subs.isEmpty()) return
    criteriaList.add(
        if (subs.size == 1) subs.single().build()
        else Criteria().orOperator(subs.map { it.build() }))
  }

  /**
   * 将已累积的条件合并为单个 [Criteria]。
   *
   * 单个条件直接返回，多个条件以 $and 组合。
   *
   * @return 合并后的 [Criteria]
   */
  fun build(): Criteria =
      when (criteriaList.size) {
        0 -> Criteria()
        1 -> criteriaList.first()
        else -> Criteria().andOperator(criteriaList)
      }
}

/**
 * 使用 DSL 构建 [Criteria]。
 *
 * 示例：
 *
 * ```kotlin
 * val c = criteria {
 *   "name" like "Alice"
 *   User::age between (10 to 20)
 *   or({ "status" eq "A" }, { "status" eq "B" })
 * }
 * ```
 *
 * @param c 条件构建 lambda
 * @return 合并后的 [Criteria]
 */
fun criteria(c: CriteriaScope.() -> Unit): Criteria {
  val scope = CriteriaScope()
  scope.c()
  return scope.build()
}
