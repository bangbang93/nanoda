package com.bangbang93.nanoda.temporal

import io.temporal.activity.ActivityOptions
import io.temporal.activity.LocalActivityOptions
import io.temporal.workflow.Workflow
import kotlin.jvm.java

inline fun <reified T> newLocalActivityStub(localActivityOptions: LocalActivityOptions? = null): T {
  return Workflow.newLocalActivityStub(T::class.java, localActivityOptions)
}

inline fun <reified T> newLocalActivityStub(builder: LocalActivityOptions.Builder.() -> Unit): T {
  val options = LocalActivityOptions.newBuilder().apply(builder).build()
  return newLocalActivityStub<T>(options)
}

inline fun <reified T> newActivityStub(activityOptions: ActivityOptions? = null): T {
  return Workflow.newActivityStub(T::class.java, activityOptions)
}

inline fun <reified T> newActivityStub(builder: ActivityOptions.Builder.() -> Unit): T {
  val options = ActivityOptions.newBuilder().apply(builder).build()
  return newActivityStub(options)
}
