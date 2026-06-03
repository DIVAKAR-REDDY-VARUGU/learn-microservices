# Multiply Strings

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Multiply Strings (Manual Simulation)**  ·  [📖 Learn this pattern](../../README.md#multiply-strings-manual-simulation)

## Problem

Given two non-negative integers `num1` and `num2` represented as strings, return the product of `num1` and `num2`, also represented as a string.

You must **not** use any built-in BigInteger library or convert the inputs to integers directly. Simulate grade-school multiplication: the product of digit `num1[i]` and `num2[j]` contributes to positions `i + j` and `i + j + 1` of a result array of length `num1.length + num2.length`. Accumulate with carries, then trim leading zeros.

**Constraints:**
- `1 <= num1.length, num2.length <= 200`
- `num1` and `num2` consist of digits only.
- Both `num1` and `num2` do not contain any leading zero, except the number `0` itself.

## Examples

- Input: `num1 = "2"`, `num2 = "3"` -> Output: `"6"`
- Input: `num1 = "123"`, `num2 = "456"` -> Output: `"56088"`
- Input: `num1 = "0"`, `num2 = "52"` -> Output: `"0"`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public String multiply(String num1, String num2) { /* your code */ }
```
