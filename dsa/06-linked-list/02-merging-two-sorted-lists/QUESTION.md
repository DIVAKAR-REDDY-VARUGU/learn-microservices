# Merge Two Sorted Lists

> **Pattern:** Linked List → **Merging Two Sorted Lists**  ·  [📖 Learn this pattern](../../README.md#merging-two-sorted-lists)

## Problem

You are given the heads of two sorted linked lists `list1` and `list2`, each sorted in **non-decreasing** order.

Merge the two lists into one sorted linked list by **splicing together the nodes of the given lists** (reuse the existing nodes; do not create new nodes for the values). Return the head of the merged sorted linked list.

The resulting list must also be sorted in non-decreasing order. The solution should run in `O(n + m)` time and `O(1)` extra space, where `n` and `m` are the lengths of the two lists.

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
- The number of nodes in both lists is in the range `[0, 50]`.
- `-100 <= Node.val <= 100`
- Both `list1` and `list2` are sorted in non-decreasing order.

## Examples

**Example 1:**
- Input: `list1 = [1, 2, 4]`, `list2 = [1, 3, 4]`
- Output: `[1, 1, 2, 3, 4, 4]`

**Example 2:**
- Input: `list1 = []`, `list2 = []`
- Output: `[]`

**Example 3:**
- Input: `list1 = []`, `list2 = [0]`
- Output: `[0]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public ListNode mergeTwoLists(ListNode list1, ListNode list2) { /* your code */ }
```
