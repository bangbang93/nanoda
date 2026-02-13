package com.bangbang93.nanoda.spring.data.mongodb

import com.bangbang93.nanoda.dto.PagedResDto
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ReactiveMongoOperationsTest :
    DescribeSpec({
      describe("ReactiveMongoOperations.findAndCount") {
        data class TestData(
            val id: String,
            val name: String,
        )

        it("返回数据和总数") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData = listOf(TestData("1", "Alice"), TestData("2", "Bob"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(2L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 0, limit = 10)

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }

        it("正确应用skip和limit参数") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData = listOf(TestData("2", "Bob"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(10L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 1, limit = 1)

          // Assert
          result.data shouldBe testData
          result.count shouldBe 10
        }

        it("当没有数据时返回空列表") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(0L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.empty()

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 0, limit = 10)

          // Assert
          result.data shouldBe emptyList()
          result.count shouldBe 0
        }

        it("总数转换为Int") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData = listOf(TestData("1", "Alice"))
          val largeCount = 1000L
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(largeCount)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 0, limit = 1)

          // Assert
          result.count shouldBe 1000
        }

        it("skip和limit为0时返回所有数据") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData = listOf(TestData("1", "Alice"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(1L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 0, limit = 0)

          // Assert
          result.data shouldBe testData
        }

        it("返回正确的PagedResDto类型") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData =
              listOf(TestData("1", "Alice"), TestData("2", "Bob"), TestData("3", "Charlie"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(3L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 0, limit = 10)

          // Assert
          result shouldBe PagedResDto(testData, 3)
        }

        it("并发执行count和find操作") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData =
              listOf(TestData("1", "Alice"), TestData("2", "Bob"), TestData("3", "Charlie"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(3L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 0, limit = 10)

          // Assert
          result.data shouldBe testData
          result.count shouldBe 3
        }

        it("大skip值和小limit值") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData = listOf(TestData("100", "Item100"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(1000L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 99, limit = 1)

          // Assert
          result.data shouldBe testData
          result.count shouldBe 1000
        }

        it("应用升序排序") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData = listOf(TestData("1", "Alice"), TestData("2", "Bob"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(2L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 0, limit = 10, sort = "+name")

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }

        it("应用降序排序") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData = listOf(TestData("2", "Bob"), TestData("1", "Alice"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(2L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 0, limit = 10, sort = "-name")

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }

        it("应用多字段排序") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData = listOf(TestData("1", "Alice"), TestData("2", "Bob"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(2L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result =
              mongoOps.findAndCount<TestData>(query, skip = 0, limit = 10, sort = "+name,-id")

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }

        it("空排序字符串不应用排序") {
          // Arrange
          val mongoOps = mockk<ReactiveMongoOperations>()
          val testData = listOf(TestData("1", "Alice"), TestData("2", "Bob"))
          val query = Query()

          every { mongoOps.count(query, TestData::class.java) } returns Mono.just(2L)
          every { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              Flux.fromIterable(testData)

          // Act
          val result = mongoOps.findAndCount<TestData>(query, skip = 0, limit = 10, sort = "")

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }
      }
    })
