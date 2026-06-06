import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    // answer is a pair of indices in any order; validate against nums+target
    static void check(String name, int[] nums, int target) {
        total++;
        try {
            int[] actual = new Answer().twoSum(nums.clone(), target);
            boolean ok = actual != null && actual.length == 2;
            if (ok) {
                int i = actual[0], j = actual[1];
                ok = i >= 0 && j >= 0 && i < nums.length && j < nums.length && i != j
                        && (long) nums[i] + (long) nums[j] == (long) target;
            }
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + Arrays.toString(actual));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums) + " target=" + target
                        + " | actual=" + Arrays.toString(actual) + " (must be two distinct valid indices summing to target)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums) + " target=" + target
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: validate property (two distinct valid indices summing to target) and time it
    static void checkProp(String name, int[] nums, int target, long budgetMs) {
        total++;
        try {
            long t0 = System.nanoTime();
            int[] actual = new Answer().twoSum(nums.clone(), target);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            boolean ok = actual != null && actual.length == 2;
            if (ok) {
                int i = actual[0], j = actual[1];
                ok = i >= 0 && j >= 0 && i < nums.length && j < nums.length && i != j
                        && (long) nums[i] + (long) nums[j] == (long) target;
            }
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " -> " + Arrays.toString(actual));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | target=" + target
                        + " | actual=" + Arrays.toString(actual) + " (must be two distinct valid indices summing to target)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | target=" + target
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static String brief(int[] a) {
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " first=" + a[0] + "...]";
    }

    public static void main(String[] args) {
        check("example1", new int[]{2,7,11,15}, 9);
        check("example2", new int[]{3,2,4}, 6);
        check("duplicate values", new int[]{3,3}, 6);
        check("two elements", new int[]{1,2}, 3);
        check("negatives", new int[]{-3,4,3,90}, 0);
        check("negative target", new int[]{-1,-2,-3,-4}, -7);
        check("zeros", new int[]{0,0}, 0);
        check("answer at ends", new int[]{5,1,2,8}, 13);
        check("large bounds", new int[]{1000000000,-1000000000,1}, 1);
        check("complement appears later", new int[]{1,5,5,3}, 8);
        check("mix pos neg", new int[]{-10,7,19,15,9,3}, -7);

        // --- added corner cases ---
        check("min array length 2", new int[]{4,5}, 9);
        check("answer first two indices", new int[]{6,4,1,2}, 10);
        check("answer index 0 and last", new int[]{8,1,2,3,4}, 12);
        check("max value bound", new int[]{1000000000,1000000000}, 2000000000);
        check("min value bound", new int[]{-1000000000,-1000000000}, -2000000000);
        check("extreme spread to zero", new int[]{-1000000000,5,1000000000}, 0);
        check("target needs overflow-safe sum", new int[]{1000000000,999999999}, 1999999999);
        check("duplicates many, pair distinct", new int[]{3,3,3,3,4}, 7);
        check("all same plus one", new int[]{5,5,5,5,2}, 7);
        check("strictly increasing", new int[]{1,2,3,4,5,6}, 11);
        check("strictly decreasing", new int[]{6,5,4,3,2,1}, 3);
        check("complement is negative", new int[]{10,-3,7,2}, 4);
        check("two negatives sum", new int[]{-8,-2,5,11}, -10);
        check("zero in middle", new int[]{4,0,-4,9}, 0);
        check("adjacent pair late", new int[]{9,8,7,6,1,2}, 3);
        check("first and second only valid", new int[]{50,50,1,2,3}, 100);

        // --- large / performance cases (property + timing) ---
        // n=10^4 distinct sequential values; unique pair engineered so a quadratic scan is exposed.
        {
            int n = 10_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = i + 1; // 1..n, all distinct
            // unique pair: indices n-2 and n-1 (values n-1 and n); make target unreachable otherwise
            // pick two specific values whose only pair is the last two: use values 3 and (n) where 3 not paired uniquely.
            // Safer: keep distinct values but set target = big[0] + big[n-1] (only pair = ends since all distinct & increasing).
            int target = big[0] + big[n - 1];
            checkProp("large 10k distinct ends pair", big, target, 3000);
        }
        // n=10^4 with the matching pair near the END so an O(n) hash solution still finds it fast,
        // but quadratic worst-case is triggered.
        {
            Random rnd = new Random(42);
            int n = 10_000;
            int[] big = new int[n];
            // fill with large distinct values far from target
            for (int i = 0; i < n; i++) big[i] = 100_000 + i * 7;
            // implant unique complementary pair at the two last positions
            big[n - 2] = 13;
            big[n - 1] = 29;
            int target = 42;
            // ensure no other pair sums to 42 (all others >= 100000)
            checkProp("large 10k pair at tail", big, target, 3000);
        }
        // n=10^4 random in [-10^9,10^9] with a guaranteed implanted unique pair.
        {
            Random rnd = new Random(42);
            int n = 10_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextInt(2_000_000_000) - 1_000_000_000;
            // implant pair summing to a far-out target unlikely to be hit by randoms
            long T = 4_000_000_000L; // bigger than any natural random pair (max ~2e9)
            // but values must fit int; use two near-max ints summing via long target
            big[1234] = 1_999_999_999;
            big[8765] = 2_000_000_001 > Integer.MAX_VALUE ? Integer.MAX_VALUE : 2_000_000_001;
            // recompute a clean target from the implanted values to avoid overflow surprises
            long tg = (long) big[1234] + (long) big[8765];
            // ensure no random pair coincidentally equals tg: tg ~ 4.1e9 which exceeds max random pair (~2e9)
            checkProp("large 10k implanted high-sum pair", big, (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, tg)) == tg ? (int) tg : 0, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
