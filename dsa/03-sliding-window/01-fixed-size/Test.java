import java.util.*;

// JUnit-free test harness for Answer.findMaxAverage(int[] nums, int k)
// Maximum Average Subarray I. Compile: javac Answer.java Test.java   Run: java Test
public class Test {

    static int pass = 0;
    static int total = 0;
    static final double EPS = 1e-5;

    static void check(String name, int[] nums, int k, double expected) {
        total++;
        try {
            double actual = new Answer().findMaxAverage(nums, k);
            if (Math.abs(actual - expected) < EPS) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + " | nums=" + brief(nums) + " k=" + k
                        + " | expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | nums=" + brief(nums) + " k=" + k
                    + " | expected=" + expected + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    // Property check for large inputs: compare against an independent O(n) oracle.
    static void checkProp(String name, int[] nums, int k) {
        total++;
        try {
            long t0 = System.nanoTime();
            double actual = new Answer().findMaxAverage(nums, k);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1e6;
            double expected = oracle(nums, k);
            String label = name + " [" + String.format("%.1f", ms) + " ms]";
            if (ms > 3000.0) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | TOO SLOW (>3000ms) possible quadratic");
            } else if (Math.abs(actual - expected) < 1e-4) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + " | expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | n=" + nums.length + " k=" + k
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent O(n) oracle: max window sum / k.
    static double oracle(int[] nums, int k) {
        long sum = 0;
        for (int i = 0; i < k; i++) sum += nums[i];
        long best = sum;
        for (int i = k; i < nums.length; i++) {
            sum += nums[i] - nums[i - k];
            if (sum > best) best = sum;
        }
        return (double) best / k;
    }

    static String brief(int[] a) {
        if (a.length <= 16) return Arrays.toString(a);
        return "[len=" + a.length + ", first=" + a[0] + "...]";
    }

    public static void main(String[] args) {
        // Provided examples
        check("example1", new int[]{1, 12, -5, -6, 50, 3}, 4, 12.75);
        check("example2 single element k=1", new int[]{5}, 1, 5.0);
        check("example3", new int[]{0, 1, 1, 3, 3}, 4, 2.0);

        // Corner: single element negative
        check("single negative", new int[]{-5}, 1, -5.0);
        // Corner: single element zero
        check("single zero", new int[]{0}, 1, 0.0);

        // Corner: k == n (whole array is the only window)
        check("k equals n", new int[]{1, 2, 3, 4}, 4, 2.5);
        check("k equals n negatives", new int[]{-2, -4, -6}, 3, -4.0);

        // Corner: k == 1 (max single element)
        check("k=1 picks max", new int[]{3, -1, 7, 2}, 1, 7.0);
        check("k=1 all negative picks largest", new int[]{-9, -3, -5}, 1, -3.0);

        // Corner: all equal elements
        check("all equal", new int[]{4, 4, 4, 4, 4}, 3, 4.0);

        // Corner: all zeros
        check("all zeros", new int[]{0, 0, 0, 0}, 2, 0.0);

        // Corner: all negatives, window of 2
        check("all negatives k=2", new int[]{-1, -12, -5, -6, -50, -3}, 2, -5.5); // best window [-5,-6] avg -5.5

        // Corner: max window at the very start
        check("max at start", new int[]{10, 10, 1, 1, 1}, 2, 10.0);
        // Corner: max window at the very end
        check("max at end", new int[]{1, 1, 1, 9, 9}, 2, 9.0);

        // Corner: min bound values
        check("min bound values", new int[]{-10000, -10000, -10000}, 2, -10000.0);
        // Corner: max bound values
        check("max bound values", new int[]{10000, 10000, 0}, 2, 10000.0);

        // Corner: mixed producing fractional average
        check("fractional avg", new int[]{4, 0, 4, 0}, 3, 8.0 / 3.0);

        // Corner: negative and positive mix, window 3
        check("mixed sign window3", new int[]{-1, 2, 3, -4, 5, 6}, 3, 7.0 / 3.0);

        // Corner: larger array, sweep
        check("larger sweep", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 5, 8.0);

        // ===== ADDED CORNER CASES =====

        // Boundary: min element value across whole array, k=n
        check("min value k=n", new int[]{-10000, -10000, -10000, -10000}, 4, -10000.0);
        // Boundary: max element value across whole array, k=n
        check("max value k=n", new int[]{10000, 10000, 10000}, 3, 10000.0);
        // Boundary: full swing min to max
        check("min to max swing", new int[]{-10000, 10000}, 1, 10000.0);
        check("min to max k=2", new int[]{-10000, 10000, -10000, 10000}, 2, 0.0);

        // Single element at upper bound
        check("single max bound", new int[]{10000}, 1, 10000.0);
        // Single element at lower bound
        check("single min bound", new int[]{-10000}, 1, -10000.0);

        // Strictly increasing: best window is the last k
        check("strictly increasing", new int[]{1, 2, 3, 4, 5, 6}, 2, 5.5);
        // Strictly decreasing: best window is the first k
        check("strictly decreasing", new int[]{6, 5, 4, 3, 2, 1}, 2, 5.5);

        // Alternating high/low
        check("alternating", new int[]{10000, -10000, 10000, -10000, 10000}, 2, 0.0);
        check("alternating k=3", new int[]{10000, -10000, 10000, -10000, 10000}, 3, 10000.0 / 3.0);

        // Heavy duplicates with one spike
        check("dup with spike k=1", new int[]{2, 2, 2, 9, 2, 2}, 1, 9.0);
        check("dup with spike k=2", new int[]{2, 2, 2, 9, 2, 2}, 2, 5.5);

        // Off-by-one: k = n-1 (two windows)
        check("k = n-1", new int[]{1, 2, 3, 4, 100}, 4, 109.0 / 4.0);
        // Off-by-one: k = 2 over tie windows
        check("tie windows k=2", new int[]{3, 3, 3, 3}, 2, 3.0);

        // Best window straddling the middle
        check("best straddles middle", new int[]{0, 0, 5, 5, 0, 0}, 2, 5.0);

        // Zeros surrounding negatives, k=1 picks zero
        check("zeros and negatives k=1", new int[]{-3, 0, -2, 0, -1}, 1, 0.0);

        // Mixed with the only positive cluster in the middle
        check("positive cluster middle", new int[]{-5, -5, 4, 4, -5, -5}, 2, 4.0);

        // ===== LARGE / PERFORMANCE CASES (verified by independent O(n) oracle) =====
        Random rnd = new Random(42);

        // Large random near upper bound, mid-size window
        int n1 = 100000;
        int[] big1 = new int[n1];
        for (int i = 0; i < n1; i++) big1[i] = rnd.nextInt(20001) - 10000; // [-10000,10000]
        checkProp("large random k=1000", big1, 1000);

        // Large random, k = 1 (each element its own window)
        int[] big2 = new int[n1];
        for (int i = 0; i < n1; i++) big2[i] = rnd.nextInt(20001) - 10000;
        checkProp("large random k=1", big2, 1);

        // Large random, k = n (single window)
        int[] big3 = new int[n1];
        for (int i = 0; i < n1; i++) big3[i] = rnd.nextInt(20001) - 10000;
        checkProp("large random k=n", big3, n1);

        // Large all-equal (overflow-safe sum check)
        int[] big4 = new int[n1];
        Arrays.fill(big4, 10000);
        checkProp("large all-max k=50000", big4, 50000);

        // Large all-min
        int[] big5 = new int[n1];
        Arrays.fill(big5, -10000);
        checkProp("large all-min k=12345", big5, 12345);

        // Large increasing ramp, off-by-one k
        int[] big6 = new int[n1];
        for (int i = 0; i < n1; i++) big6[i] = (i % 20001) - 10000;
        checkProp("large ramp k=n-1", big6, n1 - 1);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
