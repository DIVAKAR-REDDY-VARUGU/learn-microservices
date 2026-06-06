import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        // Example 1
        check(new int[]{2,3,1,1,4}, true);
        // Example 2: trapped at the zero
        check(new int[]{3,2,1,0,4}, false);
        // Example 3: single element already at last index
        check(new int[]{0}, true);

        // Single nonzero element
        check(new int[]{5}, true);
        // Two elements, can step
        check(new int[]{1,0}, true);
        // Two elements, stuck at start (0 jump)
        check(new int[]{0,1}, false);
        // Leading zero with length > 1 -> stuck immediately
        check(new int[]{0,0,0}, false);
        // Trailing zero is fine (it's the goal)
        check(new int[]{2,0,0}, true);
        // Big first jump reaches end directly
        check(new int[]{4,0,0,0,0}, true);
        // Exactly reaches end
        check(new int[]{3,0,0,0}, true);
        // Falls one short
        check(new int[]{2,0,0,0}, false);
        // All ones -> always reachable
        check(new int[]{1,1,1,1,1}, true);
        // Zero in middle but jumpable over it
        check(new int[]{2,0,1,0}, true);
        // Zero in middle that blocks
        check(new int[]{1,0,1}, false);
        // Reaches farthest exactly at a zero then needs to continue
        check(new int[]{1,1,0,1}, false);
        // Large jump values (max bound style)
        check(new int[]{100000,0,0,0,0}, true);
        // Long all-ones reachable
        check(new int[]{2,5,0,0}, true);
        // Multiple zeros, blocked
        check(new int[]{1,0,0,0}, false);
        // Just barely passes a zero region
        check(new int[]{3,2,1,0,0}, false);
        // Can reach last but value there is 0 (still counts as reached)
        check(new int[]{2,1,0}, true);

        // ===== NEW corner cases =====
        // Min length (n=1) with the max element value (10^5)
        check(new int[]{100000}, true);
        // Single zero at the only index -> already at the goal
        check(new int[]{0}, true);
        // Strictly increasing: each index reaches at least the next
        check(new int[]{1,2,3,4,5}, true);
        // Strictly decreasing but enough to slide forward
        check(new int[]{4,3,2,1,0}, true);
        // Strictly decreasing that runs out before the end
        check(new int[]{2,1,0,0,0}, false);
        // All equal to 1: exactly reaches every next index
        check(new int[]{1,1,1,1,1,1,1,1}, true);
        // All equal to 2: leaps reachable
        check(new int[]{2,2,2,2,2}, true);
        // Alternating value/zero: jumps over each zero
        check(new int[]{2,0,2,0,2,0,1}, true);
        // Alternating that traps: a zero exactly at the farthest reach
        check(new int[]{1,0,1,0,1,0,1}, false);
        // Heavy duplicates of the same nonzero, reachable
        check(new int[]{3,3,3,3,3,3,3,3,3,3}, true);
        // All zeros except first jumps to last (off-by-one exact reach)
        check(new int[]{9,0,0,0,0,0,0,0,0,0}, true);
        // Off-by-one short: first jump lands one before the last
        check(new int[]{8,0,0,0,0,0,0,0,0,0}, false);
        // Max element early dominates everything after
        check(new int[]{100000,0,0,0,0,0,0,0,0,0}, true);
        // Zero immediately after a 1, no way past
        check(new int[]{1,0,0,100000}, false);
        // Two elements where first equals max value
        check(new int[]{100000,0}, true);
        // Long run of ones then a trailing zero goal
        check(new int[]{1,1,1,1,1,1,1,1,1,0}, true);
        // A single blocking zero in the middle of a long ones array
        check(new int[]{1,1,1,0,1,1,1}, false);
        // Big head jump, then a blocked tail region that is still reached
        check(new int[]{6,0,0,0,0,0,1}, true);

        // ===== Large / performance cases (property + oracle verified) =====
        Random rng = new Random(42);

        // Large case 1: random small values (0..3) over n = 100000 -> mixed result
        largePropertyCase("rand 0..3, n=100000", randomArray(rng, 100000, 0, 3));

        // Large case 2: random values (0..2) -> more zeros, likely some blocking
        largePropertyCase("rand 0..2, n=100000", randomArray(rng, 100000, 0, 2));

        // Large case 3: guaranteed reachable (all ones) huge array
        largeExpectedCase("all-ones, n=100000", filledArray(100000, 1), true);

        // Large case 4: guaranteed blocked (zeros after the first) huge array
        int[] blocked = filledArray(100000, 0);
        blocked[0] = 1; // can only step once then stuck
        largeExpectedCase("blocked zeros, n=100000", blocked, false);

        // Large case 5: huge first jump reaching far, near upper bound n = 10000
        int[] bigHead = filledArray(10000, 0);
        bigHead[0] = 100000; // 10^5 >= 9999, reaches the end
        largeExpectedCase("big head jump, n=10000", bigHead, true);

        // Large case 6: random wider values (0..100000) n = 50000
        largePropertyCase("rand 0..100000, n=50000", randomArray(rng, 50000, 0, 100000));

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // ---- Core check: known expected value ----
    static void check(int[] nums, boolean expected) {
        total++;
        int[] inputCopy = nums == null ? null : nums.clone();
        try {
            boolean actual = new Answer().canJump(nums);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m canJump(" + Arrays.toString(inputCopy) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m canJump(" + Arrays.toString(inputCopy)
                        + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m canJump(" + Arrays.toString(inputCopy)
                    + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---- Large case where the correct answer is known/provable ----
    static void largeExpectedCase(String label, int[] nums, boolean expected) {
        total++;
        int n = nums.length;
        try {
            long t0 = System.nanoTime();
            boolean actual = new Answer().canJump(nums);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            boolean correct = actual == expected;
            boolean inBudget = ms <= 3000.0;
            if (correct && inBudget) {
                pass++;
                System.out.printf("\033[32m[PASS]\033[0m large %s (n=%d, %.2f ms) = %b%n", label, n, ms, actual);
            } else if (!correct) {
                System.out.printf("\033[31m[FAIL]\033[0m large %s (n=%d, %.2f ms) expected=%b actual=%b%n",
                        label, n, ms, expected, actual);
            } else {
                System.out.printf("\033[31m[FAIL]\033[0m large %s (n=%d) too slow: %.2f ms > 3000 ms%n", label, n, ms);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m large " + label + " threw "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---- Large case verified by an independent O(n) oracle ----
    static void largePropertyCase(String label, int[] nums) {
        total++;
        int n = nums.length;
        boolean expected = oracleCanJump(nums); // independent greedy from the END
        try {
            long t0 = System.nanoTime();
            boolean actual = new Answer().canJump(nums);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            boolean correct = actual == expected;
            boolean inBudget = ms <= 3000.0;
            if (correct && inBudget) {
                pass++;
                System.out.printf("\033[32m[PASS]\033[0m large %s (n=%d, %.2f ms) = %b%n", label, n, ms, actual);
            } else if (!correct) {
                System.out.printf("\033[31m[FAIL]\033[0m large %s (n=%d, %.2f ms) oracle=%b actual=%b%n",
                        label, n, ms, expected, actual);
            } else {
                System.out.printf("\033[31m[FAIL]\033[0m large %s (n=%d) too slow: %.2f ms > 3000 ms%n", label, n, ms);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m large " + label + " threw "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent oracle: scan from the right, tracking the leftmost "good" index
    // that can reach the end. O(n) time, O(1) space; distinct from the forward
    // farthest-reach greedy so it cross-checks the implementation.
    static boolean oracleCanJump(int[] nums) {
        int n = nums.length;
        int lastGood = n - 1;
        for (int i = n - 2; i >= 0; i--) {
            if ((long) i + nums[i] >= lastGood) {
                lastGood = i;
            }
        }
        return lastGood == 0;
    }

    // ---- Input builders ----
    static int[] randomArray(Random rng, int n, int lo, int hi) {
        int[] a = new int[n];
        int span = hi - lo + 1;
        for (int i = 0; i < n; i++) {
            a[i] = lo + rng.nextInt(span);
        }
        return a;
    }

    static int[] filledArray(int n, int value) {
        int[] a = new int[n];
        Arrays.fill(a, value);
        return a;
    }
}
