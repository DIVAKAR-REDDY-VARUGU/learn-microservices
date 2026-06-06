import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("A man, a plan, a canal: Panama", true);
        check("race a car", false);
        check(" ", true);                       // empty after cleaning

        // Corner cases (existing)
        check("", true);                         // empty string -> palindrome
        check("a", true);                        // single alphanumeric
        check("Z", true);                        // single uppercase
        check("7", true);                        // single digit
        check(".,", true);                       // only punctuation -> empty -> true
        check("ab", false);                      // two different chars
        check("aa", true);                       // two equal chars
        check("aba", true);                      // odd length palindrome
        check("abba", true);                     // even length palindrome
        check("abc", false);                     // simple non-palindrome
        check("Aa", true);                       // case-insensitive
        check("AbBa", true);                     // mixed case palindrome
        check("0P", false);                      // digit vs letter, not equal
        check("a.", true);                       // trailing punctuation ignored
        check(".a", true);                       // leading punctuation ignored
        check("Was it a car or a cat I saw?", true);   // classic phrase
        check("No 'x' in Nixon", true);          // apostrophes ignored
        check("12321", true);                    // numeric palindrome
        check("123421", false);                  // numeric non-palindrome
        check("!!!", true);                      // all non-alphanumeric
        check("Madam In Eden, I'm Adam", true);  // phrase with punctuation
        check("abcba!!!", true);                 // palindrome with trailing junk
        check("abc!cba", true);                  // punctuation in the middle
        check("ab@ba", true);                    // special char in middle ignored

        // ---- NEW corner cases ----
        check("9", true);                        // single digit boundary
        check("99", true);                       // two equal digits
        check("98", false);                      // two different digits
        check("aA", true);                       // case-insensitive reversed
        check("  ,  ", true);                    // multiple spaces and comma -> empty
        check("a1a", true);                      // letters and digit, palindrome
        check("1a1", true);                      // digit-letter-digit palindrome
        check("1a2", false);                     // not a palindrome with digits
        check("AbcBA", false);                   // mixed case but middle breaks (b vs c)
        check("AbcbA", true);                    // mixed case true palindrome
        check("ab_ba", true);                    // underscore is non-alphanumeric, ignored
        check("a b a", true);                    // spaces ignored, palindrome
        check("a b c", false);                   // spaces ignored, not palindrome
        check("Able was I ere I saw Elba", true); // classic long phrase
        check("Never odd or even", true);        // classic phrase
        check("palindrome", false);              // ordinary word
        check(".,;:'\"", true);                   // only symbols -> empty -> true
        check("1" + "0".repeat(0) + "1", true);  // "11" via concat, palindrome
        check("ab,.ba", true);                   // punctuation between mirrored halves
        check("abccБa", false);                  // contains a non-ASCII-irrelevant break (still mismatch)

        // ---- LARGE / PERFORMANCE / SCALE cases (property-based + timed) ----
        Random rnd = new Random(42);
        int n = 200000; // constraint upper bound is 2 * 10^5

        // Build a guaranteed palindrome of length ~n (lowercase letters), with the
        // exact-match verified independently via the oracle.
        char[] half = new char[n / 2];
        for (int i = 0; i < half.length; i++) half[i] = (char) ('a' + rnd.nextInt(26));
        StringBuilder pal = new StringBuilder();
        pal.append(half);
        pal.append('m'); // single middle char -> odd length palindrome
        for (int i = half.length - 1; i >= 0; i--) pal.append(half[i]);
        String palStr = pal.toString();
        timedCheck("large clean palindrome (len=" + palStr.length() + ")", palStr, oracle(palStr));

        // Same palindrome but with non-alphanumeric noise injected throughout;
        // cleaning must still yield a palindrome.
        StringBuilder noisy = new StringBuilder();
        for (int i = 0; i < palStr.length(); i++) {
            noisy.append(palStr.charAt(i));
            if ((i & 7) == 0) noisy.append(", "); // inject punctuation + space
        }
        String noisyStr = noisy.toString();
        timedCheck("large noisy palindrome (len=" + noisyStr.length() + ")", noisyStr, oracle(noisyStr));

        // Large NON-palindrome: random string; verified by oracle (almost surely false).
        char[] randArr = new char[n];
        for (int i = 0; i < n; i++) randArr[i] = (char) ('a' + rnd.nextInt(26));
        // Force a guaranteed mismatch at the extremes.
        randArr[0] = 'a';
        randArr[n - 1] = 'b';
        String randStr = new String(randArr);
        timedCheck("large random non-palindrome (len=" + n + ")", randStr, oracle(randStr));

        // Large all-same-character string -> palindrome (heavy duplicates).
        char[] allA = new char[n];
        Arrays.fill(allA, 'a');
        String allAStr = new String(allA);
        timedCheck("large all-equal palindrome (len=" + n + ")", allAStr, oracle(allAStr));

        // Large mostly-punctuation string that cleans to empty -> palindrome.
        char[] allDots = new char[n];
        Arrays.fill(allDots, '.');
        String allDotsStr = new String(allDots);
        timedCheck("large all-punctuation -> empty (len=" + n + ")", allDotsStr, oracle(allDotsStr));

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent oracle: clean (lowercase alphanumeric) then two-pointer compare.
    static boolean oracle(String s) {
        int i = 0, j = s.length() - 1;
        while (i < j) {
            char a = s.charAt(i);
            char b = s.charAt(j);
            if (!isAlnum(a)) { i++; continue; }
            if (!isAlnum(b)) { j--; continue; }
            if (lower(a) != lower(b)) return false;
            i++; j--;
        }
        return true;
    }

    static boolean isAlnum(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    static char lower(char c) {
        if (c >= 'A' && c <= 'Z') return (char) (c - 'A' + 'a');
        return c;
    }

    static void timedCheck(String label, String s, boolean expected) {
        total++;
        try {
            long start = System.nanoTime();
            boolean actual = new Answer().isPalindrome(s);
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
            boolean actual = new Answer().isPalindrome(s);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m isPalindrome(\"" + s + "\") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m isPalindrome(\"" + s + "\") expected " + expected + " but got " + actual);
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m isPalindrome(\"" + s + "\") threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
