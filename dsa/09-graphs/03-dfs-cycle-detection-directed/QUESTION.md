# Course Schedule

> **Pattern:** Graph Traversal (DFS and BFS) → **DFS Cycle Detection (Directed)**  ·  [📖 Learn this pattern](../../README.md#dfs-cycle-detection-directed)

## Problem

## Course Schedule

There are a total of `numCourses` courses you have to take, labeled from `0` to `numCourses - 1`. You are given an array `prerequisites` where `prerequisites[i] = [a, b]` indicates that you **must take course `b` first** if you want to take course `a`.

Return `true` if you can finish all courses, otherwise return `false`.

This reduces to detecting whether the directed prerequisite graph contains a cycle. Use DFS with three colors (states): unvisited, in-progress (on the current recursion stack), and fully processed. If during DFS you reach a node that is currently in-progress, you have found a back edge and therefore a cycle, so finishing all courses is impossible.

**Constraints:**
- `1 <= numCourses <= 2000`
- `0 <= prerequisites.length <= 5000`
- `prerequisites[i].length == 2`
- `0 <= a, b < numCourses`
- All the pairs `prerequisites[i]` are distinct.

## Examples

**Example 1:**
```
Input: numCourses = 2, prerequisites = [[1,0]]
Output: true
Explanation: Take course 0, then course 1.
```

**Example 2:**
```
Input: numCourses = 2, prerequisites = [[1,0],[0,1]]
Output: false
Explanation: Courses 0 and 1 depend on each other (cycle).
```

**Example 3:**
```
Input: numCourses = 3, prerequisites = [[1,0],[2,1]]
Output: true
Explanation: A valid order is 0 -> 1 -> 2.
```

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean canFinish(int numCourses, int[][] prerequisites) { /* your code */ }
```
