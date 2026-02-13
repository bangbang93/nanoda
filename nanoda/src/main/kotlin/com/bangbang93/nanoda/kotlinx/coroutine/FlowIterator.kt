package com.bangbang93.nanoda.kotlinx.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FlowIterator<T>(
    flow: Flow<T>,
) : CoroutineScope by CoroutineScope(Dispatchers.IO), Iterator<T> {
  sealed class FlowValue<out T> {
    data class Value<T>(
        val value: T,
    ) : FlowValue<T>()

    object Done : FlowValue<Nothing>()
  }

  private val channel = Channel<FlowValue<T>>()
  private var nextValue: FlowValue<T>? = null
  private var done = false

  init {
    launch {
      flow.collect { value -> channel.send(FlowValue.Value(value)) }
      channel.send(FlowValue.Done)
      channel.close()
    }
    fetchNext()
  }

  private fun fetchNext() {
    runBlocking {
      nextValue = channel.receiveCatching().getOrNull()
      done = nextValue is FlowValue.Done
    }
  }

  override fun hasNext(): Boolean = !done

  override fun next(): T {
    if (done) throw NoSuchElementException()
    val value = nextValue
    fetchNext()
    return when (value) {
      is FlowValue.Value -> value.value
      else -> throw NoSuchElementException()
    }
  }
}

fun <T> Flow<T>.asIterator(): FlowIterator<T> = FlowIterator(this)
