# Climbing Stairs

> **Pattern:** Dynamic Programming → **1D Fibonacci Style**  ·  [📖 Learn this pattern](../../README.md#1d-fibonacci-style)

## Problem

You are climbing a staircase. It takes `n` steps to reach the top.

Each time you can either climb **1** or **2** steps. In how many distinct ways can you climb to the top?

This is the classic Fibonacci-style 1D DP: the number of ways to reach step `i` equals the ways to reach step `i-1` plus the ways to reach step `i-2`.

**Constraints:**
- `1 <= n <= 45`

## Examples

- Input: `n = 2` -> Output: `2`  (the ways are `1+1` and `2`)
- Input: `n = 3` -> Output: `3`  (the ways are `1+1+1`, `1+2`, and `2+1`)
- Input: `n = 5` -> Output: `8`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int climbStairs(int n) { /* your code */ }
```
