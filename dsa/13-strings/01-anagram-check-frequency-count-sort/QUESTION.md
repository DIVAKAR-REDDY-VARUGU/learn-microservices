# Valid Anagram

> **Pattern:** String Manipulation → **Anagram Check (Frequency Count/Sort)**  ·  [📖 Learn this pattern](../../README.md#anagram-check-frequency-countsort)

## Problem

Given two strings `s` and `t`, return `true` if `t` is an anagram of `s`, and `false` otherwise.

An **anagram** is a word or phrase formed by rearranging the letters of a different word or phrase, using all the original letters exactly once.

**Constraints:**
- `1 <= s.length, t.length <= 5 * 10^4`
- `s` and `t` consist of lowercase English letters.

**Follow-up:** Solve it in O(n) time using a frequency count of size 26, or alternatively by sorting both strings and comparing.

## Examples

- Input: `s = "anagram"`, `t = "nagaram"` -> Output: `true`
- Input: `s = "rat"`, `t = "car"` -> Output: `false`
- Input: `s = "a"`, `t = "ab"` -> Output: `false`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean isAnagram(String s, String t) { /* your code */ }
```
