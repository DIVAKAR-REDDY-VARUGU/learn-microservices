# Word Break

> **Pattern:** Dynamic Programming → **1D Word Break**  ·  [📖 Learn this pattern](../../README.md#1d-word-break)

## Problem

Given a string `s` and a dictionary of strings `wordDict`, return `true` if `s` can be segmented into a space-separated sequence of one or more dictionary words.

Note that the same word in the dictionary may be reused multiple times in the segmentation. Use a 1D DP where `dp[i]` indicates whether the prefix `s[0..i)` can be segmented.

**Constraints:**
- `1 <= s.length <= 300`
- `1 <= wordDict.length <= 1000`
- `1 <= wordDict[i].length <= 20`
- `s` and `wordDict[i]` consist of only lowercase English letters.
- All the strings of `wordDict` are unique.

## Examples

- Input: `s = "leetcode", wordDict = ["leet","code"]` -> Output: `true`  (`"leetcode"` = `"leet" + "code"`)
- Input: `s = "applepenapple", wordDict = ["apple","pen"]` -> Output: `true`  (`"apple pen apple"`, reusing `"apple"`)
- Input: `s = "catsandog", wordDict = ["cats","dog","sand","and","cat"]` -> Output: `false`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean wordBreak(String s, List<String> wordDict) { /* your code */ }
```
