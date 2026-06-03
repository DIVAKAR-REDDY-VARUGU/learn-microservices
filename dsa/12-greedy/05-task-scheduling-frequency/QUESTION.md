# Task Scheduler

> **Pattern:** Greedy → **Task Scheduling (Frequency)**  ·  [📖 Learn this pattern](../../README.md#task-scheduling-frequency)

## Problem

## Task Scheduler

You are given an array of CPU `tasks`, each represented by an uppercase letter `'A'` to `'Z'`, and a non-negative integer `n`. Each task takes **one unit** of CPU time to complete.

For each unit of CPU time, the CPU could either complete one task or be **idle**. However, there is a constraint: identical tasks must be separated by **at least `n` units** of time between any two of the same task.

Return the **minimum number of units of time** the CPU needs to complete all the given tasks.

**Constraints:**
- `1 <= tasks.length <= 10^4`
- `tasks[i]` is an uppercase English letter.
- `0 <= n <= 100`

**Greedy idea:** The most frequent task dictates the schedule. With max frequency `f` (appearing `c` times), the answer is `max(tasks.length, (f - 1) * (n + 1) + c)` where `c` is the count of tasks tied for the max frequency.

## Examples

- **Input:** `tasks = ['A','A','A','B','B','B']`, `n = 2` -> **Output:** `8` (e.g. A B idle A B idle A B)
- **Input:** `tasks = ['A','C','A','B','D','B']`, `n = 1` -> **Output:** `6` (no idle needed; e.g. A B A C B D)
- **Input:** `tasks = ['A','A','A','B','B','B']`, `n = 3` -> **Output:** `10` (e.g. A B idle idle A B idle idle A B)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int leastInterval(char[] tasks, int n) { /* your code */ }
```
