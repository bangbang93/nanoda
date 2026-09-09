package com.bangbang93.nanoda.spring.data.mongodb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.bson.Document
import org.springframework.data.mongodb.core.query.Update

class UpdateTest :
    DescribeSpec({
      describe("update") {
        data class User(
            val id: Long,
            val name: String,
            val age: Int,
            val tags: List<String>,
            val price: Double,
        )

        describe("set") {
          it("字符串字段赋值") {
            val u = update { "name" set "Alice" }

            (u.updateObject["\$set"] as Document)["name"] shouldBe "Alice"
          }

          it("属性引用赋值") {
            val u = update { User::name set "Alice" }

            (u.updateObject["\$set"] as Document)["name"] shouldBe "Alice"
          }

          it("多字段合并到同一 \$set") {
            val u = update {
              "name" set "Alice"
              User::age set 30
            }

            val setDoc = u.updateObject["\$set"] as Document
            setDoc["name"] shouldBe "Alice"
            setDoc["age"] shouldBe 30
          }
        }

        describe("setOnInsert") {
          it("仅 upsert 插入时生效") {
            val u = update { User::id setOnInsert 1L }

            (u.updateObject["\$setOnInsert"] as Document)["id"] shouldBe 1L
          }
        }

        describe("unset") {
          it("删除字段") {
            val u = update { User::age.unset() }

            (u.updateObject["\$unset"] as Document)["age"] shouldBe 1
          }
        }

        describe("inc") {
          it("默认自增 1") {
            val u = update { User::age.inc() }

            ((u.updateObject["\$inc"] as Document)["age"] as Number).toInt() shouldBe 1
          }

          it("指定增量") {
            val u = update { "age" inc 5 }

            ((u.updateObject["\$inc"] as Document)["age"] as Number).toInt() shouldBe 5
          }
        }

        describe("multiply") {
          it("乘法渲染为 double") {
            val u = update { User::price multiply 2 }

            ((u.updateObject["\$mul"] as Document)["price"] as Number).toDouble() shouldBe 2.0
          }
        }

        describe("max 与 min") {
          it("仅更大时更新") {
            val u = update { User::age max 30 }

            (u.updateObject["\$max"] as Document)["age"] shouldBe 30
          }

          it("仅更小时更新") {
            val u = update { "age" min 10 }

            (u.updateObject["\$min"] as Document)["age"] shouldBe 10
          }
        }

        describe("push") {
          it("追加单个元素") {
            val u = update { User::tags push "admin" }

            (u.updateObject["\$push"] as Document)["tags"] shouldBe "admin"
          }

          it("批量追加") {
            val u = update { User::tags.pushEach("a", "b") }

            val each =
                ((u.updateObject["\$push"] as Document)["tags"] as Update.Modifiers)
                    .modifiers
                    .single()
            each.key shouldBe "\$each"
            (each.value as Array<*>).toList() shouldBe listOf("a", "b")
          }

          it("each 标记批量追加") {
            val u = update { "tags" push each("a", "b") }

            val each =
                ((u.updateObject["\$push"] as Document)["tags"] as Update.Modifiers)
                    .modifiers
                    .single()
            each.key shouldBe "\$each"
            (each.value as Array<*>).toList() shouldBe listOf("a", "b")
          }
        }

        describe("addToSet") {
          it("元素不存在时才添加") {
            val u = update { User::tags addToSet "admin" }

            (u.updateObject["\$addToSet"] as Document)["tags"] shouldBe "admin"
          }

          it("批量去重添加") {
            val u = update { "tags".addToSetEach("a", "b") }

            val each = (u.updateObject["\$addToSet"] as Document)["tags"] as Update.Modifier
            each.key shouldBe "\$each"
            (each.value as Array<*>).toList() shouldBe listOf("a", "b")
          }

          it("each 标记批量去重添加") {
            val u = update { User::tags addToSet each("a", "b") }

            val each = (u.updateObject["\$addToSet"] as Document)["tags"] as Update.Modifier
            each.key shouldBe "\$each"
            (each.value as Array<*>).toList() shouldBe listOf("a", "b")
          }
        }

        describe("pull 与 pullAll") {
          it("移除匹配元素") {
            val u = update { User::tags pull "a" }

            (u.updateObject["\$pull"] as Document)["tags"] shouldBe "a"
          }

          it("移除多个元素") {
            val u = update { User::tags.pullAll("a", "b") }

            (u.updateObject["\$pullAll"] as Document)["tags"] shouldBe listOf("a", "b")
          }
        }

        describe("popFirst 与 popLast") {
          it("弹出首元素渲染 -1") {
            val u = update { User::tags.popFirst() }

            (u.updateObject["\$pop"] as Document)["tags"] shouldBe -1
          }

          it("弹出尾元素渲染 1") {
            val u = update { User::tags.popLast() }

            (u.updateObject["\$pop"] as Document)["tags"] shouldBe 1
          }
        }

        describe("renameTo") {
          it("字段重命名") {
            val u = update { "name" renameTo "username" }

            (u.updateObject["\$rename"] as Document)["name"] shouldBe "username"
          }
        }

        describe("currentDate 与 currentTimestamp") {
          it("设为当前日期") {
            val u = update { "createdAt".currentDate() }

            (u.updateObject["\$currentDate"] as Document)["createdAt"] shouldBe true
          }

          it("设为当前时间戳") {
            val u = update { "createdAt".currentTimestamp() }

            ((u.updateObject["\$currentDate"] as Document)["createdAt"] as Document)["\$type"]
                .shouldBe("timestamp")
          }
        }

        describe("raw") {
          it("直接操作底层 Update") {
            val u = update { raw { set("rawField", 1) } }

            (u.updateObject["\$set"] as Document)["rawField"] shouldBe 1
          }
        }

        describe("多操作符并存") {
          it("顶层按操作符分组") {
            val u = update {
              "name" set "Alice"
              User::age inc 1
            }

            u.updateObject.containsKey("\$set") shouldBe true
            u.updateObject.containsKey("\$inc") shouldBe true
          }
        }
      }
    })
