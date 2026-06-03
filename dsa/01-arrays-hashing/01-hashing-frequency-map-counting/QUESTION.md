# Top K Frequent Elements

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Hashing - Frequency Map / Counting**  ·  [📖 Learn this pattern](../../README.md#hashing---frequency-map--counting)

## Problem

Given an integer array `nums` and an integer `k`, return the `k` most frequent elements. You may return the answer in any order.

Build a frequency map (element -> count) by counting occurrences, then select the `k` elements with the highest counts.

**Constraints:**
- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`
- `k` is in the range `[1, number of distinct elements]`
- It is guaranteed the answer is unique.

Aim for better than `O(n log n)` time.

## Examples

- Input: `nums = [1,1,1,2,2,3]`, `k = 2` -> Output: `[1,2]`
- Input: `nums = [1]`, `k = 1` -> Output: `[1]`
- Input: `nums = [4,1,-1,2,-1,2,3]`, `k = 2` -> Output: `[-1,2]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] topKFrequent(int[] nums, int k) { /* your code */ }
```
