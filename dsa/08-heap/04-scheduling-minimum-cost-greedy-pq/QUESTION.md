# Task Scheduler

> **Pattern:** Heap (Priority Queue) → **Scheduling / Minimum Cost (Greedy + PQ)**  ·  [📖 Learn this pattern](../../README.md#scheduling--minimum-cost-greedy--pq)

## Problem

## Task Scheduler

You are given an array of CPU `tasks`, each represented by an uppercase letter `A` to `Z`, and a non-negative integer `n`. Each CPU interval can be idle or allow the completion of one task. Tasks can be completed in **any order**, but there is a constraint: there must be a gap of **at least** `n` intervals between two tasks with the **same** label.

Return the **minimum** number of CPU intervals required to complete all tasks.

A greedy strategy uses a **max-heap** of remaining task counts: each cycle of length `n + 1`, pop the most frequent available tasks, decrement them, and re-add to the heap (often via a temporary cooldown queue), counting idle slots when fewer than `n + 1` distinct tasks remain.

**Constraints:**
- `1 <= tasks.length <= 10^4`
- `tasks[i]` is an uppercase English letter.
- `0 <= n <= 100`

## Examples

- `tasks = ['A','A','A','B','B','B'], n = 2` -> `8` (e.g. A B idle A B idle A B)
- `tasks = ['A','C','A','B','D','B'], n = 1` -> `6` (no idle needed, e.g. A B C A D B)
- `tasks = ['A','A','A','B','B','B'], n = 3` -> `10` (e.g. A B idle idle A B idle idle A B)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int leastInterval(char[] tasks, int n) { /* your code */ }
```
