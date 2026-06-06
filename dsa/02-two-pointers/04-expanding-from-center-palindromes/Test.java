import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    static boolean isPalindrome(String s) {
        int i = 0, j = s.length() - 1;
        while (i < j) {
            if (s.charAt(i) != s.charAt(j)) return false;
            i++; j--;
        }
        return true;
    }

    // Independent oracle: longest palindromic substring length via center expansion (O(n^2)).
    static int oracleMaxLen(String s) {
        if (s == null || s.isEmpty()) return 0;
        int best = 1;
        int n = s.length();
        for (int c = 0; c < n; c++) {
            // odd center
            int lo = c, hi = c;
            while (lo >= 0 && hi < n && s.charAt(lo) == s.charAt(hi)) { lo--; hi++; }
            best = Math.max(best, hi - lo - 1);
            // even center
            lo = c; hi = c + 1;
            while (lo >= 0 && hi < n && s.charAt(lo) == s.charAt(hi)) { lo--; hi++; }
            best = Math.max(best, hi - lo - 1);
        }
        return best;
    }

    // The problem allows ANY longest palindromic substring, so validate:
    //  (1) actual is a substring of s,
    //  (2) actual is itself a palindrome,
    //  (3) actual length equals the known maximum length.
    static void check(String name, String s, int expectedLen, Set<String> acceptableExamples) {
        total++;
        try {
            String actual = new Answer().longestPalindrome(s);
            boolean ok = actual != null
                && s.contains(actual)
                && isPalindrome(actual)
                && actual.length() == expectedLen;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (got \"" + actual + "\")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | input s=\"" + brief(s) + "\""
                    + " | expected a palindromic substring of length " + expectedLen
                    + " (e.g. " + acceptableExamples + ")"
                    + " | actual=\"" + actual + "\" (len=" + (actual == null ? "null" : actual.length()) + ")");
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                + " | input s=\"" + brief(s) + "\""
                + " | expected length " + expectedLen
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Convenience overload: derive expected length from the independent oracle.
    static void checkOracle(String name, String s) {
        check(name, s, oracleMaxLen(s), Collections.singleton("(oracle len=" + oracleMaxLen(s) + ")"));
    }

    // Large/timed variant: validates the property against the oracle and flags catastrophic slowness.
    static void checkTimed(String name, String s) {
        total++;
        int expectedLen = oracleMaxLen(s);
        long t0 = System.nanoTime();
        try {
            String actual = new Answer().longestPalindrome(s);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = actual != null
                && s.contains(actual)
                && isPalindrome(actual)
                && actual.length() == expectedLen
                && ms <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (len=" + actual.length() + ", " + ms + " ms)");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                    + " | s.length=" + s.length()
                    + " | expected max palindrome length " + expectedLen
                    + " | actual=\"" + brief(actual) + "\" (len=" + (actual == null ? "null" : actual.length()) + ")"
                    + (ms > 3000 ? " | too slow" : ""));
            }
        } catch (Throwable e) {
            long ms = (System.nanoTime() - t0) / 1_000_000;
            System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                + " | s.length=" + s.length()
                + " | expected length " + expectedLen
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static String brief(String s) {
        if (s == null) return "null";
        if (s.length() <= 40) return s;
        return s.substring(0, 20) + "...(" + s.length() + ")..." + s.substring(s.length() - 10);
    }

    public static void main(String[] args) {
        // ---------- Existing cases (kept) ----------
        // Examples from QUESTION.md
        check("example 1 (babad)", "babad", 3, new HashSet<>(Arrays.asList("bab", "aba")));
        check("example 2 (cbbd)", "cbbd", 2, new HashSet<>(Arrays.asList("bb")));
        check("example 3 (single char)", "a", 1, new HashSet<>(Arrays.asList("a")));

        // Corner: two same chars (even palindrome)
        check("two same", "bb", 2, new HashSet<>(Arrays.asList("bb")));

        // Corner: two different chars -> any single char, length 1
        check("two different", "ab", 1, new HashSet<>(Arrays.asList("a", "b")));

        // Corner: whole string is a palindrome (odd)
        check("whole odd palindrome", "racecar", 7, new HashSet<>(Arrays.asList("racecar")));

        // Corner: whole string is a palindrome (even)
        check("whole even palindrome", "abccba", 6, new HashSet<>(Arrays.asList("abccba")));

        // Corner: no repeats -> length 1
        check("all distinct", "abcde", 1, new HashSet<>(Arrays.asList("a", "b", "c", "d", "e")));

        // Corner: all identical chars
        check("all identical", "aaaa", 4, new HashSet<>(Arrays.asList("aaaa")));

        // Corner: palindrome embedded in the middle
        check("embedded middle", "xyzabacba", 3, new HashSet<>(Arrays.asList("aba")));

        // Corner: digits included
        check("with digits", "12321ab", 5, new HashSet<>(Arrays.asList("12321")));

        // Corner: mixed letters and digits, even center
        check("mixed even", "a1221b", 4, new HashSet<>(Arrays.asList("1221")));

        // Corner: palindrome at the very end
        check("palindrome at end", "abcdcba", 7, new HashSet<>(Arrays.asList("abcdcba")));

        // Corner: longer with multiple candidates, longest wins
        check("longest among many", "forgeeksskeegfor", 10, new HashSet<>(Arrays.asList("geeksskeeg")));

        // Corner: case sensitivity (uppercase differs from lowercase)
        check("case sensitive", "Aa", 1, new HashSet<>(Arrays.asList("A", "a")));

        // ---------- New corner cases ----------
        // Minimum length string (length 1) with a digit
        check("single digit", "7", 1, new HashSet<>(Arrays.asList("7")));
        // Two identical digits (even palindrome)
        check("two same digits", "99", 2, new HashSet<>(Arrays.asList("99")));
        // Palindrome at the very start
        check("palindrome at start", "abadef", 3, new HashSet<>(Arrays.asList("aba")));
        // Odd palindrome of length 3 in the middle of distinct chars
        check("odd center 3", "xqzqy", 3, new HashSet<>(Arrays.asList("qzq")));
        // Even palindrome flanked by mismatches
        check("even center flanked", "xabbay", 4, new HashSet<>(Arrays.asList("abba")));
        // Two separate palindromes, longer one wins
        check("two palindromes longer wins", "abaxyzzyx", 6, new HashSet<>(Arrays.asList("xyzzyx")));
        // Repeated pair pattern -> whole string palindrome of even length
        check("repeated abccba style", "noon", 4, new HashSet<>(Arrays.asList("noon")));
        // Alternating two letters (odd) -> whole string is a palindrome
        check("alternating abababa", "abababa", 7, new HashSet<>(Arrays.asList("abababa")));
        // Alternating two letters (even) -> length n-1 palindrome
        checkOracle("alternating even ababab", "ababab");
        // All same digit
        check("all same digit", "5555555", 7, new HashSet<>(Arrays.asList("5555555")));
        // Mixed case where only single chars are palindromic
        check("mixed case distinct", "AbCdE", 1, new HashSet<>(Arrays.asList("A", "b", "C", "d", "E")));
        // Long run with a palindrome spanning letters and digits
        check("letters and digits span", "ab1cc1ba", 8, new HashSet<>(Arrays.asList("ab1cc1ba")));
        // Palindrome requiring even-center detection in a longer string
        check("even center deep", "qwertytrewq", 11, new HashSet<>(Arrays.asList("qwertytrewq")));
        // Nearly-palindrome (off by one char) -> longest is the inner part
        checkOracle("near palindrome off by one", "abcdedcbaX");
        // Two equal-length palindromes -> either acceptable (length checked via oracle)
        checkOracle("two equal length palindromes", "aabbaa_ccddcc".replace('_', 'z'));

        // ---------- Large / performance cases (oracle-verified, generous time budget) ----------
        // Constraint upper bound is s.length == 1000. A center-expansion solution is O(n^2)
        // in the worst case (e.g. all-identical chars); the budget tolerates that but flags
        // anything catastrophically worse.
        Random rnd = new Random(42);

        // Case A: maximum length all-identical string -> the whole string is the answer
        // and naive center expansion degrades to its quadratic worst case here.
        {
            int n = 1000;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append('a');
            checkTimed("large n=1000 all identical", sb.toString());
        }

        // Case B: maximum length random alphanumeric string (few/short palindromes).
        {
            int n = 1000;
            String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
            checkTimed("large n=1000 random alphanumeric", sb.toString());
        }

        // Case C: maximum length string that is itself a palindrome (random first half mirrored).
        {
            int half = 500;
            String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < half; i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
            StringBuilder rev = new StringBuilder(sb).reverse();
            String s = sb.toString() + rev.toString(); // length 1000, full palindrome
            checkTimed("large n=1000 whole palindrome", s);
        }

        // Case D: large string with a long palindrome buried in random noise.
        {
            String alphabet = "abcdefghijklmnopqrstuvwxyz";
            StringBuilder noise1 = new StringBuilder();
            for (int i = 0; i < 300; i++) noise1.append(alphabet.charAt(rnd.nextInt(26)));
            StringBuilder core = new StringBuilder();
            for (int i = 0; i < 200; i++) core.append(alphabet.charAt(rnd.nextInt(26)));
            String palin = core.toString() + new StringBuilder(core).reverse().toString(); // length 400
            StringBuilder noise2 = new StringBuilder();
            for (int i = 0; i < 300; i++) noise2.append(alphabet.charAt(rnd.nextInt(26)));
            String s = noise1.toString() + palin + noise2.toString(); // length 1000
            checkTimed("large n=1000 buried palindrome", s);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
