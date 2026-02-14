---
agent: 'agent'
model: Claude Sonnet 4
tools: [ 'githubRepo', 'codebase' ]
description: 'Generate comprehensive KDoc and API documentation'
---

# Generate Documentation

Your goal is to generate comprehensive documentation for the specified Kotlin code.

## Documentation Types

### KDoc Comments

Generate complete KDoc comments for:

- Classes and interfaces (explain purpose and usage)
- Public functions (explain parameters, return value, exceptions)
- Properties (explain what they represent)
- Enums and sealed classes (explain each variant)

### KDoc Format

```kotlin
/**
 * Brief one-line summary ending with a period.
 *
 * Detailed description explaining the functionality,
 * important behavior, and usage patterns.
 *
 * @param paramName description of parameter
 * @return description of return value
 * @throws ExceptionType when this condition occurs
 */
```

### Usage Examples

- Include minimal, runnable examples
- Show both common and edge cases
- Explain non-obvious behavior
- Link to related functions

### Comments for Complex Logic

- Explain *why*, not *what*
- Document non-obvious algorithms
- Explain workarounds and compromises
- Reference related decision points

## Documentation Scope

Ask about these items if not provided:

1. **API Documentation**: Generate complete KDoc for public APIs
2. **README Updates**: Update README with usage examples
3. **Architecture Notes**: Explain design decisions if needed
4. **Code Comments**: Document complex logic and edge cases
5. **Parameter Documentation**: Clarify each parameter's role and constraints

## Guidelines to Follow

### Public API Documentation

- Document all public types and functions
- Include parameter constraints (nullability, valid ranges, etc.)
- Document return value and typical use cases
- List all exceptions that can be thrown
- Provide at least one usage example

### Example Quality

- Keep examples concise and focused
- Show practical, realistic usage
- Include error handling when relevant
- Explain what the example demonstrates
- Make examples runnable/testable

### Breaking Changes

- Document API changes clearly
- Provide migration examples for deprecated APIs
- Specify deprecation timeline
- Use `@Deprecated` annotation with `ReplaceWith`
- Link to new alternatives

## Documentation Standards

Follow these conventions:

- **Tone**: Professional, clear, and helpful
- **Tense**: Use present tense
- **Voice**: Active voice preferred
- **Code Formatting**: Use backticks for code references
- **Links**: Use markdown links to related docs
- **Examples**: Use kotlin code blocks

## Content Checklist

- [ ] All public classes documented
- [ ] All public functions documented
- [ ] All public properties documented
- [ ] Parameters explained (type, constraints, nullability)
- [ ] Return values explained
- [ ] Exceptions documented
- [ ] Usage examples provided
- [ ] Complex logic commented
- [ ] Performance characteristics noted (if relevant)
- [ ] Thread safety documented (if applicable)
- [ ] Breaking changes documented

## Dokka Generation

After adding KDoc:

- Generate documentation with: `./gradlew dokkaGenerate`
- Documentation will be in: `build/dokka/html`
- Verify all public APIs appear in generated docs
- Check code examples render correctly

Please provide the code that needs documentation.

