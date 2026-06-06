import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        // numTrees(n) = nth Catalan number
        check(1, 1);    // example
        check(3, 5);    // example
        check(4, 14);   // example
        check(2, 2);
        check(5, 42);
        check(6, 132);
        check(7, 429);
        check(8, 1430);
        check(9, 4862);
        check(10, 16796);
        check(15, 9694845);
        check(19, 1767263190); // max bound (fits in int)

        // ===== New corner cases (full sequence coverage) =====
        check(11, 58786);
        check(12, 208012);
        check(13, 742900);
        check(14, 2674440);
        check(16, 35357670);
        check(17, 129644790);
        check(18, 477638700);   // off-by-one below max
        // re-affirm both boundaries explicitly
        check(1, 1);            // min bound
        check(19, 1767263190);  // max bound again

        // ===== Property / oracle checks =====
        // Verify EVERY n in [1..19] against an independent Catalan DP oracle.
        checkRangeOracle();
        // Verify the Catalan recurrence G(n) = sum_{i=1..n} G(i-1)*G(n-i) holds via the method itself.
        checkRecurrence();

        // ===== Performance: hammer the max bound many times (flags exponential recursion) =====
        checkPerfRepeated(19, 1767263190, 100000);
        checkPerfRepeated(15, 9694845, 100000);

        // ====================================================================
        // ===== ADDED CORNER CASES (boundaries & exhaustive coverage) ========
        // ====================================================================

        // Constraint min boundary (n = 1) and the smallest non-trivial value.
        check(1, 1);            // min bound (explicit corner)
        check(2, 2);            // smallest n where two distinct shapes exist

        // Constraint max boundary (n = 19) and the off-by-one neighbour.
        check(19, 1767263190);  // max bound (largest Catalan that still fits int)
        check(18, 477638700);   // one below max

        // Re-verify the three QUESTION.md examples as explicit corners.
        check(3, 5);
        check(1, 1);
        check(4, 14);

        // Independent closed-form oracle: C(n) = binomial(2n, n) / (n + 1).
        // This is an algebraically distinct derivation from the convolution DP,
        // catching DP-recurrence-specific bugs.
        checkClosedFormAll();

        // Property: every count is strictly positive and strictly increasing in n
        // (Catalan numbers grow monotonically for n >= 0).
        checkStrictlyIncreasing();

        // Property: ratio C(n)/C(n-1) = (2(2n-1))/(n+1) for the Catalan sequence.
        // Verifies internal structure without hard-coding values.
        checkRatioIdentity();

        // Property: no negative or zero results anywhere in the valid domain.
        checkAllPositive();

        // Determinism: repeated calls with the same n return the same value
        // (guards against stateful / mutated shared-array bugs).
        checkDeterministic(13);
        checkDeterministic(19);

        // Independence: interleaving different n on the SAME Answer instance must
        // not corrupt results (guards against cached/leaked DP state).
        checkInterleavedInstance();

        // Spot-check a few mid-range values picked pseudo-randomly with a FIXED
        // seed, validated against the closed-form oracle.
        checkRandomSpotChecks();

        // ====================================================================
        // ===== ADDED PERFORMANCE CASES ======================================
        // ====================================================================

        // Large repetition counts across the whole domain: an exponential or
        // un-memoised solution blows the 3000 ms budget here.
        checkPerfFullDomain(200000);
        checkPerfRepeated(19, 1767263190, 500000);
        checkPerfRepeated(10, 16796, 1000000);
        checkPerfRepeated(1, 1, 2000000);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent oracle: nth Catalan number via the standard convolution DP.
    static int oracle(int n) {
        long[] g = new long[n + 1];
        g[0] = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) g[i] += g[j - 1] * g[i - j];
        }
        return (int) g[n];
    }

    // Algebraically distinct oracle: C(n) = binomial(2n, n) / (n + 1),
    // computed multiplicatively to avoid overflow within the valid domain.
    static long closedFormCatalan(int n) {
        long c = 1; // C(0) = 1
        for (int i = 0; i < n; i++) {
            c = c * (2L * n - i) / (i + 1);
        }
        return c / (n + 1);
    }

    static void check(int n, int expected) {
        total++;
        try {
            int actual = new Answer().numTrees(n);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m numTrees(" + n + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m numTrees(" + n + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m numTrees(" + n + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkRangeOracle() {
        total++;
        try {
            for (int n = 1; n <= 19; n++) {
                int expected = oracle(n);
                int actual = new Answer().numTrees(n);
                if (actual != expected) {
                    System.out.println("\033[31m[FAIL]\033[0m range-oracle: numTrees(" + n + ") expected=" + expected + " actual=" + actual);
                    return;
                }
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m range-oracle: all n in [1..19] match Catalan oracle");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m range-oracle: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkRecurrence() {
        total++;
        try {
            // G(0) = 1 by definition. Verify G(n) == sum_{i=1..n} G(i-1)*G(n-i) for n in [2..19],
            // using the method's own outputs as the building blocks.
            long[] g = new long[20];
            g[0] = 1;
            for (int n = 1; n <= 19; n++) g[n] = new Answer().numTrees(n);
            for (int n = 2; n <= 19; n++) {
                long sum = 0;
                for (int i = 1; i <= n; i++) sum += g[i - 1] * g[n - i];
                if (sum != g[n]) {
                    System.out.println("\033[31m[FAIL]\033[0m recurrence: broke at n=" + n + " got=" + g[n] + " sum=" + sum);
                    return;
                }
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m recurrence: G(n) = sum G(i-1)*G(n-i) holds for n in [2..19]");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m recurrence: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkPerfRepeated(int n, int expected, int reps) {
        total++;
        String label = "perf numTrees(" + n + ")x" + reps;
        try {
            long start = System.nanoTime();
            Answer a = new Answer();
            int actual = expected;
            for (int i = 0; i < reps; i++) actual = a.numTrees(n);
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

    // ===== Added helpers =====

    // Cross-check every valid n against the algebraically independent
    // closed-form binomial oracle.
    static void checkClosedFormAll() {
        total++;
        try {
            for (int n = 1; n <= 19; n++) {
                long expected = closedFormCatalan(n);
                int actual = new Answer().numTrees(n);
                if (actual != expected) {
                    System.out.println("\033[31m[FAIL]\033[0m closed-form: numTrees(" + n + ") expected=" + expected + " actual=" + actual);
                    return;
                }
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m closed-form: all n in [1..19] match binomial(2n,n)/(n+1)");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m closed-form: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: the sequence is strictly increasing across the valid domain.
    static void checkStrictlyIncreasing() {
        total++;
        try {
            int prev = new Answer().numTrees(1);
            for (int n = 2; n <= 19; n++) {
                int cur = new Answer().numTrees(n);
                if (cur <= prev) {
                    System.out.println("\033[31m[FAIL]\033[0m increasing: numTrees(" + n + ")=" + cur + " not > numTrees(" + (n - 1) + ")=" + prev);
                    return;
                }
                prev = cur;
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m increasing: numTrees strictly increases over n in [1..19]");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m increasing: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: C(n) * (n + 1) == C(n - 1) * 2 * (2n - 1) for n in [2..19].
    // Multiplied form avoids fractions; uses only the method's own outputs.
    static void checkRatioIdentity() {
        total++;
        try {
            for (int n = 2; n <= 19; n++) {
                long cn = new Answer().numTrees(n);
                long cprev = new Answer().numTrees(n - 1);
                long lhs = cn * (n + 1);
                long rhs = cprev * 2L * (2L * n - 1);
                if (lhs != rhs) {
                    System.out.println("\033[31m[FAIL]\033[0m ratio-identity: broke at n=" + n + " lhs=" + lhs + " rhs=" + rhs);
                    return;
                }
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m ratio-identity: C(n)*(n+1) == C(n-1)*2*(2n-1) for n in [2..19]");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m ratio-identity: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: every result in the valid domain is strictly positive.
    static void checkAllPositive() {
        total++;
        try {
            for (int n = 1; n <= 19; n++) {
                int v = new Answer().numTrees(n);
                if (v <= 0) {
                    System.out.println("\033[31m[FAIL]\033[0m positivity: numTrees(" + n + ")=" + v + " is not positive");
                    return;
                }
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m positivity: numTrees(n) > 0 for all n in [1..19]");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m positivity: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Determinism: many repeated calls for the same n must return identical results.
    static void checkDeterministic(int n) {
        total++;
        try {
            Answer a = new Answer();
            int first = a.numTrees(n);
            for (int i = 0; i < 1000; i++) {
                int v = a.numTrees(n);
                if (v != first) {
                    System.out.println("\033[31m[FAIL]\033[0m deterministic(" + n + "): drifted to " + v + " (first=" + first + ")");
                    return;
                }
            }
            long expected = closedFormCatalan(n);
            if (first != expected) {
                System.out.println("\033[31m[FAIL]\033[0m deterministic(" + n + "): value " + first + " != oracle " + expected);
                return;
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m deterministic(" + n + "): stable at " + first);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m deterministic(" + n + "): threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independence: interleaving different n on one instance must not leak DP state.
    static void checkInterleavedInstance() {
        total++;
        try {
            Answer a = new Answer();
            int[] order = {19, 1, 18, 2, 17, 3, 10, 5, 19, 15, 4, 19};
            for (int n : order) {
                int v = a.numTrees(n);
                long expected = closedFormCatalan(n);
                if (v != expected) {
                    System.out.println("\033[31m[FAIL]\033[0m interleaved: numTrees(" + n + ")=" + v + " expected=" + expected);
                    return;
                }
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m interleaved: shared instance returns correct values in mixed order");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m interleaved: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Pseudo-random spot checks with a FIXED seed, validated by the closed-form oracle.
    static void checkRandomSpotChecks() {
        total++;
        try {
            Random rnd = new java.util.Random(42);
            Answer a = new Answer();
            for (int t = 0; t < 200; t++) {
                int n = 1 + rnd.nextInt(19); // n in [1..19]
                int actual = a.numTrees(n);
                long expected = closedFormCatalan(n);
                if (actual != expected) {
                    System.out.println("\033[31m[FAIL]\033[0m random-spot: numTrees(" + n + ")=" + actual + " expected=" + expected);
                    return;
                }
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m random-spot: 200 seeded(42) checks match oracle");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m random-spot: threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Performance: sweep the entire valid domain many times under one budget.
    static void checkPerfFullDomain(int reps) {
        total++;
        String label = "perf full-domain[1..19]x" + reps;
        try {
            long start = System.nanoTime();
            Answer a = new Answer();
            long checksum = 0;
            for (int i = 0; i < reps; i++) {
                for (int n = 1; n <= 19; n++) checksum += a.numTrees(n);
            }
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            // Expected checksum: reps * sum_{n=1..19} C(n).
            long oneSweep = 0;
            for (int n = 1; n <= 19; n++) oneSweep += closedFormCatalan(n);
            long expected = oneSweep * reps;

            if (checksum != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " checksum=" + checksum + " expected=" + expected + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " ok (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
