package com.bangbang93.nanoda.spring.data.mongodb

import com.bangbang93.nanoda.dto.PagedResDto
import com.bangbang93.nanoda.dto.parseSortSpring
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query

suspend inline fun <reified T> MongoOperations.findAndCount(
    query: Query,
    skip: Int,
    limit: Int,
    sort: String = "",
): PagedResDto<T> = coroutineScope {
  val count = async { count(query, T::class.java) }
  query.with(parseSortSpring(sort))
  val data = async { query(T::class.java).matching(query.skip(skip.toLong()).limit(limit)).all() }
  PagedResDto(data.await(), count.await().toInt())
}
