# Integer to Roman

> **Pattern:** String Manipulation → **Integer and Roman Conversion**  ·  [📖 Learn this pattern](../../README.md#integer-and-roman-conversion)

## Problem

Seven different symbols represent Roman numerals with the following values: `I=1`, `V=5`, `X=10`, `L=50`, `C=100`, `D=500`, `M=1000`.

Given an integer `num`, convert it to a Roman numeral by greedily selecting the largest value symbol (or subtractive pair) that fits, appending it, subtracting it, and repeating. The subtractive forms are `IV=4`, `IX=9`, `XL=40`, `XC=90`, `CD=400`, and `CM=900`.

Return the resulting Roman numeral as a string.

**Constraints:**
- `1 <= num <= 3999`

## Examples

- Input: `num = 3749` -> Output: `"MMMDCCXLIX"`
- Input: `num = 58` -> Output: `"LVIII"`
- Input: `num = 1994` -> Output: `"MCMXCIV"`

## Your task

Implement the method in **`Answer.java`**, then run `java Answer.java`:

```java
public String intToRoman(int num) { /* your code */ }
```
