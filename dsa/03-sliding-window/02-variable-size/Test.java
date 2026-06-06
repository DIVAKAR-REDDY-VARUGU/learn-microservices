import java.util.*;

// JUnit-free test harness for Answer.minSubArrayLen(int target, int[] nums)
// Minimum Size Subarray Sum. Compile: javac Answer.java Test.java   Run: java Test
public class Test {

    static int pass = 0;
    static int total = 0;

    static void check(String name, int target, int[] nums, int expected) {
        total++;
        try {
            int actual = new Answer().minSubArrayLen(target, nums);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + " | target=" + target + " nums=" + brief(nums)
                        + " | expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | target=" + target + " nums=" + brief(nums)
                    + " | expected=" + expected + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    // Large/perf case: verify against an independent O(n) two-pointer oracle and time it.
    static void checkProp(String name, int target, int[] nums) {
        total++;
        try {
            long t0 = System.nanoTime();
            int actual = new Answer().minSubArrayLen(target, nums);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1e6;
            int expected = oracle(target, nums);
            String label = name + " [" + String.format("%.1f", ms) + " ms]";
            if (ms > 3000.0) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | TOO SLOW (>3000ms) possible quadratic");
            } else if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + " | expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | target=" + target + " n=" + nums.length
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent O(n) oracle: minimal window with sum >= target (positive ints).
    static int oracle(int target, int[] nums) {
        long sum = 0;
        int left = 0;
        int best = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                int len = right - left + 1;
                if (len < best) best = len;
                sum -= nums[left++];
            }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    static String brief(int[] a) {
        if (a.length <= 16) return Arrays.toString(a);
        return "[len=" + a.length + ", first=" + a[0] + "...]";
    }

    public static void main(String[] args) {
        // Provided examples
        check("example1", 7, new int[]{2, 3, 1, 2, 4, 3}, 2);
        check("example2 single elem meets target", 4, new int[]{1, 4, 4}, 1);
        check("example3 no valid subarray", 11, new int[]{1, 1, 1, 1, 1, 1, 1, 1}, 0);

        // Corner: single element array, meets target
        check("single elem meets", 3, new int[]{5}, 1);
        // Corner: single element array, does not meet target
        check("single elem fails", 6, new int[]{5}, 0);
        // Corner: single element exactly equal
        check("single elem equal", 5, new int[]{5}, 1);

        // Corner: whole array needed
        check("whole array needed", 15, new int[]{1, 2, 3, 4, 5}, 5);
        // Corner: total exactly equals target -> whole array
        check("total equals target", 6, new int[]{1, 2, 3}, 3);
        // Corner: total just below target -> 0
        check("total below target", 7, new int[]{1, 2, 3}, 0);

        // Corner: first element already >= target
        check("first elem big", 4, new int[]{4, 1, 1, 1}, 1);
        // Corner: last element already >= target
        check("last elem big", 9, new int[]{1, 1, 1, 9}, 1);

        // Corner: all equal elements
        check("all equal need two", 4, new int[]{2, 2, 2, 2}, 2);
        check("all equal need one", 2, new int[]{2, 2, 2, 2}, 1);
        check("all equal need all", 8, new int[]{2, 2, 2, 2}, 4);

        // Corner: minimal window in the middle
        check("min window middle", 8, new int[]{1, 1, 4, 4, 1, 1}, 2);

        // Corner: large target, single huge element present
        check("large target one huge", 100, new int[]{10, 20, 100, 5}, 1);

        // Corner: shrinking matters (long prefix then big)
        check("shrink from left", 8, new int[]{1, 2, 3, 4}, 2);

        // Corner: monotonic increasing
        check("increasing", 11, new int[]{1, 2, 3, 4, 5}, 3);
        // Corner: monotonic decreasing
        check("decreasing", 11, new int[]{5, 4, 3, 2, 1}, 3);

        // Corner: many small elements just reaching target
        check("many small reach", 5, new int[]{1, 1, 1, 1, 1}, 5);
        check("many small not reach", 6, new int[]{1, 1, 1, 1, 1}, 0);

        // Corner: target = 1, any single element works
        check("target one", 1, new int[]{1, 1, 1}, 1);

        // ===== ADDED CORNER CASES =====

        // Boundary: target at minimum (1) with min element value (1)
        check("target=1 min elem", 1, new int[]{1}, 1);
        // Boundary: nums[i] at upper bound 10000, single elem meets large target
        check("single max elem meets", 10000, new int[]{10000}, 1);
        check("single max elem fails", 10001, new int[]{10000}, 0);

        // Boundary: very large target (near 10^9) impossible for small array -> 0
        check("huge target impossible", 1000000000, new int[]{10000, 10000, 10000}, 0);

        // All elements equal to upper bound, exact multiple
        check("all max need three", 30000, new int[]{10000, 10000, 10000, 10000}, 3);
        check("all max need one", 10000, new int[]{10000, 10000}, 1);

        // Strictly increasing, answer is a suffix of length 1
        check("increasing suffix one", 5, new int[]{1, 2, 3, 4, 5}, 1);
        // Strictly decreasing, answer is prefix of length 1
        check("decreasing prefix one", 5, new int[]{5, 4, 3, 2, 1}, 1);

        // Alternating big/small
        check("alternating", 7, new int[]{6, 1, 6, 1, 6}, 2);
        check("alternating exact one", 6, new int[]{6, 1, 6, 1, 6}, 1);

        // Heavy duplicates of ones, exact target = length
        check("all ones exact", 7, new int[]{1, 1, 1, 1, 1, 1, 1}, 7);
        check("all ones one short", 8, new int[]{1, 1, 1, 1, 1, 1, 1}, 0);

        // Minimal window right at the end
        check("min window at end", 9, new int[]{1, 1, 1, 1, 4, 5}, 2);
        // Minimal window right at the start
        check("min window at start", 9, new int[]{5, 4, 1, 1, 1, 1}, 2);

        // Off-by-one: target one above a perfect prefix
        check("off by one above", 10, new int[]{2, 3, 4}, 0);
        check("off by one exact", 9, new int[]{2, 3, 4}, 3);

        // Single huge spike dwarfs target deep in array
        check("spike deep", 50, new int[]{1, 1, 1, 1, 1, 60}, 1);

        // Two pointers must walk past a long valley then a plateau
        check("valley then plateau", 12, new int[]{1, 1, 1, 1, 6, 6, 1}, 2);

        // ===== LARGE / PERFORMANCE CASES (verified by independent O(n) oracle) =====
        Random rnd = new Random(42);
        int n = 100000;

        // Large random positives, moderate target (answer exists, small window)
        int[] big1 = new int[n];
        for (int i = 0; i < n; i++) big1[i] = rnd.nextInt(10000) + 1; // [1,10000]
        checkProp("large random target=1e6", 1000000, big1);

        // Large random positives, tiny target -> answer 1
        int[] big2 = new int[n];
        for (int i = 0; i < n; i++) big2[i] = rnd.nextInt(10000) + 1;
        checkProp("large random target=1", 1, big2);

        // Large random, target larger than total sum -> 0 (full scan, no early exit)
        long totalSum = 0;
        int[] big3 = new int[n];
        for (int i = 0; i < n; i++) { big3[i] = rnd.nextInt(10000) + 1; totalSum += big3[i]; }
        checkProp("large impossible target", (int) Math.min(Integer.MAX_VALUE, totalSum + 1000000L) > 0 ? 1000000000 : 1000000000, big3);

        // Large all-ones: answer equals target exactly (forces full window)
        int[] big4 = new int[n];
        Arrays.fill(big4, 1);
        checkProp("large all-ones target=99999", 99999, big4);
        checkProp("large all-ones target=n+1 -> 0", n + 1, big4);

        // Large with single huge spike at the end (must scan to find it)
        int[] big5 = new int[n];
        Arrays.fill(big5, 1);
        big5[n - 1] = 10000;
        checkProp("large spike at end", 10000, big5);

        // Large increasing ramp (best window is a short suffix)
        int[] big6 = new int[n];
        for (int i = 0; i < n; i++) big6[i] = (i % 10000) + 1;
        checkProp("large ramp target=30000", 30000, big6);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
