# Subsets

> **Pattern:** Backtracking → **Subsets (Include/Exclude)**  ·  [📖 Learn this pattern](../../README.md#subsets-includeexclude)

## Problem

## Subsets

Given an integer array `nums` of **unique** elements, return *all possible subsets (the power set)*.

The solution set **must not** contain duplicate subsets. You may return the solution in **any order**.

Use backtracking: at each element, make two choices — **include** the element in the current subset or **exclude** it — and recurse.

**Constraints:**
- `1 <= nums.length <= 10`
- `-10 <= nums[i] <= 10`
- All numbers in `nums` are unique.

## Examples

**Example 1:**
Input: `nums = [1,2,3]` -> Output: `[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]`

**Example 2:**
Input: `nums = [0]` -> Output: `[[],[0]]`

**Example 3:**
Input: `nums = [9,8]` -> Output: `[[],[9],[8],[9,8]]`

(Any ordering of subsets and of elements within a subset is accepted.)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<List<Integer>> subsets(int[] nums) { /* your code */ }
```
