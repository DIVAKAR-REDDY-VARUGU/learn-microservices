import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        // Example 1
        check(new int[]{7,1,5,3,6,4}, 7);
        // Example 2: monotonically increasing
        check(new int[]{1,2,3,4,5}, 4);
        // Example 3: monotonically decreasing -> no profit
        check(new int[]{7,6,4,3,1}, 0);

        // Single element -> no transaction possible
        check(new int[]{5}, 0);
        // Two elements rising
        check(new int[]{1,5}, 4);
        // Two elements falling
        check(new int[]{5,1}, 0);
        // Two equal elements
        check(new int[]{3,3}, 0);
        // All equal -> 0
        check(new int[]{4,4,4,4}, 0);
        // Zeros only
        check(new int[]{0,0,0}, 0);
        // Contains zeros and a rise
        check(new int[]{0,0,5}, 5);
        // Up-down-up zigzag: capture each rise (1->4)=3 + (2->8)=6 = 9
        check(new int[]{1,4,2,8}, 9);
        // Classic zigzag every other day
        check(new int[]{1,2,1,2,1,2}, 3);
        // Peak in the middle
        check(new int[]{2,5,1}, 3);
        // Valley in the middle
        check(new int[]{5,1,5}, 4);
        // Long increasing run sum of all gains
        check(new int[]{1,3,6,10}, 9);
        // Plateau then rise: equal days contribute 0
        check(new int[]{3,3,3,7}, 4);
        // Rise then plateau then fall
        check(new int[]{1,5,5,5,2}, 4);
        // Max-bound values
        check(new int[]{0,10000,0,10000}, 20000);
        // Mixed realistic
        check(new int[]{6,1,3,2,4,7}, 7);

        // ===== NEW corner cases =====
        // Constraint min: single element at value boundary 0
        check(new int[]{0}, 0);
        // Single element at max value boundary
        check(new int[]{10000}, 0);
        // Two elements both at max bound, equal -> 0
        check(new int[]{10000,10000}, 0);
        // Min to max in one step -> full 10000 gain
        check(new int[]{0,10000}, 10000);
        // Max to min -> no profit
        check(new int[]{10000,0}, 0);
        // Strictly increasing across full value range -> sum of gains = last-first
        check(new int[]{0,1,2,3,9999,10000}, 10000);
        // Strictly decreasing -> 0
        check(new int[]{10000,9999,3,2,1,0}, 0);
        // Pure alternating 0/10000 (max swing each step) length 7: 3 full rises
        check(new int[]{0,10000,0,10000,0,10000,0}, 30000);
        // Heavy duplicates with one rise at the end
        check(new int[]{5,5,5,5,5,5,5,5,9}, 4);
        // Heavy duplicates with a dip then recovery: (5->5)=0 ... dip to 1 then up to 5 = 4
        check(new int[]{5,5,5,1,5,5,5}, 4);
        // Off-by-one shape: rise of exactly 1 at the very last index
        check(new int[]{3,3,3,3,4}, 1);
        // Off-by-one shape: rise of exactly 1 at the very first step then flat
        check(new int[]{3,4,4,4,4}, 1);
        // Sawtooth: every up captured, downs ignored. ups: (1->3)=2,(2->5)=3,(4->6)=2 = 7
        check(new int[]{1,3,2,5,4,6}, 7);
        // No-solution style: long flat plateau in the middle, net flat -> 0
        check(new int[]{2,2,2,2,2,2}, 0);
        // Big single climb broken by tiny dips: 1->2(+1),1->3(+2),2->4(+2) = 5
        check(new int[]{1,2,1,3,2,4}, 5);
        // Descending then one terminal spike to max
        check(new int[]{9,8,7,6,10000}, 9994);
        // Long flat at max then drop to 0 at end -> 0
        check(new int[]{10000,10000,10000,10000,0}, 0);
        // Many small equal-step rises summing up: 0,2,4,6,8,10 -> 10
        check(new int[]{0,2,4,6,8,10}, 10);

        // ===== Property-based cross-check on randomized small inputs (oracle = brute DP) =====
        Random rndSmall = new Random(42);
        for (int t = 0; t < 30; t++) {
            int n = 1 + rndSmall.nextInt(12);          // length 1..12
            int[] p = new int[n];
            for (int i = 0; i < n; i++) p[i] = rndSmall.nextInt(20); // small values 0..19
            int expected = bruteDp(p);
            check(p, expected);
        }

        // ===== LARGE / PERFORMANCE cases =====
        // Verified by an independent O(n) greedy oracle + timed under a generous 3000ms budget.
        largeCase(30000, 10000);   // n at the constraint upper bound, full value range
        largeCase(100000, 10000);  // n beyond constraint (stress) — algorithm must stay O(n)
        largeCaseMonotoneUp(30000);   // strictly non-decreasing big input -> profit = last - first
        largeCaseMonotoneDown(30000); // strictly non-increasing big input -> profit 0
        largeCaseAllEqual(50000);     // huge flat input -> profit 0

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // ---- exact-match check ----
    static void check(int[] prices, int expected) {
        total++;
        int[] inputCopy = prices == null ? null : prices.clone();
        try {
            int actual = new Answer().maxProfit(prices);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m maxProfit(" + shortArr(inputCopy) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m maxProfit(" + shortArr(inputCopy) + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m maxProfit(" + shortArr(inputCopy) + ") threw "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // ---- timed large case, verified against the O(n) greedy oracle ----
    static void largeCase(int n, int maxVal) {
        total++;
        String label = "large(random n=" + n + ", maxVal=" + maxVal + ", seed=42)";
        try {
            Random rnd = new Random(42);
            int[] p = new int[n];
            for (int i = 0; i < n; i++) p[i] = rnd.nextInt(maxVal + 1);
            int expected = greedyOracle(p);
            long t0 = System.nanoTime();
            int actual = new Answer().maxProfit(p);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            evalLarge(label, expected, actual, ms);
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static void largeCaseMonotoneUp(int n) {
        total++;
        String label = "large(monotone-up n=" + n + ")";
        try {
            int[] p = new int[n];
            // non-decreasing, kept within 0..10000 by repeating values when needed
            for (int i = 0; i < n; i++) p[i] = (int) ((long) i * 10000 / Math.max(1, n - 1));
            int expected = p[n - 1] - p[0]; // sum of all non-negative steps for a non-decreasing array
            long t0 = System.nanoTime();
            int actual = new Answer().maxProfit(p);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            evalLarge(label, expected, actual, ms);
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static void largeCaseMonotoneDown(int n) {
        total++;
        String label = "large(monotone-down n=" + n + ")";
        try {
            int[] p = new int[n];
            for (int i = 0; i < n; i++) p[i] = (int) ((long) (n - 1 - i) * 10000 / Math.max(1, n - 1));
            int expected = 0; // never profitable
            long t0 = System.nanoTime();
            int actual = new Answer().maxProfit(p);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            evalLarge(label, expected, actual, ms);
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static void largeCaseAllEqual(int n) {
        total++;
        String label = "large(all-equal n=" + n + ")";
        try {
            int[] p = new int[n];
            Arrays.fill(p, 7777);
            int expected = 0;
            long t0 = System.nanoTime();
            int actual = new Answer().maxProfit(p);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            evalLarge(label, expected, actual, ms);
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // shared evaluation for large cases: correctness AND time budget
    static void evalLarge(String label, int expected, int actual, double ms) {
        String tag = label + " elapsed=" + String.format(Locale.ROOT, "%.2f", ms) + "ms";
        if (actual != expected) {
            System.out.println("\033[31m[FAIL]\033[0m " + tag + " expected=" + expected + " actual=" + actual);
        } else if (ms > 3000.0) {
            System.out.println("\033[31m[FAIL]\033[0m " + tag + " over 3000ms budget (result=" + actual + ")");
        } else {
            pass++;
            System.out.println("\033[32m[PASS]\033[0m " + tag + " result=" + actual);
        }
    }

    // ---- O(n) greedy oracle: sum of positive day-over-day gains ----
    static int greedyOracle(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            int d = prices[i] - prices[i - 1];
            if (d > 0) profit += d;
        }
        return profit;
    }

    // ---- independent brute-force DP oracle (unbounded transactions) for small inputs ----
    // dp over states: hold = max cash while holding a share, free = max cash while not holding.
    static int bruteDp(int[] prices) {
        int free = 0, hold = Integer.MIN_VALUE;
        for (int price : prices) {
            int newHold = Math.max(hold, free - price);
            int newFree = Math.max(free, hold + price);
            hold = newHold;
            free = newFree;
        }
        return free;
    }

    // compact array printer so large inputs do not flood output
    static String shortArr(int[] a) {
        if (a == null) return "null";
        if (a.length <= 16) return Arrays.toString(a);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 8; i++) sb.append(a[i]).append(", ");
        sb.append("... (").append(a.length).append(" elems) ..., ");
        for (int i = a.length - 4; i < a.length; i++) {
            sb.append(a[i]);
            if (i < a.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
