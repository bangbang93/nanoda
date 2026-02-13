package com.bangbang93.nanoda.flow

import kotlinx.coroutines.flow.Flow

suspend fun <T, K> Flow<T>.associateBy(keySelector: suspend (T) -> K): Map<K, T> {
  val map = mutableMapOf<K, T>()
  collect { value ->
    val key = keySelector(value)
    map[key] = value
  }
  return map
}
