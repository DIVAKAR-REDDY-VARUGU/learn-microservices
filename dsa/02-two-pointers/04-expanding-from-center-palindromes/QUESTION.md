# Longest Palindromic Substring

> **Pattern:** Two Pointers → **Expanding From Center (Palindromes)**  ·  [📖 Learn this pattern](../../README.md#expanding-from-center-palindromes)

## Problem

Given a string `s`, return the **longest palindromic substring** in `s`.

A string is a palindrome when it reads the same forward and backward. A substring is a contiguous sequence of characters within the string.

A common approach is to treat each index (and each gap between two indices) as a potential center and expand two pointers outward while the characters match.

If there are multiple longest palindromic substrings of the same maximum length, returning any one of them is acceptable.

**Constraints:**
- `1 <= s.length <= 1000`
- `s` consists of only digits and English letters.

## Examples

- Input: `s = "babad"` -> Output: `"bab"` ("aba" is also a valid answer)
- Input: `s = "cbbd"` -> Output: `"bb"`
- Input: `s = "a"` -> Output: `"a"`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public String longestPalindrome(String s) { /* your code */ }
```
