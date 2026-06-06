import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Example cases from QUESTION.md =====
        check(new int[]{3, 4, 5, 1, 2}, 1);
        check(new int[]{4, 5, 6, 7, 0, 1, 2}, 0);
        check(new int[]{11, 13, 15, 17}, 11); // rotated n times == not visibly rotated

        // Single element
        check(new int[]{1}, 1);
        check(new int[]{-5000}, -5000);
        check(new int[]{5000}, 5000);

        // Two elements, both rotation states
        check(new int[]{1, 2}, 1);   // not rotated (or rotated 2x)
        check(new int[]{2, 1}, 1);   // rotated once

        // Not visibly rotated (min at index 0)
        check(new int[]{1, 2, 3, 4, 5}, 1);
        check(new int[]{-5000, -100, 0, 100, 5000}, -5000);

        // Min at the very end (rotated by n-1)
        check(new int[]{2, 3, 4, 5, 1}, 1);
        check(new int[]{5, 1, 2, 3, 4}, 1); // min just after the pivot at index 1

        // Negatives and zero crossing
        check(new int[]{0, 1, 2, -2, -1}, -2);
        check(new int[]{-3, -2, -1, 0, 1, 2}, -3); // sorted, not rotated
        check(new int[]{1, 2, -3, -2, -1, 0}, -3);

        // Larger arrays - rotate a base ascending array by every offset and verify min
        int[] base = new int[200];
        for (int i = 0; i < base.length; i++) base[i] = i - 50; // unique ascending, min = -50
        for (int r = 1; r <= base.length; r++) {
            int[] rotated = rotateLeft(base, r % base.length);
            check(rotated, -50);
        }

        // Bound values present
        check(new int[]{0, 5000, -5000, -1}, -5000);
        check(new int[]{-4999, -5000}, -5000); // rotated once with extreme bound

        // ===== NEW CORNER CASES =====
        // Min at index 1 (rotated by n-1 the other way) for several sizes
        check(new int[]{6, 1, 2, 3, 4, 5}, 1);
        check(new int[]{100, -50, -40, -30, -20, -10}, -50);

        // Pivot exactly in the middle
        check(new int[]{4, 5, 6, 1, 2, 3}, 1);
        check(new int[]{50, 60, 70, 80, 10, 20, 30, 40}, 10);

        // Constraint bound extremes (-5000..5000) as the global minimum
        check(new int[]{5000, -5000}, -5000);               // rotated once, both extremes
        check(new int[]{-5000, 5000}, -5000);               // not rotated, min at front
        check(new int[]{0, 1, 2, 3, -5000, -4999}, -5000);  // extreme min after pivot
        check(new int[]{-4998, -4997, -5000, -4999}, -5000);// pivot lands on extreme

        // Two-element edge: each ordering
        check(new int[]{-5000, 5000}, -5000);
        check(new int[]{5000, -5000}, -5000);

        // Three elements, all three meaningful rotations
        check(new int[]{1, 2, 3}, 1);   // not rotated
        check(new int[]{3, 1, 2}, 1);   // rotated 2x left -> min at idx1
        check(new int[]{2, 3, 1}, 1);   // rotated 1x left -> min at end

        // Strictly decreasing-looking but actually a rotation of ascending pairs
        check(new int[]{8, 9, 1, 2, 3, 4, 5, 6, 7}, 1);
        check(new int[]{7, 8, 9, 1, 2, 3, 4, 5, 6}, 1);

        // Min near the front but not at index 0
        check(new int[]{90, 100, 5, 10, 20, 30}, 5);

        // ===== LARGE / PERFORMANCE CASES =====
        Random rnd = new Random(42);

        // Build a large unique ascending base near the constraint upper bound (n up to 5000),
        // rotate by random offsets, and verify the returned min equals the known minimum.
        // O(log n) algorithm should be effectively instant even for thousands of rotations.
        int N = 5000;
        int[] bigBase = new int[N];
        for (int i = 0; i < N; i++) bigBase[i] = i - 2500; // unique ascending, min = -2500, in [-2500,2499] (within +-5000)
        int knownMin = bigBase[0];

        long t0 = System.nanoTime();
        boolean ok0 = true;
        try {
            for (int q = 0; q < 5000; q++) {
                int r = 1 + rnd.nextInt(N); // rotation between 1 and n
                int[] rot = rotateLeft(bigBase, r % N);
                int got = new Answer().findMin(rot);
                if (got != knownMin) { ok0 = false; break; }
            }
        } catch (Throwable t) { ok0 = false; }
        long e0 = (System.nanoTime() - t0) / 1_000_000;
        propertyCase("LARGE n=" + N + ", 5000 random rotations min=" + knownMin + " (" + e0 + " ms)",
                ok0 && e0 <= 3000);

        // Single maximum-size array, min compared against an independent O(n) scan oracle.
        long t1 = System.nanoTime();
        boolean ok1 = true;
        try {
            for (int q = 0; q < 1000; q++) {
                int r = 1 + rnd.nextInt(N);
                int[] rot = rotateLeft(bigBase, r % N);
                int got = new Answer().findMin(rot);
                int oracle = scanMin(rot);
                if (got != oracle) { ok1 = false; break; }
            }
        } catch (Throwable t) { ok1 = false; }
        long e1 = (System.nanoTime() - t1) / 1_000_000;
        propertyCase("LARGE n=" + N + ", oracle-match 1000 rotations (" + e1 + " ms)",
                ok1 && e1 <= 3000);

        // Worst-case shapes at full size: rotated by exactly 1 and by n-1.
        long t2 = System.nanoTime();
        boolean ok2;
        try {
            int[] rot1 = rotateLeft(bigBase, 1);       // min moves to index n-1
            int[] rotNm1 = rotateLeft(bigBase, N - 1);  // min moves to index 1
            ok2 = new Answer().findMin(rot1) == knownMin
               && new Answer().findMin(rotNm1) == knownMin
               && new Answer().findMin(bigBase) == knownMin; // not rotated visibly
        } catch (Throwable t) { ok2 = false; }
        long e2 = (System.nanoTime() - t2) / 1_000_000;
        propertyCase("LARGE n=" + N + " edge rotations {1, n-1, none} (" + e2 + " ms)",
                ok2 && e2 <= 3000);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static int[] rotateLeft(int[] a, int k) {
        int n = a.length;
        k %= n;
        int[] r = new int[n];
        for (int i = 0; i < n; i++) r[i] = a[(i + k) % n];
        return r;
    }

    // Independent O(n) oracle.
    static int scanMin(int[] a) {
        int m = a[0];
        for (int x : a) if (x < m) m = x;
        return m;
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

    static void check(int[] nums, int expected) {
        total++;
        try {
            int actual = new Answer().findMin(nums);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m findMin(" + brief(nums) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m findMin(" + brief(nums) + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m findMin(" + brief(nums) + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
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
