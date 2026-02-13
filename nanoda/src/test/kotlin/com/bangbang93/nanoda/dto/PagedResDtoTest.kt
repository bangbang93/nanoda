package com.bangbang93.nanoda.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PagedResDtoTest :
    DescribeSpec({
      describe("PagedResDto 构造函数") {
        it("使用数据和计数创建实例") {
          // Arrange
          val data = listOf("item1", "item2", "item3")
          val count = 100

          // Act
          val result = PagedResDto(data, count)

          // Assert
          result.data shouldBe data
          result.count shouldBe count
        }

        it("空数据列表创建实例") {
          // Arrange
          val data = emptyList<String>()
          val count = 0

          // Act
          val result = PagedResDto(data, count)

          // Assert
          result.data shouldBe emptyList()
          result.count shouldBe 0
        }

        it("大数据集合创建实例") {
          // Arrange
          val data = (1..1000).map { "item$it" }
          val count = 1000

          // Act
          val result = PagedResDto(data, count)

          // Assert
          result.data.size shouldBe 1000
          result.count shouldBe 1000
        }

        it("负数计数创建实例") {
          // Arrange
          val data = listOf("item1")
          val count = -1

          // Act
          val result = PagedResDto(data, count)

          // Assert
          result.count shouldBe -1
        }
      }

      describe("PagedResDto.fromInt") {
        it("使用整数计数创建实例") {
          // Arrange
          val data = listOf("a", "b", "c")
          val count = 50

          // Act
          val result = PagedResDto.fromInt(data, count)

          // Assert
          result.data shouldBe data
          result.count shouldBe 50
        }

        it("空数据使用fromInt") {
          // Arrange
          val data = emptyList<Int>()
          val count = 0

          // Act
          val result = PagedResDto.fromInt(data, count)

          // Assert
          result.data shouldBe emptyList()
          result.count shouldBe 0
        }

        it("最大整数值") {
          // Arrange
          val data = listOf(1)
          val count = Int.MAX_VALUE

          // Act
          val result = PagedResDto.fromInt(data, count)

          // Assert
          result.count shouldBe Int.MAX_VALUE
        }
      }

      describe("PagedResDto.fromLong") {
        it("使用长整数计数创建实例") {
          // Arrange
          val data = listOf("x", "y", "z")
          val count = 10000L

          // Act
          val result = PagedResDto.fromLong(data, count)

          // Assert
          result.data shouldBe data
          result.count shouldBe 10000
        }

        it("长整数转换为整数") {
          // Arrange
          val data = listOf("item")
          val count = 5000000L

          // Act
          val result = PagedResDto.fromLong(data, count)

          // Assert
          result.count shouldBe 5000000
        }

        it("空数据使用fromLong") {
          // Arrange
          val data = emptyList<String>()
          val count = 100L

          // Act
          val result = PagedResDto.fromLong(data, count)

          // Assert
          result.data shouldBe emptyList()
          result.count shouldBe 100
        }

        it("长整数超出整数范围") {
          // Arrange
          val data = listOf("item")
          val count = (Int.MAX_VALUE.toLong()) + 1

          // Act
          val result = PagedResDto.fromLong(data, count)

          // Assert
          result.count shouldBe (Int.MAX_VALUE + 1)
        }
      }

      describe("PagedResDto equals") {
        it("相同的数据和计数相等") {
          // Arrange
          val dto1 = PagedResDto(listOf("a", "b"), 10)
          val dto2 = PagedResDto(listOf("a", "b"), 10)

          // Act & Assert
          dto1 shouldBe dto2
        }

        it("不同的数据不相等") {
          // Arrange
          val dto1 = PagedResDto(listOf("a", "b"), 10)
          val dto2 = PagedResDto(listOf("c", "d"), 10)

          // Act & Assert
          (dto1 == dto2) shouldBe false
        }

        it("不同的计数不相等") {
          // Arrange
          val dto1 = PagedResDto(listOf("a", "b"), 10)
          val dto2 = PagedResDto(listOf("a", "b"), 20)

          // Act & Assert
          (dto1 == dto2) shouldBe false
        }

        it("自身相等") {
          // Arrange
          val dto = PagedResDto(listOf("item"), 5)

          // Act & Assert
          dto shouldBe dto
        }

        it("与不同类型比较") {
          // Arrange
          val dto = PagedResDto(listOf("a"), 1)
          val other: Any = "not a dto"

          // Act & Assert
          (dto == other) shouldBe false
        }

        it("空数据的两个实例相等") {
          // Arrange
          val dto1 = PagedResDto(emptyList<String>(), 0)
          val dto2 = PagedResDto(emptyList<String>(), 0)

          // Act & Assert
          dto1 shouldBe dto2
        }
      }

      describe("PagedResDto hashCode") {
        it("相等对象有相同的哈希码") {
          // Arrange
          val dto1 = PagedResDto(listOf("a", "b"), 10)
          val dto2 = PagedResDto(listOf("a", "b"), 10)

          // Act & Assert
          dto1.hashCode() shouldBe dto2.hashCode()
        }

        it("可用于哈希集合") {
          // Arrange
          val dto1 = PagedResDto(listOf("a"), 5)
          val dto2 = PagedResDto(listOf("a"), 5)
          val dto3 = PagedResDto(listOf("b"), 5)

          // Act
          val set = hashSetOf(dto1, dto2, dto3)

          // Assert
          set.size shouldBe 2
        }

        it("不同对象通常有不同的哈希码") {
          // Arrange
          val dto1 = PagedResDto(listOf("a"), 1)
          val dto2 = PagedResDto(listOf("b"), 2)

          // Act & Assert
          (dto1.hashCode() != dto2.hashCode()) shouldBe true
        }
      }

      describe("PagedResDto IPaged接口") {
        it("实现IPaged接口") {
          // Arrange
          val dto: Any = PagedResDto(listOf(1, 2, 3), 10)

          // Act & Assert
          (dto is PagedResDto<*>) shouldBe true
        }

        it("提供data属性") {
          // Arrange
          val data = listOf("x", "y", "z")
          val dto = PagedResDto(data, 100)

          // Act & Assert
          dto.data shouldBe data
        }

        it("提供count属性") {
          // Arrange
          val dto = PagedResDto(listOf("item"), 42)

          // Act & Assert
          dto.count shouldBe 42
        }
      }

      describe("PagedResDto 泛型") {
        it("支持不同类型的泛型") {
          // Arrange
          val stringDto = PagedResDto(listOf("a", "b"), 2)
          val intDto = PagedResDto(listOf(1, 2, 3), 3)
          val anyDto = PagedResDto(listOf(mapOf("key" to "value")), 1)

          // Act & Assert
          stringDto.data.first() shouldBe "a"
          intDto.data.first() shouldBe 1
          anyDto.data.first() shouldBe mapOf("key" to "value")
        }

        it("可转换为其他PagedResDto类型") {
          // Arrange
          val intDto = PagedResDto(listOf(1, 2, 3), 3)

          // Act
          val mappedData = intDto.data.map { it.toString() }
          val stringDto = PagedResDto(mappedData, intDto.count)

          // Assert
          stringDto.data shouldBe listOf("1", "2", "3")
        }
      }
    })
