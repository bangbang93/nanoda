import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm") version libs.versions.kotlin.get()
  alias(libs.plugins.versions)
  alias(libs.plugins.dokka)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotest)
  alias(libs.plugins.ktfmt)
  alias(libs.plugins.detekt)
}

dependencies {
  compileOnly(kotlin("stdlib"))

  compileOnly(libs.kotlinx.datetime)
  compileOnly(libs.protobuf.kotlin)
  compileOnly(libs.jakarta.validation.api)
  compileOnly(libs.bson)
  compileOnly(libs.spring.data.mongodb)
  compileOnly(libs.bundles.kotlinx.coroutines)
  compileOnly(libs.kotlinCsv)
  compileOnly(libs.kotlinLogging)
  compileOnly(libs.bundles.jackson.modules)
  compileOnly(libs.reactor.core)
  compileOnly(libs.bundles.temporal)

  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.kotest)
  testImplementation(libs.slf4jSample)
  testImplementation(libs.mockk)
  testImplementation(libs.spring.data.mongodb)
  testImplementation(libs.mongodb.driver.sync)
  testImplementation(libs.mongodb.driver.reactivestreams)
}

repositories { mavenCentral() }

group = "com.bangbang93.nanoda"

version = "0.0.1"

tasks.withType<Test>().configureEach { useJUnitPlatform() }

tasks.test { useJUnitPlatform() }

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  jvmToolchain(21)
  compilerOptions { jvmTarget = JvmTarget.JVM_17 }
}
