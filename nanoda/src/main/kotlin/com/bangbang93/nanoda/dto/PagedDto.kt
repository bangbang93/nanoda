package com.bangbang93.nanoda.dto

import com.bangbang93.nanoda.constants.MAX_LIMIT
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

open class PagedDto : IPagedDto {
  /** 页码 */
  @Min(1) override var page: Int = 1

  /** 每页条数 */
  @Suppress("MagicNumber") @Min(0) @Max(MAX_LIMIT) override var limit: Int = 10
}

interface IPagedDto {
  /** 页码 */
  val page: Int

  /** 每页条数 */
  val limit: Int

  /**
   * 计算跳过的条数
   *
   * @return 跳过的条数
   */
  val skip: Int
    get() = (page - 1) * limit
}
