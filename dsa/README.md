# 🧠 DSA Pattern Playbook — Visual Interview Prep (Java)

A **frequency-ordered, visual-first** playbook of coding-interview patterns for product-based companies.
Every subpattern has: **🔍 Recognize · 📊 Visual trace · 🧠 Idea + template · 📝 Example · ⏱️ Complexity · 💡 Tip.**

## 🏆 Study order (by interview frequency)
- **🔥 Tier 1 (must-know):** Hashing (Freq/Seen) · Prefix Sum · Two Pointers · Sliding Window · Binary Search · Stack (Parens/Monotonic) · Linked List (Reverse/Merge) · Trees (BFS/DFS/LCA) · Heap (Top-K) · Backtracking (Subsets/Perms/Combo) · Graphs (Components/Topo/Cycle) · DP (Fib/Kadane/Coin/Knapsack/LCS/Paths) · Greedy (Intervals/Stock) · Strings (Anagram/Palindrome) · Bit (XOR)
- **🟧 Tier 2 (common):** Cyclic Sort · Rotation · Product-Except-Self · Merge-from-End · Min Stack · RPN · Largest Rectangle · Monotonic Queue · Median of Two · K-way Merge · Two-Heaps · Union-Find · Dijkstra · Clone · Serialize · Gen Parens · Word Search · Jump/Gas/Task · Word Break · Edit Distance · LIS · Hamming/Power-of-2/Counting Bits · Roman
- **🟦 Tier 3 (rare/advanced):** Spiral · Set Matrix Zeroes · Multiply Strings · KMP/Rabin-Karp · N-Queens · Bellman-Ford · Catalan · Interval DP · Segment Tree · Fenwick (BIT)

## 📚 Sections (in this order)
1. Arrays & Hashing · 2. Two Pointers · 3. Sliding Window · 4. Binary Search · 5. Stack ·
6. Linked List · 7. Trees · 8. Heap · 9. Graphs · 10. Backtracking · 11. DP ·
12. Greedy · 13. Strings · 14. Bit Manipulation · 15. Segment/Fenwick Tree

> 💻 Solve order per pattern: **recognize the cue → picture the visual → recall the template → solve a classic → drill 1-2 related.**

---

<a id="index"></a>

## 🧭 Navigation & Practice Index

**Legend** — Difficulty: 🟩 Easy · 🟨 Med · 🟥 Hard | Asked: 🔥 High · 🟧 Med · 🟦 Low | Forget-risk: 🟢 Low · 🟡 Med · 🔴 High

_📖 subpattern link = learn in this file · ✍️ = open the practice folder._

**🧪 Run a question's tests:** from `dsa/` → `bash run-tests.sh <category>/<subpattern>` (e.g. `bash run-tests.sh 01-arrays-hashing/02-hashing-previously-seen-existence-check`), or inside the folder → `javac Answer.java Test.java && java Test`. Each `Test.java` runs many **corner cases** against your `Answer` and prints `[PASS]`/`[FAIL]` per case + `Summary: X/Y passed`. (Unimplemented stubs fail gracefully — no crash.)

