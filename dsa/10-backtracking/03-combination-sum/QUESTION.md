# Combination Sum

> **Pattern:** Backtracking → **Combination Sum**  ·  [📖 Learn this pattern](../../README.md#combination-sum)

## Problem

## Combination Sum

Given an array of **distinct** integers `candidates` and a target integer `target`, return *a list of all unique combinations of `candidates` where the chosen numbers sum to `target`*. You may return the combinations in **any order**.

The **same** number may be chosen from `candidates` an **unlimited number of times**. Two combinations are unique if the frequency of at least one of the chosen numbers is different.

The test cases are generated such that the number of unique combinations that sum up to `target` is fewer than 150 for the given input.

Use backtracking: at each step try a candidate, allowing reuse of the same index, and prune when the running sum exceeds `target`.

**Constraints:**
- `1 <= candidates.length <= 30`
- `2 <= candidates[i] <= 40`
- All elements of `candidates` are distinct.
- `1 <= target <= 40`

## Examples

**Example 1:**
Input: `candidates = [2,3,6,7], target = 7` -> Output: `[[2,2,3],[7]]`

**Example 2:**
Input: `candidates = [2,3,5], target = 8` -> Output: `[[2,2,2,2],[2,3,3],[3,5]]`

**Example 3:**
Input: `candidates = [2], target = 1` -> Output: `[]`

(Any ordering of the combinations is accepted.)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<List<Integer>> combinationSum(int[] candidates, int target) { /* your code */ }
```
