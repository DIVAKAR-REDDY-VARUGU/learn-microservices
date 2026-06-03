# Counting Bits

> **Pattern:** Bit Manipulation → **Bitwise DP (Counting Bits Optimization)**  ·  [📖 Learn this pattern](../../README.md#bitwise-dp-counting-bits-optimization)

## Problem

Given an integer `n`, return an array `ans` of length `n + 1` such that for each `i` (where `0 <= i <= n`), `ans[i]` is the number of set bits (1's) in the binary representation of `i`.

You should solve it in **O(n)** time using a single pass, rather than counting bits independently for each number.

**Dynamic programming insight:** The number of set bits in `i` relates to a previously computed value. Two common recurrences:
- `ans[i] = ans[i >> 1] + (i & 1)` — bits of `i` equal bits of `i/2` plus the last bit.
- `ans[i] = ans[i & (i - 1)] + 1` — clearing the lowest set bit of `i` gives a smaller, already-computed index.

**Constraints:**
- `0 <= n <= 10^5`

## Examples

- Input: `n = 2` -> Output: `[0, 1, 1]` (0->0 bits, 1->1 bit, 2->1 bit)
- Input: `n = 5` -> Output: `[0, 1, 1, 2, 1, 2]`
- Input: `n = 0` -> Output: `[0]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] countBits(int n) { /* your code */ }
```
