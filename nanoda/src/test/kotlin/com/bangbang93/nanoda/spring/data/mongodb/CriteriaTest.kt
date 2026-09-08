package com.bangbang93.nanoda.spring.data.mongodb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.regex.Pattern
import org.bson.Document
import org.springframework.data.mongodb.core.query.Criteria

class CriteriaTest :
    DescribeSpec({
      describe("criteria") {
        data class User(
            val name: String,
            val age: Int,
            val status: String,
            val nickname: String? = null,
        )

        describe("eq") {
          it("字符串字段等于") {
            val c = criteria { "name" eq "Alice" }

            c.criteriaObject["name"] shouldBe "Alice"
          }

          it("属性引用等于") {
            val c = criteria { User::name eq "Alice" }

            c.criteriaObject["name"] shouldBe "Alice"
          }

          it("null 值匹配 null 或缺失字段") {
            val c = criteria { "deletedAt" eq null }

            c.criteriaObject["deletedAt"] shouldBe null
          }

          it("可空属性允许 eq null") {
            val c = criteria { User::nickname eq null }

            c.criteriaObject["nickname"] shouldBe null
          }
        }

        describe("ne") {
          it("不等于给定值") {
            val c = criteria { "status" ne "DISABLED" }

            (c.criteriaObject["status"] as Document)["\$ne"] shouldBe "DISABLED"
          }

          it("ne null 匹配非空字段") {
            val c = criteria { "deletedAt" ne null }

            (c.criteriaObject["deletedAt"] as Document)["\$ne"] shouldBe null
          }
        }

        describe("比较操作符") {
          it("gt 生成 \$gt") {
            val c = criteria { "age" gt 30 }

            (c.criteriaObject["age"] as Document)["\$gt"] shouldBe 30
          }

          it("gte 生成 \$gte") {
            val c = criteria { User::age gte 30 }

            (c.criteriaObject["age"] as Document)["\$gte"] shouldBe 30
          }

          it("lt 生成 \$lt") {
            val c = criteria { "age" lt 30 }

            (c.criteriaObject["age"] as Document)["\$lt"] shouldBe 30
          }

          it("lte 生成 \$lte") {
            val c = criteria { User::age lte 30 }

            (c.criteriaObject["age"] as Document)["\$lte"] shouldBe 30
          }
        }

        describe("between") {
          it("Pair 区间生成 \$gte 和 \$lte") {
            val c = criteria { "age" between (10 to 20) }

            val range = c.criteriaObject["age"] as Document
            range["\$gte"] shouldBe 10
            range["\$lte"] shouldBe 20
          }

          it("ClosedRange 区间生成 \$gte 和 \$lte") {
            val c = criteria { User::age between (10..20) }

            val range = c.criteriaObject["age"] as Document
            range["\$gte"] shouldBe 10
            range["\$lte"] shouldBe 20
          }

          it("任一边界为 null 时跳过该条件") {
            val c = criteria { "age" between (null to 20) }

            c.criteriaObject.isEmpty() shouldBe true
          }
        }

        describe("inValues") {
          it("集合生成 \$in") {
            val c = criteria { "status" inValues listOf("ACTIVE", "PENDING") }

            (c.criteriaObject["status"] as Document)["\$in"] shouldBe listOf("ACTIVE", "PENDING")
          }

          it("属性引用生成 \$in") {
            val c = criteria { User::status inValues listOf("ACTIVE") }

            (c.criteriaObject["status"] as Document)["\$in"] shouldBe listOf("ACTIVE")
          }
        }

        describe("ninValues") {
          it("集合生成 \$nin") {
            val c = criteria { "status" ninValues listOf("DISABLED", "DELETED") }

            (c.criteriaObject["status"] as Document)["\$nin"] shouldBe listOf("DISABLED", "DELETED")
          }
        }

        describe("like") {
          it("生成不区分大小写的包含匹配并转义特殊字符") {
            val c = criteria { "name" like "Alice.test" }

            val pattern = c.criteriaObject["name"] as Pattern
            pattern.pattern() shouldBe "\\QAlice.test\\E"
            pattern.flags() and Pattern.CASE_INSENSITIVE shouldBe Pattern.CASE_INSENSITIVE
          }

          it("属性引用生成包含匹配") {
            val c = criteria { User::name like "Ali" }

            (c.criteriaObject["name"] as Pattern).pattern() shouldBe "\\QAli\\E"
          }
        }

        describe("regex") {
          it("默认无选项") {
            val c = criteria { "name".regex("^Ali") }

            (c.criteriaObject["name"] as Pattern).pattern() shouldBe "^Ali"
          }

          it("支持正则选项") {
            val c = criteria { "name".regex("^ali", "i") }

            val pattern = c.criteriaObject["name"] as Pattern
            pattern.pattern() shouldBe "^ali"
            pattern.flags() and Pattern.CASE_INSENSITIVE shouldBe Pattern.CASE_INSENSITIVE
          }
        }

        describe("or") {
          it("多个分支组合为 \$or") {
            val c = criteria {
              or(
                  { "a" eq 1 },
                  { "b" eq 2 },
              )
            }

            (c.criteriaObject["\$or"] as List<*>).size shouldBe 2
          }

          it("分支内多个条件按 \$and 组合") {
            val c = criteria {
              or(
                  {
                    "a" eq 1
                    "b" eq 2
                  },
                  { "c" eq 3 },
              )
            }

            val first = (c.criteriaObject["\$or"] as List<*>)[0] as Document
            first.containsKey("\$and") shouldBe true
          }

          it("仅一个有效分支时直接加入条件") {
            val c = criteria { or({ "a" eq 1 }) }

            c.criteriaObject["a"] shouldBe 1
            c.criteriaObject.containsKey("\$or") shouldBe false
          }

          it("空分支不添加任何条件") {
            val c = criteria { or({}) }

            c.criteriaObject.isEmpty() shouldBe true
          }
        }

        describe("add") {
          it("直接添加任意 Criteria") {
            val c = criteria { add(Criteria.where("x").exists(true)) }

            (c.criteriaObject["x"] as Document)["\$exists"] shouldBe true
          }
        }

        describe("条件合并") {
          it("多个条件以 \$and 组合") {
            val c = criteria {
              "a" eq 1
              "b" eq 2
            }

            (c.criteriaObject["\$and"] as List<*>).size shouldBe 2
          }

          it("空作用域返回空条件") {
            val c = criteria {}

            c.criteriaObject.isEmpty() shouldBe true
          }
        }
      }
    })
