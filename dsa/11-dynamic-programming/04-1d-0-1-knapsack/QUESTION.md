# Partition Equal Subset Sum

> **Pattern:** Dynamic Programming → **1D 0/1 Knapsack**  ·  [📖 Learn this pattern](../../README.md#1d-01-knapsack)

## Problem

Given an integer array `nums`, return `true` if you can partition the array into two subsets such that the sum of the elements in both subsets is equal, or `false` otherwise.

This reduces to a 0/1 knapsack: determine whether some subset of `nums` sums to exactly `totalSum / 2`. Each element may be used at most once.

**Constraints:**
- `1 <= nums.length <= 200`
- `1 <= nums[i] <= 100`

## Examples

- Input: `nums = [1,5,11,5]` -> Output: `true`  (the array can be partitioned as `[1,5,5]` and `[11]`)
- Input: `nums = [1,2,3,5]` -> Output: `false`  (the array cannot be partitioned into equal-sum subsets)
- Input: `nums = [2,2,2,2]` -> Output: `true`  (partition into `[2,2]` and `[2,2]`)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean canPartition(int[] nums) { /* your code */ }
```
