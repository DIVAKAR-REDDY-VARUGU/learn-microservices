# Coin Change

> **Pattern:** Dynamic Programming → **1D Coin Change / Unbounded Knapsack**  ·  [📖 Learn this pattern](../../README.md#1d-coin-change--unbounded-knapsack)

## Problem

You are given an integer array `coins` representing coins of different denominations and an integer `amount` representing a total amount of money.

Return the **fewest number of coins** that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return `-1`.

You may assume that you have an **infinite number** of each kind of coin (this makes it an unbounded knapsack problem).

**Constraints:**
- `1 <= coins.length <= 12`
- `1 <= coins[i] <= 2^31 - 1`
- `0 <= amount <= 10^4`

## Examples

- Input: `coins = [1,2,5], amount = 11` -> Output: `3`  (`11 = 5 + 5 + 1`)
- Input: `coins = [2], amount = 3` -> Output: `-1`  (`3` cannot be formed from coins of value `2`)
- Input: `coins = [1], amount = 0` -> Output: `0`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int coinChange(int[] coins, int amount) { /* your code */ }
```
