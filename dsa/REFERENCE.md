<a id="ref-top"></a>

# 📚 Data Structures & Algorithms — Reference (Java)

[⬅ Back to Patterns & Practice (README)](README.md)

Companion to the [DSA Pattern Playbook](README.md): full Java method/API tables, tree theory, and every classic algorithm with code.

## 🧭 Reference Contents
- 📌 [Java Reference — Arrays · Strings · Collections (every method + example)](#cheat)
- ☕ [Java Features (8 → latest) — interview edition](#ref-java-features)
- 🌳 [Trees — Terminology & Taxonomy](#ref-trees)
- ⛰️ [Heap & Array Representation](#ref-heap)
- 🌲 [Tree Traversals](#ref-traversals) — Preorder · Inorder · Postorder · Level-Order · Zigzag · Boundary · Diagonal
- 🧩 [Algorithm Design Paradigms](#ref-paradigms)
- 🔃 [Sorting](#ref-sorting) — Bubble · Selection · Insertion · Merge · Quick · Counting · Radix · Heap · Bucket · Shell · Tim
- 🔎 [Searching](#ref-searching) — Linear · Binary · Jump · Interpolation · Fibonacci
- 🕸️ [Graph](#ref-graph) — BFS · DFS · Dijkstra · Bellman-Ford · Floyd-Warshall · Kruskal · Prim
- 🔤 [String Matching](#ref-strings) — KMP · Rabin-Karp · Z · Manacher

---

<a id="cheat"></a>

## ☕ Java Reference — every method, with examples

[🔝 Back to contents](#ref-top)

> Full **CRUD + method tables + all loop forms + gotchas** for: Arrays · String · StringBuilder/StringBuffer · List · Set · Map · Stack/Queue/Deque · PriorityQueue · utility classes.

### 🔁 Iteration — at a glance
| Type | Idiom |
|---|---|
| **array** | `for (int x : a)` · `for (int i=0;i<a.length;i++)` |
| **List** | `for (int x : list)` · index loop |
| **Set** | `for (int x : set)` |
| **Map** | `for (var e : map.entrySet())` · `keySet()` · `values()` · `forEach((k,v)->...)` |
| **Stack/Queue/Deque** | `while (!dq.isEmpty()) dq.poll()/pop()` |
| **PriorityQueue** | `while (!pq.isEmpty()) pq.poll()` (sorted) |
| **String chars** | `for (char c : s.toCharArray())` |

### ⏱️ Operation complexity (know cold)
| Structure | add/offer | get/contains | remove |
|---|---|---|---|
| ArrayList | O(1)* | get O(1) / contains O(n) | O(n) |
| HashMap / HashSet | O(1) | O(1) | O(1) |
| TreeMap / TreeSet | O(log n) | O(log n) | O(log n) |
| ArrayDeque (stack/queue) | O(1) ends | — | O(1) ends |
| PriorityQueue | O(log n) | peek O(1) | poll O(log n) |

_*amortized_

---

### Arrays

A fixed-size, ordered, zero-indexed container holding elements of a single type (primitives or objects). Use when the element count is known/stable and you need fast O(1) index access and tight memory layout. Insertion order is preserved; size is immutable after creation (no add/remove — use `ArrayList` if the size must grow).

**CRUD**

```java
// CREATE
int[] a = new int[5];                 // {0,0,0,0,0} — default-initialized
int[] b = {10, 20, 30};               // literal initializer (only at declaration)
int[] c = new int[]{10, 20, 30};      // explicit form (needed when not at declaration / inline arg)
String[] s = new String[3];           // {null, null, null} — objects default to null
int[][] grid = {{1, 2}, {3, 4}};      // 2D array (array of arrays)

// READ — by index, O(1)
int first = b[0];                     // 10
int len   = b.length;                 // 3  (field, NOT a method — no parentheses)
int last  = b[b.length - 1];          // 30
int cell  = grid[1][0];               // 3

// UPDATE — overwrite by index, O(1)
b[1] = 99;                            // b -> {10, 99, 30}
grid[0][1] = 7;                       // grid -> {{1,7},{3,4}}

// DELETE — size is FIXED: you cannot remove a slot.
b[1] = 0;                             // "logical" delete: reset to default (0 / null / false)
// To truly shrink, build a new array (e.g. via copyOfRange or a stream filter):
int[] shorter = java.util.Arrays.copyOfRange(b, 0, 2);   // {10, 0} — drops last element
```

**Methods table** (all `java.util.Arrays`; assume `import java.util.Arrays;`)

| Method | What it does | Example |
|--------|--------------|---------|
| `sort(arr)` | Sorts the whole array ascending, in place (dual-pivot quicksort for primitives, stable mergesort for objects). | `int[] x={3,1,2}; Arrays.sort(x); // x = {1,2,3}` |
| `sort(arr, from, to)` | Sorts only `[from, to)` in place; rest untouched. | `int[] x={5,3,1,9}; Arrays.sort(x,1,3); // {5,1,3,9}` |
| `fill(arr, val)` | Sets every element to `val`, in place. | `int[] x=new int[3]; Arrays.fill(x,7); // {7,7,7}` |
| `copyOf(arr, newLen)` | Returns a new array of length `newLen`; truncates or pads with defaults. | `int[] x={1,2}; int[] y=Arrays.copyOf(x,4); // {1,2,0,0}` |
| `copyOfRange(arr, from, to)` | Returns a new array copying `[from, to)` (`to` may exceed length → padded). | `int[] x={1,2,3,4}; int[] y=Arrays.copyOfRange(x,1,3); // {2,3}` |
| `equals(a, b)` | `true` if same length and all elements equal (1D / shallow). | `Arrays.equals(new int[]{1,2}, new int[]{1,2}); // true` |
| `deepEquals(a, b)` | Deep equality for nested/2D arrays (recurses into sub-arrays). | `Arrays.deepEquals(new int[][]{{1}}, new int[][]{{1}}); // true (equals would be false)` |
| `toString(arr)` | Readable 1D string `[e0, e1, ...]`. | `Arrays.toString(new int[]{1,2,3}); // "[1, 2, 3]"` |
| `deepToString(arr)` | Readable string for nested/2D arrays. | `Arrays.deepToString(new int[][]{{1,2},{3}}); // "[[1, 2], [3]]"` |
| `asList(arr)` | Fixed-size `List` view backed by the array (objects only; `set` ok, `add`/`remove` throw). | `List<String> l = Arrays.asList("a","b"); // [a, b]` |
| `binarySearch(arr, key)` | Index of `key` in a **sorted** array; if absent returns `-(insertionPoint)-1`. | `int[] x={1,3,5}; Arrays.binarySearch(x,3); // 1` |
| `stream(arr)` | Opens a stream over the array for functional pipelines. | `int sum = Arrays.stream(new int[]{1,2,3}).sum(); // 6` |
| `setAll(arr, fn)` | Sets each index `i` to `fn(i)` via a generator function. | `int[] x=new int[4]; Arrays.setAll(x, i -> i*i); // {0,1,4,9}` |

**Iteration / loops**

```java
int[] arr = {10, 20, 30};

// 1) Classic index for — full control (index, reverse, skip, mutate in place)
for (int i = 0; i < arr.length; i++) {
    System.out.println(i + " -> " + arr[i]);
}

// 2) Reverse
for (int i = arr.length - 1; i >= 0; i--) {
    System.out.println(arr[i]);
}

// 3) Enhanced for-each — read-only over VALUES (cannot reassign arr[i], no index)
for (int v : arr) {
    System.out.println(v);
}

// 4) while
int i = 0;
while (i < arr.length) {
    System.out.println(arr[i]);
    i++;
}

// 5) do-while (runs at least once)
int j = 0;
do {
    System.out.println(arr[j]);
    j++;
} while (j < arr.length);

// 6) Stream (functional)
Arrays.stream(arr).forEach(System.out::println);
java.util.stream.IntStream.range(0, arr.length)   // index + value via stream
        .forEach(k -> System.out.println(k + "=" + arr[k]));

// 7) Arrays.toString for quick whole-array print (no manual loop)
System.out.println(Arrays.toString(arr));          // [10, 20, 30]

// --- 2D iteration ---
int[][] grid = {{1, 2}, {3, 4, 5}};
for (int r = 0; r < grid.length; r++) {            // rows
    for (int col = 0; col < grid[r].length; col++) {// cols (jagged-safe: per-row length)
        System.out.print(grid[r][col] + " ");
    }
}
for (int[] row : grid) {                            // for-each over rows, then values
    for (int v : row) System.out.print(v + " ");
}
```

**Gotchas**

- `length` is a **field**, not a method: `arr.length` (no `()`); `String`/collection use `.length()` / `.size()`. Easy to mix up.
- Indexing out of bounds throws `ArrayIndexOutOfBoundsException` at runtime; valid indices are `0 .. length-1`.
- Fixed size: no `add`/`remove`. "Growing" means allocating a new array (`copyOf`) and reassigning.
- `equals`/`toString` from `Object` (i.e. `arr.equals(other)`, `arr.toString()`) do **reference** comparison / print a hash like `[I@1b6d3586`. Always use `Arrays.equals` / `Arrays.toString` (and the `deep*` variants for 2D/nested).
- `Arrays.equals` is shallow — on a 2D array it compares inner-array references, so it returns `false` for equal-content nested arrays. Use `Arrays.deepEquals`.
- `binarySearch` requires the array to be **sorted first**; on an unsorted array the result is undefined (not just "not found").
- `Arrays.asList(T...)` returns a fixed-size, array-backed list — `add`/`remove` throw `UnsupportedOperationException`, and `set` writes through to the array. Wrap in `new ArrayList<>(...)` for a resizable copy.
- `Arrays.asList(int[])` is a trap: an `int[]` is a single `Object`, so you get a `List<int[]>` of size 1, not a `List<Integer>`. Use `Integer[]`, or `Arrays.stream(arr).boxed().collect(...)`.
- `copyOfRange(arr, from, to)` is `[from, to)` (exclusive end); `to` may exceed `length` (pads with defaults), but `from > to` or `from < 0` throws.
- Array default values: `0` (numeric), `false` (boolean), `'\u0000'` (char), `null` (objects) — `new T[n]` does not leave them "empty".
- 2D arrays are arrays of arrays and may be **jagged** (rows of differing length, or even `null` rows) — always iterate with each row's own `.length`, never assume a fixed column count.
- Assigning `a = b` copies the **reference**, not the contents — both point to the same array. Use `clone()` (shallow) or `copyOf` for an independent 1D copy; for 2D, `clone()` still shares inner rows.

### String

`java.lang.String` is an **immutable**, ordered sequence of UTF-16 `char` values. Use it for any text; because instances never change after creation, they are thread-safe and safe to share/cache. For heavy in-place mutation (loops that build text), prefer `StringBuilder`.

**CRUD**

```java
// CREATE
String a = "hello";                       // literal -> string pool
String b = new String("hello");           // forces a new heap object (rarely needed)
String c = String.valueOf(42);            // "42" from another type
String d = new String(new char[]{'h','i'}); // from char[]
String e = String.join("-", "a", "b");    // "a-b"

// READ
char ch  = a.charAt(0);                   // 'h'
int  len = a.length();                    // 5
String sub = a.substring(1, 3);           // "el"

// UPDATE (returns a NEW string; original is unchanged)
String up = a.toUpperCase();              // "HELLO"  (a is still "hello")
String r  = a.replace('l', 'L');          // "heLLo"
String cat = a.concat(" world");          // "hello world"

// DELETE (no in-place delete; build a new string without the parts you drop)
String noL = a.replace("l", "");          // "heo"
String head = a.substring(0, 2);          // "he"  (drop the tail)
// To "clear" a variable, just reassign: a = "";
```

**Methods table**

| Method | What it does | Example |
|---|---|---|
| `length()` | Number of `char` units (UTF-16, not code points) | `"abc".length()` // 3 |
| `charAt(int)` | `char` at index; throws `StringIndexOutOfBoundsException` if out of range | `"abc".charAt(1)` // 'b' |
| `isEmpty()` | True if length == 0 | `"".isEmpty()` // true |
| `isBlank()` (Java 11+) | True if empty or only whitespace | `"  \t".isBlank()` // true |
| `substring(begin)` | From `begin` to end | `"hello".substring(2)` // "llo" |
| `substring(begin,end)` | `[begin, end)` — end exclusive | `"hello".substring(1,3)` // "el" |
| `indexOf(...)` | First index of char/substring, or -1; optional fromIndex | `"banana".indexOf("a")` // 1 |
| `lastIndexOf(...)` | Last index of char/substring, or -1 | `"banana".lastIndexOf("a")` // 5 |
| `contains(CharSequence)` | True if substring present | `"hello".contains("ell")` // true |
| `startsWith(String)` | Prefix test (overload with offset) | `"hello".startsWith("he")` // true |
| `endsWith(String)` | Suffix test | `"hello".endsWith("lo")` // true |
| `toCharArray()` | New `char[]` of the contents | `"hi".toCharArray()` // {'h','i'} |
| `chars()` (Java 9+) | `IntStream` of char values | `"abc".chars().count()` // 3 |
| `toLowerCase()` | Lowercased copy (locale-sensitive overload exists) | `"ABC".toLowerCase()` // "abc" |
| `toUpperCase()` | Uppercased copy | `"abc".toUpperCase()` // "ABC" |
| `trim()` | Strips ASCII chars `<= 0x20` from both ends | `"  hi  ".trim()` // "hi" |
| `strip()` (Java 11+) | Strips Unicode whitespace from both ends | `"\u2003hi ".strip()` // "hi" |
| `replace(old,new)` | Replaces all literal char/CharSequence occurrences | `"aaa".replace("a","b")` // "bbb" |
| `replaceAll(regex,repl)` | Regex replace; `$1` back-refs in replacement | `"a1b2".replaceAll("\\d","#")` // "a#b#" |
| `split(regex)` | Splits on regex into `String[]` (optional limit) | `"a,b,c".split(",")` // ["a","b","c"] |
| `equals(Object)` | Content equality (case-sensitive) | `"abc".equals("abc")` // true |
| `equalsIgnoreCase(String)` | Content equality ignoring case | `"ABC".equalsIgnoreCase("abc")` // true |
| `compareTo(String)` | Lexicographic order; <0, 0, >0 | `"apple".compareTo("banana")` // negative |
| `compareToIgnoreCase(String)` | Same, case-insensitive | `"ABC".compareToIgnoreCase("abc")` // 0 |
| `concat(String)` | Appends; returns new string (`+` is more idiomatic) | `"foo".concat("bar")` // "foobar" |
| `repeat(int)` (Java 11+) | Repeats the string n times | `"ab".repeat(3)` // "ababab" |
| `String.join(delim, ...)` | Joins elements/Iterable with delimiter (static) | `String.join("-","a","b")` // "a-b" |
| `String.format(fmt, ...)` | printf-style formatting (static) | `String.format("%s=%d","x",5)` // "x=5" |
| `String.valueOf(x)` | Converts any type/primitive to its string (static) | `String.valueOf(true)` // "true" |
| `matches(regex)` | True if the WHOLE string matches the regex | `"a1b2".matches("[a-z\\d]+")` // true |
| `codePointAt(int)` | Unicode code point at index (handles surrogate pairs) | `"A".codePointAt(0)` // 65 |
| `intern()` | Returns the canonical pooled instance | `new String("x").intern() == "x"` // true |

**Iteration / loops**

```java
String s = "héllo";

// 1) Classic index loop over char units
for (int i = 0; i < s.length(); i++) {
    char c = s.charAt(i);
    // process c
}

// 2) char[] enhanced for-each (clean, but copies the array)
for (char c : s.toCharArray()) {
    // process c
}

// 3) chars() IntStream (Java 9+) — each int is a char value
s.chars().forEach(ic -> {
    char c = (char) ic;
    // process c
});
// e.g. count letters:
long letters = s.chars().filter(Character::isLetter).count();

// 4) codePoints() — CORRECT for emoji / supplementary chars (surrogate pairs)
String emoji = "a😀b";
emoji.codePoints().forEach(cp -> {
    // cp is a full Unicode code point, not a half surrogate
    System.out.append(Character.toChars(cp), 0, Character.charCount(cp));
});

// 5) Manual code-point walk (no streams)
for (int i = 0; i < emoji.length(); ) {
    int cp = emoji.codePointAt(i);
    i += Character.charCount(cp); // advance 1 or 2 char units
}

// 6) Iterate split tokens
for (String token : "a,b,c".split(",")) {
    // process token
}
```

**Gotchas**

- **Immutability:** every "mutating" method (`toUpperCase`, `replace`, `trim`, `substring`, `concat`, …) returns a **new** String and leaves the original untouched. `s.trim();` on its own line is a no-op bug — you must assign the result: `s = s.trim();`.
- **`==` vs `equals`:** `==` compares **references** (same object?), `equals` compares **content**. Always use `equals`/`equalsIgnoreCase` for text. `==` may *appear* to work for literals because identical compile-time literals share one pooled instance, but `new String("x") == "x"` is `false` while `.equals` is `true`. To avoid NPEs, put the known-non-null literal first: `"yes".equals(input)`.
- **String pool & `intern()`:** literals are auto-interned; `new String(...)` always creates a distinct heap object. `intern()` returns the pooled copy so `==` succeeds again, but overusing it can pressure the pool — rarely needed in app code.
- **`replace` vs `replaceAll`:** `replace` takes **literal** text (no regex); `replaceAll` takes a **regex** and the replacement string treats `\` and `$` specially (use `Matcher.quoteReplacement` for literal replacements). For a single literal swap, `replace` is faster and safer.
- **`split` surprises:** the argument is a **regex**, so `"a.b.c".split(".")` returns an empty array (`.` matches everything) — escape it: `split("\\.")`. Trailing empty strings are removed by default; pass a negative limit (`split(",", -1)`) to keep them.
- **`trim` vs `strip`:** `trim` only removes chars `<= U+0020` (misses Unicode spaces like `\u00A0`/`\u2003`); `strip` (Java 11+) is Unicode-aware. Prefer `strip` for user input.
- **`length()` is char units, not characters:** for text with emoji/CJK supplementary chars, `length()` counts UTF-16 units (a surrogate pair counts as 2). Use `codePointCount(0, length())` or `codePoints()` for true character/code-point counts.
- **`substring` indices:** `begin` inclusive, `end` exclusive; `begin == end` yields `""`, and out-of-range or `begin > end` throws `StringIndexOutOfBoundsException`. (Modern JDKs no longer share the backing array, so substring does not leak the parent's memory.)
- **`indexOf`/`contains` are case-sensitive:** there is no built-in case-insensitive search — lowercase both sides first, or use a regex with `Pattern.CASE_INSENSITIVE`.
- **`compareTo` returns a magnitude, not just sign:** don't assume it's exactly `-1/0/1`; only the sign is meaningful for ordering. It compares by UTF-16 value, so uppercase sorts before lowercase (`'Z' < 'a'`).
- **`matches` anchors the whole string:** `"hello world".matches("hello")` is `false` because the regex must match the entire input — use `Pattern`/`Matcher.find()` for partial matches, and remember it recompiles the regex every call (cache a `Pattern` in hot loops).
- **Concatenation in loops:** `result += x` inside a loop creates a new String each iteration (O(n²)); use `StringBuilder` instead. A single `a + b + c` line is fine — the compiler optimizes it.
- **`valueOf(null)` ambiguity:** `String.valueOf((Object) null)` returns `"null"`, but `String.valueOf((char[]) null)` throws NPE — watch the overload that gets selected.

### StringBuilder and StringBuffer

Mutable sequences of characters — unlike `String`, they can be modified in place (append/insert/delete) without creating new objects, making them the right tool for building strings in loops. Both preserve insertion order and are indexed (0-based). `StringBuilder` is the default choice (fast, non-synchronized); `StringBuffer` has the identical API but is synchronized (thread-safe) and therefore slower — use it only when a single builder instance is shared across threads.

**CRUD**

```java
// CREATE
StringBuilder sb = new StringBuilder();              // empty, default capacity 16
StringBuilder sb2 = new StringBuilder("Hello");      // from a String
StringBuilder sb3 = new StringBuilder(64);           // empty, initial capacity 64
StringBuffer buf = new StringBuffer("thread-safe");  // synchronized variant, same API

// READ
char c = sb2.charAt(1);          // 'e'
int len = sb2.length();          // 5
String s = sb2.toString();       // "Hello"  -> convert back to String
String part = sb2.substring(1, 3); // "el"

// UPDATE
sb2.append(" World");            // "Hello World"
sb2.insert(0, ">> ");            // ">> Hello World"
sb2.setCharAt(3, 'X');           // ">> Xello World"
sb2.replace(0, 3, "");           // "Xello World"

// DELETE
sb2.deleteCharAt(0);             // "ello World"
sb2.delete(0, 5);                // " World"
sb2.setLength(0);                // ""  -> clears the whole buffer
```

**Methods table**

| Method | What it does | Example |
| --- | --- | --- |
| `append(...)` | Adds text/value to the end; many overloads (`String`, `char`, `char[]`, `int`, `long`, `float`, `double`, `boolean`, `Object`, `CharSequence`). Returns `this` so calls chain. | `new StringBuilder("a").append('b').append(1).append(true)` // `"ab1true"` |
| `append(char[], int offset, int len)` | Appends a sub-range of a char array. | `new StringBuilder().append(new char[]{'x','y','z'}, 1, 2)` // `"yz"` |
| `insert(int offset, ...)` | Inserts text/value at `offset`; same overload set as `append`. | `new StringBuilder("ac").insert(1, "b")` // `"abc"` |
| `delete(int start, int end)` | Removes chars in `[start, end)` (end exclusive). | `new StringBuilder("hello").delete(1, 3)` // `"hlo"` |
| `deleteCharAt(int index)` | Removes the single char at `index`. | `new StringBuilder("hello").deleteCharAt(0)` // `"ello"` |
| `replace(int start, int end, String str)` | Replaces chars in `[start, end)` with `str` (lengths need not match). | `new StringBuilder("hello").replace(1, 3, "XY")` // `"hXYlo"` |
| `reverse()` | Reverses the character sequence in place. | `new StringBuilder("abc").reverse()` // `"cba"` |
| `charAt(int index)` | Returns the char at `index`. | `new StringBuilder("abc").charAt(2)` // `'c'` |
| `setCharAt(int index, char ch)` | Overwrites the char at `index` (returns void). | `StringBuilder b = new StringBuilder("cat"); b.setCharAt(0,'h')` // `"hat"` |
| `length()` | Number of characters currently held. | `new StringBuilder("abc").length()` // `3` |
| `setLength(int n)` | Truncates (if `n < length`) or pads with `'\u0000'` (if `n > length`); `setLength(0)` clears. | `StringBuilder b = new StringBuilder("hello"); b.setLength(2)` // `"he"` |
| `capacity()` | Current allocated buffer size (>= length); grows automatically. | `new StringBuilder().capacity()` // `16` (default) |
| `indexOf(String str)` / `indexOf(String, int from)` | First index of substring, or `-1`; optional start index. Also `lastIndexOf`. | `new StringBuilder("abcabc").indexOf("bc", 2)` // `4` |
| `substring(int start)` / `substring(int start, int end)` | Returns a `String` for the range (does not modify the builder). | `new StringBuilder("hello").substring(1, 4)` // `"ell"` |
| `toString()` | Builds an immutable `String` snapshot of current contents. | `new StringBuilder("hi").append('!').toString()` // `"hi!"` |

**Iteration / loops**

```java
StringBuilder sb = new StringBuilder("Hello");

// 1. Classic index loop (most common; allows setCharAt during iteration)
for (int i = 0; i < sb.length(); i++) {
    char c = sb.charAt(i);
    System.out.print(c);
}

// 2. Reverse index loop
for (int i = sb.length() - 1; i >= 0; i--) {
    System.out.print(sb.charAt(i));   // prints "olleH"
}

// 3. Convert to char[] and use enhanced for-each
for (char c : sb.toString().toCharArray()) {
    System.out.print(c);
}

// 4. chars() IntStream (Java 8+); ints are code points cast to char
sb.chars().forEach(ch -> System.out.print((char) ch));

// 5. Stream collected/transformed
String upper = sb.chars()
                 .mapToObj(ch -> String.valueOf((char) ch).toUpperCase())
                 .collect(java.util.stream.Collectors.joining());  // "HELLO"

// Build-and-iterate pattern: accumulate in a loop, then snapshot once
StringBuilder out = new StringBuilder();
String[] words = {"a", "b", "c"};
for (int i = 0; i < words.length; i++) {
    if (i > 0) out.append(", ");
    out.append(words[i]);
}
String csv = out.toString();   // "a, b, c"
```

Note: `StringBuilder` does not implement `Iterable`, so you cannot use a direct `for (char c : sb)` enhanced-for over the builder itself — go through `toString().toCharArray()` or `chars()`.

**Gotchas**

- `delete(start, end)`, `replace(start, end, ...)`, and `substring(start, end)` all treat `end` as **exclusive**; off-by-one here is the classic bug.
- `charAt`, `setCharAt`, `deleteCharAt`, and `insert` throw `StringIndexOutOfBoundsException` (or `IndexOutOfBounds`) for out-of-range indices — `setCharAt`/`deleteCharAt` require `index < length`, while `insert`/`append` allow `offset == length`.
- `setCharAt` and `reverse` return `void`/mutate in place; `append`, `insert`, `delete`, `replace`, `reverse` return `this` for chaining (`reverse()` chains but `setCharAt` does not).
- `length()` is the count of characters; `capacity()` is the allocated buffer — they are different. Setting capacity via the constructor is an optimization to avoid reallocations, not a content change.
- `setLength(n)` with `n` larger than current length pads with the null char `'\u0000'` (which prints as blank but is a real character counted by `length()`), not spaces.
- `==` compares references, and `StringBuilder` does **not** override `equals()`/`hashCode()` — two builders with identical text are not `equal`. Compare via `sb1.toString().equals(sb2.toString())` (or `sb1.compareTo(sb2)` in Java 11+, which does compare contents).
- Don't use `StringBuffer` "just to be safe" — its synchronization adds overhead with no benefit unless one instance is genuinely shared across threads; in concurrent code each thread should usually have its own `StringBuilder` anyway.
- Repeated `String` concatenation with `+` inside a loop creates a new object each pass (O(n²)); that's exactly the case `StringBuilder` solves — but a single `a + b + c` expression is already compiled to a `StringBuilder`, so don't over-optimize trivial cases.
- `insert`/`append` of a `null` reference inserts the literal text `"null"` (four chars), not a `NullPointerException` — a frequent source of stray `"null"` in output.
- `chars()` yields `int` code units; cast to `(char)` to print correctly, and be aware it iterates UTF-16 units (use `codePoints()` if you must handle characters outside the Basic Multilingual Plane).

### List / ArrayList / LinkedList

An ordered, index-based collection that allows duplicates and `null`. Use `ArrayList` as your default (array-backed: O(1) random access, fast iteration); use `LinkedList` (doubly-linked nodes) only when you need cheap insert/remove at the ends or use it as a `Deque`/`Queue`. Both are mutable, insertion-ordered, and not thread-safe.

**CRUD**

```java
// CREATE
List<String> list = new ArrayList<>();          // empty
List<String> b = new LinkedList<>();
List<String> seeded = new ArrayList<>(List.of("a", "b", "c")); // from another collection
list.add("x");                                  // append -> [x]
list.add(0, "y");                               // insert at index -> [y, x]

// READ
String first = list.get(0);                     // y  (IndexOutOfBoundsException if bad index)
boolean has  = list.contains("x");              // true
int n        = list.size();                     // 2

// UPDATE
list.set(1, "z");                               // replace index 1 -> [y, z] (returns old value "x")

// DELETE
list.remove(0);                                 // remove by index -> [z]
list.remove("z");                               // remove by value -> []  (returns boolean)
list.clear();                                   // -> []
```

**Methods table**

| Method | What it does | Example |
|--------|--------------|---------|
| `add(E e)` | Appends to end; returns `true` | `list.add("a"); // [a]` |
| `add(int index, E e)` | Inserts at index, shifts rest right | `list.add(0, "x"); // [x, a]` |
| `get(int index)` | Returns element at index | `list.get(1); // "a"` |
| `set(int index, E e)` | Replaces element, returns the old one | `list.set(0, "y"); // returns "x", list=[y, a]` |
| `remove(int index)` | Removes by position, returns removed element | `list.remove(0); // returns "y", list=[a]` |
| `remove(Object o)` | Removes first matching element, returns `boolean` | `list.remove("a"); // true, list=[]` |
| `indexOf(Object o)` | First index of element, or `-1` | `[a,b,a].indexOf("a"); // 0` |
| `lastIndexOf(Object o)` | Last index of element, or `-1` | `[a,b,a].lastIndexOf("a"); // 2` |
| `contains(Object o)` | `true` if present (uses `equals`) | `list.contains("b"); // true` |
| `size()` | Number of elements | `list.size(); // 3` |
| `isEmpty()` | `true` if size == 0 | `list.isEmpty(); // false` |
| `clear()` | Removes all elements | `list.clear(); // []` |
| `addAll(Collection c)` | Appends all (also `addAll(index, c)`) | `list.addAll(List.of("a","b")); // [a, b]` |
| `removeAll(Collection c)` | Removes everything found in `c` | `[a,b,c].removeAll(List.of("a","c")); // [b]` |
| `retainAll(Collection c)` | Keeps only what's in `c` (intersection) | `[a,b,c].retainAll(List.of("a","c")); // [a, c]` |
| `subList(int from, int to)` | **View** of `[from, to)`; changes write through | `[a,b,c,d].subList(1,3); // [b, c]` |
| `toArray()` / `toArray(T[])` | Copies to array (typed form preferred) | `list.toArray(new String[0]); // String[]` |
| `iterator()` | `Iterator` for forward traversal + safe `remove()` | `Iterator<String> it = list.iterator();` |
| `listIterator()` | Bidirectional iterator; can `add`/`set`/go back | `ListIterator<String> li = list.listIterator();` |
| `sort(Comparator c)` | Sorts in place; `null` = natural order | `list.sort(Comparator.naturalOrder()); // [a, b, c]` |
| `replaceAll(UnaryOperator op)` | Applies function to every element in place | `list.replaceAll(String::toUpperCase); // [A, B]` |
| `forEach(Consumer c)` | Runs action per element | `list.forEach(System.out::println);` |

**LinkedList extras** (Deque/Queue methods, all O(1) at the ends)

| Method | What it does | Example |
|--------|--------------|---------|
| `addFirst(e)` / `offerFirst(e)` | Insert at head | `dq.addFirst("a"); // [a, ...]` |
| `addLast(e)` / `offerLast(e)` | Insert at tail (same as `add`) | `dq.addLast("z"); // [..., z]` |
| `getFirst()` / `getLast()` | Peek head/tail; **throws** if empty | `dq.getFirst(); // "a"` |
| `removeFirst()` / `removeLast()` | Remove + return head/tail; **throws** if empty | `dq.removeFirst(); // "a"` |
| `peek()` / `peekFirst()` | Look at head; returns `null` if empty | `dq.peek(); // "a" or null` |
| `poll()` / `pollFirst()` | Remove + return head; `null` if empty | `dq.poll(); // "a" or null` |
| `offer(e)` / `offerLast(e)` | Add to tail; returns `true` | `dq.offer("z"); // true` |

> `peek`/`poll`/`offer` are the *fail-soft* (null-returning) Queue API; `getFirst`/`removeFirst`/`addFirst` are the *fail-fast* (exception-throwing) Deque API. Pick one style and stay consistent.

**Iteration / loops**

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));

// 1. Classic indexed for-loop (fast & safe to remove by index, iterate backwards when removing)
for (int i = 0; i < list.size(); i++) {
    System.out.println(list.get(i));
}

// 2. Enhanced for-each (cleanest read-only traversal)
for (String s : list) {
    System.out.println(s);
}

// 3. Iterator — the ONLY safe way to remove during iteration
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (s.equals("b")) it.remove();   // safe; list.remove() here = ConcurrentModificationException
}

// 4. ListIterator — bidirectional, plus set/add while iterating
ListIterator<String> li = list.listIterator();
while (li.hasNext()) {
    String s = li.next();
    li.set(s.toUpperCase());          // replace in place
}
while (li.hasPrevious()) {            // walk back to front
    System.out.println(li.previous());
}

// 5. forEach + lambda (Java 8+)
list.forEach(s -> System.out.println(s));
list.forEach(System.out::println);    // method reference

// 6. Stream (transform/filter/collect; use when you need pipeline ops)
list.stream()
    .filter(s -> !s.isEmpty())
    .map(String::toLowerCase)
    .forEach(System.out::println);
```

**Gotchas**

- **`remove(int)` vs `remove(Object)` — the classic cast trap.** With `List<Integer>`, `list.remove(2)` calls `remove(int index)` and deletes the element **at index 2**, not the value `2`. To remove the value, box it: `list.remove(Integer.valueOf(2))`. This only bites `List<Integer>` (and other numeric types); for `List<String>` there's no ambiguity.
- **`ConcurrentModificationException`** — never structurally modify a list (`add`/`remove`) inside a for-each loop. Use `Iterator.remove()`, `removeIf(...)`, or iterate a copy.
- **`removeIf` is the cleanest conditional delete:** `list.removeIf(s -> s.startsWith("x"));` — no iterator boilerplate.
- **`List.of(...)` / `Arrays.asList(...)` are not fully mutable.** `List.of` is fully immutable (any mutation throws `UnsupportedOperationException`). `Arrays.asList` is fixed-size: `set` works but `add`/`remove` throw. Wrap in `new ArrayList<>(...)` for a real mutable list.
- **`subList` returns a live view, not a copy.** Mutations write through to the backing list, and structurally modifying the backing list invalidates the sublist. Copy it (`new ArrayList<>(list.subList(a, b))`) if you need independence.
- **`toArray()` with no arg returns `Object[]`,** not `String[]`. Use `list.toArray(new String[0])` for a typed array.
- **Wrong tool = O(n).** `LinkedList.get(i)` is O(n) (it walks the chain) — never index-loop a `LinkedList`. Conversely `ArrayList.add(0, e)` / `remove(0)` is O(n) because it shifts every element.
- **`indexOf`/`contains`/`remove(Object)` rely on `equals()`/`hashCode()`.** Custom objects must override them, or matching falls back to reference identity.
- **`Iterator.remove()` must follow a `next()`** and can only be called once per `next()`, else `IllegalStateException`.

### Set / HashSet / LinkedHashSet / TreeSet

A `Set<E>` is a `Collection` that holds **no duplicate elements** (by `equals`/`hashCode`). Use `HashSet` for fast unordered membership tests (O(1)), `LinkedHashSet` when you need predictable **insertion order**, and `TreeSet` when you need elements kept **sorted** with range/navigation queries. All three are mutable and allow removal/iteration; `HashSet` and `LinkedHashSet` permit one `null`, `TreeSet` does **not** (it must compare elements).

#### CRUD

```java
import java.util.*;

// CREATE
Set<String> hash   = new HashSet<>();                 // no order
Set<String> linked = new LinkedHashSet<>();           // insertion order
TreeSet<String> tree = new TreeSet<>();               // natural sorted order
Set<String> seeded = new HashSet<>(List.of("a","b")); // from another collection
Set<String> immutable = Set.of("x","y","z");          // immutable, throws on mutation
TreeSet<String> byLen = new TreeSet<>(Comparator.comparingInt(String::length)); // custom order

// READ (Set has no index/get — you test membership or iterate)
boolean has = hash.contains("a");   // true/false
int n = hash.size();                // number of elements
boolean empty = hash.isEmpty();

// UPDATE (Sets are not "updated" in place — there is no set(index).
// To "change" an element you remove the old and add the new.)
linked.remove("a");
linked.add("A");
// add() returns false if the element was already present (no duplicate inserted)

// DELETE
hash.remove("b");   // remove one element, returns true if it was present
hash.clear();       // remove everything
```

#### Methods table

| Method | What it does | Example |
|--------|--------------|---------|
| `add(e)` | Adds element; returns `false` if already present (no dup) | `s.add("a"); // true, then s.add("a") -> false` |
| `remove(o)` | Removes element if present; returns `true` if removed | `s.remove("a"); // true if "a" was in set` |
| `contains(o)` | Membership test | `s.contains("a"); // true` |
| `size()` | Number of elements | `s.size(); // e.g. 3` |
| `isEmpty()` | `true` if no elements | `s.isEmpty(); // false` |
| `clear()` | Removes all elements | `s.clear(); // s is now {}` |
| `addAll(c)` | Union: adds all from `c` (skips dups) | `a.addAll(b); // a becomes a ∪ b` |
| `retainAll(c)` | Intersection: keep only elements also in `c` | `a={1,2,3}; a.retainAll(Set.of(2,3,4)); // a={2,3}` |
| `removeAll(c)` | Difference: remove every element found in `c` | `a={1,2,3}; a.removeAll(Set.of(2,3)); // a={1}` |
| `iterator()` | Returns `Iterator` for traversal/safe removal | `Iterator<String> it = s.iterator();` |
| `first()` *(TreeSet)* | Smallest element | `t={1,3,5}; t.first(); // 1` |
| `last()` *(TreeSet)* | Largest element | `t={1,3,5}; t.last(); // 5` |
| `floor(e)` *(TreeSet)* | Greatest element `<= e` (or `null`) | `t={1,3,5}; t.floor(4); // 3` |
| `ceiling(e)` *(TreeSet)* | Smallest element `>= e` (or `null`) | `t={1,3,5}; t.ceiling(4); // 5` |
| `higher(e)` *(TreeSet)* | Smallest element strictly `> e` (or `null`) | `t={1,3,5}; t.higher(3); // 5` |
| `lower(e)` *(TreeSet)* | Greatest element strictly `< e` (or `null`) | `t={1,3,5}; t.lower(3); // 1` |
| `headSet(e)` *(TreeSet)* | View of elements `< e` (exclusive) | `t={1,3,5}; t.headSet(5); // [1, 3]` |
| `tailSet(e)` *(TreeSet)* | View of elements `>= e` (inclusive) | `t={1,3,5}; t.tailSet(3); // [3, 5]` |
| `subSet(from,to)` *(TreeSet)* | View `[from, to)` — from inclusive, to exclusive | `t={1,3,5}; t.subSet(1,5); // [1, 3]` |
| `pollFirst()` *(TreeSet)* | Removes and returns smallest (or `null`) | `t={1,3,5}; t.pollFirst(); // 1, t now {3,5}` |
| `pollLast()` *(TreeSet)* | Removes and returns largest (or `null`) | `t={1,3,5}; t.pollLast(); // 5, t now {1,3}` |

`TreeSet` also has overloaded `headSet(e, inclusive)`, `tailSet(e, inclusive)`, and `subSet(from, fromInc, to, toInc)` when you need to control endpoint inclusivity, plus `descendingSet()` / `descendingIterator()` for reverse traversal.

#### Iteration / loops

```java
Set<String> s = new LinkedHashSet<>(List.of("a", "b", "c"));

// 1) Enhanced for-each (most common)
for (String e : s) {
    System.out.println(e);
}

// 2) Explicit Iterator — required if you must remove() during iteration
Iterator<String> it = s.iterator();
while (it.hasNext()) {
    String e = it.next();
    if (e.equals("b")) it.remove();   // safe; s.remove(e) here would throw ConcurrentModificationException
}

// 3) forEach with a lambda (Java 8+)
s.forEach(e -> System.out.println(e));
s.forEach(System.out::println);       // method reference

// 4) Stream pipeline
s.stream()
 .filter(e -> e.compareTo("a") > 0)
 .sorted()
 .forEach(System.out::println);

// 5) TreeSet reverse order
TreeSet<Integer> t = new TreeSet<>(List.of(1, 2, 3));
for (Integer x : t.descendingSet()) {     // 3, 2, 1
    System.out.println(x);
}
Iterator<Integer> desc = t.descendingIterator();
while (desc.hasNext()) System.out.println(desc.next());
```

#### Gotchas

- **No index access.** There is no `get(i)` / `set(i, e)` on a `Set`. To reach elements you iterate, stream, or (for `TreeSet`) use navigation methods. "Updating" means remove-then-add.
- **Duplicates are silently ignored.** `add` returns `false` instead of throwing; relying on the return value is the only way to know an element was already present.
- **Equality depends on `equals`/`hashCode`.** For `HashSet`/`LinkedHashSet`, custom objects must override **both** consistently, or duplicates will sneak in and `contains` will miss elements. Mutating a field used in `hashCode` after insertion corrupts the set (the element becomes unfindable).
- **`TreeSet` uses `compareTo`/`Comparator`, not `equals`.** Two elements are "duplicates" if the comparator returns `0`. This means an inconsistent comparator can drop elements that `.equals()` says are distinct — keep the comparator consistent with `equals`.
- **`TreeSet` rejects `null`** (throws `NullPointerException` on `add`) and throws `ClassCastException` if elements aren't mutually comparable and no `Comparator` was supplied. `HashSet`/`LinkedHashSet` allow a single `null`.
- **Navigation methods return `null` at the edges.** `floor`/`ceiling`/`higher`/`lower`/`pollFirst`/`pollLast` return `null` when no element qualifies (or the set is empty) — null-check before unboxing to a primitive or you'll `NullPointerException`. By contrast `first()`/`last()` throw `NoSuchElementException` on an empty set.
- **`headSet`/`tailSet`/`subSet` return live views, not copies.** Changes write through to the backing set, and inserting outside the view's range throws `IllegalArgumentException`. Default boundary rule: `from` inclusive, `to` exclusive; `headSet` excludes its arg, `tailSet` includes it.
- **`ConcurrentModificationException`.** Modifying a set structurally (add/remove) during a for-each or stream throws this. Use `Iterator.remove()`, `removeIf(...)`, or collect changes and apply them after the loop.
- **`retainAll`/`removeAll` mutate the receiver**, not the argument. For a non-destructive intersection/difference, copy first: `new HashSet<>(a).retainAll(b)`. Performance tip: for `a.removeAll(b)`, if `b` is a `List`, lookups are O(n) each — pass a `Set` for O(1) behavior.
- **No ordering guarantee for `HashSet`.** Iteration order can change across JVM versions or even after resizing; never depend on it. Use `LinkedHashSet` for insertion order or `TreeSet` for sorted order.
- **`Set.of(...)` is immutable and rejects nulls/duplicates.** Any mutator throws `UnsupportedOperationException`, and `Set.of(a, a)` throws `IllegalArgumentException` at construction.

### Map / HashMap / LinkedHashMap / TreeMap

A `Map<K,V>` stores key→value pairs with unique keys; `HashMap` is the unordered O(1) default, `LinkedHashMap` preserves insertion (or access) order, and `TreeMap` keeps keys sorted (O(log n)) and adds navigation. All are mutable; use `Map.of(...)`/`Collections.unmodifiableMap` for read-only views. None are thread-safe (use `ConcurrentHashMap` for concurrency).

**CRUD**

```java
// CREATE
Map<String, Integer> map = new HashMap<>();                 // unordered
Map<String, Integer> ins  = new LinkedHashMap<>();          // insertion order
Map<String, Integer> sort = new TreeMap<>();                // sorted by key
Map<String, Integer> seed = new HashMap<>(Map.of("a", 1, "b", 2)); // mutable copy of literal
Map<String, Integer> immutable = Map.of("x", 1, "y", 2);    // read-only, throws on modify

// READ
int a = map.getOrDefault("a", 0);   // safe read, no NPE on missing key
boolean has = map.containsKey("a");

// UPDATE
map.put("a", 10);                   // insert or overwrite
map.merge("a", 5, Integer::sum);    // a = 10 + 5 = 15
map.computeIfAbsent("c", k -> 0);   // only writes if absent

// DELETE
map.remove("a");                    // remove by key
map.clear();                        // remove all
```

**Methods table**

| Method | What it does | Example |
|--------|-------------|---------|
| `put(k,v)` | Insert/overwrite; returns previous value or null | `map.put("a",1); // returns null (new key)` |
| `get(k)` | Value for key, or null if absent | `map.get("a"); // 1; map.get("z"); // null` |
| `getOrDefault(k,def)` | Value, or `def` if key absent (no NPE) | `map.getOrDefault("z",0); // 0` |
| `putIfAbsent(k,v)` | Put only if key absent/null; returns existing value | `map.putIfAbsent("a",9); // returns 1, value stays 1` |
| `merge(k,v,fn)` | If absent set v, else `fn(old,v)`; null result removes key | `map.merge("a",5,Integer::sum); // 1+5 = 6` |
| `compute(k,fn)` | Recompute from `(k, oldVal)`; null result removes key | `map.compute("a",(k,v)->v==null?1:v+1); // 7` |
| `computeIfAbsent(k,fn)` | Compute & store only if absent; returns the value | `map.computeIfAbsent("list",k->new ArrayList<>()).add(1);` |
| `computeIfPresent(k,fn)` | Recompute only if present; null removes key | `map.computeIfPresent("a",(k,v)->v*2); // 14` |
| `remove(k)` | Remove by key; returns removed value or null | `map.remove("a"); // 14` |
| `remove(k,v)` | Remove only if key maps to v; returns boolean | `map.remove("b",2); // true if b == 2` |
| `replace(k,v)` | Overwrite only if key present; returns old value | `map.replace("b",99); // no-op if b absent` |
| `replace(k,old,new)` | Overwrite only if currently maps to `old` | `map.replace("b",2,99); // true if b == 2` |
| `containsKey(k)` | True if key present | `map.containsKey("a"); // true` |
| `containsValue(v)` | True if any key maps to v (O(n)) | `map.containsValue(1); // true` |
| `size()` | Number of entries | `map.size(); // 2` |
| `isEmpty()` | True if no entries | `map.isEmpty(); // false` |
| `clear()` | Remove all entries | `map.clear(); // size now 0` |
| `keySet()` | Set view of keys (backed by map) | `for (String k : map.keySet()) {...}` |
| `values()` | Collection view of values (backed by map) | `for (int v : map.values()) {...}` |
| `entrySet()` | Set view of `Map.Entry` (backed by map) | `for (var e : map.entrySet()) {...}` |
| `forEach(action)` | Run `(k,v)->...` for each entry | `map.forEach((k,v)->System.out.println(k+"="+v));` |

**TreeMap navigation** (sorted map only; keys `1,3,5,7`)

| Method | What it does | Example |
|--------|-------------|---------|
| `firstKey()` | Smallest key (throws if empty) | `tm.firstKey(); // 1` |
| `lastKey()` | Largest key (throws if empty) | `tm.lastKey(); // 7` |
| `floorKey(k)` | Greatest key ≤ k, or null | `tm.floorKey(4); // 3` |
| `ceilingKey(k)` | Smallest key ≥ k, or null | `tm.ceilingKey(4); // 5` |
| `lowerKey(k)` | Greatest key strictly < k, or null | `tm.lowerKey(5); // 3` |
| `higherKey(k)` | Smallest key strictly > k, or null | `tm.higherKey(5); // 7` |
| `firstEntry()` | Smallest entry, or null | `tm.firstEntry(); // 1=...` |
| `lastEntry()` | Largest entry, or null | `tm.lastEntry(); // 7=...` |
| `pollFirstEntry()` | Remove & return smallest entry | `tm.pollFirstEntry(); // returns 1=..., removes it` |
| `pollLastEntry()` | Remove & return largest entry | `tm.pollLastEntry(); // returns 7=..., removes it` |
| `headMap(k)` | View of keys < k (exclusive) | `tm.headMap(5); // {1,3}` |
| `headMap(k,true)` | View of keys ≤ k (inclusive) | `tm.headMap(5,true); // {1,3,5}` |
| `tailMap(k)` | View of keys ≥ k (inclusive) | `tm.tailMap(5); // {5,7}` |
| `tailMap(k,false)` | View of keys > k (exclusive) | `tm.tailMap(5,false); // {7}` |
| `subMap(from,to)` | View `[from, to)` (from inclusive, to exclusive) | `tm.subMap(3,7); // {3,5}` |
| `subMap(f,fIncl,t,tIncl)` | View with explicit inclusivity on both ends | `tm.subMap(3,true,7,true); // {3,5,7}` |
| `descendingMap()` | Reverse-ordered view | `tm.descendingMap(); // {7,5,3,1}` |
| `descendingKeySet()` | Reverse-ordered key set | `tm.descendingKeySet(); // [7,5,3,1]` |

**Iteration / loops** (all 4 styles)

```java
Map<String, Integer> map = new LinkedHashMap<>();
map.put("a", 1);
map.put("b", 2);

// 1) entrySet — best when you need BOTH key and value (single lookup)
for (Map.Entry<String, Integer> e : map.entrySet()) {
    System.out.println(e.getKey() + "=" + e.getValue());
    // e.setValue(99); // allowed; mutates the map safely during iteration
}

// 2) keySet — keys only (or key + map.get(k) if you need values)
for (String k : map.keySet()) {
    System.out.println(k);
}

// 3) values — values only, keys not needed
for (int v : map.values()) {
    System.out.println(v);
}

// 4) forEach — lambda, most concise (Java 8+)
map.forEach((k, v) -> System.out.println(k + "=" + v));

// Bonus: iterator with removal during iteration (only safe way to remove)
Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
while (it.hasNext()) {
    Map.Entry<String, Integer> e = it.next();
    if (e.getValue() == 2) it.remove();   // safe; map.remove() here would throw
}

// Sorted iteration over a HashMap (TreeMap iterates sorted automatically)
new TreeMap<>(map).forEach((k, v) -> System.out.println(k + "=" + v));
```

**Gotchas**

- `get(k)` returns `null` for both "missing key" and "key mapped to null" — use `containsKey` or `getOrDefault` to disambiguate. Unboxing a null `Integer`/`Long` into a primitive throws `NullPointerException`.
- `HashMap` permits one null key and null values; `TreeMap` forbids null keys (NPE on `put(null, ...)`); `Map.of(...)` forbids null keys AND values.
- Modifying a map during a for-each loop (other than via the iterator's own `remove`, or `entry.setValue`) throws `ConcurrentModificationException`.
- Custom key classes MUST override both `equals()` and `hashCode()` consistently for `HashMap`/`LinkedHashMap`; mutating a key after insertion so its hashCode changes makes the entry unreachable. `TreeMap` instead uses `compareTo`/`Comparator` — a comparator inconsistent with `equals` can "lose" keys.
- `keySet()`, `values()`, `entrySet()` are live **views**: removing from them removes from the map, but adding is unsupported. `Map.Entry` from these is only valid during iteration.
- `merge`/`compute`/`computeIfPresent` remove the key when the remapping function returns `null` — a subtle way entries disappear.
- `Map.of(...)` and `Collectors.toMap` throw `IllegalStateException` on duplicate keys; `Map.of` is also unordered and immutable.
- `TreeMap` navigation methods come in two flavors: `xxxKey` returns the key (or null), `xxxEntry` returns the entry (or null) — don't unbox a possibly-null result. `firstKey()`/`lastKey()` throw `NoSuchElementException` when empty, but `firstEntry()`/`lastEntry()` return null.
- `containsValue` is O(n) (full scan) while `containsKey` is O(1) for HashMap / O(log n) for TreeMap.
- `LinkedHashMap` can be constructed with `accessOrder=true` to become an LRU structure (override `removeEldestEntry`); default is insertion order.
- Sub-map views (`headMap`/`tailMap`/`subMap`) are backed by the original — writes propagate both ways, and inserting a key outside the view's range throws `IllegalArgumentException`.

### Stack, Queue, Deque (ArrayDeque)

`ArrayDeque` is a resizable-array, double-ended queue (no capacity limit, no thread-safety) that is the modern go-to for **stacks**, **queues**, and **deques**. Use it whenever you need LIFO/FIFO/both; it is faster than `LinkedList` and replaces the legacy `Stack`. **Mutable**, **not** thread-safe, **no null elements**, and iteration order follows the structure (head→tail).

**Why `ArrayDeque` over legacy `Stack`/`Vector`:**
- `Stack` extends `Vector`, so every method is `synchronized` — you pay locking overhead even single-threaded.
- `Stack`/`Vector` expose index-based, list-like access (`get(i)`, `insertElementAt`) that breaks LIFO semantics and invites misuse.
- `Stack` iterates **bottom→top** (insertion order), the opposite of pop order — a classic bug source. `ArrayDeque` as a stack iterates top→bottom (pop order), which is what you expect.
- `ArrayDeque` is backed by a circular array: O(1) amortized at both ends, better cache locality, no per-node allocation like `LinkedList`.

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
```

**CRUD**

```java
// CREATE
Deque<Integer> stack = new ArrayDeque<>();   // use as LIFO stack
Deque<Integer> deque = new ArrayDeque<>();   // double-ended
Queue<Integer> queue = new ArrayDeque<>();   // use as FIFO queue
Deque<Integer> seeded = new ArrayDeque<>(java.util.List.of(1, 2, 3)); // head=1, tail=3

// READ (does not remove)
Integer top  = stack.peek();      // head element, or null if empty
boolean has  = deque.contains(2); // O(n) membership check
int size     = deque.size();

// UPDATE (add / remove — there is no "set at index"; mutate via ends)
stack.push(10);                   // add to head
queue.offer(20);                  // add to tail
Integer popped  = stack.pop();    // remove + return head (throws if empty)
Integer polled  = queue.poll();   // remove + return head (null if empty)

// DELETE
deque.remove(Integer.valueOf(2)); // remove first occurrence by value (O(n))
deque.clear();                    // remove everything
```

**Methods table**

| Method | What it does | Example |
|---|---|---|
| **As STACK (LIFO — operate on head)** | | |
| `push(e)` | Add to head (top); same as `addFirst`. Throws if no space (won't here). | `s.push(1); s.push(2); // top=2` |
| `pop()` | Remove + return head. **Throws** `NoSuchElementException` if empty. | `s.pop() // returns 2, removes it` |
| `peek()` | Return head without removing. Returns `null` if empty. | `s.peek() // returns 2, leaves it` |
| `isEmpty()` | True if no elements. | `s.isEmpty() // false` |
| **As QUEUE (FIFO — add tail, remove head)** | | |
| `offer(e)` | Add to tail. Returns `false` if capacity-bounded (always `true` here). | `q.offer(1); q.offer(2); // head=1` |
| `poll()` | Remove + return head. Returns `null` if empty. | `q.poll() // returns 1` |
| `peek()` | Return head without removing. `null` if empty. | `q.peek() // returns 1, leaves it` |
| `add(e)` | Add to tail. **Throws** `IllegalStateException` if full (vs `offer`). | `q.add(3) // returns true` |
| `remove()` | Remove + return head. **Throws** `NoSuchElementException` if empty (vs `poll`). | `q.remove() // returns 1` |
| `element()` | Peek head. **Throws** `NoSuchElementException` if empty (vs `peek`). | `q.element() // returns 1` |
| **As DEQUE (both ends)** | | |
| `offerFirst(e)` | Add to head, returns `boolean`. | `d.offerFirst(1) // head=1` |
| `offerLast(e)` | Add to tail, returns `boolean`. | `d.offerLast(9) // tail=9` |
| `pollFirst()` | Remove + return head, `null` if empty. | `d.pollFirst() // returns head` |
| `pollLast()` | Remove + return tail, `null` if empty. | `d.pollLast() // returns tail` |
| `peekFirst()` | Return head, no remove, `null` if empty. | `d.peekFirst() // returns head` |
| `peekLast()` | Return tail, no remove, `null` if empty. | `d.peekLast() // returns tail` |
| `addFirst(e)` | Add to head. **Throws** if full (vs `offerFirst`). | `d.addFirst(0)` |
| `addLast(e)` | Add to tail. **Throws** if full (vs `offerLast`). | `d.addLast(99)` |
| `removeFirst()` | Remove + return head. **Throws** if empty (vs `pollFirst`). | `d.removeFirst()` |
| `removeLast()` | Remove + return tail. **Throws** if empty (vs `pollLast`). | `d.removeLast()` |
| `getFirst()` | Peek head. **Throws** if empty (vs `peekFirst`). | `d.getFirst()` |
| `getLast()` | Peek tail. **Throws** if empty (vs `peekLast`). | `d.getLast()` |
| **Common** | | |
| `size()` | Number of elements. | `d.size() // 3` |
| `isEmpty()` | True if empty. | `d.isEmpty()` |
| `contains(o)` | O(n) membership test. | `d.contains(9) // true` |
| `clear()` | Remove all elements. | `d.clear()` |

**Throwing vs null/false-returning pairs** — pick based on whether "empty/full" is exceptional (throw) or expected control flow (null):

| Operation | Throws on failure | Returns null/false |
|---|---|---|
| Insert tail | `add(e)` | `offer(e)` |
| Remove head | `remove()` | `poll()` |
| Examine head | `element()` | `peek()` |
| Insert head | `addFirst(e)` | `offerFirst(e)` |
| Insert tail | `addLast(e)` | `offerLast(e)` |
| Remove head | `removeFirst()` | `pollFirst()` |
| Remove tail | `removeLast()` | `pollLast()` |
| Examine head | `getFirst()` | `peekFirst()` |
| Examine tail | `getLast()` | `peekLast()` |

**Iteration / loops**

Iteration **does not remove** elements. For an `ArrayDeque` used as a stack, `iterator()` goes **head→tail = top→bottom (pop order)**; `descendingIterator()` reverses it. (Legacy `Stack` does the opposite — another reason to switch.)

```java
Deque<Integer> d = new ArrayDeque<>(java.util.List.of(1, 2, 3)); // head=1 ... tail=3

// 1) Enhanced for-each (head -> tail)
for (int x : d) {
    System.out.println(x); // 1, 2, 3
}

// 2) Explicit Iterator (head -> tail)
java.util.Iterator<Integer> it = d.iterator();
while (it.hasNext()) {
    int x = it.next();
    // it.remove(); // optional: safe in-loop removal
}

// 3) Reverse order (tail -> head)
java.util.Iterator<Integer> dit = d.descendingIterator();
while (dit.hasNext()) {
    System.out.println(dit.next()); // 3, 2, 1
}

// 4) forEach + lambda (head -> tail)
d.forEach(x -> System.out.println(x));

// 5) Stream (head -> tail), e.g. for transforms/collecting
d.stream().filter(x -> x % 2 == 1).forEach(System.out::println);

// 6) DRAINING loop — consume and empty it (common in BFS/stack algorithms)
while (!d.isEmpty()) {
    int x = d.poll();   // or pop() for LIFO; head each time
    System.out.println(x);
}
```

**BFS skeleton (queue-driven)**

```java
// Graph BFS from a start node; adj = Map<Integer, List<Integer>>
Queue<Integer> queue = new ArrayDeque<>();
Set<Integer> visited = new HashSet<>();

queue.offer(start);
visited.add(start);                 // mark on ENQUEUE, not on dequeue

while (!queue.isEmpty()) {
    int node = queue.poll();        // FIFO -> level-by-level
    // process(node);

    for (int next : adj.getOrDefault(node, List.of())) {
        if (visited.add(next)) {    // add() returns false if already present
            queue.offer(next);
        }
    }
}
```

For DFS, swap to `Deque<Integer> stack` and use `push`/`pop` (or recursion).

**Gotchas**

- **No nulls allowed.** `ArrayDeque` rejects `null` (`NullPointerException` on insert). This is deliberate so a `null` from `poll()`/`peek()` unambiguously means "empty." `LinkedList` *does* allow null, making emptiness ambiguous — prefer `ArrayDeque`.
- **`push`/`pop` operate on the HEAD, `offer`/`poll` also on the HEAD; `offer`/`add` append to the TAIL.** Mixing stack and queue methods on one instance is confusing — pick one role per object. (`push` = `addFirst`, `pop` = `removeFirst`, `poll` = `removeFirst`, `offer` = `addLast`.)
- **Throwing vs null pair confusion:** `pop`/`remove`/`element` throw on empty; `poll`/`peek` return null. In hot loops always guard with `isEmpty()` or use the null-returning variants.
- **Iteration order ≠ legacy `Stack`.** `ArrayDeque` stack iterates top→bottom; `java.util.Stack` iterates bottom→top. Don't assume when porting code.
- **Not thread-safe.** For concurrency use `ConcurrentLinkedDeque` / `ConcurrentLinkedQueue`, or `LinkedBlockingDeque`/`ArrayBlockingQueue` when you need blocking.
- **Fail-fast iterators.** Structurally modifying the deque during iteration (except via the iterator's own `remove()`) throws `ConcurrentModificationException`.
- **`remove(Object)` is O(n)** and removes only the **first** occurrence (scanning head→tail); `removeFirstOccurrence`/`removeLastOccurrence` make the direction explicit.
- **`contains` and value-based `remove` are O(n)** — `ArrayDeque` is not a set or a lookup structure; use it only for end operations.
- **No index access.** Unlike `Stack`/`Vector` there is no `get(i)`; if you need random access you picked the wrong structure (use `ArrayList`).
- **`peek()` returning `null` is ambiguous only if you allowed nulls** — since `ArrayDeque` forbids them, `null` reliably signals empty.
- **Initial capacity is rounded up to a power of two** (default 16); sizing hints via `new ArrayDeque<>(numElements)` avoid resizes but aren't a hard cap.

### PriorityQueue (heap)

A `PriorityQueue<E>` is a queue backed by a binary **min-heap** (default): `poll`/`peek` always return the *smallest* element by natural ordering or a supplied `Comparator`. Use it for top-K, scheduling, Dijkstra, merge-K-lists, or any "always grab the best next item" problem. It is **mutable**, **not thread-safe** (use `PriorityBlockingQueue` for concurrency), allows duplicates, and forbids `null`. Ordering applies only to head access — **iteration order is undefined**.

```java
import java.util.PriorityQueue;
import java.util.Comparator;
```

#### CRUD

```java
// CREATE — default min-heap (natural ordering)
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(5); pq.offer(1); pq.offer(3);          // heap head = 1

// CREATE — max-heap via Comparator
PriorityQueue<Integer> maxpq = new PriorityQueue<>(Comparator.reverseOrder());
maxpq.offer(5); maxpq.offer(1); maxpq.offer(3); // heap head = 5

// READ — inspect head without removing
int head = pq.peek();                            // 1 (null if empty)

// UPDATE — there is no "update in place". Remove the old value, add the new.
pq.remove(3);                                    // delete by value
pq.offer(2);                                     // insert new value

// DELETE — remove and return the head (the min)
int min = pq.poll();                             // 1, then head becomes 2
```

#### Building min-heap vs max-heap vs custom

```java
// MIN-HEAP (default): natural ordering, smallest at head
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// MAX-HEAP: reverse the comparator, largest at head
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
// equivalent: new PriorityQueue<>((a, b) -> b - a);  // avoid b-a on large ints (overflow); prefer Integer.compare(b, a)

// CUSTOM by an int[] field (e.g. interval = {start, end}); order by end ascending
PriorityQueue<int[]> byEnd = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
byEnd.offer(new int[]{1, 9});
byEnd.offer(new int[]{2, 3});
int[] first = byEnd.peek();                      // {2, 3} — smallest end

// CUSTOM with tie-breaker: primary by field 0 asc, then field 1 desc
PriorityQueue<int[]> tie = new PriorityQueue<>(
    Comparator.<int[]>comparingInt(x -> x[0]).thenComparing(x -> x[1], Comparator.reverseOrder()));

// WITH INITIAL CAPACITY (avoids resizes when you know the size)
PriorityQueue<Integer> sized = new PriorityQueue<>(100);
PriorityQueue<Integer> sizedCmp = new PriorityQueue<>(100, Comparator.reverseOrder());
```

#### Heapify from a collection

```java
import java.util.List;
import java.util.Arrays;

List<Integer> data = Arrays.asList(5, 1, 3, 2, 4);

// Construct directly from a Collection — O(n) heapify, uses NATURAL ordering.
PriorityQueue<Integer> fromColl = new PriorityQueue<>(data);   // head = 1

// To heapify with a custom order, you CANNOT pass a Collection + Comparator together
// (no such constructor). Create with the comparator, then addAll:
PriorityQueue<Integer> fromCollMax = new PriorityQueue<>(Comparator.reverseOrder());
fromCollMax.addAll(data);                                      // head = 5
// Note: addAll is n × O(log n), not the O(n) bulk-heapify the Collection ctor gives.
```

#### Methods table

| Method | What it does | Example |
|--------|--------------|---------|
| `new PriorityQueue<>()` | Default min-heap, natural ordering, initial capacity 11 | `PriorityQueue<Integer> q = new PriorityQueue<>();` |
| `new PriorityQueue<>(cmp)` | Heap ordered by `Comparator` (max-heap / custom) | `new PriorityQueue<>(Comparator.reverseOrder()); // max-heap` |
| `new PriorityQueue<>(int)` | Pre-sized min-heap to avoid resizes | `new PriorityQueue<>(64);` |
| `new PriorityQueue<>(int, cmp)` | Pre-sized with comparator | `new PriorityQueue<>(64, Comparator.reverseOrder());` |
| `new PriorityQueue<>(coll)` | O(n) heapify from a collection (natural order) | `new PriorityQueue<>(List.of(5,1,3)); // head=1` |
| `offer(e)` | Inserts; returns `boolean` (always true for unbounded) | `q.offer(5); // true` |
| `add(e)` | Same as `offer`; returns `boolean` (throws on capacity-restricted full, n/a here) | `q.add(3); // true` |
| `poll()` | Removes and returns head (min); `null` if empty | `q.poll(); // 1, removes it` |
| `peek()` | Returns head (min) without removing; `null` if empty | `q.peek(); // 1, stays` |
| `remove()` | Removes and returns head; **throws** `NoSuchElementException` if empty | `q.remove(); // 1` |
| `remove(Object o)` | Removes ONE occurrence equal to `o`; returns `boolean`; O(n) scan | `q.remove(3); // true if present` |
| `element()` | Returns head without removing; **throws** if empty | `q.element(); // 1` |
| `size()` | Number of elements | `q.size(); // 3` |
| `isEmpty()` | `true` if no elements | `q.isEmpty(); // false` |
| `contains(o)` | `true` if an equal element exists; O(n) scan | `q.contains(3); // true` |
| `clear()` | Removes all elements | `q.clear();` |
| `toArray()` | Snapshot as `Object[]` (heap order, NOT sorted) | `Object[] a = q.toArray();` |
| `iterator()` | Iterator in **unspecified** (heap) order | `Iterator<Integer> it = q.iterator();` |

#### Iteration / loops

WARNING: none of these visit elements in sorted order. The heap is only partially ordered; only repeated `poll()` yields ascending (or comparator) order — and it *empties* the queue.

```java
PriorityQueue<Integer> q = new PriorityQueue<>(List.of(5, 1, 3, 2, 4));

// 1) for-each — HEAP order, e.g. [1, 2, 3, 5, 4]. NOT sorted.
for (int x : q) {
    System.out.println(x);
}

// 2) Iterator — same unspecified order; supports it.remove()
Iterator<Integer> it = q.iterator();
while (it.hasNext()) {
    int x = it.next();
    if (x == 3) it.remove();   // safe structural removal during iteration
}

// 3) forEach (Java 8+) — HEAP order
q.forEach(System.out::println);

// 4) Stream — HEAP order; sort explicitly if you need it
q.stream().sorted().forEach(System.out::println);   // forces ascending

// 5) DRAIN in priority order — the ONLY way to get sorted output.
//    Destroys the queue. Copy first if you must keep it.
PriorityQueue<Integer> copy = new PriorityQueue<>(q);  // preserve original
while (!copy.isEmpty()) {
    System.out.println(copy.poll());   // 1, 2, 3, 4, 5 — sorted
}

// 6) toArray + Arrays.sort — sorted snapshot without draining
Integer[] arr = q.toArray(new Integer[0]);
Arrays.sort(arr);   // now sorted; q is untouched
```

#### Top-K and merge skeletons

```java
// TOP-K LARGEST: keep a MIN-heap of size k. Head is the k-th largest (the bar to beat).
// Evict the smallest whenever size exceeds k. O(n log k) time, O(k) space.
public static int[] topKLargest(int[] nums, int k) {
    PriorityQueue<Integer> min = new PriorityQueue<>();   // min-heap
    for (int n : nums) {
        min.offer(n);
        if (min.size() > k) min.poll();   // drop the smallest so far
    }
    int[] res = new int[k];
    for (int i = 0; i < k; i++) res[i] = min.poll();   // ascending; reverse for descending
    return res;
}

// TOP-K FREQUENT: min-heap keyed by count, capped at k.
public static List<Integer> topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int n : nums) freq.merge(n, 1, Integer::sum);
    PriorityQueue<int[]> heap =                       // int[]{value, count}
        new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));  // min by count
    for (var e : freq.entrySet()) {
        heap.offer(new int[]{e.getKey(), e.getValue()});
        if (heap.size() > k) heap.poll();
    }
    List<Integer> res = new ArrayList<>();
    while (!heap.isEmpty()) res.add(heap.poll()[0]);
    return res;
}

// MERGE K SORTED LISTS: heap holds the current front of each list. O(N log k).
// Node has int val and Node next.
public static Node mergeKLists(List<Node> lists) {
    PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.val, b.val));
    for (Node head : lists) if (head != null) pq.offer(head);   // seed with each list head
    Node dummy = new Node(0), tail = dummy;
    while (!pq.isEmpty()) {
        Node smallest = pq.poll();
        tail.next = smallest;
        tail = smallest;
        if (smallest.next != null) pq.offer(smallest.next);     // pull next from that list
    }
    return dummy.next;
}
```

#### Gotchas

- **Iteration is NOT sorted.** `for`, `forEach`, `stream()`, `toArray()` all give heap (partial) order. Only repeated `poll()` is ordered — and it empties the queue.
- **`poll()`/`peek()` return `null` when empty; `remove()`/`element()` throw** `NoSuchElementException`. Pick the pair that matches your error-handling style.
- **No `null` elements** — `offer(null)` throws `NullPointerException`.
- **`remove(Object)` and `contains(Object)` are O(n)** linear scans, not O(log n). For frequent arbitrary removals consider a `TreeSet` or a lazy-deletion pattern (mark removed, skip on poll).
- **No bulk-heapify with a custom comparator.** The `(Collection)` constructor uses natural ordering. To get O(n) heapify you'd need natural order; with a comparator you must `addAll` (n log n).
- **`b - a` for a max-heap can overflow** for large/negative ints. Use `Integer.compare(b, a)` or `Comparator.reverseOrder()`.
- **Comparator must be consistent/total.** Returning a wrong sign or a non-total order corrupts the heap and produces silently wrong head elements.
- **Mutating an element's key after insertion breaks the heap** — the queue does not re-heapify. Remove, mutate, then re-insert.
- **Capacity ≠ size.** The `int` constructor sets internal array capacity (must be ≥ 1, else `IllegalArgumentException`), not a bound; the queue grows automatically and is effectively unbounded.
- **Not thread-safe.** Use `java.util.concurrent.PriorityBlockingQueue` for concurrent producers/consumers.
- **`add` vs `offer`:** functionally identical here (both return `boolean`); the distinction only matters for capacity-restricted queues, which `PriorityQueue` is not.

### Utility classes: Collections, Character, Math, Integer

Static-method helper classes (`java.util.Collections`, `java.lang.Character`, `java.lang.Math`, `java.lang.Integer`/`Long`). You never instantiate them — you call their `static` methods directly. `Collections` operates on mutable `List`s (in-place, so element order matters and the backing list must support the operation); the others are stateless functions on primitives. Reach for these in coding rounds to avoid hand-rolling sorts, parsing, bit tricks, and char classification.

---

#### Collections

In-place algorithms over `List` (and a few factory/wrapper methods). Most mutators need a modifiable, fixed-or-growable list — `Arrays.asList(...)` is fixed-size (sort/swap/fill OK, add/remove not), and `List.of(...)` / `unmodifiableList` throw on any mutation.

**CRUD**
```java
// CREATE — a mutable list to operate on
List<Integer> nums = new ArrayList<>(List.of(5, 3, 1, 4, 2));

// READ — derive values without mutating
int hi = Collections.max(nums);          // 5
int lo = Collections.min(nums);          // 1
int f  = Collections.frequency(nums, 3); // 1

// UPDATE — mutate in place
Collections.sort(nums);                  // [1, 2, 3, 4, 5]
Collections.reverse(nums);               // [5, 4, 3, 2, 1]
Collections.swap(nums, 0, 4);            // [1, 4, 3, 2, 5]
Collections.fill(nums, 0);              // [0, 0, 0, 0, 0]

// DELETE — Collections has no remove; use the List API
nums.clear();                            // []
```

**Methods table**

| Method | What it does | Example |
|---|---|---|
| `sort(list)` | Sorts ascending by natural order (mutates). | `List<Integer> l=new ArrayList<>(List.of(3,1,2)); Collections.sort(l); // [1, 2, 3]` |
| `sort(list, cmp)` | Sorts by a `Comparator` (mutates). | `Collections.sort(l, Comparator.reverseOrder()); // [3, 2, 1]` |
| `reverse(list)` | Reverses element order (mutates). | `List<Integer> l=new ArrayList<>(List.of(1,2,3)); Collections.reverse(l); // [3, 2, 1]` |
| `max(coll)` | Largest by natural order (or `max(coll, cmp)`). | `Collections.max(List.of(4,9,2)); // 9` |
| `min(coll)` | Smallest by natural order (or `min(coll, cmp)`). | `Collections.min(List.of(4,9,2)); // 2` |
| `frequency(coll, o)` | Count of elements equal to `o`. | `Collections.frequency(List.of(1,2,2,3,2), 2); // 3` |
| `swap(list, i, j)` | Swaps elements at indices `i` and `j` (mutates). | `List<Integer> l=new ArrayList<>(List.of(1,2,3)); Collections.swap(l,0,2); // [3, 2, 1]` |
| `fill(list, val)` | Replaces every element with `val` (mutates, keeps size). | `List<Integer> l=new ArrayList<>(List.of(1,2,3)); Collections.fill(l,7); // [7, 7, 7]` |
| `nCopies(n, val)` | Immutable list of `n` copies of `val`. | `Collections.nCopies(3, "x"); // [x, x, x]` |
| `unmodifiableList(list)` | Read-only view; writes throw `UnsupportedOperationException`. | `List<Integer> v=Collections.unmodifiableList(l); // v.add(9) -> throws` |
| `emptyList()` | Shared immutable empty list. | `List<String> e=Collections.emptyList(); // []` |

**Iteration / loops**
```java
List<Integer> nums = new ArrayList<>(List.of(10, 20, 30));

// 1. Indexed for — gives access to the index (needed for swap/in-place edits)
for (int i = 0; i < nums.size(); i++) System.out.println(i + ":" + nums.get(i));

// 2. Enhanced for-each — cleanest read-only traversal
for (int n : nums) System.out.println(n);

// 3. Iterator — only safe way to remove during iteration
Iterator<Integer> it = nums.iterator();
while (it.hasNext()) { if (it.next() == 20) it.remove(); } // [10, 30]

// 4. forEach + lambda
nums.forEach(n -> System.out.println(n));

// 5. Stream — for map/filter/collect pipelines
nums.stream().filter(n -> n > 10).forEach(System.out::println);
```

**Gotchas**
- `sort`, `reverse`, `swap`, `fill` mutate the list in place and return `void` — don't write `nums = Collections.sort(nums)`.
- They throw `UnsupportedOperationException` on immutable lists (`List.of(...)`, `Collections.unmodifiableList(...)`, `nCopies`) and `add`/`remove` throw on fixed-size `Arrays.asList(...)`.
- `max`/`min` throw `NoSuchElementException` on an empty collection.
- `nCopies` returns an immutable, memory-efficient list that shares one element reference — wrap in `new ArrayList<>(...)` if you need to mutate.
- `unmodifiableList` is a *view*, not a copy: mutating the original still shows through. Copy first if you need a true snapshot.
- `frequency` uses `.equals()`, so for objects make sure `equals`/`hashCode` are defined correctly.

---

#### Character

Static classification/conversion methods on a single `char` (or its `int` code point). Pure functions — no state, nothing to mutate. The workhorse for parsing strings char-by-char in coding problems.

**CRUD**
```java
// CREATE — there's nothing to construct; you operate on a char
char c = 'A';

// READ — classify / convert
boolean d = Character.isDigit(c);     // false
char low  = Character.toLowerCase(c); // 'a'
int  val  = Character.getNumericValue('7'); // 7

// UPDATE / DELETE — N/A: Character methods return new values, never mutate
```

**Methods table**

| Method | What it does | Example |
|---|---|---|
| `isDigit(c)` | True if `c` is 0-9 (and other Unicode digits). | `Character.isDigit('5'); // true` |
| `isLetter(c)` | True if `c` is an alphabetic letter. | `Character.isLetter('a'); // true` |
| `isLetterOrDigit(c)` | True if letter or digit (handy for alphanumeric checks). | `Character.isLetterOrDigit('#'); // false` |
| `isUpperCase(c)` | True if `c` is uppercase. | `Character.isUpperCase('A'); // true` |
| `isLowerCase(c)` | True if `c` is lowercase. | `Character.isLowerCase('a'); // true` |
| `isWhitespace(c)` | True for space, tab, newline, etc. | `Character.isWhitespace(' '); // true` |
| `toLowerCase(c)` | Returns lowercase form (unchanged if not a letter). | `Character.toLowerCase('Z'); // 'z'` |
| `toUpperCase(c)` | Returns uppercase form. | `Character.toUpperCase('z'); // 'Z'` |
| `getNumericValue(c)` | Numeric value of a digit/letter char. | `Character.getNumericValue('9'); // 9` |

**Iteration / loops** (Character methods shine when scanning a String)
```java
String s = "a1 B!";

// 1. Indexed loop over chars
for (int i = 0; i < s.length(); i++) {
    char c = s.charAt(i);
    if (Character.isLetterOrDigit(c)) System.out.println(c);
}

// 2. for-each over toCharArray()
for (char c : s.toCharArray()) {
    if (Character.isUpperCase(c)) System.out.println(c); // B
}

// 3. IntStream over code points — stream-style
s.chars().filter(Character::isDigit)
         .forEach(c -> System.out.println((char) c)); // 1

// 4. Building a result (e.g., keep only digits)
StringBuilder sb = new StringBuilder();
for (char c : s.toCharArray())
    if (Character.isDigit(c)) sb.append(c); // "1"
```

**Gotchas**
- `s.chars()` yields `int` code points, not `char` — cast back to `(char)` to print/append as a character.
- `getNumericValue` returns the *digit value*, so `getNumericValue('A')` is `10` (hex-style for letters), and it returns `-1` for non-alphanumerics and `-2` for fractional Unicode — don't assume it only handles `'0'`-`'9'`. For pure ASCII digits, `c - '0'` is faster and unambiguous.
- `toUpperCase`/`toLowerCase` on a non-letter return the char unchanged (no error).
- These methods are Unicode-aware: `isLetter` is true for many non-Latin scripts and `isDigit` for non-ASCII digits — fine for interviews, but be deliberate if input is restricted to ASCII.
- `isWhitespace` (Java-specific) differs slightly from `isSpaceChar` (Unicode) — for `\n`, `isWhitespace` is true but `isSpaceChar` is false.

---

#### Math

Static numeric functions on primitives. Note return types: `pow`/`sqrt`/`ceil`/`floor` return `double`; `round(double)` returns `long`; `round(float)` returns `int`. `abs`/`max`/`min` are overloaded and return the type you pass in.

**CRUD**
```java
// CREATE — operate on primitives directly
int a = -7, b = 3;

// READ — compute results (nothing is stored or mutated)
int    m  = Math.max(a, b);   // 3
int    ab = Math.abs(a);      // 7
double p  = Math.pow(2, 10);  // 1024.0
long   r  = Math.round(2.5);  // 3

// UPDATE / DELETE — N/A: Math is pure functions
```

**Methods table**

| Method | What it does | Example |
|---|---|---|
| `max(a, b)` | Larger of two values (overloaded for int/long/double/float). | `Math.max(3, 9); // 9` |
| `min(a, b)` | Smaller of two values. | `Math.min(3, 9); // 3` |
| `abs(x)` | Absolute value. | `Math.abs(-7); // 7` |
| `pow(b, e)` | `b` raised to `e`, returns `double`. | `Math.pow(2, 10); // 1024.0` |
| `sqrt(x)` | Square root, returns `double`. | `Math.sqrt(144); // 12.0` |
| `ceil(x)` | Smallest `double` >= `x` (rounds up). | `Math.ceil(2.1); // 3.0` |
| `floor(x)` | Largest `double` <= `x` (rounds down). | `Math.floor(2.9); // 2.0` |
| `round(x)` | Nearest integer, `.5` rounds up; `long` for double, `int` for float. | `Math.round(2.5); // 3` |

**Iteration / loops** (Math is per-value; loop over data and apply it)
```java
int[] arr = {-4, 7, -1, 9, -6};

// 1. Running max/min via Math in a loop
int max = Integer.MIN_VALUE;
for (int x : arr) max = Math.max(max, x); // 9

// 2. Sum of absolute values
int sumAbs = 0;
for (int x : arr) sumAbs += Math.abs(x);  // 27

// 3. Stream reduction with Math
int streamMax = Arrays.stream(arr).reduce(Integer.MIN_VALUE, Math::max); // 9

// 4. Apply Math.sqrt element-wise
double[] roots = Arrays.stream(arr).mapToDouble(x -> Math.sqrt(Math.abs(x))).toArray();
```

**Gotchas**
- `Math.abs(Integer.MIN_VALUE)` returns `Integer.MIN_VALUE` (still negative!) because `2^31` overflows `int` — a classic overflow trap.
- `pow` and `sqrt` go through `double`, so large/odd values can have floating-point error: `(int) Math.pow(10, 2)` can surprise you — for integer exponentiation prefer a loop or `BigInteger`.
- `round` for negatives rounds toward positive infinity at `.5`: `Math.round(-2.5)` is `-2`, not `-3`.
- `ceil`/`floor` return `double`, so cast to `int`/`long` if you need an integer index.
- Integer division happens *before* `Math` sees it: `Math.ceil(5/2)` is `2.0`, not `3.0` — write `Math.ceil(5.0/2)` or use `(a + b - 1) / b` for integer ceiling.
- `max`/`min`/`abs` with mixed types may auto-promote (e.g., int + long → long); watch the inferred return type.

---

#### Integer / Long

Boxed wrappers plus a bag of static utilities: parsing, base conversion, bit counting, and constants. `Long` mirrors every method for 64-bit values.

**CRUD**
```java
// CREATE / parse
int n   = Integer.parseInt("42");        // 42
int hex = Integer.parseInt("ff", 16);    // 255 (radix overload)
long L  = Long.parseLong("9999999999");  // 9999999999

// READ — convert / inspect
String s   = Integer.toString(255);       // "255"
String bin = Integer.toBinaryString(5);   // "101"
int bits   = Integer.bitCount(7);         // 3

// UPDATE / DELETE — N/A: values are immutable; reassign instead
```

**Methods table**

| Method | What it does | Example |
|---|---|---|
| `parseInt(s)` | Parses a decimal string to `int` (throws on bad input). | `Integer.parseInt("123"); // 123` |
| `parseInt(s, radix)` | Parses in a given base. | `Integer.parseInt("1010", 2); // 10` |
| `toString(i)` | Int to its decimal string. | `Integer.toString(99); // "99"` |
| `toBinaryString(i)` | Base-2 string (no leading zeros). | `Integer.toBinaryString(10); // "1010"` |
| `bitCount(i)` | Number of 1-bits (population count). | `Integer.bitCount(11); // 3 (1011)` |
| `MAX_VALUE` | Largest `int`, `2^31 - 1`. | `Integer.MAX_VALUE; // 2147483647` |
| `MIN_VALUE` | Smallest `int`, `-2^31`. | `Integer.MIN_VALUE; // -2147483648` |
| `compare(a, b)` | Overflow-safe compare: <0, 0, or >0. | `Integer.compare(5, 9); // -1` |

(`Long` equivalents: `Long.parseLong`, `Long.toBinaryString`, `Long.bitCount`, `Long.MAX_VALUE` = `9223372036854775807`, `Long.MIN_VALUE`, `Long.compare`.)

**Iteration / loops** (these methods feed loops over digits/bits)
```java
// 1. Iterate over set bits using bitCount-friendly logic
int x = 13; // 1101
for (int i = 0; i < Integer.SIZE; i++)
    if ((x & (1 << i)) != 0) System.out.print(i + " "); // 0 2 3

// 2. Parse a list of numeric strings
String[] tokens = {"1", "22", "333"};
int sum = 0;
for (String t : tokens) sum += Integer.parseInt(t); // 356

// 3. Walk a binary string char-by-char
for (char bit : Integer.toBinaryString(10).toCharArray())
    System.out.print(bit); // 1010

// 4. Stream parse + reduce
int total = Arrays.stream(tokens).mapToInt(Integer::parseInt).sum(); // 356
```

**Gotchas**
- `parseInt` throws `NumberFormatException` on null, empty, whitespace-padded (`" 5 "`), decimals (`"4.0"`), or out-of-range strings — trim/validate first.
- A value larger than `MAX_VALUE` silently overflows if you do arithmetic in `int`; use `long` or `Math.addExact` to detect it.
- Use `Integer.compare(a, b)` instead of `a - b` in comparators — subtraction overflows for large/extreme values and breaks the ordering.
- `==` on boxed `Integer` compares references, not values; it appears to work for `-128..127` (the cache) but fails above that — always use `.equals()` or unbox to `int`.
- `toBinaryString`/`toHexString` treat the int as unsigned, so `toBinaryString(-1)` is 32 ones (`"11111111111111111111111111111111"`), not `"-1"`.
- `bitCount` counts bits in the two's-complement representation, so negatives have many set bits.
- Autoboxing in tight loops (e.g., `Integer` keys) creates garbage — prefer primitive `int` where performance matters.

---

<a id="trees-algos"></a>

## 🌳 Trees & Algorithms — Reference

[🔝 Back to contents](#ref-top)

> Tree terminology & taxonomy · heap/array representation · traversals · algorithm paradigms · sorting · searching/graph/string — with ASCII visuals + Java code.

<a id="ref-java-features"></a>

## ☕ Java Features (8 → Latest) — Interview Edition

[🔝 Back to contents](#ref-top)

> What changed each version, *why* it matters, with code + before→after visuals. ⭐ = commonly asked.

### Java 8 (2014) — the big one

Java 8 is the version interviewers obsess over because it dragged Java into the functional-programming world. Below, each feature gets: **what + why**, a **tiny runnable example**, and a **visual / BEFORE→AFTER**.

---

## 1. Lambdas

**What:** A lambda is an anonymous function — a block of behavior you can pass around like data. Syntax: `(args) -> body`.

**Why added:** Before Java 8, to pass "a piece of behavior" you needed a bulky anonymous inner class. Lambdas remove that ceremony, enabling functional-style code and the Streams API.

```java
import java.util.*;

public class LambdaDemo {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob"));

        // Sort by length using a lambda
        names.sort((a, b) -> a.length() - b.length());

        names.forEach(n -> System.out.println(n));
    }
}
```

**BEFORE → AFTER**

```
BEFORE (Java 7): anonymous inner class
---------------------------------------------------
names.sort(new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.length() - b.length();
    }
});

AFTER (Java 8): lambda
---------------------------------------------------
names.sort((a, b) -> a.length() - b.length());
```

**Visual — what a lambda actually is:**

```
   (a, b)        ->        a.length() - b.length()
   ~~~~~~                  ~~~~~~~~~~~~~~~~~~~~~~~~~
   parameters    arrow     body (the behavior)

   The compiler maps this onto a Comparator<String>,
   because Comparator has ONE abstract method: compare(a,b).
```

---

## 2. Functional interfaces (`Function`, `Predicate`, `Consumer`, `Supplier`, `@FunctionalInterface`)

**What:** A functional interface is an interface with **exactly one abstract method (SAM)**. A lambda's "type" is always some functional interface. `@FunctionalInterface` is an optional annotation that makes the compiler *enforce* the single-method rule.

**Why added:** Lambdas need a target type. Java provides ~40 ready-made functional interfaces in `java.util.function` so you rarely write your own.

**The 4 you MUST know:**

```
Interface     Abstract method      Takes   Returns   Mnemonic
-----------------------------------------------------------------------
Function<T,R>  R apply(T t)          1 in    1 out    "transform T into R"
Predicate<T>   boolean test(T t)     1 in    boolean  "test / yes-no"
Consumer<T>    void accept(T t)      1 in    nothing  "consume / use it"
Supplier<T>    T get()               0 in    1 out    "supply / factory"
```

Visual of data flow:

```
Function<T,R> :   T  --[ apply ]-->  R
Predicate<T>  :   T  --[ test  ]-->  boolean
Consumer<T>   :   T  --[ accept]-->  (void, side effect)
Supplier<T>   :  ( ) --[ get   ]-->  T
```

```java
import java.util.function.*;

public class FunctionalDemo {
    public static void main(String[] args) {
        Function<Integer, Integer> doubler = x -> x * 2;        // T -> R
        Predicate<Integer> isEven        = x -> x % 2 == 0;      // T -> boolean
        Consumer<String>   printer       = s -> System.out.println("Got: " + s);
        Supplier<String>   greeter       = () -> "Hello!";       // () -> T

        System.out.println(doubler.apply(10));   // 20
        System.out.println(isEven.test(7));      // false
        printer.accept("data");                  // Got: data
        System.out.println(greeter.get());       // Hello!
    }
}
```

**`@FunctionalInterface` — what it enforces:**

```java
@FunctionalInterface
interface Calculator {
    int operate(int a, int b);   // exactly ONE abstract method - OK

    // int extra(int x);         // <-- uncomment => COMPILE ERROR
                                 //     "not a functional interface"
}
```

```
@FunctionalInterface  =  a guardrail.
  0 abstract methods -> compile error
  1 abstract method  -> OK   (lambdas allowed)
  2 abstract methods -> compile error
(default/static methods don't count — only ABSTRACT ones do)
```

---

## 3. Streams API

**What:** A `Stream` is a pipeline that carries elements from a **source** through zero-or-more **intermediate operations** (lazy, return a new Stream) to a single **terminal operation** (eager, produces a result and ends the pipeline).

**Why added:** To express *what* you want done to a collection (filter, map, reduce) declaratively, instead of writing manual `for` loops with mutable accumulators. Bonus: trivial parallelism via `.parallelStream()`.

**Pipeline diagram:**

```
  SOURCE            INTERMEDIATE OPS (lazy)              TERMINAL OP (eager)
  ===============   ==================================   ====================
  list.stream()  →  .filter(...)  →  .map(...)  →  .sorted()  →  .collect(...)
       |                |               |              |              |
   produces        keep some       transform      reorder       PULLS data
   elements        elements         each one                   through & ends

  Nothing runs until the TERMINAL op is called.  ← key interview point
```

```java
import java.util.*;
import java.util.stream.*;

public class StreamDemo {
    public static void main(String[] args) {
        List<String> names = List.of("Alice", "Bob", "Charlie", "Dave", "Anna");

        List<String> result = names.stream()              // SOURCE
            .filter(n -> n.startsWith("A"))               // intermediate (lazy)
            .map(String::toUpperCase)                     // intermediate (lazy)
            .sorted()                                     // intermediate (lazy)
            .collect(Collectors.toList());                // TERMINAL (eager)

        System.out.println(result);   // [ALICE, ANNA]
    }
}
```

**BEFORE → AFTER**

```
BEFORE (Java 7): manual loop, mutable list
---------------------------------------------------
List<String> result = new ArrayList<>();
for (String n : names) {
    if (n.startsWith("A")) {
        result.add(n.toUpperCase());
    }
}
Collections.sort(result);

AFTER (Java 8): declarative pipeline
---------------------------------------------------
List<String> result = names.stream()
    .filter(n -> n.startsWith("A"))
    .map(String::toUpperCase)
    .sorted()
    .collect(Collectors.toList());
```

**Lazy evaluation visual (why order/short-circuit matters):**

```
Each element flows through the WHOLE chain before the next one starts,
NOT layer-by-layer:

  "Alice"  -> filter(A? yes) -> map(ALICE) -> collect
  "Bob"    -> filter(A? no ) -> X  (dropped, map never runs)
  "Charlie"-> filter(A? no ) -> X
  "Dave"   -> filter(A? no ) -> X
  "Anna"   -> filter(A? yes) -> map(ANNA)  -> collect
```

---

## 4. Method references (the 4 kinds)

**What:** Shorthand for a lambda that does nothing but call one existing method. `Class::method`. Read `::` as "use this method as the lambda body."

**Why added:** When a lambda just forwards its args to an existing method (`x -> System.out.println(x)`), the method reference (`System.out::println`) is shorter and clearer.

**The 4 kinds:**

```
Kind                          Syntax              Equivalent lambda
-----------------------------------------------------------------------------
1. Static method              Integer::parseInt   s  -> Integer.parseInt(s)
2. Instance method of a       String::toUpperCase s  -> s.toUpperCase()
   PARTICULAR-type object
   (receiver becomes 1st arg)
3. Instance method of a       System.out::println x  -> System.out.println(x)
   SPECIFIC object
4. Constructor                ArrayList::new      () -> new ArrayList<>()
```

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class MethodRefDemo {
    public static void main(String[] args) {
        // 1. Static method ref
        Function<String, Integer> parse = Integer::parseInt;
        System.out.println(parse.apply("42"));                 // 42

        // 2. Instance method of an arbitrary object of a type
        Function<String, String> upper = String::toUpperCase;
        System.out.println(upper.apply("hi"));                 // HI

        // 3. Instance method of a specific (existing) object
        List<String> list = List.of("a", "b", "c");
        list.forEach(System.out::println);                     // a b c

        // 4. Constructor ref
        Supplier<ArrayList<String>> maker = ArrayList::new;
        System.out.println(maker.get());                       // []
    }
}
```

**Visual — how kind #2 differs from #3 (the classic confusion):**

```
KIND 2:  String::toUpperCase
         "the receiver is supplied later, as the first argument"
            s -> s.toUpperCase()
            ^                      the object you call it ON
            comes from the stream/lambda input

KIND 3:  System.out::println
         "the receiver (System.out) is FIXED right now"
            x -> System.out.println(x)
                 ^^^^^^^^^^             the object is already chosen;
                                        x is just the argument
```

---

## 5. Optional

**What:** `Optional<T>` is a box that either holds a value or is empty. It makes "this might be absent" explicit in the type system instead of relying on `null`.

**Why added:** To kill `NullPointerException` surprises. A method returning `Optional<User>` *forces* the caller to think about the empty case, instead of forgetting a null check.

**Visual — null vs Optional:**

```
WITHOUT Optional                  WITH Optional
================                  =================
User u = findUser(id);            Optional<User> u = findUser(id);

   u  ----> ???                      u ----> [ User ]   (present)
            (could be null;          u ----> [   X  ]   (empty, but SAFE
            blows up on u.getName())            never a raw null)

  u.getName();  // BOOM if null    u.map(User::getName)
                                    .orElse("Guest");  // never throws
```

```java
import java.util.*;

public class OptionalDemo {
    public static void main(String[] args) {
        Optional<String> present = Optional.of("Divakar");
        Optional<String> empty   = Optional.empty();

        // Safe extraction with a fallback
        System.out.println(present.orElse("Guest"));   // Divakar
        System.out.println(empty.orElse("Guest"));     // Guest

        // Transform only if present
        present.map(String::toUpperCase)
               .ifPresent(System.out::println);        // DIVAKAR

        // Build from a possibly-null reference
        String maybeNull = null;
        Optional<String> safe = Optional.ofNullable(maybeNull);
        System.out.println(safe.isPresent());          // false
    }
}
```

**BEFORE → AFTER**

```
BEFORE (Java 7): defensive null checks everywhere
---------------------------------------------------
String name = "Guest";
User u = findUser(id);
if (u != null && u.getName() != null) {
    name = u.getName();
}

AFTER (Java 8): one readable chain
---------------------------------------------------
String name = findUser(id)          // returns Optional<User>
    .map(User::getName)
    .orElse("Guest");
```

> Interview tip: use `orElseGet(supplier)` instead of `orElse(value)` when the default is expensive to build — `orElse` *always* evaluates its argument, `orElseGet` only runs the supplier when empty.

---

## 6. Default & static methods in interfaces

**What:**
- A **default method** has a body inside an interface (`default ... { }`). Implementing classes inherit it for free but can override it.
- A **static method** in an interface is a utility tied to the interface itself (`Interface.method()`).

**Why added:** To let interfaces *evolve without breaking* every existing implementor. Java needed to add `stream()`, `forEach()`, etc. to the existing `Collection`/`Iterable` interfaces — default methods made that possible without forcing millions of classes to implement the new methods.

**Visual — the problem they solve:**

```
PRE-JAVA-8 dilemma: add a method to a published interface
=========================================================
interface List { ... add foreach() ... }
         |
         +--> YourClass implements List   --> COMPILE ERROR
         +--> TheirClass implements List   --> COMPILE ERROR
         +--> (thousands of others)        --> all BREAK

JAVA-8 fix: give the new method a DEFAULT body
=========================================================
interface List { default void foreach(){ ...real code... } }
         |
         +--> YourClass    --> inherits it, still compiles
         +--> TheirClass   --> inherits it, still compiles
         +--> override only if you want custom behavior
```

```java
interface Vehicle {
    void start();   // abstract: each class must implement

    // DEFAULT method: shared behavior, overridable
    default void honk() {
        System.out.println("Beep! (default honk)");
    }

    // STATIC method: utility on the interface itself
    static String category() {
        return "Generic Vehicle";
    }
}

class Car implements Vehicle {
    public void start() {
        System.out.println("Car starting...");
    }
    // honk() is inherited for free
}

public class DefaultStaticDemo {
    public static void main(String[] args) {
        Car c = new Car();
        c.start();                          // Car starting...
        c.honk();                           // Beep! (default honk)
        System.out.println(Vehicle.category()); // Generic Vehicle (called on interface)
    }
}
```

```
default method  ->  called on the OBJECT      (c.honk())     , inheritable/overridable
static  method  ->  called on the INTERFACE   (Vehicle.category()), NOT inherited
```

---

## 7. The `java.time` API (LocalDate / LocalDateTime / Duration)

**What:** A brand-new, well-designed date/time library in package `java.time`. Key types:
- `LocalDate` — a date, no time (2026-06-01)
- `LocalTime` — a time, no date (14:30)
- `LocalDateTime` — date + time, no timezone
- `Duration` — an amount of time in seconds/nanos (machine time)
- `Period` — an amount in years/months/days (human time)

**Why it beats `Date`/`Calendar`:**

```
OLD  java.util.Date / Calendar          NEW  java.time
=====================================   ===============================
Mutable  (shared refs get corrupted)    Immutable  (thread-safe, safe to share)
Not thread-safe                         Thread-safe by design
Months are 0-based (Jan = 0!)           Months are 1-based / Month enum (sane)
Date is "date+time+zone" mashed up      Clear separate types per concept
SimpleDateFormat not thread-safe        DateTimeFormatter IS thread-safe
Confusing, error-prone API              Fluent, readable, chainable API
```

```java
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeDemo {
    public static void main(String[] args) {
        LocalDate today    = LocalDate.of(2026, 6, 1);     // 1-based month = June
        LocalDate deadline = today.plusDays(10);           // immutable -> returns NEW date
        LocalDateTime now  = LocalDateTime.of(today, LocalTime.of(14, 30));

        System.out.println(today);      // 2026-06-01
        System.out.println(deadline);   // 2026-06-11
        System.out.println(now);        // 2026-06-01T14:30

        // Duration between two times (machine time)
        Duration d = Duration.between(
            LocalTime.of(9, 0), LocalTime.of(17, 30));
        System.out.println(d.toHours() + "h " + (d.toMinutes() % 60) + "m"); // 8h 30m

        // Formatting (thread-safe)
        String txt = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println(txt);        // 01/06/2026
    }
}
```

**BEFORE → AFTER (the 0-based-month trap)**

```
BEFORE (Calendar): mutable + Jan==0 footgun
---------------------------------------------------
Calendar c = Calendar.getInstance();
c.set(2026, 5, 1);   // month 5 == JUNE (!!), not May — classic bug
c.add(Calendar.DAY_OF_MONTH, 10);
Date deadline = c.getTime();   // mutated 'c' in place

AFTER (java.time): immutable + sane months
---------------------------------------------------
LocalDate deadline = LocalDate.of(2026, 6, 1)  // 6 == June, as you'd expect
                              .plusDays(10);     // returns a NEW object
```

**Immutability visual:**

```
LocalDate today = 2026-06-01
        |
        today.plusDays(10)   ──►  creates a NEW LocalDate (2026-06-11)
        |
        today is STILL 2026-06-01   (original untouched — safe to share across threads)
```

---

### One-screen recap

```
Lambdas ............... pass behavior as data:  (a,b) -> a+b
Functional ifaces ..... the TYPE of a lambda (1 abstract method)
   Function  T->R   |  Predicate T->bool  |  Consumer T->void  |  Supplier ()->T
Streams ............... source -> intermediate(lazy) -> terminal(eager)
Method refs ........... Class::method  (static | type-instance | object-instance | new)
Optional .............. a box: present or empty — no raw nulls
default/static ........ evolve interfaces without breaking implementors
java.time ............. immutable, thread-safe, sane months — replaces Date/Calendar
```

---

### Java 9–11 (2017–2018)

A compact, visual interview reference. Each feature has a one-line "why", a tiny example, and an ASCII visual or before→after.

---

## Java 9 (Sept 2017)

### 1. Immutable collection factory methods — `List.of` / `Set.of` / `Map.of`

**Why:** one-line creation of *immutable* collections. No more `Arrays.asList(...)` quirks or 5-line builders.

```java
List<String> colors = List.of("red", "green", "blue");
Set<Integer>  ids    = Set.of(1, 2, 3);
Map<String,Integer> ages = Map.of("Ana", 30, "Bo", 25);

// Map.ofEntries for many pairs (avoids the 10-arg limit on Map.of)
Map<String,Integer> big = Map.ofEntries(
    Map.entry("a", 1),
    Map.entry("b", 2)
);
```

```
BEFORE (Java 8)                        AFTER (Java 9)
─────────────────────────────         ──────────────────────────
List<String> l = new ArrayList<>();    List<String> l =
l.add("red");                              List.of("red","green");
l.add("green");
l = Collections.unmodifiableList(l);
```

**Gotchas (interview favorites):**
```java
List.of("a", null);        // ✗ NullPointerException — nulls not allowed
Set.of(1, 1);              // ✗ IllegalArgumentException — duplicates not allowed
colors.add("yellow");      // ✗ UnsupportedOperationException — truly immutable
```

```
List.of(...)  ──►  [ red ][ green ][ blue ]   🔒 fixed size, no nulls, no edits
```

---

### 2. Stream `takeWhile` / `dropWhile`

**Why:** stop or skip *based on a predicate* (not a fixed count). Great for ordered/sorted streams.

```java
List<Integer> nums = List.of(2, 4, 6, 7, 8, 10);

nums.stream().takeWhile(n -> n % 2 == 0).toList(); // [2, 4, 6]
nums.stream().dropWhile(n -> n % 2 == 0).toList(); // [7, 8, 10]
```

```
stream:     2   4   6   7   8   10
                        ↑ predicate (even?) first becomes FALSE here
takeWhile → │←── keep ──►│ STOP        => [2, 4, 6]
dropWhile →  drop ... drop │←── keep ──►│ => [7, 8, 10]
```

`takeWhile` = "take from the front until the predicate fails."
`dropWhile` = "drop from the front until the predicate fails, then keep the rest."
(Contrast with `filter`, which tests *every* element across the whole stream.)

---

### 3. Stream `iterate(seed, hasNext, next)` — 3-arg form

**Why:** a bounded, for-loop-style infinite stream. The new middle argument is a *stop condition*.

```java
// Java 8: needed limit() to avoid an infinite stream
Stream.iterate(1, n -> n * 2).limit(5).toList();        // [1, 2, 4, 8, 16]

// Java 9: built-in stop predicate — reads like a for-loop
Stream.iterate(1, n -> n <= 16, n -> n * 2).toList();   // [1, 2, 4, 8, 16]
//             seed   hasNext       next
```

```
for (int n = 1;  n <= 16;  n *= 2)  ...
        │          │          │
       seed     hasNext      next     ← same three roles, now as a Stream
```

---

### 4. Private interface methods

**Why:** Java 8 gave interfaces `default` methods; Java 9 lets them share code *privately* without exposing helper methods to implementers.

```java
interface Logger {
    default void info(String msg)  { log("INFO",  msg); }
    default void error(String msg) { log("ERROR", msg); }

    // private helper — hidden from implementing classes & callers
    private void log(String level, String msg) {
        System.out.println("[" + level + "] " + msg);
    }
}
```

```
                 ┌──────────── interface Logger ────────────┐
   public  ───►  │  default info()      default error()     │  ◄── visible to all
                 │        \\                 /                │
   private ───►  │          ► log(level,msg) ◄  (shared)     │  ◄── hidden helper
                 └──────────────────────────────────────────┘
```

Also allowed: `private static` interface methods (helpers for `static` interface methods).

---

### 5. JPMS — `module-info.java` (the module system, conceptual)

**Why:** *strong encapsulation* + reliable dependencies. A JAR becomes a **module** that explicitly declares what it needs and what it shares. Default = everything is hidden unless `exported`.

```java
// src/com.shop/module-info.java
module com.shop {
    requires com.payments;        // I depend on this module
    exports com.shop.api;         // others may use THIS package
    // com.shop.internal stays hidden even though it's public-class-wise
}
```

```
   ┌─────────────────┐  requires   ┌─────────────────┐
   │   com.shop      │ ──────────► │   com.payments  │
   │                 │             │                 │
   │ exports api ────┼──► visible  │ exports gateway │
   │ internal (hidden)│            │ internal(hidden)│
   └─────────────────┘             └─────────────────┘
        public class in a non-exported package = INACCESSIBLE to outsiders
```

Key terms: `requires` (depends on), `exports` (shares a package), `requires transitive` (re-export a dependency to my consumers), `opens` (allow reflection, e.g. for frameworks).

---

## Java 10 (March 2018)

### 6. `var` — local-variable type inference

**Why:** less boilerplate for obvious types. The compiler infers the type from the initializer; the variable is **still statically, strongly typed** (this is *not* dynamic typing like JavaScript `var`).

```java
var count   = 10;                       // inferred: int
var name    = "Ada";                    // inferred: String
var list    = new ArrayList<String>();  // inferred: ArrayList<String>
var entry   = Map.entry("k", 1);        // inferred: Map.Entry<String,Integer>

for (var c : colors) { ... }            // ✓ in enhanced-for
for (var i = 0; i < 10; i++) { ... }    // ✓ in classic for
```

```
   var name = "Ada";
        │        │
        │        └─ initializer  ──►  compiler reads type
        └─ no type written            ──► name is FIXED as String forever
```

**Where `var` is NOT allowed** (very common interview trap):

```java
var x;                 // ✗ no initializer — nothing to infer from
var n = null;          // ✗ null has no type
var f = () -> 42;      // ✗ lambda needs a target type
var arr = {1, 2, 3};   // ✗ array initializer needs an explicit type

class C {
    var field = 1;     // ✗ NOT for fields
    var m(var p) {}    // ✗ NOT for method return types or parameters
}
```

```
✓ allowed:  local variables • for / for-each loop vars • try-with-resources
✗ banned:   fields • method params • method return type • catch • null/lambda/{}-array, no initializer
```

Rule of thumb: `var` only inside a method body, and only when an initializer makes the type obvious.

---

## Java 11 (Sept 2018) — first "modern" LTS

### 7. New `String` methods — `isBlank` / `strip` / `repeat` / `lines`

**Why:** everyday string ergonomics, plus Unicode-aware whitespace handling.

```java
"   ".isBlank();          // true  — empty or only whitespace
"  hi  ".strip();         // "hi"  — Unicode-aware trim (better than trim())
"=".repeat(10);           // "=========="
"a\nb\nc".lines().toList(); // ["a", "b", "c"] — stream of lines
```

`strip()` vs `trim()` (the classic question):
```
trim()   removes chars <= U+0020 only        ("ASCII-era" whitespace)
strip()  removes all Unicode whitespace      (e.g. \u2007, full-width spaces)

  "\u2007hi\u2007"  .trim()  ──► "\u2007hi\u2007"   (unchanged — missed it!)
  "\u2007hi\u2007"  .strip() ──► "hi"               (correct)
```

Also handy: `stripLeading()`, `stripTrailing()`.

```
"=".repeat(10)  ──►  ==========
"abc".isBlank() ──►  false        "  ".isBlank() ──► true
```

---

### 8. `var` in lambda parameters

**Why:** consistency with `var` elsewhere, and it lets you put **annotations** on inferred lambda params.

```java
// All three are equivalent in type:
(a, b)            -> a + b;          // implicit
(int a, int b)    -> a + b;          // explicit
(var a, var b)    -> a + b;          // Java 11 var form

// The real payoff — annotate an inferred parameter:
(@NonNull var x, @NonNull var y) -> x + y;
```

```
RULE: all-or-nothing per lambda
   (var a, var b)  ✓        (a, b)        ✓
   (var a, b)      ✗        (var a, int b) ✗   ← cannot mix var with other styles
```

---

### 9. The standard `HttpClient` (`java.net.http`) — now final/standard

**Why:** modern HTTP/1.1 + HTTP/2 client with sync **and** async APIs — replaces the clunky `HttpURLConnection` (and removes the need for an external client for simple calls).

```java
import java.net.http.*;
import java.net.URI;

HttpClient client = HttpClient.newHttpClient();

HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create("https://api.example.com/data"))
        .GET()
        .build();

// Synchronous
HttpResponse<String> res =
        client.send(req, HttpResponse.BodyHandlers.ofString());
System.out.println(res.statusCode() + " " + res.body());

// Asynchronous (non-blocking)
client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
      .thenApply(HttpResponse::body)
      .thenAccept(System.out::println);
```

```
  HttpClient ──► HttpRequest ──► .send() ─────► HttpResponse<String>   (blocking)
                              └─► .sendAsync() ► CompletableFuture<HttpResponse>  (async)
```

(Incubated in Java 9 under `jdk.incubator.http`; became standard `java.net.http` in Java 11.)

---

### 10. Run a single `.java` file directly — `java File.java`

**Why:** no separate `javac` step for scripts/learning. The launcher compiles in memory and runs `main`.

```java
// Hello.java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hi, " + (args.length > 0 ? args[0] : "world"));
    }
}
```

```
BEFORE (Java 8)                 AFTER (Java 11)
──────────────────              ─────────────────────────
javac Hello.java   ──► .class   java Hello.java          (one step, no .class on disk)
java  Hello                     java Hello.java Ada      (args still work)
```

```
   source-file mode:   java Hello.java
                          │
                          └─► compile in memory ─► run main()   (nothing written to disk)
```

Bonus: combine with a shebang (`#!/path/to/java --source 11`) to make an executable Java "script" on Unix.

---

### One-glance summary

```
JAVA 9  ── List/Set/Map.of  · takeWhile/dropWhile · iterate(seed,hasNext,next)
           · private interface methods · JPMS module-info (requires/exports)
JAVA 10 ── var (local inference only; not fields/params/return/null/lambda)
JAVA 11 ── String isBlank/strip/repeat/lines · var in lambda params
   (LTS)   · standard HttpClient (sync+async) · java File.java single-file run
```

---

### Java 12–17 (2019–2021)

A clutch of releases that made Java feel modern. Java 11 was the previous LTS; **Java 17** is the LTS that locks all of this in. Below, each feature gets an explanation, a runnable code example, and a visual or before→after.

---

## 1. Switch expressions (Java 14, JEP 361)

The classic `switch` is a *statement*: it does something but doesn't produce a value. You fall through cases unless you remember `break`, and a forgotten `break` is a classic bug. The new arrow form is an *expression*: it **returns a value**, has **no fall-through**, and the compiler forces you to be exhaustive.

**Old switch (statement, fall-through, mutable variable):**

```java
int numLetters;
switch (day) {
    case MONDAY:
    case FRIDAY:
    case SUNDAY:
        numLetters = 6;
        break;          // forget this -> bug
    case TUESDAY:
        numLetters = 7;
        break;
    case THURSDAY:
    case SATURDAY:
        numLetters = 8;
        break;
    case WEDNESDAY:
        numLetters = 9;
        break;
    default:
        throw new IllegalStateException("?: " + day);
}
```

**New switch expression (arrow, returns a value, exhaustive):**

```java
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;     // comma-grouped labels
    case TUESDAY                -> 7;
    case THURSDAY, SATURDAY     -> 8;
    case WEDNESDAY              -> 9;
    // no default needed if all enum constants are covered
};
```

Need multiple statements in a branch? Use a block and `yield` to return the value:

```java
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    default -> {
        int len = day.toString().length();
        yield len;            // 'yield' returns from the block
    }
};
```

```
   OLD switch (statement)                NEW switch (expression)
   ─────────────────────                 ───────────────────────
   case A:                               case A, B -> result;
   case B:                                  │        │     │
       x = result;   ← needs break          │        │     └─ value returned
       break;        ← forget = fall-thru    │        └─ no break, no fall-through
   ...                                       └─ comma-grouped labels
   switch DOES something                  switch IS a value
   not exhaustive-checked                 compiler checks exhaustiveness
```

---

## 2. Text blocks (Java 15, JEP 378)

Multiline strings without `\n` soup and `+` concatenation. Delimited by `"""`. The compiler strips the common leading whitespace (the "incidental" indentation) so your code can stay indented.

**Before — escape-and-concatenate hell:**

```java
String html = "<html>\n" +
              "    <body>\n" +
              "        <p>Hello</p>\n" +
              "    </body>\n" +
              "</html>\n";

String json = "{\n" +
              "  \"name\": \"Ada\",\n" +
              "  \"age\": 36\n" +
              "}";
```

**After — text block:**

```java
String html = """
        <html>
            <body>
                <p>Hello</p>
            </body>
        </html>
        """;

String json = """
        {
          "name": "Ada",
          "age": 36
        }""";          // closing """ on same line = no trailing newline
```

```
   "    <p>Hi</p>\n"           """
       │      │                    <p>Hi</p>        ← reads like the real output
       │      └ literal \n     """
       └ manual indent          ▲
                                └ position of closing """ sets the
                                  left margin; everything left of it
                                  (incidental indentation) is stripped
```

Handy extras:
- `\` at end of line = suppress the newline (line continuation).
- `\s` = a space that is *not* stripped as trailing whitespace.

---

## 3. Records (Java 16, JEP 395)

A `record` is a transparent carrier for immutable data. One line generates the constructor, private final fields, accessors, `equals`, `hashCode`, and `toString`.

```java
record Point(int x, int y) {}
```

That single line replaces roughly 40 lines of boilerplate:

```java
public final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point[x=" + x + ", y=" + y + "]";
    }
}
```

You can still add methods, static factories, and validate in a **compact constructor**:

```java
record Range(int lo, int hi) {
    Range {                          // compact constructor — no param list
        if (lo > hi)
            throw new IllegalArgumentException("lo > hi");
    }
    int length() { return hi - lo; } // extra instance method, allowed
    static Range unit() { return new Range(0, 1); }
}
```

```
   record Point(int x, int y) {}
        │         └────┬────┘
        │              └─ "components" — become final fields + accessors x() y()
        │
        └─ the compiler synthesizes, for free:
              ┌─────────────────────────────────────┐
              │  Point(int x, int y)   constructor   │
              │  int x()   int y()     accessors     │
              │  equals()  hashCode()  value-based   │
              │  toString()            "Point[x=…]"  │
              └─────────────────────────────────────┘
   1 line  ───────────────────────────────►  ~40 lines gone
```

Accessor note: record accessors are `x()` and `y()`, **not** `getX()`/`getY()`.

---

## 4. Pattern matching for `instanceof` (Java 16, JEP 394)

`instanceof` can now **bind a variable** of the target type in the same breath, so you skip the redundant cast that immediately follows every `instanceof` check.

**Before — test, then cast the exact same thing:**

```java
if (obj instanceof String) {
    String s = (String) obj;        // cast you already proved is safe
    if (s.length() > 5) {
        System.out.println(s.toUpperCase());
    }
}
```

**After — test-and-bind in one step:**

```java
if (obj instanceof String s) {      // 's' is in scope and already typed
    if (s.length() > 5) {
        System.out.println(s.toUpperCase());
    }
}
```

The bound variable's scope follows the flow of logic, so this reads naturally:

```java
if (obj instanceof String s && s.length() > 5) {
    System.out.println(s.toUpperCase());   // 's' usable here
}
// great in equals():
@Override public boolean equals(Object o) {
    return o instanceof Point p && p.x == x && p.y == y;
}
```

```
   BEFORE                              AFTER
   ──────                              ─────
   if (obj instanceof String) {        if (obj instanceof String s) {
       String s = (String) obj;  ◄──┐      │                      │
       use(s);                      │      └ test            bind ┘
   }                                │
        the cast just re-states ────┘      one step, no redundant cast,
        what instanceof proved             no ClassCastException risk
```

---

## 5. Helpful NullPointerExceptions (Java 14, JEP 358)

The JVM now tells you **exactly which variable was null** in a chained expression, instead of leaving you to guess which dereference on the line blew up. (On by default since Java 15.)

```java
// person.getAddress().getCity().toLowerCase()  -> getCity() returned null
String city = person.getAddress().getCity().toLowerCase();
```

```
   OLD message:
   Exception: java.lang.NullPointerException
        at App.main(App.java:12)
        └─ which of the 3 calls was null? you guess.

   NEW message (helpful):
   Exception: java.lang.NullPointerException:
        Cannot invoke "String.toLowerCase()" because the return value of
        "Address.getCity()" is null
        └──────────── names the exact null link in the chain ────────────┘
```

No code change needed — it's a runtime/diagnostic improvement.

---

## 6. `Stream.toList()` (Java 16)

A short, immutable-list terminal operation. Replaces the verbose `collect(Collectors.toList())` for the common case.

**Before:**

```java
import static java.util.stream.Collectors.toList;

List<String> names = people.stream()
        .map(Person::name)
        .collect(toList());     // mutable, needs the Collectors import
```

**After:**

```java
List<String> names = people.stream()
        .map(Person::name)
        .toList();              // concise, returns an UNMODIFIABLE list
```

```
   .collect(Collectors.toList())   ───►   .toList()
   ────────────────────────────           ─────────
   • needs Collectors import              • built into Stream
   • returns a MUTABLE ArrayList          • returns an UNMODIFIABLE List
   • ~28 characters                       • 9 characters
```

Gotcha: the list from `toList()` is unmodifiable — calling `.add(...)` on it throws `UnsupportedOperationException`. If you need to mutate, keep `collect(Collectors.toList())`.

---

## 7. Sealed classes (Java 17, JEP 409)

A `sealed` type declares a **closed** set of permitted subtypes with `permits`. No one outside that list can extend or implement it — you control the entire hierarchy. This pairs beautifully with `switch` because the compiler now knows every possible subtype.

```java
sealed interface Shape permits Circle, Square, Rectangle {}

record Circle(double radius)            implements Shape {}
record Square(double side)              implements Shape {}
record Rectangle(double w, double h)    implements Shape {}
```

Each permitted subclass must declare how *it* continues the hierarchy — one of `final`, `sealed`, or `non-sealed`:

```java
sealed class Animal permits Dog, Cat {}

final class Dog extends Animal {}        // final — no further subclassing
non-sealed class Cat extends Animal {}   // non-sealed — reopens to anyone
```

Because the set is closed, a `switch` over a sealed type can be **exhaustive without a default**:

```java
double area = switch (shape) {           // shape is a Shape
    case Circle c    -> Math.PI * c.radius() * c.radius();
    case Square s    -> s.side() * s.side();
    case Rectangle r -> r.w() * r.h();
    // no default — compiler knows these are the ONLY shapes
};
```

```
   OPEN hierarchy (normal class)        SEALED hierarchy
   ─────────────────────────────        ─────────────────────────────
            Shape                        sealed Shape permits {C,S,R}
          /  |  \  \  ...                       /     |      \
      Circle Sq Rect  ??? ← anyone          Circle  Square  Rectangle
                          can add a              (and NOTHING else —
                          subtype                 compiler-enforced)

   compiler can't reason about           compiler KNOWS the full set
   "all subtypes"                        → exhaustive switch, no default
```

---

### Cheat-sheet

```
Feature                         Version   One-liner
──────────────────────────────  ───────   ───────────────────────────────────
Switch expressions              14        switch (x) { case A -> v; }  returns a value
Text blocks                     15        """ multiline, no \n soup """
Records                         16        record Point(int x,int y){}  data carrier
Pattern matching instanceof     16        if (o instanceof String s)   test+bind
Helpful NPEs                    14/15     names the exact null link in a chain
Stream.toList()                 16        .toList()  unmodifiable, no Collectors
Sealed classes                  17        sealed ... permits ...        closed hierarchy
```

---

### Java 21 (2023, LTS) and beyond

A visual, example-rich reference for the headline features that land in Java 21 and the previews/finals that follow in 22-23.

---

## 1. Virtual Threads (Project Loom) — JEP 444

**The problem:** A traditional Java thread (a *platform thread*) is a thin wrapper over an OS thread. OS threads are expensive (~1 MB stack each, scheduled by the kernel). You can realistically run a few thousand before memory and context-switching costs kill you. So the whole industry built async/reactive code (`CompletableFuture`, reactive streams) just to avoid blocking those precious threads.

**The fix:** A *virtual thread* is a lightweight thread managed by the JVM, not the OS. Millions can exist at once. When a virtual thread hits a blocking I/O call, the JVM **unmounts** it from its underlying OS thread (the "carrier") and parks it, freeing that carrier to run another virtual thread. When the I/O completes, the virtual thread is **remounted** on some carrier and continues. You write simple blocking code; the JVM makes it scale.

```
  PLATFORM THREADS (old model)            VIRTUAL THREADS (Loom)
  1 Java thread = 1 OS thread             many virtual threads share few carriers

   Task A  Task B  Task C                  VT1 VT2 VT3 VT4 VT5 ... VT1000000
     |       |       |                       \   \   |   /   /        /
   [OS-1]  [OS-2]  [OS-3]   <- expensive       \   \  |  /   /  ------/
     |       |       |         ~1MB each         \   \ | /   /
   [kernel scheduler]                          +---------------------+
                                               | JVM scheduler (FJP) |
   blocked thread = wasted OS thread           +---------------------+
   ceiling ~ a few thousand                       |     |     |
                                               [OS-1][OS-2][OS-3]   <- few carriers
                                                  |     |     |     (= # CPU cores)
                                               [kernel scheduler]

   When VT1 blocks on I/O:
     VT1 is UNMOUNTED from OS-1 (parked, its stack moved to heap)
     OS-1 immediately MOUNTS VT2 and runs it
     I/O done -> VT1 is REMOUNTED on any free carrier and resumes
```

Why it scales to millions: a blocked virtual thread costs only a small heap object (its stack frames), **not** a pinned OS thread. The carriers stay busy doing real work instead of sitting blocked.

```java
// Old: a bounded, expensive pool. 10,000 tasks queue behind ~200 threads.
try (var pool = Executors.newFixedThreadPool(200)) {
    for (int i = 0; i < 10_000; i++) {
        pool.submit(() -> { fetchFromDb(); return null; }); // blocks a real OS thread
    }
}

// New: one virtual thread PER TASK. The JVM multiplexes them onto a few carriers.
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 1_000_000; i++) {
        executor.submit(() -> {
            fetchFromDb();   // blocking call — but the carrier is freed while we wait
            return null;
        });
    }
} // 1,000,000 concurrent blocking tasks, only a handful of OS threads used
```

```java
// Create one directly
Thread vt = Thread.ofVirtual().start(() -> System.out.println("hi from " + Thread.currentThread()));
vt.join();
// prints something like: VirtualThread[#28]/runnable@ForkJoinPool-1-worker-3

// Platform thread, for contrast
Thread pt = Thread.ofPlatform().start(() -> {});
```

**Gotchas worth knowing for interviews:**
- *Pinning*: inside a `synchronized` block (pre-Java 24) or a native call, a virtual thread cannot unmount — it pins its carrier. Prefer `ReentrantLock`. (Java 24's JEP 491 removes the `synchronized` pinning.)
- Don't pool virtual threads — they're cheap; create one per task.
- CPU-bound work gains nothing; the win is **I/O-bound concurrency**.

---

## 2. Pattern Matching for `switch` — JEP 441

Switch can now match on the **type** of a value, bind it to a variable, and use **guards** (`when`). The compiler enforces exhaustiveness (especially powerful with sealed types).

```
        +-----------------------------+
  obj   |  switch (obj)               |
 ─────► |    case Integer i  -> ...    |   each case: TYPE + binding (+ optional guard)
        |    case String s   -> ...    |
        |    case null       -> ...    |   null can be its own case now
        |    default         -> ...    |
        +-----------------------------+
```

```java
sealed interface Shape permits Circle, Rectangle, Triangle {}
record Circle(double r) {}
record Rectangle(double w, double h) {}
record Triangle(double base, double height) {}

static String describe(Object obj) {
    return switch (obj) {
        case null              -> "nothing";
        case Integer i when i < 0 -> "negative int " + i;   // guard with `when`
        case Integer i         -> "int " + i;
        case String s          -> "string of length " + s.length();
        case Shape sh          -> "a shape";
        default                -> "unknown";
    };
}
```

```java
// Exhaustive switch over a sealed type needs NO default — compiler proves coverage:
static double area(Shape s) {
    return switch (s) {
        case Circle c     -> Math.PI * c.r() * c.r();
        case Rectangle r  -> r.w() * r.h();
        case Triangle t   -> 0.5 * t.base() * t.height();
    }; // add a 4th permitted type and this fails to compile until you handle it
}
```

---

## 3. Record Patterns — JEP 440

Record patterns **destructure** a record directly in a `case` (or `instanceof`), pulling its components into variables. They **nest**, so you can take apart deeply structured data in one line.

```
   record Point(int x, int y)
   record Line(Point from, Point to)

   Line(Point(int x1, int y1), Point(int x2, int y2))
        \_____ from _____/      \_____ to ______/
        destructures the outer record AND the nested ones in one pattern
```

```java
record Point(int x, int y) {}
record Line(Point from, Point to) {}

static String classify(Object o) {
    return switch (o) {
        // destructure Point into x, y
        case Point(int x, int y) when x == y -> "on diagonal at " + x;
        case Point(int x, int y)             -> "point (" + x + "," + y + ")";

        // NESTED destructuring — reach right into the inner Points
        case Line(Point(var x1, var y1), Point(var x2, var y2)) ->
            "line from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")";

        default -> "other";
    };
}
```

```java
// Works with instanceof too:
Object o = new Point(3, 4);
if (o instanceof Point(int x, int y)) {
    System.out.println(x + y); // 7  — x and y are in scope here
}
```

---

## 4. Sequenced Collections — JEP 431

A new set of interfaces gives every ordered collection a **uniform** way to touch the first/last element and to reverse — something Java oddly lacked (e.g., `List` had `get(0)` but `Deque` used `getFirst()`, and `LinkedHashSet` had no clean "last" at all).

```
        SequencedCollection
        ├── addFirst(e) / addLast(e)
        ├── getFirst()  / getLast()
        ├── removeFirst()/ removeLast()
        └── reversed()           <- a reversed VIEW (no copy)

   List, Deque, LinkedHashSet  ── implement SequencedCollection
   LinkedHashMap               ── implements SequencedMap (firstEntry/lastEntry/reversed)

   [ A , B , C , D ]
     ^first       ^last
   reversed()  ->  view: [ D , C , B , A ]   (changes to backing list show through)
```

```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));

list.getFirst();   // "A"   (was list.get(0))
list.getLast();    // "D"   (was list.get(list.size() - 1))
list.addFirst("Z");        // [Z, A, B, C, D]
list.removeLast();         // [Z, A, B, C]
list.reversed();           // view: [C, B, A, Z]

LinkedHashSet<Integer> set = new LinkedHashSet<>(List.of(1, 2, 3));
set.getLast();     // 3   — finally easy on a LinkedHashSet

// SequencedMap
LinkedHashMap<String,Integer> m = new LinkedHashMap<>();
m.put("one", 1); m.put("two", 2);
m.firstEntry();    // one=1
m.lastEntry();     // two=2
m.reversed();      // view with reversed encounter order
```

---

## 5. A brief look at Java 22-23

```
  Java 21 (LTS, 2023) ── virtual threads, pattern switch, record patterns, sequenced collections
  Java 22 (2024)      ── unnamed variables `_` (final), stream gatherers (preview)
  Java 23 (2024)      ── gatherers + structured concurrency still maturing as previews
```

**Unnamed variables & patterns `_` (JEP 456, final in 22).** Use `_` for things you must declare but never read — clearer intent, no "unused" warnings.

```java
// Catch where you don't care about the exception object
try { Integer.parseInt(s); }
catch (NumberFormatException _) { return 0; }

// Loop where the variable is unused
int count = 0;
for (var _ : items) count++;

// Record pattern where you only want some components
if (obj instanceof Point(int x, _)) use(x);   // ignore y

// Switch binding you don't use
return switch (shape) {
    case Circle _    -> "round";
    case Rectangle _ -> "boxy";
    default          -> "?";
};
```

**Stream Gatherers (JEP 461 preview in 22, 473 in 23).** A custom, composable intermediate operation for streams — like building your own `map`/`filter`/`windowing` step that the JDK didn't ship. Fills the gap where `Collector` only worked as a terminal op.

```java
// Sliding windows of size 3 — not expressible with built-in intermediate ops before:
List<List<Integer>> windows = Stream.of(1, 2, 3, 4, 5)
    .gather(Gatherers.windowSliding(3))
    .toList();
// [[1,2,3], [2,3,4], [3,4,5]]

// Fixed-size batches:
Stream.of(1,2,3,4,5,6,7).gather(Gatherers.windowFixed(2)).toList();
// [[1,2],[3,4],[5,6],[7]]
```

**Structured Concurrency (JEP 453/462, still preview).** Treats a group of related concurrent subtasks as a single unit of work: fork several, join them together, and propagate errors/cancellation as a bundle — so you can't leak a runaway subtask. Pairs naturally with virtual threads.

```java
// Run two calls concurrently; if either fails, the other is cancelled automatically.
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Supplier<User>  user  = scope.fork(() -> fetchUser(id));    // virtual thread
    Supplier<Order> order = scope.fork(() -> fetchOrder(id));   // virtual thread

    scope.join();             // wait for both
    scope.throwIfFailed();    // surface the first failure

    return new Response(user.get(), order.get());
}
```

```
  Structured concurrency = a "scope" owns its forked subtasks

        scope ────┬──► fork fetchUser()   (VT)
                  └──► fork fetchOrder()  (VT)
        scope.join()  -> blocks until BOTH finish (or one fails)
        leaving the try-block -> any still-running subtask is cancelled
        => no orphaned threads, errors travel up as one unit
```

---

### One-line recap

| Feature | What it buys you |
|---|---|
| Virtual threads | Millions of cheap blocking tasks; simple code, async-level scale (I/O-bound) |
| Pattern matching `switch` | Type-based branching with bindings + `when` guards, compiler-checked exhaustiveness |
| Record patterns | Destructure records (incl. nested) inline in `switch`/`instanceof` |
| Sequenced collections | Uniform `getFirst`/`getLast`/`addFirst`/`reversed()` across ordered collections |
| `_` unnamed vars | Declare-but-ignore with clear intent (Java 22+) |
| Stream gatherers | Build custom intermediate stream ops, e.g. sliding windows (Java 22+ preview→final) |
| Structured concurrency | Fork/join subtasks as one unit; automatic cancellation & error propagation (preview) |

---

### 🎤 Interview priorities

**1. What interviewers ask MOST (ranked)**

1. **Java 8 functional toolkit** — lambdas, `Stream` API, `Optional`, method references, functional interfaces. By far the most common; expect live coding on `map`/`filter`/`collect`/`reduce` and grouping with `Collectors`.
2. **Records + pattern matching** — concise data carriers, `instanceof` pattern matching, and record deconstruction. Increasingly the second-most-asked "modern Java" topic.
3. **`var` (local-variable type inference)** — quick to ask, tests whether you know where it is and isn't allowed (locals yes; fields/params/return types no).
4. **Switch expressions + text blocks** — arrow `->` syntax, `yield`, exhaustiveness, and multi-line string literals. Common "do you keep up with the language?" check.
5. **Sealed classes/interfaces** — `permits`, controlled hierarchies, and how they pair with exhaustive pattern matching in `switch`.
6. **Virtual threads (Project Loom)** — what they are, the blocking-but-cheap model vs platform threads, and why they help I/O-bound concurrency. Usually conceptual, not live-coded.

**2. Feature → version it became standard**

| Feature | Standard since |
|---|---|
| Lambdas, Streams, Optional, functional interfaces | Java 8 |
| `var` (local-variable type inference) | Java 10 |
| Switch expressions | Java 14 |
| Text blocks | Java 15 |
| Records | Java 16 |
| Pattern matching for `instanceof` | Java 16 |
| Sealed classes/interfaces | Java 17 |
| Virtual threads | Java 21 |
| Record patterns / pattern matching for `switch` | Java 21 |

**3. Honest one-line summary a candidate can say**

"I'm comfortable with the Java 8 functional toolkit (streams, lambdas, Optional) and modern features like records, sealed classes, and pattern matching, and I'm aware of virtual threads and where they fit for I/O-bound concurrency."

---

<a id="ref-trees"></a>

## 🌳 Trees — Terminology & Taxonomy

A quick visual reference. Every section has an ASCII diagram, a short explanation, and an example.

---

### 1. Degree

**Degree of a NODE** = the number of *children* it has.
**Degree of a TREE** = the *maximum* degree found among all its nodes.

```
              A          <- degree 3 (children: B, C, D)
           /  |  \
          B   C   D      B: degree 2   C: degree 0   D: degree 1
         / \      |
        E   F     G       E,F,G: degree 0 (leaves)
```

- Node `A` has 3 children → degree(A) = 3.
- Node `B` has 2 children → degree(B) = 2.
- Leaves `E`, `F`, `C`, `G` have 0 children → degree 0.
- The biggest node-degree anywhere is 3 (at `A`), so **degree of the tree = 3**.

> Rule of thumb: a "Binary tree" is just a tree whose **degree ≤ 2**. Ternary ≤ 3. N-ary ≤ N.

---

### 2. Depth vs Height vs Level (root starts at 0)

| Term       | Measured from | Definition |
|------------|---------------|------------|
| **Depth**  | the root *down* to the node | # of edges on the path **root → node** |
| **Height** | the node *down* to a leaf    | # of edges on the **LONGEST** path **node → leaf** |
| **Level**  | the root                     | same number as **Depth** |

ONE annotated tree (each node tagged `d=depth h=height`):

```
                    A  (d=0, h=3)          Level 0
                  /   \
        (d=1,h=1) B     C (d=1, h=2)       Level 1
                 /     / \
     (d=2,h=0)  D     E   F (d=2, h=1)     Level 2
                          |
                          G (d=3, h=0)     Level 3
```

- **Depth of G** = 3 → there are 3 edges from `A` down to `G`.
- **Height of A** = 3 → its LONGEST downward path is `A→C→F→G` (3 edges). Note it does NOT stop at the first/shortest leaf `A→B→D` (which is only 2 edges).
- **Height of a leaf** = 0 (`D`, `E`, `G` all have h=0).
- **Level of a node = its Depth**, so `B` and `C` are both on Level 1.
- **Height of the whole tree** = height of the root = 3.

> ⚠️ Correction to bake in: **Height is the LONGEST path** to a leaf, not the shortest. People often eyeball the nearest leaf — don't.

---

### 3. Taxonomy by Maximum Number of Children

#### 3a. 1-child family (max 1 child → a "chain")

Several names, all describing a tree where nodes have **at most one child**. It collapses into a list — O(n) operations.

```
 Unary / Degenerate / Pathological      Left Skew         Right Skew          Zigzag
 (every node has exactly 1 child)

        A                                   A                A                    A
        |                                  /                  \                    \
        B                                 B                    B                    B
        |                                /                      \                  /
        C                               C                        C                C
        |                                                                          \
        D                                                                           D
```

- **Unary / Degenerate / Pathological / Path** — synonyms: each node has one child, so the tree is effectively a linked list.
- **Skew** — all children lean the same way (Left Skew = always a left child; Right Skew = always a right child).
- **Zigzag** — children alternate sides (left, right, left, …).
- Example: inserting **already-sorted** keys into a naive BST (1, 2, 3, 4 …) produces a Right Skew degenerate tree — that's the O(n) worst case.

#### 3b. Binary family (max 2 children)

```
 FULL / strict              COMPLETE                    PERFECT
 (each node has 0 OR 2)     (all levels full except     (every internal node has 2
                             last; last filled L->R)     children AND all leaves on
                                                         the SAME level)

        A                          A                            A
       / \                        / \                          / \
      B   C                      B   C                        B   C
         / \                    / \  /                       / \ / \
        D   E                  D  E F                        D E F G

 valid: every node has        last level filled left        completely filled,
 0 or 2 children              to right, no gap before        a "triangle"
 (C has 2, B has 0)           a filled slot
```

- **Full / Strict** — no node is allowed to have exactly one child; it's **0 or 2**.
- **Complete** — every level is completely filled *except possibly the last*, and the last level fills strictly **left → right** (no holes before a filled slot). This is exactly the shape a binary **heap** uses, which is why heaps store nicely in an array.
- **Perfect** — the strongest: all internal nodes have 2 children **and** every leaf sits on the same level. A perfect tree of height `h` has exactly `2^(h+1) - 1` nodes.

> ⚠️ **"Almost Complete" is just a SYNONYM for Complete** — same thing, no separate category.

**Other binary qualifiers** (these describe a *property*, not a child-count shape):

- **BST (Binary Search Tree)** — ordering invariant: `left < node < right` for every node. Search/insert/delete are **O(log n)** when balanced, **O(n)** worst case (degenerates to a skew tree on sorted input).
  ```
          8
        /   \
       3     10        every left subtree < node < every right subtree
      / \      \
     1   6      14
  ```
- **Balanced** — for **EVERY** node, the height difference between its left and right subtrees is **≤ 1**. This is **recursive** — it must hold at every node, not just the root. Guarantees O(log n) height. Self-balancing implementations: **AVL**, **Red-Black**, **Splay**.
  ```
          B                        B            <- |h(left) - h(right)| = |2 - 0| = 2
         / \                      /                NOT balanced: fails at THIS node
        A   C                    A                 even though lower nodes look fine
                                /
                               Z
  ```

> ⚠️ Correction to bake in: **Balanced is checked at EVERY node recursively.** A tree can look fine at the root but be unbalanced at a deeper node — that still makes the whole tree unbalanced.

#### 3c. Ternary family (max 3 children)

```
            A
         /  |  \
        B   C   D        each node may have up to 3 children
       /|\
      E F G
```

- **Ternary** — degree ≤ 3. Each node points to up to three subtrees.

#### 3d. N-ary / Generic (max N children)

```
              A
        /  /  |  \  \
       B  C   D   E  F      a node can have many children (N-way)
```

- **N-ary / Generic** — degree ≤ N (or unbounded). Used when each node fans out widely.
- Real-world structures built on this idea:
  - **B-Tree** — many keys/children per node; the backbone of databases and filesystems (shallow, disk-friendly).
  - **B+ Tree** — B-Tree variant where all data lives in the leaves, which are linked for fast range scans.
  - **Trie (prefix tree)** — one child per possible next symbol (e.g. 26 for lowercase letters); used for autocomplete and dictionaries.

> 🚫 **Fenwick / Binary Indexed Tree (BIT) is NOT a tree shape — keep it OUT of this list.** It's a separate data structure that uses clever **binary indexing** over a flat array to do prefix sums in O(log n). The word "tree" in its name refers to the index math, not a node-and-children shape.

---

### 4. How the Binary Categories Relate

The key relationships (read `⊂` as "is a subset of / is a special case of"):

```
   Perfect  ⊂  Complete          (every Perfect tree is also Complete)
   Perfect  ⊂  Full              (every Perfect tree is also Full)
   Complete  ≠  Full             (these two are DIFFERENT — neither implies the other)
```

Venn-style picture:

```
        ┌─────────────── all Binary Trees ───────────────┐
        │                                                 │
        │   ┌──────── FULL ────────┐   ┌─── COMPLETE ───┐ │
        │   │  (0 or 2 children)   │   │ (levels full,  │ │
        │   │                      │   │  last L->R)    │ │
        │   │            ┌─────────┴───┴─────────┐      │ │
        │   │            │       PERFECT         │      │ │
        │   │            │  (Full AND Complete   │      │ │
        │   │            │   at the same time)   │      │ │
        │   │            └─────────┬───┬─────────┘      │ │
        │   └──────────────────────┘   └────────────────┘ │
        │                                                 │
        └─────────────────────────────────────────────────┘
```

- **Perfect** lives in the overlap: it is simultaneously **Full** (every node has 0 or 2 children) and **Complete** (all leaves on the last level, fully filled).
- **Full but not Complete** — has only 0/2-children nodes, but leaves sit at different levels:
  ```
        A
       / \
      B   C
         / \
        D   E      A's left child B is a leaf at level 1,
                   but D,E are at level 2 -> NOT complete
  ```
- **Complete but not Full** — every level filled left→right, but some node has exactly **one** child:
  ```
        A
       / \
      B   C
     /
    D            B has exactly ONE child -> NOT full,
                 but the shape is still complete
  ```
- Remember: **"Almost Complete" = Complete** (same category, just another name).

---

#### One-line cheat sheet

| Category  | Rule | Quick test |
|-----------|------|-----------|
| Full / Strict | every node has **0 or 2** children | no node with a single child |
| Complete | all levels full except last; last fills **left→right** | array-backed heap shape |
| Perfect  | Full **and** all leaves on same level | `2^(h+1) − 1` nodes |
| Balanced | `|h(left) − h(right)| ≤ 1` at **EVERY** node (recursive) | AVL / Red-Black / Splay |
| BST | `left < node < right` everywhere | O(log n) avg, O(n) skewed |
| Degenerate/Skew | each node ≤ 1 child | behaves like a linked list |

---

<a id="ref-heap"></a>

## ⛰️ Heap & Array Representation (Java)

A quick visual reference for binary heaps and how trees live inside flat arrays.

---

### 1. Heap = Complete Tree + Heap-Order

A **heap** is a **complete binary tree** (every level full, except possibly the last, which fills **left → right**) that obeys a **heap-order** property:

- **Min-heap:** every parent `<=` its children → smallest value at the root.
- **Max-heap:** every parent `>=` its children → largest value at the root.

> Note: heap-order only constrains parent vs. child. Siblings are **not** ordered, so a heap is NOT a sorted structure.

#### A small MAX-heap as a tree

```
                 [50]            index 0
                /    \
            [30]      [40]       index 1, 2
            /  \      /
        [10]  [20]  [35]         index 3, 4, 5
```

Check the rule (parent `>=` children):
- 50 `>=` 30, 40   ✓
- 30 `>=` 10, 20   ✓
- 40 `>=` 35       ✓

#### Same heap as an ARRAY (filled level-by-level, left → right)

```
 index:    0     1     2     3     4     5
        +-----+-----+-----+-----+-----+-----+
 value: | 50  | 30  | 40  | 10  | 20  | 35  |
        +-----+-----+-----+-----+-----+-----+
          root  \___ level 1 ___/  \__ level 2 __/
```

Because the tree is **complete**, there are no gaps in the array — every slot `0 .. N-1` is used.

---

### 2. Array Index Formulas (0-indexed)

For the node stored at index `i`:

```
   leftChild(i)  = 2*i + 1
   rightChild(i) = 2*i + 2
   parent(i)     = floor( (i - 1) / 2 )
```

#### ⚠️ MIND THE PARENTHESES

```
   CORRECT:  floor( (i - 1) / 2 )      <-- subtract FIRST, then divide
   WRONG:    floor(  i - 1 / 2  )      <-- this is i - (1/2), totally different!
```

Operator precedence does division before subtraction, so without the parentheses `(i-1)` you compute `i - (1/2)`, which is just `i` for integers — a silent bug. In Java, integer division already floors toward zero for non-negatives, so `(i - 1) / 2` needs no explicit `Math.floor`:

```java
int parent(int i)     { return (i - 1) / 2; }   // floor is automatic for int
int leftChild(int i)  { return 2 * i + 1; }
int rightChild(int i) { return 2 * i + 2; }
```

#### Worked example — both directions

Using the array `[50, 30, 40, 10, 20, 35]`:

**Down (parent → children):**
```
   i = 0 (50):  left = 2*0+1 = 1 (30),  right = 2*0+2 = 2 (40)
   i = 1 (30):  left = 2*1+1 = 3 (10),  right = 2*1+2 = 4 (20)
   i = 2 (40):  left = 2*2+1 = 5 (35),  right = 2*2+2 = 6 (out of range, N=6)
```

**Up (child → parent):**
```
   i = 5 (35):  parent = (5-1)/2 = 4/2 = 2   -> 40   ✓
   i = 4 (20):  parent = (4-1)/2 = 3/2 = 1   -> 30   ✓   (3/2 floors to 1)
   i = 3 (10):  parent = (3-1)/2 = 2/2 = 1   -> 30   ✓
   i = 2 (40):  parent = (2-1)/2 = 1/2 = 0   -> 50   ✓   (1/2 floors to 0)
   i = 1 (30):  parent = (1-1)/2 = 0/2 = 0   -> 50   ✓
```

Note how the floor matters: indices `3` and `4` both map up to parent `1`, and indices `1` and `2` both map up to parent `0`. That is exactly the branching of the tree.

---

### 3. Internal Nodes vs. Leaves

For a complete binary tree of `N` nodes (0-indexed):

```
   internal (have at least one child):  indices  [ 0 .. floor(N/2) - 1 ]
   leaves   (no children):              indices  [ floor(N/2) .. N - 1 ]
```

#### Example: `N = 6`  →  `floor(6/2) = 3`

```
   internal nodes:  indices 0, 1, 2     (values 50, 30, 40)
   leaves:          indices 3, 4, 5     (values 10, 20, 35)
```

Visual split on the tree:

```
                 [50]  0  ┐
                /    \     │  internal: 0,1,2
            [30] 1   [40] 2 ┘
            /  \      /
        [10] [20]  [35]    ┐  leaves: 3,4,5
          3    4     5      ┘
```

Quick sanity check: the **last** node is at index `N-1 = 5`; its parent is `(5-1)/2 = 2`, so index `2` is the last node that has a child. Everything from `0` to `2` is internal, everything from `3` onward is a leaf. That is why heap-building (heapify) starts at index `floor(N/2) - 1` and walks down to `0` — it skips all the leaves, which are already trivially valid heaps.

---

### 4. Sift-Down (Heapify) + Java's PriorityQueue

**Sift-down idea (max-heap):** a node may violate heap-order after a change (e.g. after popping the root, the last element is moved to the top). To repair it, repeatedly swap the node with its **larger child** until it is `>=` both children (or becomes a leaf).

#### One ASCII step

Suppose the root `12` is smaller than its children. Compare with the larger child (`40`) and swap:

```
   BEFORE                 swap 12 with larger child (40)        AFTER
        [12]                                                      [40]
       /    \                                                    /    \
   [30]      [40]    ── 40 > 30, and 40 > 12, so swap ──>    [30]      [12]
   /  \      /                                               /  \      /
 [10][20] [35]                                            [10][20] [35]
```

`12` keeps sifting down (now compared against `35`) until it is `>=` its children or reaches a leaf. Sift-down costs `O(log N)` because the tree height is `log N`.

**Java's `PriorityQueue` is a binary heap.** It is a **min-heap by default** (smallest element returned first by `peek()` / `poll()`), backed internally by an array exactly as described above.

```java
// Min-heap (default): smallest first
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max-heap: pass a reverse comparator
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

minHeap.offer(40);
minHeap.offer(10);
minHeap.offer(35);
minHeap.peek();   // 10  (the minimum)
minHeap.poll();   // 10  (removes & returns min, then sifts down)
```

Key operations and costs:

```
   peek()           O(1)        read the root (min or max)
   offer() / add()  O(log N)    insert, then sift UP
   poll()           O(log N)    remove root, move last to root, sift DOWN
   build from N     O(N)        heapify bottom-up (faster than N inserts)
```

---

**One-line recap:** a heap is a complete tree packed into an array where `left = 2i+1`, `right = 2i+2`, `parent = floor((i-1)/2)` — keep those parentheses, mind that leaves start at `floor(N/2)`, and remember Java's `PriorityQueue` is just this with a min-heap default.

---

<a id="ref-traversals"></a>

## 🌲 Tree Traversals (with code)

A single reference. We draw ONE sample binary tree, define the `TreeNode` once, then walk every traversal against that same tree so you can compare visit orders directly.

### The sample tree

```
              1
            /   \
           2     3
          / \   / \
         4   5 6   7
            / \
           8   9
```

All visit orders below refer to THIS tree.

### TreeNode (defined once)

```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}
```

---

## DFS — Depth-First Search

Go deep along one branch before backtracking. The three classic orders differ only in WHERE you visit the root relative to its subtrees.

### Preorder (Root, Left, Right)

```
Visit order:  1  2  4  5  8  9  3  6  7
```

```java
void preorder(TreeNode node) {
    if (node == null) return;
    System.out.print(node.val + " ");  // root
    preorder(node.left);               // left
    preorder(node.right);              // right
}
```

Iterative: use a stack; push root, then on each pop print it and push RIGHT then LEFT (so left comes off first).

### Inorder (Left, Root, Right)

```
Visit order:  4  2  8  5  9  1  6  3  7
```

```java
void inorder(TreeNode node) {
    if (node == null) return;
    inorder(node.left);                // left
    System.out.print(node.val + " ");  // root
    inorder(node.right);               // right
}
```

Iterative: push all left children onto a stack, pop+visit, then move to the popped node's right child. For a BST this yields sorted order.

### Postorder (Left, Right, Root)

```
Visit order:  4  8  9  5  2  6  7  3  1
```

```java
void postorder(TreeNode node) {
    if (node == null) return;
    postorder(node.left);              // left
    postorder(node.right);             // right
    System.out.print(node.val + " ");  // root
}
```

Iterative: do a modified preorder (root, right, left) on a stack and REVERSE the result; or use two stacks. Useful for deleting/freeing a tree bottom-up.

---

## BFS — Breadth-First Search

Visit level by level, top to bottom, using a queue.

### Level Order (queue)

```
Visit order:  1  |  2  3  |  4  5  6  7  |  8  9
              L0      L1         L2          L3
```

```java
void levelOrder(TreeNode root) {
    if (root == null) return;
    Queue<TreeNode> q = new LinkedList<>();
    q.add(root);
    while (!q.isEmpty()) {
        int size = q.size();           // nodes in this level
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            System.out.print(node.val + " ");
            if (node.left != null)  q.add(node.left);
            if (node.right != null) q.add(node.right);
        }
        System.out.println();          // end of level
    }
}
```

### Zigzag / Spiral Level Order

Same level-by-level sweep, but alternate direction each level: L0 left→right, L1 right→left, L2 left→right, ...

```
Visit order:  1  |  3  2  |  4  5  6  7  |  9  8
              →        ←           →           ←
```

```java
void zigzagLevelOrder(TreeNode root) {
    if (root == null) return;
    Queue<TreeNode> q = new LinkedList<>();
    q.add(root);
    boolean leftToRight = true;
    while (!q.isEmpty()) {
        int size = q.size();
        Integer[] level = new Integer[size];
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            int idx = leftToRight ? i : (size - 1 - i);  // place from the correct end
            level[idx] = node.val;
            if (node.left != null)  q.add(node.left);
            if (node.right != null) q.add(node.right);
        }
        for (Integer v : level) System.out.print(v + " ");
        System.out.println();
        leftToRight = !leftToRight;
    }
}
```

---

## Boundary Traversal (brief idea + ASCII)

Print the OUTLINE of the tree, anti-clockwise, with no node printed twice:
1. Root.
2. Left boundary, top→bottom (exclude leaves).
3. All leaves, left→right.
4. Right boundary, bottom→top (exclude leaves).

```
        (1)            root
       /
     (2)               left boundary  ↓
     / \
  [4] (5)              leaves [4][8][9][6][7]  →
      / \
   [8] [9]
              (3)      right boundary ↑
              / \
           [6] [7]
