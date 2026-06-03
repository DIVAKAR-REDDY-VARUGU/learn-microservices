# Merge k Sorted Lists

> **Pattern:** Heap (Priority Queue) → **K-way Merge**  ·  [📖 Learn this pattern](../../README.md#k-way-merge)

## Problem

## Merge k Sorted Lists

You are given an array of `k` linked-lists `lists`, each linked-list is sorted in **ascending order**.

Merge all the linked-lists into one sorted linked-list and return its head.

Use a min-heap (priority queue) of size `k` holding the current front node of each list; repeatedly pop the smallest node, append it to the result, and push that node's successor. This yields `O(N log k)` time where `N` is the total number of nodes.

The `ListNode` class is defined as:
```java
public class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
```

**Constraints:**
- `k == lists.length`
- `0 <= k <= 10^4`
- `0 <= lists[i].length <= 500`
- `-10^4 <= lists[i][j] <= 10^4`
- `lists[i]` is sorted in ascending order.

## Examples

- `lists = [[1,4,5],[1,3,4],[2,6]]` -> `[1,1,2,3,4,4,5,6]`
- `lists = []` -> `[]`
- `lists = [[]]` -> `[]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public ListNode mergeKLists(ListNode[] lists) { /* your code */ }
```
