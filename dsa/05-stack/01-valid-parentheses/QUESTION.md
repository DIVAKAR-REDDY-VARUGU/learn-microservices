# Valid Parentheses

> **Pattern:** Stack → **Valid Parentheses**  ·  [📖 Learn this pattern](../../README.md#valid-parentheses)

## Problem

Given a string `s` containing only the characters `'('`, `')'`, `'{'`, `'}'`, `'['` and `']'`, determine whether the input string is **valid**.

A string is valid if and only if:
1. Every open bracket is closed by a bracket of the **same type**.
2. Open brackets are closed in the **correct order** (most recently opened is closed first).
3. Every close bracket has a corresponding open bracket of the same type.

Return `true` if `s` is valid, otherwise return `false`.

**Constraints:**
- `1 <= s.length <= 10^4`
- `s` consists only of the characters `()[]{}`.

## Examples

- Input: `s = "()[]{}"` -> Output: `true`
- Input: `s = "(]"` -> Output: `false`
- Input: `s = "([{}])"` -> Output: `true`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean isValid(String s) { /* your code */ }
```
