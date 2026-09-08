package com.bangbang93.nanoda.spring.data.mongodb

import kotlin.reflect.KProperty
import org.bson.Document
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.Update.Position

/**
 * [Update] 构建 DSL 作用域。
 *
 * 方法名与 MongoDB 更新操作符一一对应（set → $set、inc → $inc、multiply → $mul、renameTo → $rename 等）。
 *
 * @see update
 */
class UpdateScope {
  private val update = Update()

  /** $set 赋值 */
  infix fun String.set(value: Any?) {
    update.set(this, value)
  }

  /** $set 赋值；值类型与属性类型一致 */
  infix fun <T> KProperty<T>.set(value: T?) {
    update.set(this.toDotPath(), value)
  }

  /** 仅 upsert 插入时生效的 $set */
  infix fun String.setOnInsert(value: Any?) {
    update.setOnInsert(this, value)
  }

  /** 仅 upsert 插入时生效的 $set；值类型与属性类型一致 */
  infix fun <T> KProperty<T>.setOnInsert(value: T?) {
    update.setOnInsert(this.toDotPath(), value)
  }

  /** $unset 删除字段 */
  fun String.unset() {
    update.unset(this)
  }

  /** $unset 删除字段 */
  fun KProperty<*>.unset() {
    update.unset(this.toDotPath())
  }

  /** $inc 自增指定增量 */
  infix fun String.inc(delta: Number) {
    update.inc(this, delta)
  }

  /** $inc 自增指定增量 */
  infix fun KProperty<*>.inc(delta: Number) {
    update.inc(this.toDotPath(), delta)
  }

  /** $inc 自增 1 */
  fun String.inc() {
    update.inc(this)
  }

  /** $inc 自增 1 */
  fun KProperty<*>.inc() {
    update.inc(this.toDotPath())
  }

  /** $mul 乘法（渲染为 double） */
  infix fun String.multiply(multiplier: Number) {
    update.multiply(this, multiplier)
  }

  /** $mul 乘法（渲染为 double） */
  infix fun KProperty<*>.multiply(multiplier: Number) {
    update.multiply(this.toDotPath(), multiplier)
  }

  /** $max 仅当新值更大时更新 */
  infix fun String.max(value: Any) {
    update.max(this, value)
  }

  /** $max 仅当新值更大时更新；值类型与属性类型一致 */
  infix fun <T : Any> KProperty<T>.max(value: T) {
    update.max(this.toDotPath(), value)
  }

  /** $min 仅当新值更小时更新 */
  infix fun String.min(value: Any) {
    update.min(this, value)
  }

  /** $min 仅当新值更小时更新；值类型与属性类型一致 */
  infix fun <T : Any> KProperty<T>.min(value: T) {
    update.min(this.toDotPath(), value)
  }

  /** $push 追加元素 */
  infix fun String.push(value: Any?) {
    update.push(this, value)
  }

  /** $push 追加元素 */
  infix fun KProperty<*>.push(value: Any?) {
    update.push(this.toDotPath(), value)
  }

  /** $push $each 批量追加 */
  fun String.pushEach(vararg values: Any?) {
    update.push(this, Document("\$each", values.toList()))
  }

  /** $push $each 批量追加 */
  fun KProperty<*>.pushEach(vararg values: Any?) {
    update.push(this.toDotPath(), Document("\$each", values.toList()))
  }

  /** $addToSet 元素不存在时才添加 */
  infix fun String.addToSet(value: Any?) {
    update.addToSet(this, value)
  }

  /** $addToSet 元素不存在时才添加 */
  infix fun KProperty<*>.addToSet(value: Any?) {
    update.addToSet(this.toDotPath(), value)
  }

  /** $pull 移除匹配的元素 */
  infix fun String.pull(value: Any?) {
    update.pull(this, value)
  }

  /** $pull 移除匹配的元素 */
  infix fun KProperty<*>.pull(value: Any?) {
    update.pull(this.toDotPath(), value)
  }

  /** $pullAll 移除多个元素 */
  fun String.pullAll(vararg values: Any?) {
    update.pullAll(this, values)
  }

  /** $pullAll 移除多个元素 */
  fun KProperty<*>.pullAll(vararg values: Any?) {
    update.pullAll(this.toDotPath(), values)
  }

  /** $pop 弹出首个元素 */
  fun String.popFirst() {
    update.pop(this, Position.FIRST)
  }

  /** $pop 弹出首个元素 */
  fun KProperty<*>.popFirst() {
    update.pop(this.toDotPath(), Position.FIRST)
  }

  /** $pop 弹出末尾元素 */
  fun String.popLast() {
    update.pop(this, Position.LAST)
  }

  /** $pop 弹出末尾元素 */
  fun KProperty<*>.popLast() {
    update.pop(this.toDotPath(), Position.LAST)
  }

  /** $rename 字段重命名 */
  infix fun String.renameTo(newField: String) {
    update.rename(this, newField)
  }

  /** $rename 字段重命名 */
  infix fun KProperty<*>.renameTo(newField: String) {
    update.rename(this.toDotPath(), newField)
  }

  /** $currentDate 设为当前日期 */
  fun String.currentDate() {
    update.currentDate(this)
  }

  /** $currentDate 设为当前日期 */
  fun KProperty<*>.currentDate() {
    update.currentDate(this.toDotPath())
  }

  /** $currentDate 设为当前时间戳（timestamp 类型） */
  fun String.currentTimestamp() {
    update.currentTimestamp(this)
  }

  /** $currentDate 设为当前时间戳（timestamp 类型） */
  fun KProperty<*>.currentTimestamp() {
    update.currentTimestamp(this.toDotPath())
  }

  /** 逃生舱：直接操作底层 [Update]（slice/position/bitwise/arrayFilters 等高级操作） */
  fun raw(u: Update.() -> Unit) {
    update.u()
  }

  /**
   * 构建更新对象。
   *
   * @return 配置好的 [Update]
   */
  fun build(): Update = update
}

/**
 * 使用 DSL 构建 [Update]。
 *
 * 示例：
 *
 * ```kotlin
 * val u = update {
 *   User::name set "Alice"
 *   User::age inc 1
 *   User::tags addToSet "admin"
 *   "lastLoginAt" currentTimestamp()
 * }
 * ```
 *
 * @param u 更新构建 lambda
 * @return 配置好的 [Update]
 */
fun update(u: UpdateScope.() -> Unit): Update {
  val scope = UpdateScope()
  scope.u()
  return scope.build()
}
