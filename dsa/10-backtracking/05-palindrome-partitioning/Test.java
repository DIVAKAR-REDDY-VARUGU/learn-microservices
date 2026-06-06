import java.util.*;

// JUnit-free harness for Answer.partition(String).
// Partitions are order-independent across the outer list, but each partition is an
// ORDERED sequence of substrings (concatenation must equal s), so inner order is kept.
public class Test {
    static int total = 0, pass = 0;

    public static void main(String[] args) {
        // ----- Existing cases (kept) -----
        // Example 1
        check("aab", lls(ls("a", "a", "b"), ls("aa", "b")));
        // Example 2: single char
        check("a", lls(ls("a")));
        // Example 3
        check("aba", lls(ls("a", "b", "a"), ls("aba")));
        // All equal chars
        check("aaa", lls(ls("a", "a", "a"), ls("a", "aa"), ls("aa", "a"), ls("aaa")));
        // No multi-char palindrome possible -> only single chars
        check("abc", lls(ls("a", "b", "c")));
        // Two equal chars
        check("bb", lls(ls("b", "b"), ls("bb")));
        // Two different chars
        check("ab", lls(ls("a", "b")));
        // Longer palindromic structure
        check("aabaa", lls(
            ls("a", "a", "b", "a", "a"),
            ls("a", "a", "b", "aa"),
            ls("a", "aba", "a"),
            ls("aa", "b", "a", "a"),
            ls("aa", "b", "aa"),
            ls("aabaa")));
        // Each partition must concatenate back to s and be all palindromes
        checkValid("valid partitions of 'abccba'", "abccba");

        // ----- New corner cases -----
        // Single char at a different letter (min length)
        check("z", lls(ls("z")));
        // Two equal chars, second letter
        check("cc", lls(ls("c", "c"), ls("cc")));
        // Three distinct chars (no multi-char palindrome)
        check("xyz", lls(ls("x", "y", "z")));
        // Even palindrome of length 4
        check("abba", lls(
            ls("a", "b", "b", "a"),
            ls("a", "bb", "a"),
            ls("abba")));
        // Four equal chars -> partitions count = 2^(4-1) = 8
        checkValidCount("aaaa valid count=8", "aaaa", 8);
        // Five equal chars -> 2^(5-1) = 16
        checkValidCount("aaaaa valid count=16", "aaaaa", 16);
        // All-distinct string -> exactly one partition (all singletons)
        checkValidCount("abcde valid count=1", "abcde", 1);
        check("abcde", lls(ls("a", "b", "c", "d", "e")));
        // Odd-length palindrome center
        check("aca", lls(ls("a", "c", "a"), ls("aca")));
        // Concatenation property on a longer nested palindrome
        checkValid("valid partitions of 'racecar'", "racecar");
        // Concatenation property on alternating chars
        checkValid("valid partitions of 'abababab'", "abababab");
        // Concatenation property with a long even palindrome
        checkValid("valid partitions of 'abccbaabccba'", "abccbaabccba");
        // Two-letter alphabet, no long palindrome boundaries
        check("abab".substring(0, 2), lls(ls("a", "b")));
        // Every piece in every partition is a palindrome (property only)
        checkAllPalindromes("all-pal 'aabbaa'", "aabbaa");
        checkAllPalindromes("all-pal 'noon'", "noon");
        // Singleton-only partition is always present
        checkHasAllSingletons("singletons present 'level'", "level");
        // The whole string as one piece appears iff s is a palindrome
        checkWholeIfPalindrome("whole-if-pal 'deed'", "deed", true);
        checkWholeIfPalindrome("whole-if-not 'deeb'", "deeb", false);

        // ----- Scale / performance case (max constraint length=16) -----
        // length is capped at 16; worst case is all-equal "aaaa...a" -> 2^15 partitions.
        scaleAllEqual("scale 'a'*16 (max constraint)", 16);
        // A pseudo-random length-16 string (seed 42), property-verified and timed.
        scaleRandom("scale random len=16");

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static void check(String s, List<List<String>> expected) {
        total++;
        try {
            List<List<String>> actual = new Answer().partition(s);
            List<String> a = norm(actual);
            List<String> e = norm(expected);
            if (a.equals(e)) {
                System.out.println("\033[32m[PASS]\033[0m partition(\"" + s + "\")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m partition(\"" + s + "\")"
                    + " expected=" + e + " actual=" + a);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m partition(\"" + s + "\") threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkValid(String name, String s) {
        total++;
        try {
            List<List<String>> actual = new Answer().partition(s);
            boolean ok = !actual.isEmpty();
            for (List<String> part : actual) {
                StringBuilder sb = new StringBuilder();
                for (String piece : part) {
                    sb.append(piece);
                    if (!isPal(piece)) { ok = false; }
                }
                if (!sb.toString().equals(s)) ok = false;
            }
            Set<String> distinct = new HashSet<>(norm(actual));
            if (ok && distinct.size() == actual.size()) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " ok=" + ok
                    + " distinct=" + distinct.size() + " size=" + actual.size());
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Validity + an exact expected count (independent oracle for special-structure strings).
    static void checkValidCount(String name, String s, int expectedCount) {
        total++;
        try {
            List<List<String>> actual = new Answer().partition(s);
            boolean ok = true;
            for (List<String> part : actual) {
                StringBuilder sb = new StringBuilder();
                for (String piece : part) {
                    sb.append(piece);
                    if (!isPal(piece)) ok = false;
                }
                if (!sb.toString().equals(s)) ok = false;
            }
            Set<String> distinct = new HashSet<>(norm(actual));
            if (ok && actual.size() == expectedCount && distinct.size() == expectedCount) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=" + expectedCount
                    + " size=" + actual.size() + " distinct=" + distinct.size() + " ok=" + ok);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkAllPalindromes(String name, String s) {
        total++;
        try {
            List<List<String>> actual = new Answer().partition(s);
            boolean ok = !actual.isEmpty();
            for (List<String> part : actual) {
                for (String piece : part) if (!isPal(piece)) { ok = false; break; }
                if (!ok) break;
            }
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " a piece was not a palindrome");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkHasAllSingletons(String name, String s) {
        total++;
        try {
            List<List<String>> actual = new Answer().partition(s);
            List<String> singletons = new ArrayList<>();
            for (int i = 0; i < s.length(); i++) singletons.add(String.valueOf(s.charAt(i)));
            boolean found = false;
            for (List<String> part : actual) if (part.equals(singletons)) { found = true; break; }
            if (found) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " all-singletons partition missing");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkWholeIfPalindrome(String name, String s, boolean expectWhole) {
        total++;
        try {
            List<List<String>> actual = new Answer().partition(s);
            boolean hasWhole = false;
            for (List<String> part : actual) {
                if (part.size() == 1 && part.get(0).equals(s)) { hasWhole = true; break; }
            }
            if (hasWhole == expectWhole) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " hasWhole=" + hasWhole + " expected=" + expectWhole);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Scale: all-equal string of given length. # partitions = 2^(len-1). Property-verified + timed.
    static void scaleAllEqual(String name, int len) {
        total++;
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) sb.append('a');
            String s = sb.toString();
            long t0 = System.nanoTime();
            List<List<String>> actual = new Answer().partition(s);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

            long expected = 1L << (len - 1);
            boolean ok = true;
            for (List<String> part : actual) {
                StringBuilder cat = new StringBuilder();
                for (String piece : part) {
                    cat.append(piece);
                    if (!isPal(piece)) ok = false;
                }
                if (!cat.toString().equals(s)) ok = false;
                if (!ok) break;
            }
            Set<String> distinct = new HashSet<>(norm(actual));
            boolean good = ok && actual.size() == expected
                && distinct.size() == expected && elapsedMs <= 3000;
            if (good) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=" + expected
                    + " size=" + actual.size() + " distinct=" + distinct.size()
                    + " ok=" + ok + " elapsed=" + elapsedMs + "ms (budget 3000ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Scale: random length-16 string over a small alphabet (seed 42), property-verified + timed.
    static void scaleRandom(String name) {
        total++;
        try {
            Random rnd = new Random(42);
            char[] alpha = {'a', 'b', 'c'};
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) sb.append(alpha[rnd.nextInt(alpha.length)]);
            String s = sb.toString();

            long t0 = System.nanoTime();
            List<List<String>> actual = new Answer().partition(s);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

            boolean ok = !actual.isEmpty();
            for (List<String> part : actual) {
                StringBuilder cat = new StringBuilder();
                for (String piece : part) {
                    cat.append(piece);
                    if (!isPal(piece)) ok = false;
                }
                if (!cat.toString().equals(s)) ok = false;
                if (!ok) break;
            }
            Set<String> distinct = new HashSet<>(norm(actual));
            boolean good = ok && distinct.size() == actual.size() && elapsedMs <= 3000;
            if (good) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " s=\"" + s + "\" size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " s=\"" + s + "\" ok=" + ok
                    + " distinct=" + distinct.size() + " size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms (budget 3000ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static boolean isPal(String x) {
        int i = 0, j = x.length() - 1;
        while (i < j) if (x.charAt(i++) != x.charAt(j--)) return false;
        return true;
    }

    // Sort only the OUTER collection; keep each partition's inner order.
    static List<String> norm(List<List<String>> lists) {
        List<String> keys = new ArrayList<>();
        for (List<String> inner : lists) keys.add(inner.toString());
        Collections.sort(keys);
        return keys;
    }

    static List<String> ls(String... xs) {
        return new ArrayList<>(Arrays.asList(xs));
    }

    @SafeVarargs
    static List<List<String>> lls(List<String>... xs) {
        return new ArrayList<>(Arrays.asList(xs));
    }
}
