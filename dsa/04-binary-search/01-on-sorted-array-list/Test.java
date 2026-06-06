import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Example cases from QUESTION.md =====
        check(new int[]{-1, 0, 3, 5, 9, 12}, 9, 4);   // target present, middle-ish
        check(new int[]{-1, 0, 3, 5, 9, 12}, 2, -1);  // target absent
        check(new int[]{5}, 5, 0);                    // single element present

        // Single-element corner cases
        check(new int[]{5}, 1, -1);                   // single element, not found
        check(new int[]{5}, 6, -1);                   // single element, target greater
        check(new int[]{0}, 0, 0);                    // single zero

        // Boundaries: first and last element
        check(new int[]{-1, 0, 3, 5, 9, 12}, -1, 0);  // first element
        check(new int[]{-1, 0, 3, 5, 9, 12}, 12, 5);  // last element

        // Not found below min and above max
        check(new int[]{-1, 0, 3, 5, 9, 12}, -100, -1);
        check(new int[]{-1, 0, 3, 5, 9, 12}, 100, -1);

        // Two elements
        check(new int[]{1, 3}, 1, 0);
        check(new int[]{1, 3}, 3, 1);
        check(new int[]{1, 3}, 2, -1);

        // Negatives only
        check(new int[]{-9, -7, -5, -3, -1}, -5, 2);
        check(new int[]{-9, -7, -5, -3, -1}, -8, -1);
        check(new int[]{-9, -7, -5, -3, -1}, -9, 0);
        check(new int[]{-9, -7, -5, -3, -1}, -1, 4);

        // Min/max bound-ish values (within -10^4..10^4)
        check(new int[]{-9999, 0, 9999}, -9999, 0);
        check(new int[]{-9999, 0, 9999}, 9999, 2);
        check(new int[]{-9999, 0, 9999}, 0, 1);
        check(new int[]{-9999, 0, 9999}, 5000, -1);

        // Larger sorted array - every element findable
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = i * 2 - 500; // unique, ascending
        for (int i = 0; i < big.length; i++) check(big, big[i], i);
        check(big, big[0] - 1, -1);            // just below min
        check(big, big[big.length - 1] + 1, -1); // just above max
        check(big, 1, -1);                      // odd value not present (all even-ish offsets)

        // Even-length array, target between two consecutive values -> not found
        check(new int[]{2, 4, 6, 8}, 5, -1);
        check(new int[]{2, 4, 6, 8}, 8, 3);
        check(new int[]{2, 4, 6, 8}, 2, 0);

        // ===== NEW CORNER CASES =====
        // Strictly increasing run, even length, every interior gap not found
        check(new int[]{0, 2, 4, 6, 8, 10}, 7, -1);   // off-by-one between consecutive
        check(new int[]{0, 2, 4, 6, 8, 10}, 9, -1);   // off-by-one near end

        // Constraint boundary values (-10^4 < v < 10^4 => extremes -9999, 9999)
        check(new int[]{-9999, -1, 0, 1, 9999}, -9999, 0);  // min legal value at start
        check(new int[]{-9999, -1, 0, 1, 9999}, 9999, 4);   // max legal value at end
        check(new int[]{-9999, -1, 0, 1, 9999}, -9998, -1); // just inside min, absent
        check(new int[]{-9999, -1, 0, 1, 9999}, 9998, -1);  // just inside max, absent

        // Two-element with negatives
        check(new int[]{-5000, 5000}, -5000, 0);
        check(new int[]{-5000, 5000}, 5000, 1);
        check(new int[]{-5000, 5000}, 0, -1);

        // All-negative ascending
        check(new int[]{-100, -50, -25, -12, -6}, -25, 2);
        check(new int[]{-100, -50, -25, -12, -6}, -7, -1);

        // Element just left/right of every position in a small array (off-by-one shapes)
        int[] small = {10, 20, 30, 40, 50};
        check(small, 15, -1);
        check(small, 25, -1);
        check(small, 35, -1);
        check(small, 45, -1);
        check(small, 9, -1);

        // Three consecutive integers, found at each index
        check(new int[]{7, 8, 9}, 7, 0);
        check(new int[]{7, 8, 9}, 8, 1);
        check(new int[]{7, 8, 9}, 9, 2);

        // ===== LARGE / PERFORMANCE CASES =====
        // Build a large unique ascending array near the constraint range and probe every element.
        // n = 9999 keeps all values strictly within (-10^4, 10^4) and unique.
        Random rnd = new Random(42);
        int N = 9999;
        int[] huge = new int[N];
        // span values from -4999 to 4999 mapped uniquely & ascending
        for (int i = 0; i < N; i++) huge[i] = i - 4999; // -4999 .. 4999, unique ascending

        long t0 = System.nanoTime();
        boolean allFound = true;
        for (int i = 0; i < N; i++) {
            int idx;
            try {
                idx = new Answer().search(huge, huge[i]);
            } catch (Throwable t) {
                allFound = false;
                break;
            }
            if (idx != i) { allFound = false; break; }
        }
        // also probe absent values: below min and above max
        boolean absentOk = true;
        try {
            absentOk = new Answer().search(huge, -100000) == -1
                    && new Answer().search(huge, 100000) == -1;
        } catch (Throwable t) { absentOk = false; }
        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
        propertyCase("LARGE all-elements-findable n=" + N + " (" + elapsedMs + " ms)",
                allFound && absentOk && elapsedMs <= 3000);

        // Random absent-target probes on the same huge array using a fixed seed.
        long t1 = System.nanoTime();
        boolean randomOk = true;
        try {
            for (int q = 0; q < 5000; q++) {
                int target = rnd.nextInt(20000) - 10000; // -10000 .. 9999
                int idx = new Answer().search(huge, target);
                int oracle = oracleIndex(huge, target);
                if (idx != oracle) { randomOk = false; break; }
            }
        } catch (Throwable t) { randomOk = false; }
        long elapsedMs1 = (System.nanoTime() - t1) / 1_000_000;
        propertyCase("LARGE random-query oracle-match q=5000 (" + elapsedMs1 + " ms)",
                randomOk && elapsedMs1 <= 3000);

        // Big array, single deep probe (worst-case path) timed.
        long t2 = System.nanoTime();
        boolean deepOk = true;
        try {
            deepOk = new Answer().search(huge, huge[N - 1]) == N - 1
                  && new Answer().search(huge, huge[0]) == 0;
        } catch (Throwable t) { deepOk = false; }
        long elapsedMs2 = (System.nanoTime() - t2) / 1_000_000;
        propertyCase("LARGE endpoint-probe n=" + N + " (" + elapsedMs2 + " ms)",
                deepOk && elapsedMs2 <= 3000);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent O(n) oracle: linear scan for the unique target's index, else -1.
    static int oracleIndex(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) if (nums[i] == target) return i;
        return -1;
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

    static void check(int[] nums, int target, int expected) {
        total++;
        try {
            int actual = new Answer().search(nums, target);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m search(" + brief(nums) + ", " + target + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m search(" + brief(nums) + ", " + target + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m search(" + brief(nums) + ", " + target + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Keep output readable for large arrays
    static String brief(int[] a) {
        if (a.length <= 12) return Arrays.toString(a);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 6; i++) sb.append(a[i]).append(", ");
        sb.append("... len=").append(a.length).append("]");
        return sb.toString();
    }
}
