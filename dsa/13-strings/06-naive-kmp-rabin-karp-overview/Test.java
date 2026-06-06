import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("sadbutsad", "sad", 0);        // first of two occurrences
        check("leetcode", "leeto", -1);      // not found
        check("hello", "ll", 2);             // middle match

        // Corner cases (existing)
        check("a", "a", 0);                  // single char match
        check("a", "b", -1);                 // single char mismatch
        check("abc", "abc", 0);              // needle equals haystack
        check("abc", "abcd", -1);            // needle longer than haystack
        check("abc", "c", 2);                // match at last index
        check("abc", "a", 0);                // match at first index
        check("mississippi", "issip", 4);    // match deeper in
        check("mississippi", "issi", 1);     // earlier of multiple matches
        check("aaaaa", "aaa", 0);            // overlapping repeats, first index
        check("aaaaa", "bba", -1);           // not present
        check("abababab", "abab", 0);        // repeating pattern
        check("abababab", "baba", 1);        // offset repeating pattern
        check("abcabcabc", "cab", 2);        // first occurrence of "cab"
        check("hello", "hello world", -1);   // needle longer than haystack
        check("aaab", "aab", 1);             // near-miss before real match
        check("abcde", "f", -1);             // single char not found
        check("abcde", "e", 4);              // last char
        check("abcde", "abcde", 0);          // full match
        check("xxxxxxy", "xy", 5);           // match only at the tail
        check("abcabd", "abd", 3);           // backtrack then match (KMP-style)
        check("aabaaac", "aac", 4);          // partial then full match

        // ---- NEW corner cases ----
        check("a", "aa", -1);                // needle longer than 1-char haystack
        check("aa", "a", 0);                 // single char in repeated haystack
        check("ab", "b", 1);                 // last index of length-2
        check("abcabcabc", "abcabcabc", 0);  // needle equals whole haystack
        check("aaaaaa", "aaaaaa", 0);        // all-equal full match
        check("aaaaaa", "aaaaaaa", -1);      // needle one longer than haystack
        check("abcabcabcd", "abcd", 6);      // match only at the very end
        check("abababab", "ba", 1);          // alternating, first "ba"
        check("abababab", "aba", 0);         // overlapping alternating match
        check("mississippi", "sip", 6);      // deeper unique match
        check("mississippi", "pi", 9);       // near-tail match
        check("mississippi", "ppi", 8);      // tail triple match
        check("abcdeabcde", "eab", 4);       // wrap-around-looking match
        check("zzzzzzzzz", "zz", 0);         // heavy duplicates, earliest
        check("zzzzzzzzz", "zzy", -1);       // not found among duplicates
        check("hello", "o", 4);              // last char single
        check("hello", "h", 0);             // first char single
        check("banana", "ana", 1);          // overlapping "ana"
        check("aaab", "b", 3);              // single tail char after run
        check("abcabcabz", "abz", 6);       // KMP-style backtrack then late match

        // existing larger constructed case: needle deep inside a long haystack
        StringBuilder big = new StringBuilder();
        for (int i = 0; i < 500; i++) big.append('a');
        big.append("needle");
        for (int i = 0; i < 500; i++) big.append('b');
        check(big.toString(), "needle", 500);
        check(big.toString(), "missing", -1);

        // ---- LARGE / PERFORMANCE / SCALE cases (property-based + timed) ----
        // Constraint upper bound 10^4 for BOTH strings. The classic quadratic
        // killer for naive search: haystack of "aaaa..." with needle "aaaa...b".
        // A naive O(n*m) scan does ~10^8 char comparisons here; KMP/Rabin-Karp
        // stay linear. We verify against an independent oracle (Java indexOf).
        Random rnd = new Random(42);

        int hn = 10000;
        char[] aBlock = new char[hn];
        Arrays.fill(aBlock, 'a');
        String haystackA = new String(aBlock); // "a" * 10000

        // Needle: 9999 'a's + a 'b' -> never matches -> -1. Worst case for naive.
        char[] needleArr = new char[hn - 1];
        Arrays.fill(needleArr, 'a');
        needleArr[hn - 2] = 'b';
        String needleAB = new String(needleArr);
        timedCheck("large worst-case no-match (|h|=" + hn + ", |n|=" + needleAB.length() + ")",
                haystackA, needleAB, haystackA.indexOf(needleAB));

        // Needle of all 'a's that DOES match at index 0 (length 5000).
        char[] needleA = new char[5000];
        Arrays.fill(needleA, 'a');
        String allAneedle = new String(needleA);
        timedCheck("large all-equal match (|h|=" + hn + ", |n|=5000)",
                haystackA, allAneedle, haystackA.indexOf(allAneedle));

        // Random large haystack with a known needle planted near the END.
        int rhn = 10000;
        char[] hArr = new char[rhn];
        for (int i = 0; i < rhn; i++) hArr[i] = (char) ('a' + rnd.nextInt(3)); // small alphabet -> many partial matches
        // Plant a distinctive needle near the tail using out-of-alphabet letters.
        String planted = "qwerty";
        int plantAt = rhn - planted.length() - 5;
        for (int i = 0; i < planted.length(); i++) hArr[plantAt + i] = planted.charAt(i);
        String randHaystack = new String(hArr);
        timedCheck("large planted-needle near tail (|h|=" + rhn + ")",
                randHaystack, planted, randHaystack.indexOf(planted));

        // Needle guaranteed absent in the random haystack -> -1.
        String absent = "zzzzz"; // 'z' never generated above
        timedCheck("large absent needle (|h|=" + rhn + ")",
                randHaystack, absent, randHaystack.indexOf(absent));

        // Overlap-heavy periodic haystack with a near-period needle (KMP stress).
        StringBuilder per = new StringBuilder();
        for (int i = 0; i < 5000; i++) per.append("ab"); // length 10000
        String periodic = per.toString();
        String periodicNeedle = "ababababab"; // matches at index 0
        timedCheck("large periodic match (|h|=" + periodic.length() + ")",
                periodic, periodicNeedle, periodic.indexOf(periodicNeedle));

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static void timedCheck(String label, String haystack, String needle, int expected) {
        total++;
        try {
            long start = System.nanoTime();
            int actual = new Answer().strStr(haystack, needle);
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

    static void check(String haystack, String needle, int expected) {
        total++;
        String hShown = haystack.length() > 30 ? haystack.substring(0, 27) + "..." : haystack;
        try {
            int actual = new Answer().strStr(haystack, needle);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m strStr(\"" + hShown + "\", \"" + needle + "\") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m strStr(\"" + hShown + "\", \"" + needle + "\") expected " + expected + " but got " + actual);
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m strStr(\"" + hShown + "\", \"" + needle + "\") threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
