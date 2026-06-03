# Merge Sorted Array

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Merge Sorted Array (In-place from End)**  ·  [📖 Learn this pattern](../../README.md#merge-sorted-array-in-place-from-end)

## Problem

You are given two integer arrays `nums1` and `nums2`, sorted in **non-decreasing order**, and two integers `m` and `n`, representing the number of elements in `nums1` and `nums2` respectively.

Merge `nums2` into `nums1` so that the combined array is sorted in non-decreasing order. The final result must be stored **inside** `nums1`. To accommodate this, `nums1` has length `m + n`, where the first `m` slots hold the elements to merge and the last `n` slots are set to `0` and should be ignored.

Merge in place by filling `nums1` from the **end** (largest values first) using three pointers, so you never overwrite an unmerged element.

**Constraints:**
- `nums1.length == m + n`
- `nums2.length == n`
- `0 <= m, n <= 200`
- `1 <= m + n <= 200`

## Examples

- Input: `nums1 = [1,2,3,0,0,0]`, `m = 3`, `nums2 = [2,5,6]`, `n = 3` -> Output: `nums1 = [1,2,2,3,5,6]`
- Input: `nums1 = [1]`, `m = 1`, `nums2 = []`, `n = 0` -> Output: `nums1 = [1]`
- Input: `nums1 = [0]`, `m = 0`, `nums2 = [1]`, `n = 1` -> Output: `nums1 = [1]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public void merge(int[] nums1, int m, int[] nums2, int n) { /* your code */ }
```
