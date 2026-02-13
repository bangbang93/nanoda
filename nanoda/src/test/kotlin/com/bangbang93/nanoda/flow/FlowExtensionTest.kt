package com.bangbang93.nanoda.flow

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

enum class Status {
  ACTIVE,
  INACTIVE,
  PENDING,
}

class FlowExtensionTest :
    DescribeSpec({
      describe("Flow.associateBy") {
        data class User(val id: Int, val name: String, val email: String)

        it("将Flow元素关联到Map当使用简单键选择器") {
          // Arrange
          val users =
              flowOf(
                  User(1, "Alice", "alice@example.com"),
                  User(2, "Bob", "bob@example.com"),
                  User(3, "Charlie", "charlie@example.com"),
              )

          // Act
          val result = users.associateBy { it.id }

          // Assert
          result.size shouldBe 3
          result[1] shouldBe User(1, "Alice", "alice@example.com")
          result[2] shouldBe User(2, "Bob", "bob@example.com")
          result[3] shouldBe User(3, "Charlie", "charlie@example.com")
        }

        it("使用字符串键关联元素") {
          // Arrange
          val users =
              flowOf(User(1, "Alice", "alice@example.com"), User(2, "Bob", "bob@example.com"))

          // Act
          val result = users.associateBy { it.name }

          // Assert
          result.size shouldBe 2
          result["Alice"] shouldBe User(1, "Alice", "alice@example.com")
          result["Bob"] shouldBe User(2, "Bob", "bob@example.com")
        }

        it("返回空Map当Flow为空") {
          // Arrange
          val emptyUsers = emptyFlow<User>()

          // Act
          val result = emptyUsers.associateBy { it.id }

          // Assert
          result.shouldBeEmpty()
        }

        it("保留最后一个值当键重复") {
          // Arrange
          val users =
              flowOf(
                  User(1, "Alice", "alice@example.com"),
                  User(1, "Alice Updated", "alice.new@example.com"),
                  User(2, "Bob", "bob@example.com"),
              )

          // Act
          val result = users.associateBy { it.id }

          // Assert
          result.size shouldBe 2
          result[1] shouldBe User(1, "Alice Updated", "alice.new@example.com")
          result[2] shouldBe User(2, "Bob", "bob@example.com")
        }

        it("处理单个元素的Flow") {
          // Arrange
          val singleUser = flowOf(User(1, "Alice", "alice@example.com"))

          // Act
          val result = singleUser.associateBy { it.id }

          // Assert
          result.size shouldBe 1
          result[1] shouldBe User(1, "Alice", "alice@example.com")
        }

        it("支持挂起函数作为键选择器") {
          // Arrange
          val users =
              flowOf(User(1, "Alice", "alice@example.com"), User(2, "Bob", "bob@example.com"))

          // Act
          val result =
              users.associateBy { user ->
                kotlinx.coroutines.delay(1)
                user.name.uppercase()
              }

          // Assert
          result.size shouldBe 2
          result["ALICE"] shouldBe User(1, "Alice", "alice@example.com")
          result["BOB"] shouldBe User(2, "Bob", "bob@example.com")
        }

        it("处理复杂的键选择器逻辑") {
          // Arrange
          val users =
              flowOf(
                  User(1, "Alice", "alice@example.com"),
                  User(2, "Bob", "bob@example.com"),
                  User(3, "Charlie", "charlie@example.com"),
              )

          // Act
          val result = users.associateBy { "${it.name}-${it.id}" }

          // Assert
          result.size shouldBe 3
          result["Alice-1"] shouldBe User(1, "Alice", "alice@example.com")
          result["Bob-2"] shouldBe User(2, "Bob", "bob@example.com")
          result["Charlie-3"] shouldBe User(3, "Charlie", "charlie@example.com")
        }

        it("处理包含null键的情况") {
          // Arrange
          data class Item(val id: Int, val name: String?)
          val items = flowOf(Item(1, "Item1"), Item(2, null), Item(3, "Item3"))

          // Act
          val result = items.associateBy { it.name }

          // Assert
          result.size shouldBe 3
          result["Item1"] shouldBe Item(1, "Item1")
          result[null] shouldBe Item(2, null)
          result["Item3"] shouldBe Item(3, "Item3")
        }

        it("处理大量元素") {
          // Arrange
          val largeFlow =
              flowOf(*(1..1000).map { User(it, "User$it", "user$it@example.com") }.toTypedArray())

          // Act
          val result = largeFlow.associateBy { it.id }

          // Assert
          result.size shouldBe 1000
          result[1] shouldBe User(1, "User1", "user1@example.com")
          result[500] shouldBe User(500, "User500", "user500@example.com")
          result[1000] shouldBe User(1000, "User1000", "user1000@example.com")
        }

        it("处理包含相同值的不同键") {
          // Arrange
          val users =
              flowOf(
                  User(1, "Alice", "alice@example.com"),
                  User(2, "Alice", "alice2@example.com"),
                  User(3, "Alice", "alice3@example.com"),
              )

          // Act
          val result = users.associateBy { it.id }

          // Assert
          result.size shouldBe 3
          result[1]?.name shouldBe "Alice"
          result[2]?.name shouldBe "Alice"
          result[3]?.name shouldBe "Alice"
        }

        it("使用计算属性作为键") {
          // Arrange
          val users =
              flowOf(User(1, "Alice", "alice@example.com"), User(2, "Bob", "bob@example.com"))

          // Act
          val result = users.associateBy { it.email.substringBefore("@") }

          // Assert
          result.size shouldBe 2
          result["alice"] shouldBe User(1, "Alice", "alice@example.com")
          result["bob"] shouldBe User(2, "Bob", "bob@example.com")
        }

        it("处理所有元素具有相同键的情况") {
          // Arrange
          val users =
              flowOf(
                  User(1, "Alice", "alice@example.com"),
                  User(2, "Bob", "bob@example.com"),
                  User(3, "Charlie", "charlie@example.com"),
              )

          // Act
          val result = users.associateBy { "same-key" }

          // Assert
          result.size shouldBe 1
          result["same-key"] shouldBe User(3, "Charlie", "charlie@example.com")
        }

        it("支持数字类型作为键") {
          // Arrange
          data class Product(val id: Int, val price: Double)
          val products = flowOf(Product(1, 10.5), Product(2, 20.0), Product(3, 15.75))

          // Act
          val result = products.associateBy { it.price }

          // Assert
          result.size shouldBe 3
          result[10.5] shouldBe Product(1, 10.5)
          result[20.0] shouldBe Product(2, 20.0)
          result[15.75] shouldBe Product(3, 15.75)
        }

        it("处理布尔值作为键") {
          // Arrange
          data class Task(val id: Int, val completed: Boolean)
          val tasks = flowOf(Task(1, true), Task(2, false), Task(3, true))

          // Act
          val result = tasks.associateBy { it.completed }

          // Assert
          result.size shouldBe 2
          result[true] shouldBe Task(3, true)
          result[false] shouldBe Task(2, false)
        }

        it("处理枚举类型作为键") {
          // Arrange
          data class Order(val id: Int, val status: Status)
          val orders =
              flowOf(
                  Order(1, Status.ACTIVE),
                  Order(2, Status.INACTIVE),
                  Order(3, Status.PENDING),
              )

          // Act
          val result = orders.associateBy { it.status }

          // Assert
          result.size shouldBe 3
          result[Status.ACTIVE] shouldBe Order(1, Status.ACTIVE)
          result[Status.INACTIVE] shouldBe Order(2, Status.INACTIVE)
          result[Status.PENDING] shouldBe Order(3, Status.PENDING)
        }

        it("处理Pair作为键") {
          // Arrange
          data class Location(val city: String, val country: String, val population: Int)
          val locations =
              flowOf(
                  Location("Tokyo", "Japan", 14000000),
                  Location("Paris", "France", 2200000),
                  Location("New York", "USA", 8400000),
              )

          // Act
          val result = locations.associateBy { it.city to it.country }

          // Assert
          result.size shouldBe 3
          result["Tokyo" to "Japan"] shouldBe Location("Tokyo", "Japan", 14000000)
          result["Paris" to "France"] shouldBe Location("Paris", "France", 2200000)
          result["New York" to "USA"] shouldBe Location("New York", "USA", 8400000)
        }
      }
    })
