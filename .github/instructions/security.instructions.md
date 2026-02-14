---
applyTo: "**/*.kt,**/build.gradle.kts"
description: "Security best practices for the Nanoda library"
---

# Security Best Practices

Apply the [general coding standards](../copilot-instructions.md) for all security-related code.

## Input Validation

### Public API Protection

- Validate all inputs to public functions
- Use `require()` for preconditions and `check()` for postconditions
- Reject null values explicitly when not accepted
- Validate string lengths, numeric ranges, and collection sizes
- Provide clear error messages that help debugging but don't leak implementation details

### Data Validation

- Validate data format and structure before processing
- Check file sizes and types before loading
- Validate URL and path inputs to prevent directory traversal
- Sanitize any user-provided data used in system commands
- Validate collection contents when type parameters are generic

## Exception Handling

### Secure Error Messages

- Provide helpful error messages without exposing internal details
- Avoid logging sensitive information (passwords, tokens, PII)
- Don't expose stack traces to users
- Log exceptions internally for debugging
- Use specific exception types for different error conditions

### Exception Specificity

- Create domain-specific exception classes
- Avoid catching generic `Exception` or `Throwable`
- Catch specific exceptions to handle expected errors
- Use sealed exception hierarchies for common patterns
- Document what exceptions functions throw

## Dependency Security

### Dependency Management

- Keep dependencies up-to-date with security patches
- Review new dependencies for security issues
- Use gradle/libs.versions.toml for centralized version management
- Monitor for CVE announcements for dependencies
- Document dependency rationale for security-critical choices

### Transitive Dependencies

- Review transitive dependencies regularly
- Exclude problematic transitive dependencies when needed
- Keep lock files up-to-date if used
- Use dependency verification for important projects

## Code Security

### Null Safety

- Use Kotlin's null safety features to prevent NPE
- Avoid `!!` operator except where truly safe
- Use safe navigation (`?.`) and elvis operator (`?:`)
- Prevent null pointer exceptions through design

### Resource Management

- Use `use` blocks for AutoCloseable resources
- Ensure resources are closed even if exceptions occur
- Limit resource consumption to prevent DoS
- Clean up temporary files and connections

### Type Safety

- Leverage Kotlin's type system to prevent type confusion
- Use sealed classes for exhaustive type checking
- Avoid unchecked casts
- Use platform types carefully from Java interop

## Cryptography and Secrets

### Secret Handling

- Never hardcode secrets, keys, or passwords
- Use external configuration for sensitive data
- Don't log or expose secrets in error messages
- Use environment variables or secure vaults for configuration
- Rotate credentials regularly

### Secure Defaults

- Use secure default configurations
- Require explicit opt-in for less secure modes
- Document security implications of configuration options
- Use strong algorithms and key sizes
- Disable insecure protocols and algorithms

## API Security

### Rate Limiting and Throttling

- Document performance expectations
- Consider resource consumption in utility functions
- Validate operation counts and sizes
- Prevent infinite loops in user-supplied functions
- Monitor for resource exhaustion attacks

### Data Exposure

- Minimize data exposure in public APIs
- Don't expose internal implementation details
- Use data encapsulation (private/internal modifiers)
- Document what data functions access or modify
- Consider performance implications of security checks

## Build Security

### Dependency Verification

- Enable dependency verification in Gradle when appropriate
- Review build configurations for security issues
- Use version catalogs to prevent version conflicts
- Document build tool versions and requirements
- Use artifact signing for releases

### Gradle Configuration

- Review plugin sources and security practices
- Limit buildscript configuration exposure
- Keep build scripts simple and auditable
- Use published plugins from trusted sources
- Avoid executing untrusted code in build scripts

## Testing Security

### Security Testing

- Test input validation for various invalid inputs
- Test error handling doesn't expose sensitive information
- Test resource limits and bounds
- Test null handling and edge cases
- Document security assumptions and guarantees

## Compliance and Standards

### OWASP Principles

- Follow OWASP Top 10 for relevant categories
- Validate and sanitize inputs
- Use parameterized queries for database operations
- Implement proper access controls
- Use HTTPS for network communications

### Security Documentation

- Document security assumptions
- Document threat model for library
- Provide security guidance for users
- Include security contact information
- Create security policy for vulnerability reporting

