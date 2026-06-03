# Product of Array Except Self

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Product Except Self (Prefix/Suffix Products)**  ·  [📖 Learn this pattern](../../README.md#product-except-self-prefixsuffix-products)

## Problem

Given an integer array `nums`, return an array `answer` such that `answer[i]` is equal to the product of all the elements of `nums` **except** `nums[i]`.

The product of any prefix or suffix of `nums` is guaranteed to fit in a 32-bit integer.

You must write an algorithm that runs in `O(n)` time and **without using the division operation**. Use prefix products and suffix products to build the result.

**Constraints:**
- `2 <= nums.length <= 10^5`
- `-30 <= nums[i] <= 30`
- The product of all prefixes and suffixes fits in a 32-bit integer.

## Examples

- Input: `nums = [1,2,3,4]` -> Output: `[24,12,8,6]`
- Input: `nums = [-1,1,0,-3,3]` -> Output: `[0,0,9,0,0]`
- Input: `nums = [2,3]` -> Output: `[3,2]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] productExceptSelf(int[] nums) { /* your code */ }
```
