package com.bangbang93.nanoda.collections

/**
 * 将集合均匀切分为 n 个子集合
 *
 * @param n 切分的数量，必须大于0
 * @return 包含子集合的列表，尽可能均匀分布元素
 * @throws IllegalArgumentException 当 n <= 0 时抛出异常
 */
fun <T> Collection<T>.parted(n: Int): List<List<T>> {
  require(n > 0) { "Split count must be greater than 0" }

  if (isEmpty()) {
    return emptyList()
  }

  return if (n >= size) {
    // 如果切分数量大于等于集合大小，每个元素单独成为一个子集合
    map { listOf(it) }
  }

  // 使用 Kotlin 内置的 chunked 方法实现
  else {
    val chunkSize = (size + n - 1) / n
    chunked(chunkSize)
  }
}

/**
 * 将序列均匀切分为 n 个子列表
 *
 * @param n 切分的数量，必须大于0
 * @return 包含 n 个子列表的列表，尽可能均匀分布元素
 * @throws IllegalArgumentException 当 n <= 0 时抛出异常
 */
fun <T> Sequence<T>.parted(n: Int): List<List<T>> = toList().parted(n)

/**
 * 将数组均匀切分为 n 个子列表
 *
 * @param n 切分的数量，必须大于0
 * @return 包含 n 个子列表的列表，尽可能均匀分布元素
 * @throws IllegalArgumentException 当 n <= 0 时抛出异常
 */
fun <T> Array<T>.parted(n: Int): List<List<T>> = toList().parted(n)
