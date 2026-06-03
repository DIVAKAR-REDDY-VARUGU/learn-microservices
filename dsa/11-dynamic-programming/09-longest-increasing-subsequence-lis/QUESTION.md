# Longest Increasing Subsequence

> **Pattern:** Dynamic Programming → **Longest Increasing Subsequence (LIS)**  ·  [📖 Learn this pattern](../../README.md#longest-increasing-subsequence-lis)

## Problem

Given an integer array `nums`, return the length of the **longest strictly increasing subsequence**.

A **subsequence** is a sequence that can be derived from an array by deleting some or no elements without changing the order of the remaining elements.

A classic `O(n^2)` DP solution defines `dp[i]` as the length of the longest increasing subsequence ending at index `i`. An optimal `O(n log n)` solution uses patience sorting with binary search.

**Constraints:**
- `1 <= nums.length <= 2500`
- `-10^4 <= nums[i] <= 10^4`

## Examples

- Input: `nums = [10,9,2,5,3,7,101,18]` -> Output: `4`  (the LIS is `[2,3,7,101]`)
- Input: `nums = [0,1,0,3,2,3]` -> Output: `4`  (the LIS is `[0,1,2,3]`)
- Input: `nums = [7,7,7,7,7,7,7]` -> Output: `1`  (strictly increasing, so only one element qualifies)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int lengthOfLIS(int[] nums) { /* your code */ }
```
