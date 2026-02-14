package com.bangbang93.nanoda.exception

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EnsureTest :
    DescribeSpec({
      describe("ensureNotNull") {
        it("非空值时正常返回") {
          // Arrange
          val value = "test"

          // Act & Assert
          ensureNotNull(value) { "Value should not be null" }
        }

        it("空值时抛出MissingArgumentServiceException") {
          // Arrange
          val value: String? = null

          // Act & Assert
          val exception =
              shouldThrow<ServiceException.MissingArgumentServiceException> {
                ensureNotNull(value) { "Value is required" }
              }
          exception.message shouldBe "Value is required"
        }

        it("空字符串时正常返回") {
          // Arrange
          val value = ""

          // Act & Assert
          ensureNotNull(value) { "Value should not be null" }
        }

        it("零值时正常返回") {
          // Arrange
          val value = 0

          // Act & Assert
          ensureNotNull(value) { "Value should not be null" }
        }

        it("false值时正常返回") {
          // Arrange
          val value = false

          // Act & Assert
          ensureNotNull(value) { "Value should not be null" }
        }
      }

      describe("ensure") {
        it("条件为true时正常返回") {
          // Arrange
          val condition = true

          // Act & Assert
          ensure(condition) { "Condition must be true" }
        }

        it("条件为false时抛出InvalidArgumentServiceException") {
          // Arrange
          val condition = false

          // Act & Assert
          val exception =
              shouldThrow<ServiceException.InvalidArgumentServiceException> {
                ensure(condition) { "Invalid condition" }
              }
          exception.message shouldBe "Invalid condition"
        }

        it("表达式结果为true时正常返回") {
          // Arrange
          val value = 10

          // Act & Assert
          ensure(value > 5) { "Value must be greater than 5" }
        }

        it("表达式结果为false时抛出InvalidArgumentServiceException") {
          // Arrange
          val value = 3

          // Act & Assert
          val exception =
              shouldThrow<ServiceException.InvalidArgumentServiceException> {
                ensure(value > 5) { "Value must be greater than 5" }
              }
          exception.message shouldBe "Value must be greater than 5"
        }

        it("复杂条件为true时正常返回") {
          // Arrange
          val name = "John"
          val age = 25

          // Act & Assert
          ensure(name.isNotEmpty() && age >= 18) { "Invalid person data" }
        }

        it("复杂条件为false时抛出InvalidArgumentServiceException") {
          // Arrange
          val name = "John"
          val age = 15

          // Act & Assert
          val exception =
              shouldThrow<ServiceException.InvalidArgumentServiceException> {
                ensure(name.isNotEmpty() && age >= 18) { "Invalid person data" }
              }
          exception.message shouldBe "Invalid person data"
        }
      }
    })
