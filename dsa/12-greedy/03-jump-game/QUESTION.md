# Jump Game

> **Pattern:** Greedy → **Jump Game**  ·  [📖 Learn this pattern](../../README.md#jump-game)

## Problem

## Jump Game

You are given an integer array `nums`. You are initially positioned at the array's **first index**, and each element `nums[i]` represents your **maximum jump length** at that position.

Return `true` if you can reach the **last index**, or `false` otherwise.

**Constraints:**
- `1 <= nums.length <= 10^4`
- `0 <= nums[i] <= 10^5`

**Greedy idea:** Track the farthest index reachable so far. Iterate left-to-right; if the current index ever exceeds the farthest reachable, you're stuck. Otherwise update `farthest = max(farthest, i + nums[i])`.

## Examples

- **Input:** `nums = [2,3,1,1,4]` -> **Output:** `true` (jump 1 step from index 0 to 1, then 3 steps to the last index)
- **Input:** `nums = [3,2,1,0,4]` -> **Output:** `false` (you always land on index 3 whose value is 0, so you can never reach the last index)
- **Input:** `nums = [0]` -> **Output:** `true` (already at the last index)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public boolean canJump(int[] nums) { /* your code */ }
```
