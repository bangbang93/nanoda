---
applyTo: "**/*.kt"
description: "Performance optimization guidelines for the Nanoda library"
---

# Performance Guidelines

Apply the [general coding standards](../copilot-instructions.md) and prioritize performance in utility functions.

## Design for Performance

### API Design

- Minimize allocations in frequently-called code paths
- Provide non-allocating alternatives when possible
- Document performance characteristics of functions
- Use inline functions only when beneficial (measure before applying)
- Design APIs to enable efficient implementation by users

### Benchmarking

- Measure performance before and after optimizations
- Use realistic workloads in benchmarks
- Document performance baselines and goals
- Create benchmarks for performance-critical paths
- Avoid premature optimization; measure first

## Memory Management

### Object Allocation

- Minimize object creation in hot paths
- Reuse objects when safe and appropriate
- Consider value classes for small wrapper types
- Use arrays instead of collections for performance-critical code
- Be aware of boxing and unboxing costs

### Collection Efficiency

- Choose appropriate collection types (List vs Set vs Map)
- Use `capacity` parameter for collections when size is known
- Prefer immutable collections to reduce allocations
- Use sequences for lazy evaluation of large datasets
- Avoid unnecessary intermediate collections

## String Handling

### String Operations

- Use string templates over concatenation
- Avoid repeated string concatenation in loops
- Use `StringBuilder` or `buildString` for complex string building
- Consider using char arrays for very performance-critical code
- Be aware of string interning implications

### Regex Performance

- Compile regex patterns once, reuse multiple times
- Document regex performance characteristics
- Prefer simple string operations over regex when appropriate
- Consider regex alternatives for simple matching

## Algorithm Efficiency

### Algorithm Complexity

- Choose algorithms with appropriate time complexity
- Reduce unnecessary iterations through collections
- Avoid nested loops when possible
- Use efficient data structures (HashSet vs list for lookups)
- Consider lazy evaluation for large datasets

### Streaming and Lazy Evaluation

- Use sequences instead of intermediate lists for chains
- Leverage lazy operations: `asSequence()`, `filter`, `map`
- Avoid materializing sequences until necessary
- Document when eager vs lazy evaluation is appropriate

## Coroutines and Concurrency

### Coroutine Efficiency

- Use appropriate scope for coroutine execution
- Avoid creating coroutines for simple synchronous code
- Use structured concurrency properly
- Be aware of context switching costs
- Document threading and concurrency implications

## Code Optimization

### Micro-optimizations

- Profile before optimizing
- Focus on algorithmic improvements first
- Inline small frequently-called functions if beneficial
- Use `@PublishedApi` carefully when inlining
- Measure impact of optimizations

### Common Patterns

- Use `when` for efficient branching (compile to tableswitch)
- Leverage Kotlin compiler optimizations
- Use appropriate primitive types when beneficial
- Consider value classes for performance-critical code
- Use `by lazy` for expensive initializations

## Build Performance

### Gradle Configuration

- Keep dependencies minimal and up-to-date
- Use Gradle dependency caching
- Avoid expensive operations in build scripts
- Profile build times for slow configurations
- Document build requirements and constraints

## JVM-Specific Optimizations

### JVM Behavior

- Understand JIT compilation and warm-up
- Be aware of escape analysis implications
- Use `@JvmStatic` and `@JvmField` judiciously
- Consider platform-specific optimizations
- Document JVM version compatibility

### Garbage Collection

- Minimize allocations in latency-critical code
- Be aware of GC pause implications
- Use object pooling cautiously
- Document memory usage patterns
- Consider ZGC or other GC algorithms for specific needs

## Performance Testing

### Benchmarking Best Practices

- Use JMH (Java Microbenchmark Harness) for accuracy
- Test with realistic data sizes
- Warm up JIT before measurements
- Document performance test results
- Include baseline comparisons

### Load Testing

- Test with expected workloads
- Monitor resource usage under load
- Document scalability characteristics
- Test edge cases and boundary conditions
- Include stress testing where appropriate

## Documentation

### Performance Documentation

- Document algorithmic complexity (O(n), etc.)
- Specify memory usage patterns
- Explain performance trade-offs
- Provide tuning recommendations
- Include benchmarking results

### Guidelines

- Suggest when to use specific functions
- Document when functions have side effects
- Explain streaming vs eager operation differences
- Provide performance tips for users
- Link to performance-related decisions

