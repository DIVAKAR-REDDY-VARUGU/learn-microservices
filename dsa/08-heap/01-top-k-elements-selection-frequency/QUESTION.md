# Top K Frequent Elements

> **Pattern:** Heap (Priority Queue) → **Top K Elements (Selection/Frequency)**  ·  [📖 Learn this pattern](../../README.md#top-k-elements-selectionfrequency)

## Problem

## Top K Frequent Elements

Given an integer array `nums` and an integer `k`, return the `k` most frequent elements. You may return the answer in **any order**.

It is guaranteed that the answer is **unique** (the set of the `k` most frequent elements is uniquely determined).

**Constraints:**
- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`
- `k` is in the range `[1, number of distinct elements in nums]`

**Follow-up:** Your algorithm's time complexity should be better than `O(n log n)`, where `n` is the array size. A min-heap of size `k` over the frequency map achieves `O(n log k)`.

## Examples

- `nums = [1,1,1,2,2,3], k = 2` -> `[1,2]` (1 appears 3 times, 2 appears 2 times)
- `nums = [1], k = 1` -> `[1]`
- `nums = [4,4,4,6,6,2,2,2,2], k = 2` -> `[2,4]` (2 appears 4 times, 4 appears 3 times)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] topKFrequent(int[] nums, int k) { /* your code */ }
```
