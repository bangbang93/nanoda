package com.bangbang93.nanoda.csv

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

class CsvBuilderTest :
    DescribeSpec({
      describe("CsvBuilder") {
        it("should build CSV with headers and rows") {
          val csvBuilder =
              CsvBuilder<Row>(
                  listOf(
                      CsvBuilder.Column("Name") { it.name },
                      CsvBuilder.Column("Age") { it.age },
                      CsvBuilder.Column("City") { it.city },
                  ),
              )

          val rows = listOf(Row("Alice", 30, "New York"), Row("Bob", 25, "Los Angeles"))

          val inputStream = csvBuilder.writeRows(rows.asFlow())

          val csvContent = inputStream.readAllBytes().decodeToString()
          val expectedContent = "\uFEFFName,Age,City\nAlice,30,New York\nBob,25,Los Angeles\n"

          io.kotest.assertions.assertSoftly { csvContent shouldBe expectedContent }
        }

        it("should build CSV without bom and header") {
          val csvBuilder =
              CsvBuilder<Row>(
                  listOf(
                      CsvBuilder.Column("Name") { it.name },
                      CsvBuilder.Column("Age") { it.age },
                      CsvBuilder.Column("City") { it.city },
                  ),
                  options = CsvBuilder.Options(bom = false, headers = false),
              )

          val rows = listOf(Row("Alice", 30, "New York"), Row("Bob", 25, "Los Angeles"))

          val inputStream = csvBuilder.writeRows(rows.asFlow())

          val csvContent = inputStream.readAllBytes().decodeToString()
          val expectedContent = "Alice,30,New York\nBob,25,Los Angeles\n"

          io.kotest.assertions.assertSoftly { csvContent shouldBe expectedContent }
        }
      }

      describe("AsyncFlow") {
        it("should build CSV with async flow") {
          val csvBuilder =
              CsvBuilder<Row>(
                  listOf(
                      CsvBuilder.Column("Name") { it.name },
                      CsvBuilder.Column("Age") { it.age },
                      CsvBuilder.Column("City") { it.city },
                  ),
              )

          val order = mutableListOf<Int>()
          val rows = flow {
            delay(100)
            order.add(3)
            emit(Row("Alice", 30, "New York"))
            order.add(4)
            emit(Row("Bob", 25, "Los Angeles"))
          }
          order.add(1)

          val inputStream = csvBuilder.writeRows(rows)

          order.add(2)
          val csvContent = inputStream.readAllBytes().decodeToString()
          order.add(5)
          val expectedContent = "\uFEFFName,Age,City\nAlice,30,New York\nBob,25,Los Angeles\n"

          csvContent shouldBe expectedContent
          order shouldBe (1..5).toList()
        }
      }
    }) {
  data class Row(
      val name: String,
      val age: Int,
      val city: String,
  )
}
