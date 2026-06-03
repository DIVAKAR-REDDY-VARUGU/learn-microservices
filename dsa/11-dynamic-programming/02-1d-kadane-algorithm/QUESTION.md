# Maximum Subarray

> **Pattern:** Dynamic Programming → **1D Kadane Algorithm**  ·  [📖 Learn this pattern](../../README.md#1d-kadane-algorithm)

## Problem

Given an integer array `nums`, find the contiguous subarray (containing at least one number) which has the largest sum, and return that sum.

A subarray is a contiguous, non-empty sequence of elements within the array. Use Kadane's algorithm: at each index, decide whether to extend the previous running sum or start a new subarray at the current element.

**Constraints:**
- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`

## Examples

- Input: `nums = [-2,1,-3,4,-1,2,1,-5,4]` -> Output: `6`  (the subarray `[4,-1,2,1]` has the largest sum `6`)
- Input: `nums = [1]` -> Output: `1`
- Input: `nums = [5,4,-1,7,8]` -> Output: `23`  (the entire array sums to `23`)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int maxSubArray(int[] nums) { /* your code */ }
```
