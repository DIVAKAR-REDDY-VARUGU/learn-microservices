# Binary Tree Postorder Traversal (Recursive)

> **Pattern:** Tree Traversal (DFS and BFS) → **Recursive Postorder**  ·  [📖 Learn this pattern](../../README.md#recursive-postorder)

## Problem

Given the `root` of a binary tree, return the **postorder traversal** of its nodes' values, implemented **recursively**.

In **postorder** traversal you recurse into the **left subtree** first, then the **right subtree**, and finally visit the **current node** (Left -> Right -> Node). This order is useful when a node must be processed only after both of its children have been processed (e.g., deleting a tree or evaluating an expression tree).

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

- Input: `root = [1,null,2,3]` (1 has right child 2; 2 has left child 3) -> Output: `[3,2,1]`
- Input: `root = []` -> Output: `[]`
- Input: `root = [1,2,3,4,5]` -> Output: `[4,5,2,3,1]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<Integer> postorderTraversal(TreeNode root) { /* your code */ }
```
