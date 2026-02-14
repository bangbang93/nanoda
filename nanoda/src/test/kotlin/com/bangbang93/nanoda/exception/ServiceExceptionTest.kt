package com.bangbang93.nanoda.exception

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ServiceExceptionTest :
    DescribeSpec({
      describe("UnknownServiceException") {
        it("创建异常时设置正确的属性") {
          // Arrange & Act
          val exception = ServiceException.UnknownServiceException("Unknown error occurred")

          // Assert
          exception.message shouldBe "Unknown error occurred"
          exception.code shouldBe "COMMON_UNKNOWN"
          exception.httpCode shouldBe 500
          exception.data shouldBe null
          exception.`$isServiceError` shouldBe true
        }

        it("创建异常时携带数据") {
          // Arrange
          val data: Map<Any, Any> = mapOf("key" to "value")

          // Act
          val exception = ServiceException.UnknownServiceException("Error with data", data = data)

          // Assert
          exception.data shouldBe data
        }
      }

      describe("NeedLoginServiceException") {
        it("创建异常时设置正确的属性") {
          // Arrange & Act
          val exception = ServiceException.NeedLoginServiceException("Login required")

          // Assert
          exception.message shouldBe "Login required"
          exception.code shouldBe "COMMON_NEED_LOGIN"
          exception.httpCode shouldBe 401
          exception.`$isServiceError` shouldBe true
        }
      }

      describe("NotFoundServiceException") {
        it("创建异常时设置正确的属性") {
          // Arrange & Act
          val exception = ServiceException.NotFoundServiceException("Resource not found")

          // Assert
          exception.message shouldBe "Resource not found"
          exception.code shouldBe "COMMON_NOT_FOUND"
          exception.httpCode shouldBe 404
          exception.`$isServiceError` shouldBe true
        }
      }

      describe("PermissionDeniedServiceException") {
        it("创建异常时设置正确的属性") {
          // Arrange & Act
          val exception = ServiceException.PermissionDeniedServiceException("Permission denied")

          // Assert
          exception.message shouldBe "Permission denied"
          exception.code shouldBe "COMMON_PERMISSION_DENIED"
          exception.httpCode shouldBe 403
          exception.`$isServiceError` shouldBe true
        }
      }

      describe("MissingArgumentServiceException") {
        it("创建异常时设置正确的属性") {
          // Arrange & Act
          val exception = ServiceException.MissingArgumentServiceException("Argument missing")

          // Assert
          exception.message shouldBe "Argument missing"
          exception.code shouldBe "COMMON_MISSING_ARGUMENT"
          exception.httpCode shouldBe 400
          exception.`$isServiceError` shouldBe true
        }
      }

      describe("InvalidArgumentServiceException") {
        it("创建异常时设置正确的属性") {
          // Arrange & Act
          val exception = ServiceException.InvalidArgumentServiceException("Invalid argument")

          // Assert
          exception.message shouldBe "Invalid argument"
          exception.code shouldBe "COMMON_INVALID_ARGUMENT"
          exception.httpCode shouldBe 400
          exception.`$isServiceError` shouldBe true
        }
      }

      describe("ServiceException继承关系") {
        it("所有异常都是ServiceException的子类") {
          // Arrange & Act
          val exceptions =
              listOf(
                  ServiceException.UnknownServiceException("test"),
                  ServiceException.NeedLoginServiceException("test"),
                  ServiceException.NotFoundServiceException("test"),
                  ServiceException.PermissionDeniedServiceException("test"),
                  ServiceException.MissingArgumentServiceException("test"),
                  ServiceException.InvalidArgumentServiceException("test"),
              )

          // Assert
          exceptions.forEach { exception ->
            exception.shouldBeInstanceOf<ServiceException>()
            exception.`$isServiceError` shouldBe true
          }
        }

        it("所有异常都是Exception的子类") {
          // Arrange & Act
          val exception = ServiceException.UnknownServiceException("test")

          // Assert
          exception.shouldBeInstanceOf<Exception>()
        }
      }

      describe("异常携带cause") {
        it("创建异常时可以传递原始异常") {
          // Arrange
          val originalException = RuntimeException("Original error")

          // Act
          val exception =
              ServiceException.UnknownServiceException(
                  "Wrapped error",
                  cause = originalException,
              )

          // Assert
          exception.message shouldBe "Wrapped error"
        }
      }

      describe("异常携带复杂数据") {
        it("data可以包含多个键值对") {
          // Arrange
          val data: Map<Any, Any> =
              mapOf(
                  "userId" to 123,
                  "action" to "delete",
                  "resource" to "file.txt",
              )

          // Act
          val exception =
              ServiceException.PermissionDeniedServiceException(
                  "Cannot delete file",
                  data = data,
              )

          // Assert
          exception.data shouldBe data
          exception.data?.get("userId") shouldBe 123
          exception.data?.get("action") shouldBe "delete"
        }

        it("data可以为空") {
          // Arrange & Act
          val exception = ServiceException.NotFoundServiceException("Not found", data = null)

          // Assert
          exception.data shouldBe null
        }
      }
    })
