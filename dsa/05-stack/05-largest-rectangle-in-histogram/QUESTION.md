# Largest Rectangle in Histogram

> **Pattern:** Stack → **Largest Rectangle in Histogram**  ·  [📖 Learn this pattern](../../README.md#largest-rectangle-in-histogram)

## Problem

Given an array of integers `heights` representing the histogram's bar heights where the width of each bar is `1`, return the **area of the largest rectangle** that can be formed within the bounds of the histogram.

The rectangle must be axis-aligned and fully contained under the bars; its height is limited by the shortest bar it spans.

Use a monotonic increasing stack of indices to compute, for each bar, the widest span over which that bar is the shortest, in O(n) time.

**Constraints:**
- `1 <= heights.length <= 10^5`
- `0 <= heights[i] <= 10^4`

## Examples

- Input: `heights = [2,1,5,6,2,3]` -> Output: `10`  (bars of height 5 and 6 span width 2)
- Input: `heights = [2,4]` -> Output: `4`
- Input: `heights = [6,2,5,4,5,1,6]` -> Output: `12`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int largestRectangleArea(int[] heights) { /* your code */ }
```
