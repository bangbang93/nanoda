package com.bangbang93.nanoda.spring.data.mongodb.aggregation

import com.bangbang93.nanoda.dto.PagedResDto
import com.bangbang93.nanoda.spring.data.mongodb.CriteriaScope
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.bson.Document
import org.springframework.data.domain.Sort
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.TypedAggregation

/**
 * [TypedAggregation] 构建 DSL 作用域。
 *
 * 各方法按调用顺序向管道追加阶段：[match] 复用 [CriteriaScope]，[sort] 复用父包的
 * [com.bangbang93.nanoda.spring.data.mongodb.asc]/[com.bangbang93.nanoda.spring.data.mongodb.desc]，未覆盖的阶段（如
 * graphLookup、bucket、setWindowFields）通过 [raw] 逃生舱追加。
 *
 * @see aggregation
 */
class AggregationScope {
  private val operations = mutableListOf<AggregationOperation>()

  /** 追加 $match 阶段，条件语法与 Criteria DSL 一致；无条件时不追加 */
  fun match(c: CriteriaScope.() -> Unit) {
    val scope = CriteriaScope().apply(c)
    if (!scope.isEmpty) {
      operations += Aggregation.match(scope.build())
    }
  }

  /** 追加 $sort 阶段 */
  fun sort(vararg orders: Sort.Order) {
    operations += Aggregation.sort(Sort.by(orders.toList()))
  }

  /** 追加 $skip 阶段（与 find 查询不同，$skip: 0 在管道中合法，会原样追加） */
  fun skip(n: Long) {
    operations += Aggregation.skip(n)
  }

  /**
   * 追加 $limit 阶段。
   *
   * 与 Query DSL 不同，聚合管道中 `$limit: 0` 非法（MongoDB 直接拒绝），因此 limit <= 0 时不追加该阶段。
   */
  fun limit(n: Int) {
    if (n > 0) {
      operations += Aggregation.limit(n.toLong())
    }
  }

  /** 追加 $project 阶段（仅 include/exclude，无表达式）；两者都为空时不追加 */
  fun project(p: ProjectScope.() -> Unit) {
    ProjectScope().apply(p).build()?.let { operations += it }
  }

  /** 追加 $group 阶段，分组键为属性引用 */
  fun group(vararg keys: KProperty<*>, g: GroupScope.() -> Unit) {
    group(*keys.map { it.toDotPath() }.toTypedArray(), g = g)
  }

  /** 追加 $group 阶段，分组键为字段名 */
  fun group(vararg keys: String, g: GroupScope.() -> Unit) {
    operations += GroupScope(keys).apply(g).build()
  }

  /** 追加 $unwind 阶段；preserveNullAndEmpty 为 true 时保留空数组/缺失字段的文档 */
  fun unwind(prop: KProperty<*>, preserveNullAndEmpty: Boolean = false) {
    operations += Aggregation.unwind(prop.toDotPath(), preserveNullAndEmpty)
  }

  /** 追加 $lookup 阶段，关联字段为属性引用 */
  fun lookup(from: String, localField: KProperty<*>, foreignField: KProperty<*>, asField: String) {
    lookup(from, localField.toDotPath(), foreignField.toDotPath(), asField)
  }

  /** 追加 $lookup 阶段，关联字段为字段名 */
  fun lookup(from: String, localField: String, foreignField: String, asField: String) {
    operations += Aggregation.lookup(from, localField, foreignField, asField)
  }

  /** 追加 $replaceRoot 阶段，以属性引用的子文档作为新根文档 */
  fun replaceRoot(prop: KProperty<*>) {
    operations += Aggregation.replaceRoot(prop.toDotPath())
  }

  /** 追加 $replaceRoot 阶段，以字段名的子文档作为新根文档 */
  fun replaceRoot(field: String) {
    operations += Aggregation.replaceRoot(field)
  }

  /**
   * 追加 $addFields 阶段。
   *
   * 不提供 `set` 入口：管道中 `$set` 是 `$addFields` 的官方别名，且 `set` 在本库 Update DSL 中已表示更新语义。
   */
  fun addFields(a: AddFieldsScope.() -> Unit) {
    operations += AddFieldsScope().apply(a).build()
  }

  /** 追加 $facet 阶段 */
  fun facet(f: FacetScope.() -> Unit) {
    operations += FacetScope().apply(f).build()
  }

  /** 逃生舱：直接追加任意 [AggregationOperation]（graphLookup/bucket/setWindowFields/表达式阶段等） */
  fun raw(op: AggregationOperation) {
    operations += op
  }

  /**
   * 构建管道阶段列表。
   *
   * @return 按追加顺序排列的 [AggregationOperation] 列表
   */
  fun build(): List<AggregationOperation> = operations.toList()
}

/**
 * 使用 DSL 构建 [TypedAggregation]（输入类型为 [T]，属性映射生效）。
 *
 * 示例：
 *
 * ```kotlin
 * val a = aggregation<User> {
 *   match { User::age gte 18 }
 *   sort(desc(User::createdAt), asc("name"))
 *   group(User::status) { count() alias "cnt" }
 *   limit(10)
 * }
 * ```
 *
 * @param a 聚合管道构建 lambda
 * @return 配置好的 [TypedAggregation]
 */
inline fun <reified T> aggregation(noinline a: AggregationScope.() -> Unit): TypedAggregation<T> =
    Aggregation.newAggregation(T::class.java, AggregationScope().apply(a).build())

/**
 * 执行 [TypedAggregation] 聚合，输出类型为 [R]。
 *
 * @param a 聚合定义
 * @return 聚合结果
 */
inline fun <T, reified R> MongoOperations.aggregate(a: TypedAggregation<T>): AggregationResults<R> =
    aggregate(a, R::class.java)

/**
 * 使用 DSL 构建并执行聚合，输出类型为 [R]。
 *
 * @param a 聚合管道构建 lambda（输入类型为 [T]）
 * @return 聚合结果
 */
inline fun <reified T, reified R> MongoOperations.aggregate(
    noinline a: AggregationScope.() -> Unit
): AggregationResults<R> = aggregate(aggregation<T>(a), R::class.java)

/**
 * 使用 DSL 构建并执行响应式聚合，结果转为 [Flow]。
 *
 * @param a 聚合管道构建 lambda（输入类型为 [T]）
 * @return 聚合结果流
 */
inline fun <reified T, reified R : Any> ReactiveMongoOperations.aggregate(
    noinline a: AggregationScope.() -> Unit
): Flow<R> = aggregate(aggregation<T>(a), R::class.java).asFlow()

/**
 * 使用 DSL 构建并执行聚合分页，DSL 中须包含 `facet { paged(dto) }`。
 *
 * 解析单个 facet 结果文档：data 分支为分页数据，total 分支首个文档的 value 字段为总数（无匹配时 total 为空 数组，总数按 0 处理）。
 *
 * @param a 聚合管道构建 lambda（输入类型为 [T]）
 * @return 分页数据与总数
 */
inline fun <reified T, reified R> MongoOperations.aggregatePaged(
    noinline a: AggregationScope.() -> Unit
): PagedResDto<R> {
  val result = aggregate(aggregation<T>(a), Document::class.java).uniqueMappedResult ?: Document()
  val data =
      result.getList(FacetScope.FACET_DATA, Document::class.java).orEmpty().map {
        converter.read(R::class.java, it)
      }
  val count =
      (result.getList(FacetScope.FACET_TOTAL, Document::class.java).orEmpty().firstOrNull()?.let {
            it[FacetScope.FACET_COUNT_VALUE] as? Number
          })
          ?.toInt() ?: 0
  return PagedResDto(data, count)
}
