package com.bangbang93.nanoda.csv

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.nio.charset.Charset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalStdlibApi::class)
class CsvBuilder<T>(
    columns: List<Column<T>> = emptyList(),
    private val options: Options = Options(),
) : AutoCloseable, CoroutineScope by CoroutineScope(Dispatchers.IO) {
  data class Column<T>(
      val header: String,
      val value: (T) -> Any?,
  )

  data class Options(
      val newLine: String = "\n",
      val delimiter: Char = ',',
      val charset: Charset = Charsets.UTF_8,
      val nullCode: String = "",
      val bom: Boolean = true,
      val headers: Boolean = true,
      /** 缓冲区大小，默认为1MB 注意：如果数据量很大，可能需要增大此值以避免频繁的flush和等待缓冲区空间。 */
      @Suppress("MagicNumber") val bufferSize: Int = 1024 * 1024,
  )

  private val columns = mutableListOf<Column<T>>().apply { addAll(columns) }
  private var headerWritten = false
  private val bufferSize = options.bufferSize
  private val buffer = PipedOutputStream()

  fun addColumn(
      header: String,
      value: (T) -> Any?,
  ) {
    columns.add(Column(header, value))
  }

  fun writeRows(
      rows: Flow<T>,
      transformer: (Any?) -> Any? = { it },
  ): InputStream {
    val input = PipedInputStream(buffer, bufferSize)
    launch {
      csvWriter {
            lineTerminator = options.newLine
            delimiter = options.delimiter
            charset = options.charset.name()
            nullCode = options.nullCode
            prependBOM = options.bom
          }
          .openAsync(buffer) {
            rows.collect { row ->
              if (!headerWritten) {
                if (options.headers) {
                  writeRow(columns.map { it.header })
                  // backpressure: flush并等待缓冲区有空间
                  buffer.flush()
                  waitForBuffer(input)
                }
                headerWritten = true
              }
              writeRow(columns.map { transformer(it.value(row)) })
              buffer.flush()
              waitForBuffer(input)
            }
          }
    }
    return input
  }

  private suspend fun waitForBuffer(input: PipedInputStream) {
    // 如果缓冲区已满，则挂起协程，直到有空间
    while (input.available() >= bufferSize) {
      // 让出协程，稍后重试
      @Suppress("MagicNumber") delay(10)
    }
  }

  override fun close() {
    buffer.flush()
    buffer.close()
  }
}
