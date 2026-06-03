# Number of Islands

> **Pattern:** Graph Traversal (DFS and BFS) → **DFS/BFS Connected Components**  ·  [📖 Learn this pattern](../../README.md#dfsbfs-connected-components)

## Problem

## Number of Islands

Given an `m x n` 2D binary grid `grid` which represents a map of `'1'`s (land) and `'0'`s (water), return the **number of islands**.

An **island** is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are surrounded by water.

Each island is a connected component of `'1'` cells; traverse the grid with DFS or BFS, and each time you encounter an unvisited land cell, flood-fill its entire component and increment your count.

**Constraints:**
- `m == grid.length`
- `n == grid[i].length`
- `1 <= m, n <= 300`
- `grid[i][j]` is `'0'` or `'1'`.

## Examples

**Example 1:**
```
Input: grid = [
  ['1','1','1','1','0'],
  ['1','1','0','1','0'],
  ['1','1','0','0','0'],
  ['0','0','0','0','0']
]
Output: 1
```

**Example 2:**
```
Input: grid = [
  ['1','1','0','0','0'],
  ['1','1','0','0','0'],
  ['0','0','1','0','0'],
  ['0','0','0','1','1']
]
Output: 3
```

**Example 3:**
```
Input: grid = [['1','0','1','0','1']]
Output: 3
```

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int numIslands(char[][] grid) { /* your code */ }
```
