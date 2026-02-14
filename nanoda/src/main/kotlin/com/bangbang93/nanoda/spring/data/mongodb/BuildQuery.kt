package com.bangbang93.nanoda.spring.data.mongodb

import com.bangbang93.nanoda.bson.toObjectIdList
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.inValues
import org.springframework.data.mongodb.core.query.where

typealias BuildQueryField = List<Pair<KProperty<*>, KProperty0<*>>>

data class BuildQueryParams(
    val equalFields: BuildQueryField? = null,
    val matchFields: BuildQueryField? = null,
    val idFields: BuildQueryField? = null,
    val betweenFields: BuildQueryField? = null,
    val inFields: BuildQueryField? = null,
    val inIdFields: BuildQueryField? = null,
    val query: Query? = null,
)

@Suppress("CyclomaticComplexMethod")
fun buildQuery(buildQueryParams: BuildQueryParams): Query {
  val query = buildQueryParams.query ?: Query()

  buildQueryParams.equalFields?.forEach { (doField, searchField) ->
    val value = searchField() ?: return@forEach
    query.addCriteria(where(doField).`is`(value))
  }

  buildQueryParams.matchFields?.forEach { (doField, searchField) ->
    val value = searchField() ?: return@forEach
    query.addCriteria(where(doField).regex(Regex.escape(value.toString()), "i"))
  }

  buildQueryParams.idFields?.forEach { (doField, searchField) ->
    val value = searchField() ?: return@forEach
    when (value) {
      is String -> query.addCriteria(where(doField).`is`(ObjectId(value)))
      is List<*> -> query.addCriteria(where(doField).inValues(value.toObjectIdList()))
      else -> query.addCriteria(where(doField).`is`(value))
    }
  }

  buildQueryParams.betweenFields?.forEach { (doField, searchField) ->
    val value = searchField()
    when (value) {
      is Pair<*, *> -> {
        query.addCriteria(
            where(doField).gte(value.first ?: return@forEach).lte(value.second ?: return@forEach),
        )
      }

      is List<*> -> {
        if (value.size != 2) return@forEach
        query.addCriteria(
            where(doField).gte(value[0] ?: return@forEach).lte(value[1] ?: return@forEach),
        )
      }

      else -> {
        return@forEach
      }
    }
  }

  buildQueryParams.inFields?.forEach { (doField, searchField) ->
    val value = searchField() ?: return@forEach
    when (value) {
      is Collection<*> -> query.addCriteria(where(doField).inValues(value))
      else -> query.addCriteria(where(doField).inValues(listOf(value)))
    }
  }

  buildQueryParams.inIdFields?.forEach { (doField, searchField) ->
    val value = searchField() ?: return@forEach
    when (value) {
      is Collection<*> -> query.addCriteria(where(doField).inValues(value.toObjectIdList()))
      else -> query.addCriteria(where(doField).inValues(listOf(value).toObjectIdList()))
    }
  }

  return query
}

internal fun List<*>.toObjectIdList(): List<ObjectId> =
    this.mapNotNull {
      when (it) {
        is String -> ObjectId(it)
        is ObjectId -> it
        else -> null
      }
    }
