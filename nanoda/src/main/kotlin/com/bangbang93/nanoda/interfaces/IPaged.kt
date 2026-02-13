package com.bangbang93.nanoda.interfaces

interface IPaged<T> {
  /** 内容 */
  val data: List<T>

  /** 总条数 */
  val count: Int
}
