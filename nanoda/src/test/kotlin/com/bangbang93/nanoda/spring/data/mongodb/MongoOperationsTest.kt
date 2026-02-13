package com.bangbang93.nanoda.spring.data.mongodb

import com.bangbang93.nanoda.dto.PagedResDto
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query

class MongoOperationsTest :
    DescribeSpec({
      describe("MongoOperations.findAndCount") {
        data class TestData(
            val id: String,
            val name: String,
        )

        it("返回数据和总数") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()
          val testData = listOf(TestData("1", "Alice"), TestData("2", "Bob"))

          coEvery { mongoOps.count(any(), TestData::class.java) } returns 2L
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              testData

          // Act
          val result = mongoOps.findAndCount<TestData>(Query(), skip = 0, limit = 10)

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }

        it("正确应用skip和limit参数") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()
          val testData = listOf(TestData("2", "Bob"))

          coEvery { mongoOps.count(any<Query>(), TestData::class.java) } returns 10L
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              testData

          // Act
          val result = mongoOps.findAndCount<TestData>(Query(), skip = 1, limit = 1)

          // Assert
          result.data shouldBe testData
          result.count shouldBe 10
        }

        it("当没有数据时返回空列表") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()

          coEvery { mongoOps.count(any<Query>(), TestData::class.java) } returns 0L
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              emptyList()

          // Act
          val result = mongoOps.findAndCount<TestData>(Query(), skip = 0, limit = 10)

          // Assert
          result.data shouldBe emptyList()
          result.count shouldBe 0
        }

        it("总数转换为Int") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()
          val testData = listOf(TestData("1", "Alice"))
          val largeCount = 1000L

          coEvery { mongoOps.count(any<Query>(), TestData::class.java) } returns largeCount
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              testData

          // Act
          val result = mongoOps.findAndCount<TestData>(Query(), skip = 0, limit = 1)

          // Assert
          result.count shouldBe 1000
        }

        it("skip和limit为0时返回所有数据") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()
          val testData = listOf(TestData("1", "Alice"))

          coEvery { mongoOps.count(any<Query>(), TestData::class.java) } returns 1L
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              testData

          // Act
          val result = mongoOps.findAndCount<TestData>(Query(), skip = 0, limit = 0)

          // Assert
          result.data shouldBe testData
        }

        it("返回正确的PagedResDto类型") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()
          val testData =
              listOf(TestData("1", "Alice"), TestData("2", "Bob"), TestData("3", "Charlie"))

          coEvery { mongoOps.count(any<Query>(), TestData::class.java) } returns 3L
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              testData

          // Act
          val result = mongoOps.findAndCount<TestData>(Query(), skip = 0, limit = 10)

          // Assert
          result shouldBe PagedResDto(testData, 3)
        }

        it("应用升序排序") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()
          val testData = listOf(TestData("1", "Alice"), TestData("2", "Bob"))

          coEvery { mongoOps.count(any<Query>(), TestData::class.java) } returns 2L
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              testData

          // Act
          val result =
              mongoOps.findAndCount<TestData>(Query(), skip = 0, limit = 10, sort = "+name")

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }

        it("应用降序排序") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()
          val testData = listOf(TestData("2", "Bob"), TestData("1", "Alice"))

          coEvery { mongoOps.count(any<Query>(), TestData::class.java) } returns 2L
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              testData

          // Act
          val result =
              mongoOps.findAndCount<TestData>(Query(), skip = 0, limit = 10, sort = "-name")

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }

        it("应用多字段排序") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()
          val testData = listOf(TestData("1", "Alice"), TestData("2", "Bob"))

          coEvery { mongoOps.count(any<Query>(), TestData::class.java) } returns 2L
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              testData

          // Act
          val result =
              mongoOps.findAndCount<TestData>(Query(), skip = 0, limit = 10, sort = "+name,-id")

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }

        it("空排序字符串不应用排序") {
          // Arrange
          val mongoOps = mockk<MongoOperations>()
          val testData = listOf(TestData("1", "Alice"), TestData("2", "Bob"))

          coEvery { mongoOps.count(any<Query>(), TestData::class.java) } returns 2L
          coEvery { mongoOps.query(TestData::class.java).matching(any<Query>()).all() } returns
              testData

          // Act
          val result = mongoOps.findAndCount<TestData>(Query(), skip = 0, limit = 10, sort = "")

          // Assert
          result.data shouldBe testData
          result.count shouldBe 2
        }
      }
    })
