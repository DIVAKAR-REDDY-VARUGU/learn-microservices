import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        check(new int[]{1,5,11,5}, true);    // example: [1,5,5] & [11]
        check(new int[]{1,2,3,5}, false);    // example: cannot partition
        check(new int[]{2,2,2,2}, true);     // example: [2,2] & [2,2]
        check(new int[]{1}, false);          // single element -> odd total
        check(new int[]{2}, false);          // single element, total=2 cannot split into two non-empty
        check(new int[]{1,1}, true);         // two equal
        check(new int[]{3,3}, true);         // two equal odd values, total even
        check(new int[]{1,2,5}, false);      // total 8, half 4 not formable
        check(new int[]{1,1,1,1}, true);     // all equal
        check(new int[]{100,100}, true);     // max value bound
        check(new int[]{1,2,3,4,5,6,7}, true); // total 28, half 14 formable
        check(new int[]{2,2,3,5}, false);    // total 12, half 6 not formable
        check(new int[]{1,5,3}, false);      // total 9 odd
        check(new int[]{3,1,1,2,2,1}, true); // total 10, half 5
        check(new int[]{14,9,8,4,3,2}, true); // total 40, half 20 (14+4+2)

        // ===== New corner cases =====
        check(new int[]{100}, false);                 // single max element -> odd? total 100 even but cannot split single
        check(new int[]{1,1,1}, false);               // odd total
        check(new int[]{2,2,2}, false);               // total 6, half 3 not formable from 2s
        check(new int[]{3,3,3,3}, true);              // total 12, half 6 = 3+3
        check(new int[]{1,2,3,4,5,6,7,8,9,10}, false);// total 55 odd
        check(new int[]{1,2,3,4,5,6,7,8,9,11}, true); // total 56, half 28 formable
        check(new int[]{100,100,100,100}, true);      // all max, total 400, half 200
        check(new int[]{100,99}, false);              // total 199 odd
        check(new int[]{1,1,1,1,2,2,2,2}, true);      // total 12, half 6
        check(new int[]{5,5,5,5,5,5}, true);          // total 30, half 15 = 5+5+5
        check(new int[]{5,5,5}, false);               // total 15 odd
        check(new int[]{23,13,11,7,6,5,5}, true);     // total 70, half 35
        check(new int[]{1,2,5,10}, false);            // total 18, half 9 -> needs 9: not formable (1+2=3,1+... no)
        check(new int[]{2,4,6,8,10}, false);          // total 30, half 15 unreachable from all-even elements
        check(new int[]{1,1,1,1,1,1}, true);          // total 6, half 3
        check(new int[]{97,3,100}, true);             // total 200, half 100 = 97+3 or 100
        check(new int[]{1}, false);                   // re-affirm minimal

        // ===== NEW corner cases (distinct from above) =====
        // Constraint boundary values from QUESTION.md: nums[i] in [1,100], length in [1,200].
        check(new int[]{1,100}, false);               // both extremes, total 101 odd
        check(new int[]{1,1,100,100}, true);          // total 202, half 101 = 1+100
        check(new int[]{100,100,1}, false);           // total 201 odd
        check(new int[]{50,50}, true);                // mid value, two equal -> true
        check(new int[]{50}, false);                  // single mid value, cannot split one element
        check(new int[]{99,99,99,99}, true);          // all-equal odd value, even count, total 396, half 198 = 99+99
        check(new int[]{99,99,99}, false);            // all-equal, odd count -> total 297 odd
        check(new int[]{1,3,5,7,9,11,13}, false);     // strictly increasing odds, total 49 odd -> false
        check(new int[]{2,4,8,16,32,64}, false);      // strictly increasing powers of 2, total 126, half 63 unreachable
        check(new int[]{10,9,8,7,6,5,4,3,2,1}, false);// strictly decreasing 1..10, total 55 odd
        check(new int[]{20,19,18,17,16,15,14}, false);// strictly decreasing, total 119 odd
        check(new int[]{1,100,1,100,1,100}, false);   // alternating, total 303 odd -> false
        check(new int[]{7,7,7,7,7,7,7,7}, true);      // heavy duplicates even count, total 56, half 28 = 7*4
        check(new int[]{7,7,7,7,7,7,7}, false);       // heavy duplicates odd count, total 49 odd
        check(new int[]{6,6,6,6,6,6,6,6,6,6}, true);  // 10 sixes, total 60, half 30 = 6*5
        check(new int[]{4,4,4,4,4}, false);           // 5 fours, total 20, half 10 not multiple of 4 -> false
        check(new int[]{1,2,3,4,5,6,7,8,9,12}, false);// total 57 odd
        check(new int[]{3,3,3,4,5}, true);            // mixed, total 18, half 9 = 3+3+3 or 4+5
        check(new int[]{1,1,1,1,1,1,1,1}, true);      // 8 ones, total 8, half 4
        check(new int[]{2,2,2,2,2,2,2,2,2,2}, true);  // 10 twos, total 20, half 10 = 2*5

        // ===== Large / performance cases (independent O(n*sum) DP oracle) =====
        // Constraint upper bound is n=200, nums[i]<=100 -> max total 20000. Build inputs at/near
        // that bound, verify by oracle property, time the call, fail only beyond a generous budget.
        checkAllEqualEvenCount(200, 100);   // 200 hundreds -> total 20000, split-able
        checkAllEqualOddCount(199, 100);    // odd count of equal -> not split-able into equal sums of those? oracle decides
        checkRandomOracle(0, 200);
        checkRandomOracle(1, 200);
        checkAllOnesBig(200);               // 200 ones -> total 200 even -> true
        checkAllOnesBig(199);               // 199 ones -> total odd -> false

        // ===== NEW large / performance cases (max-constraint inputs, oracle-verified, timed) =====
        checkAllEqualEvenCount(198, 100);   // even count just below max, total 19800 -> true
        checkAllEqualEvenCount(200, 99);    // 200 equal odd value, total 19800 (even count*odd) -> true
        checkAllOnesBig(150);               // 150 ones, total 150 even -> true
        checkAllOnesBig(101);               // 101 ones, odd total -> false
        checkRandomOracle(2, 200);          // random at max length, seed 44
        checkRandomOracle(3, 200);          // random at max length, seed 45
        checkRandomOracle(4, 199);          // random at length 199, seed 46
        checkRandomCapped(5, 200, 50);      // random in [1,50] at max length, seed 47
        checkRandomCapped(6, 200, 2);       // random in [1,2] at max length (dense small values), seed 48
        checkRandomCapped(7, 200, 100);     // random full range at max length, seed 49
        checkForcedTrue(8, 200);            // 200 random then duplicated structure forcing even total, oracle decides
        checkAllEqualEvenCount(2, 100);     // off-by-one tiny boundary at min meaningful even count

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent subset-sum DP oracle.
    static boolean oracle(int[] nums) {
        int sum = 0;
        for (int x : nums) sum += x;
        if ((sum & 1) == 1) return false;
        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int x : nums) {
            for (int j = target; j >= x; j--) {
                if (dp[j - x]) dp[j] = true;
            }
        }
        return dp[target];
    }

    static void check(int[] nums, boolean expected) {
        total++;
        try {
            boolean actual = new Answer().canPartition(nums.clone());
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m canPartition(" + brief(nums) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m canPartition(" + brief(nums) + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m canPartition(" + brief(nums) + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkAllEqualEvenCount(int n, int val) {
        int[] nums = new int[n];
        Arrays.fill(nums, val);
        checkWithOracle("all-equal n=" + n + " val=" + val, nums);
    }

    static void checkAllEqualOddCount(int n, int val) {
        int[] nums = new int[n];
        Arrays.fill(nums, val);
        checkWithOracle("all-equal-odd-count n=" + n + " val=" + val, nums);
    }

    static void checkAllOnesBig(int n) {
        int[] nums = new int[n];
        Arrays.fill(nums, 1);
        checkWithOracle("all-ones n=" + n, nums);
    }

    static void checkRandomOracle(long seedOffset, int n) {
        Random rnd = new Random(42 + seedOffset);
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = 1 + rnd.nextInt(100); // [1,100]
        checkWithOracle("random n=" + n + " seed=" + (42 + seedOffset), nums);
    }

    // Random values drawn from [1, cap], cap <= 100 per constraints.
    static void checkRandomCapped(long seedOffset, int n, int cap) {
        Random rnd = new Random(42 + seedOffset);
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = 1 + rnd.nextInt(cap);
        checkWithOracle("random-capped n=" + n + " cap=" + cap + " seed=" + (42 + seedOffset), nums);
    }

    // Build the first half randomly, mirror it into the second half so the multiset is duplicated:
    // total is always even and a true partition exists. Oracle still decides authoritatively.
    static void checkForcedTrue(long seedOffset, int n) {
        Random rnd = new Random(42 + seedOffset);
        int half = n / 2;
        int[] nums = new int[half * 2];
        for (int i = 0; i < half; i++) {
            int v = 1 + rnd.nextInt(100);
            nums[i] = v;
            nums[i + half] = v;
        }
        checkWithOracle("forced-dup n=" + nums.length + " seed=" + (42 + seedOffset), nums);
    }

    static void checkWithOracle(String label, int[] nums) {
        total++;
        try {
            boolean expected = oracle(nums);
            long start = System.nanoTime();
            boolean actual = new Answer().canPartition(nums.clone());
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
