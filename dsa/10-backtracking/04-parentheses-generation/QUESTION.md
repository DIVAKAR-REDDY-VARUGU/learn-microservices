# Generate Parentheses

> **Pattern:** Backtracking → **Parentheses Generation**  ·  [📖 Learn this pattern](../../README.md#parentheses-generation)

## Problem

## Generate Parentheses

Given `n` pairs of parentheses, write a function to *generate all combinations of well-formed (valid) parentheses*.

A string of parentheses is well-formed if every opening bracket `(` has a matching closing bracket `)` and brackets are properly nested.

Use backtracking: track how many `(` and `)` have been placed. You may add `(` while its count is less than `n`, and add `)` only while the count of `)` is less than the count of `(`.

**Constraints:**
- `1 <= n <= 8`

## Examples

**Example 1:**
Input: `n = 3` -> Output: `["((()))","(()())","(())()","()(())","()()()"]`

**Example 2:**
Input: `n = 1` -> Output: `["()"]`

**Example 3:**
Input: `n = 2` -> Output: `["(())","()()"]`

(Any ordering of the strings is accepted.)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<String> generateParenthesis(int n) { /* your code */ }
```
