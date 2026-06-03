# Find All Numbers Disappeared in an Array

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Array - Cyclic Sort**  ·  [📖 Learn this pattern](../../README.md#array---cyclic-sort)

## Problem

Given an array `nums` of `n` integers where each `nums[i]` is in the range `[1, n]`, return an array of all the integers in the range `[1, n]` that do **not** appear in `nums`.

Use cyclic sort (or in-place index marking) so that the value `v` is placed at index `v - 1`. After arranging, any index `i` where `nums[i] != i + 1` corresponds to a missing number.

**Follow-up:** Solve it without extra space (other than the output array) and in `O(n)` runtime.

**Constraints:**
- `n == nums.length`
- `1 <= n <= 10^5`
- `1 <= nums[i] <= n`

## Examples

- Input: `nums = [4,3,2,7,8,2,3,1]` -> Output: `[5,6]`
- Input: `nums = [1,1]` -> Output: `[2]`
- Input: `nums = [2,2,2]` -> Output: `[1,3]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<Integer> findDisappearedNumbers(int[] nums) { /* your code */ }
```
