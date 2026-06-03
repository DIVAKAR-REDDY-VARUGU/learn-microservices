# Linked List Cycle

> **Pattern:** Two Pointers → **Fast & Slow (Cycle Detection)**  ·  [📖 Learn this pattern](../../README.md#fast--slow-cycle-detection)

## Problem

Given the `head` of a singly linked list, determine if the linked list has a cycle in it.

There is a cycle in a linked list if there is some node in the list that can be reached again by continuously following the `next` pointer. Internally, `pos` is used to denote the index of the node that the tail's `next` pointer connects to. Note that `pos` is **not passed as a parameter** and is not visible to your function.

Return `true` if there is a cycle in the linked list. Otherwise, return `false`.

Solve it using `O(1)` (constant) memory.

**ListNode definition:**
```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; next = null; }
}
```

**Constraints:**
- The number of the nodes in the list is in the range `[0, 10^4]`.
- `-10^5 <= Node.val <= 10^5`.

## Examples

- Input: `head = [3,2,0,-4]`, tail connects to node index 1 -> Output: `true` (there is a cycle where the tail connects to the second node)
- Input: `head = [1,2]`, tail connects to node index 0 -> Output: `true` (the tail connects back to the first node)
- Input: `head = [1]`, no cycle (pos = -1) -> Output: `false` (there is no cycle in the list)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean hasCycle(ListNode head) { /* your code */ }
```
