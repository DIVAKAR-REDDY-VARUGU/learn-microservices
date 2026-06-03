# Sliding Window Maximum

> **Pattern:** Sliding Window → **Monotonic Queue for Max/Min**  ·  [📖 Learn this pattern](../../README.md#monotonic-queue-for-maxmin)

## Problem

## Sliding Window Maximum

You are given an array of integers `nums` and an integer `k`. There is a sliding window of size `k` that moves from the very left of the array to the very right. You can only see the `k` numbers inside the window, and the window moves right by one position each step.

Return an array containing the **maximum value** in the window at each of its positions.

**Constraints:**
- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`
- `1 <= k <= nums.length`

**Approach hint:** Use a monotonic deque that stores **indices** of array elements in decreasing order of their values. For each new element, pop smaller values from the back, push the current index, and evict the front index if it has slid out of the window. The front of the deque always holds the index of the current window's maximum. This achieves O(n) overall.

## Examples

- **Input:** `nums = [1,3,-1,-3,5,3,6,7]`, `k = 3` -> **Output:** `[3,3,5,5,6,7]` (window maxima: `[1,3,-1]`->3, `[3,-1,-3]`->3, `[-1,-3,5]`->5, `[-3,5,3]`->5, `[5,3,6]`->6, `[3,6,7]`->7)
- **Input:** `nums = [1]`, `k = 1` -> **Output:** `[1]`
- **Input:** `nums = [9,11]`, `k = 2` -> **Output:** `[11]` (the single window `[9,11]` has maximum 11)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] maxSlidingWindow(int[] nums, int k) { /* your code */ }
```
