package com.bangbang93.nanoda.kotlinx.coroutine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test

class FlowIteratorTest {
  @Test
  fun emitsAllValuesIncludingNull() {
    val flow = flowOf("a", null, "b")
    val iterator = FlowIterator(flow)
    iterator.hasNext() shouldBe true
    iterator.next() shouldBe "a"
    iterator.hasNext() shouldBe true
    iterator.next().shouldBeNull()
    iterator.hasNext() shouldBe true
    iterator.next() shouldBe "b"
    iterator.hasNext() shouldBe false
  }

  @Test
  fun throwsOnNextAfterEnd() {
    val flow = flowOf(1, 2)
    val iterator = FlowIterator(flow)
    iterator.next()
    iterator.next()
    iterator.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> { iterator.next() }
  }

  @Test
  fun worksWithEmptyFlow() {
    val iterator = FlowIterator(emptyFlow<String>())
    iterator.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> { iterator.next() }
  }
}
