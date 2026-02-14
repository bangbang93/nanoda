---
applyTo: "**/*Test.kt,**/*Spec.kt,**/test/**/*.kt"
description: "Testing standards and best practices for Nanoda library"
---

# Testing Standards

Apply the [general coding standards](../copilot-instructions.md) to all test code.

## Testing Philosophy

- Test all public APIs and key functionality
- Aim for >80% code coverage on library code
- Test both happy paths and error conditions
- Use descriptive test names that explain the scenario
- Keep tests focused and isolated (one concern per test)

## Kotest Framework

### Test Structure

- Use Kotest's BDD style: `describe`, `context`, `it` blocks for readability
- Organize tests hierarchically using nested contexts
- Keep test files co-located with source code or in parallel structure
- Use `Spec` suffix for test classes: `MyUtilitySpec`

### Assertions

- Use Kotest matchers for expressive assertions
- Prefer specific matchers over generic assertTrue/assertEquals
- Use `.shouldBe()`, `.shouldContain()`, `.shouldThrow()` style
- Chain matchers when testing multiple conditions
- Use `forAll` and `checkAll` for property-based testing

## Test Naming and Organization

### Naming Conventions

- Describe the scenario being tested in the test name
- Use given-when-then pattern: `given_validInput_whenProcessed_thenReturnsExpected`
- Or use descriptive names: `shouldThrowExceptionWhenInputIsNull`
- Use backtick strings for readable test names: `` `should return 42 for valid input` ``

### Test Organization

- Group related tests in nested `context` blocks
- Separate unit tests from integration tests
- Use test fixtures for shared setup (avoid duplication)
- Create test builders for complex test data

## Test Data and Fixtures

### Creating Test Data

- Use test builders for objects with many properties
- Create factories for common test instances
- Use default values in builders for optional properties
- Keep test data creation close to where it's used
- Avoid magic strings/numbers; use named constants

### Shared Setup

- Use `beforeTest` for per-test setup
- Use `beforeSpec` for one-time setup per class
- Keep setup minimal and focused
- Document complex setup logic

## Testing Different Scenarios

### Happy Path Tests

- Test the main successful execution path
- Verify the expected output and behavior
- Use realistic test data

### Error Condition Tests

- Test invalid inputs (null, empty, out-of-range)
- Test exceptions are thrown with correct messages
- Verify error handling behavior
- Test boundary conditions

### Edge Cases

- Test empty collections or strings
- Test very large inputs
- Test special characters and encoding
- Test concurrent access if applicable

## Mocking and Stubbing

### When to Use Mocks

- Mock external dependencies (databases, APIs, file systems)
- Use MockK for Kotlin mocking
- Keep mocks minimal; only mock what's necessary
- Prefer real objects when practical

### Best Practices

- Verify only essential interactions
- Use argument matchers appropriately
- Avoid mocking the class under test
- Test behavior, not implementation details

## Performance Testing

### Benchmarking Utilities

- Test performance-critical code paths
- Document performance expectations
- Use appropriate timeout values for long operations
- Avoid performance testing in standard test suites

## Documentation in Tests

### Code Comments

- Explain complex test setup or assertions
- Document why a test exists if it's non-obvious
- Use comments to explain expected failures
- Link to related issues or requirements

## Test Maintenance

### Flaky Tests

- Avoid time-dependent tests
- Use proper timeouts and retries
- Avoid external service dependencies in unit tests
- Keep tests deterministic and repeatable

### Keeping Tests Clean

- Remove or fix broken tests immediately
- Refactor test code like production code
- Avoid test duplication; use shared utilities
- Remove commented-out test code

## Coverage Goals

- **Minimum Coverage**: 80% line coverage for library code
- **Focus Areas**: Public APIs, critical paths, error handling
- **Integration Tests**: Test public API contracts
- **Exclude**: Generated code, UI code, trivial getters/setters

