package com.bangbang93.nanoda.lmdb

import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.toJavaDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll

suspend fun main() = coroutineScope {
  (1..10)
      .map { i ->
        async(Dispatchers.Default) {
          val lmdb = Lmdb.create<String, String>("test$i.lmdb", LmdbOptions(autoDelete = true))
          lmdb.use {
            for (i in 1..1000) {
              val duration = measureTime {
                for (j in 1..20000) {
                  lmdb.put("key$i-$j", "value$i-$j")
                }
              }
              println("Iteration $i took: $duration")
            }

            println("pid: ${ProcessHandle.current().pid()}")
            Thread.sleep(3600.seconds.toJavaDuration())
          }
        }
      }
      .joinAll()
}
