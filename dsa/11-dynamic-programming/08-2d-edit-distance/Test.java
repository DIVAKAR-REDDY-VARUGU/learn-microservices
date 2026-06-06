import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        check("horse", "ros", 3);          // example
        check("intention", "execution", 5); // example
        check("", "abc", 3);               // example: 3 inserts
        check("abc", "", 3);               // 3 deletes
        check("", "", 0);                  // both empty
        check("a", "a", 0);                // identical single
        check("a", "b", 1);                // single replace
        check("abc", "abc", 0);            // identical
        check("abc", "abd", 1);            // one replace
        check("abc", "abcd", 1);           // one insert
        check("abcd", "abc", 1);           // one delete
        check("aaaa", "aa", 2);            // two deletes (duplicates)
        check("sunday", "saturday", 3);    // classic
        check("kitten", "sitting", 3);     // classic Levenshtein
        check("abcdef", "azced", 3);       // mixed ops
        check("plasma", "altruism", 6);    // longer divergent

        // ===== New corner cases (existing batch, kept) =====
        check("aaaa", "bbbb", 4);          // all replaces, same length
        check("abcde", "abcde", 0);        // identical 5
        check("abc", "xyz", 3);            // fully disjoint same length
        check("a", "abcde", 4);            // 4 inserts
        check("abcde", "a", 4);            // 4 deletes
        check("distance", "instance", 2);  // 2 replaces (di->in)
        check("flaw", "lawn", 2);          // shift
        check("gumbo", "gambol", 2);       // insert + replace
        check("book", "back", 2);          // 2 replaces
        check("abcdefgh", "abcdefgh", 0);  // identical 8
        check("aaa", "aaaaa", 2);          // 2 inserts (duplicates)
        check("abcdef", "ghijkl", 6);      // total mismatch -> length
        check("pneumonia", "anemia", 4);   // medical word pair
        check("xyzzy", "zzyzx", 4);        // anagram-ish, heavy edits
        check("ab", "ba", 2);              // swap = 2 edits (no transposition op)
        check("x", "", 1);                 // single delete
        check("", "y", 1);                 // single insert

        // ===== NEW corner cases (added) =====
        // Off-by-one shapes around equal prefixes/suffixes.
        check("ab", "abc", 1);             // append one char
        check("abc", "ab", 1);             // remove last char
        check("bcd", "abcd", 1);           // prepend one char (one insert at front)
        check("abcd", "bcd", 1);           // remove first char (one delete at front)
        check("abc", "axc", 1);            // single replace in the middle
        // Heavy duplicates: only first chars differ in count.
        check("aaaaa", "aaa", 2);          // delete 2 dups
        check("aaa", "baaa", 1);           // single insert before run of dups
        check("aaab", "aaac", 1);          // tail replace amid dup prefix
        // Alternating patterns.
        check("ababab", "bababa", 2);      // shift of alternating -> 2 edits
        check("ababab", "ababab", 0);      // identical alternating
        // All-equal vs all-different same length.
        check("zzzz", "zzzz", 0);          // all-equal identical
        check("zzzz", "yyyy", 4);          // all-equal -> all replace
        // Single char vs run (insert/delete heavy).
        check("a", "aaaaaa", 5);           // 5 inserts of same char
        check("aaaaaa", "a", 5);           // 5 deletes of same char
        // Disjoint alphabets, different lengths -> max(len) via replace+insert.
        check("abc", "wxyza", 4);          // len 3 vs 5, disjoint -> 3 replace + 2 insert
        // Empty vs non-empty (single insert) and reverse.
        check("hello", "olleh", 4);        // reversed word

        // ===== Performance / large constructed cases (exact known answers) =====
        // Constraint upper bound is word length <= 500; build inputs at/near 500.
        checkConstructed("a*500 vs a*500 (identical)", rep('a', 500), rep('a', 500), 0);
        checkConstructed("a*500 vs b*500 (all replace)", rep('a', 500), rep('b', 500), 500);
        checkConstructed("a*500 vs empty (all delete)", rep('a', 500), "", 500);
        checkConstructed("empty vs a*500 (all insert)", "", rep('a', 500), 500);
        checkConstructed("a*300 vs a*300+b*50 (append)", rep('a', 300), rep('a', 300) + rep('b', 50), 50);

        // NEW constructed cases with provable exact answers.
        // Identical 500-char alternating string -> distance 0.
        checkConstructed("(ab)*250 identical", repeat("ab", 250), repeat("ab", 250), 0);
        // 500 'a' vs 250 'a' -> delete 250.
        checkConstructed("a*500 vs a*250 (delete 250)", rep('a', 500), rep('a', 250), 250);
        // a*250 vs (a*250 with one middle char flipped) -> exactly 1 replace.
        checkConstructed("a*500 vs single-flip", rep('a', 500), flipMiddle(rep('a', 500)), 1);
        // Prepend a block of distinct char to a long run -> that block length of inserts.
        checkConstructed("a*450 vs b*50+a*450 (prepend 50)", rep('a', 450), rep('b', 50) + rep('a', 450), 50);
        // Two disjoint-alphabet strings of length 500 -> all replace = 500.
        checkConstructed("a*500 vs c*500 (disjoint replace)", rep('a', 500), rep('c', 500), 500);

        // ===== Random pairs verified by independent O(n*m) Levenshtein oracle =====
        // Property-style verification: compare against an independent oracle (and
        // sanity bounds) rather than a hardcoded value, since the pair is random.
        checkRandomOracle(0, 400, 400);
        checkRandomOracle(1, 500, 480);
        // NEW oracle cases: more seeds, varied lengths/alphabets at the 500 bound.
        checkRandomOracle(2, 500, 500);    // both at max length
        checkRandomOracle(3, 500, 1);      // max vs single char (lopsided)
        checkRandomOracle(4, 1, 500);      // single vs max (lopsided, reversed)
        checkRandomOracle(5, 480, 500);    // near-max both
        checkRandomOracle(6, 333, 444);    // odd lengths
        checkRandomOracle(7, 500, 250);    // 2:1 length ratio

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent O(n*m) Levenshtein oracle (rolling rows).
    static int oracle(String a, String b) {
        int n = a.length(), m = b.length();
        int[] prev = new int[m + 1];
        int[] cur = new int[m + 1];
        for (int j = 0; j <= m; j++) prev[j] = j;
        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) cur[j] = prev[j - 1];
                else cur[j] = 1 + Math.min(prev[j - 1], Math.min(prev[j], cur[j - 1]));
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[m];
    }

    static String rep(char c, int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) s.append(c);
        return s.toString();
    }

    static String repeat(String unit, int times) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < times; i++) s.append(unit);
        return s.toString();
    }

    // Flip the middle character of a string to a guaranteed-different char,
    // so the edit distance from the original is exactly 1 (single replace).
    static String flipMiddle(String s) {
        if (s.isEmpty()) return s;
        char[] c = s.toCharArray();
        int mid = c.length / 2;
        c[mid] = (c[mid] == 'z') ? 'a' : (char) (c[mid] + 1);
        return new String(c);
    }

    static void check(String a, String b, int expected) {
        total++;
        try {
            int actual = new Answer().minDistance(a, b);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m minDistance(\"" + a + "\", \"" + b + "\") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m minDistance(\"" + a + "\", \"" + b + "\") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m minDistance(\"" + a + "\", \"" + b + "\") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkConstructed(String label, String a, String b, int expected) {
        total++;
        try {
            long start = System.nanoTime();
            int actual = new Answer().minDistance(a, b);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m perf " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m perf " + label + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m perf " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m perf " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkRandomOracle(long seedOffset, int n, int m) {
        total++;
        String label = "perf random-oracle n=" + n + " m=" + m + " seed=" + (42 + seedOffset);
        try {
            Random rnd = new Random(42 + seedOffset);
            StringBuilder sa = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sa.append((char) ('a' + rnd.nextInt(5)));
            for (int i = 0; i < m; i++) sb.append((char) ('a' + rnd.nextInt(5)));
            String a = sa.toString();
            String b = sb.toString();
            int expected = oracle(a, b);
            long start = System.nanoTime();
            int actual = new Answer().minDistance(a, b);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            // Property checks: must equal oracle AND satisfy basic Levenshtein bounds.
            int lo = Math.abs(n - m);
            int hi = Math.max(n, m);
            boolean inBounds = actual >= lo && actual <= hi;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (!inBounds) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " out of bounds [" + lo + "," + hi + "] actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}