import java.util.*;

// JUnit-free test harness for Answer.maxSlidingWindow(int[] nums, int k)
// Sliding Window Maximum. Compile: javac Answer.java Test.java   Run: java Test
public class Test {

    static int pass = 0;
    static int total = 0;

    static void check(String name, int[] nums, int k, int[] expected) {
        total++;
        try {
            int[] actual = new Answer().maxSlidingWindow(nums, k);
            if (actual != null && Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + " | nums=" + brief(nums) + " k=" + k
                        + " | expected=" + brief(expected)
                        + " actual=" + brief(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | nums=" + brief(nums) + " k=" + k
                    + " | expected=" + brief(expected)
                    + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Large/perf case: verify against an independent O(n) monotonic-deque oracle and time it.
    static void checkProp(String name, int[] nums, int k) {
        total++;
        try {
            long t0 = System.nanoTime();
            int[] actual = new Answer().maxSlidingWindow(nums, k);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1e6;
            int[] expected = oracle(nums, k);
            String label = name + " [" + String.format("%.1f", ms) + " ms]";
            if (ms > 3000.0) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | TOO SLOW (>3000ms) possible quadratic");
            } else if (actual != null && Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (out len=" + expected.length + ")");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + " | expected len=" + expected.length
                        + " actual=" + (actual == null ? "null" : ("len=" + actual.length)));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | n=" + nums.length + " k=" + k
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent O(n) oracle using a monotonic deque of indices.
    static int[] oracle(int[] nums, int k) {
        int n = nums.length;
        int[] res = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[i]) dq.pollLast();
            dq.addLast(i);
            if (dq.peekFirst() <= i - k) dq.pollFirst();
            if (i >= k - 1) res[i - k + 1] = nums[dq.peekFirst()];
        }
        return res;
    }

    static String brief(int[] a) {
        if (a == null) return "null";
        if (a.length <= 20) return Arrays.toString(a);
        return "[len=" + a.length + ", first=" + a[0] + "...]";
    }

    public static void main(String[] args) {
        // Provided examples
        check("example1", new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3, new int[]{3, 3, 5, 5, 6, 7});
        check("example2 single element k=1", new int[]{1}, 1, new int[]{1});
        check("example3 k=n=2", new int[]{9, 11}, 2, new int[]{11});

        // Corner: k == 1 -> output equals input
        check("k=1 identity", new int[]{4, 2, 12, 7, 1}, 1, new int[]{4, 2, 12, 7, 1});

        // Corner: k == n -> single max of whole array
        check("k equals n", new int[]{2, 8, 5, 3}, 4, new int[]{8});

        // Corner: all equal elements
        check("all equal", new int[]{5, 5, 5, 5}, 2, new int[]{5, 5, 5});

        // Corner: strictly increasing -> max is last of each window
        check("increasing", new int[]{1, 2, 3, 4, 5}, 3, new int[]{3, 4, 5});

        // Corner: strictly decreasing -> max is first of each window
        check("decreasing", new int[]{5, 4, 3, 2, 1}, 3, new int[]{5, 4, 3});

        // Corner: all negatives
        check("all negatives", new int[]{-7, -8, -1, -3, -5}, 2, new int[]{-7, -1, -1, -3});

        // Corner: negatives and positives mixed
        check("mixed signs", new int[]{-1, 3, -1, -3, 5, 3}, 3, new int[]{3, 3, 5, 5});

        // Corner: duplicates including max repeated
        check("dup max", new int[]{7, 7, 1, 7}, 2, new int[]{7, 7, 7});

        // Corner: max evicted as window slides
        check("max evicted", new int[]{9, 1, 1, 1, 1}, 2, new int[]{9, 1, 1, 1});

        // Corner: single element array k=1
        check("single elem", new int[]{42}, 1, new int[]{42});

        // Corner: bound values
        check("min/max bounds", new int[]{-10000, 10000, -10000, 10000}, 2, new int[]{10000, 10000, 10000});

        // Corner: peak in middle then plateau
        check("peak middle", new int[]{1, 5, 2, 5, 1}, 3, new int[]{5, 5, 5});

        // Corner: k=2 over zig-zag
        check("zigzag k2", new int[]{1, 3, 1, 2, 0, 5}, 2, new int[]{3, 3, 2, 2, 5});

        // Corner: longer window, last window contains running max
        check("longer window", new int[]{4, 3, 11, 2, 6, 8, 1}, 4, new int[]{11, 11, 11, 8});

        // ===== ADDED CORNER CASES =====

        // Boundary: single element at upper bound, k=1
        check("single max bound", new int[]{10000}, 1, new int[]{10000});
        // Boundary: single element at lower bound, k=1
        check("single min bound", new int[]{-10000}, 1, new int[]{-10000});

        // k == n with all negatives
        check("k=n all negative", new int[]{-3, -1, -7, -2}, 4, new int[]{-1});

        // k=1 with negatives -> identity
        check("k=1 negatives identity", new int[]{-5, -1, -9, -2}, 1, new int[]{-5, -1, -9, -2});

        // Increasing then decreasing (peak in middle), wide window
        check("mountain k=3", new int[]{1, 3, 5, 4, 2}, 3, new int[]{5, 5, 5});
        // Valley shape (decreasing then increasing)
        check("valley k=3", new int[]{5, 3, 1, 3, 5}, 3, new int[]{5, 3, 5});

        // Alternating high/low at bounds
        check("alternating bounds k=3", new int[]{10000, -10000, 10000, -10000, 10000}, 3,
                new int[]{10000, 10000, 10000});

        // Max at the very front then strictly decreasing -> max evicted each step
        check("front max decreasing", new int[]{8, 7, 6, 5, 4}, 2, new int[]{8, 7, 6, 5});
        // Max at the very end then strictly increasing
        check("end max increasing", new int[]{1, 2, 3, 4, 9}, 2, new int[]{2, 3, 4, 9});

        // Heavy duplicates with a single distinct dip
        check("dup with dip", new int[]{4, 4, 4, 1, 4, 4}, 3, new int[]{4, 4, 4, 4});

        // All zeros
        check("all zeros", new int[]{0, 0, 0, 0, 0}, 3, new int[]{0, 0, 0});

        // Two-element windows over a long plateau with one spike
        check("plateau one spike", new int[]{2, 2, 9, 2, 2}, 2, new int[]{2, 9, 9, 2});

        // Off-by-one: k = n-1 (exactly two windows)
        check("k = n-1", new int[]{3, 1, 4, 1, 5}, 4, new int[]{4, 5});

        // Window where the max is repeatedly the boundary element
        check("boundary max repeats", new int[]{6, 1, 6, 1, 6, 1}, 2, new int[]{6, 6, 6, 6, 6});

        // Strictly increasing then equal tail
        check("increasing then flat", new int[]{1, 2, 3, 3, 3}, 2, new int[]{2, 3, 3, 3});

        // Negative max bound mixed with min bound, k=3
        check("bounds k=3", new int[]{-10000, 10000, -10000, 10000, -10000}, 3,
                new int[]{10000, 10000, 10000});

        // ===== LARGE / PERFORMANCE CASES (verified by independent O(n) oracle) =====
        Random rnd = new Random(42);
        int n = 100000;

        // Large random in full value range, mid-size window
        int[] big1 = new int[n];
        for (int i = 0; i < n; i++) big1[i] = rnd.nextInt(20001) - 10000;
        checkProp("large random k=1000", big1, 1000);

        // Large random, k=1 (identity, full output)
        int[] big2 = new int[n];
        for (int i = 0; i < n; i++) big2[i] = rnd.nextInt(20001) - 10000;
        checkProp("large random k=1", big2, 1);

        // Large random, k=n (single output)
        int[] big3 = new int[n];
        for (int i = 0; i < n; i++) big3[i] = rnd.nextInt(20001) - 10000;
        checkProp("large random k=n", big3, n);

        // Large strictly increasing -> worst case for deque cleanup at front
        int[] big4 = new int[n];
        for (int i = 0; i < n; i++) big4[i] = i - 50000; // increasing, spans negatives/positives
        checkProp("large increasing k=500", big4, 500);

        // Large strictly decreasing -> worst case for deque pop-back
        int[] big5 = new int[n];
        for (int i = 0; i < n; i++) big5[i] = 50000 - i;
        checkProp("large decreasing k=500", big5, 500);

        // Large all-equal (deque grows/shrinks with equal elements)
        int[] big6 = new int[n];
        Arrays.fill(big6, 7);
        checkProp("large all-equal k=333", big6, 333);

        // Large half window
        int[] big7 = new int[n];
        for (int i = 0; i < n; i++) big7[i] = rnd.nextInt(20001) - 10000;
        checkProp("large random k=n/2", big7, n / 2);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
