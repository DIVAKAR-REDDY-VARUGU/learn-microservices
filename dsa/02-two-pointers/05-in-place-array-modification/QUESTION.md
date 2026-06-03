# Remove Duplicates from Sorted Array

> **Pattern:** Two Pointers → **In-place Array Modification**  ·  [📖 Learn this pattern](../../README.md#in-place-array-modification)

## Problem

Given an integer array `nums` sorted in **non-decreasing order**, remove the duplicates **in-place** such that each unique element appears only **once**. The relative order of the elements should be kept the **same**. Then return the number of unique elements `k`.

The judge considers your solution correct if, after your modification:
- The first `k` elements of `nums` contain the unique elements in their original order.
- The remaining elements of `nums` beyond the first `k` do not matter.

You must use a slow/fast two-pointer strategy and `O(1)` extra memory.

**Constraints:**
- `1 <= nums.length <= 3 * 10^4`
- `-100 <= nums[i] <= 100`
- `nums` is sorted in non-decreasing order.

## Examples

- Input: `nums = [1,1,2]` -> Output: `2`, with `nums` becoming `[1,2,_]` (the first 2 elements are the unique values; underscore marks an irrelevant slot)
- Input: `nums = [0,0,1,1,1,2,2,3,3,4]` -> Output: `5`, with `nums` becoming `[0,1,2,3,4,_,_,_,_,_]`
- Input: `nums = [1,2,3]` -> Output: `3`, with `nums` becoming `[1,2,3]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int removeDuplicates(int[] nums) { /* your code */ }
```
