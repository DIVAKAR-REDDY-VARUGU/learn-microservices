# Find All Anagrams in a String

> **Pattern:** Sliding Window → **Character Frequency Matching**  ·  [📖 Learn this pattern](../../README.md#character-frequency-matching)

## Problem

## Find All Anagrams in a String

Given two strings `s` and `p`, return a list of all the **start indices** of `p`'s anagrams in `s`. You may return the answer in **any order**.

An **anagram** is a string formed by rearranging the letters of another string, using all the original letters exactly once.

**Constraints:**
- `1 <= s.length, p.length <= 3 * 10^4`
- `s` and `p` consist of lowercase English letters only.

**Approach hint:** Use a fixed-size sliding window of length `p.length()` over `s`, maintaining character-frequency counts. Compare the window's frequency map against `p`'s frequency map (e.g., via a 26-length count array). When they match, record the window's start index.

## Examples

- **Input:** `s = "cbaebabacd"`, `p = "abc"` -> **Output:** `[0,6]` (substring at index 0 is `"cba"` and at index 6 is `"bac"`, both anagrams of `"abc"`)
- **Input:** `s = "abab"`, `p = "ab"` -> **Output:** `[0,1,2]` (`"ab"`, `"ba"`, and `"ab"` are all anagrams of `"ab"`)
- **Input:** `s = "af"`, `p = "be"` -> **Output:** `[]` (no anagram of `"be"` appears in `s`)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<Integer> findAnagrams(String s, String p) { /* your code */ }
```
