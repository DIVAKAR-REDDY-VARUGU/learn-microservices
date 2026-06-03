# Roman to Integer

> **Pattern:** String Manipulation → **Roman to Integer**  ·  [📖 Learn this pattern](../../README.md#roman-to-integer)

## Problem

Roman numerals are represented by seven symbols: `I`, `V`, `X`, `L`, `C`, `D`, and `M`, with values `1, 5, 10, 50, 100, 500, 1000` respectively.

Numbers are usually written largest to smallest from left to right and added together. However, in six **subtractive** cases a smaller numeral precedes a larger one and is subtracted:
- `I` before `V` (4) and `X` (9)
- `X` before `L` (40) and `C` (90)
- `C` before `D` (400) and `M` (900)

Given a Roman numeral string `s`, convert it to an integer.

**Constraints:**
- `1 <= s.length <= 15`
- `s` contains only the characters `('I', 'V', 'X', 'L', 'C', 'D', 'M')`.
- It is guaranteed that `s` is a valid Roman numeral in the range `[1, 3999]`.

## Examples

- Input: `s = "III"` -> Output: `3`
- Input: `s = "LVIII"` -> Output: `58` (L=50, V=5, III=3)
- Input: `s = "MCMXCIV"` -> Output: `1994` (M=1000, CM=900, XC=90, IV=4)

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public int romanToInt(String s) { /* your code */ }
```
