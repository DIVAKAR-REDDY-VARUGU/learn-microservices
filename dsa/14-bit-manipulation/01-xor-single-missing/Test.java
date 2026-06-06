import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ---- existing cases (kept) ----
        check(new int[]{1}, 1);
        check(new int[]{2, 2, 1}, 1);
        check(new int[]{4, 1, 2, 1, 2}, 4);
        check(new int[]{1, 2, 2}, 1);
        check(new int[]{2, 1, 2}, 1);
        check(new int[]{0, 1, 1}, 0);
        check(new int[]{0, 0, 7}, 7);
        check(new int[]{-1, -1, -2}, -2);
        check(new int[]{-5, 3, 3}, -5);
        check(new int[]{-7, 4, 0, 4, 0}, -7);
        check(new int[]{30000, 30000, -30000}, -30000);
        check(new int[]{-30000, -30000, 30000}, 30000);
        check(new int[]{-30000}, -30000);
        check(new int[]{30000}, 30000);
        check(new int[]{5, 6, 6, 7, 7, 8, 8, 5, 99}, 99);
        check(new int[]{8, 8, 9, 9, 10}, 10);
        check(new int[]{17, 23, 17, 42, 23}, 42);

        // ---- new corner cases ----
        // single is 0 with many other pairs around it
        check(new int[]{3, 3, 0, 5, 5}, 0);
        // single appears last
        check(new int[]{6, 6, 9, 9, 13}, 13);
        // all values negative
        check(new int[]{-4, -4, -9, -9, -11}, -11);
        // single is the only positive among negatives
        check(new int[]{-2, -2, -3, -3, 1}, 1);
        // both boundary pairs present, single = 0 (min XOR distinctness)
        check(new int[]{30000, 30000, -30000, -30000, 0}, 0);
        // heavy duplicates: odd count of a single value
        check(new int[]{7, 7, 7, 7, 7}, 7);
        // many distinct pairs, single in the middle
        check(new int[]{1, 2, 3, 4, 5, 1, 2, 3, 4}, 5);
        // single equals a high power-of-two value
        check(new int[]{16, 16, 32, 32, 64}, 64);
        // single first, then pairs
        check(new int[]{12345, 9, 9, 8, 8}, 12345);
        // negative single surrounded by positive pairs
        check(new int[]{100, 200, 100, 200, -50}, -50);
        // value 1 single, pairs of large numbers
        check(new int[]{29999, 29999, 28888, 28888, 1}, 1);
        // single is max boundary among many pairs
        check(buildSingle(new int[]{-3, -3, 11, 11, -7, -7}, 30000), 30000);
        // single is min boundary among many pairs
        check(buildSingle(new int[]{2, 2, 4, 4, 6, 6}, -30000), -30000);

        // ---- large / performance / scale cases ----
        largeRandom(30000);   // 60001 elements
        largeRandom(50000);   // 100001 elements
        largeSequential(30000);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // append a single value to an existing all-paired prefix
    static int[] buildSingle(int[] pairedPrefix, int single) {
        int[] out = Arrays.copyOf(pairedPrefix, pairedPrefix.length + 1);
        out[out.length - 1] = single;
        return out;
    }

    // independent O(n) XOR oracle (defines the correct answer)
    static int xorAll(int[] a) {
        int x = 0;
        for (int v : a) x ^= v;
        return x;
    }

    // Build numPairs random pairs + 1 single, shuffled; verify by property + timing.
    static void largeRandom(int numPairs) {
        total++;
        String label = "largeRandom(pairs=" + numPairs + ")";
        try {
            Random rnd = new Random(42);
            int n = numPairs * 2 + 1;
            int[] arr = new int[n];
            int idx = 0;
            int single = rnd.nextInt(60001) - 30000;
            for (int i = 0; i < numPairs; i++) {
                int v = rnd.nextInt(60001) - 30000;
                arr[idx++] = v;
                arr[idx++] = v;
            }
            arr[idx++] = single;
            // Fisher-Yates shuffle with the same seeded rnd
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int t = arr[i]; arr[i] = arr[j]; arr[j] = t;
            }
            int expected = xorAll(arr); // oracle: XOR of everything == the single
            long t0 = System.nanoTime();
            int actual = new Answer().singleNumber(arr);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = (actual == expected);
            if (ok && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + ms + " ms)");
            } else if (!ok) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected " + expected + " but got " + actual + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + ms + " ms (>3000)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // pairs 0,0,1,1,...,m-1,m-1 plus a single, sequentially built then verified.
    static void largeSequential(int m) {
        total++;
        String label = "largeSequential(m=" + m + ")";
        try {
            int n = m * 2 + 1;
            int[] arr = new int[n];
            int idx = 0;
            for (int v = 0; v < m; v++) { arr[idx++] = v % 30001; arr[idx++] = v % 30001; }
            int single = -30000;
            arr[idx++] = single;
            int expected = xorAll(arr);
            long t0 = System.nanoTime();
            int actual = new Answer().singleNumber(arr);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = (actual == expected);
            if (ok && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + ms + " ms)");
            } else if (!ok) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected " + expected + " but got " + actual + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + ms + " ms (>3000)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void check(int[] input, int expected) {
        total++;
        String in = Arrays.toString(input);
        try {
            int actual = new Answer().singleNumber(input);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m singleNumber(" + in + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m singleNumber(" + in + ") expected " + expected + " but got " + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m singleNumber(" + in + ") expected " + expected
                    + " but threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
