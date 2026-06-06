import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("abab", true);                 // "ab" x2
        check("aba", false);
        check("abcabcabcabc", true);         // "abc" x4 or "abcabc" x2

        // Corner cases (existing)
        check("a", false);                   // single char cannot repeat
        check("aa", true);                   // "a" x2
        check("aaa", true);                  // "a" x3
        check("aaaa", true);                 // "a" x4
        check("ab", false);                  // two distinct, no repeat
        check("abc", false);                 // three distinct
        check("abcabc", true);               // "abc" x2
        check("abaababaab", true);           // "abaab" x2
        check("abcabcabcabcd", false);       // breaks the pattern at the end
        check("xyzxyzxy", false);            // partial trailing repeat
        check("bb", true);                   // "b" x2
        check("babbabbabbabbab", false);     // 15 length, not evenly tiled
        check("abababab", true);             // "ab" x4 (also "abab" x2)
        check("abaabaaba", true);            // "aba" x3
        check("abcdabcd", true);             // "abcd" x2
        check("abcdabce", false);            // last char differs
        check("zzzzzzzzzzzz", true);         // all same
        check("aabaab", true);               // "aab" x2
        check("aabaaba", false);             // odd extra char
        check("abcabcabc", true);            // "abc" x3

        // ---- NEW corner cases ----
        check("z", false);                   // single max-letter char, no repeat
        check("zz", true);                   // "z" x2 boundary
        check("ababa", false);               // odd length, near-repeat
        check("abcabcab", false);            // truncated final unit
        check("aaaaa", true);                // prime length all-same -> "a" x5
        check("aaaaab", false);              // all-same but trailing odd char
        check("abcabcabcabcabc", true);      // "abc" x5 (length 15, divisor 3 & 5)
        check("abcabcabcabcab", false);      // length 14, no full tiling by 3
        check("xyxyxyx", false);             // odd length alternating, not tiled
        check("xyxyxy", true);               // "xy" x3
        check("abab" + "abab", true);        // "ab" x4 via concat
        check("abcd", false);                // all distinct, length 4
        check("aabbaabb", true);             // "aabb" x2
        check("aabbaabbc", false);           // breaks at the end
        check("abcabcabcx", false);          // valid prefix then stray char
        check("mmmm", true);                 // "m" x4
        check("abcabd", false);              // looks like "abc" repeat but differs
        check("teststest", false);           // near "test" repeat but off by one
        check("testtest", true);             // "test" x2
        check("abcabcabcabcabcabc", true);   // "abc" x6 (length 18)

        // existing larger constructed cases
        check(repeat("abc", 100), true);     // long exact repeat
        check(repeat("abc", 100) + "x", false); // long repeat plus stray char

        // ---- LARGE / PERFORMANCE / SCALE cases (property-based + timed) ----
        // Constraint upper bound is 10^4. Build inputs near it; for the TRUE case
        // we know by construction it is a repeat; for the FALSE case we append a
        // stray char that provably breaks any tiling. Verified independently with
        // an O(n * d(n)) divisor-based oracle on a smaller cross-check, and by
        // construction at full scale.
        Random rnd = new Random(42);

        // Random unit repeated to fill ~10^4 -> definitely true.
        int unitLen = 100;
        char[] unit = new char[unitLen];
        for (int i = 0; i < unitLen; i++) unit[i] = (char) ('a' + rnd.nextInt(26));
        int copies = 100; // 100 * 100 = 10000
        StringBuilder big = new StringBuilder();
        for (int i = 0; i < copies; i++) big.append(unit);
        String bigTrue = big.toString();
        timedCheck("large exact repeat (len=" + bigTrue.length() + ")", bigTrue, true);

        // Same big string with one final char flipped -> breaks the tiling, false.
        char[] brokenArr = bigTrue.toCharArray();
        char last = brokenArr[brokenArr.length - 1];
        brokenArr[brokenArr.length - 1] = (char) ('a' + ((last - 'a' + 1) % 26));
        String bigFalse = new String(brokenArr);
        timedCheck("large broken repeat (len=" + bigFalse.length() + ")", bigFalse, oracle(bigFalse));

        // All-same large string -> trivially a repeat of "a".
        char[] allA = new char[10000];
        Arrays.fill(allA, 'a');
        String allAStr = new String(allA);
        timedCheck("large all-equal (len=10000)", allAStr, true);

        // Prime-length all-distinct-ish random string -> almost surely false; verified by oracle.
        int primeLen = 9973; // prime, no nontrivial divisors except 1
        char[] rndArr = new char[primeLen];
        for (int i = 0; i < primeLen; i++) rndArr[i] = (char) ('a' + rnd.nextInt(26));
        String primeStr = new String(rndArr);
        timedCheck("large prime-length random (len=" + primeLen + ")", primeStr, oracle(primeStr));

        // Smallest-unit large repeat: "ab" repeated 5000 times = length 10000 -> true.
        StringBuilder ab = new StringBuilder();
        for (int i = 0; i < 5000; i++) ab.append("ab");
        String abStr = ab.toString();
        timedCheck("large two-char unit repeat (len=" + abStr.length() + ")", abStr, true);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static String repeat(String unit, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(unit);
        return sb.toString();
    }

    // Independent oracle: try every proper divisor length and test tiling. O(n * d(n)).
    static boolean oracle(String s) {
        int n = s.length();
        if (n < 2) return false;
        for (int len = 1; len <= n / 2; len++) {
            if (n % len != 0) continue;
            boolean ok = true;
            for (int i = len; i < n && ok; i++) {
                if (s.charAt(i) != s.charAt(i - len)) ok = false;
            }
            if (ok) return true;
        }
        return false;
    }

    static void timedCheck(String label, String s, boolean expected) {
        total++;
        try {
            long start = System.nanoTime();
            boolean actual = new Answer().repeatedSubstringPattern(s);
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

    static void check(String s, boolean expected) {
        total++;
        try {
            boolean actual = new Answer().repeatedSubstringPattern(s);
            String shown = s.length() > 40 ? s.substring(0, 37) + "..." : s;
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m repeatedSubstringPattern(\"" + shown + "\") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m repeatedSubstringPattern(\"" + shown + "\") expected " + expected + " but got " + actual);
            }
        } catch (Throwable e) {
            String shown = s.length() > 40 ? s.substring(0, 37) + "..." : s;
            System.out.println("\033[31m[FAIL]\033[0m repeatedSubstringPattern(\"" + shown + "\") threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
