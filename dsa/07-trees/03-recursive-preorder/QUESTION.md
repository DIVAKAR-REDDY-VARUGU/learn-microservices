# Binary Tree Preorder Traversal

> **Pattern:** Tree Traversal (DFS and BFS) → **Recursive Preorder**  ·  [📖 Learn this pattern](../../README.md#recursive-preorder)

## Problem

Given the `root` of a binary tree, return the **preorder traversal** of its nodes' values.

In **preorder** traversal you visit the **current node** first, then recurse into the **left subtree**, then the **right subtree** (Node -> Left -> Right). Implement this **recursively**.

The `TreeNode` class is defined as:

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}
```

**Constraints:**
- The number of nodes in the tree is in the range `[0, 100]`.
- `-100 <= Node.val <= 100`.

## Examples

- Input: `root = [1,null,2,3]` (1 has right child 2; 2 has left child 3) -> Output: `[1,2,3]`
- Input: `root = []` -> Output: `[]`
- Input: `root = [1,2,3,4,5]` -> Output: `[1,2,4,5,3]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<Integer> preorderTraversal(TreeNode root) { /* your code */ }
```
