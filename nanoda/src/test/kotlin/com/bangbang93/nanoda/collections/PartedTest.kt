package com.bangbang93.nanoda.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PartedTest :
    DescribeSpec({
      describe("Collection.parted") {
        it("splits evenly when elements divide perfectly") {
          val list = listOf(1, 2, 3, 4, 5, 6)
          val result = list.parted(3)
          result shouldBe
              listOf(
                  listOf(1, 2),
                  listOf(3, 4),
                  listOf(5, 6),
              )
        }

        it("distributes remaining elements to first partitions when not divisible") {
          val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
          val result = list.parted(3)
          result shouldBe
              listOf(
                  listOf(1, 2, 3, 4),
                  listOf(5, 6, 7, 8),
                  listOf(9, 10),
              )
        }

        it("returns single element lists when partition count equals collection size") {
          val list = listOf(1, 2, 3)
          val result = list.parted(3)
          result shouldBe
              listOf(
                  listOf(1),
                  listOf(2),
                  listOf(3),
              )
        }

        it("returns single element lists when partition count exceeds collection size") {
          val list = listOf(1, 2)
          val result = list.parted(5)
          result shouldBe
              listOf(
                  listOf(1),
                  listOf(2),
              )
        }

        it("returns empty list when collection is empty") {
          val list = emptyList<Int>()
          val result = list.parted(3)
          result shouldBe emptyList()
        }

        it("handles single element collection") {
          val list = listOf(42)
          val result = list.parted(1)
          result shouldBe listOf(listOf(42))
        }

        it("handles single element collection with multiple partitions") {
          val list = listOf(42)
          val result = list.parted(3)
          result shouldBe listOf(listOf(42))
        }

        it("works with different collection types") {
          val set = setOf(1, 2, 3, 4, 5)
          val result = set.parted(2)
          result.size shouldBe 2
          result.flatten().toSet() shouldBe set
        }

        it("throws exception when partition count is zero") {
          val list = listOf(1, 2, 3)
          shouldThrow<IllegalArgumentException> { list.parted(0) }
        }

        it("throws exception when partition count is negative") {
          val list = listOf(1, 2, 3)
          shouldThrow<IllegalArgumentException> { list.parted(-1) }
        }

        it("handles large collections efficiently") {
          val list = (1..100).toList()
          val result = list.parted(7)
          result.size shouldBe 7
          result.flatten() shouldBe list
        }
      }

      describe("Sequence.parted") {
        it("splits sequence evenly") {
          val sequence = sequenceOf(1, 2, 3, 4, 5, 6)
          val result = sequence.parted(2)
          result shouldBe
              listOf(
                  listOf(1, 2, 3),
                  listOf(4, 5, 6),
              )
        }

        it("handles empty sequence") {
          val sequence = emptySequence<Int>()
          val result = sequence.parted(3)
          result shouldBe emptyList()
        }

        it("throws exception when partition count is invalid") {
          val sequence = sequenceOf(1, 2, 3)
          shouldThrow<IllegalArgumentException> { sequence.parted(-1) }
        }
      }

      describe("Array.parted") {
        it("splits array evenly") {
          val array = arrayOf(1, 2, 3, 4, 5, 6)
          val result = array.parted(2)
          result shouldBe
              listOf(
                  listOf(1, 2, 3),
                  listOf(4, 5, 6),
              )
        }

        it("handles empty array") {
          val array = emptyArray<Int>()
          val result = array.parted(3)
          result shouldBe emptyList()
        }

        it("handles string arrays") {
          val array = arrayOf("a", "b", "c", "d", "e")
          val result = array.parted(2)
          result shouldBe
              listOf(
                  listOf("a", "b", "c"),
                  listOf("d", "e"),
              )
        }

        it("throws exception when partition count is invalid") {
          val array = arrayOf(1, 2, 3)
          shouldThrow<IllegalArgumentException> { array.parted(0) }
        }
      }
    })
