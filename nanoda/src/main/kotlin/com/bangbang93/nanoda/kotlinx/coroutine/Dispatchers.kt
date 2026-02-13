package com.bangbang93.nanoda.kotlinx.coroutine

import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher

/** A [CoroutineDispatcher] that uses virtual threads. */
val Dispatchers.VIRTUAL: CoroutineDispatcher
  get() = VirtualDispatcherHolder.dispatcher

private object VirtualDispatcherHolder {
  val dispatcher: CoroutineDispatcher by lazy {
    val factory = Thread.ofVirtual().name("VirtualDispatcher-", 0).factory()
    Executors.newThreadPerTaskExecutor(factory).asCoroutineDispatcher()
  }
}
