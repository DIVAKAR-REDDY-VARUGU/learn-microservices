# Reorder List

> **Pattern:** Linked List → **Reordering/Partitioning**  ·  [📖 Learn this pattern](../../README.md#reorderingpartitioning)

## Problem

You are given the `head` of a singly linked list. The list can be represented as:

```
L0 -> L1 -> ... -> L(n-1) -> Ln
```

Reorder the list to be in the following form:

```
L0 -> Ln -> L1 -> L(n-1) -> L2 -> L(n-2) -> ...
```

You may **not** modify the values in the list's nodes — only the nodes themselves (i.e. their `next` pointers) may be changed. Modify the list **in place** and return nothing.

A common approach is to: find the middle of the list (slow/fast pointers), reverse the second half in place, then merge the two halves by alternating nodes.

The list node is defined as:

```java
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
```

**Constraints:**
- The number of nodes in the list is in the range `[1, 5 * 10^4]`.
- `1 <= Node.val <= 1000`

## Examples

**Example 1:**
- Input: `head = [1, 2, 3, 4]`
- Output: `[1, 4, 2, 3]`

**Example 2:**
- Input: `head = [1, 2, 3, 4, 5]`
- Output: `[1, 5, 2, 4, 3]`

**Example 3:**
- Input: `head = [1]`
- Output: `[1]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public void reorderList(ListNode head) { /* your code */ }
```
