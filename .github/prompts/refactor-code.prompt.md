---
agent: 'agent'
model: Claude Sonnet 4
tools: [ 'githubRepo', 'codebase', 'problems' ]
description: 'Refactor Kotlin code for improved quality, performance, or maintainability'
---

# Code Refactoring Assistant

Your goal is to refactor the specified Kotlin code to improve quality, performance, or maintainability.

## Refactoring Goals

Clarify which goals are priorities:

1. **Code Quality**: Simplify logic, remove duplication, improve readability
2. **Performance**: Reduce allocations, improve algorithm efficiency, optimize hot paths
3. **Maintainability**: Improve naming, add documentation, reduce complexity
4. **Testing**: Improve testability, add missing tests
5. **Type Safety**: Leverage Kotlin's type system more effectively
6. **Kotlin Idioms**: Use more idiomatic Kotlin patterns

## Analysis Phase

Before refactoring:

1. **Understand Current Code**: What does it do?
2. **Identify Issues**: What could be improved?
3. **Consider Impact**: How do changes affect the rest of the codebase?
4. **Plan Changes**: Create a refactoring strategy
5. **Preserve Behavior**: Ensure tests still pass

## Refactoring Techniques

### Code Quality

- **Extract Functions**: Break down complex functions into smaller pieces
- **Remove Duplication**: Consolidate repeated code
- **Simplify Logic**: Use standard library functions, simplify conditionals
- **Improve Naming**: Use clearer, more descriptive names
- **Reduce Nesting**: Flatten deeply nested code

### Performance

- **Algorithm Improvement**: Use more efficient algorithms
- **Reduce Allocations**: Minimize object creation
- **Collection Efficiency**: Use appropriate collection types
- **Lazy Evaluation**: Use sequences for long chains
- **Inline Operations**: Consider inlining when beneficial

### Type Safety

- **Use Sealed Classes**: For type-safe alternatives to strings/enums
- **Data Classes**: Leverage data classes for value objects
- **Type Aliases**: Simplify complex type signatures
- **Generics**: Use generic types appropriately
- **Null Safety**: Reduce nullable types

### Kotlin Idioms

- **Extension Functions**: Add convenience functions to existing classes
- **Scope Functions**: Use `let`, `apply`, `also` appropriately
- **Standard Library**: Use built-in functions instead of manual loops
- **DSL Style**: Create cleaner APIs with receiver types
- **Destructuring**: Use when unpacking data classes

## Implementation Steps

1. **Analyze Current Code**: Understand the full context
2. **Design Refactoring**: Plan changes without breaking tests
3. **Implement Changes**: Apply refactoring incrementally
4. **Run Tests**: Verify tests still pass
5. **Document Changes**: Explain what changed and why
6. **Review Impact**: Consider effects on callers

## Constraints

- Maintain backward compatibility where possible
- Don't change the public API contract without approval
- Ensure all tests pass after refactoring
- Consider performance implications
- Maintain readability and maintainability

## Documentation

- Document why specific changes were made
- Explain new patterns or idioms used
- Provide migration path for API changes
- Update tests to match refactored code
- Include performance improvements (if applicable)

## Testing Strategy

- Run all existing tests to ensure behavior is preserved
- Add new tests for refactored code if coverage gaps exist
- Verify performance improvements with benchmarks (if applicable)
- Test edge cases remain covered

Please provide the code to refactor and clarify your refactoring goals.

