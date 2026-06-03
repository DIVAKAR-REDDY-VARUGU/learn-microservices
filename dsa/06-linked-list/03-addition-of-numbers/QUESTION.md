# Add Two Numbers

> **Pattern:** Linked List → **Addition of Numbers**  ·  [📖 Learn this pattern](../../README.md#addition-of-numbers)

## Problem

You are given two non-empty linked lists representing two non-negative integers. The digits are stored in **reverse order**, and each node contains a single digit. Add the two numbers and return the sum as a linked list, also with its digits in reverse order.

You may assume the two numbers do not contain any leading zero, except the number 0 itself. Be sure to handle the final carry (e.g. `[5] + [5] = [0, 1]`).

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
- The number of nodes in each linked list is in the range `[1, 100]`.
- `0 <= Node.val <= 9`
- It is guaranteed that the list represents a number that does not have leading zeros.

## Examples

**Example 1:**
- Input: `l1 = [2, 4, 3]`, `l2 = [5, 6, 4]`  (represents 342 + 465)
- Output: `[7, 0, 8]`  (represents 807)

**Example 2:**
- Input: `l1 = [0]`, `l2 = [0]`
- Output: `[0]`

**Example 3:**
- Input: `l1 = [9, 9, 9, 9, 9, 9, 9]`, `l2 = [9, 9, 9, 9]`  (9999999 + 9999)
- Output: `[8, 9, 9, 9, 0, 0, 0, 1]`  (represents 10009998)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public ListNode addTwoNumbers(ListNode l1, ListNode l2) { /* your code */ }
```
