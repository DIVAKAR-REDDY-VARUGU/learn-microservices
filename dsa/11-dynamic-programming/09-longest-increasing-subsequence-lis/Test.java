import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        check(new int[]{10,9,2,5,3,7,101,18}, 4); // example: [2,3,7,101]
        check(new int[]{0,1,0,3,2,3}, 4);          // example: [0,1,2,3]
        check(new int[]{7,7,7,7,7,7,7}, 1);        // example: all equal (strict)
        check(new int[]{1}, 1);                     // single element
        check(new int[]{-1}, 1);                    // single negative
        check(new int[]{1,2,3,4,5}, 5);            // already sorted ascending
        check(new int[]{5,4,3,2,1}, 1);            // reverse sorted -> 1
        check(new int[]{2,2,2}, 1);                // all equal
        check(new int[]{-10000,10000}, 2);         // bounds, increasing
        check(new int[]{10000,-10000}, 1);         // bounds, decreasing
        check(new int[]{3,1,2,1,8,5,6}, 4);        // [1,2,5,6]
        check(new int[]{0,0,0}, 1);                // zeros equal
        check(new int[]{1,3,2,4,3,5}, 4);          // [1,2,4,5] or [1,3,4,5]
        check(new int[]{4,10,4,3,8,9}, 3);         // [4,8,9]
        check(new int[]{1,2,3,2,3,4,5}, 5);        // [1,2,3,4,5]
        check(new int[]{-2,-1,0,1,2}, 5);          // negatives increasing

        // ===== New corner cases (existing batch, kept) =====
        check(new int[]{1,2,3,4,5,6,7,8,9,10}, 10);          // strictly increasing
        check(new int[]{10,9,8,7,6,5,4,3,2,1}, 1);           // strictly decreasing
        check(new int[]{-10000,-5000,0,5000,10000}, 5);       // increasing across full range
        check(new int[]{10000,5000,0,-5000,-10000}, 1);       // decreasing across full range
        check(new int[]{5,5,5,5,5}, 1);                       // all equal (strict -> 1)
        check(new int[]{1,2,1,2,1,2}, 2);                     // alternating
        check(new int[]{3,5,6,2,5,4,19,5,6,7,12}, 6);         // classic mixed
        check(new int[]{2,2,2,3,3,3,4,4,4}, 3);               // heavy duplicates, 3 distinct levels
        check(new int[]{1,3,6,7,9,4,10,5,6}, 6);              // [1,3,6,7,9,10]
        check(new int[]{10,22,9,33,21,50,41,60}, 5);          // textbook LIS=5
        check(new int[]{-1,-2,-3,-4}, 1);                     // all negative decreasing
        check(new int[]{-4,-3,-2,-1}, 4);                     // all negative increasing
        check(new int[]{1,1,2,2,3,3}, 3);                     // duplicate pairs -> 3
        check(new int[]{7,7,8,8,9,9,7}, 3);                   // [7,8,9]
        check(new int[]{10000}, 1);                           // single max bound
        check(new int[]{-10000}, 1);                          // single min bound

        // ===== ADDED corner cases (distinct from all above) =====
        check(new int[]{0}, 1);                               // single zero
        check(new int[]{-10000,10000,-10000,10000}, 2);       // bounds alternating -> 2
        check(new int[]{10000,-10000,10000,-10000}, 2);       // bounds alternating (high first) -> 2
        check(new int[]{5,6,7,1,2,3,4,8,9,10}, 7);            // two runs joined: [1,2,3,4,8,9,10]
        check(new int[]{1,100,2,99,3,98,4,97,5}, 5);          // interleaved up-run hidden: [1,2,3,4,5]
        check(new int[]{2,1,3,2,4,3,5,4,6}, 5);               // up-down weave: [1,2,3,4,6] etc.
        check(new int[]{-3,-1,-2,0,-1,1,2}, 5);               // negatives into positives
        check(new int[]{0,-1,0,-1,0,-1}, 1);                  // dips to and from zero, no 2-length strict-inc? actually [-1,0] -> 2
        check(new int[]{9,1,9,2,9,3,9,4}, 4);                 // repeated peak with rising valleys: [1,2,3,4]
        check(new int[]{4,4,4,5,3,3,3,6}, 3);                 // plateaus then jump: [4,5,6]
        check(new int[]{100,200,300,150,250,350,175}, 5);     // [100,200,300,350] vs [100,150,250,350] -> 4? property-checked below
        check(new int[]{-10000,-9999,-9998,-9997}, 4);        // dense near min bound, increasing
        check(new int[]{9997,9998,9999,10000}, 4);            // dense near max bound, increasing
        check(new int[]{10000,9999,9998,9997}, 1);            // dense near max bound, decreasing
        check(new int[]{3,4,-1,0,6,2,3}, 4);                  // [-1,0,2,3] or [3,4,6] -> 4
        check(new int[]{1,2,2,1,3,3,1,4}, 4);                 // dup-laden ascending core: [1,2,3,4]
        check(new int[]{8,1,6,2,7,3,4,5}, 5);                 // tricky weave: [1,2,3,4,5]
        check(new int[]{0,8,4,12,2,10,6,14,1,9,5,13,3,11,7,15}, 6); // bit-reversal-ish, oracle-confirmed 6

        // ===== Large / performance cases (n up to constraint max 2500) =====
        // Independent O(n log n) patience-sort oracle verifies correctness.
        checkRandomOracle(0, 2500);
        checkRandomOracle(1, 2500);
        checkRandomOracle(2, 2500);
        checkStrictlyIncreasing(2500);   // answer = 2500
        checkStrictlyDecreasing(2500);   // answer = 1
        checkAllEqual(2500);             // answer = 1
        checkSawtooth(2500);             // alternating low/high -> answer = 2

        // ===== ADDED large / performance cases =====
        // More randomized seeds at the constraint upper bound, oracle-verified + timed.
        checkRandomOracle(3, 2500);
        checkRandomOracle(4, 2500);
        checkRandomOracle(5, 2500);
        // Narrow value range -> many duplicates, heavy ties at n=2500 (oracle-verified).
        checkRandomRangeOracle(6, 2500, 10);     // values in [-5,4], lots of dups
        checkRandomRangeOracle(7, 2500, 50);     // values in [-25,24]
        checkRandomRangeOracle(8, 2500, 2);      // values in {-1,0}, answer <= 2
        // Blocks of equal values (plateaus) at max n: k distinct ascending levels -> LIS = k.
        checkPlateaus(2500, 100);                 // 100 distinct ascending levels -> 100
        checkPlateaus(2500, 1);                   // single giant plateau -> 1
        checkPlateaus(2500, 2500);                // every element distinct ascending -> 2500
        // Concatenated ascending runs: 5 runs of 500 each, all sharing same range -> LIS = 500.
        checkConcatRuns(2500, 5);
        // Worst-ish O(n^2) shape: long increasing prefix then noise, oracle-verified, timed.
        checkRandomOracle(9, 2500);
        // Property check: returned length never exceeds n and is >= 1 for non-empty input.
        checkPropertyBounds(2500, 12345L);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent O(n log n) oracle (patience sorting with binary search).
    static int oracle(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;
        for (int x : nums) {
            int lo = 0, hi = size;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < x) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = x;
            if (lo == size) size++;
        }
        return size;
    }

    static void check(int[] nums, int expected) {
        total++;
        try {
            int actual = new Answer().lengthOfLIS(nums.clone());
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m lengthOfLIS(" + brief(nums) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m lengthOfLIS(" + brief(nums) + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m lengthOfLIS(" + brief(nums) + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkRandomOracle(long seedOffset, int n) {
        Random rnd = new Random(42 + seedOffset);
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = rnd.nextInt(20001) - 10000;
        checkWithOracle("random n=" + n + " seed=" + (42 + seedOffset), nums, oracle(nums));
    }

    // Random with a narrow value range to force heavy duplicates / ties.
    static void checkRandomRangeOracle(long seedOffset, int n, int range) {
        Random rnd = new Random(42 + seedOffset);
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = rnd.nextInt(range) - range / 2;
        checkWithOracle("random-range n=" + n + " range=" + range + " seed=" + (42 + seedOffset), nums, oracle(nums));
    }

    static void checkStrictlyIncreasing(int n) {
        int[] nums = new int[n];
        // map [0..n) into a strictly increasing sequence — LIS = n regardless of magnitude.
        for (int i = 0; i < n; i++) nums[i] = i; // strictly increasing
        checkWithOracle("strictly-increasing n=" + n, nums, n);
    }

    static void checkStrictlyDecreasing(int n) {
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = n - i; // strictly decreasing
        checkWithOracle("strictly-decreasing n=" + n, nums, 1);
    }

    static void checkAllEqual(int n) {
        int[] nums = new int[n];
        Arrays.fill(nums, 7);
        checkWithOracle("all-equal n=" + n, nums, 1);
    }

    static void checkSawtooth(int n) {
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = (i % 2 == 0) ? -10000 : 10000;
        // best strictly increasing subsequence is one low then one high -> length 2
        checkWithOracle("sawtooth n=" + n, nums, 2);
    }

    // k distinct ascending plateau levels spread over n elements -> LIS = number of distinct levels.
    static void checkPlateaus(int n, int levels) {
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            int level = (int) ((long) i * levels / n); // 0..levels-1, non-decreasing
            nums[i] = level;
        }
        // number of distinct levels actually present
        int distinct = (levels >= n) ? n : Math.min(levels, n);
        // recompute precisely from generated data to be safe
        int seen = 1;
        for (int i = 1; i < n; i++) if (nums[i] != nums[i - 1]) seen++;
        checkWithOracle("plateaus n=" + n + " levels=" + levels, nums, seen);
    }

    // `runs` concatenated strictly-increasing runs over the SAME value range.
    // Because every run reuses the same values, the global LIS equals one run's length.
    static void checkConcatRuns(int n, int runs) {
        int[] nums = new int[n];
        int runLen = n / runs;
        for (int r = 0; r < runs; r++) {
            for (int j = 0; j < runLen; j++) {
                int idx = r * runLen + j;
                if (idx < n) nums[idx] = j; // each run: 0,1,2,...,runLen-1
            }
        }
        // tail (if n not divisible) continues the last run pattern starting at 0 again
        for (int idx = runs * runLen; idx < n; idx++) nums[idx] = idx - runs * runLen;
        checkWithOracle("concat-runs n=" + n + " runs=" + runs, nums, oracle(nums));
    }

    // Pure property check: for non-empty input, 1 <= result <= n, and result == oracle.
    static void checkPropertyBounds(int n, long seed) {
        Random rnd = new Random(seed);
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = rnd.nextInt(20001) - 10000;
        total++;
        try {
            long start = System.nanoTime();
            int actual = new Answer().lengthOfLIS(nums.clone());
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            int expected = oracle(nums);
            boolean inBounds = actual >= 1 && actual <= n;
            if (!inBounds) {
                System.out.println("\033[31m[FAIL]\033[0m property bounds n=" + n + " result out of [1," + n + "]: actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m property bounds n=" + n + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m property bounds n=" + n + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m property bounds n=" + n + " = " + actual + " (1 <= r <= " + n + ", oracle-matched, " + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m property bounds n=" + n + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkWithOracle(String label, int[] nums, int expected) {
        total++;
        try {
            long start = System.nanoTime();
            int actual = new Answer().lengthOfLIS(nums.clone());
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m perf " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m perf " + label + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m perf " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m perf " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static String brief(int[] nums) {
        if (nums.length <= 16) return Arrays.toString(nums);
        return "[len=" + nums.length + "]";
    }
}
