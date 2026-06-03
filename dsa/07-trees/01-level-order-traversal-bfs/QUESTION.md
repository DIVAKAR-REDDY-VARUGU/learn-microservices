# Binary Tree Level Order Traversal

> **Pattern:** Tree Traversal (DFS and BFS) → **Level Order Traversal (BFS)**  ·  [📖 Learn this pattern](../../README.md#level-order-traversal-bfs)

## Problem

Given the `root` of a binary tree, return the **level order traversal** of its nodes' values — that is, the values of the nodes grouped level by level, going from left to right, starting from the root level.

The `TreeNode` class is defined as:

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
```

Return a `List<List<Integer>>` where the i-th inner list contains all node values at depth `i` (root is depth 0), ordered left to right.

**Constraints:**
- The number of nodes in the tree is in the range `[0, 2000]`.
- `-1000 <= Node.val <= 1000`.

## Examples

- Input: `root = [3,9,20,null,null,15,7]` -> Output: `[[3],[9,20],[15,7]]`
- Input: `root = [1]` -> Output: `[[1]]`
- Input: `root = []` -> Output: `[]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<List<Integer>> levelOrder(TreeNode root) { /* your code */ }
```
