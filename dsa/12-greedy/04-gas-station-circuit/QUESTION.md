# Gas Station

> **Pattern:** Greedy → **Gas Station Circuit**  ·  [📖 Learn this pattern](../../README.md#gas-station-circuit)

## Problem

## Gas Station

There are `n` gas stations along a **circular** route, where the amount of gas at the `i`-th station is `gas[i]`.

You have a car with an unlimited gas tank, and it costs `cost[i]` of gas to travel from the `i`-th station to its next `(i + 1)`-th station. You begin the journey with an empty tank at one of the gas stations.

Given two integer arrays `gas` and `cost`, return the **starting gas station's index** if you can travel around the circuit once in the clockwise direction, otherwise return `-1`. If a solution exists, it is **guaranteed to be unique**.

**Constraints:**
- `n == gas.length == cost.length`
- `1 <= n <= 10^5`
- `0 <= gas[i], cost[i] <= 10^4`

**Greedy idea:** If the total gas is at least the total cost, a solution exists. Track a running tank from a candidate start; whenever it drops below zero, the next station becomes the new candidate start.

## Examples

- **Input:** `gas = [1,2,3,4,5]`, `cost = [3,4,5,1,2]` -> **Output:** `3` (start at index 3; tank never drops below zero around the loop)
- **Input:** `gas = [2,3,4]`, `cost = [3,4,3]` -> **Output:** `-1` (total gas 9 < total cost 10, so no start works)
- **Input:** `gas = [5,1,2,3,4]`, `cost = [4,4,1,5,1]` -> **Output:** `4`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int canCompleteCircuit(int[] gas, int[] cost) { /* your code */ }
```
