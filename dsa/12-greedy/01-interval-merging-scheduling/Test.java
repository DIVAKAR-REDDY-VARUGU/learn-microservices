import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept verbatim in intent) =====
        // Example 1
        check(new int[][]{{1,3},{2,6},{8,10},{15,18}}, new int[][]{{1,6},{8,10},{15,18}});
        // Example 2: touching intervals merge
        check(new int[][]{{1,4},{4,5}}, new int[][]{{1,5}});
        // Example 3: unsorted, fully contained
        check(new int[][]{{1,4},{0,4}}, new int[][]{{0,4}});

        // Single interval
        check(new int[][]{{5,7}}, new int[][]{{5,7}});
        // Single point interval
        check(new int[][]{{3,3}}, new int[][]{{3,3}});
        // Two disjoint intervals (already sorted)
        check(new int[][]{{1,2},{4,6}}, new int[][]{{1,2},{4,6}});
        // Two disjoint intervals reverse-sorted in input
        check(new int[][]{{4,6},{1,2}}, new int[][]{{1,2},{4,6}});
        // All identical intervals (duplicates)
        check(new int[][]{{2,5},{2,5},{2,5}}, new int[][]{{2,5}});
        // One interval fully contains another
        check(new int[][]{{1,10},{2,3},{4,5}}, new int[][]{{1,10}});
        // Chain that merges into one big interval
        check(new int[][]{{1,3},{3,5},{5,7},{7,9}}, new int[][]{{1,9}});
        // Nested + separate
        check(new int[][]{{1,4},{2,3},{6,8},{7,9}}, new int[][]{{1,4},{6,9}});
        // Already sorted, no overlaps
        check(new int[][]{{0,1},{2,3},{4,5}}, new int[][]{{0,1},{2,3},{4,5}});
        // Zero-based bounds and equal-start intervals
        check(new int[][]{{0,0},{0,1},{0,2}}, new int[][]{{0,2}});
        // Overlap by exactly one point at the seam
        check(new int[][]{{1,5},{5,10},{10,15}}, new int[][]{{1,15}});
        // Many small, none overlapping, scrambled order
        check(new int[][]{{8,9},{1,2},{5,6},{3,4}}, new int[][]{{1,2},{3,4},{5,6},{8,9}});
        // Max-bound large numbers
        check(new int[][]{{9990,10000},{9995,9999}}, new int[][]{{9990,10000}});
        // Mix: some merge, some stay
        check(new int[][]{{2,4},{1,3},{6,7},{8,10},{9,12}}, new int[][]{{1,4},{6,7},{8,12}});

        // ===== NEW corner cases =====
        // Constraint min: smallest valid length (1) at the minimum coordinate (0,0)
        check(new int[][]{{0,0}}, new int[][]{{0,0}});
        // Constraint max coordinate: single point at upper bound 10^4
        check(new int[][]{{10000,10000}}, new int[][]{{10000,10000}});
        // Full-span interval covering the entire allowed coordinate range
        check(new int[][]{{0,10000}}, new int[][]{{0,10000}});
        // One full-span interval swallows everything else
        check(new int[][]{{0,10000},{1,2},{5000,5000},{9999,10000}}, new int[][]{{0,10000}});
        // All-equal: heavy duplicates of a single-point interval
        check(new int[][]{{7,7},{7,7},{7,7},{7,7},{7,7}}, new int[][]{{7,7}});
        // Strictly increasing disjoint intervals (no merges, already sorted)
        check(new int[][]{{0,1},{3,4},{6,7},{9,10}}, new int[][]{{0,1},{3,4},{6,7},{9,10}});
        // Strictly decreasing input order, disjoint -> sorted output
        check(new int[][]{{9,10},{6,7},{3,4},{0,1}}, new int[][]{{0,1},{3,4},{6,7},{9,10}});
        // Off-by-one: gap of exactly 1 means NO merge (closed intervals, not touching)
        check(new int[][]{{1,2},{3,4}}, new int[][]{{1,2},{3,4}});
        // Off-by-one: touching at a single shared endpoint DOES merge
        check(new int[][]{{1,2},{2,3}}, new int[][]{{1,3}});
        // Alternating widths, interleaved starts that all chain together
        check(new int[][]{{1,2},{1,9},{3,4},{2,3},{8,8}}, new int[][]{{1,9}});
        // Many single-point intervals at consecutive integers: none touch (gap 1)
        check(new int[][]{{0,0},{1,1},{2,2},{3,3},{4,4}},
              new int[][]{{0,0},{1,1},{2,2},{3,3},{4,4}});
        // Heavy duplicates mixed with a couple of distinct merges
        check(new int[][]{{2,5},{2,5},{2,5},{5,8},{2,5},{8,8}}, new int[][]{{2,8}});
        // Zeros: several intervals all starting at 0 with growing ends
        check(new int[][]{{0,3},{0,1},{0,5},{0,2}}, new int[][]{{0,5}});
        // Two big blocks separated by a gap, each block internally fully merging
        check(new int[][]{{1,4},{2,5},{3,6},{100,200},{150,250},{120,130}},
              new int[][]{{1,6},{100,250}});
        // Nested chain where every later interval is contained in the first
        check(new int[][]{{0,100},{10,20},{30,40},{50,60},{90,99}}, new int[][]{{0,100}});
        // Interleaved overlap that resolves to two groups
        check(new int[][]{{1,5},{6,9},{2,3},{8,10},{4,7}}, new int[][]{{1,10}});

        // ===== LARGE / PERFORMANCE cases (verified by property) =====
        largeRandom("large-random-merging-N=10000",  10000, 0, 10000, 40);
        largeRandom("large-random-sparse-N=10000",   10000, 0, 10000, 1);
        largeAllOverlap("large-all-overlap-N=20000",  20000);
        largeDisjointMax("large-disjoint-max-N=5001", 5001);
        largeRandom("large-random-dense-N=50000",     50000, 0, 10000, 20);
        largeRandom("large-random-N=100000",         100000, 0, 10000, 30);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // ---------- exact-match check for unambiguous small cases ----------
    static void check(int[][] input, int[][] expected) {
        total++;
        int[][] inputCopy = deepCopy(input);
        try {
            int[][] actual = new Answer().merge(input);
            int[][] a = sortByStart(actual);
            int[][] e = sortByStart(expected);
            if (Arrays.deepEquals(a, e)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m merge(" + fmt(inputCopy) + ")");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m merge(" + fmt(inputCopy) + ") expected=" + fmt(e) + " actual=" + fmt(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m merge(" + fmt(inputCopy) + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---------- property-based large case: random intervals ----------
    // width is the max span of each generated interval; smaller => sparser.
    static void largeRandom(String label, int n, int lo, int hi, int width) {
        total++;
        Random rnd = new Random(42);
        int[][] input = new int[n][2];
        for (int i = 0; i < n; i++) {
            int s = lo + rnd.nextInt(hi - lo + 1);
            int span = rnd.nextInt(width + 1);
            int e = Math.min(hi, s + span);
            input[i][0] = s;
            input[i][1] = e;
        }
        runProperty(label, input);
    }

    // ---------- property-based large case: everything overlaps into ONE ----------
    static void largeAllOverlap(String label, int n) {
        total++;
        Random rnd = new Random(42);
        int[][] input = new int[n][2];
        // Every interval contains the midpoint 5000 -> result must be a single interval.
        for (int i = 0; i < n; i++) {
            int s = rnd.nextInt(5001);        // 0..5000
            int e = 5000 + rnd.nextInt(5001); // 5000..10000
            input[i][0] = s;
            input[i][1] = e;
        }
        int[][] inputCopy = deepCopy(input);
        try {
            long t0 = System.nanoTime();
            int[][] actual = new Answer().merge(input);
            long ms = (System.nanoTime() - t0) / 1_000_000L;
            String prob = propertyError(inputCopy, actual);
            boolean single = actual != null && actual.length == 1;
            if (prob == null && single && !timedOut(ms)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (n=" + n + ", out=1, " + ms + " ms)");
            } else {
                String why = prob != null ? prob
                           : !single ? "expected exactly 1 merged interval, got " + (actual == null ? "null" : actual.length)
                           : "elapsed " + ms + " ms exceeded budget";
                System.out.println("\033[31m[FAIL]\033[0m " + label + " (n=" + n + ", " + ms + " ms): " + why);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---------- property-based large case: forced fully-disjoint intervals ----------
    static void largeDisjointMax(String label, int n) {
        total++;
        // Intervals [2k, 2k+1] for k=0..n-1 => gaps of 1 => NONE merge.
        // Capped so 2k+1 <= 10000 to respect the coordinate constraint.
        int maxK = 10000 / 2;            // 5000
        int count = Math.min(n, maxK + 1);
        int[][] input = new int[count][2];
        for (int k = 0; k < count; k++) {
            input[k][0] = 2 * k;
            input[k][1] = 2 * k + 1;
        }
        int[][] inputCopy = deepCopy(input);
        try {
            long t0 = System.nanoTime();
            int[][] actual = new Answer().merge(input);
            long ms = (System.nanoTime() - t0) / 1_000_000L;
            String prob = propertyError(inputCopy, actual);
            boolean allKept = actual != null && actual.length == count;
            if (prob == null && allKept && !timedOut(ms)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (n=" + count + ", out=" + actual.length + ", " + ms + " ms)");
            } else {
                String why = prob != null ? prob
                           : !allKept ? "expected " + count + " intervals (none merge), got " + (actual == null ? "null" : actual.length)
                           : "elapsed " + ms + " ms exceeded budget";
                System.out.println("\033[31m[FAIL]\033[0m " + label + " (n=" + count + ", " + ms + " ms): " + why);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Run merge() on a large input, time it, and verify the result by PROPERTY
    // (sorted, non-overlapping output that covers exactly the same point-set as input).
    static void runProperty(String label, int[][] input) {
        int n = input.length;
        int[][] inputCopy = deepCopy(input);
        try {
            long t0 = System.nanoTime();
            int[][] actual = new Answer().merge(input);
            long ms = (System.nanoTime() - t0) / 1_000_000L;
            String prob = propertyError(inputCopy, actual);
            if (prob == null && !timedOut(ms)) {
                pass++;
                int outLen = actual == null ? 0 : actual.length;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (n=" + n + ", out=" + outLen + ", " + ms + " ms)");
            } else {
                String why = prob != null ? prob : "elapsed " + ms + " ms exceeded budget";
                System.out.println("\033[31m[FAIL]\033[0m " + label + " (n=" + n + ", " + ms + " ms): " + why);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static boolean timedOut(long ms) { return ms > 3000; }

    // Returns null if `out` is a valid merge of `in`, else a human-readable reason.
    // Valid means: well-formed; each interval start<=end; sorted output is internally
    // non-overlapping (gap > 0 between consecutive); and the union of `out` equals the
    // union of `in`. We verify coverage with an independent O(n log n) sweep oracle.
    static String propertyError(int[][] in, int[][] out) {
        if (out == null) return "output is null";
        for (int i = 0; i < out.length; i++) {
            if (out[i] == null || out[i].length != 2) return "malformed output interval at index " + i;
            if (out[i][0] > out[i][1]) return "output interval has start>end: " + Arrays.toString(out[i]);
        }
        int[][] s = sortByStart(out);
        // No two output intervals may overlap or touch (touching ones should have merged).
        for (int i = 1; i < s.length; i++) {
            if (s[i][0] <= s[i - 1][1]) {
                return "output intervals overlap/touch and should be merged: "
                        + Arrays.toString(s[i - 1]) + " & " + Arrays.toString(s[i]);
            }
        }
        // Independent oracle: compute the canonical merge of the input and compare.
        int[][] oracle = oracleMerge(in);
        if (!Arrays.deepEquals(s, oracle)) {
            return "output does not equal canonical merge (out=" + fmt(s) + ", oracle=" + fmt(oracle) + ")";
        }
        return null;
    }

    // Independent reference implementation used only to verify outputs.
    static int[][] oracleMerge(int[][] in) {
        if (in == null || in.length == 0) return new int[0][];
        int[][] arr = deepCopy(in);
        Arrays.sort(arr, (x, y) -> {
            if (x[0] != y[0]) return Integer.compare(x[0], y[0]);
            return Integer.compare(x[1], y[1]);
        });
        List<int[]> res = new ArrayList<>();
        int curS = arr[0][0], curE = arr[0][1];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i][0] <= curE) {            // overlap or touch
                curE = Math.max(curE, arr[i][1]);
            } else {
                res.add(new int[]{curS, curE});
                curS = arr[i][0];
                curE = arr[i][1];
            }
        }
        res.add(new int[]{curS, curE});
        return res.toArray(new int[0][]);
    }

    // ---------- shared helpers ----------
    static int[][] deepCopy(int[][] in) {
        if (in == null) return null;
        int[][] out = new int[in.length][];
        for (int i = 0; i < in.length; i++) out[i] = in[i] == null ? null : in[i].clone();
        return out;
    }

    static int[][] sortByStart(int[][] in) {
        if (in == null) return null;
        int[][] out = deepCopy(in);
        Arrays.sort(out, (x, y) -> {
            if (x[0] != y[0]) return Integer.compare(x[0], y[0]);
            return Integer.compare(x[1], y[1]);
        });
        return out;
    }

    static String fmt(int[][] in) {
        if (in == null) return "null";
        StringBuilder sb = new StringBuilder("[");
        int limit = Math.min(in.length, 12);
        for (int i = 0; i < limit; i++) {
            if (i > 0) sb.append(",");
            sb.append(in[i] == null ? "null" : Arrays.toString(in[i]));
        }
        if (in.length > limit) sb.append(",...(" + in.length + " total)");
        return sb.append("]").toString();
    }
}