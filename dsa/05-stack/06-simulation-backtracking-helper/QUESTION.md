# Decode String

> **Pattern:** Stack → **Simulation/Backtracking Helper**  ·  [📖 Learn this pattern](../../README.md#simulationbacktracking-helper)

## Problem

Given an encoded string `s`, return its decoded string.

The encoding rule is: `k[encoded_string]`, where the `encoded_string` inside the square brackets is repeated exactly `k` times. Here `k` is guaranteed to be a positive integer.

You may assume the input string is always valid: there are no extra white spaces, square brackets are well-formed, and the original data does not contain any digits — digits only appear as the repeat count `k`. Encodings may be nested.

Use a stack to simulate processing: push the current count and accumulated string when entering a `[`, and pop to combine when reaching the matching `]`.

**Constraints:**
- `1 <= s.length <= 30`
- `s` consists of lowercase English letters, digits, and the characters `[` and `]`.
- The decoded string length fits within standard limits (`<= 10^5`).

## Examples

- Input: `s = "3[a]2[bc]"` -> Output: `"aaabcbc"`
- Input: `s = "3[a2[c]]"` -> Output: `"accaccacc"`
- Input: `s = "2[abc]3[cd]ef"` -> Output: `"abcabccdcdcdef"`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public String decodeString(String s) { /* your code */ }
```
