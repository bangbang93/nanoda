import net.razvan.JacocoToCoberturaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm") version libs.versions.kotlin.get()
  alias(libs.plugins.versions)
  alias(libs.plugins.dokka)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotest)
  alias(libs.plugins.jacocoToCobertura)
  alias(libs.plugins.changelog)
  alias(libs.plugins.ktfmt)
  alias(libs.plugins.detekt)
}

dependencies {
  implementation(kotlin("stdlib"))

  implementation(libs.kotlinx.datetime)
  implementation(libs.protobuf.kotlin)
  implementation(libs.jakarta.validation.api)
  implementation(libs.bson)
  implementation(libs.spring.data.mongodb)
  implementation(libs.bundles.kotlinx.coroutines)
  implementation(libs.kotlinCsv)
  implementation(libs.kotlinLogging)
  implementation(libs.bundles.jackson.modules)
  implementation(libs.reactor.core)
  implementation(libs.bundles.temporal)

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

tasks.register<Jar>("sourcesJar") {
  group = "publishing"
  description = "Assembles sources jar"

  archiveClassifier.set("sources")
  from(sourceSets.main.get().allSource)
}

tasks.register<Exec>("changelog") {
  group = "documentation"
  description = "Generate changelog.md from git commits"

  workingDir = project.rootDir
  commandLine("npx", "auto-changelog", "-v", version.toString(), "--output", "CHANGELOG.md")
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

tasks.test {
  useJUnitPlatform()
  jvmArgs(
      "--add-opens",
      "java.base/java.nio=ALL-UNNAMED",
      "--add-exports",
      "java.base/sun.nio.ch=ALL-UNNAMED",
  )
  systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")
}

tasks.withType<JacocoToCoberturaTask> {
  dependsOn("koverXmlReport")
  inputFile.set(layout.buildDirectory.file("reports/kover/report.xml"))
  outputFile.set(layout.buildDirectory.file("reports/kover/cobertura.xml"))
  sourceDirectories.from(layout.projectDirectory.dir("src/main/kotlin"))
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  jvmToolchain(21)
  compilerOptions { jvmTarget = JvmTarget.JVM_17 }
}
