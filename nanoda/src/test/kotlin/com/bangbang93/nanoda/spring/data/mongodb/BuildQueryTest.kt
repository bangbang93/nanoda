package com.bangbang93.nanoda.spring.data.mongodb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class BuildQueryTest :
    DescribeSpec({
      describe("buildQuery") {
        data class SearchDto(
            var name: String? = null,
            var age: Int? = null,
            var id: String? = null,
            var ids: List<String>? = null,
            var status: String? = null,
            var statusList: List<String>? = null,
            var createdAtRange: Pair<Long?, Long?>? = null,
            var priceRange: List<Long?>? = null,
            var tags: List<String>? = null,
            var categoryIds: List<String>? = null,
        )

        data class Entity(
            val name: String,
            val age: Int,
            val id: ObjectId,
        )

        describe("equalFields") {
          it("添加等于条件当字段有值") {
            val search = SearchDto(name = "Alice")

            val result =
                buildQuery(
                    BuildQueryParams(
                        equalFields = listOf(Entity::name to search::name),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe true
            result.queryObject["name"] shouldBe "Alice"
          }

          it("忽略等于条件当字段为null") {
            val search = SearchDto()

            val result =
                buildQuery(
                    BuildQueryParams(
                        equalFields = listOf(Entity::name to search::name),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe false
          }

          it("支持多个等于条件") {
            val search = SearchDto(name = "Alice", age = 30)

            val result =
                buildQuery(
                    BuildQueryParams(
                        equalFields =
                            listOf(
                                Entity::name to search::name,
                                Entity::age to search::age,
                            ),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe true
            result.queryObject.containsKey("age") shouldBe true
          }
        }

        describe("matchFields") {
          it("添加正则匹配条件当字段有值") {
            val search = SearchDto(name = "Alice")

            val result =
                buildQuery(
                    BuildQueryParams(
                        matchFields = listOf(Entity::name to search::name),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("正则匹配转义特殊字符") {
            val search = SearchDto(name = "Alice.test")

            val result =
                buildQuery(
                    BuildQueryParams(
                        matchFields = listOf(Entity::name to search::name),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("正则匹配不区分大小写") {
            val search = SearchDto(name = "alice")

            val result =
                buildQuery(
                    BuildQueryParams(
                        matchFields = listOf(Entity::name to search::name),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("忽略正则匹配条件当字段为null") {
            val search = SearchDto()

            val result =
                buildQuery(
                    BuildQueryParams(
                        matchFields = listOf(Entity::name to search::name),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe false
          }
        }

        describe("idFields") {
          it("转换字符串为ObjectId") {
            val objectId = ObjectId()
            val search = SearchDto(id = objectId.toString())

            val result =
                buildQuery(
                    BuildQueryParams(
                        idFields = listOf(Entity::id to search::id),
                    ),
                )

            result.queryObject["id"] shouldBe objectId
          }

          it("处理ObjectId列表") {
            val objectId1 = ObjectId()
            val objectId2 = ObjectId()
            val search = SearchDto(ids = listOf(objectId1.toString(), objectId2.toString()))

            val result =
                buildQuery(
                    BuildQueryParams(
                        idFields = listOf(Entity::id to search::ids),
                    ),
                )

            result.queryObject.containsKey("id") shouldBe true
          }

          it("处理非字符串和非列表类型") {
            data class IdHolder(
                val objectId: ObjectId = ObjectId(),
            )
            val holder = IdHolder()

            val result =
                buildQuery(
                    BuildQueryParams(
                        idFields = listOf(Entity::id to holder::objectId),
                    ),
                )

            result.queryObject["id"] shouldBe holder.objectId
          }

          it("忽略id字段当值为null") {
            val search = SearchDto()

            val result =
                buildQuery(
                    BuildQueryParams(
                        idFields = listOf(Entity::id to search::id),
                    ),
                )

            result.queryObject.containsKey("id") shouldBe false
          }
        }

        describe("betweenFields") {
          it("使用Pair添加范围查询") {
            val search = SearchDto(createdAtRange = Pair(100L, 200L))

            val result =
                buildQuery(
                    BuildQueryParams(
                        betweenFields = listOf(Entity::age to search::createdAtRange),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe true
          }

          it("使用List添加范围查询") {
            val search = SearchDto(priceRange = listOf(100L, 200L))

            val result =
                buildQuery(
                    BuildQueryParams(
                        betweenFields = listOf(Entity::age to search::priceRange),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe true
          }

          it("忽略范围查询当Pair的first为null") {
            val search = SearchDto(createdAtRange = Pair(null, 200L))

            val result =
                buildQuery(
                    BuildQueryParams(
                        betweenFields = listOf(Entity::age to search::createdAtRange),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当Pair的second为null") {
            val search = SearchDto(createdAtRange = Pair(100L, null))

            val result =
                buildQuery(
                    BuildQueryParams(
                        betweenFields = listOf(Entity::age to search::createdAtRange),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当List的第一个元素为null") {
            val search = SearchDto(priceRange = listOf(null, 200L))

            val result =
                buildQuery(
                    BuildQueryParams(
                        betweenFields = listOf(Entity::age to search::priceRange),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当List的第二个元素为null") {
            val search = SearchDto(priceRange = listOf(100L, null))

            val result =
                buildQuery(
                    BuildQueryParams(
                        betweenFields = listOf(Entity::age to search::priceRange),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当List大小不等于2") {
            val search = SearchDto(priceRange = listOf(100L, 200L, 300L))

            val result =
                buildQuery(
                    BuildQueryParams(
                        betweenFields = listOf(Entity::age to search::priceRange),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当值为null") {
            val search = SearchDto()

            val result =
                buildQuery(
                    BuildQueryParams(
                        betweenFields = listOf(Entity::age to search::createdAtRange),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当值类型不是Pair或List") {
            data class InvalidHolder(
                val value: String = "invalid",
            )
            val holder = InvalidHolder()

            val result =
                buildQuery(
                    BuildQueryParams(
                        betweenFields = listOf(Entity::age to holder::value),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe false
          }
        }

        describe("inFields") {
          it("添加in查询当值为Collection") {
            val search = SearchDto(statusList = listOf("active", "pending"))

            val result =
                buildQuery(
                    BuildQueryParams(
                        inFields = listOf(Entity::name to search::statusList),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("添加in查询当值为单个对象") {
            val search = SearchDto(status = "active")

            val result =
                buildQuery(
                    BuildQueryParams(
                        inFields = listOf(Entity::name to search::status),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("忽略in查询当值为null") {
            val search = SearchDto()

            val result =
                buildQuery(
                    BuildQueryParams(
                        inFields = listOf(Entity::name to search::statusList),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe false
          }
        }

        describe("inIdFields") {
          it("转换字符串Collection为ObjectId并添加in查询") {
            val objectId1 = ObjectId()
            val objectId2 = ObjectId()
            val search = SearchDto(categoryIds = listOf(objectId1.toString(), objectId2.toString()))

            val result =
                buildQuery(
                    BuildQueryParams(
                        inIdFields = listOf(Entity::id to search::categoryIds),
                    ),
                )

            result.queryObject.containsKey("id") shouldBe true
          }

          it("转换单个字符串为ObjectId并添加in查询") {
            val objectId = ObjectId()
            val search = SearchDto(id = objectId.toString())

            val result =
                buildQuery(
                    BuildQueryParams(
                        inIdFields = listOf(Entity::id to search::id),
                    ),
                )

            result.queryObject.containsKey("id") shouldBe true
          }

          it("忽略in查询当值为null") {
            val search = SearchDto()

            val result =
                buildQuery(
                    BuildQueryParams(
                        inIdFields = listOf(Entity::id to search::categoryIds),
                    ),
                )

            result.queryObject.containsKey("id") shouldBe false
          }
        }

        describe("query参数") {
          it("使用提供的Query对象") {
            val existingQuery = Query()
            existingQuery.addCriteria(Criteria.where("existing").`is`("value"))

            val result =
                buildQuery(
                    BuildQueryParams(
                        query = existingQuery,
                    ),
                )

            result.queryObject.containsKey("existing") shouldBe true
          }

          it("创建新的Query对象当query为null") {
            val result = buildQuery(BuildQueryParams())

            result.queryObject.isEmpty() shouldBe true
          }

          it("向提供的Query对象添加新条件") {
            val existingQuery = Query()
            existingQuery.addCriteria(Criteria.where("existing").`is`("value"))
            val search = SearchDto(name = "Alice")

            val result =
                buildQuery(
                    BuildQueryParams(
                        query = existingQuery,
                        equalFields = listOf(Entity::name to search::name),
                    ),
                )

            result.queryObject.containsKey("existing") shouldBe true
            result.queryObject.containsKey("name") shouldBe true
          }
        }

        describe("组合查询") {
          it("支持多种条件类型的组合") {
            val objectId = ObjectId()
            val search =
                SearchDto(
                    name = "Alice",
                    age = 30,
                    id = objectId.toString(),
                    createdAtRange = Pair(100L, 200L),
                )

            val result =
                buildQuery(
                    BuildQueryParams(
                        equalFields = listOf(Entity::name to search::name),
                        matchFields = listOf(Entity::age to search::age),
                        idFields = listOf(Entity::id to search::id),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe true
            result.queryObject.containsKey("name") shouldBe true
            result.queryObject.containsKey("id") shouldBe true
          }

          it("忽略所有null值的字段") {
            val search = SearchDto()

            val result =
                buildQuery(
                    BuildQueryParams(
                        equalFields = listOf(Entity::name to search::name),
                        matchFields = listOf(Entity::name to search::name),
                        idFields = listOf(Entity::id to search::id),
                    ),
                )

            result.queryObject.isEmpty() shouldBe true
          }
        }
      }
    })