```

For our tree the boundary is: `1  2  4  8  9  6  7  3` (root, left-edge `2`, leaves `4 8 9 6 7`, then right-edge `3` going up). `( )` = boundary edge, `[ ]` = leaf.

---

## Diagonal Traversal (brief idea + ASCII)

Group nodes by diagonal. Going RIGHT keeps you on the same diagonal; going LEFT drops you to the next diagonal. Slopes of `-1` (lines going `\`) bucket the nodes.

```
   1 ⟍              diagonal 0: 1 3 7
    \  ⟍
   2   3 ⟍          diagonal 1: 2 5 6 9
  / \ / \  ⟍
 4  5 6  7          diagonal 2: 4 8
    / \
   8   9
```

```
Diagonal 0:  1  3  7
Diagonal 1:  2  5  6  9
Diagonal 2:  4  8
```

Idea: BFS-style with a queue — for each node, walk its entire right chain adding values to the current diagonal, and push every LEFT child into the queue as the start of the next diagonal.

---

## Where these fit: DFS/BFS are GRAPH algorithms

Trees are just acyclic, connected graphs, so DFS and BFS are really GRAPH-traversal algorithms applied to a tree (no `visited` set needed because there are no cycles).

- **Kahn's topological sort IS BFS-based** — it repeatedly dequeues nodes whose in-degree has dropped to 0, exactly a BFS over a DAG.
- **Dijkstra and Prim are NOT plain BFS** — they are GREEDY algorithms driven by a PRIORITY QUEUE (min-heap), always expanding the cheapest frontier edge/vertex. BFS uses a plain FIFO queue and only works as a shortest-path method on UNWEIGHTED graphs; the priority queue is what makes Dijkstra/Prim handle weights.

---

<a id="ref-paradigms"></a>

## 🧩 Algorithm Design Paradigms

| Paradigm | One-line Definition | Classic Examples |
|---|---|---|
| **Divide & Conquer** | Split into independent subproblems, solve recursively, combine results. | Merge Sort, Quick Sort, Binary Search |
| **Greedy** | Make the locally optimal choice at each step, never reconsidering. | Dijkstra, Huffman Coding, Fractional Knapsack |
| **Dynamic Programming** | Solve overlapping subproblems once and reuse via memoization/tabulation. | 0/1 Knapsack, Longest Common Subsequence, Fibonacci |
| **Backtracking** | Build candidates incrementally, abandon ("prune") partial ones that fail constraints. | N-Queens, Sudoku Solver, Permutations/Subsets |
| **Brute Force** | Try every possible solution exhaustively; no shortcuts. | Linear Search, Naïve string matching, generating all subsets |
| **Recursion** | A function calls itself on smaller inputs until a base case (a technique underpinning D&C, DP, backtracking). | Factorial, Tower of Hanoi, Tree traversals |
| **Hashing** | Map keys to bucket indices via a hash function for ~O(1) lookup/insert. | Hash Map / Set, Two Sum, frequency counting |
| **Bitwise** | Manipulate individual bits for compact state & fast arithmetic/logic. | XOR to find single number, bitmask DP, check power of 2 |
| **Randomized** | Use random choices to get expected-good performance or simplicity. | Randomized QuickSort (pivot), Miller-Rabin primality, Reservoir Sampling |
| **Mathematical / Number-Theory** | Apply number-theoretic / closed-form properties instead of search. | Sieve of Eratosthenes (primes ≤ n), Euclidean GCD: `gcd(a,b)=gcd(b, a%b)` |
| **Branch & Bound** | Systematic search of a state tree using bounds to prune non-promising branches (optimization). | 0/1 Knapsack (optimal), Traveling Salesman Problem, Job Assignment |

### Key clarification: Knapsack
- **Fractional Knapsack → Greedy.** Items are divisible, so picking by highest value/weight ratio (and taking a fraction of the last item) is provably optimal in O(n log n).
- **0/1 Knapsack → Dynamic Programming.** Items are indivisible (take-or-leave), the greedy ratio fails, so you need a DP table over (item, capacity) in O(n·W). (Can also be solved exactly via Branch & Bound.)

### Quick mental cheats
- Optimal substructure **+ overlapping** subproblems → **DP**; optimal substructure **without** overlap → **Divide & Conquer**.
- Greedy is DP's special case where one choice is always safe (proven by an exchange argument).
- Backtracking = brute force + pruning; Branch & Bound = backtracking for optimization + cost bounds.

---

<a id="ref-sorting"></a>

## 🔃 Sorting Algorithms (with code)

A visual reference. For each algorithm: a one-line intuition, an ASCII trace on a tiny array, Java code, and complexity/properties. Everything assumes ascending order.

---

### 1. Bubble Sort

**Intuition:** Repeatedly swap adjacent out-of-order pairs so the largest element "bubbles" to the end each pass.

```
[5, 1, 4, 2]   compare adjacent, swap if left > right

