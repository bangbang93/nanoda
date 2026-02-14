---
applyTo: "**/*"
description: "Code review standards and GitHub review guidelines"
---

# Code Review Standards

Apply the [general coding standards](../copilot-instructions.md) and use these guidelines for peer reviews.

## Review Objectives

### Primary Goals

- Ensure code quality and maintainability
- Share knowledge across the team
- Catch bugs and potential issues early
- Promote consistent coding standards
- Improve overall project health

### Review Scope

- Functional correctness
- Code quality and style adherence
- Test coverage and quality
- Documentation completeness
- Performance and security considerations

## What to Review

### Functional Correctness

- Does the code implement the intended behavior?
- Are edge cases handled appropriately?
- Is error handling complete and correct?
- Do tests verify the implementation?
- Are there potential race conditions or deadlocks?

### Code Quality

- Is the code clean, readable, and maintainable?
- Are functions small and focused (single responsibility)?
- Is naming clear and descriptive?
- Does it follow project conventions?
- Is there unnecessary duplication?

### Testing

- Are tests comprehensive?
- Do tests cover happy paths and error cases?
- Are tests isolated and deterministic?
- Is coverage adequate (target >80%)?
- Are edge cases tested?

### Documentation

- Are public APIs documented with KDoc?
- Are complex algorithms explained?
- Is the README updated if needed?
- Are breaking changes documented?
- Are examples provided where helpful?

### Performance

- Are there obvious performance issues?
- Is memory usage reasonable?
- Are expensive operations in appropriate places?
- Are there unnecessary allocations?
- Are algorithms appropriate for the use case?

### Security

- Is all public input validated?
- Are sensitive values protected?
- Are exceptions handled securely?
- Are dependencies current?
- Are there potential security vulnerabilities?

## Review Process

### Starting a Review

- Understand the change purpose and context
- Read the PR description and related issues
- Review tests first to understand expected behavior
- Then review the implementation
- Check for side effects and interactions

### Providing Feedback

#### Praise and Encouragement

- Acknowledge good work and clever solutions
- Thank the author for the effort
- Celebrate improvements in code quality
- Use positive tone even for critical feedback

#### Constructive Comments

- Explain *why* you're suggesting a change
- Provide specific examples and suggestions
- Link to relevant guidelines and standards
- Suggest alternatives when appropriate
- Consider the author's perspective

#### Comment Types

- **Blocker**: Must fix before merge
- **Important**: Should fix before merge
- **Nice to have**: Consider addressing
- **Question**: Seeking clarification
- **Suggestion**: Optional improvement

### Handling Disagreement

- Discuss rationale and trade-offs
- Reference project standards and guidelines
- Be open to alternative approaches
- Consider context and constraints
- Escalate if consensus can't be reached

## Specific Review Criteria

### Kotlin Code Review

- Are variables using `val` instead of `var`?
- Is null safety properly utilized?
- Are extension functions used appropriately?
- Is the code idiomatic Kotlin?
- Are standard library functions used effectively?

### Test Code Review

- Do tests follow BDD style with Kotest?
- Are test names descriptive?
- Are tests properly isolated?
- Is test data appropriate and minimal?
- Are mocks used appropriately?

### Documentation Review

- Is KDoc complete for public APIs?
- Are examples accurate and helpful?
- Is documentation up-to-date?
- Are code comments explaining *why*, not *what*?
- Is technical writing clear?

## GitHub Review Features

### Using GitHub Review Tools

- Use "Request Changes" for blocking issues
- Use "Comment" for general feedback
- Use "Approve" when ready to merge
- Use "Suggested Changes" for minor edits
- Group related comments into comprehensive reviews

### PR Templates

- Check if PR follows template
- Verify description is clear
- Confirm related issues are referenced
- Ensure tests are included
- Check that documentation is updated

### Automated Checks

- Wait for CI/CD to pass
- Review automated quality reports
- Check code coverage changes
- Verify dependency vulnerabilities
- Confirm build succeeds

## Turnaround Time

### Response Expectations

- Initial review within 24 hours
- Follow-up within 12 hours if changes requested
- Avoid blocking reviews for extended periods
- Communicate delays or blocking issues
- Prioritize urgent/critical changes

## Common Issues to Catch

### Architecture Issues

- Tight coupling between modules
- Circular dependencies
- Violation of SOLID principles
- Missing abstraction layers
- Inappropriate data structures

### Quality Issues

- Code duplication
- Complex nested logic
- Long functions or classes
- Magic strings/numbers
- Inconsistent naming

### Testing Issues

- Insufficient test coverage
- Tests that don't verify behavior
- Hard-to-read or complex tests
- Missing edge case testing
- Brittle tests checking implementation

### Documentation Issues

- Missing KDoc comments
- Outdated documentation
- Unclear or incomplete examples
- Undocumented breaking changes
- Poor error messages

## Best Practices

### As a Reviewer

- Review promptly and thoroughly
- Be respectful and constructive
- Ask questions when unclear
- Acknowledge good work
- Help authors improve
- Focus on code, not person
- Be consistent in standards

### As an Author

- Keep PRs focused and reasonably sized
- Provide clear description
- Respond to feedback gracefully
- Ask questions if feedback unclear
- Update based on feedback
- Thank reviewers for time
- Learn from feedback

## Escalation Paths

### When to Escalate

- Fundamental disagreement on approach
- Blocking issue that's unclear
- Performance or security concerns
- Architectural decisions
- Standards interpretation

### Escalation Process

- Document the discussion and rationale
- Involve appropriate stakeholders
- Seek consensus or decision
- Document final decision
- Update standards if needed

## Continuous Improvement

### Learning from Reviews

- Use feedback to improve skills
- Share common patterns and issues
- Update standards based on learnings
- Create guidelines for common problems
- Hold regular knowledge-sharing sessions

