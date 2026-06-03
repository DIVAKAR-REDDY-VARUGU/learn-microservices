# Median of Two Sorted Arrays

> **Pattern:** Binary Search → **Median and Kth of Two Sorted Arrays**  ·  [📖 Learn this pattern](../../README.md#median-and-kth-of-two-sorted-arrays)

## Problem

Given two sorted arrays `nums1` and `nums2` of size `m` and `n` respectively, return the **median** of the two sorted arrays.

The overall run time complexity should be `O(log (m + n))`.

The median is the middle value of the combined sorted arrays. If the combined length is even, the median is the average of the two middle values.

**Constraints:**
- `nums1.length == m`
- `nums2.length == n`
- `0 <= m <= 1000`
- `0 <= n <= 1000`
- `1 <= m + n <= 2000`
- `-10^6 <= nums1[i], nums2[i] <= 10^6`
- Both arrays are sorted in non-decreasing order.

## Examples

**Example 1**
- Input: `nums1 = [1, 3]`, `nums2 = [2]`
- Output: `2.00000` (merged array = [1, 2, 3], median is 2)

**Example 2**
- Input: `nums1 = [1, 2]`, `nums2 = [3, 4]`
- Output: `2.50000` (merged array = [1, 2, 3, 4], median is (2 + 3) / 2 = 2.5)

**Example 3**
- Input: `nums1 = [0, 0]`, `nums2 = [0, 0]`
- Output: `0.00000`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public double findMedianSortedArrays(int[] nums1, int[] nums2) { /* your code */ }
```
