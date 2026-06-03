# Spiral Matrix

> **Pattern:** Arrays & Hashing (Array/Matrix Manipulation) → **Spiral Traversal**  ·  [📖 Learn this pattern](../../README.md#spiral-traversal)

## Problem

Given an `m x n` matrix, return all elements of the matrix in **spiral order** (starting from the top-left, moving right, then down, then left, then up, and inward).

Maintain four boundaries (top, bottom, left, right). Traverse the top row left-to-right, the right column top-to-bottom, the bottom row right-to-left, and the left column bottom-to-top, shrinking the boundaries after each pass until they cross.

**Constraints:**
- `m == matrix.length`
- `n == matrix[i].length`
- `1 <= m, n <= 10`
- `-100 <= matrix[i][j] <= 100`

## Examples

- Input: `matrix = [[1,2,3],[4,5,6],[7,8,9]]` -> Output: `[1,2,3,6,9,8,7,4,5]`
- Input: `matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]` -> Output: `[1,2,3,4,8,12,11,10,9,5,6,7]`
- Input: `matrix = [[7],[9],[6]]` -> Output: `[7,9,6]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<Integer> spiralOrder(int[][] matrix) { /* your code */ }
```
