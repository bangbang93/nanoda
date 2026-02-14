---
applyTo: "**/*.kt,**/*.md,**/docs/**"
description: "Documentation requirements for API and user guides"
---

# Documentation Requirements

Apply the [general coding standards](../copilot-instructions.md) to all documentation.

## KDoc Comments

### Public API Documentation

- Document all public types (classes, interfaces, objects, sealed classes)
- Document all public functions and properties
- Avoid obvious documentation; focus on non-obvious behavior
- Include `@param` and `@return` tags
- Use `@throws` tags to document exceptions

### KDoc Structure

- First line is a summary ending with a period
- Blank line before detailed description
- Use `@param` for each parameter
- Use `@return` to describe return value
- Use `@throws` for each exception thrown
- Include usage examples for complex functions

### Example Format

```
/**
 * Processes the input string and returns the result.
 *
 * This function validates the input before processing.
 * Whitespace is trimmed automatically.
 *
 * @param input the string to process
 * @return the processed result
 * @throws IllegalArgumentException if input is empty
 */
```

## README Documentation

### Structure

- Clear project description and purpose
- Quick start/installation instructions
- Basic usage examples
- Links to detailed documentation
- License and contributor information
- Build and development instructions

### Content Guidelines

- Keep main README concise and focused
- Use code examples for clarity
- Link to API documentation (Dokka)
- Include badges for status, coverage, version
- Document dependencies and requirements

## CHANGELOG

### Format

- Follow semantic versioning (MAJOR.MINOR.PATCH)
- Group changes by type: Breaking, Added, Changed, Deprecated, Removed, Fixed
- Include version and date for each release
- Link to related issues/PRs when applicable
- Document migration paths for breaking changes

## API Documentation

### Dokka Generation

- Generate API docs with Dokka: `./gradlew dokkaGenerate`
- Document all public packages and modules
- Include package-level documentation in `package.md` files
- Add code examples in KDoc for complex APIs

### Code Examples

- Include minimal, runnable examples in documentation
- Show both successful and error cases
- Use realistic but simple scenarios
- Provide links to more comprehensive examples

## Version Documentation

### Version Information

- Document minimum Kotlin version
- Document minimum JVM version (target: 17)
- Document supported platforms
- Include deprecation timeline for API changes

## Comment Standards

### Code Comments

- Explain *why*, not *what* - code should be self-explanatory
- Use comments for non-obvious logic or workarounds
- Keep comments up-to-date with code changes
- Avoid commented-out code; use version control instead
- Use TODO, FIXME, HACK comments with explanation

## Documentation Organization

### File Structure

```
docs/
├── api/              # Generated Dokka documentation
├── guides/           # User guides and tutorials
├── architecture/     # Architecture decision records
└── examples/         # Code examples and samples
```

### Linking

- Use relative links within documentation
- Link to API documentation by class/function name
- Reference related documentation and guides
- Maintain links as documentation evolves

## Usage Examples

### Example Quality

- Keep examples concise and focused
- Show both basic and advanced usage
- Include error handling when relevant
- Provide context for why the example matters
- Link to full working examples in repository

## Breaking Changes

### Documentation

- Clearly mark breaking changes in release notes
- Provide migration guides with examples
- Deprecate before removing (if possible)
- Use `@Deprecated` annotation with migration info
- Document timeline for removal

## Developer Documentation

### Build Documentation

- Document build process and commands
- Include CI/CD pipeline documentation
- Document release procedures
- Include troubleshooting guides

### Contribution Guidelines

- Document coding standards
- Include pull request process
- Document testing requirements
- Provide setup instructions for developers

