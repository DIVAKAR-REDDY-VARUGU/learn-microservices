import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Example cases from QUESTION.md =====
        check(new int[]{5, 7, 7, 8, 8, 10}, 8, new int[]{3, 4});
        check(new int[]{5, 7, 7, 8, 8, 10}, 6, new int[]{-1, -1});
        check(new int[]{}, 0, new int[]{-1, -1});

        // Empty array, various targets
        check(new int[]{}, -5, new int[]{-1, -1});
        check(new int[]{}, 1000000000, new int[]{-1, -1});

        // Single element
        check(new int[]{5}, 5, new int[]{0, 0});
        check(new int[]{5}, 4, new int[]{-1, -1});
        check(new int[]{5}, 6, new int[]{-1, -1});
        check(new int[]{0}, 0, new int[]{0, 0});

        // All equal elements -> whole range
        check(new int[]{2, 2, 2, 2, 2}, 2, new int[]{0, 4});
        check(new int[]{2, 2, 2, 2, 2}, 3, new int[]{-1, -1});
        check(new int[]{7, 7}, 7, new int[]{0, 1});

        // Target at the very start (single occurrence)
        check(new int[]{1, 2, 3, 4, 5}, 1, new int[]{0, 0});
        // Target at the very end (single occurrence)
        check(new int[]{1, 2, 3, 4, 5}, 5, new int[]{4, 4});
        // Target in the middle (single occurrence)
        check(new int[]{1, 2, 3, 4, 5}, 3, new int[]{2, 2});

        // Duplicates at the start
        check(new int[]{8, 8, 8, 9, 10}, 8, new int[]{0, 2});
        // Duplicates at the end
        check(new int[]{1, 2, 9, 9, 9}, 9, new int[]{2, 4});

        // Not found below min and above max
        check(new int[]{5, 7, 7, 8, 8, 10}, 4, new int[]{-1, -1});
        check(new int[]{5, 7, 7, 8, 8, 10}, 11, new int[]{-1, -1});
        // Not found in a gap between values
        check(new int[]{1, 1, 4, 4, 9}, 5, new int[]{-1, -1});

        // Negatives and zeros
        check(new int[]{-5, -5, -3, 0, 0, 0, 2}, 0, new int[]{3, 5});
        check(new int[]{-5, -5, -3, 0, 0, 0, 2}, -5, new int[]{0, 1});
        check(new int[]{-5, -5, -3, 0, 0, 0, 2}, 2, new int[]{6, 6});
        check(new int[]{-5, -5, -3, 0, 0, 0, 2}, -1, new int[]{-1, -1});

        // Large-ish bound values
        check(new int[]{-1000000000, -1000000000, 1000000000}, -1000000000, new int[]{0, 1});
        check(new int[]{-1000000000, -1000000000, 1000000000}, 1000000000, new int[]{2, 2});

        // Larger array with a long run of the target in the middle
        int[] arr = new int[200];
        for (int i = 0; i < 50; i++) arr[i] = 1;
        for (int i = 50; i < 150; i++) arr[i] = 2;   // target run [50,149]
        for (int i = 150; i < 200; i++) arr[i] = 3;
        check(arr, 2, new int[]{50, 149});
        check(arr, 1, new int[]{0, 49});
        check(arr, 3, new int[]{150, 199});
        check(arr, 4, new int[]{-1, -1});

        // ===== NEW CORNER CASES =====
        // Two-element single occurrence at each end
        check(new int[]{1, 2}, 1, new int[]{0, 0});
        check(new int[]{1, 2}, 2, new int[]{1, 1});
        check(new int[]{1, 2}, 3, new int[]{-1, -1});

        // Entire array is the target (all-equal, larger)
        int[] allSame = new int[101];
        Arrays.fill(allSame, 9);
        check(allSame, 9, new int[]{0, 100});
        check(allSame, 8, new int[]{-1, -1});
        check(allSame, 10, new int[]{-1, -1});

        // Target run exactly at index 0 in a larger array
        int[] frontRun = new int[60];
        for (int i = 0; i < 30; i++) frontRun[i] = -7;
        for (int i = 30; i < 60; i++) frontRun[i] = 4;
        check(frontRun, -7, new int[]{0, 29});
        check(frontRun, 4, new int[]{30, 59});

        // Target run exactly at the last index
        int[] backRun = new int[60];
        for (int i = 0; i < 59; i++) backRun[i] = 0;
        backRun[59] = 5;
        check(backRun, 5, new int[]{59, 59});
        check(backRun, 0, new int[]{0, 58});

        // Constraint min/max bound values present and repeated
        check(new int[]{-1000000000, -1000000000, -1000000000, 0, 1000000000}, -1000000000, new int[]{0, 2});
        check(new int[]{-1000000000, 0, 1000000000, 1000000000}, 1000000000, new int[]{2, 3});
        check(new int[]{-1000000000, 0, 1000000000}, 0, new int[]{1, 1});

        // Strictly increasing (every value unique) -> singleton ranges, plus not-found gaps
        int[] strict = {-3, -1, 0, 2, 4, 6};
        check(strict, -3, new int[]{0, 0});
        check(strict, 6, new int[]{5, 5});
        check(strict, 1, new int[]{-1, -1});   // gap
        check(strict, 3, new int[]{-1, -1});   // gap

        // Heavy duplicates with a unique sentinel in the middle
        check(new int[]{0, 0, 0, 0, 1, 2, 2, 2, 2}, 1, new int[]{4, 4});
        check(new int[]{0, 0, 0, 0, 1, 2, 2, 2, 2}, 0, new int[]{0, 3});
        check(new int[]{0, 0, 0, 0, 1, 2, 2, 2, 2}, 2, new int[]{5, 8});

        // Target between two distinct duplicate blocks (off-by-one not-found)
        check(new int[]{1, 1, 1, 5, 5, 5}, 3, new int[]{-1, -1});
        check(new int[]{1, 1, 1, 5, 5, 5}, 1, new int[]{0, 2});
        check(new int[]{1, 1, 1, 5, 5, 5}, 5, new int[]{3, 5});

        // ===== LARGE / PERFORMANCE CASES =====
        Random rnd = new Random(42);

        // Build a large non-decreasing array of size N near 10^5 with controlled runs,
        // then verify searchRange by an independent O(n) oracle for many random targets.
        int N = 100000;
        int[] huge = new int[N];
        // non-decreasing: each value increases by 0 or 1 (lots of duplicates / runs)
        huge[0] = -50000;
        for (int i = 1; i < N; i++) huge[i] = huge[i - 1] + (rnd.nextInt(3) == 0 ? 1 : 0);

        long t0 = System.nanoTime();
        boolean ok = true;
        try {
            for (int q = 0; q < 2000; q++) {
                int target = huge[rnd.nextInt(N)];      // present target (exercises long runs)
                int[] got = new Answer().searchRange(huge, target);
                int[] exp = oracleRange(huge, target);
                if (!Arrays.equals(got, exp)) { ok = false; break; }
            }
            // also probe definitely-absent targets
            for (int q = 0; q < 500 && ok; q++) {
                int target = 1000000 + rnd.nextInt(1000); // above max
                int[] got = new Answer().searchRange(huge, target);
                if (!Arrays.equals(got, new int[]{-1, -1})) { ok = false; break; }
            }
        } catch (Throwable t) { ok = false; }
        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
        propertyCase("LARGE oracle-match n=" + N + ", 2500 queries (" + elapsedMs + " ms)",
                ok && elapsedMs <= 3000);

        // Single giant run of the same value: range must be [0, N-1]. O(log n) check.
        int M = 100000;
        int[] giantRun = new int[M];
        Arrays.fill(giantRun, 42);
        long t1 = System.nanoTime();
        boolean runOk;
        try {
            int[] got = new Answer().searchRange(giantRun, 42);
            runOk = Arrays.equals(got, new int[]{0, M - 1});
            // absent in a giant uniform array
            runOk = runOk && Arrays.equals(new Answer().searchRange(giantRun, 41), new int[]{-1, -1});
            runOk = runOk && Arrays.equals(new Answer().searchRange(giantRun, 43), new int[]{-1, -1});
        } catch (Throwable t) { runOk = false; }
        long elapsedMs1 = (System.nanoTime() - t1) / 1_000_000;
        propertyCase("LARGE giant-uniform-run n=" + M + " -> [0," + (M - 1) + "] (" + elapsedMs1 + " ms)",
                runOk && elapsedMs1 <= 3000);

        // Strictly increasing large array (all unique) -> every present target is a singleton.
        int P = 100000;
        int[] uniq = new int[P];
        for (int i = 0; i < P; i++) uniq[i] = i - 50000;
        long t2 = System.nanoTime();
        boolean uniqOk = true;
        try {
            for (int q = 0; q < 3000; q++) {
                int idx = rnd.nextInt(P);
                int[] got = new Answer().searchRange(uniq, uniq[idx]);
                if (!Arrays.equals(got, new int[]{idx, idx})) { uniqOk = false; break; }
            }
        } catch (Throwable t) { uniqOk = false; }
        long elapsedMs2 = (System.nanoTime() - t2) / 1_000_000;
        propertyCase("LARGE unique-singletons n=" + P + ", 3000 queries (" + elapsedMs2 + " ms)",
                uniqOk && elapsedMs2 <= 3000);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent O(n) oracle for the first/last occurrence.
    static int[] oracleRange(int[] nums, int target) {
        int first = -1, last = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                if (first == -1) first = i;
                last = i;
            }
        }
        return new int[]{first, last};
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

    static void check(int[] nums, int target, int[] expected) {
        total++;
        try {
            int[] actual = new Answer().searchRange(nums, target);
            if (Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m searchRange(" + brief(nums) + ", " + target + ") = " + Arrays.toString(actual));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m searchRange(" + brief(nums) + ", " + target + ") expected=" + Arrays.toString(expected) + " actual=" + Arrays.toString(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m searchRange(" + brief(nums) + ", " + target + ") expected=" + Arrays.toString(expected) + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
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
