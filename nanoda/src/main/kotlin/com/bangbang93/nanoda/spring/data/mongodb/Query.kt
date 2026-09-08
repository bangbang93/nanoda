package com.bangbang93.nanoda.spring.data.mongodb

import com.bangbang93.nanoda.dto.IPagedDto
import com.bangbang93.nanoda.dto.PagedResDto
import kotlin.reflect.KProperty
import org.springframework.data.domain.Sort
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query

/**
 * [Query] 构建 DSL 作用域。
 *
 * 通过 [where] 复用 [CriteriaScope] 追加查询条件，并支持排序、投影、分页、[comment]、[hint]。
 *
 * @see query
 */
class QueryScope {
  private val criteriaScope = CriteriaScope()
  private val orders = mutableListOf<Sort.Order>()
  private val includes = mutableListOf<String>()
  private val excludes = mutableListOf<String>()

  /** 偏移量 */
  var skip: Long? = null

  /** 条数限制 */
  var limit: Int? = null

  /** 查询注释（出现在 Mongo profiler 与慢查询日志，建议放请求 ID） */
  var comment: String? = null

  /** 索引提示 */
  var hint: String? = null

  /** 追加查询条件，可多次调用，条件间以 $and 合并 */
  fun where(c: CriteriaScope.() -> Unit) {
    criteriaScope.c()
  }

  /** 按 [IPagedDto] 设置分页（page 从 1 开始） */
  fun page(dto: IPagedDto) {
    skip = (dto.page - 1L) * dto.limit
    limit = dto.limit
  }

  /** 设置排序顺序 */
  fun sortedBy(vararg orders: Sort.Order) {
    this.orders += orders
  }

  /** 投影：仅返回给定属性 */
  fun only(vararg properties: KProperty<*>) {
    includes += properties.map { it.toDotPath() }
  }

  /** 投影：排除给定属性 */
  fun exclude(vararg properties: KProperty<*>) {
    excludes += properties.map { it.toDotPath() }
  }

  /**
   * 构建 [Query] 对象。
   *
   * @return 配置好的 [Query]
   */
  fun build(): Query {
    val query = if (criteriaScope.isEmpty) Query() else Query().addCriteria(criteriaScope.build())
    skip?.let(query::skip)
    limit?.let(query::limit)
    comment?.let(query::comment)
    hint?.let(query::withHint)
    if (orders.isNotEmpty()) {
      query.with(Sort.by(orders))
    }
    if (includes.isNotEmpty() || excludes.isNotEmpty()) {
      val fields = query.fields()
      if (includes.isNotEmpty()) {
        fields.include(includes)
      }
      if (excludes.isNotEmpty()) {
        fields.exclude(excludes)
      }
    }
    return query
  }
}

/** 升序排序规则（字段名） */
fun asc(field: String): Sort.Order = Sort.Order.asc(field)

/** 升序排序规则（属性引用） */
fun asc(property: KProperty<*>): Sort.Order = Sort.Order.asc(property.toDotPath())

/** 降序排序规则（字段名） */
fun desc(field: String): Sort.Order = Sort.Order.desc(field)

/** 降序排序规则（属性引用） */
fun desc(property: KProperty<*>): Sort.Order = Sort.Order.desc(property.toDotPath())

/**
 * 使用 DSL 构建 [Query]。
 *
 * 示例：
 *
 * ```kotlin
 * val q = query {
 *   where { User::age gte 18 }
 *   sortedBy(desc(User::createdAt))
 *   page(searchDto)
 *   only(User::name)
 * }
 * ```
 *
 * @param q 查询构建 lambda
 * @return 配置好的 [Query]
 */
fun query(q: QueryScope.() -> Unit): Query {
  val scope = QueryScope()
  scope.q()
  return scope.build()
}

/**
 * 使用 DSL 构建查询并返回分页结果（数据 + 总数），skip/limit 取自 DSL 设置。
 *
 * @param q 查询构建 lambda
 * @return 分页数据与总数
 */
suspend inline fun <reified T> MongoOperations.findAndCount(
    q: QueryScope.() -> Unit
): PagedResDto<T> {
  val scope = QueryScope().apply(q)
  val built = scope.build()
  return findAndCount(built, scope.skip?.toInt() ?: 0, scope.limit ?: 0)
}
