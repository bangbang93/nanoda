import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jreleaser.gradle.plugin.tasks.AbstractJReleaserTask
import org.jreleaser.model.Active
import org.jreleaser.model.Signing

plugins {
  kotlin("jvm") version libs.versions.kotlin.get()
  alias(libs.plugins.versions)
  alias(libs.plugins.dokka)
  alias(libs.plugins.kover)
  alias(libs.plugins.kotest)
  alias(libs.plugins.ktfmt)
  alias(libs.plugins.detekt)
  alias(libs.plugins.jreleaser)
  `maven-publish`
}

dependencies {
  // 核心库
  implementation(kotlin("stdlib"))
  implementation(libs.kotlinLogging)

  // 三方库 - compileOnly 以避免将它们打包到最终的 JAR 中，用户需要自行添加这些依赖
  compileOnly(libs.kotlinx.datetime)
  compileOnly(libs.protobuf.kotlin)
  compileOnly(libs.jakarta.validation.api)
  compileOnly(libs.bson)
  compileOnly(libs.spring.data.mongodb)
  compileOnly(libs.bundles.kotlinx.coroutines)
  compileOnly(libs.kotlinCsv)
  compileOnly(libs.bundles.jackson.modules)
  compileOnly(libs.reactor.core)
  compileOnly(libs.bundles.temporal)

  // 测试依赖
  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.kotest)
  testImplementation(libs.slf4jSample)
  testImplementation(libs.mockk)

  // 测试中需要使用这些库的功能，因此使用 testImplementation 来引入它们
  testImplementation(libs.kotlinx.datetime)
  testImplementation(libs.protobuf.kotlin)
  testImplementation(libs.jakarta.validation.api)
  testImplementation(libs.bson)
  testImplementation(libs.spring.data.mongodb)
  testImplementation(libs.bundles.kotlinx.coroutines)
  testImplementation(libs.kotlinCsv)
  testImplementation(libs.bundles.jackson.modules)
  testImplementation(libs.reactor.core)
  testImplementation(libs.bundles.temporal)

  // 测试依赖的依赖
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

jreleaser {
  gitRootSearch = true
  release {
    github {
      overwrite = true
      uploadAssets = Active.ALWAYS
    }
  }
  signing {
    active = Active.ALWAYS
    pgp { mode = Signing.Mode.COMMAND }
  }
  deploy {
    maven {
      mavenCentral {
        create("sonatype") {
          active = Active.ALWAYS
          url = "https://central.sonatype.com/api/v1/publisher"
          stagingRepositories = listOf("build/staging-deploy")
        }
      }
    }
  }
}

tasks.withType<AbstractJReleaserTask>().configureEach {
  notCompatibleWithConfigurationCache("https://github.com/jreleaser/jreleaser/issues/1992")
}

java {
  withSourcesJar()
  withJavadocJar()
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      pom {
        name = "nanoda"
        description = "nanoda is a Kotlin utility library"
        url = "https://github.com/bangbang93/nanoda"
        inceptionYear = "2026"

        licenses {
          license {
            name.set("MIT License")
            url.set("https://opensource.org/licenses/MIT")
          }
        }

        developers {
          developer {
            id.set("bangbang93")
            name.set("bangbang93")
          }
        }

        scm {
          connection.set("scm:git:git://github.com/bangbang93/nanoda.git")
          developerConnection.set("scm:git:ssh://git@github.com:bangbang93/nanoda.git")
          url.set("https://github.com/bangbang93/nanoda")
        }
      }
    }
  }

  repositories {
    maven {
      name = "localStaging"
      url = uri(layout.buildDirectory.dir("staging-deploy"))
    }
  }
}
