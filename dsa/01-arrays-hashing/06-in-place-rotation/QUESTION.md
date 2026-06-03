# Rotate Array

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **In-place Rotation**  ·  [📖 Learn this pattern](../../README.md#in-place-rotation)

## Problem

Given an integer array `nums`, rotate the array to the right by `k` steps, where `k` is non-negative. Modify the array **in place**.

A common approach is the reversal trick: reverse the whole array, then reverse the first `k` elements, then reverse the remaining `n - k` elements. Remember to take `k = k % n`.

**Follow-up:** Do it in-place with `O(1)` extra space.

**Constraints:**
- `1 <= nums.length <= 10^5`
- `-2^31 <= nums[i] <= 2^31 - 1`
- `0 <= k <= 10^5`

## Examples

- Input: `nums = [1,2,3,4,5,6,7]`, `k = 3` -> Output: `nums = [5,6,7,1,2,3,4]`
- Input: `nums = [-1,-100,3,99]`, `k = 2` -> Output: `nums = [3,99,-1,-100]`
- Input: `nums = [1,2]`, `k = 3` -> Output: `nums = [2,1]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public void rotate(int[] nums, int k) { /* your code */ }
```
