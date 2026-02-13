package com.bangbang93.nanoda.loader

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class CacheableLoaderTest :
    DescribeSpec({
      describe("CacheableLoader.load") {
        it("单个键的加载") {
          val loader = CacheableLoader<Int, String> { keys -> keys.map { it.toString() } }

          val result = loader.load(1)

          result shouldBe "1"
        }

        it("缓存后不再调用加载函数") {
          var callCount = 0
          val loader =
              CacheableLoader<Int, String> { keys ->
                callCount++
                keys.map { it.toString() }
              }

          loader.load(1)
          val result = loader.load(1)

          result shouldBe "1"
          callCount shouldBe 1
        }

        it("不同键进行多次加载") {
          val loader = CacheableLoader<Int, String> { keys -> keys.map { it.toString() } }

          val result1 = loader.load(1)
          val result2 = loader.load(2)

          result1 shouldBe "1"
          result2 shouldBe "2"
        }

        it("返回 null 值") {
          val loader = CacheableLoader<Int, String?> { keys -> keys.map { null } }

          val result = loader.load(1)

          result shouldBe null
        }

        it("缓存 null 值") {
          var callCount = 0
          val loader =
              CacheableLoader<Int, String?> { keys ->
                callCount++
                keys.map { null }
              }

          loader.load(1)
          loader.load(1)

          callCount shouldBe 1
        }
      }

      describe("CacheableLoader.loadMany") {
        it("批量加载多个键") {
          val loader = CacheableLoader<Int, String> { keys -> keys.map { it.toString() } }

          val result = loader.loadMany(listOf(1, 2, 3))

          result shouldContainExactly listOf("1", "2", "3")
        }

        it("返回结果顺序与请求键的顺序一致") {
          val loader = CacheableLoader<Int, String> { keys -> keys.map { it.toString() } }

          val result = loader.loadMany(listOf(3, 1, 2))

          result shouldContainExactly listOf("3", "1", "2")
        }

        it("缓存中已存在的键不再加载") {
          var callCount = 0
          val loader =
              CacheableLoader<Int, String> { keys ->
                callCount++
                keys.map { it.toString() }
              }

          loader.load(1)
          loader.loadMany(listOf(1, 2, 3))

          callCount shouldBe 2
        }

        it("包含 null 值的批量加载") {
          val loader =
              CacheableLoader<Int, String?> { keys ->
                keys.map { if (it % 2 == 0) it.toString() else null }
              }

          val result = loader.loadMany(listOf(1, 2, 3, 4))

          result shouldContainExactly listOf(null, "2", null, "4")
        }

        it("空列表的批量加载") {
          var callCount = 0
          val loader =
              CacheableLoader<Int, String> { keys ->
                callCount++
                keys.map { it.toString() }
              }

          val result = loader.loadMany(emptyList())

          result shouldBe emptyList()
          callCount shouldBe 0
        }
      }

      describe("CacheableLoader.loadMap") {
        it("批量加载返回键值对映射") {
          val loader = CacheableLoader<Int, String> { keys -> keys.map { it.toString() } }

          val result = loader.loadMap(listOf(1, 2, 3))

          result shouldBe
              mapOf(
                  1 to "1",
                  2 to "2",
                  3 to "3",
              )
        }

        it("缓存中已存在的键复用缓存值") {
          var callCount = 0
          val loader =
              CacheableLoader<Int, String> { keys ->
                callCount++
                keys.map { it.toString() }
              }

          loader.load(1)
          val result = loader.loadMap(listOf(1, 2, 3))

          result shouldBe
              mapOf(
                  1 to "1",
                  2 to "2",
                  3 to "3",
              )
          callCount shouldBe 2
        }

        it("包含 null 值的映射加载") {
          val loader =
              CacheableLoader<Int, String?> { keys ->
                keys.map { if (it % 2 == 0) it.toString() else null }
              }

          val result = loader.loadMap(listOf(1, 2, 3, 4))

          result shouldBe
              mapOf(
                  1 to null,
                  2 to "2",
                  3 to null,
                  4 to "4",
              )
        }

        it("空列表的映射加载") {
          var callCount = 0
          val loader =
              CacheableLoader<Int, String> { keys ->
                callCount++
                keys.map { it.toString() }
              }

          val result = loader.loadMap(emptyList())

          result shouldBe emptyMap()
          callCount shouldBe 0
        }
      }

      describe("CacheableLoader 加载函数异常处理") {
        it("加载函数返回大小不匹配抛出异常") {
          val loader = CacheableLoader<Int, String> { keys -> emptyList() }

          shouldThrow<IllegalStateException> { loader.load(1) }
        }

        it("多键加载时返回大小不匹配抛出异常") {
          val loader = CacheableLoader<Int, String> { keys -> listOf("1") }

          shouldThrow<IllegalStateException> { loader.loadMany(listOf(1, 2, 3)) }
        }
      }

      describe("CacheableLoader.preload") {
        it("单个键的预加载") {
          var callCount = 0
          val loader =
              CacheableLoader<Int, String> { keys ->
                callCount++
                keys.map { it.toString() }
              }

          loader.preload(1)

          callCount shouldBe 1
        }

        it("单个键预加载后缓存数据") {
          val loader = CacheableLoader<Int, String> { keys -> keys.map { it.toString() } }

          loader.preload(1)
          val result = loader.load(1)

          result shouldBe "1"
        }

        it("多个键的预加载") {
          var callCount = 0
          val loader =
              CacheableLoader<Int, String> { keys ->
                callCount++
                keys.map { it.toString() }
              }

          loader.preload(listOf(1, 2, 3))

          callCount shouldBe 1
        }

        it("批量预加载后缓存所有数据") {
          val loader = CacheableLoader<Int, String> { keys -> keys.map { it.toString() } }

          loader.preload(listOf(1, 2, 3))
          val results = loader.loadMany(listOf(1, 2, 3))

          results shouldContainExactly listOf("1", "2", "3")
        }

        it("预加载返回 null 值") {
          val loader = CacheableLoader<Int, String?> { keys -> keys.map { null } }

          loader.preload(1)
          val result = loader.load(1)

          result shouldBe null
        }

        it("预加载在 withContext 中执行") {
          var loadingDispatcher = ""
          val loader =
              CacheableLoader<Int, String> { keys ->
                loadingDispatcher = Thread.currentThread().name
                keys.map { it.toString() }
              }

          loader.preload(listOf(1, 2))

          loadingDispatcher.isNotEmpty() shouldBe true
        }
      }
    })
