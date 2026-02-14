---
applyTo: "**/*.kt,**/*.kts"
description: "Kotlin development best practices"
---

# Kotlin Development Guidelines

Apply the [general coding standards](../copilot-instructions.md) to all Kotlin code.

## Core Kotlin Practices

### Immutability and Null Safety

- Prefer `val` over `var` for immutable data
- Avoid nullable types unless necessary; use `?` sparingly
- Use safe call (`?.`), elvis (`?:`), and not-null assertion (`!!`) appropriately
- Use `require()` and `check()` for input validation

### Functions and Collections

- Keep functions small and focused
- Use lambda expressions and standard library: `map`, `filter`, `fold`
- Leverage extension functions for convenient APIs
- Use `listOf()`, `mapOf()`, `setOf()` for immutable collections

### Type System

- Use sealed classes and data classes for domain modeling
- Let the compiler infer types when clear
- Use type aliases for complex signatures
- Leverage Kotlin's smart casts and exhaustive when expressions

## API Design

### Documentation and Public APIs

- Document all public functions with KDoc comments
- Use meaningful parameter names and clear signatures
- Provide sensible defaults with default parameters
- Create specific exception types with clear error messages
- Use `@Deprecated` with ReplaceWith for API changes

### Builder and DSL Patterns

- Use builders for complex object construction
- Leverage DSL-style APIs with receiver types
- Apply fluent interfaces with scope functions: `apply`, `let`, `also`

## Error Handling

### Exception Usage

- Throw specific exceptions for different error cases
- Include context in error messages
- Validate inputs to public functions
- Avoid catching generic `Exception`

## Testing Strategy

- Write testable code with minimal side effects
- Inject dependencies rather than hardcoding
- Keep constructors simple
- Provide test-friendly default values
- Aim for pure functions when possible

## Performance and Build

### Code Organization

- One public class per file (except related types)
- Use `internal` modifier for library internals
- Organize packages by functionality
- Use sensible naming conventions (camelCase)

### Gradle Configuration

- Maintain organized `build.gradle.kts` files
- Use version catalogs in `gradle/libs.versions.toml`
- Document custom build tasks
- Keep build configuration DRY

### Performance Considerations

- Use `inline` for frequently-called small functions
- Minimize object allocations in hot paths
- Consider memory footprint of utilities
- Profile performance-critical code
- Use sequences for lazy collection evaluation

## Kotlin Idioms

- Use `when` expressions as exhaustive type checks
- Leverage scope functions appropriately
- Use `repeat()` instead of for-loop with counter
- Use `use` for resource cleanup (AutoCloseable)
- Prefer sequences for large collection chains
