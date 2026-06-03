# Course Schedule II

> **Pattern:** Graph Traversal (DFS and BFS) → **Kahn Topological Sort**  ·  [📖 Learn this pattern](../../README.md#kahn-topological-sort)

## Problem

## Course Schedule II

There are a total of `numCourses` courses you have to take, labeled from `0` to `numCourses - 1`. You are given an array `prerequisites` where `prerequisites[i] = [a, b]` indicates that you **must take course `b` first** if you want to take course `a`.

Return the **ordering of courses** you should take to finish all courses. If there are many valid answers, return **any** of them. If it is impossible to finish all courses (the prerequisite graph contains a cycle), return an **empty array**.

Use Kahn's algorithm: compute the in-degree of every node, push all zero-in-degree nodes into a queue, then repeatedly pop a node into the result and decrement the in-degree of its neighbors, enqueuing any that reach zero. If the result does not include all courses, a cycle exists.

**Constraints:**
- `1 <= numCourses <= 2000`
- `0 <= prerequisites.length <= numCourses * (numCourses - 1)`
- `prerequisites[i].length == 2`
- `0 <= a, b < numCourses`
- All pairs `[a, b]` are distinct.

## Examples

**Example 1:**
```
Input: numCourses = 2, prerequisites = [[1,0]]
Output: [0,1]
Explanation: To take course 1 you must first take course 0.
```

**Example 2:**
```
Input: numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
Output: [0,1,2,3]
Explanation: [0,2,1,3] is also a valid ordering.
```

**Example 3:**
```
Input: numCourses = 2, prerequisites = [[1,0],[0,1]]
Output: []
Explanation: There is a cycle, so no valid ordering exists.
```

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[] findOrder(int numCourses, int[][] prerequisites) { /* your code */ }
```
