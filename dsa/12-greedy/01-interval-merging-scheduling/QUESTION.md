# Merge Intervals

> **Pattern:** Greedy → **Interval Merging/Scheduling**  ·  [📖 Learn this pattern](../../README.md#interval-mergingscheduling)

## Problem

## Merge Intervals

You are given an array `intervals` where `intervals[i] = [start_i, end_i]` represents a closed interval on the number line.

Merge all **overlapping** intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.

Two intervals are considered overlapping if they share at least one point (e.g. `[1,4]` and `[4,5]` overlap and merge into `[1,5]`).

**Constraints:**
- `1 <= intervals.length <= 10^4`
- `intervals[i].length == 2`
- `0 <= start_i <= end_i <= 10^4`

The returned intervals may be in any order, but a sorted-by-start order is conventional.

**Greedy idea:** Sort intervals by start time, then sweep left-to-right merging any interval whose start is within the current merged interval's end.

## Examples

- **Input:** `intervals = [[1,3],[2,6],[8,10],[15,18]]` -> **Output:** `[[1,6],[8,10],[15,18]]` (because `[1,3]` and `[2,6]` overlap into `[1,6]`)
- **Input:** `intervals = [[1,4],[4,5]]` -> **Output:** `[[1,5]]` (they touch at 4, so they merge)
- **Input:** `intervals = [[1,4],[0,4]]` -> **Output:** `[[0,4]]`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int[][] merge(int[][] intervals) { /* your code */ }
```
