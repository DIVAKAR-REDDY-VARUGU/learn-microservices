# Find First and Last Position of Element in Sorted Array

> **Pattern:** Binary Search → **Find First/Last Occurrence**  ·  [📖 Learn this pattern](../../README.md#find-firstlast-occurrence)

## Problem

Given an array of integers `nums` sorted in **non-decreasing order**, find the starting and ending position of a given `target` value.

If `target` is not found in the array, return `[-1, -1]`.

You must write an algorithm with `O(log n)` runtime complexity.

**Constraints:**
- `0 <= nums.length <= 10^5`
- `-10^9 <= nums[i] <= 10^9`
- `nums` is a non-decreasing array (duplicates allowed).
- `-10^9 <= target <= 10^9`

## Examples

**Example 1**
- Input: `nums = [5, 7, 7, 8, 8, 10]`, `target = 8`
- Output: `[3, 4]`

**Example 2**
- Input: `nums = [5, 7, 7, 8, 8, 10]`, `target = 6`
- Output: `[-1, -1]`

**Example 3**
- Input: `nums = []`, `target = 0`
- Output: `[-1, -1]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] searchRange(int[] nums, int target) { /* your code */ }
```