Pass 1: (5 1) 4 2 -> 1 5 4 2
        1 (5 4) 2 -> 1 4 5 2
        1 4 (5 2) -> 1 4 2 5   <- 5 parked at end
Pass 2: (1 4) 2 5 -> 1 4 2 5
        1 (4 2) 5 -> 1 2 4 5   <- 4 parked
Pass 3: (1 2) 4 5 -> no swap   -> done (early exit)
```

```java
static void bubbleSort(int[] a) {
    int n = a.length;
    for (int i = 0; i < n - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < n - 1 - i; j++) {   // last i are sorted
            if (a[j] > a[j + 1]) {
                int t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;
                swapped = true;
            }
        }
        if (!swapped) break;                     // already sorted -> O(n)
    }
}
```

Best **O(n)** (with early exit) · Avg **O(n²)** · Worst **O(n²)** · Space **O(1)** · Stable ✅ · In-place ✅

---

### 2. Selection Sort

**Intuition:** Repeatedly pick the minimum of the unsorted region and place it at the front.

```
[5, 1, 4, 2]   find min of unsorted suffix, swap into position

i=0: min of [5 1 4 2] = 1 -> swap(5,1) -> [1, 5, 4, 2]
i=1: min of   [5 4 2] = 2 -> swap(5,2) -> [1, 2, 4, 5]
i=2: min of     [4 5] = 4 -> no swap    -> [1, 2, 4, 5]
done -> [1, 2, 4, 5]
```

```java
static void selectionSort(int[] a) {
    int n = a.length;
    for (int i = 0; i < n - 1; i++) {
        int min = i;
        for (int j = i + 1; j < n; j++)
            if (a[j] < a[min]) min = j;
        int t = a[i]; a[i] = a[min]; a[min] = t;  // one swap per pass
    }
}
```

Best **O(n²)** · Avg **O(n²)** · Worst **O(n²)** · Space **O(1)** · Stable ❌ (swaps can leapfrog equal keys) · In-place ✅

---

### 3. Insertion Sort

**Intuition:** Grow a sorted prefix; take the next element and shift larger ones right to insert it in place.

```
[5, 1, 4, 2]   | = boundary of sorted prefix

