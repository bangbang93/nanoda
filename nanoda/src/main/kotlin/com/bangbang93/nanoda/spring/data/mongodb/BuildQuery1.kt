package com.bangbang93.nanoda.spring.data.mongodb

import com.bangbang93.nanoda.bson.toObjectIdList
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.inValues
import org.springframework.data.mongodb.core.query.where
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

typealias BuildQueryField1<TSearch> = List<Pair<KProperty<*>, KProperty1<TSearch, Any?>>>

data class BuildQueryParams1<TSearch>(
    val equalFields: BuildQueryField1<TSearch>? = null,
    val matchFields: BuildQueryField1<TSearch>? = null,
    val idFields: BuildQueryField1<TSearch>? = null,
    val betweenFields: BuildQueryField1<TSearch>? = null,
    val inFields: BuildQueryField1<TSearch>? = null,
    val inIdFields: BuildQueryField1<TSearch>? = null,
    val query: Query? = null,
)

@Suppress("CyclomaticComplexMethod")
fun <TSearch> buildQuery1(
    search: TSearch,
    buildQueryParams: BuildQueryParams1<TSearch>,
): Query {
  val query = buildQueryParams.query ?: Query()

  buildQueryParams.equalFields?.forEach { (doField, searchField) ->
    val value = searchField(search) ?: return@forEach
    query.addCriteria(where(doField).`is`(value))
  }

  buildQueryParams.matchFields?.forEach { (doField, searchField) ->
    val value = searchField(search) ?: return@forEach
    query.addCriteria(where(doField).regex(Regex.escape(value.toString()), "i"))
  }

  buildQueryParams.idFields?.forEach { (doField, searchField) ->
    val value = searchField(search) ?: return@forEach
    when (value) {
      is String -> query.addCriteria(where(doField).`is`(ObjectId(value)))
      is Collection<*> -> query.addCriteria(where(doField).inValues(value.toObjectIdList()))
      else -> query.addCriteria(where(doField).`is`(value))
    }
  }

  buildQueryParams.betweenFields?.forEach { (doField, searchField) ->
    val value = searchField(search)
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
    val value = searchField(search) ?: return@forEach
    when (value) {
      is Collection<*> -> query.addCriteria(where(doField).inValues(value))
      else -> query.addCriteria(where(doField).inValues(listOf(value)))
    }
  }

  buildQueryParams.inIdFields?.forEach { (doField, searchField) ->
    val value = searchField(search) ?: return@forEach
    when (value) {
      is Collection<*> -> query.addCriteria(where(doField).inValues(value.toObjectIdList()))
      else -> query.addCriteria(where(doField).inValues(listOf(value).toObjectIdList()))
    }
  }

  return query
}
