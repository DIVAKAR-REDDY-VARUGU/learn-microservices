import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        check(new int[]{-2,1,-3,4,-1,2,1,-5,4}, 6);   // example: subarray [4,-1,2,1]
        check(new int[]{1}, 1);                        // single element (example)
        check(new int[]{5,4,-1,7,8}, 23);              // entire array (example)
        check(new int[]{-1}, -1);                      // single negative
        check(new int[]{-5}, -5);                      // single negative min-ish
        check(new int[]{-2,-3,-1,-4}, -1);             // all negative -> least negative
        check(new int[]{-1,-2,-3}, -1);               // all negative
        check(new int[]{0}, 0);                         // single zero
        check(new int[]{0,0,0}, 0);                     // all zeros
        check(new int[]{2,2,2,2}, 8);                  // all equal positive
        check(new int[]{1,2,3,4,5}, 15);              // already sorted ascending
        check(new int[]{5,4,3,2,1}, 15);              // reverse sorted
        check(new int[]{-1,4,-1,4,-1}, 7);            // alternating, [4,-1,4]
        check(new int[]{8,-19,5,-4,20}, 21);          // [5,-4,20]
        check(new int[]{10000,-10000,10000}, 10000); // big bounds
        check(new int[]{-2,1}, 1);                     // pick the positive

        // ===== New corner cases (existing) =====
        check(new int[]{-10000}, -10000);              // single min bound
        check(new int[]{10000}, 10000);                // single max bound
        check(new int[]{-10000,-10000,-10000}, -10000);// all min, all negative
        check(new int[]{10000,10000,10000}, 30000);    // all max
        check(new int[]{0,-1,0,-2,0}, 0);              // zeros separated by negatives
        check(new int[]{-3,-3,-3,-3}, -3);             // all equal negative
        check(new int[]{5,-5,5,-5,5}, 5);              // alternating equal magnitude
        check(new int[]{-1,-1,5,-1,-1}, 5);            // single positive island
        check(new int[]{1,-1,1,-1,1,-1,1}, 1);         // tight alternating
        check(new int[]{3,-2,5,-1}, 6);                // [3,-2,5]
        check(new int[]{-2,-1}, -1);                   // both negative, pick larger
        check(new int[]{2,-1,2,-1,2}, 4);              // sum across small dips
        check(new int[]{4,-1,2,1}, 6);                 // the optimal subarray alone
        check(new int[]{-5,-4,-3,-2,-1}, -1);          // strictly increasing negatives
        check(new int[]{1,-2,3,-4,5,-6,7}, 7);         // alternating sign growing
        check(new int[]{100,-1,100,-1,100}, 298);      // tall plateaus with tiny dips

        // ===== ADDED corner cases (distinct from above) =====
        check(new int[]{-10000,10000}, 10000);                 // min then max bound, take max alone
        check(new int[]{10000,-10000}, 10000);                 // max then min bound, take max alone
        check(new int[]{-9999}, -9999);                        // single near-min negative
        check(new int[]{9999}, 9999);                          // single near-max positive
        check(new int[]{0,0,0,0,0,0,0,0}, 0);                  // many zeros
        check(new int[]{-1,0,-1,0,-1}, 0);                     // zeros are the best among negatives
        check(new int[]{7,7,7,7,7,7,7}, 49);                   // all equal positive (odd length)
        check(new int[]{-4,-4,-4,-4,-4,-4}, -4);              // all equal negative (even length)
        check(new int[]{1,2,3,4,5,6,7,8,9,10}, 55);          // strictly increasing positives
        check(new int[]{10,9,8,7,6,5,4,3,2,1}, 55);          // strictly decreasing positives
        check(new int[]{-1,2,-1,2,-1,2,-1}, 4);              // alternating, optimal [2,-1,2,-1,2]=4
        check(new int[]{6,-1,-1,-1,-1,-1,-1,6}, 6);          // two peaks separated by deep valley -> single peak 6
        check(new int[]{-3,4,-1,2,1,-5,4}, 6);               // classic-ish, [4,-1,2,1]=6
        check(new int[]{2,2,2,2,2,2,2,2,2,2}, 20);           // heavy duplicates positive
        check(new int[]{-2,-2,-2,3,-2,-2,-2}, 3);            // single positive island among dups
        check(new int[]{10000,-1,-1,-1,10000}, 19997);      // wide span beats single peak
        check(new int[]{-10000,1,1,1,1,1,1,1,1,1,1}, 10);   // skip a huge negative front
        check(new int[]{1,1,1,1,1,1,1,1,1,-10000}, 9);      // skip a huge negative tail
        check(new int[]{3,-1,-1,-1,-1,3}, 3);               // valley exactly cancels gain -> 3

        // ===== Large / performance cases (existing) =====
        checkRandomOracle(100_000, 0);
        checkRandomOracle(100_000, 1);
        checkAllNegativeLarge(100_000);
        checkAllMaxLarge(100_000);

        // ===== ADDED large / performance cases =====
        checkRandomOracle(100_000, 2);                  // another random seed at upper bound
        checkRandomOracle(50_000, 3);                   // mid-size random vs O(n) oracle
        checkRandomOracle(10_000, 4);                   // smaller random vs O(n) oracle
        checkRandomSmallRangeOracle(100_000, 5);        // small value range -> many sign flips
        checkSawtoothLarge(100_000);                    // structured +10000/-10000 pattern, property-verified
        checkAllZeroLarge(100_000);                     // all zeros -> answer 0

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent O(n) oracle (uses long to avoid any intermediate overflow).
    static long kadaneOracle(int[] nums) {
        long best = nums[0];
        long cur = nums[0];
        for (int i = 1; i < nums.length; i++) {
            cur = Math.max((long) nums[i], cur + nums[i]);
            best = Math.max(best, cur);
        }
        return best;
    }

    static void check(int[] nums, int expected) {
        total++;
        try {
            int actual = new Answer().maxSubArray(nums.clone());
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m maxSubArray(" + brief(nums) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m maxSubArray(" + brief(nums) + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m maxSubArray(" + brief(nums) + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkRandomOracle(int n, long seedOffset) {
        total++;
        String label = "perf random n=" + n + " seed=" + (42 + seedOffset);
        try {
            Random rnd = new Random(42 + seedOffset);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = rnd.nextInt(20001) - 10000; // [-10000,10000]
            long expected = kadaneOracle(nums);
            long start = System.nanoTime();
            int actual = new Answer().maxSubArray(nums.clone());
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms (possible quadratic)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Small value range -> frequent sign changes; still exact-match against O(n) oracle.
    static void checkRandomSmallRangeOracle(int n, long seedOffset) {
        total++;
        String label = "perf random small-range n=" + n + " seed=" + (42 + seedOffset);
        try {
            Random rnd = new Random(42 + seedOffset);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = rnd.nextInt(11) - 5; // [-5,5]
            long expected = kadaneOracle(nums);
            long start = System.nanoTime();
            int actual = new Answer().maxSubArray(nums.clone());
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms (possible quadratic)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkAllNegativeLarge(int n) {
        total++;
        String label = "perf all-negative n=" + n;
        try {
            Random rnd = new Random(42);
            int[] nums = new int[n];
            int expectedMax = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                nums[i] = -1 - rnd.nextInt(10000); // [-10000,-1]
                expectedMax = Math.max(expectedMax, nums[i]);
            }
            long start = System.nanoTime();
            int actual = new Answer().maxSubArray(nums.clone());
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expectedMax) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expectedMax + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkAllMaxLarge(int n) {
        total++;
        String label = "perf all-max n=" + n;
        try {
            int[] nums = new int[n];
            Arrays.fill(nums, 10000);
            long expected = (long) n * 10000L; // 1e9, fits int? n=1e5 -> 1e9 fits int (< 2.1e9)
            long start = System.nanoTime();
            int actual = new Answer().maxSubArray(nums.clone());
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if ((long) actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Structured sawtooth: 10000,-10000,10000,-10000,... at the value bounds.
    // Best subarray is just a single 10000 (any extension drops it), so answer = 10000.
    static void checkSawtoothLarge(int n) {
        total++;
        String label = "perf sawtooth n=" + n;
        try {
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = (i % 2 == 0) ? 10000 : -10000;
            long expected = kadaneOracle(nums); // independent oracle confirms = 10000
            long start = System.nanoTime();
            int actual = new Answer().maxSubArray(nums.clone());
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if ((long) actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkAllZeroLarge(int n) {
        total++;
        String label = "perf all-zero n=" + n;
        try {
            int[] nums = new int[n]; // already all zeros
            int expected = 0;
            long start = System.nanoTime();
            int actual = new Answer().maxSubArray(nums.clone());
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static String brief(int[] nums) {
        if (nums.length <= 16) return Arrays.toString(nums);
        return "[len=" + nums.length + " " + nums[0] + "," + nums[1] + ",...," + nums[nums.length - 1] + "]";
    }
}
