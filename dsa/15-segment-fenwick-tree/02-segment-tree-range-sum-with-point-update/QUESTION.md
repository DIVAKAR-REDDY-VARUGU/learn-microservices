# Range Sum Query - Mutable

> **Pattern:** Segment Tree and Fenwick Tree → **Segment Tree - Range Sum with Point Update**  ·  [📖 Learn this pattern](../../README.md#segment-tree---range-sum-with-point-update)

## Problem

## Range Sum Query - Mutable

Design a data structure that, given an integer array `nums`, supports two operations efficiently:

1. **Update** the value of an element at a given index.
2. **Query** the sum of the elements between two indices `left` and `right` **inclusive**.

Implement the `NumArray` class:

- `NumArray(int[] nums)` initializes the object with the integer array `nums`.
- `void update(int index, int val)` updates the value of `nums[index]` to be `val`.
- `int sumRange(int left, int right)` returns the sum `nums[left] + nums[left + 1] + ... + nums[right]`.

Use a **Segment Tree** (range sum with point update) so that each `update` and `sumRange` runs in `O(log n)` time, rather than rebuilding a prefix-sum array on every update.

### Constraints
- `1 <= nums.length <= 3 * 10^4`
- `-100 <= nums[i] <= 100`
- `0 <= index < nums.length`
- `-100 <= val <= 100`
- `0 <= left <= right < nums.length`
- At most `3 * 10^4` calls in total to `update` and `sumRange`.

## Examples

**Example 1**
```
Input:
["NumArray", "sumRange", "update", "sumRange"]
[[[1, 3, 5]], [0, 2], [1, 2], [0, 2]]

Output:
[null, 9, null, 8]
```
Explanation:
```
NumArray numArray = new NumArray([1, 3, 5]);
numArray.sumRange(0, 2); // return 1 + 3 + 5 = 9
numArray.update(1, 2);   // nums = [1, 2, 5]
numArray.sumRange(0, 2); // return 1 + 2 + 5 = 8
```

**Example 2**
```
Input:
["NumArray", "sumRange", "update", "update", "sumRange"]
[[[7, 2, 7, 2, 0]], [1, 3], [0, 6], [3, 4], [2, 4]]

Output:
[null, 11, null, null, 13]
```
Explanation: sumRange(1,3) = 2+7+2 = 11; after update(0,6) and update(3,4), nums = [6,2,7,4,0], sumRange(2,4) = 7+4+0 = 13.

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
class NumArray {
    public NumArray(int[] nums)
    public void update(int index, int val)
    public int sumRange(int left, int right)
} { /* your code */ }
```
