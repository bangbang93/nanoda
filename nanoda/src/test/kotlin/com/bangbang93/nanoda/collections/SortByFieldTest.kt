package com.bangbang93.nanoda.collections

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class SortByFieldTest :
    DescribeSpec({
      describe("Iterable.sortedByField") {
        data class Person(
            val name: String,
            val age: Int,
        )

        it("升序排序指定字段") {
          // Arrange
          val people =
              listOf(
                  Person("Alice", 30),
                  Person("Bob", 25),
                  Person("Charlie", 35),
              )

          // Act
          val result = people.sortedByField("+age")

          // Assert
          result.map { it.age } shouldBe listOf(25, 30, 35)
        }

        it("降序排序指定字段") {
          // Arrange
          val people =
              listOf(
                  Person("Alice", 30),
                  Person("Bob", 25),
                  Person("Charlie", 35),
              )

          // Act
          val result = people.sortedByField("-age")

          // Assert
          result.map { it.age } shouldBe listOf(35, 30, 25)
        }

        it("不指定排序符号时默认升序") {
          // Arrange
          val people =
              listOf(
                  Person("Charlie", 35),
                  Person("Alice", 30),
                  Person("Bob", 25),
              )

          // Act
          val result = people.sortedByField("age")

          // Assert
          result.map { it.age } shouldBe listOf(25, 30, 35)
        }

        it("按字符串字段升序排序") {
          // Arrange
          val people =
              listOf(
                  Person("Charlie", 35),
                  Person("Alice", 30),
                  Person("Bob", 25),
              )

          // Act
          val result = people.sortedByField("+name")

          // Assert
          result.map { it.name } shouldBe listOf("Alice", "Bob", "Charlie")
        }

        it("按字符串字段降序排序") {
          // Arrange
          val people =
              listOf(
                  Person("Charlie", 35),
                  Person("Alice", 30),
                  Person("Bob", 25),
              )

          // Act
          val result = people.sortedByField("-name")

          // Assert
          result.map { it.name } shouldBe listOf("Charlie", "Bob", "Alice")
        }

        it("空集合返回空列表") {
          // Arrange
          val emptyList = emptyList<Person>()

          // Act
          val result = emptyList.sortedByField("+age")

          // Assert
          result shouldBe emptyList()
        }

        it("单元素集合返回相同的列表") {
          // Arrange
          val people = listOf(Person("Alice", 30))

          // Act
          val result = people.sortedByField("+age")

          // Assert
          result shouldBe people
        }

        it("字段不存在时返回原列表") {
          // Arrange
          val people =
              listOf(
                  Person("Alice", 30),
                  Person("Bob", 25),
              )

          // Act
          val result = people.sortedByField("+nonexistent")

          // Assert
          result shouldBe people
        }

        it("包含相同字段值的元素时保持原有顺序") {
          // Arrange
          val people =
              listOf(
                  Person("Alice", 30),
                  Person("Bob", 30),
                  Person("Charlie", 25),
              )

          // Act
          val result = people.sortedByField("+age")

          // Assert
          result.map { it.name } shouldBe listOf("Charlie", "Alice", "Bob")
        }
      }
    })
