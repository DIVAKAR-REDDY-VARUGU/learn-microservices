import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("anagram", "nagaram", true);
        check("rat", "car", false);
        check("a", "ab", false);

        // Corner cases (existing)
        check("a", "a", true);                 // single equal char
        check("a", "b", false);                // single different char
        check("ab", "ba", true);               // simple swap
        check("aacc", "ccac", false);          // same length, different freq
        check("", "", true);                   // both empty -> anagrams
        check("", "a", false);                 // one empty
        check("abc", "abcd", false);           // different length
        check("abcd", "abc", false);           // different length (longer first)
        check("aaaa", "aaaa", true);           // all equal, same
        check("aaaa", "aaab", false);          // all equal vs one diff
        check("listen", "silent", true);       // classic anagram
        check("hello", "olleh", true);         // reverse is anagram
        check("hello", "world", false);        // unrelated words
        check("aabbcc", "abcabc", true);       // duplicates reordered
        check("abcdefghijklmnopqrstuvwxyz", "zyxwvutsrqponmlkjihgfedcba", true); // full alphabet
        check("aabb", "abab", true);           // duplicates, same multiset
        check("aabb", "aabc", false);          // one char off

        // ---- NEW corner cases ----
        check("z", "z", true);                 // boundary single max-letter
        check("ab", "ab", true);               // identical strings are anagrams
        check("abc", "cba", true);             // strictly reversed small
        check("aaab", "abaa", true);           // heavy duplicate of one letter
        check("aaab", "aaba", true);           // same multiset, different order
        check("aaab", "aabb", false);          // off-by-one in frequency
        check("abcabc", "aabbcc", true);       // alternating vs grouped
        check("zzzz", "zzzz", true);           // all-equal max letter
        check("zzzz", "zzzy", false);          // all-equal but one differs
        check("abc", "", false);               // non-empty vs empty
        check("", "abc", false);               // empty vs non-empty
        check("mleetcode", "codelteem", false); // near anagram, freq mismatch
        check("qwerty", "qwerty", true);       // identical mid-length
        check("abcdz", "zabcd", true);         // rotation is an anagram
        check("aab", "abb", false);            // swapped frequency of two letters
        check("abab", "abba", true);           // alternating vs paired, same multiset
        check("xy", "yx", true);               // two-char swap
        check("abcde", "abcdf", false);        // single letter replaced

        // existing larger stress case (same multiset, shuffled order)
        StringBuilder sb = new StringBuilder();
        StringBuilder rev = new StringBuilder();
        for (int i = 0; i < 1000; i++) { char c = (char) ('a' + (i % 26)); sb.append(c); }
        rev.append(sb).reverse();
        check(sb.toString(), rev.toString(), true);

        // ---- LARGE / PERFORMANCE / SCALE cases (property-based + timed) ----
        // Build a near-upper-bound string, then verify a shuffled permutation is
        // reported as an anagram, and a single-character perturbation is not.
        Random rnd = new Random(42);
        int n = 50000; // constraint upper bound is 5 * 10^4
        char[] base = new char[n];
        for (int i = 0; i < n; i++) base[i] = (char) ('a' + rnd.nextInt(26));
        String sBig = new String(base);
        // Permutation: Fisher-Yates shuffle of the same characters.
        char[] permArr = base.clone();
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            char tmp = permArr[i]; permArr[i] = permArr[j]; permArr[j] = tmp;
        }
        String tPermBig = new String(permArr);
        timedCheck("large permutation (n=" + n + ")", sBig, tPermBig, true);

        // Perturb one character so it is NOT an anagram (change a char to one that
        // is guaranteed to break the frequency multiset).
        char[] perturbed = permArr.clone();
        char old = perturbed[0];
        perturbed[0] = (char) ('a' + ((old - 'a' + 1) % 26));
        // This changes one letter's count, so multisets differ unless n forces a
        // coincidence; guard by verifying with an independent oracle.
        boolean expectedPerturb = isAnagramOracle(sBig, new String(perturbed));
        timedCheck("large perturbed one char (n=" + n + ")", sBig, new String(perturbed), expectedPerturb);

        // All-same-letter large input vs itself (heavy duplicates at scale).
        char[] allZ = new char[n];
        Arrays.fill(allZ, 'z');
        String allZStr = new String(allZ);
        timedCheck("large all-equal (n=" + n + ")", allZStr, allZStr, true);

        // Large mismatched lengths -> immediate false for an O(n) solution.
        char[] shorter = new char[n - 1];
        Arrays.fill(shorter, 'z');
        timedCheck("large length-mismatch (n=" + n + ")", allZStr, new String(shorter), false);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent O(n) oracle using a frequency count of size 26.
    static boolean isAnagramOracle(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] freq = new int[26];
        for (int i = 0; i < s.length(); i++) freq[s.charAt(i) - 'a']++;
        for (int i = 0; i < t.length(); i++) {
            int idx = t.charAt(i) - 'a';
            if (--freq[idx] < 0) return false;
        }
        return true;
    }

    static void timedCheck(String label, String s, String t, boolean expected) {
        total++;
        try {
            long start = System.nanoTime();
            boolean actual = new Answer().isAnagram(s, t);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual == expected && elapsedMs <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            } else if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected " + expected + " but got " + actual + " (" + elapsedMs + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms (got " + actual + ")");
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static void check(String s, String t, boolean expected) {
        total++;
        try {
            boolean actual = new Answer().isAnagram(s, t);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m isAnagram(\"" + s + "\", \"" + t + "\") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m isAnagram(\"" + s + "\", \"" + t + "\") expected " + expected + " but got " + actual);
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m isAnagram(\"" + s + "\", \"" + t + "\") threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
