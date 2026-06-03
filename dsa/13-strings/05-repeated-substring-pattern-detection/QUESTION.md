# Repeated Substring Pattern

> **Pattern:** String Manipulation → **Repeated Substring Pattern Detection**  ·  [📖 Learn this pattern](../../README.md#repeated-substring-pattern-detection)

## Problem

Given a string `s`, check if it can be constructed by taking a substring of it and appending multiple copies of that substring together.

Return `true` if such a substring exists, otherwise return `false`.

**Hint:** A useful trick is that `s` has a repeated substring pattern if and only if `s` appears as a substring of `(s + s)` with the first and last characters removed. Alternatively, try every divisor length `len` of `s.length` and check whether repeating the prefix of that length reconstructs `s`.

**Constraints:**
- `1 <= s.length <= 10^4`
- `s` consists of lowercase English letters.

## Examples

- Input: `s = "abab"` -> Output: `true` (it is "ab" repeated twice)
- Input: `s = "aba"` -> Output: `false`
- Input: `s = "abcabcabcabc"` -> Output: `true` (it is "abc" repeated four times, or "abcabc" twice)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean repeatedSubstringPattern(String s) { /* your code */ }
```
