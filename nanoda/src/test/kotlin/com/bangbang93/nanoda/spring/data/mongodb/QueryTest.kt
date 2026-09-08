package com.bangbang93.nanoda.spring.data.mongodb

import com.bangbang93.nanoda.dto.PagedDto
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query

class QueryTest :
    DescribeSpec({
      describe("query") {
        data class User(val id: Long, val name: String, val age: Int, val createdAt: Long)

        describe("where") {
          it("复用 Criteria DSL 构建条件") {
            val q = query {
              where {
                "name" eq "Alice"
                User::age gte 18
              }
            }

            (q.queryObject["\$and"] as List<*>).size shouldBe 2
          }

          it("多次调用累积合并条件") {
            val q = query {
              where { "name" eq "Alice" }
              where { User::age gte 18 }
            }

            (q.queryObject["\$and"] as List<*>).size shouldBe 2
          }

          it("无条件时不添加 criteria") {
            val q = query { where {} }

            q.queryObject.isEmpty() shouldBe true
          }
        }

        describe("skip 与 limit") {
          it("设置偏移量与条数") {
            val q = query {
              skip = 20
              limit = 10
            }

            q.skip shouldBe 20L
            q.limit shouldBe 10
          }

          it("未设置时保持默认") {
            val q = query {}

            q.skip shouldBe 0L
            q.limit shouldBe 0
          }
        }

        describe("page") {
          it("按 PagedDto 计算分页") {
            val dto =
                PagedDto().apply {
                  page = 3
                  limit = 10
                }

            val q = query { page(dto) }

            q.skip shouldBe 20L
            q.limit shouldBe 10
          }
        }

        describe("sortedBy") {
          it("属性降序与字段名升序组合") {
            val q = query { sortedBy(desc(User::createdAt), asc("name")) }

            q.sortObject["createdAt"] shouldBe -1
            q.sortObject["name"] shouldBe 1
          }
        }

        describe("投影") {
          it("only 仅包含给定属性") {
            val q = query { only(User::name, User::age) }

            q.fieldsObject["name"] shouldBe 1
            q.fieldsObject["age"] shouldBe 1
            q.fieldsObject.containsKey("id") shouldBe false
          }

          it("exclude 排除给定属性") {
            val q = query { exclude(User::id) }

            q.fieldsObject["id"] shouldBe 0
          }
        }

        describe("comment 与 hint") {
          it("设置查询注释") {
            val q = query { comment = "req-123" }

            q.meta.comment shouldBe "req-123"
          }

          it("设置索引提示") {
            val q = query { hint = "idx_status_createdAt" }

            q.hint shouldBe "idx_status_createdAt"
          }
        }

        describe("findAndCount") {
          it("使用 DSL 构建查询并返回分页结果") {
            val mongoOps = mockk<MongoOperations>()
            val users = listOf(User(1, "Alice", 20, 100L))
            val captured = slot<Query>()

            coEvery { mongoOps.count(any(), User::class.java) } returns 42L
            coEvery { mongoOps.query(User::class.java).matching(capture(captured)).all() } returns
                users

            val dto =
                PagedDto().apply {
                  page = 2
                  limit = 10
                }
            val result =
                mongoOps.findAndCount<User> {
                  where { User::age gte 18 }
                  page(dto)
                  sortedBy(desc(User::createdAt))
                }

            result.data shouldBe users
            result.count shouldBe 42
            captured.captured.skip shouldBe 10L
            captured.captured.limit shouldBe 10
            captured.captured.sortObject["createdAt"] shouldBe -1
          }
        }
      }
    })
