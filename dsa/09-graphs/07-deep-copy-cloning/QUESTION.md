# Clone Graph

> **Pattern:** Graph Traversal (DFS and BFS) → **Deep Copy / Cloning**  ·  [📖 Learn this pattern](../../README.md#deep-copy--cloning)

## Problem

## Clone Graph

Given a reference of a node in a **connected** undirected graph, return a **deep copy** (clone) of the graph.

Each node in the graph contains a value (`int`) and a list (`List<Node>`) of its neighbors:
```java
class Node {
    public int val;
    public List<Node> neighbors;
}
```

The graph is given in the test case using an adjacency list, and each node's value is the same as its 1-indexed position. The given node is always the first node with `val == 1`. You must return the copy of the given node as a reference to the cloned graph.

Traverse the graph with DFS or BFS while maintaining a hash map from each original node to its clone. When you first visit a node, create its clone and record it in the map; then recursively clone and attach each neighbor, reusing existing clones from the map to correctly reproduce cycles and shared neighbors.

**Constraints:**
- The number of nodes is in the range `[0, 100]`.
- `1 <= Node.val <= 100`
- `Node.val` is unique for each node.
- There are no repeated edges and no self-loops.
- The graph is connected and all nodes can be reached starting from the given node.
- If the input node is `null`, return `null`.

## Examples

**Example 1:**
```
Input (adjList): [[2,4],[1,3],[2,4],[1,3]]
Output: [[2,4],[1,3],[2,4],[1,3]]
Explanation: 4 nodes. Node 1 -> {2,4}, Node 2 -> {1,3}, Node 3 -> {2,4}, Node 4 -> {1,3}. The returned clone has identical structure but all-new node objects.
```

**Example 2:**
```
Input (adjList): [[]]
Output: [[]]
Explanation: One node with val 1 and no neighbors.
```

**Example 3:**
```
Input (adjList): []
Output: []
Explanation: The graph is empty; the given node is null, so return null.
```

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public Node cloneGraph(Node node) { /* your code */ }
```
