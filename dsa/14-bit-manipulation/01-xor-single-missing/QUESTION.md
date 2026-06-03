# Single Number

> **Pattern:** Bit Manipulation → **XOR (Single/Missing)**  ·  [📖 Learn this pattern](../../README.md#xor-singlemissing)

## Problem

Given a non-empty array of integers `nums`, every element appears **twice** except for one element which appears **exactly once**. Find that single element.

You must implement a solution with **linear runtime complexity** and use only **constant extra space**.

**Key insight:** XOR of a number with itself is 0, and XOR with 0 leaves the number unchanged. XORing all elements together cancels out every pair, leaving the unique element.

**Constraints:**
- `1 <= nums.length <= 3 * 10^4`
- `-3 * 10^4 <= nums[i] <= 3 * 10^4`
- Each element appears exactly twice except for one element which appears once.

## Examples

- Input: `nums = [2, 2, 1]` -> Output: `1`
- Input: `nums = [4, 1, 2, 1, 2]` -> Output: `4`
- Input: `nums = [1]` -> Output: `1`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int singleNumber(int[] nums) { /* your code */ }
```
