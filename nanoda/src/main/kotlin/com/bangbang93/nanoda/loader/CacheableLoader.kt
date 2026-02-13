package com.bangbang93.nanoda.loader

import kotlin.collections.firstOrNull

class CacheableLoader<K, V>(
    private val loadFn: suspend (List<K>) -> List<V?>,
) {
  private val cache = mutableMapOf<K, V?>()

  suspend fun load(key: K): V? {
    if (cache.contains(key)) {
      return cache[key]
    }

    val result = invokeLoadFn(listOf(key)).firstOrNull()
    cache[key] = result
    return result
  }

  suspend fun loadMany(keys: List<K>): List<V?> {
    val map = loadMap(keys)
    return keys.map { map[it] }
  }

  suspend fun loadMap(keys: List<K>): Map<K, V?> {
    val resultMap = mutableMapOf<K, V?>()
    val keysToLoad = mutableListOf<K>()

    for (key in keys) {
      if (cache.containsKey(key)) {
        resultMap[key] = cache[key]
      } else {
        keysToLoad.add(key)
      }
    }

    if (keysToLoad.isNotEmpty()) {
      val loadedValues = invokeLoadFn(keysToLoad)
      keysToLoad.forEachIndexed { i, key ->
        val value = loadedValues[i]
        cache[key] = value
        resultMap[key] = value
      }
    }

    return resultMap
  }

  suspend fun preload(key: K) {
    invokeLoadFn(listOf(key))
  }

  suspend fun preload(keys: List<K>) {
    invokeLoadFn(keys)
  }

  private suspend fun invokeLoadFn(keys: List<K>): List<V?> {
    val res = loadFn(keys)
    check(res.size == keys.size) {
      "Load function returned a list of size ${res.size}, but expected ${keys.size}"
    }
    return res
  }
}
