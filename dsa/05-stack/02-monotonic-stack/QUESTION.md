# Daily Temperatures

> **Pattern:** Stack → **Monotonic Stack**  ·  [📖 Learn this pattern](../../README.md#monotonic-stack)

## Problem

Given an array of integers `temperatures` representing the daily temperatures, return an array `answer` such that `answer[i]` is the **number of days** you have to wait after the `i`-th day to get a **warmer** temperature.

If there is no future day with a warmer temperature, set `answer[i] = 0`.

This is the canonical "next greater element" problem solved with a monotonic (decreasing) stack of indices.

**Constraints:**
- `1 <= temperatures.length <= 10^5`
- `30 <= temperatures[i] <= 100`

## Examples

- Input: `temperatures = [73,74,75,71,69,72,76,73]` -> Output: `[1,1,4,2,1,1,0,0]`
- Input: `temperatures = [30,40,50,60]` -> Output: `[1,1,1,0]`
- Input: `temperatures = [30,60,90]` -> Output: `[1,1,0]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] dailyTemperatures(int[] temperatures) { /* your code */ }
```
