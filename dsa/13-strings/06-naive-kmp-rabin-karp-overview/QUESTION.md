# Find the Index of the First Occurrence in a String

> **Pattern:** String Manipulation → **Naive/KMP/Rabin-Karp (overview)**  ·  [📖 Learn this pattern](../../README.md#naivekmprabin-karp-overview)

## Problem

Given two strings `needle` and `haystack`, return the index of the first occurrence of `needle` in `haystack`, or `-1` if `needle` is not part of `haystack`.

This is the canonical **substring search** problem. It can be solved with a naive O(n*m) scan, or in O(n+m) time using the **KMP** algorithm (with a precomputed longest-prefix-suffix table) or the **Rabin-Karp** rolling-hash technique.

**Constraints:**
- `1 <= haystack.length, needle.length <= 10^4`
- `haystack` and `needle` consist of only lowercase English characters.

## Examples

- Input: `haystack = "sadbutsad"`, `needle = "sad"` -> Output: `0` (occurs at index 0 and 6; first is 0)
- Input: `haystack = "leetcode"`, `needle = "leeto"` -> Output: `-1` ("leeto" never occurs)
- Input: `haystack = "hello"`, `needle = "ll"` -> Output: `2`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int strStr(String haystack, String needle) { /* your code */ }
```
