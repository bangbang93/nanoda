package com.bangbang93.nanoda.dto

import com.bangbang93.nanoda.constants.MAX_LIMIT
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

open class PagedDto {
  /** 页码 */
  @Min(1) open var page: Int = 1

  /** 每页条数 */
  @Suppress("MagicNumber") @Min(0) @Max(MAX_LIMIT) open var limit: Int = 10

  /**
   * 计算跳过的条数
   *
   * @return 跳过的条数
   */
  open val skip: Int by lazy { (page - 1) * limit }
}
