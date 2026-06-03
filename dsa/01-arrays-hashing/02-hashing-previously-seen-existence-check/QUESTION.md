# Two Sum

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Hashing - Previously Seen / Existence Check**  ·  [📖 Learn this pattern](../../README.md#hashing---previously-seen--existence-check)

## Problem

Given an array of integers `nums` and an integer `target`, return the indices of the two numbers such that they add up to `target`.

You may assume that each input has **exactly one solution**, and you may not use the same element twice. Return the answer in any order.

Use a hash map to remember values already seen (value -> index). For each element, check whether its complement (`target - nums[i]`) has been seen previously.

**Constraints:**
- `2 <= nums.length <= 10^4`
- `-10^9 <= nums[i] <= 10^9`
- `-10^9 <= target <= 10^9`
- Exactly one valid answer exists.

## Examples

- Input: `nums = [2,7,11,15]`, `target = 9` -> Output: `[0,1]` (because `nums[0] + nums[1] == 9`)
- Input: `nums = [3,2,4]`, `target = 6` -> Output: `[1,2]`
- Input: `nums = [3,3]`, `target = 6` -> Output: `[0,1]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] twoSum(int[] nums, int target) { /* your code */ }
```
