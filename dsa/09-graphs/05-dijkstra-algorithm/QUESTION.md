# Network Delay Time

> **Pattern:** Graph Traversal (DFS and BFS) → **Dijkstra Algorithm**  ·  [📖 Learn this pattern](../../README.md#dijkstra-algorithm)

## Problem

## Network Delay Time

You are given a network of `n` nodes, labeled from `1` to `n`. You are also given `times`, a list of travel times as directed edges `times[i] = (u, v, w)`, where `u` is the source node, `v` is the target node, and `w` is the time it takes for a signal to travel from source to target.

We will send a signal from a given node `k`. Return the **minimum time** it takes for all `n` nodes to receive the signal. If it is impossible for all `n` nodes to receive the signal, return `-1`.

Run Dijkstra's single-source shortest path from `k` using a min-heap (priority queue) keyed on the current shortest distance. The answer is the maximum of all nodes' shortest distances; if any node is unreachable, return `-1`.

**Constraints:**
- `1 <= k <= n <= 100`
- `1 <= times.length <= 6000`
- `times[i].length == 3`
- `1 <= u, v <= n`
- `u != v`
- `0 <= w <= 100`
- All the pairs `(u, v)` are unique.

## Examples

**Example 1:**
```
Input: times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2
Output: 2
Explanation: From node 2, node 1 takes 1, node 3 takes 1, node 4 takes 2. Max = 2.
```

**Example 2:**
```
Input: times = [[1,2,1]], n = 2, k = 1
Output: 1
```

**Example 3:**
```
Input: times = [[1,2,1]], n = 2, k = 2
Output: -1
Explanation: Node 1 is unreachable from node 2.
```

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int networkDelayTime(int[][] times, int n, int k) { /* your code */ }
```
