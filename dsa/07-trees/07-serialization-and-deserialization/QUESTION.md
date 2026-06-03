# Serialize and Deserialize Binary Tree

> **Pattern:** Tree Traversal (DFS and BFS) → **Serialization and Deserialization**  ·  [📖 Learn this pattern](../../README.md#serialization-and-deserialization)

## Problem

Serialization is the process of converting a data structure into a sequence of bytes/characters so it can be stored or transmitted, and deserialization is the reverse process of reconstructing the original data structure from that representation.

Design an algorithm to **serialize and deserialize a binary tree**. There is no restriction on how your serialization/deserialization algorithm works — you only need to ensure that a binary tree can be serialized to a `String` and that this string can be deserialized back to the **exact same** tree structure.

Implement the `Codec` class with two methods:
- `String serialize(TreeNode root)` — encodes the tree to a single string.
- `TreeNode deserialize(String data)` — decodes that string back to the tree.

The `TreeNode` class is defined as:

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}
```

Your `deserialize(serialize(root))` must reproduce a tree identical to `root`.

**Constraints:**
- The number of nodes in the tree is in the range `[0, 10^4]`.
- `-1000 <= Node.val <= 1000`.

Provide the method signature for `serialize` below; implement `deserialize` with the symmetric signature `public TreeNode deserialize(String data)` inside the same `Codec` class.

## Examples

- Input: `root = [1,2,3,null,null,4,5]` -> after `deserialize(serialize(root))` the returned tree equals `[1,2,3,null,null,4,5]`
- Input: `root = []` -> after round-trip the returned tree is empty (`[]`)
- Input: `root = [1,2]` -> after round-trip the returned tree equals `[1,2]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public String serialize(TreeNode root) { /* your code */ }
```
