package com.bangbang93.nanoda.spring.data.mongodb.aggregation

import com.bangbang93.nanoda.dto.IPagedDto
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.FacetOperation

/**
 * $facet 阶段作用域：[paged] 输出 data 分支（$skip + $limit）与 total 分支（$count），[branch] 输出通用命名分支。
 *
 * 与 [MongoOperations.aggregatePaged] 配合使用。
 */
class FacetScope {
  private var operation = Aggregation.facet()

  /**
   * 分页分支：按 [IPagedDto] 输出 data 分支（$skip + $limit）与 total 分支（$count 总数）。
   *
   * page 从 1 开始；limit <= 0 时省略 $limit（聚合管道中 `$limit: 0` 非法）。
   */
  fun paged(dto: IPagedDto) {
    val ops = buildList {
      add(Aggregation.skip(dto.skip.toLong()))
      if (dto.limit > 0) {
        add(Aggregation.limit(dto.limit.toLong()))
      }
    }
    operation = operation.and(*ops.toTypedArray()).`as`(FACET_DATA)
    operation = operation.and(Aggregation.count().`as`(FACET_COUNT_VALUE)).`as`(FACET_TOTAL)
  }

  /**
   * 通用命名分支：lambda 内可使用 [AggregationScope] 的全部阶段语法。
   *
   * 注意 MongoDB 限制：分支内不可再嵌 `$facet`（执行时由 MongoDB 报错），DSL 层不拦截。
   */
  fun branch(name: String, b: AggregationScope.() -> Unit) {
    operation = operation.and(*AggregationScope().apply(b).build().toTypedArray()).`as`(name)
  }

  /** 构建 [FacetOperation] */
  internal fun build(): FacetOperation = operation

  companion object {
    /** data 分支字段名 */
    const val FACET_DATA = "data"

    /** total 分支字段名 */
    const val FACET_TOTAL = "total"

    /** total 分支计数字段名 */
    const val FACET_COUNT_VALUE = "value"
  }
}