[5 | 1 4 2]  insert 1: shift 5 right    -> [1 5 | 4 2]
[1 5 | 4 2]  insert 4: shift 5 right    -> [1 4 5 | 2]
[1 4 5 | 2]  insert 2: shift 5,4 right  -> [1 2 4 5 |]
done -> [1, 2, 4, 5]
```

```java
static void insertionSort(int[] a) {
    for (int i = 1; i < a.length; i++) {
        int key = a[i], j = i - 1;
        while (j >= 0 && a[j] > key) {   // shift larger elements right
            a[j + 1] = a[j];
            j--;
        }
        a[j + 1] = key;                  // drop key into the gap
    }
}
```

Best **O(n)** (nearly sorted) · Avg **O(n²)** · Worst **O(n²)** · Space **O(1)** · Stable ✅ · In-place ✅

---

### 4. Merge Sort

**Intuition:** Split in half recursively, then merge two sorted halves into one.

```
[5, 1, 4, 2]
     split
[5, 1]     [4, 2]
 split       split
[5][1]     [4][2]
 merge       merge
[1, 5]     [2, 4]
       merge two sorted halves
   compare fronts: 1<2 -> 1 | 2<5 -> 2 | 4<5 -> 4 | 5
[1, 2, 4, 5]
```

```java
static void mergeSort(int[] a, int lo, int hi) {     // sorts a[lo..hi]
    if (lo >= hi) return;
    int mid = (lo + hi) >>> 1;
    mergeSort(a, lo, mid);
    mergeSort(a, mid + 1, hi);
    merge(a, lo, mid, hi);
}

