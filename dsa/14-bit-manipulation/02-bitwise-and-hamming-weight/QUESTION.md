# Number of 1 Bits

> **Pattern:** Bit Manipulation → **Bitwise AND (Hamming Weight)**  ·  [📖 Learn this pattern](../../README.md#bitwise-and-hamming-weight)

## Problem

Write a function that takes the binary representation of a positive integer `n` and returns the number of set bits it has (also known as the **Hamming weight**).

**Approach hint:** Repeatedly apply `n = n & (n - 1)`. Each such operation clears the lowest set bit, so the number of iterations until `n` becomes 0 equals the count of set bits. Alternatively, check the least significant bit with `n & 1` and right-shift.

**Constraints:**
- `1 <= n <= 2^31 - 1`

Note: The input is given as an `int`. Treat its bits directly; do not convert to a string.

## Examples

- Input: `n = 11` (binary `1011`) -> Output: `3`
- Input: `n = 128` (binary `10000000`) -> Output: `1`
- Input: `n = 2147483645` (binary `1111111111111111111111111111101`) -> Output: `30`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int hammingWeight(int n) { /* your code */ }
```
