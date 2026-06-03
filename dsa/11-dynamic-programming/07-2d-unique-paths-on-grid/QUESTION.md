# Unique Paths

> **Pattern:** Dynamic Programming → **2D Unique Paths on Grid**  ·  [📖 Learn this pattern](../../README.md#2d-unique-paths-on-grid)

## Problem

There is a robot on an `m x n` grid. The robot is initially located at the top-left corner (i.e., `grid[0][0]`). The robot tries to move to the bottom-right corner (i.e., `grid[m-1][n-1]`).

The robot can only move either **down** or **right** at any point in time.

Return the number of possible unique paths that the robot can take to reach the bottom-right corner. Use a 2D DP where the number of paths to a cell equals the sum of the paths from the cell above and the cell to the left.

**Constraints:**
- `1 <= m, n <= 100`
- The answer is guaranteed to be less than or equal to `2 * 10^9`.

## Examples

- Input: `m = 3, n = 7` -> Output: `28`
- Input: `m = 3, n = 2` -> Output: `3`  (the paths are: Right->Down->Down, Down->Down->Right, Down->Right->Down)
- Input: `m = 1, n = 1` -> Output: `1`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int uniquePaths(int m, int n) { /* your code */ }
```
