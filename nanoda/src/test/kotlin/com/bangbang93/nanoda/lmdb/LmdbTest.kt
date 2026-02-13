package com.bangbang93.nanoda.lmdb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.equals.shouldBeEqual
import java.io.File
import java.time.Instant
import kotlin.random.Random
import kotlin.time.measureTime
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class LmdbTest :
    DescribeSpec({
      beforeTest {
        // Clean up the LMDB file after tests
        val lmdbFile = "test.lmdb"
        val file = File(lmdbFile)
        if (file.exists()) {
          file.delete()
        }

        // cleanup lock file
        val lockFile = File("$lmdbFile-lock")
        if (lockFile.exists()) {
          lockFile.delete()
        }
      }

      it("should write to lmdb") {
        val lmdb = Lmdb.create<String, String>("test.lmdb")
        lmdb.use { lmdb.put("key1", "value1") }
      }

      it("should read from lmdb") {
        val lmdb = Lmdb.create<String, String>("test.lmdb")
        lmdb.use {
          val value = lmdb["key1"]
          println("Retrieved value: $value")
        }
      }

      it("should iterate over all entries") {
        println("pid: ${ProcessHandle.current().pid()}")
        val duration = measureTime {
          val lmdb = Lmdb.create<String, String>("test.lmdb")
          val max = 1000000
          lmdb.use {
            for (i in 1..max) {
              lmdb.put("key$i", "value$i")
            }
            lmdb.flush()

            val flow = lmdb.asFlow()
            var counter = 0
            flow.collect { entry -> counter++ }
            counter shouldBeEqual max
          }
        }
        println("Iteration took: $duration")
      }

      it("should iterate correctly") {
        val lmdb = Lmdb.create<String, String>("test.lmdb")
        val max = 10
        val keys = mutableSetOf<String>()
        lmdb.use {
          for (i in 1..max) {
            val key = "key$i"
            lmdb["key$i"] = "value$i"
            keys.add(key)
          }

          delay(lmdb.options.flushDelay - 10)
          val iterator = lmdb.iterator()
          var count = 0
          val resultKeys = mutableSetOf<String>()
          while (iterator.hasNext()) {
            val entry = iterator.next()
            println("Key: ${entry.key}, Value: ${entry.value}")
            keys shouldContain entry.key
            resultKeys.add(entry.key)
            count++
          }
          keys shouldBeEqual resultKeys
        }
      }

      it("should resize the database") {
        val lmdb =
            Lmdb.create<String, String>(
                "small.lmdb",
                LmdbOptions(mapSize = 1L * 1024 * 1024, autoDelete = true),
            )
        lmdb.use {
          for (i in 1..100000) {
            lmdb.put("key$i", "value$i")
          }
          lmdb.flush()
          println("Database resized successfully.")
        }
      }

      it("should handle cache size limit") {
        val lmdb = Lmdb.create<String, String>("test.lmdb", LmdbOptions(maxCacheSize = 10))
        lmdb.use {
          for (i in 1..10000) {
            lmdb["key$i"] = "value$i"
          }
        }
      }

      it("should compact the database") {
        val lmdb = Lmdb.create<String, String>("test.lmdb", LmdbOptions(enableCompression = true))
        lmdb.use {
          for (i in 1..1000) {
            lmdb.put("key${Random.nextInt()}", "value$i")
          }
          lmdb.flushSync()
          println("Before compact: ${File(lmdb.path).length()}")

          val compactFile = "test_compact.lmdb"
          lmdb.compact(compactFile)
          println("After compact: ${File(compactFile).length()}")
          File(compactFile).delete()
        }
      }

      it("should handle parallel writes") {
        val lmdb =
            Lmdb.create<String, String>(
                "test.lmdb",
                LmdbOptions(maxCacheSize = 1000, mapSize = 1L * 1024 * 1024, autoDelete = true),
            )
        val max = 10_000L
        val repeat = 100
        lmdb.use {
          coroutineScope {
            repeat(repeat) { i ->
              async {
                for (j in 1..max) {
                  lmdb["key${i * max + j}"] = "value${i * max + j}"
                }
              }
            }
          }
          lmdb.flush()

          lmdb.env.txnRead().use { lmdb.defaultDb.stat(it).entries } shouldBeEqual max * repeat
        }
      }

      it("should handle complex objects as values") {
        data class Data(val instant: Instant = Instant.now())

        val lmdb = Lmdb.create<String, Data>("test.lmdb")
        lmdb.use {
          lmdb.put("1", Data())
          lmdb.flush()

          val data = lmdb["1"]
          println("Retrieved Person: $data")
        }
      }

      describe("containsKey") {
        it("缓存中存在的键应该返回true") {
          // Arrange
          val lmdb = Lmdb.create<String, String>("test.lmdb")
          lmdb.use {
            lmdb.put("key1", "value1")

            // Act
            val result = lmdb.containsKey("key1")

            // Assert
            result shouldBeEqual true
          }
        }

        it("数据库中存在但缓存中不存在的键应该返回true") {
          // Arrange
          val lmdb = Lmdb.create<String, String>("test.lmdb")
          lmdb.use {
            lmdb.put("key1", "value1")
            lmdb.flushSync()

            // Act
            val result = lmdb.containsKey("key1")

            // Assert
            result shouldBeEqual true
          }
        }

        it("不存在的键应该返回false") {
          // Arrange
          val lmdb = Lmdb.create<String, String>("test.lmdb")
          lmdb.use {
            // Act
            val result = lmdb.containsKey("nonexistent")

            // Assert
            result shouldBeEqual false
          }
        }
      }

      describe("isEmpty") {
        it("空数据库和空缓存应该返回true") {
          // Arrange
          val lmdb = Lmdb.create<String, String>("test.lmdb")
          lmdb.use {
            // Act
            val result = lmdb.isEmpty()

            // Assert
            result shouldBeEqual true
          }
        }

        it("缓存中有数据时应该返回false") {
          // Arrange
          val lmdb = Lmdb.create<String, String>("test.lmdb")
          lmdb.use {
            lmdb.put("key1", "value1")

            // Act
            val result = lmdb.isEmpty()

            // Assert
            result shouldBeEqual false
          }
        }

        it("数据库中有数据但缓存为空时应该返回false") {
          // Arrange
          val lmdb = Lmdb.create<String, String>("test.lmdb")
          lmdb.use {
            lmdb.put("key1", "value1")
            lmdb.flushSync()

            // Act
            val result = lmdb.isEmpty()

            // Assert
            result shouldBeEqual false
          }
        }
      }
    })