static void merge(int[] a, int lo, int mid, int hi) {
    int[] tmp = new int[hi - lo + 1];
    int i = lo, j = mid + 1, k = 0;
    while (i <= mid && j <= hi)
        tmp[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];  // <= keeps it stable
    while (i <= mid) tmp[k++] = a[i++];
    while (j <= hi)  tmp[k++] = a[j++];
    System.arraycopy(tmp, 0, a, lo, tmp.length);
}
```

Best **O(n log n)** · Avg **O(n log n)** · Worst **O(n log n)** · Space **O(n)** (temp arrays) · Stable ✅ · In-place ❌

---

### 5. Quick Sort (with a partition)

**Intuition:** Pick a pivot, partition so smaller elements go left and larger go right, then recurse on each side.

```
[5, 1, 4, 2]   pivot = last = 2 (Lomuto)
i tracks the boundary of "< pivot"; scan j

start i=-1
 j=0: 5 < 2? no
 j=1: 1 < 2? yes -> i=0, swap a[0],a[1] -> [1, 5, 4, 2]
 j=2: 4 < 2? no
place pivot: swap a[i+1]=a[1] with pivot a[3] -> [1, 2, 4, 5]
pivot 2 now at index 1; recurse left [1], right [4, 5]
-> [1, 2, 4, 5]
```

```java
static void quickSort(int[] a, int lo, int hi) {
    if (lo >= hi) return;
    int p = partition(a, lo, hi);
    quickSort(a, lo, p - 1);
    quickSort(a, p + 1, hi);
}

