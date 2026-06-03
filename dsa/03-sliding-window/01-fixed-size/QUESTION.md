# Maximum Average Subarray I

> **Pattern:** Sliding Window → **Fixed Size**  ·  [📖 Learn this pattern](../../README.md#fixed-size)

## Problem

## Maximum Average Subarray I

You are given an integer array `nums` consisting of `n` elements, and an integer `k`.

Find a **contiguous subarray** whose length is exactly equal to `k` that has the **maximum average value**, and return this maximum average value.

The answer is considered correct if its absolute or relative error from the true value is less than `10^-5`.

**Constraints:**
- `1 <= k <= n <= 10^5`
- `-10^4 <= nums[i] <= 10^4`

**Approach hint:** Maintain a window of fixed size `k`. Compute the sum of the first `k` elements, then slide the window one step at a time by adding the incoming element and subtracting the outgoing element, tracking the maximum sum seen. Divide by `k` at the end.

## Examples

- **Input:** `nums = [1,12,-5,-6,50,3]`, `k = 4` -> **Output:** `12.75000` (the window `[12,-5,-6,50]` has the maximum sum 51, average 51/4 = 12.75)
- **Input:** `nums = [5]`, `k = 1` -> **Output:** `5.00000`
- **Input:** `nums = [0,1,1,3,3]`, `k = 4` -> **Output:** `2.00000` (window `[1,1,3,3]` sums to 8, average 8/4 = 2.0)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public double findMaxAverage(int[] nums, int k) { /* your code */ }
```
