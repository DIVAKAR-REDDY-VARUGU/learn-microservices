# Permutations

> **Pattern:** Backtracking → **Permutations**  ·  [📖 Learn this pattern](../../README.md#permutations)

## Problem

## Permutations

Given an array `nums` of **distinct** integers, return *all the possible permutations*. You may return the answer in **any order**.

Use backtracking: build a permutation one element at a time, marking elements as used so each value appears exactly once per arrangement, then unmark on backtrack.

**Constraints:**
- `1 <= nums.length <= 6`
- `-10 <= nums[i] <= 10`
- All the integers of `nums` are unique.

## Examples

**Example 1:**
Input: `nums = [1,2,3]` -> Output: `[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]`

**Example 2:**
Input: `nums = [0,1]` -> Output: `[[0,1],[1,0]]`

**Example 3:**
Input: `nums = [1]` -> Output: `[[1]]`

(Any ordering of the permutations is accepted.)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<List<Integer>> permute(int[] nums) { /* your code */ }
```
