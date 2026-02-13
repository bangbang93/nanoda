# Nanoda 通用类库

## 简介
Nanoda 是一个基于 Kotlin 和 Gradle 构建的后端通用类库，提供了一些常用的工具和功能，旨在提高开发效率并简化后端开发流程。

## 技术栈
- **语言**: Kotlin
- **目标平台**: JVM
- **构建工具**: Gradle
- **依赖管理**: Gradle `libs.versions.toml`
- **主要依赖**:
    - Kotlinx DateTime
    - Protobuf Kotlin
    - Kotest (测试框架)
    - Dokka (文档生成)

## 项目结构
- `nanoda/build.gradle.kts`: 项目的 Gradle 构建脚本，定义了依赖、插件和发布配置。
- `gradle/libs.versions.toml`: 管理项目依赖的版本。
- `.gitlab-ci.yml`: GitLab CI 配置文件，定义了 CI/CD 流程。

## 构建和运行

### 环境要求
- **JDK**: 17+
- **Gradle**: 8.13.0+

### 本地构建
运行以下命令以构建项目：
```bash
./gradlew build
```

### 发布工件
运行以下命令将工件发布到配置的 Maven 仓库：
```bash
./gradlew publish
```

### 生成文档
运行以下命令生成项目文档：
```bash
./gradlew dokkaGenerate
```
生成的文档位于 `build/dokka/html` 目录。

## CI/CD
项目使用 GitLab CI 进行持续集成和部署，主要阶段包括：
1. **构建应用**: 编译和打包项目。
2. **生成文档**: 使用 Dokka 生成文档。
3. **部署文档**: 将生成的文档发布到 GitLab Pages。

## 配置
### Gradle 配置
在 `gradle.properties` 中启用了以下配置：
- `org.gradle.configuration-cache=true`: 启用 Gradle 配置缓存以加速构建。
- `org.jetbrains.dokka.experimental.gradle.pluginMode=V2Enabled`: 启用 Dokka V2 模式。

## 测试
项目使用 Kotest 作为测试框架，运行以下命令执行测试：
```bash
./gradlew test
```

## 贡献
欢迎提交 Issue 和 Pull Request！请确保代码通过所有测试并符合项目的代码规范。

## 许可证
此项目的许可证信息请参考相关文件。
