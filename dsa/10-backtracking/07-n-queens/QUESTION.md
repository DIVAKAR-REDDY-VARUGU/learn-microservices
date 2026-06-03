# N-Queens

> **Pattern:** Backtracking → **N-Queens**  ·  [📖 Learn this pattern](../../README.md#n-queens)

## Problem

## N-Queens

The **n-queens** puzzle is the problem of placing `n` queens on an `n x n` chessboard such that no two queens attack each other (no two share the same row, column, or diagonal).

Given an integer `n`, return *all distinct solutions to the n-queens puzzle*. You may return the answer in **any order**.

Each solution contains a distinct board configuration of the n-queens' placement, where `'Q'` and `'.'` indicate a queen and an empty space, respectively. Each solution is represented as a `List<String>` of `n` strings, each of length `n`.

Use backtracking: place one queen per row, and for each candidate column check that no previously placed queen shares its column or either diagonal; recurse to the next row and undo on backtrack.

**Constraints:**
- `1 <= n <= 9`

## Examples

**Example 1:**
Input: `n = 4` -> Output: `[[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]` (the two distinct solutions)

**Example 2:**
Input: `n = 1` -> Output: `[["Q"]]`

**Example 3:**
Input: `n = 2` -> Output: `[]` (no valid placement exists; same for n = 3)

(Any ordering of the solutions is accepted.)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<List<String>> solveNQueens(int n) { /* your code */ }
```
