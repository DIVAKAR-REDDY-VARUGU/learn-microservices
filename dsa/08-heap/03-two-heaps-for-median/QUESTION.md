# Find Median from Data Stream

> **Pattern:** Heap (Priority Queue) → **Two Heaps for Median**  ·  [📖 Learn this pattern](../../README.md#two-heaps-for-median)

## Problem

## Find Median from Data Stream

The **median** is the middle value in an ordered integer list. If the size of the list is even, there is no single middle value and the median is the mean of the two middle values.

- For example, for `arr = [2,3,4]`, the median is `3`.
- For example, for `arr = [2,3]`, the median is `(2 + 3) / 2 = 2.5`.

Implement the `MedianFinder` class:
- `MedianFinder()` initializes the `MedianFinder` object.
- `void addNum(int num)` adds the integer `num` from the data stream to the data structure.
- `double findMedian()` returns the median of all elements so far. Answers within `10^-5` of the actual answer will be accepted.

Maintain a **max-heap** for the lower half and a **min-heap** for the upper half, keeping their sizes balanced (differ by at most one). `addNum` is `O(log n)` and `findMedian` is `O(1)`.

**Constraints:**
- `-10^5 <= num <= 10^5`
- There will be at least one element in the data structure before calling `findMedian`.
- At most `5 * 10^4` calls will be made to `addNum` and `findMedian`.

## Examples

- `addNum(1); addNum(2); findMedian()` -> `1.5`
- After the above, `addNum(3); findMedian()` -> `2.0`
- `addNum(6); addNum(10); addNum(2); addNum(6); findMedian()` -> `6.0` (sorted: [2,6,6,10], median = (6+6)/2)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
class MedianFinder {
    public MedianFinder() {}
    public void addNum(int num) {}
    public double findMedian() {}
} { /* your code */ }
```
