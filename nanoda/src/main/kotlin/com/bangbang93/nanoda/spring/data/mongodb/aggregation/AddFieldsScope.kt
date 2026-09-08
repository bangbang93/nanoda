package com.bangbang93.nanoda.spring.data.mongodb.aggregation

import kotlin.reflect.KProperty
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.Fields

/** $addFields 阶段作用域 */
class AddFieldsScope {
  private val builder = AddFieldsOperation.builder()

  /** 引用属性字段值（渲染为 "$path" 引用） */
  infix fun String.from(prop: KProperty<*>) {
    builder.addFieldWithValueOf(this, Fields.field(prop.toDotPath()))
  }

  /** 引用字段值（渲染为 "$path" 引用，支持嵌套路径如 "user.id"） */
  infix fun String.from(field: String) {
    builder.addFieldWithValueOf(this, Fields.field(field))
  }

  /** 赋常量值 */
  infix fun String.value(value: Any?) {
    builder.addFieldWithValue(this, value)
  }

  /** 逃生舱：以 [AggregationExpression] 计算字段值 */
  infix fun String.expr(expression: AggregationExpression) {
    builder.addField(this).withValue(expression)
  }

  /** 构建 [AddFieldsOperation] */
  internal fun build(): AddFieldsOperation = builder.build()
}
