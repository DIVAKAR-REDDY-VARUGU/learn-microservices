# Edit Distance

> **Pattern:** Dynamic Programming → **2D Edit Distance**  ·  [📖 Learn this pattern](../../README.md#2d-edit-distance)

## Problem

Given two strings `word1` and `word2`, return the **minimum number of operations** required to convert `word1` to `word2`.

You have the following three operations permitted on a word:
- Insert a character
- Delete a character
- Replace a character

Use a 2D DP table where `dp[i][j]` is the minimum edit distance between the prefixes `word1[0..i)` and `word2[0..j)` (this is the Levenshtein distance).

**Constraints:**
- `0 <= word1.length, word2.length <= 500`
- `word1` and `word2` consist of lowercase English letters.

## Examples

- Input: `word1 = "horse", word2 = "ros"` -> Output: `3`  (horse -> rorse (replace 'h' with 'r'), rorse -> rose (delete 'r'), rose -> ros (delete 'e'))
- Input: `word1 = "intention", word2 = "execution"` -> Output: `5`
- Input: `word1 = "", word2 = "abc"` -> Output: `3`  (insert 'a', 'b', 'c')

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int minDistance(String word1, String word2) { /* your code */ }
```
