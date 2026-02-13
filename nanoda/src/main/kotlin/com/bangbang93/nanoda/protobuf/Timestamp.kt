@file:Suppress("TooManyFunctions")

package com.bangbang93.nanoda.protobuf

import com.google.protobuf.Timestamp
import com.google.protobuf.timestamp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime

fun Instant.toProtobufTimestamp(): Timestamp = timestamp {
  seconds = epochSeconds
  nanos = nanosecondsOfSecond
}

fun Timestamp.toInstant(): Instant = Instant.fromEpochSeconds(this.seconds, this.nanos)

fun LocalDateTime.toProtobufTimestamp(tz: TimeZone = TimeZone.currentSystemDefault()): Timestamp {
  val instant = this.toInstant(tz)
  return timestamp {
    seconds = instant.epochSeconds
    nanos = instant.nanosecondsOfSecond
  }
}

fun Timestamp.toLocalDateTime(tz: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime =
    this.toInstant().toLocalDateTime(tz)

fun LocalDate.toProtobufTimestamp(tz: TimeZone = TimeZone.currentSystemDefault()): Timestamp {
  val instant = this.atStartOfDayIn(tz)
  return timestamp {
    seconds = instant.epochSeconds
    nanos = instant.nanosecondsOfSecond
  }
}

fun Timestamp.toLocalDate(tz: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    this.toLocalDateTime(tz).date

fun java.util.Date.toProtobufTimestamp(): Timestamp {
  val instant = this.toInstant()
  return instant.toProtobufTimestamp()
}

fun java.time.LocalDate.toProtobufTimestamp(): Timestamp =
    this.toKotlinLocalDate().toProtobufTimestamp()

fun java.time.LocalDateTime.toProtobufTimestamp(): Timestamp =
    this.toKotlinLocalDateTime().toProtobufTimestamp()

fun java.time.Instant.toProtobufTimestamp(): Timestamp =
    this.toKotlinInstant().toProtobufTimestamp()

fun Timestamp.toJavaLocalDate(): java.time.LocalDate = this.toLocalDate().toJavaLocalDate()

fun Timestamp.toJavaLocalDateTime(): java.time.LocalDateTime =
    this.toLocalDateTime().toJavaLocalDateTime()

fun Timestamp.toJavaInstant(): java.time.Instant = this.toInstant().toJavaInstant()

fun Timestamp.toJavaDate(): java.util.Date = java.util.Date.from(this.toJavaInstant())
