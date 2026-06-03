# Best Time to Buy and Sell Stock II

> **Pattern:** Greedy → **Buy/Sell Stock**  ·  [📖 Learn this pattern](../../README.md#buysell-stock)

## Problem

## Best Time to Buy and Sell Stock II

You are given an integer array `prices` where `prices[i]` is the price of a given stock on the `i`-th day.

On each day, you may decide to buy and/or sell the stock. You can hold **at most one** share of the stock at any time. However, you may buy it then immediately sell it on the **same day**.

Find and return the **maximum profit** you can achieve.

**Constraints:**
- `1 <= prices.length <= 3 * 10^4`
- `0 <= prices[i] <= 10^4`

**Greedy idea:** Sum every positive day-over-day gain (`prices[i] - prices[i-1]` when positive). Capturing every upward step is equivalent to buying at each local minimum and selling at each local maximum.

## Examples

- **Input:** `prices = [7,1,5,3,6,4]` -> **Output:** `7` (buy at 1 sell at 5 = 4, buy at 3 sell at 6 = 3; total 7)
- **Input:** `prices = [1,2,3,4,5]` -> **Output:** `4` (buy at 1 sell at 5; equivalently sum all daily gains)
- **Input:** `prices = [7,6,4,3,1]` -> **Output:** `0` (prices only fall, so do nothing)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int maxProfit(int[] prices) { /* your code */ }
```