// Lomuto partition: returns final index of the pivot
static int partition(int[] a, int lo, int hi) {
    int pivot = a[hi];           // pivot = last element
    int i = lo - 1;              // boundary of the "< pivot" region
    for (int j = lo; j < hi; j++) {
        if (a[j] < pivot) {
            i++;
            int t = a[i]; a[i] = a[j]; a[j] = t;
        }
    }
    int t = a[i + 1]; a[i + 1] = a[hi]; a[hi] = t;  // pivot into place
    return i + 1;
}
```

Best **O(n log n)** · Avg **O(n log n)** · Worst **O(n²)** (already-sorted with bad pivot) · Space **O(log n)** (recursion stack) · Stable ❌ · In-place ✅

---

### 6. Counting Sort

**Intuition:** Count occurrences of each key, then use a prefix-sum of counts to place each element at its exact final index.

```
[2, 5, 2, 1]   range 0..5

count:   idx 0 1 2 3 4 5
             0 1 2 0 0 1
prefix sums (running total -> end positions):
             0 1 3 3 3 4
place from RIGHT (keeps it stable):
  1 -> pos 0;  2 -> pos 2;  5 -> pos 3;  2 -> pos 1
[1, 2, 2, 5]
```

```java
static int[] countingSort(int[] a, int maxVal) {
    int[] count = new int[maxVal + 1];
    for (int x : a) count[x]++;                 // tally
    for (int i = 1; i <= maxVal; i++)
        count[i] += count[i - 1];               // prefix sums
    int[] out = new int[a.length];
    for (int i = a.length - 1; i >= 0; i--) {   // right-to-left -> stable
        out[--count[a[i]]] = a[i];
    }
    return out;
}
```

Best **O(n + k)** · Avg **O(n + k)** · Worst **O(n + k)** · Space **O(n + k)** · Stable ✅ · In-place ❌ · (k = value range; great when k is small)

**🔻 Handling `0` and NEGATIVE values — shift by an offset.**

Counting sort uses the **value itself as an index** (`count[value]++`), so a negative value like `-3` becomes a negative index → `ArrayIndexOutOfBoundsException`. (`0` alone is fine — it's a valid index.) Fix: find `min` and **offset every value by `-min`**, so the smallest value maps to index `0`.

```
nums = [3, -1, 0, -3, 2, -1, 0]        min = -3,  max = 3
index = value - min  (= value + 3)     ->  count size = max - min + 1 = 7

