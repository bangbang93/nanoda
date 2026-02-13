package com.bangbang93.nanoda.lmdb

import io.kotest.matchers.equals.shouldBeEqual
import kotlin.test.Test
import kotlin.time.measureTime
import kotlinx.coroutines.runBlocking

class LmdbPerformanceTest {
  @Test
  fun testIteratorLarge(): Unit = runBlocking {
    val lmdb = Lmdb.create<String, String>("testIteratorLarge.lmdb", LmdbOptions.withAutoDelete())
    val max = 13000
    val keys = mutableSetOf<String>()
    lmdb.use {
      for (i in 1..max) {
        val key = "key$i"
        lmdb["key$i"] = "value$i"
        keys.add(key)
      }

      println("put finish")

      val iterator = lmdb.iterator()
      var count = 0
      val resultKeys = mutableSetOf<String>()
      while (iterator.hasNext()) {
        val entry = iterator.next()
        resultKeys.add(entry.key)
        count++
      }
      keys shouldBeEqual resultKeys
    }
  }

  @Test
  fun testLarge() {
    val lmdb =
        Lmdb.create<String, String>(
            "testLarge.lmdb", LmdbOptions(maxCacheSize = 100000, autoDelete = true))
    lmdb.use {
      for (i in 1..1000) {
        val duration = measureTime {
          for (j in 1..20000) {
            lmdb.put("key$i-$j", "value$i-$j")
          }
        }
        println("Iteration $i took: $duration")
      }
    }
  }

  @Test
  fun testReverseLarge() {
    val lmdb =
        Lmdb.create<String, String>(
            "test.lmdb", LmdbOptions(maxCacheSize = 100000, autoDelete = true))
    lmdb.use {
      for (i in 1000 downTo 1) {
        val duration = measureTime {
          for (j in 20000 downTo 1) {
            lmdb.put("key$i-$j", "value$i-$j")
          }
        }
        println("Iteration $i took: $duration")
      }
    }
  }
}
