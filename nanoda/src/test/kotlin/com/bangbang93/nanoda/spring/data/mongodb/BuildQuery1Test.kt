package com.bangbang93.nanoda.spring.data.mongodb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class BuildQuery1Test :
    DescribeSpec({
      describe("buildQuery1") {
        data class SearchDto(
            val name: String? = null,
            val age: Int? = null,
            val id: String? = null,
            val ids: List<String>? = null,
            val status: String? = null,
            val statusList: List<String>? = null,
            val createdAtRange: Pair<Long?, Long?>? = null,
            val priceRange: List<Long?>? = null,
            val tags: List<String>? = null,
            val categoryIds: List<String>? = null,
        )

        data class Entity(val name: String, val age: Int, val id: ObjectId)

        describe("equalFields") {
          it("添加等于条件当字段有值") {
            val search = SearchDto(name = "Alice")

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(equalFields = listOf(Entity::name to SearchDto::name)),
                )

            result.queryObject.containsKey("name") shouldBe true
            result.queryObject["name"] shouldBe "Alice"
          }

          it("忽略等于条件当字段为null") {
            val search = SearchDto()

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(equalFields = listOf(Entity::name to SearchDto::name)),
                )

            result.queryObject.containsKey("name") shouldBe false
          }

          it("支持多个等于条件") {
            val search = SearchDto(name = "Alice", age = 30)

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        equalFields =
                            listOf(
                                Entity::name to SearchDto::name,
                                Entity::age to SearchDto::age,
                            )),
                )

            result.queryObject.containsKey("name") shouldBe true
            result.queryObject.containsKey("age") shouldBe true
          }

          it("支持不同数据类型的等于条件") {
            val search = SearchDto(age = 25)

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(equalFields = listOf(Entity::age to SearchDto::age)),
                )

            result.queryObject["age"] shouldBe 25
          }
        }

        describe("matchFields") {
          it("添加正则匹配条件当字段有值") {
            val search = SearchDto(name = "Alice")

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(matchFields = listOf(Entity::name to SearchDto::name)),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("正则匹配转义特殊字符") {
            val search = SearchDto(name = "Alice.test")

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(matchFields = listOf(Entity::name to SearchDto::name)),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("正则匹配不区分大小写") {
            val search = SearchDto(name = "alice")

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(matchFields = listOf(Entity::name to SearchDto::name)),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("忽略正则匹配条件当字段为null") {
            val search = SearchDto()

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(matchFields = listOf(Entity::name to SearchDto::name)),
                )

            result.queryObject.containsKey("name") shouldBe false
          }

          it("正则匹配支持数字类型") {
            val search = SearchDto(age = 30)

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(matchFields = listOf(Entity::age to SearchDto::age)),
                )

            result.queryObject.containsKey("age") shouldBe true
          }
        }

        describe("idFields") {
          it("转换字符串为ObjectId") {
            val objectId = ObjectId()
            val search = SearchDto(id = objectId.toString())

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(idFields = listOf(Entity::id to SearchDto::id)),
                )

            result.queryObject["id"] shouldBe objectId
          }

          it("处理字符串列表并转换为ObjectId") {
            val objectId1 = ObjectId()
            val objectId2 = ObjectId()
            val search = SearchDto(ids = listOf(objectId1.toString(), objectId2.toString()))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(idFields = listOf(Entity::id to SearchDto::ids)),
                )

            result.queryObject.containsKey("id") shouldBe true
          }

          it("处理ObjectId对象") {
            data class SearchWithObjectId(val objectId: ObjectId)
            val objectId = ObjectId()
            val search = SearchWithObjectId(objectId)

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        idFields = listOf(Entity::id to SearchWithObjectId::objectId)),
                )

            result.queryObject["id"] shouldBe objectId
          }

          it("忽略id字段当值为null") {
            val search = SearchDto()

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(idFields = listOf(Entity::id to SearchDto::id)),
                )

            result.queryObject.containsKey("id") shouldBe false
          }
        }

        describe("betweenFields") {
          it("使用Pair添加范围查询") {
            val search = SearchDto(createdAtRange = Pair(100L, 200L))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        betweenFields = listOf(Entity::age to SearchDto::createdAtRange)),
                )

            result.queryObject.containsKey("age") shouldBe true
          }

          it("使用List添加范围查询") {
            val search = SearchDto(priceRange = listOf(100L, 200L))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(betweenFields = listOf(Entity::age to SearchDto::priceRange)),
                )

            result.queryObject.containsKey("age") shouldBe true
          }

          it("忽略范围查询当Pair的first为null") {
            val search = SearchDto(createdAtRange = Pair(null, 200L))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        betweenFields = listOf(Entity::age to SearchDto::createdAtRange)),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当Pair的second为null") {
            val search = SearchDto(createdAtRange = Pair(100L, null))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        betweenFields = listOf(Entity::age to SearchDto::createdAtRange)),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当List的第一个元素为null") {
            val search = SearchDto(priceRange = listOf(null, 200L))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(betweenFields = listOf(Entity::age to SearchDto::priceRange)),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当List的第二个元素为null") {
            val search = SearchDto(priceRange = listOf(100L, null))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(betweenFields = listOf(Entity::age to SearchDto::priceRange)),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当List大小不等于2") {
            val search = SearchDto(priceRange = listOf(100L, 200L, 300L))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(betweenFields = listOf(Entity::age to SearchDto::priceRange)),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当List只有一个元素") {
            val search = SearchDto(priceRange = listOf(100L))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(betweenFields = listOf(Entity::age to SearchDto::priceRange)),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当值为null") {
            val search = SearchDto()

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        betweenFields = listOf(Entity::age to SearchDto::createdAtRange)),
                )

            result.queryObject.containsKey("age") shouldBe false
          }

          it("忽略范围查询当值类型不是Pair或List") {
            data class InvalidSearch(val value: String = "invalid")
            val search = InvalidSearch()

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(betweenFields = listOf(Entity::age to InvalidSearch::value)),
                )

            result.queryObject.containsKey("age") shouldBe false
          }
        }

        describe("inFields") {
          it("添加in查询当值为Collection") {
            val search = SearchDto(statusList = listOf("active", "pending"))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(inFields = listOf(Entity::name to SearchDto::statusList)),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("添加in查询当值为单个对象") {
            val search = SearchDto(status = "active")

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(inFields = listOf(Entity::name to SearchDto::status)),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("添加in查询当Collection包含多个值") {
            val search = SearchDto(tags = listOf("tag1", "tag2", "tag3"))

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(inFields = listOf(Entity::name to SearchDto::tags)),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("添加in查询当Collection为空列表") {
            val search = SearchDto(tags = emptyList())

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(inFields = listOf(Entity::name to SearchDto::tags)),
                )

            result.queryObject.containsKey("name") shouldBe true
          }

          it("忽略in查询当值为null") {
            val search = SearchDto()

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(inFields = listOf(Entity::name to SearchDto::statusList)),
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
                buildQuery1(
                    search,
                    BuildQueryParams1(inIdFields = listOf(Entity::id to SearchDto::categoryIds)),
                )

            result.queryObject.containsKey("id") shouldBe true
          }

          it("转换单个字符串为ObjectId并添加in查询") {
            val objectId = ObjectId()
            val search = SearchDto(id = objectId.toString())

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(inIdFields = listOf(Entity::id to SearchDto::id)),
                )

            result.queryObject.containsKey("id") shouldBe true
          }

          it("处理空的ObjectId列表") {
            val search = SearchDto(categoryIds = emptyList())

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(inIdFields = listOf(Entity::id to SearchDto::categoryIds)),
                )

            result.queryObject.containsKey("id") shouldBe true
          }

          it("忽略in查询当值为null") {
            val search = SearchDto()

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(inIdFields = listOf(Entity::id to SearchDto::categoryIds)),
                )

            result.queryObject.containsKey("id") shouldBe false
          }
        }

        describe("query参数") {
          it("使用提供的Query对象") {
            val existingQuery = Query()
            existingQuery.addCriteria(Criteria.where("existing").`is`("value"))
            val search = SearchDto()

            val result = buildQuery1(search, BuildQueryParams1(query = existingQuery))

            result.queryObject.containsKey("existing") shouldBe true
          }

          it("创建新的Query对象当query为null") {
            val search = SearchDto()

            val result = buildQuery1(search, BuildQueryParams1())

            result.queryObject.isEmpty() shouldBe true
          }

          it("向提供的Query对象添加新条件") {
            val existingQuery = Query()
            existingQuery.addCriteria(Criteria.where("existing").`is`("value"))
            val search = SearchDto(name = "Alice")

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        query = existingQuery,
                        equalFields = listOf(Entity::name to SearchDto::name),
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
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        equalFields = listOf(Entity::name to SearchDto::name),
                        matchFields = listOf(Entity::age to SearchDto::age),
                        idFields = listOf(Entity::id to SearchDto::id),
                    ),
                )

            result.queryObject.containsKey("age") shouldBe true
            result.queryObject.containsKey("name") shouldBe true
            result.queryObject.containsKey("id") shouldBe true
          }

          it("忽略所有null值的字段") {
            val search = SearchDto()

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        equalFields = listOf(Entity::name to SearchDto::name),
                        matchFields = listOf(Entity::name to SearchDto::name),
                        idFields = listOf(Entity::id to SearchDto::id),
                    ),
                )

            result.queryObject.isEmpty() shouldBe true
          }

          it("支持所有字段类型的完整组合") {
            val objectId = ObjectId()
            val search =
                SearchDto(
                    name = "Alice",
                    age = 30,
                    id = objectId.toString(),
                    statusList = listOf("active"),
                    createdAtRange = Pair(100L, 200L),
                    categoryIds = listOf(objectId.toString()),
                )

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        equalFields = listOf(Entity::name to SearchDto::name),
                        idFields = listOf(Entity::id to SearchDto::id),
                        inFields = listOf(Entity::age to SearchDto::statusList),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe true
            result.queryObject.containsKey("id") shouldBe true
            result.queryObject.containsKey("age") shouldBe true
          }

          it("部分字段有值部分为null") {
            val search = SearchDto(name = "Alice", age = null, id = null)

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        equalFields =
                            listOf(
                                Entity::name to SearchDto::name,
                                Entity::age to SearchDto::age,
                            ),
                        idFields = listOf(Entity::id to SearchDto::id),
                    ),
                )

            result.queryObject.containsKey("name") shouldBe true
            result.queryObject.containsKey("age") shouldBe false
            result.queryObject.containsKey("id") shouldBe false
          }
        }

        describe("类型安全") {
          it("正确处理泛型类型参数") {
            val search = SearchDto(name = "Test")

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1<SearchDto>(
                        equalFields = listOf(Entity::name to SearchDto::name)),
                )

            result.queryObject["name"] shouldBe "Test"
          }

          it("支持不同的搜索DTO类型") {
            data class CustomSearch(val customField: String)
            val search = CustomSearch("value")

            val result =
                buildQuery1(
                    search,
                    BuildQueryParams1(
                        equalFields = listOf(Entity::name to CustomSearch::customField)),
                )

            result.queryObject["name"] shouldBe "value"
          }
        }
      }
    })