value:  -3  -2  -1   0   1   2   3
index:   0   1   2   3   4   5   6      <- index = value - min
count:   1   0   2   2   0   1   1

rebuild -> for each index i, emit (i + min) repeated count[i] times:
   -3x1 , -1x2 , 0x2 , 2x1 , 3x1   ->   [-3, -1, -1, 0, 0, 2, 3]
```

```java
static int[] countingSortWithNegatives(int[] a) {
    int min = a[0], max = a[0];
    for (int x : a) { min = Math.min(min, x); max = Math.max(max, x); }
    int[] count = new int[max - min + 1];          // range size
    for (int x : a) count[x - min]++;              // offset handles negatives & 0
    int idx = 0;
    for (int i = 0; i < count.length; i++)
        while (count[i]-- > 0) a[idx++] = i + min; // value = index + min
    return a;
}
```

> 🔑 Two-way rule: `index = value - min`, `value = index + min`. The same `- min` offset also slots into the stable prefix-sum version above. Stays efficient only when the **range `max - min`** is small relative to `n`.

**🔒 Stable counting sort WITH negatives — offset + cumulative together.**

Combine both tricks: index *everywhere* with `value - min`, but **store the original value** (so it also works for objects by key). The cumulative + right-to-left placement is unchanged — only the index is offset.

Example `[1, -2, 1, -2, 0]` (duplicates tagged `a`/`b` to watch stability): `min = -2`, so `index = value + 2`.

```
① count (offset index)                 ② cumulative (prefix sum)
 idx (value): 0(-2) 1(-1) 2(0) 3(1)       count: [2, 0, 1, 2]
 count      :  2     0    1    2             |  running total
                                           cumul: [2, 2, 3, 5]

③ place RIGHT -> LEFT:   out[--cumul[value - min]] = value
 cumul = [2, 2, 3, 5]                 out = [ _ ,  _ , _ ,  _ ,  _ ]
 x=0    idx=2  cumul[2]:3->2  out[2]=0     [ _ ,  _ , 0 ,  _ ,  _ ]
 x=-2b  idx=0  cumul[0]:2->1  out[1]=-2b   [ _ , -2b, 0 ,  _ ,  _ ]
 x=1b   idx=3  cumul[3]:5->4  out[4]=1b    [ _ , -2b, 0 ,  _ , 1b ]
 x=-2a  idx=0  cumul[0]:1->0  out[0]=-2a   [-2a, -2b, 0 ,  _ , 1b ]
 x=1a   idx=3  cumul[3]:4->3  out[3]=1a    [-2a, -2b, 0 , 1a , 1b ]
 -> [-2, -2, 0, 1, 1]   (-2a before -2b, 1a before 1b  =>  STABLE)
```

```java
static int[] stableCountingSort(int[] a) {
    int min = a[0], max = a[0];
    for (int x : a) { min = Math.min(min, x); max = Math.max(max, x); }
    int[] count = new int[max - min + 1];
    for (int x : a) count[x - min]++;              // (1) count  (offset index)
    for (int i = 1; i < count.length; i++)
        count[i] += count[i - 1];                  // (2) cumulative
    int[] out = new int[a.length];
    for (int i = a.length - 1; i >= 0; i--)        // (3) right->left = STABLE
        out[--count[a[i] - min]] = a[i];           //     index offset; store ORIGINAL value
    return out;
}
```

> 🔑 Negatives change exactly one thing: index with `value - min` (size `max - min + 1`). The cumulative + right-to-left logic that gives stability is identical. We store `a[i]` itself (not `i + min`), so the same method sorts objects by a key.

---

### 7. Radix Sort (LSD)

**Intuition:** Stably sort by one digit at a time, starting from the **least-significant** digit.

> ⚠️ **Common mistake — CORRECTED:** LSD radix sort processes digits from the **LEAST-significant digit first (RIGHTMOST), moving LEFT** — *not* left-to-right. Each pass must use a **stable** sub-sort (counting sort) so the ordering established by earlier, lower-order digits survives. If you go left-to-right (most-significant first) with a single stable pass, the result is wrong. (There *is* an MSD radix variant, but it recurses into buckets per digit — it is not the simple single-pass LSD loop shown here.)

```
[170, 45, 75, 90]   process digits RIGHT -> LEFT

Pass 1 — ones digit (rightmost):
  170(0) 90(0) 45(5) 75(5)  -> [170, 90, 45, 75]
Pass 2 — tens digit:
  170(7) 90(9) 45(4) 75(7)  stable by tens
  -> [45, 170, 75, 90]   (170 before 75: both 7 in tens, 170 came first)
Pass 3 — hundreds digit:
  045(0) 075(0) 090(0) 170(1) -> [45, 75, 90, 170]
done -> [45, 75, 90, 170]
```

```java
static void radixSortLSD(int[] a) {
    if (a.length == 0) return;
    int max = a[0];
    for (int x : a) max = Math.max(max, x);

    // exp = 1, 10, 100, ...  ->  ones, tens, hundreds (RIGHT to LEFT)
    for (int exp = 1; max / exp > 0; exp *= 10) {
        countingSortByDigit(a, exp);
    }
}

static void countingSortByDigit(int[] a, int exp) {
    int n = a.length;
    int[] out = new int[n];
    int[] count = new int[10];                  // digits 0..9
    for (int x : a) count[(x / exp) % 10]++;
    for (int i = 1; i < 10; i++)
        count[i] += count[i - 1];               // prefix sums
    for (int i = n - 1; i >= 0; i--) {          // right-to-left -> STABLE
        int d = (a[i] / exp) % 10;
        out[--count[d]] = a[i];
    }
    System.arraycopy(out, 0, a, 0, n);
}
```

Best **O(d·(n + b))** · Avg **O(d·(n + b))** · Worst **O(d·(n + b))** · Space **O(n + b)** · Stable ✅ · In-place ❌ · (d = #digits, b = base/radix, here 10)

---

### 8. Heap Sort

**Intuition:** Build a max-heap, then repeatedly swap the root (max) to the end and sift the new root down.

```
[5, 1, 4, 2]   build max-heap (array as binary tree)

heapify -> max-heap: [5, 2, 4, 1]
          5
        /   \
       2     4
      /
     1

