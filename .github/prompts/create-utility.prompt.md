---
agent: 'agent'
model: Claude Sonnet 4
tools: [ 'githubRepo', 'codebase' ]
description: 'Create a new Kotlin utility function or extension'
---

# Create New Kotlin Utility Function

Your goal is to create a new Kotlin utility function or extension based on the requirements provided.

## Gathering Requirements

Ask for the following information if not provided:

1. **Function Purpose**: What problem does this function solve?
2. **Input Parameters**: What data does it accept? What are the types?
3. **Return Value**: What should it return?
4. **Error Cases**: How should it handle invalid input?
5. **Performance Requirements**: Any specific performance constraints?
6. **Integration Point**: Where should this function live in the codebase?

## Implementation Guidelines

Follow these guidelines when creating the function:

- **Follow Kotlin idioms**: Use Kotlin standard library functions, extension functions, and scope functions
  appropriately
- **Type Safety**: Leverage Kotlin's type system. Use sealed classes and data classes where appropriate
- **Error Handling**: Validate inputs using `require()` for preconditions. Use specific exception types
- **Documentation**: Include comprehensive KDoc with `@param`, `@return`, and `@throws` tags
- **Testing**: Create unit tests using Kotest BDD style (`describe`, `context`, `it` blocks)
- **Performance**: Consider memory allocation and algorithmic efficiency
- **Null Safety**: Use proper null handling with safe calls (`?.`) and elvis operator (`?:`)

## Implementation Steps

1. **Analyze Requirements**: Understand the exact requirements and edge cases
2. **Design the API**: Create a clean, intuitive function signature
3. **Implement Function**: Write the implementation following Kotlin best practices
4. **Write KDoc**: Document the function comprehensively
5. **Create Tests**: Write unit tests covering happy paths and error cases
6. **Review**: Verify the implementation matches requirements and follows standards

## Code Examples

Reference the existing utility functions in `src/main/kotlin/` to maintain consistency with the codebase style.

