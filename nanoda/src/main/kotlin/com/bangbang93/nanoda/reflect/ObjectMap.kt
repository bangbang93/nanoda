package com.bangbang93.nanoda.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Creates any Kotlin Data-Class by a map.
 *
 * ```kotlin
 *  val map = mutableMapOf("first" to "Henry", "last" to "O'Conner")
 *  val obj: SomeClass = SomeClass::class.by(map)
 *  ```
 */
fun <T : Any> KClass<T>.by(values: Map<String, Any?>): T {
  val ctor = this.primaryConstructor!!
  val params = mutableMapOf<KParameter, Any?>()
  for (param in ctor.parameters) {
    val name = param.name!!
    if (values.containsKey(name)) {
      params[param] = values[name]
    } else if (param.type.isMarkedNullable) {
      params[param] = null
    }
  }
  return ctor.callBy(params)
}

/**
 * Converts public properties of any Kotlin object to a Map.
 *
 * ```kotlin
 *  val obj = SomeClass("Henry", "O'Conner")
 *  val map: Map<String, Any?> = obj.asMap()
 *  ```
 */
fun <T : Any> T.asMap(): Map<String, Any?> =
    javaClass.kotlin.memberProperties
        .filter { it.visibility == KVisibility.PUBLIC }
        .associate { it.name to it.get(this) }
