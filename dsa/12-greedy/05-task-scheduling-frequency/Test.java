import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        // Example 1
        check(new char[]{'A','A','A','B','B','B'}, 2, 8);
        // Example 2: no idle needed
        check(new char[]{'A','C','A','B','D','B'}, 1, 6);
        // Example 3: larger cooldown
        check(new char[]{'A','A','A','B','B','B'}, 3, 10);

        // Single task
        check(new char[]{'A'}, 0, 1);
        // Single task with cooldown (no repeats -> length wins)
        check(new char[]{'A'}, 5, 1);
        // n = 0 means no cooldown -> answer is just length
        check(new char[]{'A','A','A','A'}, 0, 4);
        // n = 0 with distinct tasks
        check(new char[]{'A','B','C'}, 0, 3);
        // All same task, cooldown forces idles
        check(new char[]{'A','A','A'}, 2, 7);
        // All same task, n=1
        check(new char[]{'A','A','A','A'}, 1, 7);
        // Two distinct, no cooldown pressure
        check(new char[]{'A','B'}, 2, 2);
        // All distinct -> length, no idles regardless of n
        check(new char[]{'A','B','C','D'}, 10, 4);
        // Frequencies dominated by length (many fillers)
        check(new char[]{'A','A','A','B','C','D','E','F','G'}, 2, 9);
        // Two tasks tied for max frequency
        check(new char[]{'A','A','B','B'}, 2, 4);
        // Tied max freq with cooldown forcing idle
        check(new char[]{'A','A','A','B','B','B','C','C'}, 2, 8);
        // Many tasks, max freq drives formula
        check(new char[]{'A','A','A','A','B','B','B','C','C','C','D','D','E','E'}, 2, 16);
        // Large n, single dominant task
        check(new char[]{'A','A','B'}, 100, 102);
        // Full alphabet spread (length dominates)
        check(new char[]{'A','B','C','A','B','C'}, 2, 6);
        // Idle-heavy: one task far more frequent
        check(new char[]{'A','A','A','A','B'}, 3, 13);

        // ===== NEW corner cases =====
        // Constraint min: single task, n = 0 (smallest possible input)
        check(new char[]{'Z'}, 0, 1);
        // n at max boundary (100), single repeated pair dominates schedule
        check(new char[]{'A','A'}, 100, 102);
        // n at max boundary with three of same -> (3-1)*101 + 1
        check(new char[]{'A','A','A'}, 100, 203);
        // Two tasks tied at max, large n: (f-1)*(n+1)+c = 1*101 + 2
        check(new char[]{'A','A','B','B'}, 100, 103);
        // All 26 distinct letters once, n large -> length dominates
        check(allLetters(1), 100, 26);
        // All 26 distinct letters, each twice, n=25 -> tied block fits exactly: (2-1)*26 + 26 = 52
        check(allLetters(2), 25, 52);
        // All 26 distinct, each twice, n=1 -> length dominates (52 vs (1)*2+26=28)
        check(allLetters(2), 1, 52);
        // Heavy duplicates of one task, n=0 -> just length
        check(repeat('A', 1000), 0, 1000);
        // Heavy duplicates of one task, n=2 -> (1000-1)*3 + 1
        check(repeat('A', 1000), 2, 2998);
        // Off-by-one: idle exactly cancelled by one extra distinct task
        // freq A=3 (n=2 -> frame (3-1)*3+1=7); adding B,B,C,C,D fills the 7 slots exactly
        check(new char[]{'A','A','A','B','B','C','C','D'}, 2, 8);
        // Off-by-one the other way: length one over the greedy frame -> length wins
        check(new char[]{'A','A','A','B','B','C','C','D','E'}, 2, 9);
        // Alternating pattern, n=1 -> no idle needed, length wins
        check(new char[]{'A','B','A','B','A','B'}, 1, 6);
        // Alternating pattern, n=2 -> A appears 3x, frame (3-1)*3+1=7 > length 6
        check(new char[]{'A','B','A','B','A','B'}, 2, 7);
        // Many tasks all tied at max freq (full block, no idle), n=3
        // 4 distinct each appearing 5 times, n=3: frame (5-1)*4 + 4 = 20 == length 20
        check(blocks(new char[]{'A','B','C','D'}, 5), 3, 20);
        // Strictly increasing frequencies A1 B2 C3 D4, n=2 -> D drives: (4-1)*3 + 1 = 10 == length 10
        check(new char[]{'A','B','B','C','C','C','D','D','D','D'}, 2, 10);
        // Single dominant with sparse fillers, n=100 -> huge idle gaps
        check(new char[]{'A','A','A','A','A','B','C'}, 100, 405);
        // Two equally-dominant tasks with n=0 -> length only
        check(new char[]{'A','A','A','B','B','B'}, 0, 6);

        // ===== LARGE / PERFORMANCE cases (property + O(n) oracle, timed) =====
        Random rng = new Random(42);

        // 10^4 random tasks over full alphabet, varied n
        largePropertyCheck(randomTasks(10000, 26, rng), 0, rng);
        largePropertyCheck(randomTasks(10000, 26, rng), 1, rng);
        largePropertyCheck(randomTasks(10000, 26, rng), 50, rng);
        largePropertyCheck(randomTasks(10000, 26, rng), 100, rng);

        // 10^4 random tasks over a SMALL alphabet (heavy duplication -> idle-heavy)
        largePropertyCheck(randomTasks(10000, 2, rng), 100, rng);
        largePropertyCheck(randomTasks(10000, 3, rng), 100, rng);

        // Constraint upper bound: tasks.length == 10^4, single letter, n=100 (max idle stress)
        largePropertyCheck(repeat('A', 10000), 100, rng);

        // tasks.length == 10^4, exactly two tied dominant letters, n=100
        largePropertyCheck(tied(10000, 2, rng), 100, rng);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // ---------- exact-match check ----------
    static void check(char[] tasks, int n, int expected) {
        total++;
        char[] inputCopy = tasks == null ? null : tasks.clone();
        try {
            int actual = new Answer().leastInterval(tasks, n);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m leastInterval(" + preview(inputCopy) + ", n=" + n + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m leastInterval(" + preview(inputCopy) + ", n=" + n
                        + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m leastInterval(" + preview(inputCopy) + ", n=" + n
                    + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---------- large property/oracle check (timed) ----------
    static void largePropertyCheck(char[] tasks, int n, Random rng) {
        total++;
        int len = tasks == null ? 0 : tasks.length;
        char[] inputCopy = tasks == null ? null : tasks.clone();
        try {
            long start = System.nanoTime();
            int actual = new Answer().leastInterval(tasks, n);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            int oracle = oracle(inputCopy, n);
            String label = "leastInterval(len=" + len + ", n=" + n + ") = " + actual
                    + " [oracle=" + oracle + ", " + elapsedMs + " ms]";

            // Property 1: must equal the independent O(n) oracle (formula is exact here).
            if (actual != oracle) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " : disagrees with oracle");
                return;
            }
            // Property 2: answer is at least the number of tasks (no task is dropped).
            if (actual < len) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " : answer < number of tasks");
                return;
            }
            // Property 3: answer respects the greedy lower bound from the most frequent task.
            int[] freq = freq(inputCopy);
            int f = 0, c = 0;
            for (int v : freq) { if (v > f) { f = v; c = 1; } else if (v == f && v > 0) c++; }
            int lower = Math.max(len, (f - 1) * (n + 1) + c);
            if (actual < lower) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " : below greedy lower bound " + lower);
                return;
            }
            // Property 4: generous time budget.
            if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " : exceeded 3000 ms budget");
                return;
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m " + label);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m leastInterval(len=" + len + ", n=" + n
                    + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---------- independent O(n) oracle ----------
    // answer = max(tasks.length, (f-1)*(n+1) + c)
    static int oracle(char[] tasks, int n) {
        if (tasks == null || tasks.length == 0) return 0;
        int[] freq = freq(tasks);
        int f = 0, c = 0;
        for (int v : freq) {
            if (v > f) { f = v; c = 1; }
            else if (v == f && v > 0) c++;
        }
        return Math.max(tasks.length, (f - 1) * (n + 1) + c);
    }

    static int[] freq(char[] tasks) {
        int[] freq = new int[26];
        if (tasks != null) for (char ch : tasks) freq[ch - 'A']++;
        return freq;
    }

    // ---------- input builders ----------
    static char[] repeat(char ch, int count) {
        char[] a = new char[count];
        Arrays.fill(a, ch);
        return a;
    }

    // each of the first `distinct` letters appears `times` times
    static char[] allLetters(int times) {
        return blocks(allLetters0(26), times);
    }

    static char[] allLetters0(int distinct) {
        char[] a = new char[distinct];
        for (int i = 0; i < distinct; i++) a[i] = (char) ('A' + i);
        return a;
    }

    // repeat each letter in `letters` exactly `times` times, concatenated
    static char[] blocks(char[] letters, int times) {
        char[] a = new char[letters.length * times];
        int idx = 0;
        for (char ch : letters)
            for (int t = 0; t < times; t++) a[idx++] = ch;
        return a;
    }

    static char[] randomTasks(int len, int distinct, Random rng) {
        char[] a = new char[len];
        for (int i = 0; i < len; i++) a[i] = (char) ('A' + rng.nextInt(distinct));
        return a;
    }

    // exactly `dominantCount` letters share the maximum frequency; rest are sparse fillers
    static char[] tied(int len, int dominantCount, Random rng) {
        char[] a = new char[len];
        int half = len / 2;
        for (int i = 0; i < half; i++) a[i] = (char) ('A' + (i % dominantCount));
        for (int i = half; i < len; i++) a[i] = (char) ('A' + dominantCount + rng.nextInt(26 - dominantCount));
        return a;
    }

    // ---------- display ----------
    static String preview(char[] tasks) {
        if (tasks == null) return "null";
        if (tasks.length <= 24) return Arrays.toString(tasks);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 12; i++) sb.append(tasks[i]).append(", ");
        sb.append("... (len=").append(tasks.length).append(")]");
        return sb.toString();
    }
}
