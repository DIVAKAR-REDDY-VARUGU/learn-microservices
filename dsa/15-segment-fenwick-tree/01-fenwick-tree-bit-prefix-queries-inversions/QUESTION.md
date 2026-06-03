# Count of Smaller Numbers After Self

> **Pattern:** Segment Tree and Fenwick Tree → **Fenwick Tree (BIT) - Prefix Queries / Inversions**  ·  [📖 Learn this pattern](../../README.md#fenwick-tree-bit---prefix-queries--inversions)

## Problem

## Count of Smaller Numbers After Self

You are given an integer array `nums`. Return an integer array `counts` where `counts[i]` is the number of elements to the **right** of `nums[i]` that are **strictly smaller** than `nums[i]`.

This is the classic application of a Fenwick Tree (Binary Indexed Tree): by traversing the array from right to left and querying the prefix sum of how many already-seen values are smaller than the current value (after coordinate compression), you can count these "inversions to the right" in `O(n log n)` total time.

### Constraints
- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`

### Notes
- The returned list must have the same length as `nums`.
- Counting is strict: equal values do not count as smaller.

## Examples

**Example 1**
```
Input:  nums = [5, 2, 6, 1]
Output: [2, 1, 1, 0]
```
Explanation: To the right of 5 there are 2 smaller (2 and 1); to the right of 2 there is 1 smaller (1); to the right of 6 there is 1 smaller (1); to the right of 1 there are 0 smaller.

**Example 2**
```
Input:  nums = [-1, -1]
Output: [0, 0]
```

**Example 3**
```
Input:  nums = [2, 0, 1]
Output: [2, 0, 0]
```

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<Integer> countSmaller(int[] nums) { /* your code */ }
```
