import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (preserved) =====
        // Examples from QUESTION.md
        check("example1 [5,2,6,1]", new int[]{5, 2, 6, 1}, Arrays.asList(2, 1, 1, 0));
        check("example2 [-1,-1]", new int[]{-1, -1}, Arrays.asList(0, 0));
        check("example3 [2,0,1]", new int[]{2, 0, 1}, Arrays.asList(2, 0, 0));

        // Single element
        check("single [7]", new int[]{7}, Arrays.asList(0));
        check("single negative [-5]", new int[]{-5}, Arrays.asList(0));
        check("single zero [0]", new int[]{0}, Arrays.asList(0));

        // Two elements
        check("two ascending [1,2]", new int[]{1, 2}, Arrays.asList(0, 0));
        check("two descending [2,1]", new int[]{2, 1}, Arrays.asList(1, 0));
        check("two equal [3,3]", new int[]{3, 3}, Arrays.asList(0, 0));

        // All equal (duplicates) -> no strictly smaller
        check("all equal [4,4,4,4]", new int[]{4, 4, 4, 4}, Arrays.asList(0, 0, 0, 0));

        // Already sorted ascending -> all zeros
        check("sorted asc [1,2,3,4,5]", new int[]{1, 2, 3, 4, 5}, Arrays.asList(0, 0, 0, 0, 0));

        // Reverse sorted -> n-1, n-2, ... 0
        check("sorted desc [5,4,3,2,1]", new int[]{5, 4, 3, 2, 1}, Arrays.asList(4, 3, 2, 1, 0));

        // Negatives and zeros mixed
        check("neg/zero mix [-1,0,-2,0,1]", new int[]{-1, 0, -2, 0, 1}, brute(new int[]{-1, 0, -2, 0, 1}));

        // Duplicates with strictly-smaller semantics
        check("dups [3,1,2,2,1]", new int[]{3, 1, 2, 2, 1}, brute(new int[]{3, 1, 2, 2, 1}));
        check("dups2 [2,2,1,1]", new int[]{2, 2, 1, 1}, brute(new int[]{2, 2, 1, 1}));

        // Min/max bounds from constraints (-10^4 .. 10^4)
        check("bounds [10000,-10000,0]", new int[]{10000, -10000, 0}, Arrays.asList(2, 0, 0));
        check("bounds mix [-10000,10000,-10000,10000]", new int[]{-10000, 10000, -10000, 10000}, brute(new int[]{-10000, 10000, -10000, 10000}));

        // Larger deterministic array verified by brute force
        int[] big = new int[60];
        int seed = 12345;
        for (int i = 0; i < big.length; i++) {
            seed = (seed * 1103515245 + 12345) & 0x7fffffff;
            big[i] = (seed % 41) - 20; // range -20..20
        }
        check("large random (brute-verified, len=60)", big, brute(big));

        // All same length guarantee (length 1 edge already covered); zigzag
        check("zigzag [1,3,2,3,1]", new int[]{1, 3, 2, 3, 1}, brute(new int[]{1, 3, 2, 3, 1}));

        // ===== NEW corner cases =====
        // Single element at both constraint extremes
        check("single max [10000]", new int[]{10000}, Arrays.asList(0));
        check("single min [-10000]", new int[]{-10000}, Arrays.asList(0));

        // Strictly increasing longer -> all zeros
        check("strict inc len8", new int[]{-10, -3, 0, 1, 4, 9, 100, 9999}, Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));

        // Strictly decreasing longer -> triangular counts
        check("strict dec len6", new int[]{6, 5, 4, 3, 2, 1}, Arrays.asList(5, 4, 3, 2, 1, 0));

        // Alternating high/low (negatives & positives)
        check("alternating [-10000,10000,-9999,9999,-1,1]",
                new int[]{-10000, 10000, -9999, 9999, -1, 1},
                brute(new int[]{-10000, 10000, -9999, 9999, -1, 1}));

        // Heavy duplicates with one outlier each end
        check("heavy dups [5,5,5,5,5,1]", new int[]{5, 5, 5, 5, 5, 1}, brute(new int[]{5, 5, 5, 5, 5, 1}));
        check("heavy dups2 [1,5,5,5,5,5]", new int[]{1, 5, 5, 5, 5, 5}, brute(new int[]{1, 5, 5, 5, 5, 5}));

        // All negative descending
        check("all neg desc [-1,-2,-3,-4]", new int[]{-1, -2, -3, -4}, Arrays.asList(3, 2, 1, 0));

        // All negative equal
        check("all neg equal [-7,-7,-7]", new int[]{-7, -7, -7}, Arrays.asList(0, 0, 0));

        // All zeros
        check("all zeros [0,0,0,0,0]", new int[]{0, 0, 0, 0, 0}, Arrays.asList(0, 0, 0, 0, 0));

        // Smallest value at the very front -> 0 (nothing smaller after a global min at front)
        check("min at front [-10000,3,2,1]", new int[]{-10000, 3, 2, 1}, brute(new int[]{-10000, 3, 2, 1}));

        // Largest value at the very end -> 0 for that element
        check("max at end [1,2,3,10000]", new int[]{1, 2, 3, 10000}, brute(new int[]{1, 2, 3, 10000}));

        // Off-by-one shape: two distinct values, blocks
        check("blocks [9,9,9,1,1,1]", new int[]{9, 9, 9, 1, 1, 1}, brute(new int[]{9, 9, 9, 1, 1, 1}));

        // Mixed with repeated min interleaved
        check("interleaved min [3,-10000,2,-10000,1]",
                new int[]{3, -10000, 2, -10000, 1},
                brute(new int[]{3, -10000, 2, -10000, 1}));

        // Wide coordinate spread (sparse values) -> coordinate compression stress
        check("sparse spread", new int[]{-10000, -1, 0, 1, 10000, -5000, 5000},
                brute(new int[]{-10000, -1, 0, 1, 10000, -5000, 5000}));

        // Medium random brute-verified (len=300)
        {
            java.util.Random rnd = new java.util.Random(42);
            int[] m = new int[300];
            for (int i = 0; i < m.length; i++) m[i] = rnd.nextInt(20001) - 10000;
            check("medium random (brute-verified, len=300)", m, brute(m));
        }

        // Medium random with small value range -> many ties (len=400)
        {
            java.util.Random rnd = new java.util.Random(42);
            int[] m = new int[400];
            for (int i = 0; i < m.length; i++) m[i] = rnd.nextInt(5) - 2; // -2..2
            check("medium ties random (brute-verified, len=400)", m, brute(m));
        }

        // ===== LARGE / PERFORMANCE / SCALE cases =====
        // n = 100000 near upper bound; verified by an independent O(n log n) BIT oracle.
        largeProperty("large n=100000 random (oracle-verified)", 100000, 20001, -10000, 42);
        // n = 100000 worst-case fully descending (max inversions) -> closed-form expected.
        largeDescending("large n=100000 strictly descending (closed-form)", 100000);
        // n = 80000 heavy-duplicate stress (small value range) -> oracle-verified.
        largeProperty("large n=80000 heavy dups (oracle-verified)", 80000, 11, -5, 42);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Brute-force reference: counts[i] = #{ j>i : nums[j] < nums[i] }  (O(n^2), for small/medium inputs)
    static List<Integer> brute(int[] nums) {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int c = 0;
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] < nums[i]) c++;
            }
            out.add(c);
        }
        return out;
    }

    // Independent O(n log n) oracle using a Fenwick tree (BIT) over compressed coordinates.
    static List<Integer> oracle(int[] nums) {
        int n = nums.length;
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        // compressed distinct values
        int m = 0;
        int[] uniq = new int[n];
        for (int i = 0; i < n; i++) {
            if (i == 0 || sorted[i] != sorted[i - 1]) uniq[m++] = sorted[i];
        }
        int[] bit = new int[m + 1];
        Integer[] res = new Integer[n];
        for (int i = n - 1; i >= 0; i--) {
            // rank = number of distinct values strictly less than nums[i] (1-based index of first >=)
            int lo = 0, hi = m; // find first index in uniq[0..m-1] >= nums[i]
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (uniq[mid] < nums[i]) lo = mid + 1; else hi = mid;
            }
            // count of seen values with rank in [1..lo] (i.e. strictly smaller than nums[i])
            int cnt = 0;
            for (int x = lo; x > 0; x -= x & (-x)) cnt += bit[x];
            res[i] = cnt;
            // position of nums[i] in uniq (lo is first >= nums[i]; since nums[i] exists, uniq[lo]==nums[i])
            int pos = lo + 1; // 1-based
            for (int x = pos; x <= m; x += x & (-x)) bit[x]++;
        }
        return Arrays.asList(res);
    }

    // Build a big random array, time the call, verify against the oracle, fail only on a generous budget.
    static void largeProperty(String name, int n, int span, int base, long seedVal) {
        total++;
        try {
            java.util.Random rnd = new java.util.Random(seedVal);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(span) + base;
            List<Integer> expected = oracle(a);
            long t0 = System.nanoTime();
            List<Integer> actual = new Answer().countSmaller(a.clone());
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            String label = name + " (n=" + n + ", " + String.format(Locale.US, "%.1f", ms) + " ms)";
            if (actual == null) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " returned null");
            } else if (actual.size() != n) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " size=" + actual.size() + " expected size=" + n);
            } else if (!expected.equals(actual)) {
                int mismatch = firstMismatch(expected, actual);
                System.out.println("\033[31m[FAIL]\033[0m " + label + " first mismatch at i=" + mismatch
                        + " expected=" + expected.get(mismatch) + " actual=" + actual.get(mismatch));
            } else if (ms > 3000.0) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " exceeded 3000 ms budget (likely quadratic)");
            } else {
                System.out.println("\033[32m[PASS]\033[0m " + label);
                pass++;
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Strictly descending distinct values -> counts[i] = n-1-i exactly (closed form, max inversions).
    static void largeDescending(String name, int n) {
        total++;
        try {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = n - i; // strictly descending, distinct
            long t0 = System.nanoTime();
            List<Integer> actual = new Answer().countSmaller(a.clone());
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            String label = name + " (n=" + n + ", " + String.format(Locale.US, "%.1f", ms) + " ms)";
            boolean ok = actual != null && actual.size() == n;
            if (ok) {
                for (int i = 0; i < n; i++) {
                    if (actual.get(i) != n - 1 - i) { ok = false; break; }
                }
            }
            if (!ok) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " output is not the closed-form descending profile");
            } else if (ms > 3000.0) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " exceeded 3000 ms budget (likely quadratic)");
            } else {
                System.out.println("\033[32m[PASS]\033[0m " + label);
                pass++;
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static int firstMismatch(List<Integer> a, List<Integer> b) {
        int n = Math.min(a.size(), b.size());
        for (int i = 0; i < n; i++) if (!a.get(i).equals(b.get(i))) return i;
        return n;
    }

    static void check(String name, int[] input, List<Integer> expected) {
        total++;
        try {
            List<Integer> actual = new Answer().countSmaller(input.clone());
            if (expected.equals(actual)) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + "  input=" + brief(input)
                        + " expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + "  input=" + brief(input)
                    + " expected=" + expected + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    // Avoid dumping huge arrays in failure messages.
    static String brief(int[] a) {
        if (a.length <= 32) return Arrays.toString(a);
        return "[len=" + a.length + ", first8=" + Arrays.toString(Arrays.copyOf(a, 8)) + "...]";
    }
}
