# Reverse Linked List

> **Pattern:** Linked List → **In-place Reversal**  ·  [📖 Learn this pattern](../../README.md#in-place-reversal)

## Problem

Given the `head` of a singly linked list, reverse the list and return the head of the reversed list.

You must reverse the list **in place** by re-pointing the existing nodes' `next` references — do not allocate new nodes or copy node values into another data structure. The solution should run in `O(n)` time and use `O(1)` extra space.

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
- The number of nodes in the list is in the range `[0, 5000]`.
- `-5000 <= Node.val <= 5000`

## Examples

**Example 1:**
- Input: `head = [1, 2, 3, 4, 5]`
- Output: `[5, 4, 3, 2, 1]`

**Example 2:**
- Input: `head = [1, 2]`
- Output: `[2, 1]`

**Example 3:**
- Input: `head = []`
- Output: `[]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public ListNode reverseList(ListNode head) { /* your code */ }
```
