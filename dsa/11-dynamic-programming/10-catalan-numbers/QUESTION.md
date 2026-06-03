# Unique Binary Search Trees

> **Pattern:** Dynamic Programming → **Catalan Numbers**  ·  [📖 Learn this pattern](../../README.md#catalan-numbers)

## Problem

Given an integer `n`, return the number of structurally unique **BST's** (binary search trees) which has exactly `n` nodes of unique values from `1` to `n`.

The count of such trees follows the **Catalan number** sequence: the number of BSTs with `n` nodes is `G(n) = sum over i from 1 to n of G(i-1) * G(n-i)`, where the `i`-th node is chosen as the root, partitioning the remaining nodes into a left and right subtree.

**Constraints:**
- `1 <= n <= 19`

## Examples

- Input: `n = 3` -> Output: `5`
- Input: `n = 1` -> Output: `1`
- Input: `n = 4` -> Output: `14`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int numTrees(int n) { /* your code */ }
```
