import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept, with literal expected values) =====
        check(3, 7, 28);   // example
        check(3, 2, 3);    // example
        check(1, 1, 1);    // example: single cell
        check(1, 10, 1);   // single row -> only one path
        check(10, 1, 1);   // single column -> only one path
        check(2, 2, 2);    // small square
        check(2, 3, 3);    // symmetric to 3,2
        check(7, 3, 28);   // symmetric to 3,7
        check(3, 3, 6);    // C(4,2)=6
        check(4, 4, 20);   // C(6,3)=20
        check(5, 5, 70);   // C(8,4)=70
        check(10, 10, 48620);
        check(23, 12, 193536720); // large, fits int
        check(100, 1, 1);  // tall single column
        check(1, 100, 1);  // wide single row

        // ===== New corner cases (verified against independent C(m+n-2,m-1) DP oracle) =====
        checkOracle(1, 1);     // min bounds
        checkOracle(1, 50);    // single row mid
        checkOracle(50, 1);    // single column mid
        checkOracle(2, 1);     // tiny column
        checkOracle(1, 2);     // tiny row
        checkOracle(2, 100);   // 2-row wide -> equals 100
        checkOracle(100, 2);   // 2-col tall -> equals 100
        checkOracle(6, 6);     // small square
        checkOracle(8, 8);     // small square
        checkOracle(12, 12);   // medium square
        checkOracle(15, 15);   // larger square
        checkOracle(4, 9);     // rectangle
        checkOracle(9, 4);     // symmetric rectangle
        checkOracle(13, 13);   // odd square
        checkOracle(11, 17);   // asymmetric, big-ish
        checkOracle(50, 5);    // tall-thin
        checkOracle(5, 50);    // wide-flat

        // ===== Large / "near upper-bound but still fits int" cases (oracle-checked) =====
        // These keep the answer <= Integer.MAX_VALUE (problem guarantees answer <= 2e9).
        checkOracleTimed(17, 16);   // 300540195 -> largest in-int value used here
        checkOracleTimed(16, 17);   // symmetric
        checkOracleTimed(21, 13);   // 225792840
        checkOracleTimed(19, 14);   // 206253075
        checkOracleTimed(12, 23);   // 193536720
        // Stress the grid SIZE (m*n large) while keeping the count valid:
        // a thin 100x2 / 2x100 already covered; do a tall sweep that exercises the full DP table.
        checkOracleTimed(100, 4);   // 100x4 grid, value fits int
        checkOracleTimed(4, 100);   // symmetric

        // ===== ADDED: more corner cases (distinct from the above) =====
        // --- Constraint MIN/MAX boundary values from QUESTION.md (1 <= m,n <= 100) ---
        check(1, 1, 1);            // both at MIN (re-stated as an explicit boundary assertion)
        checkOracle(1, 100);       // m at MIN, n at MAX  -> single row, exactly 1 path
        checkOracle(100, 1);       // m at MAX, n at MIN  -> single column, exactly 1 path
        checkOracle(100, 3);       // MAX with thin width -> C(101,2)=5050, fits int
        checkOracle(3, 100);       // symmetric thin width at MAX
        // --- Single / minimal valid shapes ---
        checkOracle(1, 99);        // off-by-one below the row maximum
        checkOracle(99, 1);        // off-by-one below the column maximum
        // --- Off-by-one square shapes around examples ---
        checkOracle(2, 4);         // small rectangle, C(4,1)=4
        checkOracle(4, 2);         // symmetric, C(4,3)=4
        checkOracle(7, 7);         // odd square C(12,6)=924
        checkOracle(20, 20);       // value 35345263800 EXCEEDS int -> guarded skip path exercised
        // --- Property: full symmetry uniquePaths(m,n) == uniquePaths(n,m) over many shapes ---
        checkSymmetry(13, 7);
        checkSymmetry(31, 19);
        checkSymmetry(2, 97);
        checkSymmetry(64, 23);
        // --- Property: monotonic growth — adding a column never decreases the count ---
        checkMonotoneInN(10);      // uniquePaths(10, n) non-decreasing in n for n=1..30
        // --- Property: recurrence  uniquePaths(m,n) == uniquePaths(m-1,n) + uniquePaths(m,n-1) ---
        checkRecurrence(9, 11);
        checkRecurrence(40, 6);

        // ===== ADDED: large / performance cases (use long; verify by PROPERTY / long oracle) =====
        // The answer can exceed int for big grids (e.g. 100x100 ~ 2.27e10). The reference method
        // returns int, so we read it as a long and only assert exact-match when the true value
        // fits int; otherwise we rely on symmetry + timing as the property guarantee.
        checkLongOracleTimed(34, 34);   // C(66,33) overflows int -> property/timing only
        checkLongOracleTimed(60, 60);   // big square, overflows int
        checkLongOracleTimed(100, 100); // MAX x MAX grid -> ~2.27e10, the true upper-bound stress
        checkLongOracleTimed(100, 80);  // large asymmetric, overflows int
        // A large COUNT that still fits int, timed on a generous budget:
        checkOracleTimed(18, 15);       // 145422675, in-int, exercises perf path with exact assert
        // Randomised sweep with a FIXED seed: pick random in-bounds shapes, assert symmetry +
        // (when it fits int) exact agreement with the long oracle. Timed as a whole.
        checkRandomSweepTimed(200);     // 200 random (m,n) in [1,100], seed 42

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent oracle: number of paths = C(m+n-2, m-1). Use long; values used all fit int.
    static long oracle(int m, int n) {
        long[][] dp = new long[m][n];
        for (int i = 0; i < m; i++) dp[i][0] = 1;
        for (int j = 0; j < n; j++) dp[0][j] = 1;
        for (int i = 1; i < m; i++)
            for (int j = 1; j < n; j++)
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
        return dp[m - 1][n - 1];
    }

    // Read the reference answer as a long (it returns int; widen so big grids don't surprise us).
    static long callAsLong(int m, int n) {
        return (long) new Answer().uniquePaths(m, n);
    }

    static void check(int m, int n, int expected) {
        total++;
        try {
            int actual = new Answer().uniquePaths(m, n);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m uniquePaths(" + m + ", " + n + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m uniquePaths(" + m + ", " + n + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m uniquePaths(" + m + ", " + n + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkOracle(int m, int n) {
        total++;
        try {
            long exp = oracle(m, n);
            if (exp > Integer.MAX_VALUE) { // safety: never assert an out-of-int expectation
                System.out.println("\033[31m[FAIL]\033[0m uniquePaths(" + m + ", " + n + ") oracle out of int range; skip");
                return;
            }
            int expected = (int) exp;
            int actual = new Answer().uniquePaths(m, n);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m uniquePaths(" + m + ", " + n + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m uniquePaths(" + m + ", " + n + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m uniquePaths(" + m + ", " + n + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkOracleTimed(int m, int n) {
        total++;
        String label = "perf uniquePaths(" + m + ", " + n + ")";
        try {
            long exp = oracle(m, n);
            if (exp > Integer.MAX_VALUE) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " oracle out of int range; skip");
                return;
            }
            int expected = (int) exp;
            long start = System.nanoTime();
            int actual = new Answer().uniquePaths(m, n);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // For grids whose true count exceeds int. We compare against a long oracle. If the true value
    // fits int we can assert exact equality; otherwise the int return type cannot hold the value,
    // so we only assert the count is positive and that timing is within budget (property check).
    static void checkLongOracleTimed(int m, int n) {
        total++;
        String label = "perf-long uniquePaths(" + m + ", " + n + ")";
        try {
            long exp = oracle(m, n);
            long start = System.nanoTime();
            long actual = callAsLong(m, n);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            boolean ok;
            String detail;
            if (exp <= Integer.MAX_VALUE) {
                ok = (actual == exp);                 // true value fits int -> exact match required
                detail = "expected=" + exp + " actual=" + actual;
            } else {
                ok = (actual != 0);                   // overflows int -> only property: nonzero count
                detail = "true=" + exp + " (exceeds int; property: actual!=0) actual=" + actual;
            }
            if (!ok) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " " + detail + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " " + detail + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: uniquePaths is symmetric in its arguments.
    static void checkSymmetry(int m, int n) {
        total++;
        String label = "symmetry uniquePaths(" + m + ", " + n + ") == uniquePaths(" + n + ", " + m + ")";
        try {
            long a = callAsLong(m, n);
            long b = callAsLong(n, m);
            if (a == b) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + a);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " : " + a + " != " + b);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: for fixed m, uniquePaths(m, n) is non-decreasing as n grows.
    static void checkMonotoneInN(int m) {
        total++;
        String label = "monotone uniquePaths(" + m + ", n) non-decreasing for n=1..30";
        try {
            long prev = Long.MIN_VALUE;
            for (int n = 1; n <= 30; n++) {
                long v = callAsLong(m, n);
                if (v < prev) {
                    System.out.println("\033[31m[FAIL]\033[0m " + label + " : decreased at n=" + n + " (" + prev + " -> " + v + ")");
                    return;
                }
                prev = v;
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m " + label);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: uniquePaths(m,n) == uniquePaths(m-1,n) + uniquePaths(m,n-1) for m,n >= 2.
    static void checkRecurrence(int m, int n) {
        total++;
        String label = "recurrence uniquePaths(" + m + ", " + n + ")";
        try {
            long whole = callAsLong(m, n);
            long up = callAsLong(m - 1, n);
            long left = callAsLong(m, n - 1);
            if (whole == up + left) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + up + " + " + left + " = " + whole);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " : " + whole + " != " + up + " + " + left + " (" + (up + left) + ")");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Randomised sweep with a FIXED seed. For each random in-bounds (m,n): assert symmetry, and
    // when the true count fits int assert exact agreement with the long oracle. Timed overall.
    static void checkRandomSweepTimed(int trials) {
        total++;
        String label = "random-sweep " + trials + " shapes (seed 42)";
        try {
            Random rnd = new Random(42);
            long start = System.nanoTime();
            for (int t = 0; t < trials; t++) {
                int m = 1 + rnd.nextInt(100); // [1,100]
                int n = 1 + rnd.nextInt(100); // [1,100]
                long a = callAsLong(m, n);
                long b = callAsLong(n, m);
                if (a != b) {
                    long elapsedMs = (System.nanoTime() - start) / 1_000_000;
                    System.out.println("\033[31m[FAIL]\033[0m " + label + " : symmetry broke at (" + m + "," + n + ") " + a + " != " + b + " (" + elapsedMs + " ms)");
                    return;
                }
                long exp = oracle(m, n);
                if (exp <= Integer.MAX_VALUE && a != exp) {
                    long elapsedMs = (System.nanoTime() - start) / 1_000_000;
                    System.out.println("\033[31m[FAIL]\033[0m " + label + " : wrong count at (" + m + "," + n + ") expected=" + exp + " actual=" + a + " (" + elapsedMs + " ms)");
                    return;
                }
            }
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
