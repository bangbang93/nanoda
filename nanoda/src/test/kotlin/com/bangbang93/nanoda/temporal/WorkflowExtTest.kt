package com.bangbang93.nanoda.temporal

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.temporal.activity.ActivityOptions
import io.temporal.activity.LocalActivityOptions
import io.temporal.workflow.Workflow
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

interface TestActivity {
  fun doSomething(): String
}

class WorkflowExtTest :
    DescribeSpec({
      describe("newLocalActivityStub") {
        it("使用默认选项创建LocalActivityStub") {
          // Arrange
          val mockActivity = mockk<TestActivity>()
          mockkStatic(Workflow::class)
          every { Workflow.newLocalActivityStub(TestActivity::class.java, null) } returns
              mockActivity

          // Act
          val result = newLocalActivityStub<TestActivity>()

          // Assert
          result shouldBe mockActivity
          verify(exactly = 1) { Workflow.newLocalActivityStub(TestActivity::class.java, null) }
        }

        it("使用自定义LocalActivityOptions创建LocalActivityStub") {
          // Arrange
          val mockActivity = mockk<TestActivity>()
          val options =
              LocalActivityOptions.newBuilder()
                  .setScheduleToCloseTimeout(5.seconds.toJavaDuration())
                  .build()
          mockkStatic(Workflow::class)
          every { Workflow.newLocalActivityStub(TestActivity::class.java, options) } returns
              mockActivity

          // Act
          val result = newLocalActivityStub<TestActivity>(options)

          // Assert
          result shouldBe mockActivity
          verify(exactly = 1) { Workflow.newLocalActivityStub(TestActivity::class.java, options) }
        }

        it("使用builder lambda创建LocalActivityStub") {
          // Arrange
          val mockActivity = mockk<TestActivity>()
          mockkStatic(Workflow::class)
          every {
            Workflow.newLocalActivityStub(TestActivity::class.java, any<LocalActivityOptions>())
          } returns mockActivity

          // Act
          val result =
              newLocalActivityStub<TestActivity> {
                setScheduleToCloseTimeout(10.seconds.toJavaDuration())
              }

          // Assert
          result shouldBe mockActivity
          verify(exactly = 1) {
            Workflow.newLocalActivityStub(
                TestActivity::class.java,
                match<LocalActivityOptions> { it.scheduleToCloseTimeout.toMillis() == 10000L },
            )
          }
        }
      }

      describe("newActivityStub") {
        it("使用默认选项创建ActivityStub") {
          // Arrange
          val mockActivity = mockk<TestActivity>()
          mockkStatic(Workflow::class)
          every { Workflow.newActivityStub(TestActivity::class.java, null) } returns mockActivity

          // Act
          val result = newActivityStub<TestActivity>()

          // Assert
          result shouldBe mockActivity
          verify(exactly = 1) { Workflow.newActivityStub(TestActivity::class.java, null) }
        }

        it("使用自定义ActivityOptions创建ActivityStub") {
          // Arrange
          val mockActivity = mockk<TestActivity>()
          val options =
              ActivityOptions.newBuilder()
                  .setScheduleToCloseTimeout(30.seconds.toJavaDuration())
                  .build()
          mockkStatic(Workflow::class)
          every { Workflow.newActivityStub(TestActivity::class.java, options) } returns mockActivity

          // Act
          val result = newActivityStub<TestActivity>(options)

          // Assert
          result shouldBe mockActivity
          verify(exactly = 1) { Workflow.newActivityStub(TestActivity::class.java, options) }
        }

        it("使用builder lambda创建ActivityStub") {
          // Arrange
          val mockActivity = mockk<TestActivity>()
          mockkStatic(Workflow::class)
          every {
            Workflow.newActivityStub(TestActivity::class.java, any<ActivityOptions>())
          } returns mockActivity

          // Act
          val result =
              newActivityStub<TestActivity> {
                setScheduleToCloseTimeout(60.seconds.toJavaDuration())
              }

          // Assert
          result shouldBe mockActivity
          verify(exactly = 1) {
            Workflow.newActivityStub(
                TestActivity::class.java,
                match<ActivityOptions> { it.scheduleToCloseTimeout.toMillis() == 60000L },
            )
          }
        }
      }
    })
