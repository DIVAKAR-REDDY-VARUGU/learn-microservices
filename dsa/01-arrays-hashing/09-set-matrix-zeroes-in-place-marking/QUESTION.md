# Set Matrix Zeroes

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Set Matrix Zeroes (In-place Marking)**  ·  [📖 Learn this pattern](../../README.md#set-matrix-zeroes-in-place-marking)

## Problem

Given an `m x n` integer matrix `matrix`, if an element is `0`, set its entire row and column to `0`. You must do it **in place**.

Do not naively zero rows/columns while scanning, as that corrupts the original zero information. Instead, use the first row and first column as marker storage to record which rows/columns must be zeroed (handling the first row and first column separately with a couple of flags), achieving `O(1)` extra space.

**Constraints:**
- `m == matrix.length`
- `n == matrix[0].length`
- `1 <= m, n <= 200`
- `-2^31 <= matrix[i][j] <= 2^31 - 1`

## Examples

- Input: `matrix = [[1,1,1],[1,0,1],[1,1,1]]` -> Output: `[[1,0,1],[0,0,0],[1,0,1]]`
- Input: `matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]` -> Output: `[[0,0,0,0],[0,4,5,0],[0,3,1,0]]`
- Input: `matrix = [[1,2,3]]` -> Output: `[[1,2,3]]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public void setZeroes(int[][] matrix) { /* your code */ }
```
