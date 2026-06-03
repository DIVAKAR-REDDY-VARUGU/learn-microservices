# Evaluate Reverse Polish Notation

> **Pattern:** Stack → **Expression Evaluation (RPN/Infix)**  ·  [📖 Learn this pattern](../../README.md#expression-evaluation-rpninfix)

## Problem

You are given an array of strings `tokens` that represents an arithmetic expression in **Reverse Polish Notation** (postfix notation).

Evaluate the expression and return an integer that represents its value.

Notes:
- The valid operators are `"+"`, `"-"`, `"*"`, and `"/"`.
- Each operand may be an integer or another expression.
- Division between two integers always **truncates toward zero**.
- There will be no division by zero.
- The input represents a valid expression in RPN, and every operation/intermediate result fits in a 32-bit signed integer.

**Constraints:**
- `1 <= tokens.length <= 10^4`
- `tokens[i]` is either an operator (`"+"`, `"-"`, `"*"`, `"/"`) or an integer in the range `[-200, 200]`.

## Examples

- Input: `tokens = ["2","1","+","3","*"]` -> Output: `9`  (computes ((2 + 1) * 3))
- Input: `tokens = ["4","13","5","/","+"]` -> Output: `6`  (computes (4 + (13 / 5)))
- Input: `tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]` -> Output: `22`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int evalRPN(String[] tokens) { /* your code */ }
```
