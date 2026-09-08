package com.bangbang93.nanoda.spring.data.mongodb.aggregation

import com.bangbang93.nanoda.dto.PagedDto
import com.bangbang93.nanoda.spring.data.mongodb.asc
import com.bangbang93.nanoda.spring.data.mongodb.desc
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import reactor.core.publisher.Flux

class AggregationTest :
    DescribeSpec({
      data class Address(val city: String)

      data class User(
          val _id: String,
          val name: String,
          val age: Int,
          val status: String,
          val dept: String,
          val price: Double,
          val createdAt: Long,
          val tags: List<String>,
          val address: Address,
      )

      data class Order(val id: String, val userId: String)

      fun TypedAggregation<*>.pipeline() = toPipeline(Aggregation.DEFAULT_CONTEXT)

      describe("match") {
        it("复用 Criteria DSL 构建 \$match 阶段") {
          val pipeline = aggregation<User> { match { "name" eq "Alice" } }.pipeline()

          pipeline shouldBe listOf(Document("\$match", Document("name", "Alice")))
        }

        it("多个条件以 \$and 合并") {
          val pipeline =
              aggregation<User> {
                    match {
                      "name" eq "Alice"
                      User::age gte 18
                    }
                  }
                  .pipeline()

          val match = pipeline.single()["\$match"] as Document
          (match["\$and"] as List<*>).size shouldBe 2
        }

        it("无条件时不追加 \$match 阶段") {
          val scope = AggregationScope().apply { match {} }

          scope.build().isEmpty() shouldBe true
        }
      }

      describe("sort") {
        it("属性降序与字段名升序组合") {
          val pipeline = aggregation<User> { sort(desc(User::createdAt), asc("name")) }.pipeline()

          pipeline shouldBe listOf(Document("\$sort", Document("createdAt", -1).append("name", 1)))
        }
      }

      describe("skip 与 limit") {
        it("设置偏移量与条数") {
          val pipeline =
              aggregation<User> {
                    skip(20)
                    limit(10)
                  }
                  .pipeline()

          pipeline shouldBe listOf(Document("\$skip", 20L), Document("\$limit", 10L))
        }

        it("limit 为 0 时不追加阶段（聚合管道中 \$limit: 0 非法）") {
          val scope = AggregationScope().apply { limit(0) }

          scope.build().isEmpty() shouldBe true
        }

        it("limit 为负数时不追加阶段") {
          val scope = AggregationScope().apply { limit(-1) }

          scope.build().isEmpty() shouldBe true
        }
      }

      describe("project") {
        it("include 仅包含给定属性") {
          val pipeline = aggregation<User> { project { include(User::name, User::age) } }.pipeline()

          pipeline shouldBe listOf(Document("\$project", Document("name", 1).append("age", 1)))
        }

        it("exclude 排除给定属性") {
          val pipeline = aggregation<User> { project { exclude(User::tags) } }.pipeline()

          pipeline shouldBe listOf(Document("\$project", Document("tags", 0)))
        }

        it("include 与 exclude 都为空时不追加阶段") {
          val scope = AggregationScope().apply { project {} }

          scope.build().isEmpty() shouldBe true
        }
      }

      describe("group") {
        it("单分组键与 count 累加器") {
          val pipeline =
              aggregation<User> { group(User::status) { count() alias "cnt" } }.pipeline()

          pipeline shouldBe
              listOf(
                  Document(
                      "\$group", Document("_id", "\$status").append("cnt", Document("\$sum", 1))))
        }

        it("多分组键组合为复合 _id") {
          val pipeline =
              aggregation<User> {
                    group(User::status, User::dept) { sum(User::price) alias "total" }
                  }
                  .pipeline()

          pipeline shouldBe
              listOf(
                  Document(
                      "\$group",
                      Document("_id", Document("status", "\$status").append("dept", "\$dept"))
                          .append("total", Document("\$sum", "\$price"))))
        }

        it("字段名分组键") {
          val pipeline = aggregation<User> { group("status") { count() alias "cnt" } }.pipeline()

          pipeline shouldBe
              listOf(
                  Document(
                      "\$group", Document("_id", "\$status").append("cnt", Document("\$sum", 1))))
        }

        it("支持全部累加器") {
          val pipeline =
              aggregation<User> {
                    group(User::status) {
                      count() alias "cnt"
                      sum(User::price) alias "total"
                      avg(User::price) alias "avgPrice"
                      min(User::age) alias "minAge"
                      max(User::age) alias "maxAge"
                      first(User::name) alias "firstName"
                      last(User::name) alias "lastName"
                      push(User::name) alias "names"
                      addToSet(User::name) alias "uniqueNames"
                    }
                  }
                  .pipeline()

          val group = pipeline.single()["\$group"] as Document
          group["cnt"] shouldBe Document("\$sum", 1)
          group["total"] shouldBe Document("\$sum", "\$price")
          group["avgPrice"] shouldBe Document("\$avg", "\$price")
          group["minAge"] shouldBe Document("\$min", "\$age")
          group["maxAge"] shouldBe Document("\$max", "\$age")
          group["firstName"] shouldBe Document("\$first", "\$name")
          group["lastName"] shouldBe Document("\$last", "\$name")
          group["names"] shouldBe Document("\$push", "\$name")
          group["uniqueNames"] shouldBe Document("\$addToSet", "\$name")
        }

        it("未指定 alias 的累加器不生效") {
          val pipeline = aggregation<User> { group(User::status) { count() } }.pipeline()

          val group = pipeline.single()["\$group"] as Document
          group.keys shouldBe setOf("_id")
        }
      }

      describe("unwind") {
        it("展开数组字段") {
          val pipeline = aggregation<User> { unwind(User::tags) }.pipeline()

          pipeline shouldBe listOf(Document("\$unwind", "\$tags"))
        }

        it("保留空数组与缺失字段的文档") {
          val pipeline =
              aggregation<User> { unwind(User::tags, preserveNullAndEmpty = true) }.pipeline()

          pipeline shouldBe
              listOf(
                  Document(
                      "\$unwind",
                      Document("path", "\$tags").append("preserveNullAndEmptyArrays", true)))
        }
      }

      describe("lookup") {
        it("属性引用关联字段") {
          val pipeline =
              aggregation<User> { lookup("orders", User::_id, Order::userId, "orders") }.pipeline()

          pipeline shouldBe
              listOf(
                  Document(
                      "\$lookup",
                      Document("from", "orders")
                          .append("localField", "_id")
                          .append("foreignField", "userId")
                          .append("as", "orders")))
        }

        it("字段名关联字段") {
          val pipeline =
              aggregation<User> { lookup("orders", "_id", "userId", "orders") }.pipeline()

          pipeline shouldBe
              listOf(
                  Document(
                      "\$lookup",
                      Document("from", "orders")
                          .append("localField", "_id")
                          .append("foreignField", "userId")
                          .append("as", "orders")))
        }
      }

      describe("replaceRoot") {
        it("属性引用的子文档作为新根文档") {
          val pipeline = aggregation<User> { replaceRoot(User::address) }.pipeline()

          pipeline shouldBe listOf(Document("\$replaceRoot", Document("newRoot", "\$address")))
        }

        it("字段名的子文档作为新根文档") {
          val pipeline = aggregation<User> { replaceRoot("address") }.pipeline()

          pipeline shouldBe listOf(Document("\$replaceRoot", Document("newRoot", "\$address")))
        }
      }

      describe("addFields") {
        it("from 引用属性字段值") {
          val pipeline = aggregation<User> { addFields { "id" from User::_id } }.pipeline()

          pipeline shouldBe listOf(Document("\$addFields", Document("id", "\$_id")))
        }

        it("from 引用嵌套字段路径") {
          val pipeline = aggregation<User> { addFields { "city" from "address.city" } }.pipeline()

          pipeline shouldBe listOf(Document("\$addFields", Document("city", "\$address.city")))
        }

        it("value 赋常量值") {
          val pipeline = aggregation<User> { addFields { "source" value "import" } }.pipeline()

          pipeline shouldBe listOf(Document("\$addFields", Document("source", "import")))
        }

        it("expr 支持 AggregationExpression 逃生舱") {
          val pipeline =
              aggregation<User> {
                    addFields { "agePlusOne" expr (ArithmeticOperators.valueOf("age").add(1)) }
                  }
                  .pipeline()

          val addFields = pipeline.single()["\$addFields"] as Document
          addFields["agePlusOne"] shouldBe Document("\$add", listOf("\$age", 1))
        }

        it("多种取值方式组合") {
          val pipeline =
              aggregation<User> {
                    addFields {
                      "id" from User::_id
                      "source" value "import"
                    }
                  }
                  .pipeline()

          pipeline shouldBe
              listOf(Document("\$addFields", Document("id", "\$_id").append("source", "import")))
        }
      }

      describe("facet") {
        it("paged 输出 data 与 total 分支") {
          val dto =
              PagedDto().apply {
                page = 3
                limit = 10
              }

          val pipeline = aggregation<User> { facet { paged(dto) } }.pipeline()

          pipeline shouldBe
              listOf(
                  Document(
                      "\$facet",
                      Document("data", listOf(Document("\$skip", 20L), Document("\$limit", 10L)))
                          .append("total", listOf(Document("\$count", "value")))))
        }

        it("branch 渲染通用命名分支") {
          val pipeline =
              aggregation<User> {
                    facet {
                      branch("byTag") {
                        unwind(User::tags)
                        group(User::tags) { count() alias "count" }
                        sort(desc("count"))
                      }
                    }
                  }
                  .pipeline()

          pipeline shouldBe
              listOf(
                  Document(
                      "\$facet",
                      Document(
                          "byTag",
                          listOf(
                              Document("\$unwind", "\$tags"),
                              Document(
                                  "\$group",
                                  Document("_id", "\$tags").append("count", Document("\$sum", 1))),
                              Document("\$sort", Document("count", -1))))))
        }

        it("paged 与 branch 按调用顺序共存") {
          val dto =
              PagedDto().apply {
                page = 2
                limit = 5
              }

          val pipeline =
              aggregation<User> {
                    facet {
                      paged(dto)
                      branch("names") { project { include(User::name) } }
                    }
                  }
                  .pipeline()

          (pipeline.single()["\$facet"] as Document).keys.toList() shouldBe
              listOf("data", "total", "names")
        }
      }

      describe("raw") {
        it("追加未封装的阶段") {
          val pipeline =
              aggregation<User> {
                    raw(Aggregation.stage(Document("\$sample", Document("size", 3))))
                  }
                  .pipeline()

          pipeline shouldBe listOf(Document("\$sample", Document("size", 3)))
        }
      }

      describe("阶段顺序") {
        it("按调用顺序排列管道阶段") {
          val pipeline =
              aggregation<User> {
                    match { "status" eq "ACTIVE" }
                    sort(desc(User::createdAt))
                    skip(20)
                    limit(10)
                    project { include(User::name) }
                  }
                  .pipeline()

          pipeline.map { it.keys.first() } shouldBe
              listOf("\$match", "\$sort", "\$skip", "\$limit", "\$project")
        }
      }

      describe("MongoOperations.aggregate") {
        it("使用 DSL 构建并执行聚合") {
          val mongoOps = mockk<MongoOperations>()
          val users =
              listOf(User("1", "Alice", 20, "A", "dev", 9.9, 100L, listOf("x"), Address("sh")))
          val captured = slot<TypedAggregation<User>>()

          every { mongoOps.aggregate(capture(captured), User::class.java) } returns
              AggregationResults(users, Document())

          val result =
              mongoOps.aggregate<User, User> {
                match { "status" eq "A" }
                sort(desc(User::createdAt))
              }

          result.mappedResults shouldBe users
          captured.captured.toPipeline(Aggregation.DEFAULT_CONTEXT).map { it.keys.first() } shouldBe
              listOf("\$match", "\$sort")
        }

        it("以 TypedAggregation 执行聚合") {
          val mongoOps = mockk<MongoOperations>()
          val users =
              listOf(User("1", "Alice", 20, "A", "dev", 9.9, 100L, listOf("x"), Address("sh")))

          every { mongoOps.aggregate(any<TypedAggregation<User>>(), User::class.java) } returns
              AggregationResults(users, Document())

          val result =
              mongoOps.aggregate<User, User>(aggregation<User> { match { "status" eq "A" } })

          result.mappedResults shouldBe users
        }
      }

      describe("ReactiveMongoOperations.aggregate") {
        it("使用 DSL 构建并执行响应式聚合，结果转为 Flow") {
          val mongoOps = mockk<ReactiveMongoOperations>()
          val users =
              listOf(User("1", "Alice", 20, "A", "dev", 9.9, 100L, listOf("x"), Address("sh")))

          every { mongoOps.aggregate(any<TypedAggregation<User>>(), User::class.java) } returns
              Flux.fromIterable(users)

          val result = mongoOps.aggregate<User, User> { match { "status" eq "A" } }.toList()

          result shouldBe users
        }
      }

      describe("MongoOperations.aggregatePaged") {
        data class SimpleUser(val name: String, val age: Int)

        fun converter(): MappingMongoConverter =
            MappingMongoConverter(NoOpDbRefResolver.INSTANCE, MongoMappingContext()).apply {
              afterPropertiesSet()
            }

        it("解析 facet 结果为分页数据与总数") {
          val mongoOps = mockk<MongoOperations>()
          val facetResult =
              Document(
                      "data",
                      listOf(Document("name", "Alice").append("age", 20)),
                  )
                  .append("total", listOf(Document("value", 42)))

          every {
            mongoOps.aggregate(any<TypedAggregation<SimpleUser>>(), Document::class.java)
          } returns AggregationResults(listOf(facetResult), Document())
          every { mongoOps.converter } returns converter()

          val result =
              mongoOps.aggregatePaged<SimpleUser, SimpleUser> {
                match { "age" gte 18 }
                facet { paged(PagedDto().apply { limit = 10 }) }
              }

          result.data shouldBe listOf(SimpleUser("Alice", 20))
          result.count shouldBe 42
        }

        it("total 分支为空数组时总数为 0") {
          val mongoOps = mockk<MongoOperations>()
          val facetResult =
              Document("data", emptyList<Document>()).append("total", emptyList<Document>())

          every {
            mongoOps.aggregate(any<TypedAggregation<SimpleUser>>(), Document::class.java)
          } returns AggregationResults(listOf(facetResult), Document())
          every { mongoOps.converter } returns converter()

          val result =
              mongoOps.aggregatePaged<SimpleUser, SimpleUser> {
                facet { paged(PagedDto().apply { limit = 10 }) }
              }

          result.data shouldBe emptyList()
          result.count shouldBe 0
        }
      }
    })
