# Find Minimum in Rotated Sorted Array

> **Pattern:** Binary Search → **Find Min/Max in Rotated Sorted Array**  ·  [📖 Learn this pattern](../../README.md#find-minmax-in-rotated-sorted-array)

## Problem

Suppose an array of length `n` sorted in ascending order is **rotated** between `1` and `n` times. For example, the array `nums = [0, 1, 2, 4, 5, 6, 7]` might become:
- `[4, 5, 6, 7, 0, 1, 2]` if it was rotated 4 times.
- `[0, 1, 2, 4, 5, 6, 7]` if it was rotated 7 times.

Given the sorted rotated array `nums` of **unique** elements, return the **minimum element** of this array.

You must write an algorithm that runs in `O(log n)` time.

**Constraints:**
- `n == nums.length`
- `1 <= n <= 5000`
- `-5000 <= nums[i] <= 5000`
- All the integers of `nums` are unique.
- `nums` is sorted and rotated between `1` and `n` times.

## Examples

**Example 1**
- Input: `nums = [3, 4, 5, 1, 2]`
- Output: `1`

**Example 2**
- Input: `nums = [4, 5, 6, 7, 0, 1, 2]`
- Output: `0`

**Example 3**
- Input: `nums = [11, 13, 15, 17]`
- Output: `11`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int findMin(int[] nums) { /* your code */ }
```
