# Two Sum II - Input Array Is Sorted

> **Pattern:** Two Pointers → **Converging (Sorted Array Target Sum)**  ·  [📖 Learn this pattern](../../README.md#converging-sorted-array-target-sum)

## Problem

Given a **1-indexed** array of integers `numbers` that is already sorted in **non-decreasing order**, find two numbers such that they add up to a specific `target`.

Let these two numbers be `numbers[index1]` and `numbers[index2]` where `1 <= index1 < index2 <= numbers.length`.

Return the indices of the two numbers, `index1` and `index2`, as an integer array `[index1, index2]` of length 2.

The tests are generated such that there is **exactly one solution**. You may **not** use the same element twice.

Your solution must use only constant extra space.

**Constraints:**
- `2 <= numbers.length <= 3 * 10^4`
- `-1000 <= numbers[i] <= 1000`
- `numbers` is sorted in non-decreasing order.
- `-1000 <= target <= 1000`
- Exactly one valid answer exists.

## Examples

- Input: `numbers = [2,7,11,15], target = 9` -> Output: `[1,2]` (because numbers[1] + numbers[2] = 2 + 7 = 9)
- Input: `numbers = [2,3,4], target = 6` -> Output: `[1,3]` (because numbers[1] + numbers[3] = 2 + 4 = 6)
- Input: `numbers = [-1,0], target = -1` -> Output: `[1,2]` (because numbers[1] + numbers[2] = -1 + 0 = -1)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] twoSum(int[] numbers, int target) { /* your code */ }
```
