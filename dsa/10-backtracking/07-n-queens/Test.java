import java.util.*;

// JUnit-free harness for Answer.solveNQueens(int).
// Solutions are order-independent across the outer list; each solution is an ORDERED
// list of n strings (row 0..n-1), so inner order is significant and not sorted.
public class Test {
    static int total = 0, pass = 0;

    public static void main(String[] args) {
        // ----- Existing cases (kept) -----
        // Example 2: n=1 -> single trivial board
        check(1, lls(ls("Q")));
        // Example 3: n=2 -> no solution
        check(2, lls());
        // n=3 -> no solution
        check(3, lls());
        // Example 1: n=4 -> exactly two solutions
        check(4, lls(
            ls(".Q..", "...Q", "Q...", "..Q."),
            ls("..Q.", "Q...", "...Q", ".Q..")));
        // Count checks for larger n (known sequence)
        checkCount("n=5 count=10", 5, 10);
        checkCount("n=6 count=4", 6, 4);
        checkCount("n=7 count=40", 7, 40);
        checkCount("n=8 count=92", 8, 92);
        checkCount("n=9 count=352", 9, 352);
        // Validity: every returned board is a legal n-queens placement
        checkValid("n=6 valid boards", 6);

        // ----- New corner cases -----
        // n=2 and n=3 again via empty-result helper (no solution exists)
        checkEmpty("n=2 empty", 2);
        checkEmpty("n=3 empty", 3);
        // n=1 (min constraint): exactly one solution, count + validity
        checkCount("n=1 count=1", 1, 1);
        // Each n=4 board is structurally valid (one Q per row/col, no diagonal clash)
        checkValid("n=4 valid boards", 4);
        checkValid("n=5 valid boards", 5);
        checkValid("n=7 valid boards", 7);
        checkValid("n=8 valid boards", 8);
        // Distinctness: no duplicate board for n=5
        checkDistinct("n=5 distinct", 5);
        checkDistinct("n=8 distinct", 8);
        // Each board is exactly n x n with exactly one 'Q' per row (n=6)
        checkShape("n=6 shape", 6);
        checkShape("n=9 shape", 9);
        // Total queen count equals n on every board (n=7)
        checkQueenCount("n=7 queen-count", 7);
        // Columns used across a single board form a permutation of 0..n-1 (n=8)
        checkColumnPermutation("n=8 column-perm", 8);
        // Independent oracle: count matches the known n-queens sequence for n=4..6
        checkCount("n=4 count=2", 4, 2);
        checkCount("n=6 count=4 (oracle)", 6, 4);
        // For n=2,3 the result is exactly the empty list (strict equality)
        check(2, lls());
        check(3, lls());

        // ----- Scale / performance case (max constraint n=9 -> 352 solutions) -----
        // n is capped at 9 by constraints, so the upper bound is the "large" case.
        scaleCheck("scale n=9 (max constraint)", 9, 352);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static void check(int n, List<List<String>> expected) {
        total++;
        try {
            List<List<String>> actual = new Answer().solveNQueens(n);
            List<String> a = norm(actual);
            List<String> e = norm(expected);
            if (a.equals(e)) {
                System.out.println("\033[32m[PASS]\033[0m solveNQueens(" + n + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m solveNQueens(" + n + ")"
                    + " expected=" + e + " actual=" + a);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m solveNQueens(" + n + ") threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkCount(String name, int n, int expectedCount) {
        total++;
        try {
            List<List<String>> actual = new Answer().solveNQueens(n);
            Set<String> distinct = new HashSet<>(norm(actual));
            boolean allValid = true;
            for (List<String> b : actual) if (!valid(b, n)) { allValid = false; break; }
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
            List<List<String>> actual = new Answer().solveNQueens(n);
            boolean ok = !actual.isEmpty();
            for (List<String> b : actual) if (!valid(b, n)) ok = false;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " produced an invalid or empty board set");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkEmpty(String name, int n) {
        total++;
        try {
            List<List<String>> actual = new Answer().solveNQueens(n);
            if (actual.isEmpty()) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected empty actual=" + norm(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkDistinct(String name, int n) {
        total++;
        try {
            List<List<String>> actual = new Answer().solveNQueens(n);
            Set<String> distinct = new HashSet<>(norm(actual));
            if (distinct.size() == actual.size()) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " duplicate boards: size=" + actual.size()
                    + " distinct=" + distinct.size());
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Each board is n strings, each of length n, containing only 'Q'/'.'.
    static void checkShape(String name, int n) {
        total++;
        try {
            List<List<String>> actual = new Answer().solveNQueens(n);
            boolean ok = !actual.isEmpty();
            for (List<String> b : actual) {
                if (b.size() != n) { ok = false; break; }
                for (String row : b) {
                    if (row.length() != n) { ok = false; break; }
                    for (int c = 0; c < n; c++) {
                        char ch = row.charAt(c);
                        if (ch != 'Q' && ch != '.') { ok = false; break; }
                    }
                }
                if (!ok) break;
            }
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " a board had wrong shape/characters");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Each board contains exactly n queens.
    static void checkQueenCount(String name, int n) {
        total++;
        try {
            List<List<String>> actual = new Answer().solveNQueens(n);
            boolean ok = !actual.isEmpty();
            for (List<String> b : actual) {
                int q = 0;
                for (String row : b) for (int c = 0; c < row.length(); c++) if (row.charAt(c) == 'Q') q++;
                if (q != n) { ok = false; break; }
            }
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " a board did not have exactly " + n + " queens");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // The set of queen columns across rows must be a permutation of 0..n-1.
    static void checkColumnPermutation(String name, int n) {
        total++;
        try {
            List<List<String>> actual = new Answer().solveNQueens(n);
            boolean ok = !actual.isEmpty();
            for (List<String> b : actual) {
                boolean[] used = new boolean[n];
                int count = 0;
                for (String row : b) {
                    int col = row.indexOf('Q');
                    if (col < 0 || col >= n || used[col]) { ok = false; break; }
                    used[col] = true;
                    count++;
                }
                if (count != n) ok = false;
                if (!ok) break;
            }
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " columns were not a permutation of 0.." + (n - 1));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Scale: max-constraint n, verify count + validity + distinctness, timed.
    static void scaleCheck(String name, int n, int expectedCount) {
        total++;
        try {
            long t0 = System.nanoTime();
            List<List<String>> actual = new Answer().solveNQueens(n);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

            Set<String> distinct = new HashSet<>(norm(actual));
            boolean allValid = true;
            for (List<String> b : actual) if (!valid(b, n)) { allValid = false; break; }
            boolean ok = actual.size() == expectedCount && distinct.size() == expectedCount
                && allValid && elapsedMs <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=" + expectedCount
                    + " size=" + actual.size() + " distinct=" + distinct.size()
                    + " allValid=" + allValid + " elapsed=" + elapsedMs + "ms (budget 3000ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // A board is valid if it is n x n, has exactly one Q per row, and no two queens
    // share a column or diagonal.
    static boolean valid(List<String> board, int n) {
        if (board.size() != n) return false;
        int[] cols = new int[n];
        for (int r = 0; r < n; r++) {
            String row = board.get(r);
            if (row.length() != n) return false;
            int qCount = 0, qCol = -1;
            for (int c = 0; c < n; c++) {
                char ch = row.charAt(c);
                if (ch == 'Q') { qCount++; qCol = c; }
                else if (ch != '.') return false;
            }
            if (qCount != 1) return false;
            cols[r] = qCol;
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (cols[i] == cols[j]) return false;
                if (Math.abs(cols[i] - cols[j]) == Math.abs(i - j)) return false;
            }
        }
        return true;
    }

    // Sort only the OUTER collection; keep each board's inner row order.
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
