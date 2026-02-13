package com.bangbang93.nanoda.bson

import org.bson.types.ObjectId

fun ObjectId.equalString(other: String): Boolean = this.toString() == other

fun Collection<ObjectId>.toStringList() = this.map { it.toString() }

fun Collection<String>.toObjectIdList() = this.map { ObjectId(it) }

@JvmName("toObjectIdListAny")
fun Collection<*>.toObjectIdList() =
    this.mapNotNull {
      when (it) {
        is String -> ObjectId(it)
        is ObjectId -> it
        else -> null
      }
    }
