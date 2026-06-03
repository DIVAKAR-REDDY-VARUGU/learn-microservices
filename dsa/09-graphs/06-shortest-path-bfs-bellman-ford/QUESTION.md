# Cheapest Flights Within K Stops

> **Pattern:** Graph Traversal (DFS and BFS) → **Shortest Path (BFS/Bellman-Ford)**  ·  [📖 Learn this pattern](../../README.md#shortest-path-bfsbellman-ford)

## Problem

## Cheapest Flights Within K Stops

There are `n` cities connected by some number of flights. You are given an array `flights` where `flights[i] = [from, to, price]` indicates that there is a flight from city `from` to city `to` with cost `price`.

You are also given three integers `src`, `dst`, and `k`. Return the **cheapest price** from `src` to `dst` with at most `k` stops. If there is no such route, return `-1`.

Because the path is bounded by `k` stops (`k+1` edges), use a Bellman-Ford-style relaxation: run `k+1` rounds, and in each round relax every edge using a **snapshot** of the distances from the previous round (so a single round cannot use more than one new edge). Alternatively, run a level-by-level BFS tracking cost and number of stops.

**Constraints:**
- `1 <= n <= 100`
- `0 <= flights.length <= (n * (n - 1) / 2)`
- `flights[i].length == 3`
- `0 <= from, to < n`
- `from != to`
- `1 <= price <= 10000`
- There will not be any multiple flights between two cities.
- `0 <= src, dst, k < n`
- `src != dst`

## Examples

**Example 1:**
```
Input: n = 4, flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]], src = 0, dst = 3, k = 1
Output: 700
Explanation: The cheapest route with at most 1 stop is 0 -> 1 -> 3, costing 100 + 600 = 700.
```

**Example 2:**
```
Input: n = 3, flights = [[0,1,100],[1,2,100],[0,2,500]], src = 0, dst = 2, k = 1
Output: 200
Explanation: 0 -> 1 -> 2 costs 200 and uses 1 stop.
```

**Example 3:**
```
Input: n = 3, flights = [[0,1,100],[1,2,100],[0,2,500]], src = 0, dst = 2, k = 0
Output: 500
Explanation: With 0 stops only the direct flight 0 -> 2 (cost 500) qualifies.
```

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) { /* your code */ }
```
