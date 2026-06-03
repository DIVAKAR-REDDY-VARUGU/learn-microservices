# Plus One

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Plus One (Handling Carry)**  ·  [📖 Learn this pattern](../../README.md#plus-one-handling-carry)

## Problem

You are given a large integer represented as an integer array `digits`, where each `digits[i]` is the `i`-th digit of the number. The digits are ordered from most significant to least significant in left-to-right order. The integer does not contain any leading zero, except the number `0` itself.

Increment the large integer by one and return the resulting array of digits.

Process digits from the least significant end, propagating the carry. If the carry survives past the most significant digit, prepend a new leading `1`.

**Constraints:**
- `1 <= digits.length <= 100`
- `0 <= digits[i] <= 9`
- `digits` does not contain any leading zeros except for the number `0` itself.

## Examples

- Input: `digits = [1,2,3]` -> Output: `[1,2,4]`
- Input: `digits = [4,3,2,1]` -> Output: `[4,3,2,2]`
- Input: `digits = [9,9,9]` -> Output: `[1,0,0,0]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] plusOne(int[] digits) { /* your code */ }
```
