import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (preserved; expected values corrected where the
        //       original placeholders/comments were wrong, verified by oracle) =====
        check(new int[]{3,1,5,8}, 167);  // example
        check(new int[]{1,5}, 10);        // example
        check(new int[]{7}, 7);           // example: single balloon -> 1*7*1
        check(new int[]{1}, 1);           // single, value 1
        check(new int[]{0}, 0);           // single zero
        check(new int[]{100}, 100);       // single max value
        check(new int[]{1,1}, 2);         // 1*1*1 + 1*1*1
        check(new int[]{0,0}, 0);         // all zeros
        check(new int[]{2,3,4}, 36);      // small triple
        check(new int[]{5,5}, 30);        // corrected: best order yields 30
        check(new int[]{3,1,5,8,2}, 192); // corrected from 167 (oracle-verified)
        check(new int[]{9,76,64,21}, 116718); // larger
        check(new int[]{8,3,4,3,5,7,9,8}, 1748); // corrected placeholder 0 -> 1748

        // ===== (a) MORE CORNER CASES =====
        // constraint value max (nums[i] up to 100) boundaries
        check(new int[]{100,100}, 10100);            // two max balloons
        check(new int[]{100,100,100}, 1010100);      // three max balloons
        check(new int[]{0,100,0}, 100);              // max surrounded by zeros
        // all-equal
        check(new int[]{2,2,2,2,2}, 30);             // all equal non-trivial
        check(new int[]{7,7,7,7,7,7,7}, 1771);       // heavy duplicates, all equal
        // strictly increasing / decreasing
        check(new int[]{1,2,3,4,5}, 110);            // strictly increasing
        check(new int[]{5,4,3,2,1}, 110);            // strictly decreasing (symmetric)
        // alternating
        check(new int[]{1,100,1,100,1}, 20300);      // low/high alternating
        check(new int[]{100,1,100,1,100}, 1030100);  // high/low alternating
        // zeros mixed in
        check(new int[]{0,1,0}, 1);                  // zeros flanking a one
        check(new int[]{1,0,1}, 2);                  // zero in the middle
        check(new int[]{0,0,0,0}, 0);                // all zeros, n=4
        // off-by-one / small symmetric shapes
        check(new int[]{1,2,1}, 6);                  // palindrome triple
        // single max-value-and-boundary
        check(new int[]{100}, 100);                  // n==1 with value max
        // small all-ones (only product-of-ones available)
        check(new int[]{1,1,1,1,1,1,1,1,1,1}, 10);   // all ones, n=10
        // mixed medium
        check(new int[]{9,76,64,21,97,30}, 844156);  // mixed larger values
        check(new int[]{3,1,5,8,9,2,6}, 909);        // mixed n=7

        // ===== (b) LARGE / PERFORMANCE cases =====
        // Constraint upper bound is n <= 300, values 0..100. We build inputs at the
        // top of that bound and VERIFY by an independent O(n^3) interval-DP oracle,
        // timing the Answer call and failing only on a generous 3000ms budget.
        checkLarge("random n=300 (seed 42)", randomArray(300, 101, 42));
        checkLarge("random n=299 (seed 42)", randomArray(299, 101, 42)); // odd-length shape
        checkLarge("all 100 n=300 (max coins / overflow guard)", filled(300, 100));
        checkLarge("all 1 n=300 (degenerate products)", filled(300, 1));
        checkLarge("all 0 n=300 (zero everywhere)", filled(300, 0));
        checkLarge("random small values 0..2 n=300 (seed 42)", randomArray(300, 3, 42));

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // ---------- exact-match check ----------
    static void check(int[] nums, int expected) {
        total++;
        try {
            int actual = new Answer().maxCoins(nums.clone());
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m maxCoins(" + brief(nums) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m maxCoins(" + brief(nums) + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m maxCoins(" + brief(nums) + ") expected=" + expected
                    + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---------- large/perf check: oracle-verified + timed ----------
    static void checkLarge(String label, int[] nums) {
        total++;
        try {
            int expected = oracle(nums.clone());
            long t0 = System.nanoTime();
            int actual = new Answer().maxCoins(nums.clone());
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String tag = label + " [n=" + nums.length + ", " + elapsedMs + " ms]";
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + tag + " expected=" + expected + " actual=" + actual);
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + tag + " value OK (" + actual + ") but too slow (>3000 ms)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + tag + " = " + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " [n=" + nums.length + "] threw "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---------- independent O(n^3) interval-DP oracle ----------
    static int oracle(int[] nums) {
        int n = nums.length;
        int[] a = new int[n + 2];
        a[0] = 1;
        a[n + 1] = 1;
        for (int i = 0; i < n; i++) a[i + 1] = nums[i];
        int[][] dp = new int[n + 2][n + 2];
        for (int len = 1; len <= n; len++) {
            for (int left = 1; left + len - 1 <= n; left++) {
                int right = left + len - 1;
                int best = 0;
                for (int k = left; k <= right; k++) {
                    int coins = a[left - 1] * a[k] * a[right + 1] + dp[left][k - 1] + dp[k + 1][right];
                    if (coins > best) best = coins;
                }
                dp[left][right] = best;
            }
        }
        return dp[1][n];
    }

    // ---------- helpers ----------
    static int[] randomArray(int n, int bound, long seed) {
        Random r = new Random(seed);
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = r.nextInt(bound); // 0 .. bound-1
        return arr;
    }

    static int[] filled(int n, int value) {
        int[] arr = new int[n];
        Arrays.fill(arr, value);
        return arr;
    }

    // compact label so huge arrays don't flood the output
    static String brief(int[] nums) {
        if (nums.length <= 16) return Arrays.toString(nums);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 8; i++) sb.append(nums[i]).append(",");
        sb.append("...,");
        for (int i = nums.length - 4; i < nums.length; i++) {
            sb.append(nums[i]);
            if (i < nums.length - 1) sb.append(",");
        }
        sb.append("] (len=").append(nums.length).append(")");
        return sb.toString();
    }
}
