# Lowest Common Ancestor of a Binary Tree

> **Pattern:** Tree Traversal (DFS and BFS) → **Lowest Common Ancestor (LCA)**  ·  [📖 Learn this pattern](../../README.md#lowest-common-ancestor-lca)

## Problem

Given the `root` of a binary tree and two distinct nodes `p` and `q` that are guaranteed to exist in the tree, return their **lowest common ancestor (LCA)**.

The lowest common ancestor of two nodes `p` and `q` is defined as the **deepest** node in the tree that has **both** `p` and `q` as descendants. A node is allowed to be a descendant of itself, so if `p` is an ancestor of `q` (or vice versa), that ancestor node is the LCA.

The `TreeNode` class is defined as:

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}
```

**Constraints:**
- The number of nodes in the tree is in the range `[2, 10^5]`.
- `-10^9 <= Node.val <= 10^9`.
- All `Node.val` are unique.
- `p != q`, and both `p` and `q` exist in the tree.

Return the `TreeNode` that is the LCA of `p` and `q`.

## Examples

- Input: `root = [3,5,1,6,2,0,8,null,null,7,4]`, `p = 5`, `q = 1` -> Output: `3` (node with value 3)
- Input: `root = [3,5,1,6,2,0,8,null,null,7,4]`, `p = 5`, `q = 4` -> Output: `5` (a node can be a descendant of itself)
- Input: `root = [1,2]`, `p = 1`, `q = 2` -> Output: `1`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) { /* your code */ }
```
