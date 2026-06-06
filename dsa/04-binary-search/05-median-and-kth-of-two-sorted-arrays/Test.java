import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;
    static final double EPS = 1e-6;

    public static void main(String[] args) {
        // ===== Example cases from QUESTION.md =====
        check(new int[]{1, 3}, new int[]{2}, 2.0);
        check(new int[]{1, 2}, new int[]{3, 4}, 2.5);
        check(new int[]{0, 0}, new int[]{0, 0}, 0.0);

        // One array empty (other has odd length -> single middle)
        check(new int[]{}, new int[]{1}, 1.0);
        check(new int[]{1}, new int[]{}, 1.0);
        // One array empty (other has even length -> avg of two middles)
        check(new int[]{}, new int[]{1, 2}, 1.5);
        check(new int[]{2, 3}, new int[]{}, 2.5);
        // One array empty, longer
        check(new int[]{}, new int[]{1, 2, 3, 4, 5}, 3.0);
        check(new int[]{}, new int[]{1, 2, 3, 4, 5, 6}, 3.5);

        // Single element each
        check(new int[]{1}, new int[]{2}, 1.5);
        check(new int[]{2}, new int[]{1}, 1.5);
        check(new int[]{5}, new int[]{5}, 5.0);

        // All equal across both arrays
        check(new int[]{2, 2, 2}, new int[]{2, 2}, 2.0);
        check(new int[]{7, 7}, new int[]{7, 7}, 7.0);

        // Disjoint ranges (one entirely below the other)
        check(new int[]{1, 2, 3}, new int[]{4, 5, 6}, 3.5);
        check(new int[]{4, 5, 6}, new int[]{1, 2, 3}, 3.5);
        // Interleaved
        check(new int[]{1, 3, 5, 7}, new int[]{2, 4, 6, 8}, 4.5);

        // Negatives and zeros
        check(new int[]{-5, -3, -1}, new int[]{-2, 0}, -2.0);
        check(new int[]{-10, -5}, new int[]{-3, 0, 5}, -3.0);
        check(new int[]{-1, 0, 1}, new int[]{0}, 0.0);

        // Duplicates spanning both
        check(new int[]{1, 1, 1, 1}, new int[]{1, 1, 1}, 1.0);
        check(new int[]{1, 2, 2}, new int[]{2, 2, 3}, 2.0);

        // Uneven sizes
        check(new int[]{1, 2, 3, 4, 5}, new int[]{6}, 3.5);
        check(new int[]{6}, new int[]{1, 2, 3, 4, 5}, 3.5);
        check(new int[]{1, 3, 8, 9, 15}, new int[]{7, 11, 18, 19, 21, 25}, 11.0);
        check(new int[]{23, 26, 31, 35}, new int[]{3, 5, 7, 9, 11, 16}, 13.5);

        // Large bound values
        check(new int[]{-1000000, 0}, new int[]{0, 1000000}, 0.0);
        check(new int[]{-1000000}, new int[]{1000000}, 0.0);

        // Larger arrays validated against a brute-force merge median
        Random rnd = new Random(42);
        for (int t = 0; t < 30; t++) {
            int m = rnd.nextInt(40);          // 0..39
            int n = rnd.nextInt(40);          // 0..39
            if (m + n == 0) n = 1;             // ensure m+n >= 1
            int[] a = randomSorted(rnd, m);
            int[] b = randomSorted(rnd, n);
            check(a, b, bruteMedian(a, b));
        }

        // ===== NEW CORNER CASES =====
        // Constraint extremes for values (-10^6 .. 10^6)
        check(new int[]{-1000000}, new int[]{1000000}, 0.0);
        check(new int[]{-1000000, -1000000}, new int[]{1000000, 1000000}, 0.0);
        check(new int[]{-1000000, 0, 1000000}, new int[]{0}, 0.0);
        check(new int[]{-1000000}, new int[]{-1000000}, -1000000.0); // both at min
        check(new int[]{1000000}, new int[]{1000000}, 1000000.0);    // both at max

        // m+n == 1 (smallest legal combined length), in both orientations
        check(new int[]{42}, new int[]{}, 42.0);
        check(new int[]{}, new int[]{-7}, -7.0);

        // One array entirely below the other, odd combined length
        check(new int[]{1, 2, 3, 4, 5}, new int[]{100, 200}, 3.0);
        check(new int[]{100, 200}, new int[]{1, 2, 3, 4, 5}, 3.0);

        // Heavy duplicates, even combined length
        check(new int[]{5, 5, 5, 5}, new int[]{5, 5}, 5.0);
        check(new int[]{0, 0, 0}, new int[]{0, 0, 0}, 0.0);

        // Strictly increasing across both, interleaved at the boundary
        check(new int[]{1, 2, 3, 10}, new int[]{4, 5, 6, 7}, 4.5);
        check(new int[]{2, 4, 6, 8, 10}, new int[]{1, 3, 5, 7, 9}, 5.5);

        // All-negative combined
        check(new int[]{-9, -7, -5}, new int[]{-8, -6, -4}, -6.5);
        check(new int[]{-3, -2, -1}, new int[]{-100}, -2.5);

        // Very lopsided: tiny array vs medium array, both parities
        check(new int[]{50}, new int[]{1, 2, 3, 4, 5, 6, 7}, 4.5);   // combined len 8 (even)
        check(new int[]{50}, new int[]{1, 2, 3, 4, 5, 6}, 4.0);      // combined len 7 (odd)

        // Median sits across the array boundary (classic partition stress)
        check(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, new int[]{9, 10, 11, 12, 13, 14, 15, 16}, 8.5);
        check(new int[]{9, 10, 11, 12, 13, 14, 15, 16}, new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 8.5);

        // ===== LARGE / PERFORMANCE CASES =====
        // Build large arrays near the constraint upper bound (m+n up to 2000).
        // Validate by an independent O((m+n) log(m+n)) merge-sort oracle.
        // O(log(m+n)) algorithm is trivially fast; a quadratic mistake still passes the
        // generous time budget, so correctness via oracle is the real guard here.
        Random big = new Random(42);

        // Two max-size halves (1000 + 1000 = 2000).
        int[] A = randomSorted(big, 1000);
        int[] B = randomSorted(big, 1000);
        timedOracleCase("LARGE m=1000 n=1000", A, B, big);

        // Max combined length with one empty array (0 + 2000).
        int[] C = new int[2000];
        for (int i = 0; i < C.length; i++) C[i] = big.nextInt(2_000_001) - 1_000_000;
        Arrays.sort(C);
        timedOracleCase("LARGE m=0 n=2000", new int[]{}, C, big);

        // Lopsided max: 1 + 1999.
        int[] D = randomSorted(big, 1);
        int[] E = randomSorted(big, 1999);
        timedOracleCase("LARGE m=1 n=1999", D, E, big);

        // Many random instances of varying size up to the bound, all oracle-checked, timed once.
        long t0 = System.nanoTime();
        boolean ok = true;
        try {
            for (int it = 0; it < 300; it++) {
                int m = big.nextInt(1001);     // 0..1000
                int n = big.nextInt(1001);     // 0..1000
                if (m + n == 0) { m = 1; }      // ensure m+n >= 1
                if (m + n > 2000) { n = 2000 - m; } // respect combined bound
                int[] a = randomSorted(big, m);
                int[] b = randomSorted(big, n);
                double got = new Answer().findMedianSortedArrays(a, b);
                double exp = bruteMedian(a, b);
                if (Math.abs(got - exp) > EPS) { ok = false; break; }
            }
        } catch (Throwable t) { ok = false; }
        long e0 = (System.nanoTime() - t0) / 1_000_000;
        propertyCase("LARGE 300 random instances oracle-match (" + e0 + " ms)", ok && e0 <= 3000);

        // Heavy-duplicate large case: both arrays mostly the same value (partition edge stress).
        int[] dupA = new int[1000];
        int[] dupB = new int[1000];
        Arrays.fill(dupA, 0);
        Arrays.fill(dupB, 0);
        for (int i = 900; i < 1000; i++) { dupA[i] = 1; dupB[i] = 1; } // tail of 1s, still sorted
        timedOracleCase("LARGE heavy-duplicates m=1000 n=1000", dupA, dupB, big);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Times a single large call and checks it against the merge oracle on a generous budget.
    static void timedOracleCase(String label, int[] a, int[] b, Random rnd) {
        total++;
        try {
            long t = System.nanoTime();
            double got = new Answer().findMedianSortedArrays(a, b);
            long ms = (System.nanoTime() - t) / 1_000_000;
            double exp = bruteMedian(a, b);
            boolean correct = Math.abs(got - exp) <= EPS;
            if (correct && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " median=" + got + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + exp + " actual=" + got + " (" + ms + " ms)");
            }
        } catch (Throwable th) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + th.getClass().getSimpleName() + ": " + th.getMessage());
        }
    }

    static int[] randomSorted(Random rnd, int len) {
        int[] a = new int[len];
        for (int i = 0; i < len; i++) a[i] = rnd.nextInt(2001) - 1000; // -1000..1000
        Arrays.sort(a);
        return a;
    }

    // Independent oracle: merge + sort, then take the median directly.
    static double bruteMedian(int[] a, int[] b) {
        int[] merged = new int[a.length + b.length];
        System.arraycopy(a, 0, merged, 0, a.length);
        System.arraycopy(b, 0, merged, a.length, b.length);
        Arrays.sort(merged);
        int n = merged.length;
        if (n % 2 == 1) return merged[n / 2];
        return (merged[n / 2 - 1] + merged[n / 2]) / 2.0;
    }

    static void propertyCase(String label, boolean ok) {
        total++;
        if (ok) {
            pass++;
            System.out.println("\033[32m[PASS]\033[0m " + label);
        } else {
            System.out.println("\033[31m[FAIL]\033[0m " + label);
        }
    }

    static void check(int[] nums1, int[] nums2, double expected) {
        total++;
        try {
            double actual = new Answer().findMedianSortedArrays(nums1, nums2);
            if (Math.abs(actual - expected) <= EPS) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m findMedianSortedArrays(" + brief(nums1) + ", " + brief(nums2) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m findMedianSortedArrays(" + brief(nums1) + ", " + brief(nums2) + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m findMedianSortedArrays(" + brief(nums1) + ", " + brief(nums2) + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static String brief(int[] a) {
        if (a.length <= 10) return Arrays.toString(a);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 6; i++) sb.append(a[i]).append(", ");
        sb.append("... len=").append(a.length).append("]");
        return sb.toString();
    }
}
