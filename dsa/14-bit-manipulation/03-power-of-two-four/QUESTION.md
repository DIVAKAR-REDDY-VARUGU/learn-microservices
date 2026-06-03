# Power of Two

> **Pattern:** Bit Manipulation → **Power of Two/Four**  ·  [📖 Learn this pattern](../../README.md#power-of-twofour)

## Problem

Given an integer `n`, return `true` if it is a **power of two**, otherwise return `false`.

An integer `n` is a power of two if there exists an integer `x` such that `n == 2^x`.

**Approach hint:** A positive power of two has exactly one set bit in its binary representation. Therefore `n > 0 && (n & (n - 1)) == 0` is true if and only if `n` is a power of two. Be sure to handle zero and negative inputs (which are never powers of two).

**Constraints:**
- `-2^31 <= n <= 2^31 - 1`
- Follow up: Could you solve it without loops or recursion?

## Examples

- Input: `n = 1` -> Output: `true` (2^0)
- Input: `n = 16` -> Output: `true` (2^4)
- Input: `n = 3` -> Output: `false`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean isPowerOfTwo(int n) { /* your code */ }
```
