import java.util.*;

// NOTE: MedianFinder is defined as a (nested) class on Answer per the stub/QUESTION.md.
// This test accesses it as an inner class:  new Answer().new MedianFinder().
// If you instead make MedianFinder a top-level/static class, change the construction below.
public class Test {
    static int pass = 0;
    static int total = 0;
    static final double EPS = 1e-5;

    public static void main(String[] args) {
        // ---- existing cases (kept) ----
        // doc example 1: addNum(1); addNum(2); findMedian() -> 1.5; then addNum(3); findMedian() -> 2.0
        runSequence("doc example 1+2 (even then odd)",
                new int[]{1,2,3},
                new int[]{2,3},                 // query after 2 nums, then after 3 nums
                new double[]{1.5, 2.0});

        // doc example 3: 6,10,2,6 -> sorted [2,6,6,10] median (6+6)/2 = 6.0
        runSequence("doc example 3 (even count)",
                new int[]{6,10,2,6},
                new int[]{4},
                new double[]{6.0});

        runSequence("single element (odd)",
                new int[]{42},
                new int[]{1},
                new double[]{42.0});

        runSequence("two elements (even)",
                new int[]{2,3},
                new int[]{2},
                new double[]{2.5});

        runSequence("negatives",
                new int[]{-5,-1,-3},
                new int[]{3},
                new double[]{-3.0});

        runSequence("duplicates all equal",
                new int[]{4,4,4,4},
                new int[]{4},
                new double[]{4.0});

        runSequence("min/max bounds even",
                new int[]{-100000,100000},
                new int[]{2},
                new double[]{0.0});

        runSequence("running medians as stream grows",
                new int[]{5,15,1,3},
                new int[]{1,2,3,4},
                new double[]{5.0, 10.0, 5.0, 4.0});

        runSequence("descending insert order",
                new int[]{9,7,5,3,1},
                new int[]{5},
                new double[]{5.0});

        // ---- NEW corner cases ----
        runSequence("single min bound element",
                new int[]{-100000}, new int[]{1}, new double[]{-100000.0});
        runSequence("single max bound element",
                new int[]{100000}, new int[]{1}, new double[]{100000.0});
        runSequence("single zero",
                new int[]{0}, new int[]{1}, new double[]{0.0});
        runSequence("two min/max bound -> .5 fraction",
                new int[]{-100000,99999}, new int[]{2}, new double[]{-0.5});
        runSequence("strictly increasing, query each step",
                new int[]{1,2,3,4,5},
                new int[]{1,2,3,4,5},
                new double[]{1.0, 1.5, 2.0, 2.5, 3.0});
        runSequence("strictly decreasing, query each step",
                new int[]{5,4,3,2,1},
                new int[]{1,2,3,4,5},
                new double[]{5.0, 4.5, 4.0, 3.5, 3.0});
        runSequence("all equal large block",
                new int[]{7,7,7,7,7,7,7}, new int[]{7}, new double[]{7.0});
        runSequence("alternating high/low",
                new int[]{1,100,2,99,3,98},
                new int[]{6}, new double[]{50.5});
        runSequence("insert ascending then median of 6 (even)",
                new int[]{10,20,30,40,50,60},
                new int[]{6}, new double[]{35.0});
        runSequence("negatives and positives mix",
                new int[]{-3,-2,-1,1,2,3},
                new int[]{6}, new double[]{0.0});
        runSequence("heavy duplicates with one outlier",
                new int[]{5,5,5,5,1000},
                new int[]{5}, new double[]{5.0});
        runSequence("median moves across duplicates",
                new int[]{2,2,2,8,8},
                new int[]{1,3,5}, new double[]{2.0, 2.0, 2.0});
        runSequence("two-element steps even-only queries",
                new int[]{4,8,2,6},
                new int[]{2,4}, new double[]{6.0, 5.0});
        runSequence("all negative descending",
                new int[]{-1,-2,-3,-4,-5,-6},
                new int[]{6}, new double[]{-3.5});
        runSequence("odd then even then odd progression",
                new int[]{50,10,30,20,40},
                new int[]{1,2,3,4,5}, new double[]{50.0, 30.0, 30.0, 25.0, 30.0});
        runSequence("zeros and bounds interleaved",
                new int[]{0,-100000,100000,0},
                new int[]{4}, new double[]{0.0});

        // ---- LARGE / PERFORMANCE / SCALE cases (oracle-verified, brute force on sorted list) ----
        // constraint: up to 5*10^4 calls. Use ~50000 addNum with periodic queries.
        largeStream("large random stream n=50000, query every 1000", 50000, 1000);
        largeStream("large sorted-ascending stream n=50000", -50000, 1000);   // negative size flag => ascending
        largeStream("large all-equal stream n=50000", -1, 1000);             // -1 flag => all equal

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // adds nums[0..]; after inserting the i-th number (1-based) given in queryAfter,
    // calls findMedian() and compares to the corresponding expected value.
    static void runSequence(String label, int[] nums, int[] queryAfter, double[] expected) {
        total++;
        try {
            Answer.MedianFinder mf = new Answer().new MedianFinder();
            List<Double> got = new ArrayList<>();
            int qi = 0;
            for (int i = 0; i < nums.length; i++) {
                mf.addNum(nums[i]);
                if (qi < queryAfter.length && queryAfter[qi] == (i + 1)) {
                    got.add(mf.findMedian());
                    qi++;
                }
            }
            boolean ok = got.size() == expected.length;
            for (int i = 0; ok && i < expected.length; i++) {
                if (Math.abs(got.get(i) - expected[i]) >= EPS) { ok = false; }
            }
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + " | nums=" + Arrays.toString(nums)
                        + " | queryAfter=" + Arrays.toString(queryAfter)
                        + " | expected=" + Arrays.toString(expected)
                        + " | actual=" + got);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label
                    + " | nums=" + Arrays.toString(nums)
                    + " | expected=" + Arrays.toString(expected)
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // brute-force oracle median over a growing multiset (kept sorted) -- O(n^2) total
    // but we only re-sort when querying, and queries are sparse, so it stays fast enough
    // for the oracle side; the SOLUTION side is what we time.
    static double oracleMedian(int[] vals, int count) {
        int[] copy = Arrays.copyOf(vals, count);
        Arrays.sort(copy);
        if (count % 2 == 1) return copy[count / 2];
        return ((double) copy[count / 2 - 1] + (double) copy[count / 2]) / 2.0;
    }

    // sizeFlag: positive => random in [-100000,100000];
    //           negative magnitude with special meaning:
    //             -1 => all-equal stream of size 50000;
    //             other negative => ascending stream of that magnitude.
    static void largeStream(String label, int sizeFlag, int queryEvery) {
        total++;
        try {
            Random rnd = new Random(42);
            int n;
            int mode; // 0 random, 1 ascending, 2 all-equal
            if (sizeFlag > 0) { n = sizeFlag; mode = 0; }
            else if (sizeFlag == -1) { n = 50000; mode = 2; }
            else { n = -sizeFlag; mode = 1; }

            int[] vals = new int[n];
            for (int i = 0; i < n; i++) {
                if (mode == 0) vals[i] = rnd.nextInt(200001) - 100000;
                else if (mode == 1) vals[i] = -100000 + (int) ((200000L * i) / n);
                else vals[i] = 12345;
            }

            Answer.MedianFinder mf = new Answer().new MedianFinder();
            boolean ok = true;
            long elapsed = 0L;
            for (int i = 0; i < n; i++) {
                long t0 = System.nanoTime();
                mf.addNum(vals[i]);
                elapsed += System.nanoTime() - t0;
                int cnt = i + 1;
                if (cnt % queryEvery == 0 || cnt == n) {
                    long q0 = System.nanoTime();
                    double got = mf.findMedian();
                    elapsed += System.nanoTime() - q0;
                    double exp = oracleMedian(vals, cnt);
                    if (Math.abs(got - exp) >= EPS) {
                        ok = false;
                        System.out.println("\033[31m[FAIL]\033[0m " + label
                                + " | at count=" + cnt + " expected=" + exp + " got=" + got);
                        break;
                    }
                }
            }
            long elapsedMs = elapsed / 1_000_000;
            String lab = label + " (n=" + n + ") [" + elapsedMs + " ms]";
            if (elapsedMs > 3000) ok = false;
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + lab);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + lab + " | property/median mismatch or too slow");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
