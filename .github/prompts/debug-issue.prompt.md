---
agent: 'agent'
model: Claude Sonnet 4
tools: [ 'githubRepo', 'codebase', 'problems' ]
description: 'Debug and fix issues in Kotlin code'
---

# Debug and Fix Issues

Your goal is to debug and fix issues in the Kotlin codebase.

## Problem Analysis Phase

Start by understanding the issue:

1. **Error Description**: What error or unexpected behavior occurs?
2. **Reproduction Steps**: How to reproduce the issue?
3. **Expected Behavior**: What should happen?
4. **Actual Behavior**: What actually happens?
5. **Error Messages**: Any stack traces or error messages?
6. **Affected Code**: Which code is involved?
7. **When Introduced**: Did it ever work? When did it break?

## Investigation Strategy

### Gather Information

- Review relevant code sections
- Check error messages and stack traces
- Look for recent changes that might cause the issue
- Review related tests for expected behavior
- Check for null pointer issues, type mismatches, etc.

### Common Kotlin Issues

- **Null Pointer Exceptions**: Missing null checks or !!
- **Type Mismatches**: Incorrect generic types or casts
- **Collection Issues**: Empty collections, wrong collection types
- **Exception Handling**: Incorrect exception handling or uncaught exceptions
- **Concurrency**: Race conditions, deadlocks (if using coroutines)
- **Resource Leaks**: Unclosed resources or connections

### Debugging Techniques

- Add logging to understand execution flow
- Check variable values at key points
- Verify assumptions about input data
- Test individual components in isolation
- Use step-through debugging if needed

## Root Cause Analysis

Once you identify the issue:

1. **Pinpoint Root Cause**: What is the actual problem?
2. **Understand Impact**: How widespread is this?
3. **Verify Cause**: Confirm your hypothesis
4. **Consider Related Issues**: Are there similar problems?

## Solution Approach

### Fix Implementation

- Make minimal changes to fix the issue
- Don't over-engineer the fix
- Maintain backward compatibility
- Follow project coding standards
- Add appropriate error handling

### Code Changes

- Fix only the identified issue
- Don't refactor unless necessary
- Maintain readability
- Include explanatory comments for non-obvious fixes
- Consider edge cases in the fix

### Testing

- Write tests that reproduce the original issue
- Verify the fix resolves the issue
- Check that existing tests still pass
- Test edge cases related to the bug
- Add regression tests to prevent recurrence

## Documentation

After fixing:

1. **Comment Complex Fixes**: Explain why the fix works
2. **Update Tests**: Add tests for the bug
3. **Document Changes**: Note what was changed and why
4. **Record Decisions**: Document any design decisions in the fix

## Root Cause Categories

- **Logic Errors**: Incorrect conditionals, wrong operators
- **Null Safety**: Missing null checks or misuse of nullable types
- **Type Errors**: Wrong types, incorrect casts, generics issues
- **Collection Issues**: Empty collections, wrong access patterns
- **Resource Management**: Not closing resources, memory leaks
- **Exception Handling**: Missing or incorrect exception handling
- **Concurrency**: Race conditions, improper synchronization
- **State Management**: Incorrect variable initialization, mutation issues

## Verification Checklist

- [ ] Issue is reproduced
- [ ] Root cause identified
- [ ] Fix addresses root cause
- [ ] Tests verify the fix
- [ ] No new issues introduced
- [ ] Related code checked
- [ ] Documentation updated
- [ ] Build succeeds
- [ ] Tests pass

Please describe the issue you need to debug and fix.

