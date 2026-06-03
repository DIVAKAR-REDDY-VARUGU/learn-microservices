# Burst Balloons

> **Pattern:** Dynamic Programming → **Interval DP**  ·  [📖 Learn this pattern](../../README.md#interval-dp)

## Problem

You are given `n` balloons, indexed from `0` to `n - 1`. Each balloon is painted with a number on it represented by an array `nums`. You are asked to burst all the balloons.

If you burst the `i`-th balloon, you will get `nums[i - 1] * nums[i] * nums[i + 1]` coins. If `i - 1` or `i + 1` goes out of bounds of the array, then treat it as if there is a balloon with a `1` painted on it.

Return the **maximum coins** you can collect by bursting the balloons wisely.

This is a classic interval DP: define `dp[left][right]` as the maximum coins from bursting all balloons strictly between `left` and `right`, considering each balloon `k` in that range as the **last** one to be burst in the interval.

**Constraints:**
- `n == nums.length`
- `1 <= n <= 300`
- `0 <= nums[i] <= 100`

## Examples

- Input: `nums = [3,1,5,8]` -> Output: `167`  (burst order yields `3*1*5 + 3*5*8 + 1*3*8 + 1*8*1 = 15 + 120 + 24 + 8 = 167`)
- Input: `nums = [1,5]` -> Output: `10`  (`1*1*5 + 1*5*1 = 5 + 5 = 10`)
- Input: `nums = [7]` -> Output: `7`  (`1*7*1 = 7`)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int maxCoins(int[] nums) { /* your code */ }
```
