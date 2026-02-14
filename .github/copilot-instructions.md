---
applyTo: "**/*"
description: "General coding standards for the Nanoda library"
---

# Nanoda Library - GitHub Copilot Configuration

Welcome to the Nanoda project! This document provides guidance for GitHub Copilot interactions across the entire
repository.

## Project Overview

Nanoda is a Kotlin-based backend utility library designed to improve development efficiency and simplify backend
development workflows. The project is built with Gradle, publishes to Maven Central, and follows modern Kotlin best
practices.

## Core Principles

### 1. Code Quality

- Write clean, maintainable, and well-documented code
- Follow functional programming principles where applicable
- Use immutable data structures and prefer val over var
- Apply SOLID principles in library design
- Keep functions small and focused

### 2. Type Safety

- Use Kotlin's type system effectively
- Prefer non-nullable types and use ? only when necessary
- Use sealed classes and data classes for better type safety
- Leverage compiler checks to catch errors early

### 3. Kotlin-Specific Best Practices

- Use extension functions to provide convenient APIs
- Leverage Kotlin's standard library functions (map, filter, fold, etc.)
- Use coroutines for async operations where applicable
- Prefer inline functions for performance-critical code
- Use string templates and multiline strings appropriately

### 4. Library Design

- Design APIs with end-users in mind
- Minimize binary compatibility issues for major versions
- Provide sensible defaults and convenient overloads
- Document all public APIs with KDoc comments
- Use @Deprecated with migration path for API changes

### 5. Testing Standards

- Write tests for all public APIs
- Use Kotest for unit testing following BDD style
- Aim for high test coverage (target: >80%)
- Test edge cases and error conditions
- Use test fixtures and builders for complex test data

### 6. Documentation

- Include KDoc comments for all public types and functions
- Keep README.md updated with usage examples
- Generate API documentation with Dokka
- Provide clear error messages in exceptions
- Document breaking changes in CHANGELOG

### 7. Build and Release

- Use Gradle for all build tasks
- Maintain semantic versioning (MAJOR.MINOR.PATCH)
- Sign releases with PGP for security
- Publish to Maven Central via Sonatype
- Keep gradle/libs.versions.toml organized and up-to-date

### 8. Security and Performance

- Validate all public API inputs
- Avoid catching generic Exception; use specific exceptions
- Be mindful of memory usage in utility functions
- Consider performance impact of utility functions
- Follow OWASP security guidelines where applicable

## Related Instructions

For more specific guidance, refer to:

- [Kotlin Development Guidelines](./instructions/kotlin.instructions.md)
- [Testing Standards](./instructions/testing.instructions.md)
- [Documentation Requirements](./instructions/documentation.instructions.md)
- [Security Best Practices](./instructions/security.instructions.md)
- [Performance Guidelines](./instructions/performance.instructions.md)
- [Code Review Standards](./instructions/code-review.instructions.md)

## Project Structure

```
nanoda/
├── src/
│   ├── main/kotlin/          # Source code
│   └── test/kotlin/          # Test code
├── build.gradle.kts          # Build configuration
├── gradle/
│   └── libs.versions.toml    # Dependency versions
├── .github/                  # GitHub configuration
│   ├── copilot-instructions.md
│   ├── instructions/         # Detailed guidelines
│   ├── prompts/              # Reusable prompts
│   ├── agents/               # Chat modes
│   └── workflows/            # GitHub Actions
└── docs/                     # Documentation
```

## Getting Started

When working with Copilot in this project:

1. **For new features**: Use the `architect` agent mode for planning
2. **For writing code**: Follow the Kotlin guidelines in `kotlin.instructions.md`
3. **For testing**: Reference `testing.instructions.md` and use write-tests prompt
4. **For documentation**: Use generate-docs prompt for API documentation
5. **For code review**: Use code-review agent mode for peer review assistance
6. **For debugging**: Use debugger agent mode for troubleshooting

## Version Information

- **Kotlin Version**: Latest from libs.versions.toml
- **Java Target**: JVM 17 (compiled with Java 21)
- **Gradle Version**: 8.13.0+
- **Current Project Version**: 0.0.2

