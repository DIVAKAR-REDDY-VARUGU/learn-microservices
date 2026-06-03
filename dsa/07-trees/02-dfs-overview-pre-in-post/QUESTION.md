# Binary Tree Postorder Traversal

> **Pattern:** Tree Traversal (DFS and BFS) → **DFS Overview (Pre/In/Post)**  ·  [📖 Learn this pattern](../../README.md#dfs-overview-preinpost)

## Problem

Given the `root` of a binary tree, return the **postorder traversal** of its nodes' values.

Postorder is one of the three classic depth-first traversal orders (preorder, inorder, postorder). In **postorder** you visit the **left subtree**, then the **right subtree**, and finally the **current node** (Left -> Right -> Node).

The `TreeNode` class is defined as:

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}
```

Follow up: A recursive solution is trivial; can you also do it iteratively using an explicit stack?

**Constraints:**
- The number of nodes in the tree is in the range `[0, 100]`.
- `-100 <= Node.val <= 100`.

## Examples

- Input: `root = [1,null,2,3]` (1 has right child 2; 2 has left child 3) -> Output: `[3,2,1]`
- Input: `root = []` -> Output: `[]`
- Input: `root = [1,2,3,4,5,null,null]` -> Output: `[4,5,2,3,1]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public List<Integer> postorderTraversal(TreeNode root) { /* your code */ }
```
