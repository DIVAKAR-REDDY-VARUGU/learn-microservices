# Binary Tree Inorder Traversal

> **Pattern:** Tree Traversal (DFS and BFS) → **Recursive Inorder**  ·  [📖 Learn this pattern](../../README.md#recursive-inorder)

## Problem

Given the `root` of a binary tree, return the **inorder traversal** of its nodes' values.

In **inorder** traversal you recurse into the **left subtree** first, then visit the **current node**, then recurse into the **right subtree** (Left -> Node -> Right). For a binary search tree, this yields the values in ascending sorted order. Implement this **recursively**.

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

- Input: `root = [1,null,2,3]` (1 has right child 2; 2 has left child 3) -> Output: `[1,3,2]`
- Input: `root = []` -> Output: `[]`
- Input: `root = [4,2,6,1,3,5,7]` -> Output: `[1,2,3,4,5,6,7]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<Integer> inorderTraversal(TreeNode root) { /* your code */ }
```
