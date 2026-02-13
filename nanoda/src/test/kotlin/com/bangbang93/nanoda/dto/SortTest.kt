package com.bangbang93.nanoda.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Sort

data class TestEntity(
    val id: Long = 0,
    val name: String = "",
    val age: Int = 0,
    val email: String = "",
)

class SortTest :
    DescribeSpec({
      describe("parseSort") {
        it("升序排序单个字段") {
          // Arrange
          val input = "+name"

          // Act
          val result = parseSort(input)

          // Assert
          result shouldBe listOf("name" to 1)
        }

        it("降序排序单个字段") {
          // Arrange
          val input = "-age"

          // Act
          val result = parseSort(input)

          // Assert
          result shouldBe listOf("age" to -1)
        }

        it("不指定符号时默认升序") {
          // Arrange
          val input = "email"

          // Act
          val result = parseSort(input)

          // Assert
          result shouldBe listOf("email" to 1)
        }

        it("解析多个排序字段") {
          // Arrange
          val input = "+name,-age,email"

          // Act
          val result = parseSort(input)

          // Assert
          result shouldBe
              listOf(
                  "name" to 1,
                  "age" to -1,
                  "email" to 1,
              )
        }

        it("处理包含空格的排序字符串") {
          // Arrange
          val input = " +name , -age , email "

          // Act
          val result = parseSort(input)

          // Assert
          result shouldBe
              listOf(
                  "name" to 1,
                  "age" to -1,
                  "email" to 1,
              )
        }

        it("空字符串返回空列表") {
          // Arrange
          val input = ""

          // Act
          val result = parseSort(input)

          // Assert
          result shouldBe emptyList()
        }

        it("仅包含空格的字符串返回空列表") {
          // Arrange
          val input = "   "

          // Act
          val result = parseSort(input)

          // Assert
          result shouldBe emptyList()
        }

        it("处理多个加号符号") {
          // Arrange
          val input = "++name"

          // Act
          val result = parseSort(input)

          // Assert
          result shouldBe listOf("+name" to 1)
        }

        it("处理多个减号符号") {
          // Arrange
          val input = "--age"

          // Act
          val result = parseSort(input)

          // Assert
          result shouldBe listOf("-age" to -1)
        }
      }

      describe("parseSort with KProperty") {
        it("用KProperty过滤允许的字段") {
          // Arrange
          val input = "+name,-age,id"
          val fieldAllowList = listOf(TestEntity::name, TestEntity::age)

          // Act
          val result = parseSort(input, fieldAllowList)

          // Assert
          result shouldBe
              listOf(
                  "name" to 1,
                  "age" to -1,
              )
        }
      }

      describe("parseSortSpring with KProperty") {
        it("用KProperty过滤允许的字段") {
          // Arrange
          val input = "+name,id,-age"
          val fieldAllowList = listOf(TestEntity::name, TestEntity::age)

          // Act
          val result = parseSortSpring(input, fieldAllowList)

          // Assert
          result shouldBe
              Sort.by(
                  Sort.Order.asc("name"),
                  Sort.Order.desc("age"),
              )
        }
      }

      describe("toSpringSort") {
        it("空列表返回unsorted") {
          // Arrange
          val input = emptyList<Pair<String, Int>>()

          // Act
          val result = input.toSpringSort()

          // Assert
          result shouldBe Sort.unsorted()
        }

        it("单个升序字段") {
          // Arrange
          val input = listOf("name" to 1)

          // Act
          val result = input.toSpringSort()

          // Assert
          result shouldBe Sort.by(Sort.Order.asc("name"))
        }

        it("单个降序字段") {
          // Arrange
          val input = listOf("age" to -1)

          // Act
          val result = input.toSpringSort()

          // Assert
          result shouldBe Sort.by(Sort.Order.desc("age"))
        }

        it("多个字段升序") {
          // Arrange
          val input = listOf("name" to 1, "age" to 1)

          // Act
          val result = input.toSpringSort()

          // Assert
          result shouldBe Sort.by(Sort.Order.asc("name"), Sort.Order.asc("age"))
        }

        it("多个字段降序") {
          // Arrange
          val input = listOf("name" to -1, "age" to -1)

          // Act
          val result = input.toSpringSort()

          // Assert
          result shouldBe Sort.by(Sort.Order.desc("name"), Sort.Order.desc("age"))
        }

        it("混合升序和降序") {
          // Arrange
          val input = listOf("name" to 1, "age" to -1)

          // Act
          val result = input.toSpringSort()

          // Assert
          result shouldBe Sort.by(Sort.Order.asc("name"), Sort.Order.desc("age"))
        }

        it("方向值为0时视为升序") {
          // Arrange
          val input = listOf("name" to 0)

          // Act
          val result = input.toSpringSort()

          // Assert
          result shouldBe Sort.by(Sort.Order.asc("name"))
        }

        it("正方向值视为升序") {
          // Arrange
          val input = listOf("name" to 5)

          // Act
          val result = input.toSpringSort()

          // Assert
          result shouldBe Sort.by(Sort.Order.asc("name"))
        }

        it("负方向值视为降序") {
          // Arrange
          val input = listOf("name" to -5)

          // Act
          val result = input.toSpringSort()

          // Assert
          result shouldBe Sort.by(Sort.Order.desc("name"))
        }
      }

      describe("parseSortSpring") {
        it("空字符串返回unsorted") {
          // Arrange
          val input = ""

          // Act
          val result = parseSortSpring(input)

          // Assert
          result shouldBe Sort.unsorted()
        }

        it("单个升序字段") {
          // Arrange
          val input = "+name"

          // Act
          val result = parseSortSpring(input)

          // Assert
          result shouldBe Sort.by(Sort.Order.asc("name"))
        }

        it("单个降序字段") {
          // Arrange
          val input = "-age"

          // Act
          val result = parseSortSpring(input)

          // Assert
          result shouldBe Sort.by(Sort.Order.desc("age"))
        }

        it("多个字段的完整转换") {
          // Arrange
          val input = "+name,-age,email"

          // Act
          val result = parseSortSpring(input)

          // Assert
          result shouldBe
              Sort.by(
                  Sort.Order.asc("name"),
                  Sort.Order.desc("age"),
                  Sort.Order.asc("email"),
              )
        }

        it("处理包含空格的排序字符串") {
          // Arrange
          val input = " +name , -age "

          // Act
          val result = parseSortSpring(input)

          // Assert
          result shouldBe Sort.by(Sort.Order.asc("name"), Sort.Order.desc("age"))
        }

        it("不指定符号时默认升序") {
          // Arrange
          val input = "name,age"

          // Act
          val result = parseSortSpring(input)

          // Assert
          result shouldBe Sort.by(Sort.Order.asc("name"), Sort.Order.asc("age"))
        }

        it("混合带符号和不带符号的字段") {
          // Arrange
          val input = "+name,age,-email"

          // Act
          val result = parseSortSpring(input)

          // Assert
          result shouldBe
              Sort.by(
                  Sort.Order.asc("name"),
                  Sort.Order.asc("age"),
                  Sort.Order.desc("email"),
              )
        }

        it("仅包含空格的字符串返回unsorted") {
          // Arrange
          val input = "   "

          // Act
          val result = parseSortSpring(input)

          // Assert
          result shouldBe Sort.unsorted()
        }
      }
    })
