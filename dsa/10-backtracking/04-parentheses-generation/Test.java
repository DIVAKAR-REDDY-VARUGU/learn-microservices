import java.util.*;

// JUnit-free harness for Answer.generateParenthesis(int).
// Output is a list of strings in any order -> sort both before comparing.
public class Test {
    static int total = 0, pass = 0;

    public static void main(String[] args) {
        // ----- Existing cases (kept) -----
        // Example 2: n=1
        check(1, Arrays.asList("()"));
        // Example 3: n=2
        check(2, Arrays.asList("(())", "()()"));
        // Example 1: n=3
        check(3, Arrays.asList("((()))", "(()())", "(())()", "()(())", "()()()"));
        // n=4: Catalan(4)=14 distinct, all well-formed
        checkCount("n=4 count=14", 4, 14);
        // n=5: Catalan(5)=42
        checkCount("n=5 count=42", 5, 42);
        // n=8 (upper bound): Catalan(8)=1430
        checkCount("n=8 count=1430", 8, 1430);
        // Validity + distinctness check for n=6
        checkValid("n=6 valid+distinct", 6);

        // ----- New corner cases -----
        // n=1 (min constraint) exact, again with explicit list
        check(1, Collections.singletonList("()"));
        // n=2 distinct count = Catalan(2) = 2
        checkCount("n=2 count=2", 2, 2);
        // n=3 distinct count = Catalan(3) = 5
        checkCount("n=3 count=5", 3, 5);
        // n=6 count = Catalan(6) = 132
        checkCount("n=6 count=132", 6, 132);
        // n=7 count = Catalan(7) = 429
        checkCount("n=7 count=429", 7, 429);
        // Every result for n=1 has length 2
        checkLengths("n=1 lengths", 1);
        // Every result for n=5 has length 10
        checkLengths("n=5 lengths", 5);
        // Every result for n=8 (max) has length 16
        checkLengths("n=8 lengths", 8);
        // Each n=4 string is well-formed and distinct
        checkValid("n=4 valid+distinct", 4);
        // Each n=5 string is well-formed and distinct
        checkValid("n=5 valid+distinct", 5);
        // Each n=7 string is well-formed and distinct
        checkValid("n=7 valid+distinct", 7);
        // The fully-nested string is always present (n=4)
        checkContains("n=4 has fully-nested", 4, "(((())))");
        // The fully-flat string is always present (n=4)
        checkContains("n=4 has fully-flat", 4, "()()()()");
        // The fully-nested string present for n=6
        checkContains("n=6 has fully-nested", 6, nested(6));
        // Balanced-prefix property: no string ever has more ')' than '(' at any prefix (n=5)
        checkPrefixBalance("n=5 prefix-balance", 5);
        // Count equals Catalan via independent formula for n=4
        checkCatalanOracle("n=4 catalan-oracle", 4);
        checkCatalanOracle("n=6 catalan-oracle", 6);

        // ----- Scale / performance case (max constraint n=8 -> 1430 strings) -----
        // n is capped at 8 by constraints, so the upper bound is the "large" case.
        scaleCheck("scale n=8 (max constraint)", 8);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static String nested(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append('(');
        for (int i = 0; i < n; i++) sb.append(')');
        return sb.toString();
    }

    static long catalan(int n) {
        long c = 1;
        for (int i = 0; i < n; i++) c = c * (2L * n - i) / (i + 1);
        return c / (n + 1);
    }

    static void check(int n, List<String> expected) {
        total++;
        try {
            List<String> actual = new Answer().generateParenthesis(n);
            List<String> a = sorted(actual);
            List<String> e = sorted(expected);
            if (a.equals(e)) {
                System.out.println("\033[32m[PASS]\033[0m generateParenthesis(" + n + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m generateParenthesis(" + n + ")"
                    + " expected=" + e + " actual=" + a);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m generateParenthesis(" + n + ") threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkCount(String name, int n, int expectedCount) {
        total++;
        try {
            List<String> actual = new Answer().generateParenthesis(n);
            Set<String> distinct = new HashSet<>(actual);
            boolean allValid = true;
            for (String s : actual) if (!wellFormed(s, n)) { allValid = false; break; }
            if (actual.size() == expectedCount && distinct.size() == expectedCount && allValid) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=" + expectedCount
                    + " size=" + actual.size() + " distinct=" + distinct.size()
                    + " allValid=" + allValid);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkValid(String name, int n) {
        total++;
        try {
            List<String> actual = new Answer().generateParenthesis(n);
            Set<String> distinct = new HashSet<>(actual);
            boolean allValid = true;
            for (String s : actual) if (!wellFormed(s, n)) { allValid = false; break; }
            if (allValid && distinct.size() == actual.size()) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " allValid=" + allValid
                    + " distinct=" + distinct.size() + " size=" + actual.size());
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkLengths(String name, int n) {
        total++;
        try {
            List<String> actual = new Answer().generateParenthesis(n);
            boolean ok = !actual.isEmpty();
            for (String s : actual) if (s.length() != 2 * n) { ok = false; break; }
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " a string had wrong length");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkContains(String name, int n, String mustHave) {
        total++;
        try {
            List<String> actual = new Answer().generateParenthesis(n);
            if (new HashSet<>(actual).contains(mustHave)) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " missing \"" + mustHave + "\"");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: every string keeps a non-negative running balance at every prefix.
    static void checkPrefixBalance(String name, int n) {
        total++;
        try {
            List<String> actual = new Answer().generateParenthesis(n);
            boolean ok = !actual.isEmpty();
            for (String s : actual) {
                int bal = 0;
                for (int i = 0; i < s.length(); i++) {
                    bal += s.charAt(i) == '(' ? 1 : -1;
                    if (bal < 0) { ok = false; break; }
                }
                if (bal != 0) ok = false;
                if (!ok) break;
            }
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " a string violated prefix balance");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent oracle: count must equal the nth Catalan number.
    static void checkCatalanOracle(String name, int n) {
        total++;
        try {
            List<String> actual = new Answer().generateParenthesis(n);
            long expected = catalan(n);
            Set<String> distinct = new HashSet<>(actual);
            if (actual.size() == expected && distinct.size() == expected) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=" + expected
                    + " size=" + actual.size() + " distinct=" + distinct.size());
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Scale case: max-constraint n, verify by property (Catalan count, all valid, distinct), timed.
    static void scaleCheck(String name, int n) {
        total++;
        try {
            long t0 = System.nanoTime();
            List<String> actual = new Answer().generateParenthesis(n);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

            long expected = catalan(n);
            Set<String> distinct = new HashSet<>(actual);
            boolean allValid = true;
            for (String s : actual) if (!wellFormed(s, n)) { allValid = false; break; }
            boolean ok = actual.size() == expected && distinct.size() == expected
                && allValid && elapsedMs <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=" + expected
                    + " size=" + actual.size() + " distinct=" + distinct.size()
                    + " allValid=" + allValid + " elapsed=" + elapsedMs + "ms (budget 3000ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static boolean wellFormed(String s, int n) {
        if (s.length() != 2 * n) return false;
        int bal = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') bal++;
            else if (c == ')') bal--;
            else return false;
            if (bal < 0) return false;
        }
        return bal == 0;
    }

    static List<String> sorted(List<String> in) {
        List<String> copy = new ArrayList<>(in);
        Collections.sort(copy);
        return copy;
    }
}
