# Backspace String Compare

> **Pattern:** Two Pointers → **String Comparison with Backspaces**  ·  [📖 Learn this pattern](../../README.md#string-comparison-with-backspaces)

## Problem

Given two strings `s` and `t`, return `true` if they are equal when both are typed into empty text editors. The character `'#'` means a **backspace** character.

Note that after backspacing an empty text, the text will continue to remain empty.

**Follow-up:** Can you solve it in `O(n)` time and `O(1)` space, scanning from the end of each string with two pointers?

**Constraints:**
- `1 <= s.length, t.length <= 200`
- `s` and `t` only contain lowercase letters and `'#'` characters.

## Examples

- Input: `s = "ab#c", t = "ad#c"` -> Output: `true` (both become "ac")
- Input: `s = "ab##", t = "c#d#"` -> Output: `true` (both become "")
- Input: `s = "a#c", t = "b"` -> Output: `false` (s becomes "c" while t becomes "b")

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean backspaceCompare(String s, String t) { /* your code */ }
```
