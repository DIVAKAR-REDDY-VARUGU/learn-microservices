# Palindrome Partitioning

> **Pattern:** Backtracking → **Palindrome Partitioning**  ·  [📖 Learn this pattern](../../README.md#palindrome-partitioning)

## Problem

## Palindrome Partitioning

Given a string `s`, partition `s` such that every substring of the partition is a **palindrome**. Return *all possible palindrome partitionings of `s`*. You may return the answer in **any order**.

A **palindrome** string is a string that reads the same backward as forward.

Use backtracking: try every prefix of the remaining string; if the prefix is a palindrome, add it to the current partition and recurse on the rest.

**Constraints:**
- `1 <= s.length <= 16`
- `s` contains only lowercase English letters.

## Examples

**Example 1:**
Input: `s = "aab"` -> Output: `[["a","a","b"],["aa","b"]]`

**Example 2:**
Input: `s = "a"` -> Output: `[["a"]]`

**Example 3:**
Input: `s = "aba"` -> Output: `[["a","b","a"],["aba"]]`

(Any ordering of the partitions is accepted.)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<List<String>> partition(String s) { /* your code */ }
```
