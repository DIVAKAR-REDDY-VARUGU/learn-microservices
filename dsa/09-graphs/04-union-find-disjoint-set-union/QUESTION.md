# Number of Provinces

> **Pattern:** Graph Traversal (DFS and BFS) → **Union-Find (Disjoint Set Union)**  ·  [📖 Learn this pattern](../../README.md#union-find-disjoint-set-union)

## Problem

## Number of Provinces

There are `n` cities. Some of them are connected, while some are not. If city `a` is connected directly with city `b`, and city `b` is connected directly with city `c`, then city `a` is connected indirectly with city `c`.

A **province** is a group of directly or indirectly connected cities and no other cities outside of the group.

You are given an `n x n` matrix `isConnected` where `isConnected[i][j] = 1` if the `i`-th city and the `j`-th city are directly connected, and `isConnected[i][j] = 0` otherwise.

Return the total number of **provinces**.

Use a Disjoint Set Union: initialize each city as its own set, then union the sets of every directly connected pair. The number of provinces is the number of distinct set roots remaining. Apply union by rank/size and path compression for near-constant amortized operations.

**Constraints:**
- `1 <= n <= 200`
- `n == isConnected.length == isConnected[i].length`
- `isConnected[i][j]` is `1` or `0`.
- `isConnected[i][i] == 1`
- `isConnected[i][j] == isConnected[j][i]`

## Examples

**Example 1:**
```
Input: isConnected = [[1,1,0],[1,1,0],[0,0,1]]
Output: 2
Explanation: Cities 0 and 1 form one province; city 2 forms another.
```

**Example 2:**
```
Input: isConnected = [[1,0,0],[0,1,0],[0,0,1]]
Output: 3
Explanation: No city is connected to another, so 3 provinces.
```

**Example 3:**
```
Input: isConnected = [[1,1,1],[1,1,1],[1,1,1]]
Output: 1
Explanation: All three cities are connected into a single province.
```

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int findCircleNum(int[][] isConnected) { /* your code */ }
```
