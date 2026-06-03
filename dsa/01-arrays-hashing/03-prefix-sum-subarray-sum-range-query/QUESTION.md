# Subarray Sum Equals K

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Prefix Sum - Subarray Sum / Range Query**  ·  [📖 Learn this pattern](../../README.md#prefix-sum---subarray-sum--range-query)

## Problem

Given an array of integers `nums` and an integer `k`, return the total number of **contiguous subarrays** whose elements sum to exactly `k`.

Maintain a running prefix sum while iterating. Use a hash map (prefix sum -> number of times it has occurred) so that for each index you can count how many earlier prefix sums equal `currentPrefix - k`.

**Constraints:**
- `1 <= nums.length <= 2 * 10^4`
- `-1000 <= nums[i] <= 1000`
- `-10^7 <= k <= 10^7`

## Examples

- Input: `nums = [1,1,1]`, `k = 2` -> Output: `2` (subarrays `[1,1]` at indices 0-1 and 1-2)
- Input: `nums = [1,2,3]`, `k = 3` -> Output: `2` (subarrays `[1,2]` and `[3]`)
- Input: `nums = [1,-1,0]`, `k = 0` -> Output: `3` (subarrays `[1,-1]`, `[0]`, `[1,-1,0]`)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int subarraySum(int[] nums, int k) { /* your code */ }
```