**📌 [Java Reference — Arrays · Strings · Collections (every method + example)](REFERENCE.md#cheat)** — CRUD · methods · loops · gotchas.

**🌳 [Trees & Algorithms — Reference](REFERENCE.md#trees-algos)** — taxonomy · heap · traversals · paradigms · sorting & searching/graph/string algos (with code).


### 1. [Arrays & Hashing (Array/Matrix Manipulation)](#arrays--hashing-arraymatrix-manipulation)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Hashing - Frequency Map / Counting](#hashing---frequency-map--counting) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](01-arrays-hashing/01-hashing-frequency-map-counting/QUESTION.md) |
| [Hashing - Previously Seen / Existence Check](#hashing---previously-seen--existence-check) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](01-arrays-hashing/02-hashing-previously-seen-existence-check/QUESTION.md) |
| [Prefix Sum - Subarray Sum / Range Query](#prefix-sum---subarray-sum--range-query) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](01-arrays-hashing/03-prefix-sum-subarray-sum-range-query/QUESTION.md) |
| [Product Except Self (Prefix/Suffix Products)](#product-except-self-prefixsuffix-products) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](01-arrays-hashing/04-product-except-self-prefix-suffix-products/QUESTION.md) |
| [Array - Cyclic Sort](#array---cyclic-sort) | 🟨 Med | 🟧 Med | 🔴 High | [✍️](01-arrays-hashing/05-array-cyclic-sort/QUESTION.md) |
| [In-place Rotation](#in-place-rotation) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](01-arrays-hashing/06-in-place-rotation/QUESTION.md) |
| [Merge Sorted Array (In-place from End)](#merge-sorted-array-in-place-from-end) | 🟩 Easy | 🟧 Med | 🟡 Med | [✍️](01-arrays-hashing/07-merge-sorted-array-in-place-from-end/QUESTION.md) |
| [Plus One (Handling Carry)](#plus-one-handling-carry) | 🟩 Easy | 🟧 Med | 🟢 Low | [✍️](01-arrays-hashing/08-plus-one-handling-carry/QUESTION.md) |
| [Set Matrix Zeroes (In-place Marking)](#set-matrix-zeroes-in-place-marking) | 🟨 Med | 🟧 Med | 🔴 High | [✍️](01-arrays-hashing/09-set-matrix-zeroes-in-place-marking/QUESTION.md) |
| [Spiral Traversal](#spiral-traversal) | 🟨 Med | 🟦 Low | 🔴 High | [✍️](01-arrays-hashing/10-spiral-traversal/QUESTION.md) |
| [Multiply Strings (Manual Simulation)](#multiply-strings-manual-simulation) | 🟨 Med | 🟦 Low | 🔴 High | [✍️](01-arrays-hashing/11-multiply-strings-manual-simulation/QUESTION.md) |

### 2. [Two Pointers](#two-pointers)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Converging (Sorted Array Target Sum)](#converging-sorted-array-target-sum) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](02-two-pointers/01-converging-sorted-array-target-sum/QUESTION.md) |
| [Fast & Slow (Cycle Detection)](#fast--slow-cycle-detection) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](02-two-pointers/02-fast-slow-cycle-detection/QUESTION.md) |
| [Fixed Separation (Nth from End)](#fixed-separation-nth-from-end) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](02-two-pointers/03-fixed-separation-nth-from-end/QUESTION.md) |
| [Expanding From Center (Palindromes)](#expanding-from-center-palindromes) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](02-two-pointers/04-expanding-from-center-palindromes/QUESTION.md) |
| [In-place Array Modification](#in-place-array-modification) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](02-two-pointers/05-in-place-array-modification/QUESTION.md) |
| [String Comparison with Backspaces](#string-comparison-with-backspaces) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](02-two-pointers/06-string-comparison-with-backspaces/QUESTION.md) |
| [String Reversal](#string-reversal) | 🟩 Easy | 🟧 Med | 🟢 Low | [✍️](02-two-pointers/07-string-reversal/QUESTION.md) |

### 3. [Sliding Window](#sliding-window)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Fixed Size](#fixed-size) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](03-sliding-window/01-fixed-size/QUESTION.md) |
| [Variable Size](#variable-size) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](03-sliding-window/02-variable-size/QUESTION.md) |
| [Character Frequency Matching](#character-frequency-matching) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](03-sliding-window/03-character-frequency-matching/QUESTION.md) |
| [Monotonic Queue for Max/Min](#monotonic-queue-for-maxmin) | 🟥 Hard | 🟧 Med | 🔴 High | [✍️](03-sliding-window/04-monotonic-queue-for-max-min/QUESTION.md) |

### 4. [Binary Search](#binary-search)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [On Sorted Array/List](#on-sorted-arraylist) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](04-binary-search/01-on-sorted-array-list/QUESTION.md) |
| [Find First/Last Occurrence](#find-firstlast-occurrence) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](04-binary-search/02-find-first-last-occurrence/QUESTION.md) |
| [On Answer / Condition Function](#on-answer--condition-function) | 🟥 Hard | 🔥 High | 🔴 High | [✍️](04-binary-search/03-on-answer-condition-function/QUESTION.md) |
| [Find Min/Max in Rotated Sorted Array](#find-minmax-in-rotated-sorted-array) | 🟨 Med | 🔥 High | 🔴 High | [✍️](04-binary-search/04-find-min-max-in-rotated-sorted-array/QUESTION.md) |
| [Median and Kth of Two Sorted Arrays](#median-and-kth-of-two-sorted-arrays) | 🟥 Hard | 🟧 Med | 🔴 High | [✍️](04-binary-search/05-median-and-kth-of-two-sorted-arrays/QUESTION.md) |

### 5. [Stack](#stack)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Valid Parentheses](#valid-parentheses) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](05-stack/01-valid-parentheses/QUESTION.md) |
| [Monotonic Stack](#monotonic-stack) | 🟥 Hard | 🔥 High | 🔴 High | [✍️](05-stack/02-monotonic-stack/QUESTION.md) |
| [Min Stack Design](#min-stack-design) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](05-stack/03-min-stack-design/QUESTION.md) |
| [Expression Evaluation (RPN/Infix)](#expression-evaluation-rpninfix) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](05-stack/04-expression-evaluation-rpn-infix/QUESTION.md) |
| [Largest Rectangle in Histogram](#largest-rectangle-in-histogram) | 🟥 Hard | 🟧 Med | 🔴 High | [✍️](05-stack/05-largest-rectangle-in-histogram/QUESTION.md) |
| [Simulation/Backtracking Helper](#simulationbacktracking-helper) | 🟨 Med | 🟦 Low | 🟡 Med | [✍️](05-stack/06-simulation-backtracking-helper/QUESTION.md) |

### 6. [Linked List](#linked-list)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [In-place Reversal](#in-place-reversal) | 🟩 Easy | 🔥 High | 🟡 Med | [✍️](06-linked-list/01-in-place-reversal/QUESTION.md) |
| [Merging Two Sorted Lists](#merging-two-sorted-lists) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](06-linked-list/02-merging-two-sorted-lists/QUESTION.md) |
| [Addition of Numbers](#addition-of-numbers) | 🟨 Med | 🟧 Med | 🟢 Low | [✍️](06-linked-list/03-addition-of-numbers/QUESTION.md) |
| [Reordering/Partitioning](#reorderingpartitioning) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](06-linked-list/04-reordering-partitioning/QUESTION.md) |

### 7. [Tree Traversal (DFS and BFS)](#tree-traversal-dfs-and-bfs)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Level Order Traversal (BFS)](#level-order-traversal-bfs) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](07-trees/01-level-order-traversal-bfs/QUESTION.md) |
| [DFS Overview (Pre/In/Post)](#dfs-overview-preinpost) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](07-trees/02-dfs-overview-pre-in-post/QUESTION.md) |
| [Recursive Preorder](#recursive-preorder) | 🟩 Easy | 🟧 Med | 🟢 Low | [✍️](07-trees/03-recursive-preorder/QUESTION.md) |
| [Recursive Inorder](#recursive-inorder) | 🟩 Easy | 🟧 Med | 🟢 Low | [✍️](07-trees/04-recursive-inorder/QUESTION.md) |
| [Recursive Postorder](#recursive-postorder) | 🟩 Easy | 🟧 Med | 🟢 Low | [✍️](07-trees/05-recursive-postorder/QUESTION.md) |
| [Lowest Common Ancestor (LCA)](#lowest-common-ancestor-lca) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](07-trees/06-lowest-common-ancestor-lca/QUESTION.md) |
| [Serialization and Deserialization](#serialization-and-deserialization) | 🟥 Hard | 🟧 Med | 🔴 High | [✍️](07-trees/07-serialization-and-deserialization/QUESTION.md) |

### 8. [Heap (Priority Queue)](#heap-priority-queue)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Top K Elements (Selection/Frequency)](#top-k-elements-selectionfrequency) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](08-heap/01-top-k-elements-selection-frequency/QUESTION.md) |
| [K-way Merge](#k-way-merge) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](08-heap/02-k-way-merge/QUESTION.md) |
| [Two Heaps for Median](#two-heaps-for-median) | 🟥 Hard | 🟧 Med | 🔴 High | [✍️](08-heap/03-two-heaps-for-median/QUESTION.md) |
| [Scheduling / Minimum Cost (Greedy + PQ)](#scheduling--minimum-cost-greedy--pq) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](08-heap/04-scheduling-minimum-cost-greedy-pq/QUESTION.md) |

### 9. [Graph Traversal (DFS and BFS)](#graph-traversal-dfs-and-bfs)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [DFS/BFS Connected Components](#dfsbfs-connected-components) | 🟨 Med | 🔥 High | 🟢 Low | [✍️](09-graphs/01-dfs-bfs-connected-components/QUESTION.md) |
| [Kahn Topological Sort](#kahn-topological-sort) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](09-graphs/02-kahn-topological-sort/QUESTION.md) |
| [DFS Cycle Detection (Directed)](#dfs-cycle-detection-directed) | 🟨 Med | 🔥 High | 🔴 High | [✍️](09-graphs/03-dfs-cycle-detection-directed/QUESTION.md) |
| [Union-Find (Disjoint Set Union)](#union-find-disjoint-set-union) | 🟨 Med | 🟧 Med | 🔴 High | [✍️](09-graphs/04-union-find-disjoint-set-union/QUESTION.md) |
| [Dijkstra Algorithm](#dijkstra-algorithm) | 🟥 Hard | 🟧 Med | 🔴 High | [✍️](09-graphs/05-dijkstra-algorithm/QUESTION.md) |
| [Shortest Path (BFS/Bellman-Ford)](#shortest-path-bfsbellman-ford) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](09-graphs/06-shortest-path-bfs-bellman-ford/QUESTION.md) |
| [Deep Copy / Cloning](#deep-copy--cloning) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](09-graphs/07-deep-copy-cloning/QUESTION.md) |

### 10. [Backtracking](#backtracking)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Subsets (Include/Exclude)](#subsets-includeexclude) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](10-backtracking/01-subsets-include-exclude/QUESTION.md) |
| [Permutations](#permutations) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](10-backtracking/02-permutations/QUESTION.md) |
| [Combination Sum](#combination-sum) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](10-backtracking/03-combination-sum/QUESTION.md) |
| [Parentheses Generation](#parentheses-generation) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](10-backtracking/04-parentheses-generation/QUESTION.md) |
| [Palindrome Partitioning](#palindrome-partitioning) | 🟨 Med | 🟧 Med | 🔴 High | [✍️](10-backtracking/05-palindrome-partitioning/QUESTION.md) |
| [Word Search / Path in Grid](#word-search--path-in-grid) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](10-backtracking/06-word-search-path-in-grid/QUESTION.md) |
| [N-Queens](#n-queens) | 🟥 Hard | 🟦 Low | 🔴 High | [✍️](10-backtracking/07-n-queens/QUESTION.md) |

### 11. [Dynamic Programming](#dynamic-programming)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [1D Fibonacci Style](#1d-fibonacci-style) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](11-dynamic-programming/01-1d-fibonacci-style/QUESTION.md) |
| [1D Kadane Algorithm](#1d-kadane-algorithm) | 🟩 Easy | 🔥 High | 🟡 Med | [✍️](11-dynamic-programming/02-1d-kadane-algorithm/QUESTION.md) |
| [1D Coin Change / Unbounded Knapsack](#1d-coin-change--unbounded-knapsack) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](11-dynamic-programming/03-1d-coin-change-unbounded-knapsack/QUESTION.md) |
| [1D 0/1 Knapsack](#1d-01-knapsack) | 🟨 Med | 🔥 High | 🔴 High | [✍️](11-dynamic-programming/04-1d-0-1-knapsack/QUESTION.md) |
| [1D Word Break](#1d-word-break) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](11-dynamic-programming/05-1d-word-break/QUESTION.md) |
| [2D Longest Common Subsequence](#2d-longest-common-subsequence) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](11-dynamic-programming/06-2d-longest-common-subsequence/QUESTION.md) |
| [2D Unique Paths on Grid](#2d-unique-paths-on-grid) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](11-dynamic-programming/07-2d-unique-paths-on-grid/QUESTION.md) |
| [2D Edit Distance](#2d-edit-distance) | 🟥 Hard | 🟧 Med | 🔴 High | [✍️](11-dynamic-programming/08-2d-edit-distance/QUESTION.md) |
| [Longest Increasing Subsequence (LIS)](#longest-increasing-subsequence-lis) | 🟨 Med | 🟧 Med | 🔴 High | [✍️](11-dynamic-programming/09-longest-increasing-subsequence-lis/QUESTION.md) |
| [Catalan Numbers](#catalan-numbers) | 🟨 Med | 🟦 Low | 🔴 High | [✍️](11-dynamic-programming/10-catalan-numbers/QUESTION.md) |
| [Interval DP](#interval-dp) | 🟥 Hard | 🟦 Low | 🔴 High | [✍️](11-dynamic-programming/11-interval-dp/QUESTION.md) |

### 12. [Greedy](#greedy)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Interval Merging/Scheduling](#interval-mergingscheduling) | 🟨 Med | 🔥 High | 🟡 Med | [✍️](12-greedy/01-interval-merging-scheduling/QUESTION.md) |
| [Buy/Sell Stock](#buysell-stock) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](12-greedy/02-buy-sell-stock/QUESTION.md) |
| [Jump Game](#jump-game) | 🟨 Med | 🟧 Med | 🟡 Med | [✍️](12-greedy/03-jump-game/QUESTION.md) |
| [Gas Station Circuit](#gas-station-circuit) | 🟨 Med | 🟧 Med | 🔴 High | [✍️](12-greedy/04-gas-station-circuit/QUESTION.md) |
| [Task Scheduling (Frequency)](#task-scheduling-frequency) | 🟨 Med | 🟧 Med | 🔴 High | [✍️](12-greedy/05-task-scheduling-frequency/QUESTION.md) |

### 13. [String Manipulation](#string-manipulation)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Anagram Check (Frequency Count/Sort)](#anagram-check-frequency-countsort) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](13-strings/01-anagram-check-frequency-count-sort/QUESTION.md) |
| [Palindrome Check (Two Pointers / Reverse)](#palindrome-check-two-pointers--reverse) | 🟩 Easy | 🔥 High | 🟢 Low | [✍️](13-strings/02-palindrome-check-two-pointers-reverse/QUESTION.md) |
| [Roman to Integer](#roman-to-integer) | 🟩 Easy | 🟧 Med | 🟢 Low | [✍️](13-strings/03-roman-to-integer/QUESTION.md) |
| [Integer and Roman Conversion](#integer-and-roman-conversion) | 🟩 Easy | 🟧 Med | 🟡 Med | [✍️](13-strings/04-integer-and-roman-conversion/QUESTION.md) |
| [Repeated Substring Pattern Detection](#repeated-substring-pattern-detection) | 🟨 Med | 🟦 Low | 🔴 High | [✍️](13-strings/05-repeated-substring-pattern-detection/QUESTION.md) |
| [Naive/KMP/Rabin-Karp (overview)](#naivekmprabin-karp-overview) | 🟥 Hard | 🟦 Low | 🔴 High | [✍️](13-strings/06-naive-kmp-rabin-karp-overview/QUESTION.md) |

### 14. [Bit Manipulation](#bit-manipulation)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [XOR (Single/Missing)](#xor-singlemissing) | 🟩 Easy | 🔥 High | 🟡 Med | [✍️](14-bit-manipulation/01-xor-single-missing/QUESTION.md) |
| [Bitwise AND (Hamming Weight)](#bitwise-and-hamming-weight) | 🟩 Easy | 🟧 Med | 🟢 Low | [✍️](14-bit-manipulation/02-bitwise-and-hamming-weight/QUESTION.md) |
| [Power of Two/Four](#power-of-twofour) | 🟩 Easy | 🟧 Med | 🟡 Med | [✍️](14-bit-manipulation/03-power-of-two-four/QUESTION.md) |
| [Bitwise DP (Counting Bits Optimization)](#bitwise-dp-counting-bits-optimization) | 🟨 Med | 🟦 Low | 🟡 Med | [✍️](14-bit-manipulation/04-bitwise-dp-counting-bits-optimization/QUESTION.md) |

### 15. [Segment Tree and Fenwick Tree](#segment-tree-and-fenwick-tree)

| Subpattern | Difficulty | Asked | Forget-risk | Solve |
|---|---|---|---|---|
| [Fenwick Tree (BIT) - Prefix Queries / Inversions](#fenwick-tree-bit---prefix-queries--inversions) | 🟥 Hard | 🟦 Low | 🔴 High | [✍️](15-segment-fenwick-tree/01-fenwick-tree-bit-prefix-queries-inversions/QUESTION.md) |
| [Segment Tree - Range Sum with Point Update](#segment-tree---range-sum-with-point-update) | 🟥 Hard | 🟦 Low | 🔴 High | [✍️](15-segment-fenwick-tree/02-segment-tree-range-sum-with-point-update/QUESTION.md) |

---

## Arrays & Hashing (Array/Matrix Manipulation)

### Hashing - Frequency Map / Counting

[🔝 Back to index](#index)

- **🔍 Recognize:** "count occurrences", "most/least frequent", "anagram", "appears exactly k times", "duplicate within group", "can rearrange into". You need to know *how many* of each value/char exist.
- **📊 Visual:**
```
Count chars in "leetcode"  →  HashMap<Char,Int>

  l e e t c o d e
  │ │ │ │ │ │ │ │
  ▼ ▼ ▼ ▼ ▼ ▼ ▼ ▼
 {l:1}
 {l:1, e:1}
 {l:1, e:2}        ← 'e' seen again, bump count
 {l:1, e:2, t:1}
 ... continue ...
 final → { l:1, e:3, t:1, c:1, o:1, d:1 }
                        ▲ first char with freq 1 reading left→right? = 'l'
```
- **🧠 Idea + template:** One pass to tally, second pass to query. For 26 lowercase letters, an `int[26]` beats a HashMap.
```java
Map<Character, Integer> freq = new HashMap<>();
for (char c : s.toCharArray())
    freq.merge(c, 1, Integer::sum);   // count++ or init to 1

// fixed alphabet variant
int[] count = new int[26];
for (char c : s.toCharArray()) count[c - 'a']++;
```
- **📝 Example:** *Valid Anagram* — build freq of `s`, decrement with chars of `t`; anagram iff every count ends at 0 (and lengths match).
- **⏱️ Complexity:** Time O(n), Space O(k) where k = distinct keys (O(1) for fixed alphabet).
- **💡 Remember-it tip:** "Tally first, ask later." Drill: *Group Anagrams* (sorted-string key), *Top K Frequent Elements* (bucket sort by frequency).

### Hashing - Previously Seen / Existence Check

[🔝 Back to index](#index)

- **🔍 Recognize:** "has duplicate", "two numbers that sum to target", "complement", "first repeating", "contains nearby duplicate". You need O(1) "have I seen X before?" lookups.
- **📊 Visual:**
```
Two Sum: nums=[2,7,11,15], target=9
seen = {}   (value → index)

i=0 v=2  need 9-2=7 ? not in seen   → put 2→0   seen={2:0}
i=1 v=7  need 9-7=2 ? YES in seen!  → answer [seen[2], 1] = [0,1]
                                       ▲ found complement, stop
```
- **🧠 Idea + template:** Trade space for time — store what you've seen in a Set/Map, and on each element check for the partner/complement *before* inserting.
```java
Set<Integer> seen = new HashSet<>();
for (int x : nums) {
    if (seen.contains(x)) return true;  // duplicate
    seen.add(x);
}
// Two Sum: Map<Integer,Integer> idx; check idx.containsKey(target - x) first
```
- **📝 Example:** *Two Sum* — for each `x`, if `target-x` was seen, return both indices; else record `x→i`. Single pass.
- **⏱️ Complexity:** Time O(n), Space O(n).
- **💡 Remember-it tip:** "Check before you store." Drill: *Contains Duplicate*, *Longest Consecutive Sequence* (use a Set, only start counting at sequence heads where `x-1` is absent).

#### 📚 Deep dive — worked examples

**Golden rule — "Check before you store":** for each element, *first* ask "is the thing I need already in my Set/Map?", *then* add the current element. If you store first, you'd always "find" the element you just inserted.

**Set vs Map — pick by *what you need to remember*:**
```
just "have I seen this value?"   → Set  (existence only)     → Contains Duplicate
need INFO about the seen value   → Map  (value→index/count)  → Two Sum, Nearby Duplicate
```

**① Contains Duplicate (Set)** — *does any value appear twice?*
```java
Set<Integer> seen = new HashSet<>();
for (int x : nums) {
    if (seen.contains(x)) return true;   // CHECK first
    seen.add(x);                         // then STORE
}
return false;
```
```
nums = [1, 2, 3, 1]
x=1  seen 1? no  → add → {1}
x=2  seen 2? no  → add → {1,2}
x=3  seen 3? no  → add → {1,2,3}
x=1  seen 1? YES → return true ✅
```
*Why check-first:* if you add first, every element "looks" duplicate (you just inserted it).

**② Two Sum (Map: value → index)** — need the complement's *index*, so a Map.
```java
Map<Integer,Integer> seen = new HashMap<>();      // value → index
for (int i = 0; i < nums.length; i++) {
    int need = target - nums[i];
    if (seen.containsKey(need)) return new int[]{ seen.get(need), i };
    seen.put(nums[i], i);
}
```
```
nums = [2,7,11,15], target = 9
i=0 x=2  need 7 → seen has 7? no  → put 2→0   seen={2:0}
i=1 x=7  need 2 → seen has 2? YES at 0 → return [0,1] ✅
```
*Map, not Set,* because we must return the index of the complement. *Check-first* also stops us pairing an element with itself.

**③ Contains Nearby Duplicate (Map: value → last index)** — equal values within distance `k`.
```java
Map<Integer,Integer> last = new HashMap<>();      // value → last index seen
for (int i = 0; i < nums.length; i++) {
    if (last.containsKey(nums[i]) && i - last.get(nums[i]) <= k) return true;
    last.put(nums[i], i);
}
```
```
nums = [1,2,3,1], k = 3
i=0 v=1 → {1:0}
i=1 v=2 → {1:0,2:1}
i=2 v=3 → {1:0,2:1,3:2}
i=3 v=1 seen at 0 → |3-0|=3 ≤ 3 → true ✅
```

#### 🧩 Hard drill — Longest Consecutive Sequence (Set + "head" trick), O(n)

*Longest run of consecutive ints (`n, n+1, n+2 …`), order doesn't matter — without sorting.*

**The trap:** counting a streak from *every* number re-counts the same run:
```
run 1,2,3,4 counted from each → 4+3+2+1 = O(n²) 🐢
```

**The insight — only count from the HEAD of a run.** `x` is a head **iff `x-1` is NOT in the set**:
```
x-1 NOT in set → the run starts at x (HEAD) → count forward ✅
x-1 IS  in set → x is inside a run          → SKIP ⏭️

   1 → 2 → 3 → 4
   ▲ head (0 absent) — count forward until it breaks
   2,3,4 each have a left-neighbor in the set → skip
```
Each run is counted **exactly once** → each element touched ≤ 2× → **O(n)**.

```java
Set<Integer> set = new HashSet<>();
for (int x : nums) set.add(x);

int longest = 0;
for (int x : set) {
    if (!set.contains(x - 1)) {            // x is a HEAD (no left-neighbor)
        int cur = x, length = 1;
        while (set.contains(cur + 1)) {    // walk the run forward
            cur++; length++;
        }
        longest = Math.max(longest, length);
    }
}
return longest;
```
```
nums=[100,4,200,1,3,2]   set={100,4,200,1,3,2}
x=100: 99 in set? no  → HEAD → 100, 101? no → len 1
x=4  : 3  in set? yes → SKIP
x=200: 199? no        → HEAD → len 1
x=1  : 0  in set? no  → HEAD → 1,2,3,4, 5? no → len 4 ✅
x=3,2: predecessor in set → SKIP
answer = 4
```
```
WITHOUT head-check: count from every number → O(n²) 🐢
WITH    head-check: count each run once      → O(n)  🚀
```

### Prefix Sum - Subarray Sum / Range Query

[🔝 Back to index](#index)

- **🔍 Recognize:** "sum of subarray", "range sum query (immutable)", "subarray summing to k", "continuous subarray", "equal number of 0s and 1s". Repeated range-sum questions over a static array.
- **📊 Visual:**
```
nums   =   [ 2,  4,  1,  3,  5 ]
prefix = [0, 2,  6,  7, 10, 15 ]   prefix[i] = sum of first i elems
            0  1   2   3   4   5

Range sum of indices [1..3] (4+1+3 = 8):
   prefix[4] - prefix[1] = 10 - 2 = 8  ✓
            └──────┘
   "sum up to 4" minus "sum up to 1"

Count subarrays summing to k=7  (map: prefixSum → how many times seen)
   walk sum; at each step ask: have I seen (sum - k)?
   if yes, every such earlier index closes a valid subarray.
```
- **🧠 Idea + template:** `sum(i..j) = prefix[j+1] - prefix[i]`. For "count subarrays == k", hash running sums so a match is O(1).
```java
// Subarray Sum Equals K
Map<Integer,Integer> seen = new HashMap<>();
seen.put(0, 1);                 // empty prefix
int sum = 0, res = 0;
for (int x : nums) {
    sum += x;
    res += seen.getOrDefault(sum - k, 0);
    seen.merge(sum, 1, Integer::sum);
}
```
- **📝 Example:** *Subarray Sum Equals K* — running sum + map of prefix counts; add `count[sum-k]` each step. O(n).
- **⏱️ Complexity:** Time O(n) build / O(1) per range query; Space O(n).
- **💡 Remember-it tip:** "Difference of two prefixes = a range." Seed the map with `{0:1}`. Drill: *Range Sum Query - Immutable*, *Contiguous Array* (map 0→-1).

### Product Except Self (Prefix/Suffix Products)

[🔝 Back to index](#index)

- **🔍 Recognize:** "product of all elements except self", "without using division", "O(n) and no division". The output at `i` depends on everything *but* `i`.
- **📊 Visual:**
```
nums   = [ 1,  2,  3,  4 ]

prefix (product of everything to the LEFT):
 res =   [ 1,  1,  2,  6 ]     res[i] = nums[0..i-1] product
          ▲left of 0 is empty→1

now sweep RIGHT→LEFT multiplying a running suffix product R:
 i=3  R=1   res[3]=6*1 =6    then R*=4 → R=4
 i=2  R=4   res[2]=2*4 =8    then R*=3 → R=12
 i=1  R=12  res[1]=1*12=12   then R*=2 → R=24
 i=0  R=24  res[0]=1*24=24   then R*=1 → R=24

answer = [24, 12, 8, 6]
```
- **🧠 Idea + template:** `answer[i] = (product of all left) * (product of all right)`. Two sweeps, O(1) extra space (output array doesn't count).
```java
int n = nums.length;
int[] res = new int[n];
res[0] = 1;
for (int i = 1; i < n; i++) res[i] = res[i-1] * nums[i-1]; // left products
int right = 1;
for (int i = n - 1; i >= 0; i--) {
    res[i] *= right;        // combine with right product
    right  *= nums[i];
}
```
- **📝 Example:** *Product of Array Except Self* — fill `res` with prefix products, then multiply in suffix products on a reverse pass. No division, O(n).
- **⏱️ Complexity:** Time O(n), Space O(1) extra (besides the output).
- **💡 Remember-it tip:** "Left pass writes, right pass multiplies." Drill: *Maximum Product Subarray* (track min & max because negatives flip).

### Array - Cyclic Sort

[🔝 Back to index](#index)

- **🔍 Recognize:** Numbers are in a known bounded range, typically `1..n` or `0..n-1`. "Find the missing number", "find the duplicate", "find all disappeared numbers", "first missing positive". Value ⇄ index correspondence.
- **📊 Visual:**
```
Place each value at index value-1.   nums = [3, 1, 5, 4, 2]

i=0 v=3 → belongs at idx2. swap(0,2):  [5,1,3,4,2]
i=0 v=5 → belongs at idx4. swap(0,4):  [2,1,3,4,5]
i=0 v=2 → belongs at idx1. swap(0,1):  [1,2,3,4,5]
i=0 v=1 → already at idx0 (1-1). i++
i=1..4 all in place →  [1,2,3,4,5]   sorted in O(n)!

  idx:  0  1  2  3  4
  val:  1  2  3  4  5      val == idx+1 everywhere ✓
```
- **🧠 Idea + template:** Each correctly-placed swap fixes one element forever, so total swaps ≤ n. After sorting, any `nums[i] != i+1` reveals the missing/duplicate.
```java
int i = 0;
while (i < nums.length) {
    int correct = nums[i] - 1;                  // target index for nums[i]
    if (nums[i] != nums[correct]) {             // not yet in place
        int t = nums[i]; nums[i] = nums[correct]; nums[correct] = t;
    } else i++;                                 // in place (or dup) → advance
}
// scan for first i where nums[i] != i+1  → missing/duplicate
```
- **📝 Example:** *Find All Numbers Disappeared in an Array* — cyclic-sort, then every index `i` with `nums[i] != i+1` means `i+1` is missing.
- **⏱️ Complexity:** Time O(n) (amortized — each value placed once), Space O(1).
- **💡 Remember-it tip:** "Value `v` goes home to index `v-1`." Compare against `nums[correct]`, not `i`, to dodge infinite swap loops on duplicates. Drill: *Missing Number*, *Find the Duplicate Number*.

### In-place Rotation

[🔝 Back to index](#index)

- **🔍 Recognize:** "rotate array by k steps", "rotate in place", "O(1) extra space", "rotate image/matrix 90°". Shifting elements cyclically without an auxiliary array.
- **📊 Visual:**
```
Rotate right by k=3:  nums=[1,2,3,4,5,6,7]   (k %= n)

Triple-reverse trick:
1) reverse whole:        [7,6,5,4,3,2,1]
2) reverse first k=3:    [5,6,7,4,3,2,1]
                          └─────┘
3) reverse rest n-k:     [5,6,7,1,2,3,4]   ✓ done
                                └───────┘
last k elements rolled to the front.
```
- **🧠 Idea + template:** Reverse-the-whole then reverse-the-two-halves lands every element in its rotated slot using only swaps.
```java
public void rotate(int[] a, int k) {
    int n = a.length; k %= n;
    reverse(a, 0, n - 1);
    reverse(a, 0, k - 1);
    reverse(a, k, n - 1);
}
void reverse(int[] a, int l, int r) {
    while (l < r) { int t = a[l]; a[l++] = a[r]; a[r--] = t; }
}
```
- **📝 Example:** *Rotate Array* — `k %= n`, then the three reversals above. O(n) time, O(1) space.
- **⏱️ Complexity:** Time O(n), Space O(1).
- **💡 Remember-it tip:** "Reverse all, then reverse the two pieces." Always `k %= n` first. Drill: *Rotate Image* (transpose then reverse each row for 90° clockwise).

### Merge Sorted Array (In-place from End)

[🔝 Back to index](#index)

- **🔍 Recognize:** "merge two sorted arrays", "nums1 has enough trailing space (m+n)", "do it in-place / O(1) extra". Two sorted inputs, result must live in the first array.
- **📊 Visual:**
```
nums1=[1,2,3,0,0,0] m=3   nums2=[2,5,6] n=3
Fill from the BACK so we never overwrite unmerged values.
 p1=2(→3)  p2=2(→6)  p=5

 cmp 3 vs 6 → 6 bigger: nums1[5]=6, p2-- p--   [1,2,3,0,0,6]
 cmp 3 vs 5 → 5 bigger: nums1[4]=5, p2-- p--   [1,2,3,0,5,6]
 cmp 3 vs 2 → 3 bigger: nums1[3]=3, p1-- p--   [1,2,3,3,5,6]
 cmp 2 vs 2 → 2 (n1):   nums1[2]=2, p1-- p--   [1,2,2,3,5,6]
 cmp 1 vs 2 → 2 (n2):   nums1[1]=2 ...
 p2 exhausted → remaining nums1 already in place ✓
 result = [1,2,2,3,5,6]
```
- **🧠 Idea + template:** Write the *largest* remaining element into the *last* free slot, moving backwards — the tail of nums1 is empty so nothing gets clobbered.
```java
int p1 = m - 1, p2 = n - 1, p = m + n - 1;
while (p2 >= 0) {                       // nums2 left to place
    if (p1 >= 0 && nums1[p1] > nums2[p2]) nums1[p--] = nums1[p1--];
    else                                  nums1[p--] = nums2[p2--];
}
```
- **📝 Example:** *Merge Sorted Array (LC 88)* — three back pointers; loop until `nums2` drained, leftover `nums1` prefix is already sorted.
- **⏱️ Complexity:** Time O(m+n), Space O(1).
- **💡 Remember-it tip:** "Fill from the back, biggest first." You only need `while (p2 >= 0)` — if nums1's elements remain, they're already positioned. Drill: *Merge Intervals*, *Merge k Sorted Lists*.

### Plus One (Handling Carry)

[🔝 Back to index](#index)

- **🔍 Recognize:** "array of digits represents a number", "add one", "increment large number stored as digits", carry propagation, "all nines" edge case.
- **📊 Visual:**
```
digits = [1, 2, 9]    add 1, walk right→left

 i=2: 9+1 = 10 → write 0, carry 1     [1,2,0]
 i=1: 2+1 = 3  < 10 → write 3, DONE   [1,3,0]  ✓
                       (no carry, early return)

All-nines case  digits=[9,9,9]:
 9→0 carry, 9→0 carry, 9→0 carry  → [0,0,0] then prepend 1
 result = [1,0,0,0]   (length grows by one)
```
- **🧠 Idea + template:** Add from the last digit; a digit `<9` just `++` and return. If every digit was 9 you fall through and need a leading 1.
```java
public int[] plusOne(int[] d) {
    for (int i = d.length - 1; i >= 0; i--) {
        if (d[i] < 9) { d[i]++; return d; }   // no carry → done
        d[i] = 0;                              // 9 → 0, carry on
    }
    int[] res = new int[d.length + 1];
    res[0] = 1;                                // all nines case
    return res;
}
```
- **📝 Example:** *Plus One* — right-to-left; first non-9 increments and returns, otherwise build a new array one longer with a leading 1.
- **⏱️ Complexity:** Time O(n), Space O(1) (O(n) only in the all-nines grow case).
- **💡 Remember-it tip:** "Less than 9 → bump and bail; only all-9s grow the array." Drill: *Add Binary*, *Add Strings*.

### Set Matrix Zeroes (In-place Marking)

[🔝 Back to index](#index)

- **🔍 Recognize:** "if an element is 0, set its entire row and column to 0", "do it in place", "O(1) extra space" on a matrix. You must mark without a separate visited grid.
- **📊 Visual:**
```
Use row 0 & col 0 as the "should-zero" markers.

 [1, 1, 1]          first track if row0/col0 themselves need zeroing,
 [1, 0, 1]  ──►     then for each cell(r,c)==0 set marks:
 [1, 1, 1]            matrix[r][0]=0  and  matrix[0][c]=0

 after marking (cell (1,1)=0 marks row1 & col1):
 col0 ─┐
 [1, 0, 1]   ← row0 used as col-flags (col1 flagged)
 [0, 0, 1]   ← row1 flagged via matrix[1][0]
 [1, 1, 1]

 second pass: for r,c>=1, zero cell if its row-flag OR col-flag is 0
 [1, 0, 1]      [1,0,1]
 [0, 0, 0]  ──► finally handle row0/col0 from saved booleans
 [1, 0, 1]      result:
                [1,0,1]
                [0,0,0]
                [1,0,1]
```
- **🧠 Idea + template:** The first row and column store flags for the rest. Capture col0's own state in a boolean first (since `matrix[0][0]` is shared), then mark, then apply.
```java
boolean col0 = false;
int R = m.length, C = m[0].length;
for (int i = 0; i < R; i++) {
    if (m[i][0] == 0) col0 = true;
    for (int j = 1; j < C; j++)
        if (m[i][j] == 0) { m[i][0] = 0; m[0][j] = 0; }   // set flags
}
for (int i = R - 1; i >= 0; i--) {                        // apply bottom-up
    for (int j = C - 1; j >= 1; j--)
        if (m[i][0] == 0 || m[0][j] == 0) m[i][j] = 0;
    if (col0) m[i][0] = 0;
}
```
- **📝 Example:** *Set Matrix Zeroes* — use row 0 / col 0 as flag storage, a single `col0` boolean for the overlap, apply in reverse so flags aren't destroyed prematurely.
- **⏱️ Complexity:** Time O(m·n), Space O(1).
- **💡 Remember-it tip:** "Borrow row 0 and col 0 as scratch flags; guard col 0 with one extra boolean." Apply marks bottom-up/right-to-left so you read flags before overwriting them. Drill: *Game of Life* (encode next-state in spare bits in place).

### Spiral Traversal

[🔝 Back to index](#index)

- **🔍 Recognize:** "return elements in spiral order", "traverse matrix in a spiral", "generate spiral matrix". Layer-by-layer boundary walk of a 2D grid.
- **📊 Visual:**
```
matrix          shrinking boundaries: top,bottom,left,right
 1  2  3
 4  5  6        ┌──────────────►┐  right along top row: 1 2 3   top++
 7  8  9        │  1  2  3      │
                │            ▼  │  down right col:      6 9     right--
 walk:          │  4  5  6  ▼   │
  1 2 3         │            6  │  left along bottom:   8 7     bottom--
  → 6 9         └◄─────────  9  │
  → 8 7         ▲  7  8           up left col:          4       left++
  → 4           └─ 4              then 5 (center)
  → 5
 spiral = [1,2,3,6,9,8,7,4,5]
```
- **🧠 Idea + template:** Maintain four shrinking walls; emit top row →, right col ↓, bottom row ←, left col ↑, then close inward. Guard the last two edges when only one row/col remains.
```java
int top=0, bottom=m.length-1, left=0, right=m[0].length-1;
List<Integer> res = new ArrayList<>();
while (top <= bottom && left <= right) {
    for (int j=left; j<=right; j++) res.add(m[top][j]);      top++;
    for (int i=top; i<=bottom; i++) res.add(m[i][right]);    right--;
    if (top <= bottom)
        for (int j=right; j>=left; j--) res.add(m[bottom][j]); bottom--;
    if (left <= right)
        for (int i=bottom; i>=top; i--) res.add(m[i][left]);  left++;
}
```
- **📝 Example:** *Spiral Matrix* — four boundaries closing inward; the two `if` guards prevent re-reading a lone middle row/column.
- **⏱️ Complexity:** Time O(m·n), Space O(1) extra (excluding output).
- **💡 Remember-it tip:** "Top→, Right↓, Bottom←, Left↑, then shrink." The two guards matter only for non-square / single-line leftovers. Drill: *Spiral Matrix II* (fill 1..n² in spiral order).

### Multiply Strings (Manual Simulation)

[🔝 Back to index](#index)

- **🔍 Recognize:** "multiply two numbers given as strings", "numbers too big for int/long", "no built-in BigInteger", grade-school multiplication, digit-by-digit.
- **📊 Visual:**
```
"12" * "34"   → result length ≤ 2+2 = 4 digits

Key index rule: digit i of num1 × digit j of num2 lands at
positions p1=i+j (carry) and p2=i+j+1 (units) in result[].

       1  2        i:0 1
   ×   3  4        j:0 1
   ----------
 res indices: [ . . . . ]   (size 4)

 i=1(2) j=1(4): 2*4=8  → pos2,3   res=[0,0,0,8]
 i=1(2) j=0(3): 2*3=6  → pos1,2   res=[0,0,6,8]
 i=0(1) j=1(4): 1*4=4  → pos1,2   res=[0,4,0,8] (+carry handled)
 i=0(1) j=0(3): 1*3=3  → pos0,1   res=[0,4,0,8]→[4,0,8] after carries
 combine sums + carries  →  "408"   (= 12*34 ✓)
```
- **🧠 Idea + template:** Result array of size `m+n`; product of digits `i,j` adds into `res[i+j+1]` with carry into `res[i+j]`. Strip leading zeros at the end.
```java
public String multiply(String a, String b) {
    if (a.equals("0") || b.equals("0")) return "0";
    int m = a.length(), n = b.length();
    int[] res = new int[m + n];
    for (int i = m - 1; i >= 0; i--)
        for (int j = n - 1; j >= 0; j--) {
            int mul = (a.charAt(i)-'0') * (b.charAt(j)-'0');
            int p1 = i + j, p2 = i + j + 1;
            int sum = mul + res[p2];
            res[p2] = sum % 10;
            res[p1] += sum / 10;        // carry up
        }
    StringBuilder sb = new StringBuilder();
    for (int d : res) if (!(sb.length()==0 && d==0)) sb.append(d);
    return sb.length()==0 ? "0" : sb.toString();
}
```
- **📝 Example:** *Multiply Strings* — accumulate each digit product into `res[i+j+1]` (carry to `i+j`), then skip leading zeros when stringifying.
- **⏱️ Complexity:** Time O(m·n), Space O(m+n).
- **💡 Remember-it tip:** "Digits i and j meet at index i+j+1, carry to i+j." Result is always ≤ m+n digits. Drill: *Add Strings*, *Plus One*.

---

## Two Pointers

### Converging (Sorted Array Target Sum)

[🔝 Back to index](#index)
- **🔍 Recognize:** sorted array (or sortable), "find a pair/triplet that sums to target", "two numbers add up to X", monotonic search space where moving an end predictably increases/decreases a value.
- **📊 Visual (MOST IMPORTANT):**
```
target = 9,  arr = [1, 2, 4, 7, 11, 15]   (sorted!)

 L                       R      sum = 1+15 = 16 > 9  -> R--
[1,  2,  4,  7, 11, 15]
 ^                    ^

 L                   R          sum = 1+11 = 12 > 9  -> R--
[1,  2,  4,  7, 11, 15]
 ^                ^

 L               R              sum = 1+7  =  8 < 9  -> L++
[1,  2,  4,  7, 11, 15]
 ^            ^

     L           R              sum = 2+7  =  9 == 9 -> FOUND (2,7)
[1,  2,  4,  7, 11, 15]
     ^        ^

Each step throws away one impossible end. O(n) instead of O(n^2).
```
- **🧠 Idea + template:** Squeeze inward from both ends; the sorted order tells you *which* pointer to move.
```java
int[] twoSum(int[] a, int target) {
    int L = 0, R = a.length - 1;
    while (L < R) {
        int sum = a[L] + a[R];
        if (sum == target) return new int[]{L, R};
        if (sum < target) L++;   // need bigger -> raise low end
        else              R--;   // need smaller -> lower high end
    }
    return new int[]{-1, -1};
}
```
- **📝 Example:** *Two Sum II – Input Array Is Sorted (LC 167).* Use converging pointers; return 1-based indices. Same engine powers *3Sum* (fix one element, two-pointer the rest).
- **⏱️ Complexity:** Time O(n) (O(n²) for 3Sum after sort); Space O(1).
- **💡 Remember-it tip:** "Sorted + pair-sum = pinch from both sides." Drill: *3Sum (LC 15)*, *Container With Most Water (LC 11)*.

### Fast & Slow (Cycle Detection)

[🔝 Back to index](#index)
- **🔍 Recognize:** linked list "does it have a cycle?", "find the cycle start", "find the middle node", "happy number", anything where a tortoise/hare meeting matters or you can't store visited nodes.
- **📊 Visual (MOST IMPORTANT):**
```
List with a cycle (tail 5 -> back to 3):

  1 -> 2 -> 3 -> 4 -> 5
            ^         |
            +---------+

slow moves 1 step, fast moves 2 steps:

step 0:  S=1            F=1
step 1:  S=2            F=3
step 2:  S=3            F=5
step 3:  S=4            F=4   <- inside the loop they collide!
                              fast laps slow -> CYCLE confirmed

No cycle? fast hits null (end) first -> return false.
```
- **🧠 Idea + template:** Two speeds on the same track: if there's a loop, the fast one laps the slow one; if not, fast falls off the end.
```java
boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;          // +1
        fast = fast.next.next;     // +2
        if (slow == fast) return true;  // they met
    }
    return false;                  // fast reached the end
}
```
- **📝 Example:** *Linked List Cycle (LC 141).* Run tortoise/hare; meeting ⇒ cycle. For *Cycle II (LC 142)*, after meeting reset one pointer to head and step both by 1; they meet at the cycle entrance.
- **⏱️ Complexity:** Time O(n); Space O(1).
- **💡 Remember-it tip:** "Tortoise & hare on a circular track always meet." Drill: *Middle of the Linked List (LC 876)*, *Happy Number (LC 202)*.

### Fixed Separation (Nth from End)

[🔝 Back to index](#index)
- **🔍 Recognize:** "Nth node from the end", "remove the Nth-from-last", single-pass requirement, can't (or don't want to) compute length first.
- **📊 Visual (MOST IMPORTANT):**
```
Remove n = 2 from end:  1 -> 2 -> 3 -> 4 -> 5 -> null

1) Advance FAST n+1 = 3 steps to create the gap (so SLOW lands BEFORE target):

 S              F
 1 -> 2 -> 3 -> 4 -> 5 -> null    gap of 3

2) Move BOTH until FAST == null  (gap stays fixed):

      S              F
 1 -> 2 -> 3 -> 4 -> 5 -> null

           S              F(null)
 1 -> 2 -> 3 -> 4 -> 5 -> null
           ^slow sits on node 3, just before the 4 we delete

3) slow.next = slow.next.next  ->  1 -> 2 -> 3 -> 5
```
- **🧠 Idea + template:** Lock a constant gap of n between two pointers; when the leader hits the end, the follower is exactly n-from-end.
```java
ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0, head);
    ListNode slow = dummy, fast = dummy;
    for (int i = 0; i <= n; i++) fast = fast.next; // open gap of n+1
    while (fast != null) { slow = slow.next; fast = fast.next; }
    slow.next = slow.next.next;                    // unlink target
    return dummy.next;
}
```
- **📝 Example:** *Remove Nth Node From End of List (LC 19).* Use a dummy head, advance fast by n+1, walk both, then splice out `slow.next`.
- **⏱️ Complexity:** Time O(n) single pass; Space O(1).
- **💡 Remember-it tip:** "Fixed gap = a ruler you slide to the wall." Use a dummy node so deleting the head is not a special case. Drill: *Middle of the Linked List (LC 876)*, *Swapping Nodes in a Linked List (LC 1721)*.

### Expanding From Center (Palindromes)

[🔝 Back to index](#index)
- **🔍 Recognize:** "longest palindromic substring", "count palindromic substrings", symmetry around a center, checking mirror characters outward.
- **📊 Visual (MOST IMPORTANT):**
```
s = "babad"   -> expand around each center

Odd center at index 2 ('b'):
        a b a
        ^ ^ ^
        L c R     s[L]==s[R] ('a'=='a') -> grow
      a b a b a
      ^       ^
      L       R   s[1]='a' vs s[3]='a' -> still match... 'b'!='d' stop
   => "aba" (length 3)

Even center between i,i+1 (e.g. "bb"):
        b b
        ^ ^
        L R       s[L]==s[R] -> expand outward; mismatch -> stop

Try every center (2n-1 of them); keep the longest span.
```
- **🧠 Idea + template:** Every palindrome has a center; push two pointers outward while characters mirror.
```java
String longestPalindrome(String s) {
    int start = 0, end = 0;
    for (int i = 0; i < s.length(); i++) {
        int a = expand(s, i, i);     // odd-length center
        int b = expand(s, i, i + 1); // even-length center
        int len = Math.max(a, b);
        if (len > end - start) { start = i - (len - 1) / 2; end = i + len / 2; }
    }
    return s.substring(start, end + 1);
}
int expand(String s, int L, int R) {
    while (L >= 0 && R < s.length() && s.charAt(L) == s.charAt(R)) { L--; R++; }
    return R - L - 1;                // length once it stops
}
```
- **📝 Example:** *Longest Palindromic Substring (LC 5).* Expand around all 2n−1 centers, track the widest span. Same trick counts palindromes in *LC 647*.
- **⏱️ Complexity:** Time O(n²); Space O(1).
- **💡 Remember-it tip:** "Pick a mirror, grow outward." Remember odd AND even centers. Drill: *Palindromic Substrings (LC 647)*, *Valid Palindrome (LC 125)*.

### In-place Array Modification

[🔝 Back to index](#index)
- **🔍 Recognize:** "do it in O(1) extra space", "modify the array in place", "remove duplicates / remove element / move zeroes", a *write* pointer trailing a *read* pointer.
- **📊 Visual (MOST IMPORTANT):**
```
Move Zeroes:  nums = [0, 1, 0, 3, 12]
W = write index (next slot for a non-zero), R = read index

R=0 nums[0]=0  -> skip            [0,1,0,3,12]  W=0
R=1 nums[1]=1  -> swap W,R; W=1   [1,0,0,3,12]  W=1
R=2 nums[2]=0  -> skip            [1,0,0,3,12]  W=1
R=3 nums[3]=3  -> swap W,R; W=2   [1,3,0,0,12]  W=2
R=4 nums[4]=12 -> swap W,R; W=3   [1,3,12,0,0]  W=3

Read scans everything; Write only advances on a "keeper".
Result: [1, 3, 12, 0, 0]
```
- **🧠 Idea + template:** A slow *write* pointer marks where the next kept element goes while a fast *read* pointer scans ahead.
```java
void moveZeroes(int[] nums) {
    int w = 0;
    for (int r = 0; r < nums.length; r++) {
        if (nums[r] != 0) {              // a keeper
            int t = nums[w]; nums[w] = nums[r]; nums[r] = t;
            w++;
        }
    }
}
```
- **📝 Example:** *Move Zeroes (LC 283).* Swap each non-zero forward to the write pointer; trailing slots become zero automatically. Same shape: *Remove Duplicates from Sorted Array (LC 26)* (advance write only when `nums[r] != nums[w-1]`).
- **⏱️ Complexity:** Time O(n); Space O(1).
- **💡 Remember-it tip:** "Write pointer collects the keepers." Drill: *Remove Element (LC 27)*, *Remove Duplicates from Sorted Array (LC 26)*.

### String Comparison with Backspaces

[🔝 Back to index](#index)
- **🔍 Recognize:** "backspace" character (`#`) meaning delete, "do the strings type out equal?", "compare two streams with deletions", ideally in O(1) space ⇒ scan from the right.
- **📊 Visual (MOST IMPORTANT):**
```
s = "ab#c"   t = "ad#c"     ('#' deletes the char before it)

Walk from the RIGHT; keep a "skip" counter for pending deletes.

s: a b # c              t: a d # c
         ^ i=3 'c' keep         ^ j=3 'c' keep   -> compare 'c'=='c' OK
     ^ i=2 '#' -> skip++=1   ^ j=2 '#' -> skip++=1
   ^ i=1 'b' but skip>0 -> drop 'b', skip--   ... same drops 'd'
 ^ i=0 'a' keep                ^ j=0 'a' keep   -> compare 'a'=='a' OK

both exhausted, all compares matched -> EQUAL  ("ac" == "ac")
```
- **🧠 Idea + template:** Scan right-to-left; each `#` adds a skip, real chars are dropped while skips remain, otherwise they must match.
```java
boolean backspaceCompare(String s, String t) {
    int i = s.length() - 1, j = t.length() - 1;
    int skipS = 0, skipT = 0;
    while (i >= 0 || j >= 0) {
        i = nextValid(s, i);               // land on next real char
        j = nextValid(t, j);
        if (i < 0 && j < 0) return true;   // both done together
        if (i < 0 || j < 0) return false;  // one ran out first
        if (s.charAt(i) != t.charAt(j)) return false;
        i--; j--;
    }
    return true;
}
int nextValid(String x, int k) {
    int skip = 0;
    while (k >= 0) {
        if (x.charAt(k) == '#') { skip++; k--; }
        else if (skip > 0)      { skip--; k--; }   // deleted by a '#'
        else break;                                // a surviving char
    }
    return k;
}
```
- **📝 Example:** *Backspace String Compare (LC 844).* Process both strings from the back so each `#` cancels the nearest unprocessed char; compare surviving chars in lockstep.
- **⏱️ Complexity:** Time O(n + m); Space O(1) (vs O(n) if you rebuild with a stack).
- **💡 Remember-it tip:** "Read backwards so deletes look forwards." A stack works too but costs space. Drill: *Backspace String Compare (LC 844)*, *Crawler Log Folder (LC 1598)*.

### String Reversal

[🔝 Back to index](#index)
- **🔍 Recognize:** "reverse the string/char array in place", "reverse vowels", "reverse words", swap symmetric positions, O(1) extra space on a `char[]`.
- **📊 Visual (MOST IMPORTANT):**
```
Reverse char[] = ['h','e','l','l','o']

 L                 R     swap h<->o   -> ['o','e','l','l','h']
 ^                 ^

     L         R         swap e<->l   -> ['o','l','l','e','h']
     ^         ^

         LR              L meets R (or crosses) -> STOP
         ^
Result: ['o','l','l','e','h']   ("olleh")
Each swap fixes TWO positions, so only n/2 iterations.
```
- **🧠 Idea + template:** Swap the outermost pair, step both pointers inward until they meet.
```java
void reverseString(char[] s) {
    int L = 0, R = s.length - 1;
    while (L < R) {
        char t = s[L]; s[L] = s[R]; s[R] = t;
        L++; R--;
    }
}
```
- **📝 Example:** *Reverse String (LC 344).* Swap ends and march inward; stop when the pointers cross. For *Reverse Vowels (LC 345)*, advance each pointer to the next vowel before swapping.
- **⏱️ Complexity:** Time O(n); Space O(1).
- **💡 Remember-it tip:** "Swap the ends, walk inward." Each swap settles two characters. Drill: *Reverse Vowels of a String (LC 345)*, *Reverse Words in a String III (LC 557)*.

---

## Sliding Window

### Fixed Size

[🔝 Back to index](#index)
- **🔍 Recognize:** Phrases like "subarray/substring **of size k**", "every **window of length k**", "max/min/average of **all contiguous blocks of k** elements". The window width never changes.
- **📊 Visual (MOST IMPORTANT):**
```
nums = [2, 1, 5, 1, 3, 2]   k = 3      goal: max sum of any window

 [2  1  5] 1  3  2     window sum = 8   <- compute first k
  ^-----^
  2 [1  5  1] 3  2     slide: -2 (out)  +1 (in)  => 8-2+1 = 7
     ^-----^
  2  1 [5  1  3] 2     slide: -1 (out)  +3 (in)  => 7-1+3 = 9  <-- max
        ^-----^
  2  1  5 [1  3  2]    slide: -5 (out)  +2 (in)  => 9-5+2 = 6
           ^-----^
answer = 9
```
- **🧠 Idea + template:** Keep a running sum; each step add the entering element and subtract the leaving one — O(1) per slide, no recompute.
```java
int maxSumFixed(int[] nums, int k) {
    int sum = 0, max;
    for (int i = 0; i < k; i++) sum += nums[i];   // first window
    max = sum;
    for (int r = k; r < nums.length; r++) {        // slide
        sum += nums[r] - nums[r - k];              // in - out
        max = Math.max(max, sum);
    }
    return max;
}
```
- **📝 Example:** *Maximum Average Subarray I* — build the size-k sum, slide once per index updating `sum += in - out`, track the max sum, divide by k at the end.
- **⏱️ Complexity:** Time O(n), Space O(1).
- **💡 Remember-it tip:** "Add the new, drop the old." Window is a **conveyor belt** of fixed length. Drill: *Maximum Average Subarray I*, *Number of Sub-arrays of Size K and Avg ≥ Threshold*.

### Variable Size

[🔝 Back to index](#index)
- **🔍 Recognize:** "**longest/shortest** subarray/substring such that <condition>", "smallest window whose sum ≥ target", "at most / exactly K of something". Window grows and shrinks based on a constraint.
- **📊 Visual (MOST IMPORTANT):**
```
nums = [2, 3, 1, 2, 4, 3]   target sum >= 7   goal: shortest length

L,R both start at 0. Expand R; when sum>=7, shrink L.

[2] 3 1 2 4 3        sum=2  <7  expand
 L
 R
[2 3 1 2] 4 3        sum=8  >=7  record len=4, shrink L
 L     R
 2[3 1 2] 4 3        sum=6  <7   expand R
   L   R
 2[3 1 2 4] 3        sum=10 >=7  record len=4, shrink L
   L     R
 2 3[1 2 4] 3        sum=7  >=7  record len=3, shrink L
     L   R
 2 3 1[2 4] 3        sum=6  <7   expand R
       L R
 2 3 1[2 4 3]        sum=9  >=7  record len=3, shrink L
       L   R
 2 3 1 2[4 3]        sum=7  >=7  record len=2  <-- min
         L R
answer = 2  (window [4,3])
```
- **🧠 Idea + template:** Two pointers; **grow `r`** to satisfy the goal, **shrink `l`** while the window stays valid — each index enters and leaves once.
```java
int minLenAtLeastTarget(int[] nums, int target) {
    int l = 0, sum = 0, best = Integer.MAX_VALUE;
    for (int r = 0; r < nums.length; r++) {
        sum += nums[r];                 // expand right
        while (sum >= target) {         // shrink while valid
            best = Math.min(best, r - l + 1);
            sum -= nums[l++];
        }
    }
    return best == Integer.MAX_VALUE ? 0 : best;
}
```
- **📝 Example:** *Minimum Size Subarray Sum* — expand `r` adding to `sum`; whenever `sum >= target`, record `r-l+1` and shrink from `l`; return the smallest length seen.
- **⏱️ Complexity:** Time O(n) (each element added/removed once), Space O(1).
- **💡 Remember-it tip:** "**Right feeds, left starves.**" For *longest* problems shrink only while **invalid**; for *shortest* shrink while **valid**. Drill: *Longest Substring Without Repeating Characters*, *Minimum Size Subarray Sum*.

### Character Frequency Matching

[🔝 Back to index](#index)
- **🔍 Recognize:** "**anagram**", "**permutation** in string", "contains **all characters of** T", "substring with **at most/exactly K distinct**". You're matching counts of characters, not a numeric sum.
- **📊 Visual (MOST IMPORTANT):**
```
s = "cbaebabacd"   p = "abc"     goal: find all anagram start indices

need = {a:1, b:1, c:1}   window size = 3, "have" = current counts

 [c b a] e b a b a c d   have={c1,b1,a1} == need  -> MATCH at i=0
  ^---^
  c[b a e] b a b a c d   have={b1,a1,e1}  e extra -> no
    ^---^
  c b[a e b] a b a c d   have={a1,e1,b1}  e extra -> no
      ^---^
  c b a[e b a] b a c d   have={e1,b1,a1}  e extra -> no
        ^---^
        ... slide on ...
  c b a e b a[b a c] d   have={b1,a1,c1} == need  -> MATCH at i=6
              ^---^
matches = [0, 6]
```
- **🧠 Idea + template:** Track how many char-counts currently satisfy the target with a single `matched` counter; window of fixed/variable width updates counts in O(1).
```java
List<Integer> findAnagrams(String s, String p) {
    int[] need = new int[26];
    for (char c : p.toCharArray()) need[c - 'a']++;
    List<Integer> res = new ArrayList<>();
    int[] win = new int[26];
    for (int r = 0; r < s.length(); r++) {
        win[s.charAt(r) - 'a']++;                 // add right
        if (r >= p.length())                      // drop left
            win[s.charAt(r - p.length()) - 'a']--;
        if (Arrays.equals(win, need)) res.add(r - p.length() + 1);
    }
    return res;
}
```
- **📝 Example:** *Find All Anagrams in a String* — keep a size-`p` window of letter counts; after the window fills, compare to `need` each step and record the start index on a full match.
- **⏱️ Complexity:** Time O(n) (26-array compare is O(26)≈O(1)), Space O(1) (fixed 26 counts).
- **💡 Remember-it tip:** "**Counts must match, not order.**" Use a frequency array + a `matched` counter to avoid re-scanning. Drill: *Permutation in String*, *Minimum Window Substring*.

### Monotonic Queue for Max/Min

[🔝 Back to index](#index)
- **🔍 Recognize:** "**maximum/minimum of each sliding window**", "window where **max - min ≤ limit**", needing the extreme of a moving window in O(1). A plain heap is O(n log n); a monotonic deque gets O(n).
- **📊 Visual (MOST IMPORTANT):**
```
nums = [1, 3, -1, -3, 5, 3]   k = 3   goal: max of each window
deque holds INDICES; values kept in DECREASING order. front = max.

r=0 val=1   push        deque=[0]            (vals: 1)
r=1 val=3   pop 1<3     deque=[1]            (vals: 3)
r=2 val=-1  push        deque=[1,2]          (vals: 3,-1)  window[0..2] max=3
r=3 val=-3  push        deque=[1,2,3]        (vals: 3,-1,-3)
            front idx 1 still in window      window[1..3] max=3
r=4 val=5   pop -3,-1,3 deque=[4]            (vals: 5)
            (all smaller popped from back)   window[2..4] max=5
r=5 val=3   push        deque=[4,5]          (vals: 5,3)   window[3..5] max=5

outputs = [3, 3, 5, 5]
back-pop rule: remove smaller tails   front-pop rule: drop idx <= r-k
```
- **🧠 Idea + template:** Maintain a deque of indices with **decreasing values**; pop smaller values off the back (they can never be max), pop stale indices off the front — the front is always the window max.
```java
int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> dq = new ArrayDeque<>();   // stores indices, vals decreasing
    int[] res = new int[nums.length - k + 1];
    for (int r = 0; r < nums.length; r++) {
        while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[r])
            dq.pollLast();                    // back-pop smaller
        dq.offerLast(r);
        if (dq.peekFirst() <= r - k) dq.pollFirst();  // front-pop stale
        if (r >= k - 1) res[r - k + 1] = nums[dq.peekFirst()];
    }
    return res;
}
```
- **📝 Example:** *Sliding Window Maximum* — keep a decreasing deque of indices; back-pop everything `<=` the new value, push it, evict the front if it fell out of the window, and read the front as that window's max.
- **⏱️ Complexity:** Time O(n) (each index pushed/popped once), Space O(k).
- **💡 Remember-it tip:** "**Front is king, kill the weak tails.**" For a *min* window flip the comparison (`>=`). Drill: *Sliding Window Maximum*, *Shortest Subarray with Sum at Least K*, *Longest Continuous Subarray with Absolute Diff ≤ Limit* (two deques).

---

## Binary Search

Binary search repeatedly halves a search space using a yes/no test on the midpoint. The hard part is never the idea — it's the boundary bookkeeping. The visuals below show exactly where each pointer lands.

> **Universal mantra:** decide `lo`/`hi` bounds, decide the loop condition (`<` vs `<=`), decide how `mid` is computed (`lo + (hi-lo)/2`), and decide which half you discard. Always use `lo + (hi - lo) / 2` to avoid integer overflow.

---

### On Sorted Array/List

[🔝 Back to index](#index)

- **🔍 Recognize:** "sorted array", "find target", "O(log n) required", "search in...". The data is already sorted (or you can sort it) and you need an exact match or insertion point.

- **📊 Visual (MOST IMPORTANT):**
```
Find target = 7 in: [1, 3, 5, 7, 9, 11, 13]   (indices 0..6)

         lo                  mid                   hi
          v                   v                     v
        [ 1 ,  3 ,  5 ,  7 ,  9 , 11 , 13 ]
 step1   0    1    2    3    4    5    6     mid=3 -> a[3]=7 == 7  FOUND!

----- if target = 5 instead -----
 step1  lo=0 ............ mid=3 ............ hi=6   a[3]=7 > 5  -> go LEFT, hi=mid-1=2
        [ 1 ,  3 ,  5 ] x  x   x   x
 step2  lo=0 ... mid=1 ... hi=2              a[1]=3 < 5  -> go RIGHT, lo=mid+1=2
        x   x  [ 5 ]
 step3  lo=2 = mid=2 = hi=2                  a[2]=5 == 5  FOUND at index 2!

Search space halves each step:  7 -> 3 -> 1 element.
```

- **🧠 Idea + template:** Compare `mid` to target; throw away the half that cannot contain it.
```java
int search(int[] a, int target) {
    int lo = 0, hi = a.length - 1;     // inclusive bounds
    while (lo <= hi) {                 // <= because lo==hi is a valid single cell
        int mid = lo + (hi - lo) / 2;
        if (a[mid] == target) return mid;
        else if (a[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;                          // or `lo` = insertion point
}
```

- **📝 Example:** *Binary Search (LC 704)* / *Search Insert Position (LC 35)*. Run the standard loop; for insert position, when not found, `lo` is exactly where the target should go.

- **⏱️ Complexity:** Time `O(log n)`, Space `O(1)`.

- **💡 Remember-it tip:** "**Inclusive bounds → `<=` and `mid±1`.**" When the loop exits, `lo > hi` and `lo` is the insertion point. Drill: *LC 704*, *LC 35*.

---

### Find First/Last Occurrence

[🔝 Back to index](#index)

- **🔍 Recognize:** "first/last position", "count occurrences of target", "leftmost/rightmost index", duplicates allowed in a sorted array. A plain match isn't enough — you must keep searching after finding.

- **📊 Visual (MOST IMPORTANT):**
```
Find FIRST index of target = 5 in: [2, 5, 5, 5, 8, 9]   (indices 0..5)

        [ 2 ,  5 ,  5 ,  5 ,  8 ,  9 ]
          0    1    2    3    4    5

step1  lo=0  hi=5  mid=2  a[2]=5 == 5  -> RECORD ans=2, then keep going LEFT (hi=mid-1=1)
step2  lo=0  hi=1  mid=0  a[0]=2 <  5  -> go RIGHT (lo=mid+1=1)
step3  lo=1  hi=1  mid=1  a[1]=5 == 5  -> RECORD ans=1, then keep going LEFT (hi=mid-1=0)
       lo=1 > hi=0  -> STOP.   FIRST occurrence = index 1

Key trick: on a hit, don't stop — shrink toward the side you want.
   FIRST  -> push hi left   (hi = mid - 1)
   LAST   -> push lo right  (lo = mid + 1)
```

- **🧠 Idea + template:** On a match, record the index but keep shrinking toward the desired edge instead of returning.
```java
int findBound(int[] a, int target, boolean first) {
    int lo = 0, hi = a.length - 1, ans = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (a[mid] == target) {
            ans = mid;
            if (first) hi = mid - 1;   // keep searching left
            else       lo = mid + 1;   // keep searching right
        } else if (a[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return ans;
}
```

- **📝 Example:** *Find First and Last Position of Element in Sorted Array (LC 34)*. Call `findBound` twice (first=true, then first=false); count = last − first + 1.

- **⏱️ Complexity:** Time `O(log n)`, Space `O(1)`.

- **💡 Remember-it tip:** "**Hit ≠ stop — lean to your edge.**" First→squeeze right pointer left; Last→squeeze left pointer right. Drill: *LC 34*, *LC 35 (lower_bound)*.

---

### On Answer / Condition Function

[🔝 Back to index](#index)

- **🔍 Recognize:** "minimum/maximum X such that...", "smallest capacity/speed/distance that works", answer lies in a numeric range (not array indices), and there's a monotonic feasibility check `canDo(x)`: if `x` works, every larger (or smaller) value works too.

- **📊 Visual (MOST IMPORTANT):**
```
Koko Eating Bananas (LC 875): min speed k so she finishes in H hours.
feasible(k) is MONOTONIC:  too slow = FALSE, fast enough = TRUE

 speed:   1    2    3    4    5    6    7    8
 ok?      F    F    F    T    T    T    T    T
                         ^
                         answer = first TRUE = 4

Binary search the BOUNDARY between F and T:

 lo=1 ............ mid=4 ............ hi=8
 feasible(4)=T  -> this might be the answer; squeeze left:  hi=mid=4
 lo=1 ... mid=2 ... hi=4
 feasible(2)=F  -> too slow, discard left half:  lo=mid+1=3
 lo=3  mid=3  hi=4
 feasible(3)=F  -> lo=mid+1=4
 lo=4 == hi=4  -> STOP.  answer = lo = 4

We binary-search the VALUE axis, not array indices.
```

- **🧠 Idea + template:** Binary-search the answer range; `feasible()` returns a monotonic boolean and you hunt for the F→T boundary.
```java
int minFeasible(int lo, int hi) {          // search smallest x with feasible(x)==true
    while (lo < hi) {                       // < : converge to single answer
        int mid = lo + (hi - lo) / 2;
        if (feasible(mid)) hi = mid;        // mid works -> answer is mid or left
        else               lo = mid + 1;    // mid fails -> answer strictly right
    }
    return lo;                              // lo == hi == first feasible value
}
// boolean feasible(int x) { ... return true/false based on the constraint ... }
```

- **📝 Example:** *Koko Eating Bananas (LC 875)*. `feasible(k)` = sum of ceil(pile/k) ≤ H. Search k in [1, max(pile)] for the smallest feasible speed.

- **⏱️ Complexity:** Time `O(n · log(range))` (each `feasible` is `O(n)`), Space `O(1)`.

- **💡 Remember-it tip:** "**Paint the axis F…F T…T, grab the first T.**" The shape must be monotonic. Drill: *LC 875*, *LC 1011 (Ship Within D Days)*, *LC 410 (Split Array Largest Sum)*.

---

### Find Min/Max in Rotated Sorted Array

[🔝 Back to index](#index)

- **🔍 Recognize:** "rotated sorted array", "find minimum", "search in rotated array", "no duplicates, originally ascending". The array is sorted then rotated at an unknown pivot; one half is always still sorted.

- **📊 Visual (MOST IMPORTANT):**
```
Find MIN in rotated: [4, 5, 6, 7, 0, 1, 2]   (pivot/min is the 0)
                       0  1  2  3  4  5  6

Compare a[mid] with a[hi] to learn which half holds the min:

step1  lo=0  hi=6  mid=3  a[3]=7  >  a[6]=2  -> min is to the RIGHT, lo=mid+1=4
       [ . . . . | 0  1  2 ]
step2  lo=4  hi=6  mid=5  a[5]=1  <  a[6]=2  -> min is mid or LEFT, hi=mid=5
       [ 0  1 ]
step3  lo=4  hi=5  mid=4  a[4]=0  <  a[5]=1  -> hi=mid=4
       lo=4 == hi=4  -> STOP.  MIN = a[4] = 0

Rule of thumb:
   a[mid] > a[hi]  -> the drop (min) is to the RIGHT  -> lo = mid + 1
   a[mid] < a[hi]  -> mid could BE the min, go left   -> hi = mid
```

- **🧠 Idea + template:** Compare `mid` to `hi` (the right end) to decide which side still contains the rotation point / minimum.
```java
int findMin(int[] a) {
    int lo = 0, hi = a.length - 1;
    while (lo < hi) {                       // converge to the single min
        int mid = lo + (hi - lo) / 2;
        if (a[mid] > a[hi]) lo = mid + 1;   // min is in the right half
        else                hi = mid;       // min is mid or in the left half
    }
    return a[lo];                           // lo == hi points at the minimum
}
```

- **📝 Example:** *Find Minimum in Rotated Sorted Array (LC 153)*. Converge with the `a[mid]` vs `a[hi]` rule; the index where `lo==hi` is the rotation pivot (the minimum). For *Search in Rotated (LC 33)*, first find pivot, then binary-search the correct sorted half.

- **⏱️ Complexity:** Time `O(log n)`, Space `O(1)`.

- **💡 Remember-it tip:** "**Compare to the RIGHT end; `>` means the cliff is ahead.**" Use `hi = mid` (not `mid-1`) so you never skip the candidate. Drill: *LC 153*, *LC 33*, *LC 154 (with duplicates)*.

---

### Median and Kth of Two Sorted Arrays

[🔝 Back to index](#index)

- **🔍 Recognize:** "median of two sorted arrays", "kth smallest in two sorted arrays", strict `O(log(m+n))` requirement. You must combine two sorted arrays' order statistics without merging them.

- **📊 Visual (MOST IMPORTANT):**
```
Median of A=[1,3,8] and B=[7,9,10,11]   (m=3, n=4, total=7 -> median is 4th smallest)

Binary-search a PARTITION of the SHORTER array A. Take i elements from A,
the rest (half - i) from B, so left side has half = (m+n+1)/2 = 4 elements.

Want:  maxLeftA <= minRightB   AND   maxLeftB <= minRightA
                                       (left halves are the smallest 4)

 try i=1 (1 from A, 3 from B):
        A:  1 | 3  8           Lmax_A=1   Rmin_A=3
        B:  7  9  10 | 11      Lmax_B=10  Rmin_B=11
        check 1 <= 11  ✓  but 10 <= 3 ✗   -> B's left too big -> take MORE from A
                                              move i RIGHT

 try i=2 (2 from A, 2 from B):
        A:  1  3 | 8           Lmax_A=3   Rmin_A=8
        B:  7  9 | 10  11      Lmax_B=9   Rmin_B=10
        check 3 <= 10 ✓  and  9 <= 8 ✗    -> B's left STILL too big -> i RIGHT

 try i=3 (3 from A, 1 from B):
        A:  1  3  8 |          Lmax_A=8   Rmin_A=+inf
        B:  7 | 9  10  11      Lmax_B=7   Rmin_B=9
        check 8 <= 9 ✓  and  7 <= +inf ✓   -> PARTITION FOUND!

 odd total -> median = max(Lmax_A, Lmax_B) = max(8,7) = 8
   (sorted merge would be [1,3,7,8,9,10,11], 4th = 8 ✓)
```

- **🧠 Idea + template:** Binary-search the cut point in the shorter array so the combined left half holds the smallest `(m+n+1)/2` elements; use ±∞ sentinels at the edges.
```java
double findMedianSortedArrays(int[] A, int[] B) {
    if (A.length > B.length) { int[] t = A; A = B; B = t; }   // ensure A is shorter
    int m = A.length, n = B.length, half = (m + n + 1) / 2;
    int lo = 0, hi = m;
    while (lo <= hi) {
        int i = lo + (hi - lo) / 2;          // cut in A
        int j = half - i;                    // matching cut in B
        int Lا = (i == 0) ? Integer.MIN_VALUE : A[i - 1];
        int Ra = (i == m) ? Integer.MAX_VALUE : A[i];
        int Lb = (j == 0) ? Integer.MIN_VALUE : B[j - 1];
        int Rb = (j == n) ? Integer.MAX_VALUE : B[j];
        if (Lا <= Rb && Lb <= Ra) {          // correct partition
            if (((m + n) & 1) == 1) return Math.max(Lا, Lb);
            return (Math.max(Lا, Lb) + Math.min(Ra, Rb)) / 2.0;
        } else if (Lا > Rb) hi = i - 1;       // took too many from A -> move cut left
        else                lo = i + 1;       // took too few  from A -> move cut right
    }
    return -1.0;                              // unreachable for valid input
}
```

- **📝 Example:** *Median of Two Sorted Arrays (LC 4)*. Always binary-search the shorter array's partition; sentinels (`±∞`) handle empty left/right sides cleanly.

- **⏱️ Complexity:** Time `O(log(min(m, n)))`, Space `O(1)`.

- **💡 Remember-it tip:** "**Cut the short array; left side = smallest half; balance the two maxes.**" If `Lmax_A > Rmin_B` you over-cut A → move left. Drill: *LC 4*, *Kth Smallest in Two Sorted Arrays*, *LC 378 (Kth Smallest in a Sorted Matrix)*.

---

## Stack

### Valid Parentheses

[🔝 Back to index](#index)
- **🔍 Recognize:** "matching/balanced brackets", multiple bracket types `() [] {}`, "valid/well-formed", "every open has a matching close in correct order", nesting must be respected. Any time *most-recent-open must close first* (LIFO), think stack.
- **📊 Visual (MOST IMPORTANT):**
```
Input: "([{}])"            stack grows on open, must MATCH on close

ch   action                 stack (bottom->top)
--   --------------------    -------------------
(    push (                  [ (
[    push [                  [ ( [
{    push {                  [ ( [ {
}    close: top is { ✓ pop   [ ( [
]    close: top is [ ✓ pop   [ (
)    close: top is ( ✓ pop   [ ]   <- empty
end  stack empty -> VALID

Counter-example "(]":
(    push (                  [ (
]    close: top is ( , expected [  -> MISMATCH -> INVALID
```
- **🧠 Idea + template:** Push opens; on a close, the stack top *must* be its matching open — otherwise fail. Stack empty at the end == valid.
```java
boolean isValid(String s) {
    Deque<Character> st = new ArrayDeque<>();
    Map<Character,Character> close = Map.of(')','(', ']','[', '}','{');
    for (char c : s.toCharArray()) {
        if (close.containsKey(c)) {                 // c is a closing bracket
            if (st.isEmpty() || st.pop() != close.get(c)) return false;
        } else {
            st.push(c);                             // opening bracket
        }
    }
    return st.isEmpty();
}
```
- **📝 Example:** *Valid Parentheses (LC 20).* Iterate; push opens, pop+match on closes, return `stack.isEmpty()` at the end.
- **⏱️ Complexity:** Time O(n), Space O(n) worst case (all opens).
- **💡 Remember-it tip:** "Last opened, first closed." Drill: *Minimum Remove to Make Valid Parentheses (LC 1249)*, *Longest Valid Parentheses (LC 32, indices on stack)*.

---

### Monotonic Stack

[🔝 Back to index](#index)
- **🔍 Recognize:** "next greater/smaller element", "previous greater/smaller", "warmer temperature", "span", "how many days until…", or you need the *nearest* element to the left/right that beats the current one. Answer per index in O(n).
- **📊 Visual (MOST IMPORTANT):**
```
Next Greater Element of  [2, 1, 2, 4, 3]
Keep a stack of INDICES whose values are still "waiting" for a bigger one.
Stack stays DECREASING (top = smallest waiting). Pop while curr > top.

i  val  pops (val>top?)        stack(idx:val)   ans set
-  ---  --------------------   --------------    -------------
0  2    -                      [0:2]
1  1    1<2 no pop, push       [0:2, 1:1]
2  2    2>1 pop idx1 ->ans[1]=2 [0:2]            ans[1]=2
        2>2? no (strict)push   [0:2, 2:2]
3  4    4>2 pop idx2 ->ans[2]=4 [0:2]            ans[2]=4
        4>2 pop idx0 ->ans[0]=4 []               ans[0]=4
        push                   [3:4]
4  3    3<4 no pop, push       [3:4, 4:3]
end leftover 3:4,4:3 -> no greater -> -1

ans = [4, 2, 4, -1, -1]
```
- **🧠 Idea + template:** Maintain a stack that stays monotonic; when the current element breaks the order, it is the "answer" for everything you pop.
```java
int[] nextGreater(int[] a) {
    int n = a.length;
    int[] ans = new int[n];
    Arrays.fill(ans, -1);
    Deque<Integer> st = new ArrayDeque<>();   // indices, values DECREASING
    for (int i = 0; i < n; i++) {
        while (!st.isEmpty() && a[i] > a[st.peek()])
            ans[st.pop()] = a[i];             // a[i] is next greater for popped idx
        st.push(i);
    }
    return ans;
}
```
- **📝 Example:** *Daily Temperatures (LC 739).* Store indices in a decreasing stack; when a warmer day appears, pop and set `answer[idx] = i - idx` (days waited).
- **⏱️ Complexity:** Time O(n) (each index pushed/popped once), Space O(n).
- **💡 Remember-it tip:** "Pop the smaller losers when a bigger boss arrives." Pick direction by scan order; pick `>` vs `>=` for strict vs non-strict. Drill: *Next Greater Element II (LC 503, circular — loop `2n`)*, *Online Stock Span (LC 901)*.

---

### Min Stack Design

[🔝 Back to index](#index)
- **🔍 Recognize:** "design a stack", "push/pop/top **and** retrieve the minimum in O(1)", any data-structure-design prompt asking for an auxiliary constant-time aggregate (min/max) alongside normal stack ops.
- **📊 Visual (MOST IMPORTANT):**
```
Two stacks: main values + a parallel "min-so-far" stack.
min stack top ALWAYS = minimum of everything currently in main.

op          main (bot->top)     min  (bot->top)    getMin()
--------     ----------------    ----------------   --------
push(5)      5                   5                  5
push(3)      5 3                 5 3   (3<5)        3
push(7)      5 3 7               5 3 3 (7>3 keep 3) 3
getMin()     ->                  read min top = 3   3
pop()  (7)   5 3                 5 3                3
pop()  (3)   5                   5                  5
getMin()     ->                  read min top = 5   5
```
- **🧠 Idea + template:** Each push also records "min including me"; pop both in lockstep so `min.top()` is always the current minimum.
```java
class MinStack {
    private Deque<Integer> s   = new ArrayDeque<>();
    private Deque<Integer> min = new ArrayDeque<>();
    public void push(int x) {
        s.push(x);
        min.push(min.isEmpty() ? x : Math.min(x, min.peek()));
    }
    public void pop()   { s.pop(); min.pop(); }
    public int  top()   { return s.peek(); }
    public int  getMin(){ return min.peek(); }
}
```
- **📝 Example:** *Min Stack (LC 155).* Keep a twin stack of running minima; every op stays O(1). (Space-optimized variant: store only on new minima, or encode deltas.)
- **⏱️ Complexity:** Time O(1) per op, Space O(n).
- **💡 Remember-it tip:** "Carry your minimum on your back" — each level remembers the min beneath it. Drill: *Max Stack (LC 716)*, *Implement Queue using Stacks (LC 232)*.

---

### Expression Evaluation (RPN / Infix)

[🔝 Back to index](#index)
- **🔍 Recognize:** "evaluate expression", "Reverse Polish Notation / postfix", "basic calculator", operators `+ - * /`, parentheses, operator precedence. Numbers wait on a stack; operators consume operands.
- **📊 Visual (MOST IMPORTANT):**
```
RPN: ["2","1","+","3","*"]   meaning (2+1)*3
Push numbers; on an operator pop TWO operands (b on top, a below).

token  action                       stack (bot->top)
-----  --------------------------   ----------------
2      push 2                       [2]
1      push 1                       [2, 1]
+      pop b=1, a=2 -> 2+1=3 push    [3]
3      push 3                       [3, 3]
*      pop b=3, a=3 -> 3*3=9 push    [9]
end    result = stack.top = 9

ORDER MATTERS for - and / :  compute (a OP b), a is the lower one.
"6 2 /" -> a=6, b=2 -> 6/2 = 3   (NOT 2/6)
```
- **🧠 Idea + template:** Operands pile on a stack; each operator pops two, applies `a op b` (lower operand is left), pushes the result.
```java
int evalRPN(String[] tokens) {
    Deque<Integer> st = new ArrayDeque<>();
    for (String t : tokens) {
        switch (t) {
            case "+": case "-": case "*": case "/": {
                int b = st.pop(), a = st.pop();           // order matters!
                st.push(switch (t) {
                    case "+" -> a + b;  case "-" -> a - b;
                    case "*" -> a * b;  default  -> a / b;
                });
                break;
            }
            default: st.push(Integer.parseInt(t));        // a number
        }
    }
    return st.pop();
}
```
- **📝 Example:** *Evaluate Reverse Polish Notation (LC 150).* Push numbers, on each operator pop two operands in order `(a op b)`, push result; final stack holds the answer.
- **⏱️ Complexity:** Time O(n), Space O(n).
- **💡 Remember-it tip:** "Top of stack is the *right* operand." For infix (*Basic Calculator I/II*), keep a number stack and apply the *previous* operator on the fly, handling `*`/`/` immediately and deferring `+`/`-`. Drill: *Basic Calculator II (LC 227)*, *Basic Calculator (LC 224, parentheses + sign stack)*.

---

### Largest Rectangle in Histogram

[🔝 Back to index](#index)
- **🔍 Recognize:** "largest rectangle", "maximal area" under bars/heights, "max area in a histogram", or a 2D variant ("maximal rectangle" of 1s row-by-row). Each bar's reach is bounded by the nearest *shorter* bar on each side.
- **📊 Visual (MOST IMPORTANT):**
```
heights = [2, 1, 5, 6, 2, 3]      (append a 0 sentinel to flush all)
Stack holds indices with INCREASING heights.
When curr height < top, pop and compute area: that bar's rectangle ends here.

           6 _
       5 _| |
      |   | |        3
   2 _|   | |   2 _|
  |   | 1 | |  |   |
  |   |_| | |  |   |
  idx:0  1 2 3   4   5

i  h   action                                   stack(idx)   maxArea
-  --  --------------------------------------    ----------   -------
0  2   push                                      [0]
1  1   1<2 pop0: h=2,width=i=1 ->area2           []           2
       push1                                     [1]
2  5   push                                      [1,2]
3  6   push                                      [1,2,3]
4  2   2<6 pop3: h=6,width=i-stk.top-1=4-2-1=1 ->6 [1,2]      6
       2<5 pop2: h=5,width=4-1-1=2 ->10          [1]          10
       push4                                     [1,4]
5  3   push                                      [1,4,5]
6  0   (sentinel) pop5 h=3 w=6-4-1=1 ->3         [1,4]        10
       pop4 h=2 w=6-1-1=4 ->8                    [1]          10
       pop1 h=1 w=6 ->6                          []           10
answer = 10   (bars 5,6 of heights 5 and 6 -> width 2 x height 5)
```
- **🧠 Idea + template:** Keep indices of increasing bars; when a shorter bar arrives, each popped bar's rectangle is fixed — its width spans from the new bar back to the previous shorter bar.
```java
int largestRectangleArea(int[] h) {
    Deque<Integer> st = new ArrayDeque<>();   // indices, heights INCREASING
    int max = 0, n = h.length;
    for (int i = 0; i <= n; i++) {
        int cur = (i == n) ? 0 : h[i];        // 0 sentinel flushes the stack
        while (!st.isEmpty() && cur < h[st.peek()]) {
            int height = h[st.pop()];
            int width  = st.isEmpty() ? i : i - st.peek() - 1;
            max = Math.max(max, height * width);
        }
        st.push(i);
    }
    return max;
}
```
- **📝 Example:** *Largest Rectangle in Histogram (LC 84).* Maintain an increasing-height index stack; pop on a shorter bar and compute `height * (i - newTop - 1)`; a trailing 0 flushes leftovers.
- **⏱️ Complexity:** Time O(n), Space O(n).
- **💡 Remember-it tip:** "Each bar waits until a shorter one closes its rectangle." Width = gap between the two surrounding shorter bars. Drill: *Maximal Rectangle (LC 85, build histogram per row)*, *Trapping Rain Water (LC 42, stack variant)*.

---

### Simulation / Backtracking Helper

[🔝 Back to index](#index)
- **🔍 Recognize:** "process and undo", "collapse adjacent pairs", "asteroid collision", "remove k to make smallest", "decode nested string `3[a2[c]]`", "build result while folding from the left". A stack acts as the *partial result you can pop back* (push state, undo on a trigger).
- **📊 Visual (MOST IMPORTANT):**
```
Asteroid Collision  [5, 10, -5]   (+ moves right, - moves left; collide when ... +  then -)
Stack = survivors so far. A negative may pop smaller positives off the top.

a    action                                          stack
---  ----------------------------------------------  ----------
5    push (moving right)                              [5]
10   push                                             [5, 10]
-5   top=10 (>0) and |-5|=5 < 10  -> -5 explodes      [5, 10]   (-5 dies, nothing pushed)
end  survivors                                        [5, 10]

Trickier  [8, -8]:
8    push                                             [8]
-8   top=8, equal size -> BOTH explode (pop 8)        []        (push nothing)
end                                                   []

Rule when current = -v meets a positive top p:
  p <  v : pop p, keep checking      p == v : pop p, current dies
  p >  v : current dies              (no positive top): push current
```
- **🧠 Idea + template:** Treat the stack as the evolving answer; the new element interacts with the top, popping/cancelling until it settles or is itself discarded.
```java
int[] asteroidCollision(int[] as) {
    Deque<Integer> st = new ArrayDeque<>();
    for (int a : as) {
        boolean alive = true;
        while (alive && a < 0 && !st.isEmpty() && st.peek() > 0) {
            if (st.peek() < -a)      st.pop();          // top smaller -> it dies, keep checking
            else if (st.peek() == -a){ st.pop(); alive = false; } // equal -> both die
            else                      alive = false;    // top bigger -> a dies
        }
        if (alive) st.push(a);
    }
    int[] res = new int[st.size()];                     // stack is bottom->top order
    for (int i = res.length - 1; i >= 0; i--) res[i] = st.pop();
    return res;
}
```
- **📝 Example:** *Asteroid Collision (LC 735).* Push right-movers; a left-mover collides with positive tops, popping smaller ones, dying on a bigger one, mutual-destruction on equal.
- **⏱️ Complexity:** Time O(n), Space O(n).
- **💡 Remember-it tip:** "The stack is your undo button." If a new item cancels recent work, a stack folds it cleanly. Drill: *Decode String (LC 394, push counts+strings)*, *Remove K Digits (LC 402, monotonic+budget)*, *Simplify Path (LC 71)*.

---

## Linked List

A linked list is just nodes wired by `next` pointers. Almost every interview problem reduces to **moving 2-3 pointers carefully** and **not losing the rest of the list** when you rewire a link. The golden rule drawn below: before you overwrite `cur.next`, save it.

---

### In-place Reversal

[🔝 Back to index](#index)
- **🔍 Recognize:** "reverse the list / sublist", "reverse in groups of k", "reverse between positions m and n", "swap nodes in pairs", or any ask to flip direction using **O(1) extra space** (no array/stack copy).

- **📊 Visual (MOST IMPORTANT):** reverse `1 -> 2 -> 3 -> null`. Three pointers `prev`, `cur`, `nxt` walk forward; each step flips one arrow backward.

```
init:   prev=null   cur=1 -> 2 -> 3 -> null

step1:  save nxt=2; flip 1.next -> null
        null <- 1     2 -> 3 -> null
                ^prev  ^cur(now 2)

step2:  save nxt=3; flip 2.next -> 1
        null <- 1 <- 2     3 -> null
                     ^prev  ^cur(now 3)

step3:  save nxt=null; flip 3.next -> 2
        null <- 1 <- 2 <- 3     null
                          ^prev  ^cur(null) -> STOP

return prev  ==>  3 -> 2 -> 1 -> null
```

- **🧠 Idea + template:** Walk forward flipping each `next` to point backward; `prev` ends as the new head.

```java
ListNode reverse(ListNode head) {
    ListNode prev = null, cur = head;
    while (cur != null) {
        ListNode nxt = cur.next; // 1. save rest
        cur.next = prev;         // 2. flip arrow
        prev = cur;              // 3. advance prev
        cur = nxt;               // 4. advance cur
    }
    return prev;                 // new head
}
```

- **📝 Example:** *Reverse Linked List II* (LC 92) — walk to node before position `m`, reverse exactly `n - m + 1` nodes with the loop above, then stitch the reversed chunk back to the head and tail segments.

- **⏱️ Complexity:** Time `O(n)` (single pass), Space `O(1)`.

- **💡 Remember-it tip:** Chant **"save, flip, advance, advance"**. Picture three fingers sliding right, each flipping one arrow. Drill: *Reverse Linked List* (LC 206), *Reverse Nodes in k-Group* (LC 25).

---

### Merging Two Sorted Lists

[🔝 Back to index](#index)
- **🔍 Recognize:** "merge two sorted lists", "merge k sorted lists", "combine sorted sequences", "sort a list" (merge sort step). Inputs are already sorted and you must keep output sorted.

- **📊 Visual (MOST IMPORTANT):** merge `A: 1 -> 4` and `B: 2 -> 3`. A `dummy` node anchors the head; `tail` always points at the last stitched node and grabs the smaller front.

```
dummy -> ?      A:1 -> 4     B:2 -> 3
 tail^

cmp 1<2: take A1
dummy -> 1      A:4          B:2 -> 3
         tail^

cmp 4>2: take B2
dummy -> 1 -> 2      A:4     B:3
              tail^

cmp 4>3: take B3
dummy -> 1 -> 2 -> 3      A:4     B:null
                   tail^

B empty: attach rest of A
dummy -> 1 -> 2 -> 3 -> 4 -> null

return dummy.next  ==>  1 -> 2 -> 3 -> 4
```

- **🧠 Idea + template:** Use a dummy head; repeatedly append the smaller front node, then attach whatever list is left over.

```java
ListNode merge(ListNode a, ListNode b) {
    ListNode dummy = new ListNode(0), tail = dummy;
    while (a != null && b != null) {
        if (a.val <= b.val) { tail.next = a; a = a.next; }
        else                { tail.next = b; b = b.next; }
        tail = tail.next;
    }
    tail.next = (a != null) ? a : b; // attach leftover
    return dummy.next;
}
```

- **📝 Example:** *Merge Two Sorted Lists* (LC 21) — exactly the template. For *Merge k Sorted Lists* (LC 23), pairwise-merge or push heads into a min-heap and pop the smallest each step.

- **⏱️ Complexity:** Time `O(n + m)`, Space `O(1)` (reuses existing nodes; `O(log k)`/heap for k-way).

- **💡 Remember-it tip:** **"Dummy anchors, tail stitches."** The dummy spares you the special-case for the first node. Drill: *Merge k Sorted Lists* (LC 23), *Sort List* (LC 148).

---

### Addition of Numbers

[🔝 Back to index](#index)
- **🔍 Recognize:** "add two numbers represented by linked lists", "each node holds one digit", "sum digit by digit", mentions of **carry**. Digits may be stored forward or reversed.

- **📊 Visual (MOST IMPORTANT):** add `342 + 465` stored reversed as `A: 2 -> 4 -> 3` and `B: 5 -> 6 -> 4`. Walk both, summing with a running `carry`.

```
carry=0
pos0:  2 + 5 + 0 = 7   -> digit 7, carry 0   => 7
pos1:  4 + 6 + 0 = 10  -> digit 0, carry 1   => 7 -> 0
pos2:  3 + 4 + 1 = 8   -> digit 8, carry 0   => 7 -> 0 -> 8
end:   carry 0 -> stop

result: 7 -> 0 -> 8   (reads as 807 = 342 + 465)
```

```
digit = sum % 10     carry = sum / 10
 sum=10  -> 10%10=0, 10/10=1   (write 0, carry 1)
 sum=7   ->  7%10=7,  7/10=0   (write 7, carry 0)
```

- **🧠 Idea + template:** Walk both lists with a carry; at each step `sum = a + b + carry`, append `sum % 10`, propagate `sum / 10`. Loop while either node or the carry remains.

```java
ListNode addTwoNumbers(ListNode a, ListNode b) {
    ListNode dummy = new ListNode(0), tail = dummy;
    int carry = 0;
    while (a != null || b != null || carry != 0) {
        int sum = carry;
        if (a != null) { sum += a.val; a = a.next; }
        if (b != null) { sum += b.val; b = b.next; }
        carry = sum / 10;
        tail.next = new ListNode(sum % 10);
        tail = tail.next;
    }
    return dummy.next;
}
```

- **📝 Example:** *Add Two Numbers* (LC 2) — exactly the template (digits reversed, so least-significant first). For *Add Two Numbers II* (LC 445, forward order), reverse both lists first or push onto stacks, then add.

- **⏱️ Complexity:** Time `O(max(n, m))`, Space `O(max(n, m))` for the result list.

- **💡 Remember-it tip:** **"% gives the digit, / gives the carry."** Don't forget the trailing carry (e.g. `5 + 5 = 10` needs an extra node). Drill: *Add Two Numbers* (LC 2), *Add Two Numbers II* (LC 445).

---

### Reordering/Partitioning

[🔝 Back to index](#index)
- **🔍 Recognize:** "reorder list" (`L0 -> Ln -> L1 -> Ln-1 ...`), "partition around value x", "group odd/even positions", "rearrange so smaller-than-x come first" while **preserving relative order** and using O(1) space.

- **📊 Visual (MOST IMPORTANT):** partition `1 -> 4 -> 3 -> 2 -> 5` around `x = 3`. Build two chains — `less` (`< x`) and `ge` (`>= x`) — then splice `less -> ge`.

```
x=3, walk each node, append to a bucket:

node 1 (<3): less:  1
node 4 (>=3): ge:   4
node 3 (>=3): ge:   4 -> 3
node 2 (<3): less:  1 -> 2
node 5 (>=3): ge:   4 -> 3 -> 5

stitch:  less_tail.next = ge_head ; ge_tail.next = null

less: 1 -> 2  ++  ge: 4 -> 3 -> 5
result: 1 -> 2 -> 4 -> 3 -> 5
        \_<x_/   \__>=x___/   (relative order preserved)
```

For **Reorder List**, the combo is: find middle (slow/fast) -> reverse 2nd half -> merge-alternate the two halves:

```
1->2->3->4->5  =>  split 1->2->3 | 4->5
reverse 2nd:   4->5  =>  5->4
weave:         1->5->2->4->3
```

- **🧠 Idea + template:** Split into two dummy-anchored chains by a predicate, then join them; reset the final tail's `next` to `null`.

```java
ListNode partition(ListNode head, int x) {
    ListNode lessD = new ListNode(0), geD = new ListNode(0);
    ListNode less = lessD, ge = geD;
    for (ListNode cur = head; cur != null; cur = cur.next) {
        if (cur.val < x) { less.next = cur; less = less.next; }
        else             { ge.next = cur;   ge = ge.next;   }
    }
    ge.next = null;            // terminate the list
    less.next = geD.next;      // splice less -> ge
    return lessD.next;
}
```

- **📝 Example:** *Partition List* (LC 86) — exactly the template. *Reorder List* (LC 143) — find mid with slow/fast, reverse the second half (use the In-place Reversal template), then alternately weave the two halves.

- **⏱️ Complexity:** Time `O(n)`, Space `O(1)` (pointer rewiring, no new nodes).

- **💡 Remember-it tip:** **"Two buckets, then bridge."** Always null-terminate the last chain or you'll create a cycle. Drill: *Partition List* (LC 86), *Odd Even Linked List* (LC 328), *Reorder List* (LC 143).

---

## Tree Traversal (DFS and BFS)

### Level Order Traversal (BFS)

[🔝 Back to index](#index)

- **🔍 Recognize:** Phrases like "level by level", "each level", "row of the tree", "left-to-right per depth", "zigzag", "right side view", "minimum depth", "connect nodes at same level". Anything that groups nodes by **depth** screams BFS with a queue.

- **📊 Visual (MOST IMPORTANT):**
```
Tree:                Queue evolves (process one full level per outer loop):

        1            Level 0:  [1]            -> emit [1]
       / \                      pop 1, push 2,3
      2   3          Level 1:  [2,3]          -> emit [2,3]
     / \   \                    pop 2 push 4,5; pop 3 push 6
    4   5   6        Level 2:  [4,5,6]        -> emit [4,5,6]

Key trick: snapshot queue size = number of nodes on THIS level.

  size=1 |1|              <- one node this level
  size=2 |2|3|            <- drain exactly 2, enqueue their kids
  size=3 |4|5|6|          <- drain exactly 3

Result: [[1],[2,3],[4,5,6]]
```

- **🧠 Idea + template:** Use a queue; before draining, record `queue.size()` to capture exactly one level.
```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();              // freeze this level's count
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode n = q.poll();
            level.add(n.val);
            if (n.left  != null) q.offer(n.left);
            if (n.right != null) q.offer(n.right);
        }
        res.add(level);
    }
    return res;
}
```

- **📝 Example:** *Binary Tree Level Order Traversal (LC 102)* — BFS with a queue, snapshot `size` each iteration, collect each level into its own list.

- **⏱️ Complexity:** Time **O(n)** (each node enqueued/dequeued once); Space **O(n)** for the queue (up to ~n/2 nodes on the widest level).

- **💡 Remember-it tip:** "**Size first, then drain**" — freeze `q.size()` before the inner loop. Drill: *Zigzag Level Order (LC 103)* (reverse alternate levels), *Right Side View (LC 199)* (take last node per level).

---

### DFS Overview (Pre/In/Post)

[🔝 Back to index](#index)

- **🔍 Recognize:** "Explore a path to the bottom before backtracking", needing **root vs. children ordering**, problems mentioning sorted order of a BST (inorder), copying/serializing top-down (preorder), or deleting/freeing/aggregating from children up (postorder).

- **📊 Visual (MOST IMPORTANT):**
```
Tree:          The ONLY difference is WHERE you "visit" (V) relative to recursing L and R:

      1        Preorder  (V,L,R):  Node BEFORE children   -> 1 2 4 5 3
     / \       Inorder   (L,V,R):  Node BETWEEN children  -> 4 2 5 1 3
    2   3      Postorder (L,R,V):  Node AFTER children    -> 4 5 2 3 1
   / \
  4   5

Think of one DFS walk hugging the left wall; you "touch" each node 3 times:

        (pre)1(in)        (post)
         /        \
   (pre)2(in)(post) (pre)3(in)(post)

  Preorder  = stamp on FIRST touch  (going down)
  Inorder   = stamp on SECOND touch (coming up from left)
  Postorder = stamp on THIRD touch  (leaving to parent)
```

- **🧠 Idea + template:** Same recursion skeleton; move the "visit" line to change order.
```java
void dfs(TreeNode node, List<Integer> out) {
    if (node == null) return;
    // visit(node);  // <-- PREORDER position
    dfs(node.left, out);
    // visit(node);  // <-- INORDER position
    dfs(node.right, out);
    // visit(node);  // <-- POSTORDER position
}
```

- **📝 Example:** *Validate Binary Search Tree (LC 98)* — an **inorder** walk must yield strictly increasing values; track `prev` and fail if `node.val <= prev`.

- **⏱️ Complexity:** Time **O(n)**; Space **O(h)** recursion stack (h = height; O(n) skewed, O(log n) balanced).

- **💡 Remember-it tip:** "**PRe = Print Root Early; POst = Print On Surfacing; IN = In the middle.**" Drill: *Binary Tree Paths (LC 257)* (preorder), *Kth Smallest in BST (LC 230)* (inorder).

---

### Recursive Preorder

[🔝 Back to index](#index)

- **🔍 Recognize:** "Process the node first, then descend", copying/cloning a tree, building a prefix/path from root downward, serializing in a top-down way, "root, left, right".

- **📊 Visual (MOST IMPORTANT):**
```
Tree:           Visit order numbered (root FIRST):

      A(1)              1. A   <- stamp on arrival
     /   \              2. B   <- go all the way left
   B(2)   C(5)          3. D
   /  \                 4. E   <- B's subtree done
 D(3)  E(4)             5. C   <- now the right subtree

Path of the pointer (V=visit):
   A:V -> B:V -> D:V -> back -> E:V -> back -> back -> C:V

Output: A B D E C   (Root, Left, Right)
```

- **🧠 Idea + template:** Stamp the node **before** recursing into children.
```java
void preorder(TreeNode node, List<Integer> out) {
    if (node == null) return;
    out.add(node.val);          // ROOT
    preorder(node.left, out);   // LEFT
    preorder(node.right, out);  // RIGHT
}
```

- **📝 Example:** *Binary Tree Preorder Traversal (LC 144)* — add the value on entry, recurse left, recurse right; or use an explicit stack pushing **right then left** so left pops first.

- **⏱️ Complexity:** Time **O(n)**; Space **O(h)** stack.

- **💡 Remember-it tip:** "**Root opens the show**" — the root is always the very first value. Drill: *Clone a Tree / Copy with Random Pointer*, *Construct Tree from Preorder+Inorder (LC 105)*.

---

### Recursive Inorder

[🔝 Back to index](#index)

- **🔍 Recognize:** Anything involving a **BST in sorted order**, "kth smallest/largest", validating a BST, finding successor/predecessor, "left, root, right". Inorder of a BST = ascending sequence.

- **📊 Visual (MOST IMPORTANT):**
```
BST:            Visit order numbered (left FIRST, then root, then right):

      4(3)             1. 1   <- dive left-most first
     /   \             2. 2
   2(2)   6(5)         3. 3 (the 4 node) <- root stamped AFTER left subtree
   /  \                4. 4 (the value)
 1(1) 3(?)             5. 6

Left-wall dive then surface:
   go to 1 (leftmost) -> stamp 1
   surface to 2       -> stamp 2
   into 3             -> stamp 3
   surface to 4(root) -> stamp 4
   into right -> 6    -> stamp 6

Output: 1 2 3 4 6   <- SORTED! (hallmark of inorder on a BST)
```

- **🧠 Idea + template:** Recurse left fully, stamp node, then recurse right.
```java
void inorder(TreeNode node, List<Integer> out) {
    if (node == null) return;
    inorder(node.left, out);    // LEFT
    out.add(node.val);          // ROOT (in the middle)
    inorder(node.right, out);   // RIGHT
}
```

- **📝 Example:** *Kth Smallest Element in a BST (LC 230)* — inorder traverse, decrement a counter; the node where it hits 0 is the answer (can stop early).

- **⏱️ Complexity:** Time **O(n)** (or O(h+k) with early stop); Space **O(h)** stack.

- **💡 Remember-it tip:** "**Inorder on a BST = sorted, every time.**" If you need ascending order from a BST, reach for inorder. Drill: *Validate BST (LC 98)*, *Convert BST to Sorted Doubly Linked List (LC 426)*.

---

### Recursive Postorder

[🔝 Back to index](#index)

- **🔍 Recognize:** "Children before parent", computing a value that **depends on subtree results** (height, diameter, sum, "is balanced"), deleting/freeing nodes bottom-up, "left, right, root". Whenever a node's answer needs its kids' answers first.

- **📊 Visual (MOST IMPORTANT):**
```
Tree:           Visit order numbered (root LAST):

      A(5)             1. D   <- left subtree, deepest left first
     /   \             2. E
   B(3)   C(4)         3. B   <- B stamped AFTER both its kids
   /  \                4. C
 D(1)  E(2)            5. A   <- root stamped LAST of all

Bottom-up aggregation (e.g., height):
   h(D)=1  h(E)=1
   h(B)=1+max(h(D),h(E))=2   <- needs kids first!
   h(C)=1
   h(A)=1+max(h(B),h(C))=3

Output: D E B C A   (Left, Right, Root)
```

- **🧠 Idea + template:** Recurse both children first, combine, then stamp/return the node.
```java
int height(TreeNode node) {            // classic postorder aggregation
    if (node == null) return 0;
    int left  = height(node.left);     // LEFT result
    int right = height(node.right);    // RIGHT result
    return 1 + Math.max(left, right);  // ROOT uses children (postorder)
}
```

- **📝 Example:** *Diameter of Binary Tree (LC 543)* — postorder return each subtree's height; at every node update `best = max(best, left + right)`; return `1 + max(left, right)`.

- **⏱️ Complexity:** Time **O(n)**; Space **O(h)** stack.

- **💡 Remember-it tip:** "**Root signs off last**" — children must report before the parent decides. Drill: *Balanced Binary Tree (LC 110)*, *Maximum Path Sum (LC 124)*.

---

### Lowest Common Ancestor (LCA)

[🔝 Back to index](#index)

- **🔍 Recognize:** "Lowest/deepest common ancestor", "nearest shared parent of two nodes", "where do two paths from root merge", distance between two nodes (via LCA). For a **BST**, exploit ordering; for a general tree, use postorder.

- **📊 Visual (MOST IMPORTANT):**
```
Find LCA(4, 5):

        3                 Search returns "found one of p/q" up the tree:
       / \
      5   1               at 6: null   at 2: null
     / \                  at 7: null
    6   2   p=5, q=4      at 4: FOUND(4) ─┐
       / \                                ├─> both sides non-null at 2 => LCA = 2
      7   4(q)            at 0? n/a        │
            ^             at 5: FOUND(5) ──┘
        (node 0 omitted)

Mechanism: a node is the LCA when p is found in ONE subtree and q in the OTHER
(or the node itself equals p/q and the other is below).

   left=find(2.left=6) -> null... actually 5 is elsewhere; trace simplified:
   left subtree of 2 has 7, right has 4 -> q=4 found right
   p=5 found up at 3's left -> first node seeing BOTH below it = 3? 
   (Use the real subtree: here LCA(5,4)=3 if 4 under 2 under... )

CLEAN small example  LCA(4,5):
        1
       / \
      2   3
     / \
    4   5        4 in 2.left, 5 in 2.right  => first node with both = 2  => LCA=2
```

- **🧠 Idea + template:** Return non-null when you find p or q; the node receiving non-null from **both** sides is the LCA.
```java
TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode left  = lca(root.left,  p, q);
    TreeNode right = lca(root.right, p, q);
    if (left != null && right != null) return root;  // p & q on opposite sides
    return left != null ? left : right;              // both on one side (or none)
}

// BST shortcut: walk down using ordering
TreeNode lcaBST(TreeNode root, TreeNode p, TreeNode q) {
    while (root != null) {
        if (p.val < root.val && q.val < root.val)      root = root.left;
        else if (p.val > root.val && q.val > root.val) root = root.right;
        else return root;   // split point = LCA
    }
    return null;
}
```

- **📝 Example:** *Lowest Common Ancestor of a Binary Tree (LC 236)* — postorder; bubble up p/q; first node seeing both below it is the LCA. (For *LC 235*, the BST version, use the split-point walk in O(h).)

- **⏱️ Complexity:** General tree: Time **O(n)**, Space **O(h)**. BST: Time **O(h)**, Space **O(1)** iterative.

- **💡 Remember-it tip:** "**Both sides report back ⇒ you're the meeting point.**" Drill: *LCA in a BST (LC 235)*, *LCA with parent pointers (LC 1650)*.

---

### Serialization and Deserialization

[🔝 Back to index](#index)

- **🔍 Recognize:** "Encode a tree to a string and rebuild it", "serialize/deserialize", persisting/transmitting a tree, "design a codec". Preorder + explicit null markers is the go-to.

- **📊 Visual (MOST IMPORTANT):**
```
Tree:                 Preorder with null markers (# = null):

      1                serialize (V,L,R):
     / \                 1, then left(2), then right(3)
    2   3                2 has no kids -> 2,#,#
       / \               3 -> 3, left(4), right(5)
      4   5

  String: "1,2,#,#,3,4,#,#,5,#,#"

deserialize — consume tokens left-to-right, recursion rebuilds same shape:

  tokens: 1 2 # # 3 4 # # 5 # #
          ^build 1
            ^build 2
              ^#->left null  ^#->right null  (2 done)
                  ^build 3
                    ^build 4 (# # -> leaf)
                          ^build 5 (# # -> leaf)

  Each value -> new node; each '#' -> null. Order of consumption == preorder.
```

- **🧠 Idea + template:** Preorder DFS writing `#` for nulls; deserialize by consuming the same stream.
```java
// SERIALIZE (preorder)
void ser(TreeNode n, StringBuilder sb) {
    if (n == null) { sb.append("#,"); return; }
    sb.append(n.val).append(",");
    ser(n.left, sb);
    ser(n.right, sb);
}

// DESERIALIZE (consume tokens in same preorder)
int idx = 0; String[] t;
TreeNode build() {
    String s = t[idx++];
    if (s.equals("#")) return null;
    TreeNode node = new TreeNode(Integer.parseInt(s));
    node.left  = build();   // LEFT
    node.right = build();   // RIGHT
    return node;
}
// usage: t = data.split(","); idx = 0; return build();
```

- **📝 Example:** *Serialize and Deserialize Binary Tree (LC 297)* — preorder with `#` null markers into a comma-separated string; rebuild by recursively consuming tokens in the same order.

- **⏱️ Complexity:** Time **O(n)** both ways; Space **O(n)** for the string plus **O(h)** recursion.

- **💡 Remember-it tip:** "**Same order in, same order out**" — serialize and deserialize must agree on traversal order, and `#` keeps the shape. Drill: *Serialize/Deserialize BST (LC 449)* (skip null markers using BST bounds), *Serialize N-ary Tree (LC 428)*.

---

## Heap (Priority Queue)

### Top K Elements (Selection/Frequency)

[🔝 Back to index](#index)
- **🔍 Recognize:** "top K", "K largest/smallest", "K most frequent", "K closest", "Kth largest". You need a *partial* ordering of the best K — not a full sort. Streaming data where you can't hold everything sorted.
- **📊 Visual (MOST IMPORTANT):**
```
Goal: K=2 LARGEST of [5, 1, 8, 3, 9]
Trick: use a MIN-heap of size K. The root is the SMALLEST
of your current winners — your "bouncer" at the door.

push 5      push 1       push 8 (size>K!)        push 3 (3>1? yes)
  [5]    →   [1]     →    [1]   pop root 1   →    [3]   pop root 3?
            /  \         /  \   --------→        /  \   no, 3 is new root
          (5)            5    8    [5]          5    8   wait:
                                   |  \         after pop 1, heap=[5,8]
                                   5   8        push 3 -> [3,8,5]? size3>2
                                                pop min 3 -> [5,8]

push 9 (9>5? yes): pop 5, push 9 -> heap=[8,9]
                                          ↑root=8
Final heap holds the K winners: {8, 9}   ← answer (any order)

Rule each step:  if heap.size() > K  ->  poll()  (evict weakest)
```
- **🧠 Idea + template:** For K *largest*, keep a **min-heap of size K** (root = weakest survivor to evict); for K *smallest*, keep a **max-heap of size K**.
```java
// K largest elements
PriorityQueue<Integer> pq = new PriorityQueue<>(); // min-heap
for (int x : nums) {
    pq.offer(x);
    if (pq.size() > k) pq.poll();   // evict current minimum
}
// pq now holds the k largest; pq.peek() == kth largest

// K most frequent: count first, then heap on frequency
Map<Integer,Integer> freq = new HashMap<>();
for (int x : nums) freq.merge(x, 1, Integer::sum);
PriorityQueue<int[]> h = new PriorityQueue<>((a,b) -> a[1] - b[1]); // min by freq
for (var e : freq.entrySet()) {
    h.offer(new int[]{e.getKey(), e.getValue()});
    if (h.size() > k) h.poll();
}
```
- **📝 Example:** *Kth Largest Element in an Array* (LC 215). Push all into a size-K min-heap; after the pass the root `peek()` is the Kth largest. (Quickselect is O(n) avg alternative.)
- **⏱️ Complexity:** Time `O(n log k)`, Space `O(k)`. (Full sort would be `O(n log n)`.)
- **💡 Remember-it tip:** "Largest ⇒ MIN-heap" feels backwards — the min-heap root is the **bouncer** who kicks out anyone smaller. Drill: *Top K Frequent Elements* (LC 347), *K Closest Points to Origin* (LC 973).

---

### K-way Merge

[🔝 Back to index](#index)
- **🔍 Recognize:** Merging "K sorted lists/arrays/streams", "smallest range covering K lists", matrix with sorted rows/columns, "Kth smallest in sorted matrix". You repeatedly need the *global minimum* across several already-sorted sequences.
- **📊 Visual (MOST IMPORTANT):**
```
Merge 3 sorted lists. Heap holds ONE frontier node per list.
L0: 1 → 4 → 7
L1: 2 → 5
L2: 3 → 6 → 8

Seed heap with each list head (value, listIdx, nodeIdx):
heap = { (1,L0) (2,L1) (3,L2) }      output: []

poll (1,L0) ─ push next of L0 = 4
heap = { (2,L1) (3,L2) (4,L0) }      output: [1]

poll (2,L1) ─ push next of L1 = 5
heap = { (3,L2) (4,L0) (5,L1) }      output: [1,2]

poll (3,L2) ─ push next of L2 = 6
heap = { (4,L0) (5,L1) (6,L2) }      output: [1,2,3]

... continue: poll 4→push7, poll5(L1 done), poll6→push8 ...
output: [1,2,3,4,5,6,7,8]
        ↑ heap size never exceeds K (= #lists)
```
- **🧠 Idea + template:** Keep a heap with exactly **one "frontier" element per list**; after polling the min, push that list's *next* element. Heap size stays ≤ K.
```java
ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> pq =
        new PriorityQueue<>((a, b) -> a.val - b.val);
    for (ListNode node : lists)
        if (node != null) pq.offer(node);     // seed: one head per list

    ListNode dummy = new ListNode(0), tail = dummy;
    while (!pq.isEmpty()) {
        ListNode min = pq.poll();
        tail.next = min;
        tail = tail.next;
        if (min.next != null) pq.offer(min.next); // refill from same list
    }
    return dummy.next;
}
```
- **📝 Example:** *Merge k Sorted Lists* (LC 23). Seed the heap with all K heads; repeatedly poll the smallest, append it, and push its successor until the heap drains.
- **⏱️ Complexity:** Time `O(N log k)` where N = total elements; Space `O(k)` for the heap.
- **💡 Remember-it tip:** Picture K conveyor belts feeding one gate — the heap only ever sees the **front item of each belt**. Drill: *Kth Smallest Element in a Sorted Matrix* (LC 378), *Smallest Range Covering Elements from K Lists* (LC 632).

---

### Two Heaps for Median

[🔝 Back to index](#index)
- **🔍 Recognize:** "median of a data stream", "median of sliding window", needing the *middle* element(s) as numbers arrive, or any "balance the lower half vs upper half" requirement. Continuous insert + query-median.
- **📊 Visual (MOST IMPORTANT):**
```
Split the data at the median. LEFT half = MAX-heap (its
root is the biggest small number). RIGHT half = MIN-heap
(its root is the smallest big number). Roots kiss at the middle.

        maxHeap (low)        minHeap (high)
        biggest on top        smallest on top
            ┌───┐                ┌───┐
            │ ? │  <-- median --> │ ? │
            └───┘                └───┘

add 5:  L=[5]            R=[]          median=5
add 15: L=[5]            R=[15]        median=(5+15)/2=10
add 1:  L=[5,1]→top5     R=[15]        median=5   (L has 2, R has1)
add 3:  push3→L=[5,3,1], top5;
        rebalance: L too big → move top(5) to R
        L=[3,1]top3      R=[5,15]top5  median=(3+5)/2=4

INVARIANT every step:
  size(L) == size(R)   (even total) -> median = avg of roots
  size(L) == size(R)+1 (odd total)  -> median = L.root
Push rule: add to L, then move L.top to R, then if R bigger move R.top back.
```
- **🧠 Idea + template:** Two balanced heaps with roots meeting at the median: a **max-heap for the lower half**, a **min-heap for the upper half**; keep sizes within 1.
```java
PriorityQueue<Integer> lo = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
PriorityQueue<Integer> hi = new PriorityQueue<>();                            // min-heap

void addNum(int num) {
    lo.offer(num);              // 1) always push to lower
    hi.offer(lo.poll());        // 2) balance: top of lo -> hi
    if (hi.size() > lo.size())  // 3) keep lo >= hi
        lo.offer(hi.poll());
}
double findMedian() {
    return lo.size() > hi.size()
        ? lo.peek()
        : (lo.peek() + hi.peek()) / 2.0;
}
```
- **📝 Example:** *Find Median from Data Stream* (LC 295). Maintain the two heaps on every `addNum`; `findMedian` reads one or both roots in O(1).
- **⏱️ Complexity:** `addNum` `O(log n)`, `findMedian` `O(1)`; Space `O(n)`.
- **💡 Remember-it tip:** Two heaps **back-to-back like cupped hands**, big-enders and small-enders touching at the median. Drill: *Sliding Window Median* (LC 480), *IPO* (LC 502, two-heap greedy variant).

---

### Scheduling / Minimum Cost (Greedy + PQ)

[🔝 Back to index](#index)
- **🔍 Recognize:** "minimum cost to…", "maximum number of …", "schedule tasks/meetings", "merge stones/ropes", overlapping intervals needing the *earliest-freeing* resource, "process the cheapest/most-urgent next". Greedy choice = always grab the current best, and a PQ surfaces it in O(log n).
- **📊 Visual (MOST IMPORTANT):**
```
Meeting Rooms II: how many rooms? PQ holds END times of
busy rooms; its root = the room that frees SOONEST.

meetings sorted by START: [0,30] [5,10] [15,20]

[0,30]: no room frees before 0 → open room.  PQ(ends)=[30]
[5,10]: root=30 > 5  (still busy) → open room. PQ=[10,30]
[15,20]: root=10 ≤ 15 (freed!) → REUSE it.
         poll 10, push 20.                    PQ=[20,30]

rooms needed = max PQ size reached = 2
   ┌──────────────── timeline ───────────────┐
R1 |0========================30|             |
R2     |5==10|     |15===20|                  |
        ↑reused after 10 frees at 15
```
- **🧠 Idea + template:** Sort by the natural order (start time / deadline), then use a PQ to always pull the **cheapest / earliest-freeing / most-urgent** candidate at each step.
```java
// Meeting Rooms II — minimum rooms
int minMeetingRooms(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);   // by start
    PriorityQueue<Integer> ends = new PriorityQueue<>(); // min-heap of end times
    for (int[] m : intervals) {
        if (!ends.isEmpty() && ends.peek() <= m[0])
            ends.poll();          // a room freed in time → reuse it
        ends.offer(m[1]);         // occupy a room until this end
    }
    return ends.size();           // rooms alive simultaneously
}
```
- **📝 Example:** *Meeting Rooms II* (LC 253). Sort by start; for each meeting, free the earliest-ending room if it's available, else allocate a new one; the heap size is the answer.
- **⏱️ Complexity:** Time `O(n log n)` (sort + n heap ops), Space `O(n)`.
- **💡 Remember-it tip:** PQ = a **"who's next?" line that auto-reorders** by cost/time — sort sets the order you *consider* items, the heap picks the best *available* one. Drill: *Task Scheduler* (LC 621), *Minimum Cost to Connect Sticks* (LC 1167), *Reorganize String* (LC 767).

---

## Graph Traversal (DFS and BFS)

### DFS/BFS Connected Components

[🔝 Back to index](#index)
- **🔍 Recognize:** "number of islands", "number of provinces/friend circles", "connected groups", "count regions", undirected graph or grid where you must group reachable nodes. Signal: count how many separate clusters exist, or size of each cluster.
- **📊 Visual (MOST IMPORTANT):**
```
Grid (1 = land):          BFS flood from each unvisited land cell:

  c0 c1 c2 c3              Start (0,0)='1' -> component #1
r0  1  1  0  0             enqueue (0,0), mark visited
r1  1  0  0  1                pop (0,0) -> push (1,0),(0,1)
r2  0  0  1  1                pop (1,0) -> neighbors visited
r3  0  0  1  0                pop (0,1) -> neighbors done   [comp #1 = {(0,0),(1,0),(0,1)}]

scan continues... (1,3)='1' unvisited -> component #2  {(1,3)}
scan continues... (2,2)='1' unvisited -> component #3  {(2,2),(2,3),(3,2)}

   count = 3   <- answer

Visited map after run:
  V  V  .  .
  V  .  .  V
  .  .  V  V
  .  .  V  .
```
- **🧠 Idea + template:** Iterate every node; each time you hit an unvisited one, launch a full DFS/BFS to "paint" its whole component, then increment the count.
```java
int numComponents(char[][] g) {
    int rows = g.length, cols = g[0].length, count = 0;
    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
    for (int r = 0; r < rows; r++)
        for (int c = 0; c < cols; c++)
            if (g[r][c] == '1') {            // unvisited land
                count++;
                Deque<int[]> q = new ArrayDeque<>();
                q.add(new int[]{r, c}); g[r][c] = '0'; // mark
                while (!q.isEmpty()) {
                    int[] cell = q.poll();
                    for (int[] d : dirs) {
                        int nr = cell[0]+d[0], nc = cell[1]+d[1];
                        if (nr>=0 && nr<rows && nc>=0 && nc<cols && g[nr][nc]=='1') {
                            g[nr][nc] = '0';   // mark visited
                            q.add(new int[]{nr, nc});
                        }
                    }
                }
            }
    return count;
}
```
- **📝 Example:** *Number of Islands (LC 200).* Scan the grid; on each unvisited '1', BFS/DFS flood-fill the island to '0' and add 1 to the island count. Each cell is visited once.
- **⏱️ Complexity:** Time `O(V + E)` (grid: `O(rows*cols)`); Space `O(V)` for the queue/recursion and visited set.
- **💡 Remember-it tip:** "Outer loop *finds* a new island, inner search *sinks* it." Drill: *Number of Provinces (LC 547)*, *Max Area of Island (LC 695)*.

### Kahn Topological Sort

[🔝 Back to index](#index)
- **🔍 Recognize:** "course schedule / prerequisites", "build order", "task ordering with dependencies", "is it possible to finish", any DAG where you must linearize so every edge points forward. BFS-flavored topo sort detects cycles too.
- **📊 Visual (MOST IMPORTANT):**
```
DAG (edge a->b means a before b):

   5 ---> 0 <--- 4
   |             |
   v             v
   2 ---> 3 ---> 1

indegree:  0:2  1:2  2:1  3:1  4:0  5:0

Start: queue = [5, 4]   (all indegree 0)
order=[]

pop 5 -> order=[5]; drop edges 5->0 (0:1), 5->2 (2:0) -> push 2
pop 4 -> order=[5,4]; drop 4->0 (0:0) push 0, 4->1 (1:1)
pop 2 -> order=[5,4,2]; drop 2->3 (3:0) push 3
pop 0 -> order=[5,4,2,0]; (0 has no out-edges)
pop 3 -> order=[5,4,2,0,3]; drop 3->1 (1:0) push 1
pop 1 -> order=[5,4,2,0,3,1]   queue empty

processed 6 == V  => valid topo order (no cycle)
```
- **🧠 Idea + template:** Repeatedly remove nodes with indegree 0 (no remaining prerequisites). If you can remove all `V` nodes, the order is a valid topo sort; if fewer, a cycle exists.
```java
int[] topoOrder(int n, int[][] edges) {        // edge {u,v} = u before v
    List<List<Integer>> adj = new ArrayList<>();
    for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    int[] indeg = new int[n];
    for (int[] e : edges) { adj.get(e[0]).add(e[1]); indeg[e[1]]++; }
    Deque<Integer> q = new ArrayDeque<>();
    for (int i = 0; i < n; i++) if (indeg[i] == 0) q.add(i);
    int[] order = new int[n]; int idx = 0;
    while (!q.isEmpty()) {
        int u = q.poll(); order[idx++] = u;
        for (int v : adj.get(u)) if (--indeg[v] == 0) q.add(v);
    }
    return idx == n ? order : new int[0];        // empty => cycle
}
```
- **📝 Example:** *Course Schedule II (LC 210).* Build adjacency + indegree from prerequisite pairs, BFS from all indegree-0 courses, append as you pop. If you ordered all `n` courses, return the order; else return `[]` (cycle).
- **⏱️ Complexity:** Time `O(V + E)`; Space `O(V + E)` for adjacency list, indegree array, and queue.
- **💡 Remember-it tip:** "Indegree 0 means *ready to go*; count popped vs V to smell a cycle." Drill: *Course Schedule (LC 207)*, *Alien Dictionary (LC 269)*.

### DFS Cycle Detection (Directed)

[🔝 Back to index](#index)
- **🔍 Recognize:** "detect a cycle in a directed graph", "can the schedule deadlock?", "is this a valid DAG?", dependency resolution where a back-edge means contradiction. Prefer this when you also want the recursion-stack insight (vs Kahn's counting).
- **📊 Visual (MOST IMPORTANT):**
```
Directed graph:  1 -> 2 -> 3 -> 1   (cycle!)  and  2 -> 4

state: 0=unvisited(WHITE) 1=in-stack(GRAY) 2=done(BLACK)

dfs(1): mark 1 GRAY  stack=[1]
  dfs(2): mark 2 GRAY  stack=[1,2]
    dfs(3): mark 3 GRAY  stack=[1,2,3]
      edge 3->1 : node 1 is GRAY  ===> BACK EDGE ===> CYCLE FOUND

Why GRAY matters:
   WHITE  -> not seen yet, recurse into it
   GRAY   -> currently on the recursion path  -> seeing it again = cycle
   BLACK  -> fully explored, safe to ignore (cross/forward edge, no cycle)

  [ . ] [G] [G] [G]      <- at moment of detection: 1,2,3 all GRAY (on stack)
   0     1   2   3
```
- **🧠 Idea + template:** Color nodes WHITE/GRAY/BLACK. During DFS, an edge to a GRAY node (still on the current recursion stack) is a back-edge = cycle. A BLACK node is finished and safe.
```java
int[] color;                 // 0=white,1=gray,2=black
List<List<Integer>> adj;
boolean hasCycle(int n) {
    color = new int[n];
    for (int i = 0; i < n; i++)
        if (color[i] == 0 && dfs(i)) return true;
    return false;
}
boolean dfs(int u) {
    color[u] = 1;                       // GRAY: on stack
    for (int v : adj.get(u)) {
        if (color[v] == 1) return true; // back-edge -> cycle
        if (color[v] == 0 && dfs(v)) return true;
    }
    color[u] = 2;                       // BLACK: done
    return false;
}
```
- **📝 Example:** *Course Schedule (LC 207).* Build a directed graph from prerequisites; run colored DFS. If any DFS reaches a GRAY node, a prerequisite cycle exists, so you cannot finish all courses.
- **⏱️ Complexity:** Time `O(V + E)`; Space `O(V)` for the color array plus recursion stack.
- **💡 Remember-it tip:** "GRAY on the path = trap; only WHITE→GRAY→BLACK is clean." Drill: *Course Schedule (LC 207)*, *Find Eventual Safe States (LC 802)*.

### Union-Find (Disjoint Set Union)

[🔝 Back to index](#index)
- **🔍 Recognize:** "are these two connected?", "number of connected components" with incremental edges, "redundant connection", "accounts merge", "detect cycle in undirected graph", dynamic connectivity / grouping queries. Signal: union pairs, then ask membership.
- **📊 Visual (MOST IMPORTANT):**
```
Union edges: (0-1), (1-2), (3-4)   parent[] starts as self:

idx:    0  1  2  3  4
parent: 0  1  2  3  4    (5 singletons)

union(0,1): find(0)=0, find(1)=1 -> parent[1]=0
parent: 0  0  2  3  4
                    forest:  0        2     3     4
                             |
                             1

union(1,2): find(1)=0, find(2)=2 -> parent[2]=0
parent: 0  0  0  3  4
                    forest:    0          3     4
                              / \
                             1   2

union(3,4): parent[4]=3
parent: 0  0  0  3  3

find(2): parent[2]=0 -> root 0
PATH COMPRESSION makes 2 point straight at root 0 (already does here).

connected(0,2)? find(0)==find(2)==0 -> TRUE
connected(0,4)? 0 != 3 -> FALSE
components = number of distinct roots = {0, 3} = 2
```
- **🧠 Idea + template:** Each set is a tree with a representative root. `find` walks to the root (with path compression); `union` links one root under the other (by rank/size). Two nodes are connected iff same root.
```java
class DSU {
    int[] parent, rank; int count;
    DSU(int n) {
        parent = new int[n]; rank = new int[n]; count = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }
    int find(int x) {                          // path compression
        while (parent[x] != x) { parent[x] = parent[parent[x]]; x = parent[x]; }
        return x;
    }
    boolean union(int a, int b) {              // union by rank
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;            // already together (cycle!)
        if (rank[ra] < rank[rb]) { int t = ra; ra = rb; rb = t; }
        parent[rb] = ra;
        if (rank[ra] == rank[rb]) rank[ra]++;
        count--;                               // one fewer component
        return true;
    }
}
```
- **📝 Example:** *Number of Provinces (LC 547).* Initialize DSU over `n` cities; for every `isConnected[i][j]==1`, `union(i,j)`. The final `count` (distinct roots) is the number of provinces.
- **⏱️ Complexity:** Time `O(E · α(V))` ≈ `O(E)` with path compression + union by rank (α = inverse Ackermann, effectively constant); Space `O(V)`.
- **💡 Remember-it tip:** "*Union* glues roots, *find* names your family; same name = same group." Drill: *Redundant Connection (LC 684)*, *Accounts Merge (LC 721)*.

### Dijkstra Algorithm

[🔝 Back to index](#index)
- **🔍 Recognize:** "shortest path" with **non-negative weighted** edges, "minimum cost / time to reach", "cheapest route", "network delay time", single-source shortest path on a positively weighted graph. No negatives → Dijkstra.
- **📊 Visual (MOST IMPORTANT):**
```
Weighted graph (source = A):

      4        1
   A ---> B ---> C
   |      |      ^
  1|     2|     5|
   v      v      |
   D ---> ...    |
        (A->D=1, D->B=2, B->C=1, A->B=4, A->C via... etc.)

Min-heap of (dist, node). dist[] starts INF except A=0.

pop (0,A)  -> settle A.  relax: B=4, D=1            heap:[(1,D),(4,B)]
pop (1,D)  -> settle D.  relax: B = min(4, 1+2=3)   heap:[(3,B),(4,B)]
pop (3,B)  -> settle B.  relax: C = 3+1 = 4         heap:[(4,B)stale,(4,C)]
pop (4,B)  -> STALE (B already settled at 3) skip
pop (4,C)  -> settle C.                              heap empty

Final dist:  A=0  D=1  B=3  C=4
             ^greedy: always expand the closest unsettled node first
```
- **🧠 Idea + template:** Greedily pull the closest unsettled node from a min-heap, settle it (its distance is final), and relax its outgoing edges. Skip stale heap entries.
```java
int[] dijkstra(int n, List<int[]>[] adj, int src) {  // adj[u] = list of {v, w}
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> a[1] - b[1]); // {node, d}
    pq.add(new int[]{src, 0});
    while (!pq.isEmpty()) {
        int[] top = pq.poll(); int u = top[0], d = top[1];
        if (d > dist[u]) continue;                 // stale entry, skip
        for (int[] e : adj[u]) {
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
- **📝 Example:** *Network Delay Time (LC 743).* Run Dijkstra from source `k` over the weighted directed graph; the answer is the maximum of all finite `dist[]` (the last node to receive the signal), or `-1` if any node is unreachable.
- **⏱️ Complexity:** Time `O(E log V)` with a binary heap; Space `O(V + E)`.
- **💡 Remember-it tip:** "Closest-first, settle-once; a popped node is *done forever*. Negatives break it." Drill: *Cheapest Flights Within K Stops (LC 787)*, *Path With Minimum Effort (LC 1631)*.

### Shortest Path (BFS / Bellman-Ford)

[🔝 Back to index](#index)
- **🔍 Recognize:** **BFS** when edges are *unweighted* (or all weight 1) → fewest-steps / minimum-moves / shortest path in a grid/maze. **Bellman-Ford** when edges can be *negative*, or you need "at most K edges/stops", or to *detect negative cycles*.
- **📊 Visual (MOST IMPORTANT):**
```
(A) BFS on UNWEIGHTED graph = shortest path by LAYERS (each layer = +1 step):

      S - a - c
      |   |
      b - d - t

level 0: [S]              dist S=0
level 1: [a, b]           dist a=1, b=1     (S's neighbors)
level 2: [c, d]           dist c=2, d=2
level 3: [t]              dist t=3   <- first time we reach t = shortest

frontier expands ring by ring; first arrival is optimal (no weights to beat).

----------------------------------------------------------------------

(B) BELLMAN-FORD = relax ALL edges (V-1) times (here V=3, so 2 passes):

edges: A->B (4), A->C (5), B->C (-3)     source A
init:  dist A=0  B=INF  C=INF

pass 1: relax A->B: B=4 | A->C: C=5 | B->C: 4+(-3)=1 -> C=1
        dist A=0  B=4  C=1
pass 2: no improvement  -> stable
        (a V-th pass that still improves => NEGATIVE CYCLE)
```
- **🧠 Idea + template:** BFS: a queue expands the graph layer by layer, so the first time you dequeue the target you have the fewest edges. Bellman-Ford: relax every edge `V-1` times; one extra improving pass means a negative cycle.
```java
// BFS shortest path (unweighted), returns steps from src to each node
int[] bfsDist(int n, List<Integer>[] adj, int src) {
    int[] dist = new int[n]; Arrays.fill(dist, -1);
    Deque<Integer> q = new ArrayDeque<>(); q.add(src); dist[src] = 0;
    while (!q.isEmpty()) {
        int u = q.poll();
        for (int v : adj[u]) if (dist[v] == -1) {   // unvisited
            dist[v] = dist[u] + 1;
            q.add(v);
        }
    }
    return dist;
}

// Bellman-Ford: handles negative edges; null => negative cycle
int[] bellmanFord(int n, int[][] edges, int src) { // edges: {u,v,w}
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE); dist[src] = 0;
    for (int i = 1; i < n; i++)                      // V-1 passes
        for (int[] e : edges)
            if (dist[e[0]] != Integer.MAX_VALUE && dist[e[0]] + e[2] < dist[e[1]])
                dist[e[1]] = dist[e[0]] + e[2];
    for (int[] e : edges)                            // V-th pass: detect
        if (dist[e[0]] != Integer.MAX_VALUE && dist[e[0]] + e[2] < dist[e[1]])
            return null;                             // negative cycle
    return dist;
}
```
- **📝 Example:** *Rotting Oranges (LC 994)* uses multi-source BFS (minutes = layers). *Cheapest Flights Within K Stops (LC 787)* is Bellman-Ford with exactly `K+1` relaxation passes over a frozen copy of `dist`.
- **⏱️ Complexity:** BFS `O(V + E)` time, `O(V)` space. Bellman-Ford `O(V · E)` time, `O(V)` space.
- **💡 Remember-it tip:** "Unweighted? BFS rings. Negatives or 'K stops'? Bellman-Ford passes." Drill: *Word Ladder (LC 127, BFS)*, *Cheapest Flights Within K Stops (LC 787, Bellman-Ford)*.

### Deep Copy / Cloning

[🔝 Back to index](#index)
- **🔍 Recognize:** "clone a graph", "deep copy a linked list with random pointers", "copy a connected structure" where naively following pointers would loop forever or duplicate nodes. Signal: must preserve structure *without* sharing references to originals.
- **📊 Visual (MOST IMPORTANT):**
```
Original graph (undirected):     1 --- 2
                                 |     |
                                 4 --- 3

Goal: build clones, using a map  original -> clone, to avoid re-cloning / loops.

DFS(1): not in map -> create 1'. map={1:1'}
  neighbor 2: not in map -> create 2'. map={1:1',2:2'}; 1'.nbrs += 2'
    neighbor 1: IN map -> reuse 1' (no infinite loop!); 2'.nbrs += 1'
    neighbor 3: create 3'. map={...,3:3'}; 2'.nbrs += 3'
      neighbor 2: in map reuse 2'
      neighbor 4: create 4'. map={...,4:4'}; 3'.nbrs += 4'
        neighbor 1: in map reuse 1'; 4'.nbrs += 1'
        neighbor 3: in map reuse 3'
  neighbor 4: IN map -> reuse 4'; 1'.nbrs += 4'

map: 1->1'  2->2'  3->3'  4->4'   (every node cloned exactly once)

      1' --- 2'
      |      |       <- identical shape, ZERO shared refs with original
      4' --- 3'
```
- **🧠 Idea + template:** Keep a `original -> clone` hash map. Create each clone once on first visit; for every neighbor, recurse to get (or create) its clone and wire it up. The map both memoizes and breaks cycles.
```java
Map<Node, Node> map = new HashMap<>();
Node cloneGraph(Node node) {
    if (node == null) return null;
    if (map.containsKey(node)) return map.get(node);  // already cloned -> reuse
    Node copy = new Node(node.val);
    map.put(node, copy);                              // record BEFORE recursing
    for (Node nei : node.neighbors)
        copy.neighbors.add(cloneGraph(nei));          // deep-copy each neighbor
    return copy;
}
```
- **📝 Example:** *Clone Graph (LC 133).* DFS/BFS from the start node; maintain a visited map from original to copy, create each node once, and reconnect cloned neighbors. The map prevents infinite recursion on cycles.
- **⏱️ Complexity:** Time `O(V + E)` (each node and edge handled once); Space `O(V)` for the map plus recursion/queue.
- **💡 Remember-it tip:** "Map *before* you recurse — that's what stops the infinite loop." Drill: *Copy List with Random Pointer (LC 138)*, *Clone N-ary Tree*.

---

## Backtracking

> Mental model: every backtracking problem is a **DFS over a decision tree**. At each node you *make a choice → recurse → undo the choice* (the "undo" is the backtrack). Draw the tree, number the visit order, and the code writes itself.

```
TEMPLATE SHAPE (burn this in):

void dfs(state):
    if (goal reached) { record(); return; }
    for (choice : choices):
        make(choice)        // add to path
        dfs(next state)     // recurse deeper
        undo(choice)        // BACKTRACK — pop it back off
```

---

### Subsets (Include/Exclude)

[🔝 Back to index](#index)

- **🔍 Recognize:** "all subsets", "power set", "all possible combinations of any length", no ordering constraint, result count looks like 2^n.
- **📊 Visual (MOST IMPORTANT):**

```
nums = [1,2,3]   ── at each index: SKIP (exclude) or TAKE (include)

                          []                         <- start, idx0
                 take1 /        \ skip1
                    [1]            []                 idx1
            take2 /   \ skip2   /     \
              [1,2]   [1]     [2]      []             idx2
          t3/  \s3  t3/ \s3  t3/ \s3  t3/  \s3
      [1,2,3][1,2][1,3][1] [2,3][2] [3]   []         idx3 = leaves

  Visit order (DFS, take-first): [123]→[12]→[13]→[1]→[23]→[2]→[3]→[]
  Every leaf = one subset. 2^3 = 8 leaves.
```

- **🧠 Idea + template:** At each index make a binary choice — include this element or not — and recurse on the next index.

```java
void dfs(int[] nums, int start, List<Integer> path, List<List<Integer>> res) {
    res.add(new ArrayList<>(path));      // every node is a valid subset
    for (int i = start; i < nums.length; i++) {
        path.add(nums[i]);               // include
        dfs(nums, i + 1, path, res);     // recurse on rest
        path.remove(path.size() - 1);    // exclude (backtrack)
    }
}
```

- **📝 Example:** *LeetCode 78 — Subsets.* Iterate from `start`, add the current path snapshot at every call, recurse with `i+1`, then pop. For **Subsets II (90)** with dups: sort first, then `if (i > start && nums[i]==nums[i-1]) continue;` to skip duplicate siblings.
- **⏱️ Complexity:** Time `O(n · 2^n)` (2^n subsets, O(n) to copy each). Space `O(n)` recursion depth (excluding output).
- **💡 Remember-it tip:** "**Take it or leave it**" at each step. Drill: **Subsets II (90)**, **Letter Combinations of a Phone Number (17)**.

---

### Permutations

[🔝 Back to index](#index)

- **🔍 Recognize:** "all permutations", "all orderings/arrangements", order **matters**, result count looks like n!, every element used exactly once.
- **📊 Visual (MOST IMPORTANT):**

```
nums = [1,2,3]   ── pick any UNUSED element at each level (used[] marks taken)

                      [ ]
          /            |            \
        [1]           [2]           [3]
        /  \          /  \          /  \
     [1,2][1,3]   [2,1][2,3]   [3,1][3,2]
       |    |       |    |       |    |
   [1,2,3][1,3,2][2,1,3][2,3,1][3,1,2][3,2,1]   <- 3! = 6 leaves

  used = [F,F,F] → pick 1 → used=[T,F,F] → pick 2 → ... full → record, undo
  Branching SHRINKS each level: 3 choices, then 2, then 1.
```

- **🧠 Idea + template:** Track which elements are used; at each level try every unused element, recurse, then release it.

```java
void dfs(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> res) {
    if (path.size() == nums.length) { res.add(new ArrayList<>(path)); return; }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        used[i] = true; path.add(nums[i]);           // choose
        dfs(nums, used, path, res);                  // explore
        used[i] = false; path.remove(path.size()-1); // un-choose
    }
}
```

- **📝 Example:** *LeetCode 46 — Permutations.* Loop over all indices each call (start from 0, not `start`), skip used ones, recurse until `path` is full. For **Permutations II (47)** with dups: sort, then skip with `if (i>0 && nums[i]==nums[i-1] && !used[i-1]) continue;`.
- **⏱️ Complexity:** Time `O(n · n!)`. Space `O(n)` depth + `O(n)` for `used`.
- **💡 Remember-it tip:** Permutation = **order matters, loop from 0 every time** (vs Subsets which loops from `start`). Drill: **Permutations II (47)**, **Next Permutation (31)**.

---

### Combination Sum

[🔝 Back to index](#index)

- **🔍 Recognize:** "combinations that sum to target", "pick numbers that add up to T", candidates may be reused (or not), count not bounded by simple n!.
- **📊 Visual (MOST IMPORTANT):**

```
candidates=[2,3,6,7], target=7   ── reuse allowed → recurse on SAME i

                         (rem=7, start=0)
               +2 /        +3 |     +6 \      +7 \
          (5,i=0)      (4,i=1)   (1,i=2)    (0,i=3)✔ [7]
        +2/  +3 \       +3|        x prune (6>1)
   (3,i0) (2,i1)     (1,i1) x
   +2/ +3\  ...        prune
 (1,i0)x (0,i1)✔[2,2,3]
   prune

  ✔ when rem==0 (record).   x when rem<0 (prune, dead branch).
  Reuse same number → pass i (not i+1). Results: [2,2,3] and [7].
```

- **🧠 Idea + template:** Subtract each candidate from the remaining target; recurse from the **same** index to allow reuse; prune when remaining goes negative.

```java
void dfs(int[] cand, int start, int rem, List<Integer> path, List<List<Integer>> res) {
    if (rem == 0) { res.add(new ArrayList<>(path)); return; }
    for (int i = start; i < cand.length; i++) {
        if (cand[i] > rem) continue;            // prune (sort cand for early break)
        path.add(cand[i]);
        dfs(cand, i, rem - cand[i], path, res); // i (reuse) — use i+1 for no-reuse
        path.remove(path.size() - 1);
    }
}
```

- **📝 Example:** *LeetCode 39 — Combination Sum.* Sort candidates, recurse passing `i` (reuse), prune when `cand[i] > rem`. For **Combination Sum II (40)** (each used once, has dups): pass `i+1` and add the duplicate-skip `if (i>start && cand[i]==cand[i-1]) continue;`.
- **⏱️ Complexity:** Time `O(n^(T/min))` worst case (tree depth ~ T/min candidate). Space `O(T/min)` depth.
- **💡 Remember-it tip:** **"Same index = reuse, next index = use once."** Prune the moment you overshoot. Drill: **Combination Sum II (40)**, **Combinations (77)**.

---

### Parentheses Generation

[🔝 Back to index](#index)

- **🔍 Recognize:** "generate all valid parentheses", "well-formed brackets", `n` pairs, balance/validity constraint on the path.
- **📊 Visual (MOST IMPORTANT):**

```
n=2   ── rule: can add '(' if open<n ; can add ')' if close<open

                         ""  (o0,c0)
                          | add '('  (only ( allowed)
                        "("  (o1,c0)
                  add'(' /      \ add ')'
              "(("(o2,c0)      "()"(o1,c1)
                  |add ')'        |add '('
              "(()"(o2,c1)     "()("(o2,c1)
                  |add ')'        |add ')'
            "(())"✔(o2,c2)    "()()"✔(o2,c2)

  Track (open,close). Valid string when open==close==n.
  Branch pruned automatically: never close more than you've opened.
  Results: "(())" , "()()"
```

- **🧠 Idea + template:** Build the string char by char, keeping counts of open/close; only add `(` while `open<n` and `)` while `close<open`.

```java
void dfs(int open, int close, int n, StringBuilder sb, List<String> res) {
    if (sb.length() == 2 * n) { res.add(sb.toString()); return; }
    if (open < n)    { sb.append('('); dfs(open+1, close, n, sb, res); sb.deleteCharAt(sb.length()-1); }
    if (close < open){ sb.append(')'); dfs(open, close+1, n, sb, res); sb.deleteCharAt(sb.length()-1); }
}
```

- **📝 Example:** *LeetCode 22 — Generate Parentheses.* Two guarded branches: add `(` if `open<n`, add `)` if `close<open`; record when length hits `2n`. The constraints make every generated string valid by construction.
- **⏱️ Complexity:** Time `O(4^n / √n)` (the nth Catalan number of valid strings). Space `O(n)` depth.
- **💡 Remember-it tip:** **"Open if you have spares, close only if something's open."** Drill: **Letter Case Permutation (784)**, **Restore IP Addresses (93)**.

---

### Palindrome Partitioning

[🔝 Back to index](#index)

- **🔍 Recognize:** "partition string so every piece is a palindrome", "all ways to cut a string into valid substrings", cut-point decisions over a string.
- **📊 Visual (MOST IMPORTANT):**

```
s = "aab"   ── try every prefix cut; recurse on the rest if prefix is a palindrome

  start=0  cut after:
     "a" | "ab"        ✔ "a" is palindrome → recurse on "ab"
     "aa"| "b"         ✔ "aa" is palindrome → recurse on "b"
     "aab"             ✘ not palindrome → skip

  Tree:
                 "aab"
          /"a"            \"aa"
        "ab"               "b"
     /"a"   \"ab"✘        |"b"
    "b"      (skip)     [aa, b] ✔
    |"b"
  [a, a, b] ✔

  Results: ["a","a","b"]  and  ["aa","b"]
```

- **🧠 Idea + template:** For each starting index, try every prefix; if it's a palindrome, add it and recurse on the suffix.

```java
void dfs(String s, int start, List<String> path, List<List<String>> res) {
    if (start == s.length()) { res.add(new ArrayList<>(path)); return; }
    for (int end = start + 1; end <= s.length(); end++) {
        String piece = s.substring(start, end);
        if (!isPal(piece)) continue;          // only cut at palindrome prefixes
        path.add(piece);
        dfs(s, end, path, res);               // recurse on the rest
        path.remove(path.size() - 1);
    }
}
boolean isPal(String t){ int i=0,j=t.length()-1; while(i<j) if(t.charAt(i++)!=t.charAt(j--)) return false; return true; }
```

- **📝 Example:** *LeetCode 131 — Palindrome Partitioning.* Walk `end` from `start+1`; whenever `s[start..end)` is a palindrome, take that cut and recurse from `end`. Base case fires when `start` reaches the end.
- **⏱️ Complexity:** Time `O(n · 2^n)` (2^n possible cut sets, O(n) palindrome check / copy). Space `O(n)` depth. (Precompute a DP palindrome table to drop the per-cut check.)
- **💡 Remember-it tip:** **"Cut where it's a palindrome, recurse on the tail."** Same skeleton as Subsets, gated by `isPal`. Drill: **Palindrome Partitioning II (132)**, **Word Break II (140)**.

---

### Word Search / Path in Grid

[🔝 Back to index](#index)

- **🔍 Recognize:** "does the word exist in the grid", "path through a matrix", move in 4 (or 8) directions, can't reuse a visited cell, "connected sequence".
- **📊 Visual (MOST IMPORTANT):**

```
grid          word = "ABCCED"
A B C E
S F C S        Start where cell==word[0]. DFS in 4 dirs, mark visited (#).
A D E E

Step trace (match each char, '#' = currently on path):
  (0,0)A ✔match'A'  →  (0,1)B ✔'B'  →  (0,2)C ✔'C'  →  (1,2)C ✔'C'
        down/up/left blocked or mismatch → go to (2,2)E ✔'E' → (2,1)D ✔'D' ✔FOUND

  # B C E       on a dead branch you UNMARK (#→original) and back out:
  S F # S         if neighbor out-of-bounds OR visited OR != word[k] → return false
  A D # E         restore cell before returning so other paths can use it
```

- **🧠 Idea + template:** From each matching start cell, DFS in 4 directions matching the next char; mark the cell visited during recursion, then restore it on the way back.

```java
boolean dfs(char[][] b, String w, int k, int r, int c) {
    if (k == w.length()) return true;
    if (r<0||c<0||r>=b.length||c>=b[0].length || b[r][c]!=w.charAt(k)) return false;
    char tmp = b[r][c]; b[r][c] = '#';                    // mark visited
    boolean found = dfs(b,w,k+1,r+1,c) || dfs(b,w,k+1,r-1,c)
                 || dfs(b,w,k+1,r,c+1) || dfs(b,w,k+1,r,c-1);
    b[r][c] = tmp;                                         // restore (backtrack)
    return found;
}
```

- **📝 Example:** *LeetCode 79 — Word Search.* Try every cell as a start; DFS matching `word[k]`, temporarily blanking the cell to `#` so the same path can't reuse it, restore on return. Return true the instant any branch reaches `k == len`.
- **⏱️ Complexity:** Time `O(m·n·4^L)` (L = word length, 4 dirs per step). Space `O(L)` recursion depth.
- **💡 Remember-it tip:** **"Mark, explore 4 ways, un-mark."** The `#` swap is the in-place visited set. Drill: **Word Search II (212)** (add a Trie), **Number of Islands (200)**.

---

### N-Queens

[🔝 Back to index](#index)

- **🔍 Recognize:** "place N queens so none attack each other", "place items on a board with conflict constraints", one item per row/column, diagonal constraints.
- **📊 Visual (MOST IMPORTANT):**

```
N=4   ── place ONE queen per row; for each row try every column that's safe

Conflict sets: cols[], diag1[r-c], diag2[r+c]   (a col / diagonal already used?)

row0: try col0 → place         row0: Q . . .
row1: col0✘(col) col1✘(diag) col2✔   . . Q .
row2: col0✘ col1? diag clash ... all ✘ → DEAD END, backtrack row1
row1: col3✔                    . . . Q   then row2: col1✔  . Q . .
row3: col0,col2✘ ... no safe col → backtrack again ...

One full solution found:
   . Q . .      Each '#'/clash prunes a whole subtree.
   . . . Q      Backtrack = remove queen, free its col + both diagonals,
   Q . . .      try the next column.
   . . Q .
```

- **🧠 Idea + template:** Place one queen per row; a column is safe if its `col`, `r-c` diagonal, and `r+c` diagonal are all unused. Recurse to the next row, then free the sets.

```java
void dfs(int r, int n, boolean[] col, boolean[] d1, boolean[] d2, int[] pos, List<List<String>> res) {
    if (r == n) { res.add(build(pos, n)); return; }
    for (int c = 0; c < n; c++) {
        int i = r - c + n, j = r + c;                  // diag indices (offset to stay >=0)
        if (col[c] || d1[i] || d2[j]) continue;        // attacked → skip
        col[c]=d1[i]=d2[j]=true; pos[r]=c;             // place
        dfs(r+1, n, col, d1, d2, pos, res);            // next row
        col[c]=d1[i]=d2[j]=false;                      // remove (backtrack)
    }
}
```

- **📝 Example:** *LeetCode 51 — N-Queens.* One queen per row; maintain `col`, `diag1 (r-c)`, `diag2 (r+c)` boolean sets for O(1) safety checks; recurse row by row, undo the three flags on backtrack. Build the board from `pos[]` at `r==n`.
- **⏱️ Complexity:** Time `O(n!)` (n choices row 0, ~n−1 next, …, with diagonal pruning). Space `O(n)` for the conflict sets + recursion.
- **💡 Remember-it tip:** **"One queen per row; guard col + two diagonals; flip flags on / off."** The `r-c` and `r+c` trick uniquely IDs each diagonal. Drill: **N-Queens II (52)**, **Sudoku Solver (37)**.

---

## Dynamic Programming

DP is "smart recursion": find the recurrence (how a bigger answer is built from smaller ones), then either memoize (top-down) or fill a table (bottom-up). The visual you want in every problem is the **table or the recursion tree filling in**. Below, every subpattern shows that mechanism on a tiny concrete example.

---

### 1D Fibonacci Style

[🔝 Back to index](#index)

- **🔍 Recognize:** "ways to reach step n", "you can take 1 or 2 (or k) steps", answer for `n` depends only on the previous one or two states. Linear chain of states, each built from a fixed number of predecessors.
- **📊 Visual (climbing stairs, n=5; ways[i] = ways[i-1] + ways[i-2]):**

```
step i :   0    1    2    3    4    5
           |    |    |    |    |    |
ways   :   1    1    2    3    5    8
                ^----+----^
                |    |    |
        ways[2]=ways[1]+ways[0] = 1+1 = 2
        ways[3]=ways[2]+ways[1] = 2+1 = 3
        ways[4]=ways[3]+ways[2] = 3+2 = 5
        ways[5]=ways[4]+ways[3] = 5+3 = 8   <- answer

Only ever look BACK 2 cells -> keep 2 variables, slide right:
   [a, b] -> [b, a+b] -> slide -> ...
    1  1      1  2        2  3 ...
```

- **🧠 Idea + template:** `dp[i] = dp[i-1] + dp[i-2]`; since you only need the last two states, collapse the array into two rolling variables (O(1) space).

```java
int climbStairs(int n) {
    int prev2 = 1, prev1 = 1;      // ways to reach step 0 and step 1
    for (int i = 2; i <= n; i++) {
        int cur = prev1 + prev2;   // recurrence
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
```

- **📝 Example:** *Climbing Stairs* (LC 70). Each step reachable from one or two below, so total ways = sum of those two. Roll two variables forward to `n`.
- **⏱️ Complexity:** Time O(n), Space O(1).
- **💡 Remember-it tip:** "Look back two, slide forward." Drill *Fibonacci Number* (LC 509) and *House Robber* (LC 198 — same shape, `dp[i]=max(dp[i-1], dp[i-2]+nums[i])`).

---

### 1D Kadane Algorithm

[🔝 Back to index](#index)

- **🔍 Recognize:** "maximum sum contiguous subarray", "best subarray/substring ending here", array with negatives, you must keep a *contiguous* run. The phrase "subarray" (not subsequence) + "maximum" is the tell.
- **📊 Visual (array = [-2, 1, -3, 4, -1, 2, 1, -5, 4]):**

```
At each i, decide: EXTEND previous run, or START fresh at nums[i]?
   cur = max(nums[i],  cur + nums[i])

 i:        0    1    2    3    4    5    6    7    8
 nums:    -2    1   -3    4   -1    2    1   -5    4
 cur :    -2    1   -2    4    3    5    6    1    5
           |    |    |    |              ^
       start  start start  \____ extends extends ____/   peak=6
 best:    -2    1    1    4    4    5    6    6    6   <- answer 6

cur drops below 0  => throw it away, restart (a negative prefix only hurts).
best subarray = [4, -1, 2, 1] = 6
```

- **🧠 Idea + template:** A negative running sum can only drag the future down, so reset it; `cur` tracks the best sum *ending at i*, `best` tracks the global max.

```java
int maxSubArray(int[] nums) {
    int cur = nums[0], best = nums[0];
    for (int i = 1; i < nums.length; i++) {
        cur  = Math.max(nums[i], cur + nums[i]); // start fresh vs extend
        best = Math.max(best, cur);
    }
    return best;
}
```

- **📝 Example:** *Maximum Subarray* (LC 53). Walk left to right; at each element keep the larger of "this element alone" vs "this element appended to the prior run"; track the max seen.
- **⏱️ Complexity:** Time O(n), Space O(1).
- **💡 Remember-it tip:** "Drop the run the moment it goes negative." Drill *Maximum Product Subarray* (LC 152 — track min and max because of sign flips) and *Best Time to Buy/Sell Stock* (LC 121).

---

### 1D Coin Change / Unbounded Knapsack

[🔝 Back to index](#index)

- **🔍 Recognize:** "minimum coins / fewest items", "infinite supply" / "use each item as many times as you want", "number of ways to make amount". Reuse-allowed item selection toward a target.
- **📊 Visual (coins = [1,2,5], target = 11; dp[a] = min coins for amount a):**

```
dp[0]=0. For each amount a, try every coin c<=a: dp[a]=min(dp[a], dp[a-c]+1)

 amount a: 0  1  2  3  4  5  6  7  8  9 10 11
 dp      : 0  1  1  2  2  1  2  2  3  3  2  3
                            ^                 ^
   dp[5] = dp[5-5]+1 = dp[0]+1 = 1  (one 5-coin)
   dp[11]: try c=1 -> dp[10]+1 = 3
           try c=2 -> dp[9]+1  = 4
           try c=5 -> dp[6]+1  = 3   -> min = 3   (5+5+1)

Unbounded => inner amount loop goes LOW->HIGH so a coin can be reused.
```

- **🧠 Idea + template:** Build up every amount from 0..target; reusing items means the amount loop runs forward so `dp[a-c]` may already include the same coin.

```java
int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);       // "infinity" sentinel
    dp[0] = 0;
    for (int c : coins)
        for (int a = c; a <= amount; a++)   // forward => unbounded reuse
            dp[a] = Math.min(dp[a], dp[a - c] + 1);
    return dp[amount] > amount ? -1 : dp[amount];
}
```

- **📝 Example:** *Coin Change* (LC 322). 1D dp over amounts, each cell = fewest coins; for "number of ways" use *Coin Change II* (LC 518) with `dp[a] += dp[a-c]`.
- **⏱️ Complexity:** Time O(amount × coins), Space O(amount).
- **💡 Remember-it tip:** "Unbounded = forward inner loop (reuse allowed)." Drill *Coin Change II* (LC 518) and *Combination Sum IV* (LC 377 — swap loop order when order matters).

---

### 1D 0/1 Knapsack

[🔝 Back to index](#index)

- **🔍 Recognize:** "each item used at most once", "pick a subset to maximize value within capacity W", "can a subset sum to target". Bounded selection — every item is a yes/no.
- **📊 Visual (weights=[1,3,4], values=[15,20,30], W=4; rows add items, cols are capacity):**

```
dp[i][w] = best value using first i items within capacity w
take item only if its weight <= w:  dp[i][w] = max(skip, take)
   skip = dp[i-1][w]           take = dp[i-1][w - wt] + val

        w=0   1    2    3    4
 i=0  [  0    0    0    0    0 ]   (no items)
 i=1  [  0   15   15   15   15 ]   item(1,15)
 i=2  [  0   15   15   20   35 ]   item(3,20):  dp[4]=max(15, dp[1]+20=35)
 i=3  [  0   15   15   30   35 ]   item(4,30):  dp[4]=max(35, dp[0]+30=30)=35
                                  answer = 35  (items 1+3: wt 1+3=4, val 15+20)

1D rolling: iterate capacity HIGH->LOW so each item used once:
   for w = W .. wt:  dp[w] = max(dp[w], dp[w-wt] + val)
```

- **🧠 Idea + template:** Each item is take-or-skip; compress the 2D table to 1D by iterating capacity **backwards** so an item can't be reused within the same pass.

```java
int knapsack(int[] wt, int[] val, int W) {
    int[] dp = new int[W + 1];
    for (int i = 0; i < wt.length; i++)
        for (int w = W; w >= wt[i]; w--)     // backward => each item once
            dp[w] = Math.max(dp[w], dp[w - wt[i]] + val[i]);
    return dp[W];
}
```

- **📝 Example:** *Partition Equal Subset Sum* (LC 416). Target = total/2; boolean knapsack `dp[s] |= dp[s-num]` over capacity `s` going high→low; answer is `dp[target]`.
- **⏱️ Complexity:** Time O(n × W), Space O(W).
- **💡 Remember-it tip:** "0/1 = backward inner loop (no reuse)." Contrast with unbounded's forward loop. Drill *Target Sum* (LC 494) and *Last Stone Weight II* (LC 1049).

---

### 1D Word Break

[🔝 Back to index](#index)

- **🔍 Recognize:** "can the string be segmented into dictionary words", "split into valid words", break a sequence at positions where each piece is valid. Boolean reachability over string indices.
- **📊 Visual (s = "leetcode", dict = {leet, code}; dp[i] = can split s[0..i)):**

```
dp[i] = true if some j<i has dp[j]==true AND s[j..i) is a word
                l  e  e  t  c  o  d  e
 index:      0  1  2  3  4  5  6  7  8
 dp   :      T  F  F  F  T  F  F  F  T
             ^              ^              ^
          dp[0]=T (empty)  dp[4]=T:       dp[8]=T:
                           s[0..4)="leet" s[4..8)="code"
                           and dp[0]=T     and dp[4]=T

Scan i=1..n; for each i look back for a cut point j where the left part
is already reachable (dp[j]) and the right chunk s[j..i) is in the dict.
Answer = dp[8] = TRUE.
```

- **🧠 Idea + template:** `dp[i]` is reachable if there's an earlier reachable cut `j` and the substring between them is a dictionary word.

```java
boolean wordBreak(String s, List<String> dict) {
    Set<String> words = new HashSet<>(dict);
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true;                              // empty prefix
    for (int i = 1; i <= s.length(); i++)
        for (int j = 0; j < i; j++)
            if (dp[j] && words.contains(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
    return dp[s.length()];
}
```

- **📝 Example:** *Word Break* (LC 139). 1D boolean dp over prefixes; mark `dp[i]` true when a reachable `dp[j]` plus a valid chunk reaches `i`.
- **⏱️ Complexity:** Time O(n² × L) (substring/lookup cost L), Space O(n).
- **💡 Remember-it tip:** "Reachable cut + valid chunk." Drill *Word Break II* (LC 140 — return all sentences via DFS + memo) and *Palindrome Partitioning* (LC 131).

---

### 2D Longest Common Subsequence

[🔝 Back to index](#index)

- **🔍 Recognize:** "longest common subsequence", "edit/diff two strings", "delete chars to make equal", subsequence (not substring — gaps allowed) shared by two sequences.
- **📊 Visual (text1="ABCBDAB", shortened to "ACE" vs "ABCDE"; match => diagonal+1, else max of up/left):**

```
         ""  A  B  C  D  E
     ""    0  0  0  0  0  0
     A     0  1  1  1  1  1      A==A: diag(0)+1=1
     C     0  1  1  2  2  2      C==C: diag(1)+1=2
     E     0  1  1  2  2  3      E==E: diag(2)+1=3  <- LCS length 3

Rule per cell dp[i][j]:
   if s1[i-1]==s2[j-1]:  dp[i][j] = dp[i-1][j-1] + 1     (take diagonal)
   else:                 dp[i][j] = max(dp[i-1][j], dp[i][j-1])  (up vs left)

   match  ↖+1        mismatch  ↑ or ← (drop one char, keep best)
```

- **🧠 Idea + template:** On a match, extend the diagonal; on a mismatch, inherit the best of dropping one character from either string.

```java
int lcs(String a, String b) {
    int m = a.length(), n = b.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            dp[i][j] = (a.charAt(i-1) == b.charAt(j-1))
                ? dp[i-1][j-1] + 1
                : Math.max(dp[i-1][j], dp[i][j-1]);
    return dp[m][n];
}
```

- **📝 Example:** *Longest Common Subsequence* (LC 1143). Fill an (m+1)×(n+1) grid; diagonal+1 on match, max of neighbors otherwise; answer in bottom-right.
- **⏱️ Complexity:** Time O(m×n), Space O(m×n) (compressible to O(min(m,n))).
- **💡 Remember-it tip:** "Match goes diagonal, mismatch takes the bigger neighbor." Drill *Longest Common Substring* (reset to 0 on mismatch) and *Shortest Common Supersequence* (LC 1092).

---

### 2D Unique Paths on Grid

[🔝 Back to index](#index)

- **🔍 Recognize:** "robot moving right/down on a grid", "count paths from top-left to bottom-right", "minimum path sum", grid traversal where each cell reached from above or left.
- **📊 Visual (3×3 grid; paths[i][j] = paths[i-1][j] + paths[i][j-1]):**

```
First row & col = 1 (only one straight way to reach them).
Every other cell = (cell above) + (cell to the left).

      c0   c1   c2
 r0 [  1    1    1 ]
 r1 [  1    2    3 ]       2 = 1(up) + 1(left)
 r2 [  1    3    6 ]       6 = 3(up) + 3(left)  <- answer: 6 paths

         ↓ from above
         + → from left   accumulates into each cell

With obstacles: set that cell to 0 (no path flows through it).
For MIN PATH SUM: dp[i][j] = grid[i][j] + min(up, left).
```

- **🧠 Idea + template:** Each cell's path count is the sum of the cell above and the cell to the left (boundaries seeded to 1).

```java
int uniquePaths(int m, int n) {
    int[][] dp = new int[m][n];
    for (int[] row : dp) Arrays.fill(row, 1);   // first row & col = 1
    for (int i = 1; i < m; i++)
        for (int j = 1; j < n; j++)
            dp[i][j] = dp[i-1][j] + dp[i][j-1];  // above + left
    return dp[m-1][n-1];
}
```

- **📝 Example:** *Unique Paths* (LC 62). DP grid where each interior cell sums its top and left neighbors; bottom-right holds the total count.
- **⏱️ Complexity:** Time O(m×n), Space O(m×n) (compressible to O(n) with one row).
- **💡 Remember-it tip:** "Above + Left fills the grid." Drill *Unique Paths II* (LC 63 — obstacles zero out cells) and *Minimum Path Sum* (LC 64 — swap `+` for `min`).

---

### 2D Edit Distance

[🔝 Back to index](#index)

- **🔍 Recognize:** "minimum operations to convert word1 into word2", "insert/delete/replace", "Levenshtein distance", spell-check / fuzzy-match style transforms between two strings.
- **📊 Visual (word1="horse" → "ros", trimmed to "ho" → "r"; dp[i][j] = min edits):**

```
dp[i][j] = min edits to turn first i chars of A into first j chars of B
 base: dp[i][0]=i (delete all), dp[0][j]=j (insert all)

         ""   r
     ""   0   1
     h    1   1
     o    2   2

 match  A[i-1]==B[j-1]:  dp[i][j] = dp[i-1][j-1]            (free, diagonal)
 else   dp[i][j] = 1 + min( dp[i-1][j-1]  replace ↖
                            dp[i-1][j]    delete  ↑
                            dp[i][j-1] )  insert  ←

 full "horse"->"ros" works out to 3:  replace h->r, delete o, delete e... 
 (= 3 operations total)
```

- **🧠 Idea + template:** On a matching char, copy the diagonal for free; otherwise it's 1 + the cheapest of replace (diagonal), delete (up), or insert (left).

```java
int editDistance(String a, String b) {
    int m = a.length(), n = b.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;   // delete all
    for (int j = 0; j <= n; j++) dp[0][j] = j;   // insert all
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            dp[i][j] = (a.charAt(i-1) == b.charAt(j-1))
                ? dp[i-1][j-1]
                : 1 + Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1]));
    return dp[m][n];
}
```

- **📝 Example:** *Edit Distance* (LC 72). Grid where matches copy the diagonal and mismatches add 1 to the min of the three neighbors; answer in bottom-right.
- **⏱️ Complexity:** Time O(m×n), Space O(m×n) (compressible to O(n)).
- **💡 Remember-it tip:** "Match = diagonal free; else 1 + min(↖↑←)." Drill *Delete Operation for Two Strings* (LC 583) and *One Edit Distance* (LC 161).

---

### Longest Increasing Subsequence (LIS)

[🔝 Back to index](#index)

- **🔍 Recognize:** "longest strictly increasing subsequence", "longest chain", subsequence with an ordering constraint (each next element bigger). Often disguised: envelopes, box stacking, patience sorting.
- **📊 Visual (nums = [10, 9, 2, 5, 3, 7]; two views):**

```
O(n^2) dp[i] = longest increasing subseq ENDING at i:
 nums:  10   9   2   5   3   7
 dp  :   1   1   1   2   2   3
                     |   |   |
        dp[3]: 2<5  -> dp[2]+1 = 2
        dp[4]: 2<3  -> dp[2]+1 = 2
        dp[5]: 5<7 or 3<7 -> max(dp[3],dp[4])+1 = 3   <- answer 3
        (subsequence 2,5,7 or 2,3,7)

O(n log n) "tails" array — keep smallest tail for each length; binary-search:
 add 10 -> [10]
 add  9 -> [9]            (9 replaces 10)
 add  2 -> [2]
 add  5 -> [2,5]
 add  3 -> [2,3]         (3 replaces 5)
 add  7 -> [2,3,7]       length 3 = LIS
```

- **🧠 Idea + template:** Maintain `tails[k]` = smallest possible tail of an increasing subsequence of length k+1; binary-search each number's slot. Length of `tails` is the LIS.

```java
int lengthOfLIS(int[] nums) {
    List<Integer> tails = new ArrayList<>();
    for (int x : nums) {
        int lo = 0, hi = tails.size();
        while (lo < hi) {                 // lower_bound: first tail >= x
            int mid = (lo + hi) >>> 1;
            if (tails.get(mid) < x) lo = mid + 1; else hi = mid;
        }
        if (lo == tails.size()) tails.add(x);
        else tails.set(lo, x);            // replace to keep tail small
    }
    return tails.size();
}
```

- **📝 Example:** *Longest Increasing Subsequence* (LC 300). Patience-sorting: for each number, binary-search the first tail ≥ it and overwrite (or append); list size is the answer.
- **⏱️ Complexity:** Time O(n log n) (or O(n²) for the dp version), Space O(n).
- **💡 Remember-it tip:** "Keep tails small with binary search." Drill *Russian Doll Envelopes* (LC 354 — sort then LIS) and *Number of LIS* (LC 673).

---

### Catalan Numbers

[🔝 Back to index](#index)

- **🔍 Recognize:** "number of unique BSTs", "valid parentheses combinations", "ways to triangulate a polygon", "distinct full binary trees". Counting structures that split into a left part and a right part around a chosen root/pivot.
- **📊 Visual (Cₙ = Σ Cᵢ·Cₙ₋₁₋ᵢ; "pick each element as root, multiply left × right subtrees"):**

```
C0=1, C1=1, then each Cn sums over the root choice:

 C2 = C0*C1 + C1*C0 = 1 + 1            = 2
 C3 = C0*C2 + C1*C1 + C2*C0 = 2+1+2    = 5
 C4 = C0*C3 + C1*C2 + C2*C1 + C3*C0
    = 1*5 + 1*2 + 2*1 + 5*1            = 14

Unique BSTs with keys {1,2,3} (C3 = 5): choose each key as root,
        left subtree uses smaller keys, right uses larger:

 root=1     root=2       root=3
   1          2            3
    \        / \          /
     2      1   3        2
      \                 /
       3               1     ... (5 distinct shapes total)

 #left nodes = i, #right nodes = n-1-i  ->  Ci * C(n-1-i)
```

- **🧠 Idea + template:** Pick each element as the "root/split point"; the count is the product of arrangements of the left group and the right group, summed over all splits.

```java
int numTrees(int n) {                 // n-th Catalan number
    int[] dp = new int[n + 1];
    dp[0] = 1;
    for (int nodes = 1; nodes <= n; nodes++)
        for (int root = 0; root < nodes; root++)   // left=root, right=nodes-1-root
            dp[nodes] += dp[root] * dp[nodes - 1 - root];
    return dp[n];
}
```

- **📝 Example:** *Unique Binary Search Trees* (LC 96). For n keys, sum over each key as root; multiply counts of the smaller-key left subtree and larger-key right subtree.
- **⏱️ Complexity:** Time O(n²), Space O(n).
- **💡 Remember-it tip:** "Split at the root: left × right, summed." Drill *Unique Binary Search Trees II* (LC 95 — build the trees) and *Generate Parentheses* (LC 22).

---

### Interval DP

[🔝 Back to index](#index)

- **🔍 Recognize:** "merge / burst / remove over a range and combine costs", "best way to combine the whole interval", answer for `[i..j]` depends on splitting at some `k` inside it. Process by **increasing interval length**.
- **📊 Visual (Matrix Chain / Burst Balloons shape; dp[i][j] = best over range, try every split k):**

```
dp[i][j] = best for subarray i..j = min/max over k of
           dp[i][k] (+) dp[k+1][j] (+) cost(i,k,j)

Fill by LENGTH (len=2,3,...,n) so smaller ranges are ready first:

      j=0  1   2   3
 i=0 [ 0   .   .   X ]   <- final answer dp[0][n-1] (top-right corner)
 i=1 [     0   .   . ]
 i=2 [         0   . ]   diagonal = length-1 ranges = base (cost 0)
 i=3 [             0 ]

Order of computation (length grows):
 len1: (0,0)(1,1)(2,2)(3,3)            base
 len2: (0,1)(1,2)(2,3)                 try 1 split each
 len3: (0,2)(1,3)                      try splits k = i..j-1
 len4: (0,3)  <- answer

For dp[0][3], try k=0,1,2:  combine dp[0][k] & dp[k+1][3] + merge-cost,
keep the best.
```

- **🧠 Idea + template:** For each interval, try every internal split point and combine the two sub-results plus a merge cost; iterate by interval length so sub-intervals are solved first.

```java
// Matrix Chain Multiplication: p[] has n+1 dims, dp[i][j]=min mults for i..j
int matrixChain(int[] p) {
    int n = p.length - 1;
    int[][] dp = new int[n][n];
    for (int len = 2; len <= n; len++)              // grow interval length
        for (int i = 0; i + len - 1 < n; i++) {
            int j = i + len - 1;
            dp[i][j] = Integer.MAX_VALUE;
            for (int k = i; k < j; k++)             // try every split
                dp[i][j] = Math.min(dp[i][j],
                    dp[i][k] + dp[k+1][j] + p[i]*p[k+1]*p[j+1]);
        }
    return dp[0][n-1];
}
```

- **📝 Example:** *Burst Balloons* (LC 312). `dp[i][j]` = max coins bursting the open interval; pick the **last** balloon `k` to burst, adding `nums[i-1]*nums[k]*nums[j+1]` plus the two sub-intervals.
- **⏱️ Complexity:** Time O(n³) (n² intervals × n splits), Space O(n²).
- **💡 Remember-it tip:** "Grow by length, split at k, combine + merge cost." Drill *Burst Balloons* (LC 312) and *Minimum Cost to Cut a Stick* (LC 1547).

---

## Greedy

Greedy works when a **locally optimal choice provably leads to a global optimum**. You sort or scan once, keep a running "best so far," and never look back. The trick is *recognizing* when greedy is safe — the visuals below show the mechanism so you can picture it instantly.

---

### Interval Merging/Scheduling

[🔝 Back to index](#index)
- **🔍 Recognize:** "intervals," "[start, end]," "merge overlapping," "minimum meeting rooms," "remove overlapping intervals," "maximum non-overlapping." Signal = a list of pairs where you reason about overlap. **Sort first** (by start to merge, by end to schedule the most non-overlapping).
- **📊 Visual (MOST IMPORTANT):**
```
Merge intervals: [[1,3],[2,6],[8,10],[15,18]]   (already sorted by start)

axis: 1   3   6   8  10        15      18
       [---]                              [1,3]
         [-----]                          [2,6]   2 <= 3  -> OVERLAP, extend end to 6
                  [---]                    [8,10]  8 > 6   -> GAP, push new
                            [------]       [15,18] 15 > 10 -> GAP, push new

merged grows:
  push [1,3]        -> [[1,3]]
  [2,6]: 2<=3       -> merge -> [[1,6]]
  [8,10]: 8>6       -> push  -> [[1,6],[8,10]]
  [15,18]: 15>10    -> push  -> [[1,6],[8,10],[15,18]]
RESULT = [[1,6],[8,10],[15,18]]
```
- **🧠 Idea + template:** Sort by start; if the next start <= last end, stretch the last end; otherwise start a new interval.
```java
int[][] merge(int[][] iv) {
    Arrays.sort(iv, (a, b) -> Integer.compare(a[0], b[0]));
    List<int[]> out = new ArrayList<>();
    int[] cur = iv[0];
    for (int i = 1; i < iv.length; i++) {
        if (iv[i][0] <= cur[1]) {                 // overlap
            cur[1] = Math.max(cur[1], iv[i][1]);  // extend
        } else {
            out.add(cur);                         // gap -> commit
            cur = iv[i];
        }
    }
    out.add(cur);
    return out.toArray(new int[0][]);
}
```
- **📝 Example:** *56. Merge Intervals* — sort by start, walk through, extend on overlap else append. For *435. Non-overlapping Intervals* sort by **end** and greedily keep the earliest-ending interval, counting removals.
- **⏱️ Complexity:** Time `O(n log n)` (sort dominates), Space `O(n)` for output (`O(1)` extra if in place).
- **💡 Remember-it tip:** "Sort by start to **glue**, sort by end to **pack**." Drill: *57. Insert Interval*, *252/253. Meeting Rooms I & II*.

---

### Buy/Sell Stock

[🔝 Back to index](#index)
- **🔍 Recognize:** "prices array," "maximize profit," "buy before you sell," "one transaction" (single pass min-tracking) vs. "as many transactions as you like" (sum the climbs). One day = one price index.
- **📊 Visual (MOST IMPORTANT):**
```
prices = [7, 1, 5, 3, 6, 4]      one transaction, track min-so-far

i:        0   1   2   3   4   5
price:    7   1   5   3   6   4
minSoFar: 7   1   1   1   1   1     (lowest price seen up to i)
profit:   0   0   4   2   5   5     price - minSoFar, keep the max
                  ^           ^
              buy@1,sell@5   buy@1,sell@6  <- best = 5

Each step:
  minSoFar = min(minSoFar, price)
  best     = max(best, price - minSoFar)
                 buy low ----^   sell high here
```
- **🧠 Idea + template:** Sweep left to right; remember the cheapest price seen, and the best profit = today's price minus that cheapest.
```java
int maxProfit(int[] p) {
    int minSoFar = Integer.MAX_VALUE, best = 0;
    for (int price : p) {
        minSoFar = Math.min(minSoFar, price);   // cheapest buy so far
        best     = Math.max(best, price - minSoFar);
    }
    return best;
}
// "Many transactions" (LC122): sum every upward step
int maxProfitII(int[] p) {
    int profit = 0;
    for (int i = 1; i < p.length; i++)
        if (p[i] > p[i - 1]) profit += p[i] - p[i - 1];
    return profit;
}
```
- **📝 Example:** *121. Best Time to Buy and Sell Stock* — one pass, track min price and max profit. *122. Best Time II* — grab every positive day-to-day delta (you bank each rising segment).
- **⏱️ Complexity:** Time `O(n)`, Space `O(1)`.
- **💡 Remember-it tip:** "Buy the dip you've **already** seen." One transaction = min tracker; unlimited = add every uphill. Drill: *122*, *123/188* (k transactions, escalates to DP).

---

### Jump Game

[🔝 Back to index](#index)
- **🔍 Recognize:** "array of jump lengths," "can you reach the last index," "minimum jumps to reach end." Each cell tells you the *max* you can advance. Greedy on **farthest reachable**.
- **📊 Visual (MOST IMPORTANT):**
```
nums = [2, 3, 1, 1, 4]      can we reach the last index?

idx:     0   1   2   3   4
val:     2   3   1   1   4
reach:   2   4   4   4   8     reach = max(reach, idx + val)

walk:
 i=0 reach=max(0,0+2)=2   (0<=2 ok)
 i=1 reach=max(2,1+3)=4   (1<=2 ok)
 i=2 reach=max(4,2+1)=4   (2<=4 ok)
 i=3 reach=max(4,3+1)=4   (3<=4 ok)
 i=4 4<=4 ok  -> reach 4 >= lastIndex(4)  => TRUE

If ever  i > reach  the chain breaks -> stuck -> FALSE
e.g. [3,2,1,0,4]: reach caps at 3, i=4 needs reach>=4 -> FALSE
```
- **🧠 Idea + template:** Track the farthest index reachable; if your current index ever exceeds it, you're stuck.
```java
boolean canJump(int[] nums) {
    int reach = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > reach) return false;            // gap you can't cross
        reach = Math.max(reach, i + nums[i]);   // extend the frontier
    }
    return true;
}
// LC45 min jumps: count window boundaries (BFS-by-levels)
int jump(int[] nums) {
    int jumps = 0, curEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        if (i == curEnd) { jumps++; curEnd = farthest; }  // forced to jump
    }
    return jumps;
}
```
- **📝 Example:** *55. Jump Game* — track farthest reach; false the moment index passes it. *45. Jump Game II* — count the implicit BFS layers: each time you hit the current window's end, take a jump and extend the window to `farthest`.
- **⏱️ Complexity:** Time `O(n)`, Space `O(1)`.
- **💡 Remember-it tip:** "Keep your **frontier** ahead of your feet." Min-jumps = count how many times the frontier resets. Drill: *45. Jump Game II*, *1306. Jump Game III*.

---

### Gas Station Circuit

[🔝 Back to index](#index)
- **🔍 Recognize:** "circular route," "gas[]" and "cost[]," "start at which station to complete the loop," "travel around once." Circular tank that must never go negative.
- **📊 Visual (MOST IMPORTANT):**
```
gas  = [1, 2, 3, 4, 5]
cost = [3, 4, 5, 1, 2]
diff = gas-cost = [-2, -2, -2, 3, 3]   total = 0 >= 0 => a solution EXISTS

scan, reset start whenever tank dips below 0:
 i=0 tank+=-2 -> -2  <0 -> can't start <=0; start=1, tank=0
 i=1 tank+=-2 -> -2  <0 -> start=2, tank=0
 i=2 tank+=-2 -> -2  <0 -> start=3, tank=0
 i=3 tank+= 3 ->  3  ok
 i=4 tank+= 3 ->  6  ok
 end: total>=0 so answer = start = 3

Why it works: if you fail going A..B, NO station in A..B can be a start
(each was a fresh non-negative attempt that still couldn't bridge the gap).
```
- **🧠 Idea + template:** If total gas >= total cost a unique answer exists; the start is the index right after the last point the running tank went negative.
```java
int canCompleteCircuit(int[] gas, int[] cost) {
    int total = 0, tank = 0, start = 0;
    for (int i = 0; i < gas.length; i++) {
        int diff = gas[i] - cost[i];
        total += diff;
        tank  += diff;
        if (tank < 0) {        // can't reach i+1 from current start
            start = i + 1;     // next candidate
            tank = 0;          // fresh tank
        }
    }
    return total >= 0 ? start : -1;
}
```
- **📝 Example:** *134. Gas Station* — one pass: accumulate `total` and a resettable `tank`; every time `tank` < 0, the next index becomes the new start. Feasible iff `total >= 0`.
- **⏱️ Complexity:** Time `O(n)`, Space `O(1)`.
- **💡 Remember-it tip:** "Fail forward: blow up here, **restart on the next pump**." Total >= 0 guarantees success. Drill: *134*, and the proof-cousin *1013. Partition Array Into Three Parts*.

---

### Task Scheduling (Frequency)

[🔝 Back to index](#index)
- **🔍 Recognize:** "tasks with cooldown `n`," "same task must be `n` apart," "rearrange / least intervals / idle time," "CPU/scheduler." Driven by the **most frequent** element creating mandatory gaps.
- **📊 Visual (MOST IMPORTANT):**
```
tasks = [A,A,A,B,B,B], n = 2     (same task needs 2 slots between)

Most frequent = A and B, count maxF = 3.  Build a skeleton from the max:

   A _ _ | A _ _ | A          <- (maxF-1) frames of size (n+1), then a final A
   frame1   frame2   tail
Fill the gaps with the next-frequent tasks (B):
   A B _ | A B _ | A B
   slot   idle    slots

length = (maxF-1)*(n+1) + (#tasks tied at maxF)
       = (3-1)*(2+1)    + 2        (A and B both hit 3)
       = 6 + 2 = 8
schedule: A B idle A B idle A B   -> 8 intervals (1 idle)

answer = max(tasks.length, formula)   // crowded inputs need no idling
```
- **🧠 Idea + template:** Lay down `(maxFreq-1)` blocks of width `(n+1)`, then add one slot per task that ties the max; floor it at the total task count.
```java
int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;
    int maxF = 0, countMax = 0;
    for (int f : freq) maxF = Math.max(maxF, f);
    for (int f : freq) if (f == maxF) countMax++;
    int frames = (maxF - 1) * (n + 1) + countMax;
    return Math.max(tasks.length, frames);   // can't be shorter than #tasks
}
```
- **📝 Example:** *621. Task Scheduler* — count frequencies, find the max and how many tasks tie it; slot answer = `max(len, (maxF-1)*(n+1)+countMax)`. The max-frequency task dictates the mandatory idle skeleton.
- **⏱️ Complexity:** Time `O(N)` (N = number of tasks; the 26-bucket scan is `O(1)`), Space `O(1)` (fixed 26 counters).
- **💡 Remember-it tip:** "The **busiest task builds the cage**; everyone else fills the bars." Picture `(maxF-1)` frames of `(n+1)`. Drill: *621. Task Scheduler*, *358. Rearrange String k Distance Apart*, *767. Reorganize String*.

---

## String Manipulation

### Anagram Check (Frequency Count/Sort)

[🔝 Back to index](#index)
- **🔍 Recognize:** "are these two strings anagrams", "rearrangement of letters", "same characters same counts", "group anagrams together". Order doesn't matter, only the multiset of characters.
- **📊 Visual (MOST IMPORTANT):**
```
s = "anagram"   t = "nagaram"

Frequency-count approach (single int[26]):
Walk s → ++ count    Walk t → -- count    All zero ⇒ anagram

index:  a  b  c ... g  m  n  r
        |          |  |  |  |
s "anagram":
  a:+1 n:+1 a:+2 g:+1 r:+1 a:+3 m:+1
  count → a=3 n=1 g=1 r=1 m=1

t "nagaram":
  n:-1 a:-1 g:-1 a:-2 r:-1 a:-3 m:-1
  count → a=0 n=0 g=0 r=0 m=0   ✅ all 0 → ANAGRAM

Sort approach (alternative):
  sort("anagram") = "aaagmnr"
  sort("nagaram") = "aaagmnr"   equal strings → ANAGRAM
```
- **🧠 Idea + template:** Same length + same character counts ⇒ anagram. One `int[26]`: increment for `s`, decrement for `t`, check all zeros.
```java
boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    int[] cnt = new int[26];
    for (int i = 0; i < s.length(); i++) {
        cnt[s.charAt(i) - 'a']++;
        cnt[t.charAt(i) - 'a']--;
    }
    for (int c : cnt) if (c != 0) return false;
    return true;
}
// Unicode/general: use HashMap<Character,Integer> instead of int[26].
```
- **📝 Example:** *Valid Anagram* (LC 242). Length-check early exit, single count array, sweep for non-zero. *Group Anagrams* (LC 49): key each word by its sorted form (or count signature) into a `HashMap<String, List<String>>`.
- **⏱️ Complexity:** Count: O(n) time, O(1) space (fixed 26). Sort: O(n log n) time.
- **💡 Remember-it tip:** "++ then -- → zeros." The count array is a tally that should perfectly cancel out. Drill: *Find All Anagrams in a String* (LC 438, sliding window), *Group Anagrams* (LC 49).

### Palindrome Check (Two Pointers / Reverse)

[🔝 Back to index](#index)
- **🔍 Recognize:** "reads the same forwards and backwards", "valid palindrome", "ignore non-alphanumeric / case", "palindrome after removing one char". Symmetry around a center.
- **📊 Visual (MOST IMPORTANT):**
```
s = "A man, a plan: a canal — Panama"
After filtering non-alphanumeric + lowercasing → "amanaplanacanalpanama"

Two pointers converge from both ends:

  a m a n a p l a n a c a n a l p a n a m a
  ^                                       ^
  L=0                                   R=20   a==a ✓ → L++, R--

  a m a n a p l a n a c a n a l p a n a m a
    ^                                   ^
    L=1                               R=19   m==m ✓ → L++, R--

            ... keep matching inward ...

  a m a n a p l a n a c a n a l p a n a m a
                    ^ ^
                  L=9 R=11   a==a ✓

                     ^
                   L=R=10  → pointers crossed → PALINDROME ✅

Mismatch case ("abca"): L='a' a^...^a R='a' ✓ then b≠c ✗ → NOT palindrome
```
- **🧠 Idea + template:** Two pointers from both ends move inward; every mirrored pair must match. Skip junk in place to keep O(1) space.
```java
boolean isPalindrome(String s) {
    int l = 0, r = s.length() - 1;
    while (l < r) {
        while (l < r && !Character.isLetterOrDigit(s.charAt(l))) l++;
        while (l < r && !Character.isLetterOrDigit(s.charAt(r))) r--;
        if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r)))
            return false;
        l++; r--;
    }
    return true;
}
```
- **📝 Example:** *Valid Palindrome* (LC 125). Filter to alphanumeric, lowercase, two pointers inward; mismatch ⇒ false. *Valid Palindrome II* (LC 680): on first mismatch, try skipping left OR right char and check the remaining substring.
- **⏱️ Complexity:** O(n) time, O(1) space (in-place pointers; reverse-and-compare costs O(n) space).
- **💡 Remember-it tip:** "Pincers closing inward." Each step compares a mirror pair. Drill: *Valid Palindrome II* (LC 680), *Palindrome Linked List* (LC 234), *Longest Palindromic Substring* (LC 5, expand-around-center).

### Roman to Integer

[🔝 Back to index](#index)
- **🔍 Recognize:** "convert Roman numeral to integer", symbols `I V X L C D M`, subtractive pairs like `IV`, `IX`, `XL`. Input is a valid Roman string.
- **📊 Visual (MOST IMPORTANT):**
```
s = "MCMXCIV"  (expected 1994)

Rule: scan left→right. If a symbol's value < the NEXT symbol's value,
      SUBTRACT it; otherwise ADD it.

 idx  sym  val   next  compare        action      total
  0    M  1000   C=100 1000>=100 →    +1000        1000
  1    C   100   M=1000 100<1000 →    -100          900
  2    M  1000   X=10  1000>=10  →    +1000        1900
  3    X    10   C=100 10<100   →     -10          1890
  4    C   100   I=1   100>=1   →     +100         1990
  5    I     1   V=5   1<5      →     -1           1989
  6    V     5   (end) →             +5           1994 ✅

Subtractive pairs to picture:  IV=4  IX=9  XL=40  XC=90  CD=400  CM=900
```
- **🧠 Idea + template:** A smaller value immediately before a larger one is subtracted (lookahead); otherwise added.
```java
int romanToInt(String s) {
    Map<Character,Integer> v = Map.of(
        'I',1,'V',5,'X',10,'L',50,'C',100,'D',500,'M',1000);
    int total = 0;
    for (int i = 0; i < s.length(); i++) {
        int cur = v.get(s.charAt(i));
        if (i + 1 < s.length() && cur < v.get(s.charAt(i + 1)))
            total -= cur;          // subtractive
        else
            total += cur;
    }
    return total;
}
```
- **📝 Example:** *Roman to Integer* (LC 13). Map each symbol; for each char compare with its right neighbor — subtract if smaller, else add.
- **⏱️ Complexity:** O(n) time (n ≤ 15), O(1) space.
- **💡 Remember-it tip:** "Smaller-before-bigger = minus." Look right: if I'm worth less than my neighbor, I'm a discount. Drill: *Integer to Roman* (LC 12, the inverse below).

### Integer and Roman Conversion

[🔝 Back to index](#index)
- **🔍 Recognize:** "convert integer to Roman numeral", "build a Roman string from a number", range 1–3999. The greedy inverse of Roman-to-Integer.
- **📊 Visual (MOST IMPORTANT):**
```
num = 1994  →  expected "MCMXCIV"

Greedy: keep a value table sorted DESC (including subtractive 900,400,90,40,9,4).
Repeatedly subtract the largest value that fits, append its symbol.

 values: [1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1]
 symbols:[ "M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"]

 num=1994 | 1000 fits → append "M"   | num=994     result="M"
 num= 994 |  900 fits → append "CM"  | num= 94     result="MCM"
 num=  94 |   90 fits → append "XC"  | num=  4     result="MCMXC"
 num=   4 |    4 fits → append "IV"  | num=  0     result="MCMXCIV" ✅

Bar shrinking:
 1994 ──M──> 994 ──CM──> 94 ──XC──> 4 ──IV──> 0
```
- **🧠 Idea + template:** Greedy with the subtractive forms baked into the table; always grab the biggest value that fits, loop until zero.
```java
String intToRoman(int num) {
    int[]    val = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
    String[] sym = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < val.length; i++) {
        while (num >= val[i]) {     // append as many as fit
            sb.append(sym[i]);
            num -= val[i];
        }
    }
    return sb.toString();
}
```
- **📝 Example:** *Integer to Roman* (LC 12). Parallel arrays sorted descending including the six subtractive forms; greedily subtract and append.
- **⏱️ Complexity:** O(1) time (table is fixed, ≤13 iterations × bounded appends), O(1) space.
- **💡 Remember-it tip:** "Biggest coin first." Like making change with fixed Roman 'coins' that include 900/400/90/40/9/4. Drill: *Roman to Integer* (LC 13, the inverse above).

### Repeated Substring Pattern Detection

[🔝 Back to index](#index)
- **🔍 Recognize:** "can the string be built by repeating a substring", "is it periodic", "made of copies of a smaller block". Looking for a repeating unit/period.
- **📊 Visual (MOST IMPORTANT):**
```
s = "abcabcabc"  → built from "abc" repeated 3× → TRUE

Trick: build (s + s), strip first & last char, search for s inside.

  s + s            = "abcabcabc abcabcabc"   (space added for readability)
  remove 1st char  =  "bcabcabc abcabcabc"
  remove last char =  "bcabcabc abcabcab"
  doubled, trimmed = "bcabcabcabcabcabcab"

  Does it contain s = "abcabcabc"?
       bcabcabcabcabcabcab
           ^^^^^^^^^
           abcabcabc   ← found at index 5 → TRUE ✅

Why it works: a periodic string reappears inside its own double when the
two original "seams" are removed. Non-periodic (e.g. "aba") does NOT.

  "aba"+"aba"="abaaba" → trim → "baab" → contains "aba"? NO → FALSE
```
- **🧠 Idea + template:** If `s` is `k` copies of a block, then `(s+s)` minus its first and last characters still contains `s`.
```java
boolean repeatedSubstringPattern(String s) {
    String doubled = (s + s).substring(1, 2 * s.length() - 1);
    return doubled.contains(s);
}
// Divisor alternative: try each block length L that divides n,
// check if s.substring(0,L) repeated n/L times equals s.
```
- **📝 Example:** *Repeated Substring Pattern* (LC 459). Concatenate `s+s`, drop the boundary chars, and test `contains(s)`. True ⇔ periodic.
- **⏱️ Complexity:** O(n) to O(n²) depending on `contains` (KMP-based search → O(n)); O(n) space for the doubled string.
- **💡 Remember-it tip:** "Double, trim the seams, find yourself." The pattern only survives the trim if it tiles perfectly. Drill: *Repeated String Match* (LC 686), *Implement strStr()* (LC 28).

### Naive / KMP / Rabin-Karp (overview)

[🔝 Back to index](#index)
- **🔍 Recognize:** "find a pattern in text", "indexOf / strStr", "first occurrence of needle in haystack", "count occurrences of a substring". Classic substring search.
- **📊 Visual (MOST IMPORTANT):**
```
text = "ABABCABAB"   pattern = "ABABC"

NAIVE: slide pattern, recompare from scratch on each mismatch.
  ABABCABAB
  ABABC          i=0: A B A B match, C vs C ✓ → FOUND at 0
  (worst case re-scans → O(n·m))

KMP: precompute LPS (longest proper prefix = suffix) to skip re-checks.
  pattern  A B A B C
  index    0 1 2 3 4
  LPS      0 0 1 2 0      ("AB" prefix reused after a partial match)

  On mismatch at pattern[j], jump j = LPS[j-1] instead of restarting → O(n+m).

RABIN-KARP: hash the window, compare hashes (rolling hash slides cheaply).
  hash("ABABC") computed once.
  Roll window across text: drop leading char, add trailing char in O(1):
    h_new = (h_old - val(out)*base^(m-1)) * base + val(in)
  Hash match → verify chars (guard against collisions). Avg O(n+m).
```
- **🧠 Idea + template:** Naive is brute force; KMP reuses a failure table to never re-scan; Rabin-Karp slides a rolling hash and verifies on hash hits.
```java
// KMP: build LPS then scan
int strStr(String t, String p) {
    int m = p.length(); if (m == 0) return 0;
    int[] lps = new int[m];
    for (int i = 1, len = 0; i < m; ) {           // build LPS
        if (p.charAt(i) == p.charAt(len)) lps[i++] = ++len;
        else if (len > 0) len = lps[len - 1];
        else lps[i++] = 0;
    }
    for (int i = 0, j = 0; i < t.length(); ) {    // scan text
        if (t.charAt(i) == p.charAt(j)) { i++; j++; if (j == m) return i - m; }
        else if (j > 0) j = lps[j - 1];
        else i++;
    }
    return -1;
}
```
- **📝 Example:** *Implement strStr()* (LC 28). Naive double loop is accepted, but KMP gives guaranteed O(n+m). Rabin-Karp shines for *multiple-pattern* search and *Repeated DNA Sequences* (LC 187).
- **⏱️ Complexity:** Naive O(n·m). KMP O(n+m) time, O(m) space. Rabin-Karp O(n+m) average, O(n·m) worst (hash collisions), O(1) extra space.
- **💡 Remember-it tip:** "Naive restarts, KMP remembers, Rabin-Karp hashes." LPS = "how much of my prefix I can reuse." Drill: *Implement strStr()* (LC 28), *Repeated DNA Sequences* (LC 187), *Shortest Palindrome* (LC 214, uses KMP LPS).

---

## Bit Manipulation

### XOR (Single/Missing)

[🔝 Back to index](#index)
- **🔍 Recognize:** "every element appears twice except one", "find the single number", "one missing number in 0..n", "find the duplicate using O(1) space", pairs that cancel, no extra memory allowed.
- **📊 Visual (MOST IMPORTANT):**
```
Key facts:  a ^ a = 0   and   a ^ 0 = a   (XOR cancels pairs, order-free)

Array = [4, 1, 2, 1, 2]   find the single number

  acc = 0
  ^ 4  ->  0000 ^ 0100 = 0100  (4)
  ^ 1  ->  0100 ^ 0001 = 0101  (5)
  ^ 2  ->  0101 ^ 0010 = 0111  (7)
  ^ 1  ->  0111 ^ 0001 = 0110  (6)   <- the 1s start cancelling
  ^ 2  ->  0110 ^ 0010 = 0100  (4)   <- the 2s cancelled too
                         =====
  result = 0100 = 4  (the lonely element survives)

Missing number in 0..n: XOR all indices AND all values, leftover = missing
  idx:  0 1 2 3      vals: [0,1,3]
  (0^1^2^3) ^ (0^1^3) = 2   <- 2 is missing
```
- **🧠 Idea + template:** XOR all elements; identical pairs annihilate to 0, leaving the unique value.
```java
int singleNumber(int[] nums) {
    int acc = 0;
    for (int n : nums) acc ^= n;
    return acc;
}
// Missing number in [0..n]:
int missingNumber(int[] nums) {
    int acc = nums.length;            // start with n (the absent index)
    for (int i = 0; i < nums.length; i++) acc ^= i ^ nums[i];
    return acc;
}
```
- **📝 Example:** *Single Number* (LC 136) — accumulate XOR over the array; pairs cancel, the answer is whatever remains in one pass.
- **⏱️ Complexity:** Time O(n), Space O(1).
- **💡 Remember-it tip:** "Twins cancel, the loner lives." XOR is its own inverse. Drill: *Single Number II* (LC 137, count bits mod 3), *Single Number III* (LC 260, split by a set bit).

### Bitwise AND (Hamming Weight)

[🔝 Back to index](#index)
- **🔍 Recognize:** "count the number of 1 bits", "Hamming weight", "count set bits", "number of bits to flip to convert a to b" (Hamming distance = popcount of a^b), population count.
- **📊 Visual (MOST IMPORTANT):**
```
Trick:  n & (n-1)  clears the LOWEST set bit each time.
Count how many clears until n becomes 0  ==  number of set bits.

n = 11 = 1011                 count
  n-1   = 1010
  n&(n-1)= 1010  (cleared bit0)   1
  ----
n = 1010
  n-1   = 1001
  n&(n-1)= 1000  (cleared bit1)   2
  ----
n = 1000
  n-1   = 0111
  n&(n-1)= 0000  (cleared bit3)   3
  ----
n = 0  -> stop          popcount(11) = 3

Why? subtracting 1 flips the lowest 1 to 0 and all trailing 0s to 1;
ANDing with n wipes exactly that lowest 1 bit.
```
- **🧠 Idea + template:** Repeatedly clear the lowest set bit (`n & (n-1)`); the loop runs once per set bit.
```java
int hammingWeight(int n) {
    int count = 0;
    while (n != 0) {
        n &= (n - 1);   // drop the lowest set bit
        count++;
    }
    return count;
}
// Hamming distance between a and b:
int hammingDistance(int a, int b) {
    return Integer.bitCount(a ^ b);   // popcount of the differing bits
}
```
- **📝 Example:** *Number of 1 Bits* (LC 191) — loop `n &= n-1` until zero, counting iterations; far fewer than checking all 32 bits when bits are sparse.
- **⏱️ Complexity:** Time O(set bits) ≤ O(32), Space O(1).
- **💡 Remember-it tip:** "`n & (n-1)` snips off the rightmost 1." Drill: *Hamming Distance* (LC 461), *Total Hamming Distance* (LC 477, count per-column bits).

### Power of Two/Four

[🔝 Back to index](#index)
- **🔍 Recognize:** "is n a power of two", "power of four", "single bit set", checking if a number is exactly 2^k or 4^k, validating sizes that must be powers of two.
- **📊 Visual (MOST IMPORTANT):**
```
A power of two has EXACTLY ONE set bit:
   1 = 0001   2 = 0010   4 = 0100   8 = 1000

Trick:  n > 0  &&  (n & (n-1)) == 0   -> exactly one bit set

n = 8 = 1000          n = 6 = 0110 (NOT a power)
  n-1   = 0111          n-1   = 0101
  n&(n-1)= 0000  ✓        n&(n-1)= 0100  ✗ (leftover bits)

Power of FOUR: power of two AND the single bit sits on an EVEN position.
  mask 0x55555555 = 0101 0101 ... marks even positions (bit0,2,4,...)
   4 = 0100 -> bit at pos2 (even)  & 0101 = 0100 != 0  ✓
   8 = 1000 -> bit at pos3 (odd)   & 0101 = 0000        ✗ (pow2 but not pow4)
```
- **🧠 Idea + template:** Power of two = one bit set (`n & (n-1) == 0`); power of four adds the constraint that the bit lands on an even index.
```java
boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
}
boolean isPowerOfFour(int n) {
    return n > 0
        && (n & (n - 1)) == 0          // exactly one bit set (power of two)
        && (n & 0x55555555) != 0;      // that bit is on an even position
}
```
- **📝 Example:** *Power of Two* (LC 231) — return `n > 0 && (n & (n-1)) == 0` in O(1); no loops or logarithms needed.
- **⏱️ Complexity:** Time O(1), Space O(1).
- **💡 Remember-it tip:** "One bit = power of two; even slot = power of four." The mask `0x55555555` is `0101…` (even positions). Drill: *Power of Four* (LC 342), *Power of Three* (LC 326, the non-bit oddball — use it to remember bases that AREN'T 2^k can't use this trick).

### Bitwise DP (Counting Bits Optimization)

[🔝 Back to index](#index)
- **🔍 Recognize:** "count bits for every number from 0 to n", "return an array of popcounts", building a result that depends on already-computed smaller cases, "do it in O(n) instead of O(n log n)".
- **📊 Visual (MOST IMPORTANT):**
```
Goal: countBits[i] = number of set bits in i, for i = 0..n

Recurrence:  dp[i] = dp[i >> 1] + (i & 1)
  (drop the lowest bit -> already solved; add back the bit you dropped)

 i  binary   i>>1  dp[i>>1]  (i&1)  dp[i]
 0   0000     -        -       -      0
 1   0001    0000      0       1      1
 2   0010    0001      1       0      1
 3   0011    0001      1       1      2
 4   0100    0010      1       0      1
 5   0101    0010      1       1      2
 6   0110    0011      2       0      2
 7   0111    0011      2       1      3

dp table fills left->right, each cell reusing a HALF-INDEX already computed:
  dp = [0, 1, 1, 2, 1, 2, 2, 3, ...]
       ^each value is "a smaller answer + one trailing bit"
```
- **🧠 Idea + template:** `dp[i] = dp[i >> 1] + (i & 1)` — the popcount of `i` is the popcount of `i/2` plus the bit you shifted out.
```java
int[] countBits(int n) {
    int[] dp = new int[n + 1];
    for (int i = 1; i <= n; i++) {
        dp[i] = dp[i >> 1] + (i & 1);
    }
    return dp;
}
// Alternative recurrence: dp[i] = dp[i & (i-1)] + 1  (last-set-bit removed)
```
- **📝 Example:** *Counting Bits* (LC 338) — fill `dp[i] = dp[i>>1] + (i&1)` for `i` from 1 to n; each answer reuses a previously computed half-index in O(1).
- **⏱️ Complexity:** Time O(n), Space O(n) for the output array.
- **💡 Remember-it tip:** "Halve the number, you already know its popcount — just add the bit you dropped." Drill: *Counting Bits* (LC 338), *Single Number II* (LC 137, per-bit counting), *Sum of Two Integers* (LC 371, add without `+` using XOR/AND-carry).

---

## Segment Tree and Fenwick Tree

### Fenwick Tree (BIT) - Prefix Queries / Inversions

[🔝 Back to index](#index)

- **🔍 Recognize:** "prefix sum **with updates**", "point update + range sum query", "count of smaller elements to the right", "**number of inversions**", "count elements in a range as you stream them". Signal: a plain prefix-sum array would be O(1) query but O(n) update — you need *both* update and query to be fast. Values/indices fit in a bounded range (or can be coordinate-compressed).

- **📊 Visual (MOST IMPORTANT):**
```
Fenwick tree over array of size 8 (1-indexed). Each node i covers a
range of length = lowbit(i) = i & (-i), ending AT index i.

 index:   1    2    3    4    5    6    7    8
 lowbit:  1    2    1    4    1    2    1    8
 covers: [1] [1-2][3] [1-4][5] [5-6][7] [1-8]

 Coverage drawn as bars (which leaves each tree[i] sums):
 tree[8] |===============================|   covers 1..8
 tree[4] |===============|                   covers 1..4
 tree[6]                 |=======|           covers 5..6
 tree[2] |=======|                           covers 1..2
 tree[1] |===|                               covers 1..1
 tree[3]         |===|                       covers 3..3
 tree[5]                 |===|               covers 5..5
 tree[7]                         |===|       covers 7..7

UPDATE(i): climb UP adding lowbit ->  i += i & (-i)
  update(3): 3 -> 4 -> 8 -> (>8 stop)      [hits 3,4,8]

QUERY(i)=prefix sum 1..i: walk DOWN stripping lowbit -> i -= i & (-i)
  query(7): 7 -> 6 -> 4 -> 0 stop
            tree[7] + tree[6] + tree[4]
            = [7]   + [5-6]   + [1-4]   = sum 1..7  ✓

INVERSION COUNT trace, arr = [3, 1, 2] (process RIGHT->LEFT):
  see 2: query(2-1)=query(1)=0  smaller-seen-right -> 0 ; add 2
  see 1: query(1-1)=query(0)=0                       -> 0 ; add 1
  see 3: query(3-1)=query(2)=2  (1 and 2 already in) -> 2 ; add 3
  total inversions = 0 + 0 + 2 = 2   (pairs (3,1),(3,2))
```

- **🧠 Idea + template:** `i & (-i)` isolates the lowest set bit = the span each node owns; update climbs adding it, query descends subtracting it — both O(log n).
```java
class BIT {
    int[] tree; int n;
    BIT(int n){ this.n=n; tree=new int[n+1]; }      // 1-indexed
    void update(int i, int delta){                   // point add
        for(; i<=n; i += i & (-i)) tree[i] += delta;
    }
    int query(int i){                                // prefix sum 1..i
        int s=0;
        for(; i>0; i -= i & (-i)) s += tree[i];
        return s;
    }
    int rangeQuery(int l, int r){ return query(r) - query(l-1); }
}
// Inversions / count-smaller-to-right: compress values to ranks [1..m],
// iterate right->left, ans += query(rank-1); then update(rank, 1).
```

- **📝 Example:** *Count of Smaller Numbers After Self* (LC 315). Coordinate-compress values to ranks. Iterate from the right; for each element add `query(rank - 1)` (how many already-inserted values are strictly smaller) to its answer, then `update(rank, 1)`.

- **⏱️ Complexity:** Build O(n) (or O(n log n) with compression sort), each update/query O(log n), total O(n log n). Space O(n) for the tree (+O(n) for compression map).

- **💡 Remember-it tip:** "**Update climbs, query descends**" — and the magic spell is `i & (-i)` (lowbit). Picture the coverage bars: powers of two stack widest. Drill: *Range Sum Query - Mutable* (LC 307), *Reverse Pairs* (LC 493).

### Segment Tree - Range Sum with Point Update

[🔝 Back to index](#index)

- **🔍 Recognize:** "**range sum/min/max** AND **point updates**", "query arbitrary `[l, r]` repeatedly while values change", any associative range aggregate (sum, min, max, gcd) where a BIT is awkward (min/max can't be done by a plain BIT). Signal: you need flexible range queries that a prefix array can't refresh cheaply.

- **📊 Visual (MOST IMPORTANT):**
```
Array a = [2, 1, 5, 3]  (n=4). Segment tree as array, node 1 = root.
Each node stores the SUM of its segment [lo..hi].

                     node1 [0..3]=11
                   /                  \
          node2 [0..1]=3        node3 [2..3]=8
            /      \              /        \
   node4[0..0]=2 node5[1..1]=1 node6[2..2]=5 node7[3..3]=3
        a[0]        a[1]          a[2]         a[3]

Children of node i: left=2*i, right=2*i+1.  Leaves hold a[lo].

QUERY range-sum(1,3)  (want a[1]+a[2]+a[3] = 1+5+3 = 9):
  node1[0..3] partial -> recurse both
    node2[0..1] partial -> recurse
        node4[0..0] OUTSIDE [1..3]      -> 0
        node5[1..1] INSIDE              -> 1   ✓
    node3[2..3] FULLY inside [1..3]     -> 8   ✓ (no deeper recursion)
  total = 1 + 8 = 9  ✓

UPDATE point: set a[2] = 10  (delta +5). Climb the path, fix sums:
  node6[2..2]: 5 -> 10
  node3[2..3]: 8 -> 13      (1 leaf changed, only this path updates)
  node1[0..3]: 11 -> 16
  (node2 subtree untouched)   -> O(log n) nodes rewritten
```

- **🧠 Idea + template:** recursively split `[lo,hi]` at the midpoint; a query stops at a node fully inside the range (no deeper descent), so only O(log n) nodes are touched per op.
```java
class SegTree {
    int[] tree; int n;
    SegTree(int[] a){ n=a.length; tree=new int[4*n]; build(a,1,0,n-1); }
    void build(int[] a,int node,int lo,int hi){
        if(lo==hi){ tree[node]=a[lo]; return; }
        int mid=(lo+hi)>>>1;
        build(a,2*node,lo,mid); build(a,2*node+1,mid+1,hi);
        tree[node]=tree[2*node]+tree[2*node+1];
    }
    void update(int idx,int val){ update(1,0,n-1,idx,val); }
    void update(int node,int lo,int hi,int idx,int val){
        if(lo==hi){ tree[node]=val; return; }
        int mid=(lo+hi)>>>1;
        if(idx<=mid) update(2*node,lo,mid,idx,val);
        else         update(2*node+1,mid+1,hi,idx,val);
        tree[node]=tree[2*node]+tree[2*node+1];
    }
    int query(int l,int r){ return query(1,0,n-1,l,r); }
    int query(int node,int lo,int hi,int l,int r){
        if(r<lo || hi<l) return 0;              // outside
        if(l<=lo && hi<=r) return tree[node];   // fully inside
        int mid=(lo+hi)>>>1;                    // partial -> split
        return query(2*node,lo,mid,l,r) + query(2*node+1,mid+1,hi,l,r);
    }
}
```

- **📝 Example:** *Range Sum Query - Mutable* (LC 307). Build the tree once; `update(i, val)` rewrites the leaf and the O(log n) ancestors on its path; `sumRange(l, r)` returns `query(l, r)`. Both operations are O(log n).

- **⏱️ Complexity:** Build O(n), update O(log n), query O(log n). Space O(4n) ≈ O(n) for the `tree` array (4n is the safe size to fit all nodes).

- **💡 Remember-it tip:** Three cases at every node — **outside → 0, fully inside → return stored, partial → split & recurse**. Children at `2i` / `2i+1`, size the array `4*n`. For range *updates* too, add **lazy propagation**. Drill: *Range Sum Query 2D - Mutable* (LC 308), *My Calendar III* (LC 732, range-add via lazy seg tree).