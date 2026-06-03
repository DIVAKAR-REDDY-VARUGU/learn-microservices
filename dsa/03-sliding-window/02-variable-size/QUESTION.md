# Minimum Size Subarray Sum

> **Pattern:** Sliding Window → **Variable Size**  ·  [📖 Learn this pattern](../../README.md#variable-size)

## Problem

## Minimum Size Subarray Sum

Given an array of **positive integers** `nums` and a positive integer `target`, return the **minimal length** of a contiguous subarray `[nums[l], nums[l+1], ..., nums[r]]` whose sum is **greater than or equal to** `target`.

If there is no such subarray, return `0` instead.

**Constraints:**
- `1 <= target <= 10^9`
- `1 <= nums.length <= 10^5`
- `1 <= nums[i] <= 10^4`

**Approach hint:** Use a variable-size sliding window with two pointers. Expand the right pointer to grow the window sum; once the sum reaches `target`, shrink from the left while the condition still holds, recording the smallest window length each time.

## Examples

- **Input:** `target = 7`, `nums = [2,3,1,2,4,3]` -> **Output:** `2` (the subarray `[4,3]` has sum 7 >= 7 and length 2, which is minimal)
- **Input:** `target = 4`, `nums = [1,4,4]` -> **Output:** `1` (the subarray `[4]` already meets the target)
- **Input:** `target = 11`, `nums = [1,1,1,1,1,1,1,1]` -> **Output:** `0` (total sum is 8 < 11, so no valid subarray exists)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int minSubArrayLen(int target, int[] nums) { /* your code */ }
```
