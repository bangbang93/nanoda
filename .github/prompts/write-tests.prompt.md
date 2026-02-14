---
agent: 'agent'
model: Claude Sonnet 4
tools: [ 'githubRepo', 'codebase', 'problems' ]
description: 'Generate comprehensive unit tests for Kotlin code'
---

# Write Comprehensive Unit Tests

Your goal is to generate comprehensive unit tests for the specified Kotlin function or class.

## Test Strategy

Use Kotest framework with BDD style for test organization:

- Use `describe` blocks for the function/class being tested
- Use `context` blocks for different scenarios
- Use `it` blocks for individual test cases
- Write descriptive test names that explain the scenario

## Test Coverage Requirements

Tests should cover:

1. **Happy Path**: Normal successful execution with valid inputs
2. **Invalid Inputs**:
    - Null values (where applicable)
    - Empty collections or strings
    - Out-of-range values
3. **Error Cases**: Verify exceptions are thrown with correct messages
4. **Edge Cases**:
    - Boundary conditions (minimum/maximum values)
    - Empty or single-element collections
    - Special characters in strings
5. **Type Variations**: Test with different input types if function is generic

## Test Implementation Guidelines

- **Use Kotest Matchers**: Prefer `.shouldBe()`, `.shouldContain()`, `.shouldThrow()` style
- **Test Data**: Create test fixtures and builders for complex objects
- **One Assertion Per Test**: Keep tests focused on a single concern
- **Avoid Duplication**: Use `beforeTest` for shared setup
- **Mock Sparingly**: Only mock external dependencies, not the code under test
- **Clear Names**: Use backtick strings for readable test names: `` `should return 42 for valid input` ``

## Coverage Target

- Aim for >80% line coverage
- Focus on public API surface
- Test critical paths and error handling
- Document why specific edge cases are tested

## Test File Organization

- Place tests in `src/test/kotlin/` mirroring source structure
- Name test files with `*Spec.kt` suffix
- Group related tests in nested contexts
- Keep test files focused and maintainable

## Example Structure

```
describe("function name") {
  context("given valid input") {
    it("should return expected result") {
      // test implementation
    }
  }
  context("given invalid input") {
    it("should throw IllegalArgumentException") {
      // test implementation
    }
  }
}
```

## Common Test Patterns

- **Success case**: Verify correct output for valid input
- **Exception case**: Use `.shouldThrow<ExceptionType> { }` blocks
- **Collection cases**: Test empty, single element, multiple elements
- **Boundary cases**: Test min/max values, special values

Please ask which function/class should be tested and provide its current implementation.

