package com.bangbang93.nanoda.spring.data.mongodb

import com.bangbang93.nanoda.dto.PagedResDto
import com.bangbang93.nanoda.dto.parseSortSpring
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Query

suspend inline fun <reified T> ReactiveMongoOperations.findAndCount(
    query: Query,
    skip: Int,
    limit: Int,
    sort: String = "",
): PagedResDto<T> = coroutineScope {
  val count = async { count(query, T::class.java).awaitSingle() }
  query.with(parseSortSpring(sort))
  val data = async {
    query(T::class.java)
        .matching(query.skip(skip.toLong()).limit(limit))
        .all()
        .collectList()
        .awaitSingle()
  }
  PagedResDto(data.await(), count.await().toInt())
}
