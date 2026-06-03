# Koko Eating Bananas

> **Pattern:** Binary Search → **On Answer / Condition Function**  ·  [📖 Learn this pattern](../../README.md#on-answer--condition-function)

## Problem

Koko loves to eat bananas. There are `n` piles of bananas, the `i`-th pile has `piles[i]` bananas. The guards have gone and will come back in `h` hours.

Koko can decide her bananas-per-hour eating speed of `k`. Each hour, she chooses some pile of bananas and eats `k` bananas from that pile. If the pile has fewer than `k` bananas, she eats all of them instead and will not eat any more bananas during this hour.

Koko likes to eat slowly but still wants to finish eating all the bananas before the guards return.

Return the **minimum integer** `k` such that she can eat all the bananas within `h` hours.

**Constraints:**
- `1 <= piles.length <= 10^4`
- `piles.length <= h <= 10^9`
- `1 <= piles[i] <= 10^9`

## Examples

**Example 1**
- Input: `piles = [3, 6, 7, 11]`, `h = 8`
- Output: `4`

**Example 2**
- Input: `piles = [30, 11, 23, 4, 20]`, `h = 5`
- Output: `30`

**Example 3**
- Input: `piles = [30, 11, 23, 4, 20]`, `h = 6`
- Output: `23`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int minEatingSpeed(int[] piles, int h) { /* your code */ }
```
