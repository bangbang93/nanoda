package com.bangbang93.nanoda.reflect

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ObjectMapTest :
    DescribeSpec({
      describe("KClass.by") {
        data class Person(
            val first: String,
            val last: String,
        )

        data class PersonWithNullable(
            val name: String,
            val nickname: String?,
        )

        data class PersonWithDefaults(
            val name: String,
            val country: String = "China",
        )

        data class PersonWithMultipleTypes(
            val name: String,
            val age: Int,
            val height: Double,
            val active: Boolean,
        )

        it("从完整的Map创建数据类") {
          // Arrange
          val map = mapOf("first" to "Henry", "last" to "O'Conner")

          // Act
          val result = Person::class.by(map)

          // Assert
          result.first shouldBe "Henry"
          result.last shouldBe "O'Conner"
        }

        it("从包含null值的Map创建数据类") {
          // Arrange
          val map = mapOf("name" to "Alice")

          // Act
          val result = PersonWithNullable::class.by(map)

          // Assert
          result.name shouldBe "Alice"
          result.nickname shouldBe null
        }

        it("从包含所有参数的Map创建带默认值的数据类") {
          // Arrange
          val map = mapOf("name" to "Bob")

          // Act
          val result = PersonWithDefaults::class.by(map)

          // Assert
          result.name shouldBe "Bob"
          result.country shouldBe "China"
        }

        it("从包含多种类型的Map创建数据类") {
          // Arrange
          val map = mapOf("name" to "Charlie", "age" to 25, "height" to 180.5, "active" to true)

          // Act
          val result = PersonWithMultipleTypes::class.by(map)

          // Assert
          result.name shouldBe "Charlie"
          result.age shouldBe 25
          result.height shouldBe 180.5
          result.active shouldBe true
        }

        it("Map中缺少必需参数时抛出异常") {
          // Arrange
          val map = mapOf("first" to "Henry")

          // Act & Assert
          shouldThrow<Exception> { Person::class.by(map) }
        }

        it("Map中包含额外的键时正常创建对象") {
          // Arrange
          val map = mapOf("first" to "Henry", "last" to "O'Conner", "extra" to "value")

          // Act
          val result = Person::class.by(map)

          // Assert
          result.first shouldBe "Henry"
          result.last shouldBe "O'Conner"
        }
      }

      describe("Any.asMap") {
        data class Person(
            val first: String,
            val last: String,
        )

        data class PersonWithAge(
            val name: String,
            val age: Int,
        )

        data class PersonWithNullable(
            val name: String,
            val nickname: String?,
        )

        data class SingleField(
            val value: String,
        )

        data class PersonWithMultipleTypes(
            val name: String,
            val age: Int,
            val height: Double,
            val active: Boolean,
        )

        it("将简单数据类转换为Map") {
          // Arrange
          val person = Person("Henry", "O'Conner")

          // Act
          val result = person.asMap()

          // Assert
          result shouldBe mapOf("first" to "Henry", "last" to "O'Conner")
        }

        it("将单字段数据类转换为Map") {
          // Arrange
          val single = SingleField("test")

          // Act
          val result = single.asMap()

          // Assert
          result shouldBe mapOf("value" to "test")
        }

        it("将包含null值的数据类转换为Map") {
          // Arrange
          val person = PersonWithNullable("Alice", null)

          // Act
          val result = person.asMap()

          // Assert
          result shouldBe mapOf("name" to "Alice", "nickname" to null)
        }

        it("将包含多种类型的数据类转换为Map") {
          // Arrange
          val person = PersonWithMultipleTypes("Charlie", 25, 180.5, true)

          // Act
          val result = person.asMap()

          // Assert
          result shouldBe
              mapOf(
                  "name" to "Charlie",
                  "age" to 25,
                  "height" to 180.5,
                  "active" to true,
              )
        }

        it("只转换public属性") {
          // Arrange
          @Suppress("UNUSED_PARAMETER", "unused")
          class PersonWithPrivate(
              val name: String,
              private val secret: String,
          ) {
            @Suppress("unused") fun getSecret() = secret
          }
          val person = PersonWithPrivate("Alice", "secret123")

          // Act
          val result = person.asMap()

          // Assert
          result shouldBe mapOf("name" to "Alice")
          result.keys.contains("secret") shouldBe false
        }

        it("转换包含空字符串的数据类") {
          // Arrange
          val person = Person("", "")

          // Act
          val result = person.asMap()

          // Assert
          result shouldBe mapOf("first" to "", "last" to "")
        }

        it("转换包含零值的数据类") {
          // Arrange
          val person = PersonWithAge("Bob", 0)

          // Act
          val result = person.asMap()

          // Assert
          result shouldBe mapOf("name" to "Bob", "age" to 0)
        }
      }

      describe("KClass.by 和 Any.asMap 的往返转换") {
        data class Person(
            val first: String,
            val last: String,
        )

        data class PersonWithNullable(
            val name: String,
            val nickname: String?,
        )

        data class PersonWithMultipleTypes(
            val name: String,
            val age: Int,
            val height: Double,
            val active: Boolean,
        )

        it("对象转Map再转回对象保持一致") {
          // Arrange
          val original = Person("Henry", "O'Conner")

          // Act
          val map = original.asMap()
          val result = Person::class.by(map)

          // Assert
          result shouldBe original
        }

        it("包含null值的对象往返转换保持一致") {
          // Arrange
          val original = PersonWithNullable("Alice", null)

          // Act
          val map = original.asMap()
          val result = PersonWithNullable::class.by(map)

          // Assert
          result shouldBe original
        }

        it("包含多种类型的对象往返转换保持一致") {
          // Arrange
          val original = PersonWithMultipleTypes("Charlie", 25, 180.5, true)

          // Act
          val map = original.asMap()
          val result = PersonWithMultipleTypes::class.by(map)

          // Assert
          result shouldBe original
        }
      }
    })
