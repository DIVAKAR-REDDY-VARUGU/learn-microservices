# Min Stack

> **Pattern:** Stack → **Min Stack Design**  ·  [📖 Learn this pattern](../../README.md#min-stack-design)

## Problem

Design a stack that supports `push`, `pop`, `top`, and retrieving the minimum element, with each operation running in **O(1)** time.

Implement the `MinStack` class:
- `MinStack()` initializes the stack object.
- `void push(int val)` pushes the element `val` onto the stack.
- `void pop()` removes the element on the top of the stack.
- `int top()` returns the element on the top of the stack.
- `int getMin()` retrieves the minimum element currently in the stack.

All methods `pop`, `top`, and `getMin` are called only on non-empty stacks.

**Constraints:**
- `-2^31 <= val <= 2^31 - 1`
- At most `3 * 10^4` calls will be made to the methods in total.

## Examples

- Input: `["MinStack","push","push","push","getMin","pop","top","getMin"]`, `[[],[-2],[0],[-3],[],[],[],[]]` -> Output: `[null,null,null,null,-3,null,0,-2]`
- Sequence: push(5), push(2), getMin() -> `2`; pop(); getMin() -> `5`
- Sequence: push(1), top() -> `1`; getMin() -> `1`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
class MinStack { public MinStack(); public void push(int val); public void pop(); public int top(); public int getMin(); } { /* your code */ }
```
