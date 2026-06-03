# Valid Palindrome

> **Pattern:** String Manipulation → **Palindrome Check (Two Pointers / Reverse)**  ·  [📖 Learn this pattern](../../README.md#palindrome-check-two-pointers--reverse)

## Problem

A phrase is a **palindrome** if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward. Alphanumeric characters include letters and digits.

Given a string `s`, return `true` if it is a palindrome, or `false` otherwise.

Use the **two pointers** technique: move one pointer from the start and one from the end, skipping non-alphanumeric characters, and compare the lowercased characters.

**Constraints:**
- `1 <= s.length <= 2 * 10^5`
- `s` consists only of printable ASCII characters.

## Examples

- Input: `s = "A man, a plan, a canal: Panama"` -> Output: `true` (reads as "amanaplanacanalpanama")
- Input: `s = "race a car"` -> Output: `false` (reads as "raceacar")
- Input: `s = " "` -> Output: `true` (empty string after cleaning)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean isPalindrome(String s) { /* your code */ }
```
