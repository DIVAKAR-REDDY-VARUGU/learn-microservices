# Remove Nth Node From End of List

> **Pattern:** Two Pointers → **Fixed Separation (Nth from End)**  ·  [📖 Learn this pattern](../../README.md#fixed-separation-nth-from-end)

## Problem

Given the `head` of a linked list, remove the `n`-th node from the end of the list and return its head.

You should aim to do this in **one pass** using two pointers separated by a fixed gap.

**ListNode definition:**
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
- The number of nodes in the list is `sz`.
- `1 <= sz <= 30`
- `0 <= Node.val <= 100`
- `1 <= n <= sz`

## Examples

- Input: `head = [1,2,3,4,5], n = 2` -> Output: `[1,2,3,5]` (the 2nd node from the end, value 4, is removed)
- Input: `head = [1], n = 1` -> Output: `[]` (the only node is removed, leaving an empty list)
- Input: `head = [1,2], n = 1` -> Output: `[1]` (the last node, value 2, is removed)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public ListNode removeNthFromEnd(ListNode head, int n) { /* your code */ }
```
