# Longest Common Subsequence

> **Pattern:** Dynamic Programming → **2D Longest Common Subsequence**  ·  [📖 Learn this pattern](../../README.md#2d-longest-common-subsequence)

## Problem

Given two strings `text1` and `text2`, return the length of their **longest common subsequence**. If there is no common subsequence, return `0`.

A **subsequence** of a string is a new string generated from the original string with some characters (can be none) deleted without changing the relative order of the remaining characters. A **common subsequence** of two strings is a subsequence that is common to both strings.

Use a 2D DP table where `dp[i][j]` is the LCS length of the prefixes `text1[0..i)` and `text2[0..j)`.

**Constraints:**
- `1 <= text1.length, text2.length <= 1000`
- `text1` and `text2` consist of only lowercase English characters.

## Examples

- Input: `text1 = "abcde", text2 = "ace"` -> Output: `3`  (the LCS is `"ace"`)
- Input: `text1 = "abc", text2 = "abc"` -> Output: `3`  (the LCS is `"abc"`)
- Input: `text1 = "abc", text2 = "def"` -> Output: `0`  (there is no common subsequence)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int longestCommonSubsequence(String text1, String text2) { /* your code */ }
```
