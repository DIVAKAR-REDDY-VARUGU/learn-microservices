import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        // n, expected (Fibonacci-style: ways(n) = ways(n-1) + ways(n-2))
        check(1, 1);   // single step, only one way
        check(2, 2);   // 1+1, 2  (example)
        check(3, 3);   // 1+1+1, 1+2, 2+1 (example)
        check(4, 5);
        check(5, 8);   // example
        check(6, 13);
        check(7, 21);
        check(10, 89);
        check(20, 10946);
        check(45, 1836311903); // max bound (fits in int)

        // ===== New corner cases (kept) =====
        check(8, 34);
        check(9, 55);
        check(11, 144);
        check(12, 233);
        check(13, 377);
        check(15, 987);
        check(25, 121393);
        check(30, 1346269);
        check(40, 165580141);
        check(44, 1134903170);     // off-by-one below max
        check(43, 701408733);      // off-by-one below max
        check(35, 14930352);

        // ===== Additional corner cases (NEW) =====
        // Minimum constraint boundary n=1 retested as explicit boundary, plus n=2 (smallest with branching).
        check(14, 610);             // fills small-range gap
        check(16, 1597);            // fills small-range gap
        check(17, 2584);            // fills small-range gap
        check(18, 4181);            // fills small-range gap
        check(19, 6765);            // fills small-range gap
        check(21, 17711);           // mid-range
        check(31, 2178309);         // mid-range
        check(42, 433494437);       // off-by-one shape near the max bound
        check(41, 267914296);       // off-by-one shape near the max bound
        check(33, 5702887);
        check(34, 9227465);         // adjacent pair: check(33)+check(35) note recurrence neighbour
        check(36, 24157817);
        check(37, 39088169);

        // ===== Property checks (NEW) =====
        // Boundary property: every n in [1..45] must be strictly increasing and positive.
        checkMonotonicPositive();
        // Idempotence/statelessness: repeated calls with the same n give the same answer.
        checkDeterministic();

        // ===== Property / consistency check across the whole range =====
        // Build expected via an independent O(n) oracle and verify EVERY n in [1..45].
        // Also confirm the Fibonacci recurrence holds: f(n) == f(n-1) + f(n-2).
        checkRangeOracle();

        // ===== Performance =====
        // Constraint cap is small (n<=45) so the "large input" is the CALL COUNT, not n.
        // Hammer the max bound and randomized n in [1..45] with a FIXED seed; time with nanoTime.
        checkPerfRepeated(45, 1836311903, 200000);
        checkPerfRandomHammer(100000);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent oracle: ways(n) is Fibonacci with ways(1)=1, ways(2)=2.
    static int oracle(int n) {
        if (n <= 0) return 1;
        long a = 1, b = 1; // a=ways(0)=1, b=ways(1)=1
        for (int i = 2; i <= n; i++) {
            long c = a + b;
            a = b;
            b = c;
        }
        return (int) b;
    }

    static void check(int n, int expected) {
        total++;
        try {
            int actual = new Answer().climbStairs(n);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m climbStairs(" + n + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m climbStairs(" + n + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m climbStairs(" + n + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkRangeOracle() {
        total++;
        try {
            for (int n = 1; n <= 45; n++) {
                int expected = oracle(n);
                int actual = new Answer().climbStairs(n);
                if (actual != expected) {
                    System.out.println("\033[31m[FAIL]\033[0m range-oracle: climbStairs(" + n + ") expected=" + expected + " actual=" + actual);
                    return;
                }
                // recurrence: for n>=3, ways(n) == ways(n-1)+ways(n-2)
                if (n >= 3) {
                    int p1 = new Answer().climbStairs(n - 1);
                    int p2 = new Answer().climbStairs(n - 2);
                    if (actual != p1 + p2) {
                        System.out.println("\033[31m[FAIL]\033[0m range-oracle: recurrence broke at n=" + n
                                + " got=" + actual + " but f(n-1)+f(n-2)=" + (p1 + p2));
                        return;
                    }
                }
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m range-oracle: all n in [1..45] match Fibonacci oracle and recurrence");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m range-oracle: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: results are strictly increasing and strictly positive over the whole valid domain.
    static void checkMonotonicPositive() {
        total++;
        try {
            int prev = Integer.MIN_VALUE;
            for (int n = 1; n <= 45; n++) {
                int v = new Answer().climbStairs(n);
                if (v <= 0) {
                    System.out.println("\033[31m[FAIL]\033[0m monotonic: climbStairs(" + n + ")=" + v + " is not positive");
                    return;
                }
                if (v <= prev) {
                    System.out.println("\033[31m[FAIL]\033[0m monotonic: climbStairs(" + n + ")=" + v + " not > previous=" + prev);
                    return;
                }
                prev = v;
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m monotonic: ways(n) strictly increasing and positive over [1..45]");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m monotonic: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: the function is deterministic/stateless — same n repeated yields the same value.
    static void checkDeterministic() {
        total++;
        try {
            Random rng = new java.util.Random(42);
            for (int t = 0; t < 50; t++) {
                int n = 1 + rng.nextInt(45); // n in [1..45]
                int first = new Answer().climbStairs(n);
                int again = new Answer().climbStairs(n);
                int expected = oracle(n);
                if (first != again || first != expected) {
                    System.out.println("\033[31m[FAIL]\033[0m deterministic: climbStairs(" + n + ") first=" + first
                            + " again=" + again + " oracle=" + expected);
                    return;
                }
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m deterministic: repeated calls match oracle for 50 random n (seed=42)");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m deterministic: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkPerfRepeated(int n, int expected, int reps) {
        total++;
        try {
            long start = System.nanoTime();
            Answer a = new Answer();
            int actual = expected;
            for (int i = 0; i < reps; i++) {
                actual = a.climbStairs(n);
            }
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m perf climbStairs(" + n + ")x" + reps + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m perf climbStairs(" + n + ")x" + reps + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m perf climbStairs(" + n + ")x" + reps + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m perf climbStairs(" + n + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Performance: many randomized calls (fixed seed) across the full domain, verified by oracle PROPERTY.
    static void checkPerfRandomHammer(int reps) {
        total++;
        try {
            Random rng = new java.util.Random(42);
            int[] ns = new int[reps];
            int[] exp = new int[reps];
            for (int i = 0; i < reps; i++) {
                int n = 1 + rng.nextInt(45); // n in [1..45]
                ns[i] = n;
                exp[i] = oracle(n);
            }
            Answer a = new Answer();
            long start = System.nanoTime();
            int mismatchIdx = -1;
            int mismatchVal = 0;
            for (int i = 0; i < reps; i++) {
                int got = a.climbStairs(ns[i]);
                if (got != exp[i]) {
                    mismatchIdx = i;
                    mismatchVal = got;
                    break;
                }
            }
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (mismatchIdx >= 0) {
                System.out.println("\033[31m[FAIL]\033[0m perf-random x" + reps + " mismatch at n=" + ns[mismatchIdx]
                        + " expected=" + exp[mismatchIdx] + " actual=" + mismatchVal + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m perf-random x" + reps + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m perf-random x" + reps + " random n in [1..45] (seed=42) all match oracle (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m perf-random: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
