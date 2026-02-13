# AGENTS.md - Nanoda Coding Guidelines

## Build Commands

```bash
# Build the project
./gradlew build

# Assemble JAR (CI build)
./gradlew assemble

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.bangbang93.nanoda.temporal.WorkflowExtTest"

# Run tests matching pattern
./gradlew test --tests "*WorkflowExt*"

# Generate test coverage report (Kover)
./gradlew koverXmlReport

# Generate Cobertura coverage (for CI)
./gradlew jacocoToCobertura

# Format code with Spotless
./gradlew spotlessApply

# Check code formatting
./gradlew spotlessCheck

# Generate documentation (Dokka)
./gradlew dokkaGenerate

# Publish to Maven repository
./gradlew publish

# Generate changelog
./gradlew changelog
```

## Code Style Guidelines

### Formatting
- **Tool**: Spotless with `ktfmt()` (Kotlin formatter)
- Apply formatting before committing: `./gradlew spotlessApply`
- Never disable Spotless checks

### Imports
- Group imports: standard library, third-party, project
- Use wildcard imports only for related packages (e.g., `kotlinx.coroutines.*`)
- Remove unused imports before committing

### Naming Conventions
- **Packages**: `com.bangbang93.nanoda.<module>` (all lowercase)
- **Classes**: PascalCase (e.g., `WorkflowExt`, `ChipcooException`)
- **Functions**: camelCase (e.g., `newActivityStub`, `buildQuery`)
- **Test Classes**: `<ClassUnderTest>Test` suffix (e.g., `WorkflowExtTest`)
- **Extension Functions**: Descriptive names indicating what they extend
- **Constants**: UPPER_SNAKE_CASE in companion objects

### Types
- Use explicit types on public APIs
- Leverage Kotlin type inference for local variables
- Use nullable types (`?`) appropriately
- Prefer `val` over `var`
- Target JVM 17 (source), build with JVM 21

### Error Handling
- Use custom exceptions inheriting from `ChipcooException`
- Provide error codes, HTTP codes, and optional data
- Use `Ensure` utility for preconditions (from `com.bangbang93.nanoda.exception`)
- Document exceptions in KDoc comments

### Testing (Kotest)
- Use `DescribeSpec` style with Chinese test descriptions
- Pattern: `describe("feature") { it("behavior") { ... } }`
- Arrange-Act-Assert structure with comments
- Use MockK for mocking (e.g., `mockk()`, `mockkStatic()`)
- Assertions: `shouldBe`, `shouldNotBe`, `shouldThrow`

### Documentation
- All public APIs must have KDoc
- Include `@param`, `@return`, `@throws` where applicable
- Keep documentation concise and clear

### Version Management
- Update version in `nanoda/build.gradle.kts`
- Document changes in `CHANGELOG.md`
- Use semantic versioning (currently at 0.7.8)

### Project Structure
- Source: `nanoda/src/main/kotlin/com/bangbang93/nanoda/`
- Tests: `nanoda/src/test/kotlin/com/bangbang93/nanoda/`
- Organize by feature (e.g., `temporal/`, `exception/`, `collections/`)

## CI/CD Integration

- GitLab CI runs: `gradle assemble`, `gradle koverXmlReport jacocoToCobertura koverLog`
- Coverage reports generated in `nanoda/build/reports/kover/`
- SonarQube analysis on master branch and MRs