swap root 5 <-> last 1 -> [1, 2, 4 | 5], shrink, sift down -> [4, 2, 1 | 5]
swap root 4 <-> last 1 -> [1, 2 | 4 5], sift down          -> [2, 1 | 4 5]
swap root 2 <-> last 1 -> [1 | 2 4 5]
done -> [1, 2, 4, 5]
```

```java
static void heapSort(int[] a) {
    int n = a.length;
    for (int i = n / 2 - 1; i >= 0; i--)   // build max-heap bottom-up
        siftDown(a, i, n);
    for (int end = n - 1; end > 0; end--) {
        int t = a[0]; a[0] = a[end]; a[end] = t;  // max -> end
        siftDown(a, 0, end);                       // restore heap on [0,end)
    }
}

static void siftDown(int[] a, int i, int n) {
    while (true) {
        int largest = i, l = 2 * i + 1, r = 2 * i + 2;
        if (l < n && a[l] > a[largest]) largest = l;
        if (r < n && a[r] > a[largest]) largest = r;
        if (largest == i) break;
        int t = a[i]; a[i] = a[largest]; a[largest] = t;
        i = largest;
    }
}
```

Best **O(n log n)** · Avg **O(n log n)** · Worst **O(n log n)** · Space **O(1)** · Stable ❌ · In-place ✅

---

### 9. Bucket Sort

**Intuition:** Scatter elements into ordered buckets by range, sort each bucket, then concatenate.

```
[0.42, 0.11, 0.45, 0.91]   n=4 buckets, index = floor(v * n)

scatter:
  bucket0 [0.0,0.25): 0.11
  bucket1 [0.25,0.5): 0.42, 0.45
  bucket2 [0.5,0.75): (empty)
  bucket3 [0.75,1.0): 0.91
sort each bucket: [0.11] [0.42,0.45] [] [0.91]
concatenate -> [0.11, 0.42, 0.45, 0.91]
```

```java
static void bucketSort(float[] a) {
    int n = a.length;
    if (n == 0) return;
    List<List<Float>> buckets = new ArrayList<>();
    for (int i = 0; i < n; i++) buckets.add(new ArrayList<>());

    for (float v : a) {                       // scatter (assumes v in [0,1))
        int idx = Math.min(n - 1, (int) (v * n));
        buckets.get(idx).add(v);
    }
    for (List<Float> b : buckets) Collections.sort(b);  // sort each bucket

    int k = 0;                                // gather
    for (List<Float> b : buckets)
        for (float v : b) a[k++] = v;
}
```

Best **O(n + k)** · Avg **O(n + k)** · Worst **O(n²)** (all in one bucket) · Space **O(n + k)** · Stable ✅ (if bucket sort is stable) · In-place ❌ · (assumes uniform distribution)

---

### 10. Shell Sort

**Intuition:** Insertion sort on elements spaced a "gap" apart, shrinking the gap to 1 so far-apart elements settle early.

```
[5, 1, 4, 2, 8, 3]   gaps: 3, then 1

gap=3: compare indices (0,3)(1,4)(2,5)
  (5,2)->swap, (1,8)->ok, (4,3)->swap
  -> [2, 1, 3, 5, 8, 4]
gap=1: ordinary insertion sort on a nearly-sorted array (cheap)
  -> [1, 2, 3, 4, 5, 8]
```

```java
static void shellSort(int[] a) {
    int n = a.length;
    for (int gap = n / 2; gap > 0; gap /= 2) {     // shrinking gap
        for (int i = gap; i < n; i++) {            // gapped insertion sort
            int key = a[i], j = i;
            while (j >= gap && a[j - gap] > key) {
                a[j] = a[j - gap];
                j -= gap;
            }
            a[j] = key;
        }
    }
}
```

Best **O(n log n)** · Avg **~O(n^1.25)** (gap-sequence dependent) · Worst **O(n²)** (with this gap sequence) · Space **O(1)** · Stable ❌ · In-place ✅

---

### 11. Tim Sort

**Intuition:** Find/extend small "runs" with insertion sort, then merge the runs like merge sort (Java's `Arrays.sort` for objects).

```
[5, 1, 4, 2, 8, 3]   RUN = 4 here (real Timsort uses 32–64)

split into runs of size RUN:
  run A = [5, 1, 4, 2]   insertion-sort -> [1, 2, 4, 5]
  run B = [8, 3]         insertion-sort -> [3, 8]
merge sorted runs A and B (like merge sort):
  1<3 ->1 | 2<3 ->2 | 3<4 ->3 | 4<8 ->4 | 5<8 ->5 | 8
-> [1, 2, 3, 4, 5, 8]
```

```java
static final int RUN = 32;

static void timSort(int[] a) {
    int n = a.length;
    for (int i = 0; i < n; i += RUN)                       // 1) sort runs
        insertionRange(a, i, Math.min(i + RUN - 1, n - 1));

    for (int size = RUN; size < n; size *= 2) {            // 2) merge runs
        for (int lo = 0; lo < n; lo += 2 * size) {
            int mid = Math.min(lo + size - 1, n - 1);
            int hi  = Math.min(lo + 2 * size - 1, n - 1);
            if (mid < hi) merge(a, lo, mid, hi);            // reuse merge()
        }
    }
}

static void insertionRange(int[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++) {
        int key = a[i], j = i - 1;
        while (j >= lo && a[j] > key) { a[j + 1] = a[j]; j--; }
        a[j + 1] = key;
    }
}
```

Best **O(n)** (already-sorted runs) · Avg **O(n log n)** · Worst **O(n log n)** · Space **O(n)** · Stable ✅ · In-place ❌

---

## 📊 Comparison Table

| Algorithm | Best | Avg | Worst | Space | Stable | In-place | Notes |
|-----------|------|-----|-------|-------|:------:|:--------:|-------|
| **Bubble** | O(n) | O(n²) | O(n²) | O(1) | ✅ | ✅ | Early-exit on no-swap; teaching only |
| **Selection** | O(n²) | O(n²) | O(n²) | O(1) | ❌ | ✅ | Minimum swaps (n-1); always n² compares |
| **Insertion** | O(n) | O(n²) | O(n²) | O(1) | ✅ | ✅ | Excellent for small/nearly-sorted input |
| **Merge** | O(n log n) | O(n log n) | O(n log n) | O(n) | ✅ | ❌ | Predictable; good for linked lists/external sort |
| **Quick** | O(n log n) | O(n log n) | O(n²) | O(log n) | ❌ | ✅ | Fast in practice; randomize/median pivot to avoid n² |
| **Counting** | O(n + k) | O(n + k) | O(n + k) | O(n + k) | ✅ | ❌ | Non-comparison; needs small integer range k |
| **Radix (LSD)** | O(d(n+b)) | O(d(n+b)) | O(d(n+b)) | O(n + b) | ✅ | ❌ | Digits RIGHT→LEFT; stable sub-sort each pass |
| **Heap** | O(n log n) | O(n log n) | O(n log n) | O(1) | ❌ | ✅ | In-place n log n; poor cache locality |
| **Bucket** | O(n + k) | O(n + k) | O(n²) | O(n + k) | ✅ | ❌ | Needs uniform distribution over a range |
| **Shell** | O(n log n) | ~O(n^1.25) | O(n²) | O(1) | ❌ | ✅ | Gapped insertion; complexity = gap-sequence |
| **Tim** | O(n) | O(n log n) | O(n log n) | O(n) | ✅ | ❌ | Runs + merge; Java/Python default for objects |

**Legend:** k = value range/extra slots · d = number of digits · b = radix/base (10 above) · ✅ yes · ❌ no.

**Quick picks:**
- **General purpose, comparison-based:** Quick (in-place, fast) or Merge/Tim (stable, guaranteed n log n).
- **Small or nearly-sorted:** Insertion.
- **Small integer keys / fixed-width keys:** Counting / Radix (beat the n log n comparison lower bound).
- **Need stability:** Merge, Tim, Insertion, Counting, Radix.
- **Tight memory + guaranteed n log n:** Heap.

---

## 🔎 Searching · 🕸️ Graph · 🔤 String Algorithms

A compact interview reference. All code is Java, ready to adapt. Complexities use `n` for input size, `V`/`E` for graph vertices/edges.

---

<a id="ref-searching"></a>

## 🔎 Searching

| Algorithm     | Time (avg)      | Requires sorted? | One-line when-to-use                                  |
|---------------|-----------------|------------------|------------------------------------------------------|
| Linear        | O(n)            | No               | Tiny/unsorted data, or you only scan once.           |
| Binary        | O(log n)        | Yes              | Sorted array, random access, default go-to.          |
| Jump          | O(√n)           | Yes              | Sorted, when jumping back is cheaper than seeking.   |
| Interpolation | O(log log n)*   | Yes              | Sorted AND uniformly distributed numeric keys.       |
| Fibonacci     | O(log n)        | Yes              | Sorted, no division allowed, non-uniform memory cost.|

\* Interpolation degrades to O(n) on skewed data.

### Linear Search
**Intuition:** Walk every element until you hit the target. No assumptions, no preprocessing.
**When:** Data is unsorted, tiny, or you'd pay more to sort than to scan.

```java
int linearSearch(int[] a, int target) {
    for (int i = 0; i < a.length; i++)
        if (a[i] == target) return i;
    return -1;
}
```

### Binary Search
**Intuition:** Halve the search space each step by comparing against the middle. The workhorse of sorted search.
**When:** Sorted array with O(1) random access. This is your default for "find in sorted."

```
[ 1  3  5  7  9 11 13 ]   target = 9
  lo        mid        hi
            a[mid]=7 < 9  -> go right
                  lo  mid  hi
                      a[mid]=11 > 9 -> go left ... converge on 9
```

```java
int binarySearch(int[] a, int target) {
    int lo = 0, hi = a.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;   // avoids overflow vs (lo+hi)/2
        if (a[mid] == target) return mid;
        else if (a[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}
```

### Jump Search
**Intuition:** Hop forward in blocks of √n until you overshoot, then linear-scan back through the last block.
**When:** Sorted data where jumping forward is cheap but jumping *back* is expensive (e.g., certain external/sequential media) — fewer backward steps than binary search.

```java
int jumpSearch(int[] a, int target) {
    int n = a.length, step = (int) Math.sqrt(n), prev = 0;
    while (prev < n && a[Math.min(step, n) - 1] < target) {
        prev = step;
        step += (int) Math.sqrt(n);
        if (prev >= n) return -1;
    }
    for (int i = prev; i < Math.min(step, n); i++)
        if (a[i] == target) return i;
    return -1;
}
```

### Interpolation Search
**Intuition:** Like binary, but *estimates* where the target lies by linear interpolation instead of always picking the middle — guesses near the start for small keys, near the end for large.
**When:** Sorted AND values are roughly uniformly distributed (e.g., phone numbers, evenly-spaced IDs). Beats binary on uniform data, but worst case is O(n).

```java
int interpolationSearch(int[] a, int target) {
    int lo = 0, hi = a.length - 1;
    while (lo <= hi && target >= a[lo] && target <= a[hi]) {
        if (lo == hi) return a[lo] == target ? lo : -1;
        // proportional probe position
        int pos = lo + (int) (((long)(target - a[lo]) * (hi - lo)) / (a[hi] - a[lo]));
        if (a[pos] == target) return pos;
        else if (a[pos] < target) lo = pos + 1;
        else hi = pos - 1;
    }
    return -1;
}
```

### Fibonacci Search
**Intuition:** Divides the array using Fibonacci numbers instead of halving — splits are uneven (≈61.8%/38.2%) and use only **addition/subtraction, no division**.
**When:** Sorted data where division is costly, or when you want to probe earlier indices (cache-friendly / non-uniform memory access).

```java
int fibonacciSearch(int[] a, int target) {
    int n = a.length;
    int fib2 = 0, fib1 = 1, fib = fib2 + fib1;   // fib = fib1 + fib2
    while (fib < n) { fib2 = fib1; fib1 = fib; fib = fib1 + fib2; }
    int offset = -1;
    while (fib > 1) {
        int i = Math.min(offset + fib2, n - 1);
        if (a[i] < target)      { fib = fib1; fib1 = fib2; fib2 = fib - fib1; offset = i; }
        else if (a[i] > target) { fib = fib2; fib1 = fib1 - fib2; fib2 = fib - fib1; }
        else return i;
    }
    if (fib1 == 1 && offset + 1 < n && a[offset + 1] == target) return offset + 1;
    return -1;
}
```

---

<a id="ref-graph"></a>

## 🕸️ Graph

> ⚠️ **Greedy ≠ BFS.** **Dijkstra** and **Prim** look like BFS because they explore outward, but they pull the *cheapest* frontier node from a **priority queue** (a greedy choice), not the next node from a plain FIFO queue. Plain BFS only gives shortest paths when every edge has equal weight.

| Algorithm       | Solves                        | Technique             | Time                  | Notes                          |
|-----------------|-------------------------------|-----------------------|-----------------------|--------------------------------|
| BFS             | Unweighted shortest path      | FIFO queue            | O(V+E)                | Level-order traversal.         |
| DFS             | Reachability, cycles, topo    | Stack / recursion     | O(V+E)                | Goes deep first.               |
| Dijkstra        | SSSP, non-negative weights    | **Greedy + PQ**       | O((V+E) log V)        | No negative edges!             |
| Bellman-Ford    | SSSP, allows negatives        | DP / edge relaxation  | O(V·E)                | Detects negative cycles.       |
| Floyd-Warshall  | All-pairs shortest path       | DP                    | O(V³)                 | Small dense graphs.            |
| Kruskal         | MST                           | Greedy + Union-Find   | O(E log E)            | Sort edges; good for sparse.   |
| Prim            | MST                           | **Greedy + PQ**       | O((V+E) log V)        | Grow one tree; good for dense. |

### BFS — Breadth-First Search
**Intuition:** Explore in rings: all nodes at distance 1, then distance 2, … via a FIFO queue. Gives shortest paths in **unweighted** graphs.

```java
void bfs(List<List<Integer>> adj, int src) {
    boolean[] seen = new boolean[adj.size()];
    Queue<Integer> q = new ArrayDeque<>();
    seen[src] = true; q.add(src);
    while (!q.isEmpty()) {
        int u = q.poll();
        // visit(u);
        for (int v : adj.get(u))
            if (!seen[v]) { seen[v] = true; q.add(v); }
    }
}
```

### DFS — Depth-First Search
**Intuition:** Dive as deep as possible before backtracking. Foundation for cycle detection, topological sort, connected components.

```java
void dfs(List<List<Integer>> adj, int u, boolean[] seen) {
    seen[u] = true;
    // visit(u);
    for (int v : adj.get(u))
        if (!seen[v]) dfs(adj, v, seen);
}
```

### Dijkstra — Single-Source Shortest Path (non-negative)
**Intuition:** Greedily settle the closest unfinished node from a min-heap, then relax its edges. **Fails with negative weights** because a settled node could later be improved.

```java
// edge = int[]{to, weight}; n = number of vertices
int[] dijkstra(List<List<int[]>> adj, int src, int n) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    // PQ orders by distance — the greedy heart of the algorithm
    PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> x[1] - y[1]);
    pq.add(new int[]{src, 0});
    while (!pq.isEmpty()) {
        int[] top = pq.poll();
        int u = top[0], d = top[1];
        if (d > dist[u]) continue;            // stale entry, skip
        for (int[] e : adj.get(u)) {
            int v = e[0], w = e[1];
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                pq.add(new int[]{v, dist[v]});
            }
        }
    }
    return dist;
}
```

### Bellman-Ford — SSSP with negative edges
**Intuition:** Relax *every* edge V−1 times; the longest shortest-path has at most V−1 edges. A V-th relaxation that still improves something means a **negative cycle** exists.

```java
// edges = int[]{u, v, w}
int[] bellmanFord(int[][] edges, int n, int src) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    for (int i = 1; i < n; i++)                       // V-1 passes
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v])
                dist[v] = dist[u] + w;
        }
    for (int[] e : edges)                             // V-th pass: detect cycle
        if (dist[e[0]] != Integer.MAX_VALUE && dist[e[0]] + e[2] < dist[e[1]])
            throw new IllegalStateException("Negative cycle detected");
    return dist;
}
```

### Floyd-Warshall — All-Pairs Shortest Path
**Intuition:** For every intermediate vertex `k`, ask "is going through `k` shorter?" Triple loop over (k, i, j). Handles negative edges (no negative cycles).

```java
void floydWarshall(int[][] d, int n) {   // d[i][j] = weight or INF; d[i][i] = 0
    for (int k = 0; k < n; k++)
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (d[i][k] != INF && d[k][j] != INF && d[i][k] + d[k][j] < d[i][j])
                    d[i][j] = d[i][k] + d[k][j];
}
```

### Kruskal — Minimum Spanning Tree (edge-centric)
**Intuition:** Sort all edges by weight; greedily add the cheapest edge that doesn't form a cycle (checked via Union-Find). Great for **sparse** graphs.

```java
int[] parent;
int find(int x) { return parent[x] == x ? x : (parent[x] = find(parent[x])); }
boolean union(int a, int b) {
    int ra = find(a), rb = find(b);
    if (ra == rb) return false;             // would form a cycle
    parent[ra] = rb; return true;
}
int kruskal(int[][] edges, int n) {         // edges = {u, v, w}
    parent = new int[n];
    for (int i = 0; i < n; i++) parent[i] = i;
    Arrays.sort(edges, (x, y) -> x[2] - y[2]);
    int total = 0, used = 0;
    for (int[] e : edges) {
        if (union(e[0], e[1])) { total += e[2]; used++; }
        if (used == n - 1) break;
    }
    return total;
}
```

### Prim — Minimum Spanning Tree (vertex-centric)
**Intuition:** Grow one tree from a start vertex; repeatedly pull the cheapest edge crossing the tree boundary from a **priority queue** (greedy). Good for **dense** graphs.

```java
// adj edge = int[]{to, weight}
int prim(List<List<int[]>> adj, int n) {
    boolean[] inTree = new boolean[n];
    PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> x[1] - y[1]); // {vertex, edgeWeight}
    pq.add(new int[]{0, 0});
    int total = 0, count = 0;
    while (!pq.isEmpty() && count < n) {
        int[] top = pq.poll();
        int u = top[0], w = top[1];
        if (inTree[u]) continue;            // already absorbed
        inTree[u] = true; total += w; count++;
        for (int[] e : adj.get(u))
            if (!inTree[e[0]]) pq.add(new int[]{e[0], e[1]});
    }
    return total;
}
```

**Dijkstra vs Prim (both greedy + PQ):** Dijkstra's key is *distance from source* (`dist[u] + w`); Prim's key is *the single crossing-edge weight* (`w`). Same machinery, different priority.

---

<a id="ref-strings"></a>

## 🔤 String Matching

| Algorithm   | Preprocess | Match    | One-line intuition / when-to-use                          |
|-------------|------------|----------|-----------------------------------------------------------|
| KMP         | O(m)       | O(n)     | Skip re-checks using a failure table. Single pattern, worst-case linear. |
| Rabin-Karp  | O(m)       | O(n) avg | Hash the window; compare hashes. Best for **multiple** patterns / plagiarism. |
| Z-algorithm | O(n+m)     | O(n+m)   | Z-array = longest prefix match at each index. Clean linear matching + string analysis. |
| Manacher    | —          | O(n)     | Longest **palindromic** substring in linear time.         |

(`n` = text length, `m` = pattern length.)

### KMP — Knuth-Morris-Pratt
**Intuition:** When a mismatch happens, don't restart — use a precomputed **LPS** table to skip characters you already know match.
**When:** Single-pattern exact match where you need guaranteed O(n+m), no hashing.

**The LPS (failure) function — the heart of KMP.** `lps[i]` = length of the longest proper prefix of `pat[0..i]` that is also a suffix. On a mismatch at pattern index `j`, instead of moving back in the *text*, you jump the pattern pointer to `lps[j-1]` — reusing the matched prefix.

```
pat = "ababaca"
idx:   0 1 2 3 4 5 6
lps:   0 0 1 2 3 0 1
                 ^ "abab": prefix "ab" == suffix "ab" -> 2
```

```java
int[] buildLps(String p) {
    int[] lps = new int[p.length()];
    int len = 0;                          // length of current longest prefix-suffix
    for (int i = 1; i < p.length(); ) {
        if (p.charAt(i) == p.charAt(len)) lps[i++] = ++len;
        else if (len > 0) len = lps[len - 1];   // fall back, don't reset i
        else lps[i++] = 0;
    }
    return lps;
}

int kmpSearch(String t, String p) {
    int[] lps = buildLps(p);
    int i = 0, j = 0;                     // i->text, j->pattern
    while (i < t.length()) {
        if (t.charAt(i) == p.charAt(j)) { i++; j++;
            if (j == p.length()) return i - j;   // match found
        } else if (j > 0) j = lps[j - 1];        // reuse prefix
        else i++;
    }
    return -1;
}
```

### Rabin-Karp
**Intuition:** Roll a hash over each window of length `m`; only do a full char compare when hashes collide.
**When:** Searching for **many** patterns at once, or detecting duplicates/plagiarism. Risk: hash collisions (mitigate with a large prime modulus).

```java
int rabinKarp(String t, String p) {
    int n = t.length(), m = p.length();
    if (m > n) return -1;
    long MOD = 1_000_000_007L, BASE = 256, ph = 0, th = 0, pow = 1;
    for (int i = 0; i < m; i++) {
        ph = (ph * BASE + p.charAt(i)) % MOD;
        th = (th * BASE + t.charAt(i)) % MOD;
        if (i < m - 1) pow = pow * BASE % MOD;     // BASE^(m-1)
    }
    for (int i = 0; i + m <= n; i++) {
        if (ph == th && t.substring(i, i + m).equals(p)) return i;  // verify on hash hit
        if (i + m < n) {                            // roll: drop left char, add right
            th = ((th - t.charAt(i) * pow % MOD + MOD) % MOD * BASE
                  + t.charAt(i + m)) % MOD;
        }
    }
    return -1;
}
```

### Z-Algorithm
**Intuition:** Build a **Z-array** where `Z[i]` = length of the longest substring starting at `i` that matches a prefix of the string. Search by running Z over `pattern + separator + text`.
**When:** Linear exact matching, counting occurrences, and prefix-based string analysis (e.g., string periodicity).

```java
int[] zArray(String s) {
    int n = s.length();
    int[] z = new int[n];
    int l = 0, r = 0;                     // [l, r] = current rightmost Z-box
    for (int i = 1; i < n; i++) {
        if (i < r) z[i] = Math.min(r - i, z[i - l]);   // reuse inside the box
        while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
        if (i + z[i] > r) { l = i; r = i + z[i]; }      // extend the box
    }
    return z;
}
// Usage: zArray(p + "\u0001" + t); any z[i] == p.length() is a match.
```

### Manacher
**Intuition:** Find the **longest palindromic substring** in O(n) by transforming the string (insert separators so even/odd lengths unify) and expanding palindromes while mirroring previously computed radii.
**When:** Anything about longest/all palindromic substrings where O(n²) center-expansion is too slow.

```java
String longestPalindrome(String s) {
    if (s.isEmpty()) return "";
    StringBuilder sb = new StringBuilder("^");          // sentinels avoid bounds checks
    for (char c : s.toCharArray()) sb.append('#').append(c);
    sb.append("#$");
    char[] t = sb.toString().toCharArray();
    int[] p = new int[t.length];
    int center = 0, right = 0;
    for (int i = 1; i < t.length - 1; i++) {
        if (i < right) p[i] = Math.min(right - i, p[2 * center - i]);  // mirror
        while (t[i + p[i] + 1] == t[i - p[i] - 1]) p[i]++;             // expand
        if (i + p[i] > right) { center = i; right = i + p[i]; }
    }
    int maxLen = 0, centerIdx = 0;
    for (int i = 1; i < p.length - 1; i++)
        if (p[i] > maxLen) { maxLen = p[i]; centerIdx = i; }
    int start = (centerIdx - maxLen) / 2;               // map back to original index
    return s.substring(start, start + maxLen);
}
```

---

### 🧭 Quick decision guide
- **Sorted numeric, uniform?** Interpolation. **Sorted, general?** Binary. **Unsorted/tiny?** Linear.
- **Unweighted shortest path?** BFS. **Weighted, non-negative?** Dijkstra. **Negative edges?** Bellman-Ford. **All pairs, small V?** Floyd-Warshall.
- **MST sparse?** Kruskal. **MST dense?** Prim.
- **One pattern, guaranteed linear?** KMP. **Many patterns / fingerprints?** Rabin-Karp. **Prefix analysis?** Z. **Palindromes?** Manacher.

---
