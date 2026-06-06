import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        check("abcde", "ace", 3);   // example: LCS "ace"
        check("abc", "abc", 3);      // example: identical
        check("abc", "def", 0);      // example: no common
        check("", "", 0);            // both empty
        check("", "abc", 0);         // one empty
        check("abc", "", 0);         // other empty
        check("a", "a", 1);          // single matching char
        check("a", "b", 0);          // single non-matching
        check("aaaa", "aa", 2);      // duplicates
        check("abcba", "abcbcba", 5); // LCS abcba
        check("bsbininm", "jmjkbkjkv", 1); // sparse common (b)
        check("ezupkr", "ubmrapg", 2); // LCS "ur"
        check("bl", "yby", 1);       // LCS "b"
        check("abcdgh", "aedfhr", 3); // LCS "adh"
        check("xyz", "xyz", 3);      // identical 2
        check("abab", "baba", 3);    // interleaved

        // ===== New corner cases (existing batch, kept) =====
        check("aaaa", "bbbb", 0);                 // same length, nothing in common
        check("abcabcabc", "abc", 3);             // repeated pattern vs single unit
        check("ababab", "bababa", 5);             // alternating, off-by-one phase
        check("abcdefghij", "aceg", 4);           // subsequence pick
        check("zxabcyz", "abc", 3);               // embedded contiguous block
        check("aaaaa", "aaa", 3);                 // heavy duplicates, shorter caps it
        check("abcde", "edcba", 1);               // reversed -> only 1
        check("pqrst", "pqrst", 5);               // identical longer
        check("xaxbxcx", "abc", 3);               // interleaved with noise
        check("a", "aaaaaaa", 1);                 // single vs many identical
        check("abcdef", "abcdef", 6);             // full identical
        check("abcdef", "fedcba", 1);             // full reverse
        check("aXbXcX", "abc", 3);                // capital noise (treated as chars)
        check("longest", "stone", 3);             // LCS "one" len 3
        check("zzzz", "z", 1);                    // many vs one
        check("abcdefghijklmnop", "ponmlkjihgfedcba", 1); // long reverse -> 1
        check("aabbccdd", "abcd", 4);             // grouped duplicates

        // ===== ADDED corner cases (distinct from above) =====
        // -- constraint min-boundary (length 1 on both sides) --
        check("z", "a", 0);                       // min boundary, distinct -> 0
        check("ab", "ba", 1);                     // smallest swap -> 1

        // -- all-equal --
        check("aaaaaaaaaa", "aaaaaaaaaa", 10);    // all-equal identical len 10

        // -- strictly increasing / strictly decreasing alphabets --
        check("abcdefghij", "abcdefghij", 10);    // strictly increasing identical
        check("abcdefghij", "jihgfedcba", 1);     // increasing vs decreasing -> 1
        check("abcdefg", "gfedcba", 1);           // full reverse len 7 -> 1

        // -- alternating patterns --
        check("abababab", "abababab", 8);         // alternating identical
        check("abababab", "babababa", 7);         // alternating, phase shift -> 7

        // -- all-distinct disjoint blocks (no-solution) --
        check("aaaaaaaa", "bbbbbbbb", 0);         // disjoint, no common subsequence

        // -- heavy duplicates / repeated patterns --
        check("mississippi", "mississippi", 11);  // dup-heavy identical
        check("mississippi", "misp", 4);          // subsequence of dup-heavy string
        check("aaaaabbbbb", "ababababab", 6);     // grouped blocks vs alternating
        check("abcabcabcabc", "abcabc", 6);       // repeated pattern, longer vs shorter
        check("aaa", "aaaaa", 3);                 // duplicate count capped by shorter

        // -- scramble / reorder shapes (off-by-one and permutations) --
        check("xyzxyzxyz", "zyx", 3);             // reversed unit picks one of each
        check("aebdcf", "abcdef", 4);             // scrambled vs sorted -> 4
        check("abc", "cba", 1);                   // reverse len 3 -> 1
        check("xxxyyyzzz", "xyz", 3);             // blocks vs single occurrences

        // -- word-like inputs --
        check("hello", "world", 1);               // common 'l' (or 'o') -> 1
        check("dynamic", "programming", 3);       // mixed words -> 3
        check("banana", "ananas", 5);             // heavy overlap -> 5
        check("qwerty", "qwerty", 6);             // identical 6 distinct chars

        // ===== Large / performance cases =====
        // Constructed so the property answer is known exactly.
        checkConstructed("a*1000 vs a*1000 (identical)", rep('a', 1000), rep('a', 1000), 1000);
        checkConstructed("a*1000 vs b*1000 (disjoint)", rep('a', 1000), rep('b', 1000), 0);
        checkConstructed("alt*1000 identical", alt(1000), alt(1000), 1000);
        // s vs a strict subsequence of s -> LCS == length of the subsequence.
        checkSubsequenceProperty(0, 1000, 400);
        checkSubsequenceProperty(1, 1000, 700);
        // Random pair, verified by independent O(n*m) DP oracle (same as optimal, used as property).
        checkRandomOracle(2, 800, 800);

        // ===== ADDED large / performance cases =====
        // Maximum parameter shape: both at constraint upper bound (1000 x 1000), verified by oracle.
        checkRandomOracle(3, 1000, 1000);         // max boundary, small alphabet -> nontrivial LCS
        checkRandomOracleAlpha(4, 1000, 1000, 26); // max boundary, full 26-letter alphabet
        // Off-by-one shapes around the upper bound.
        checkRandomOracle(5, 999, 1000);
        checkRandomOracle(6, 1000, 999);
        // Heavily skewed shapes (very long vs very short) at the boundary.
        checkConstructed("a*1000 vs a*1 (short side)", rep('a', 1000), rep('a', 1), 1);
        checkRandomOracle(7, 1000, 5);            // long vs tiny, oracle-verified
        // All-equal upper-bound stress: identical worst case for naive recursion.
        checkConstructed("a*1000 vs a*999 (all-equal off-by-one)", rep('a', 1000), rep('a', 999), 999);
        // Many distinct subsequence-property checks for property-based confidence.
        checkSubsequenceProperty(8, 1000, 1);     // subsequence length 1
        checkSubsequenceProperty(9, 1000, 999);   // subsequence length n-1
        checkSubsequenceProperty(10, 1000, 1000); // subsequence == whole string -> 1000
        // Two-character alphabet random pair at the boundary (dense matches).
        checkRandomOracleAlpha(11, 1000, 1000, 2);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent O(n*m) DP oracle.
    static int oracle(String a, String b) {
        int n = a.length(), m = b.length();
        int[] prev = new int[m + 1];
        int[] cur = new int[m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) cur[j] = prev[j - 1] + 1;
                else cur[j] = Math.max(prev[j], cur[j - 1]);
            }
            int[] tmp = prev; prev = cur; cur = tmp;
            Arrays.fill(cur, 0);
        }
        return prev[m];
    }

    static String rep(char c, int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) s.append(c);
        return s.toString();
    }

    static String alt(int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) s.append((i % 2 == 0) ? 'a' : 'b');
        return s.toString();
    }

    static void check(String a, String b, int expected) {
        total++;
        try {
            int actual = new Answer().longestCommonSubsequence(a, b);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m longestCommonSubsequence(\"" + a + "\", \"" + b + "\") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m longestCommonSubsequence(\"" + a + "\", \"" + b + "\") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m longestCommonSubsequence(\"" + a + "\", \"" + b + "\") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkConstructed(String label, String a, String b, int expected) {
        total++;
        try {
            long start = System.nanoTime();
            int actual = new Answer().longestCommonSubsequence(a, b);
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

    // Build a random string of length n, then a subsequence of length k -> LCS must be >= k,
    // and <= min(n,k)=k, so exactly k.
    static void checkSubsequenceProperty(long seedOffset, int n, int k) {
        total++;
        String label = "perf subsequence-property n=" + n + " k=" + k + " seed=" + (42 + seedOffset);
        try {
            Random rnd = new Random(42 + seedOffset);
            char[] a = new char[n];
            for (int i = 0; i < n; i++) a[i] = (char) ('a' + rnd.nextInt(26));
            // pick k increasing indices to form a guaranteed subsequence
            boolean[] mark = new boolean[n];
            int chosen = 0;
            // choose first k indices deterministically spread out
            for (int i = 0; i < n && chosen < k; i += Math.max(1, n / k)) {
                mark[i] = true;
                chosen++;
            }
            for (int i = 0; i < n && chosen < k; i++) {
                if (!mark[i]) { mark[i] = true; chosen++; }
            }
            StringBuilder sub = new StringBuilder();
            for (int i = 0; i < n; i++) if (mark[i]) sub.append(a[i]);
            String s = new String(a);
            String b = sub.toString();
            int expected = oracle(s, b); // exact, must equal b.length() since b is a subsequence of s
            if (expected != b.length()) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " oracle-self-check failed (" + expected + " != " + b.length() + ")");
                return;
            }
            long start = System.nanoTime();
            int actual = new Answer().longestCommonSubsequence(s, b);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
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

    static void checkRandomOracle(long seedOffset, int n, int m) {
        // default: small alphabet (4 letters) -> nontrivial LCS
        checkRandomOracleAlpha(seedOffset, n, m, 4);
    }

    // Random pair over an `alphabet`-letter lowercase alphabet, verified by the independent
    // O(n*m) DP oracle. Property: optimal LCS length must equal the oracle's exact value.
    static void checkRandomOracleAlpha(long seedOffset, int n, int m, int alphabet) {
        total++;
        String label = "perf random-oracle n=" + n + " m=" + m + " alpha=" + alphabet + " seed=" + (42 + seedOffset);
        try {
            Random rnd = new Random(42 + seedOffset);
            StringBuilder sa = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sa.append((char) ('a' + rnd.nextInt(alphabet)));
            for (int i = 0; i < m; i++) sb.append((char) ('a' + rnd.nextInt(alphabet)));
            String a = sa.toString();
            String b = sb.toString();
            int expected = oracle(a, b);
            long start = System.nanoTime();
            int actual = new Answer().longestCommonSubsequence(a, b);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
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
