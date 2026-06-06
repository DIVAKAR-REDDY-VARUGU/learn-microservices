import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static void check(String name, int[] nums, int[] expected) {
        total++;
        try {
            int[] actual = new Answer().productExceptSelf(nums.clone());
            if (actual != null && Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + brief(actual));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums)
                        + " | expected=" + brief(expected) + " | actual=" + (actual == null ? "null" : brief(actual)));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums)
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: compare against independent oracle and time it
    static void checkProp(String name, int[] nums, long budgetMs) {
        total++;
        try {
            int[] expected = oracle(nums);
            long t0 = System.nanoTime();
            int[] actual = new Answer().productExceptSelf(nums.clone());
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            boolean ok = actual != null && Arrays.equals(actual, expected);
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | mismatch vs oracle");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // independent oracle using prefix/suffix products (constraints guarantee 32-bit fit)
    static int[] oracle(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        int pre = 1;
        for (int i = 0; i < n; i++) { res[i] = pre; pre *= nums[i]; }
        int suf = 1;
        for (int i = n - 1; i >= 0; i--) { res[i] *= suf; suf *= nums[i]; }
        return res;
    }

    static String brief(int[] a) {
        if (a == null) return "null";
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " first=" + a[0] + "...]";
    }

    public static void main(String[] args) {
        check("example1", new int[]{1,2,3,4}, new int[]{24,12,8,6});
        check("example2 with zero", new int[]{-1,1,0,-3,3}, new int[]{0,0,9,0,0});
        check("two elements", new int[]{2,3}, new int[]{3,2});
        check("two zeros", new int[]{0,0}, new int[]{0,0});
        check("single zero", new int[]{0,4,5}, new int[]{20,0,0});
        check("all ones", new int[]{1,1,1,1}, new int[]{1,1,1,1});
        check("negatives", new int[]{-1,-2,-3,-4}, new int[]{-24,-12,-8,-6});
        check("contains one", new int[]{1,2,3}, new int[]{6,3,2});
        check("mixed signs", new int[]{2,-3,4}, new int[]{-12,8,-6});
        check("pair negatives", new int[]{-2,-3}, new int[]{-3,-2});
        check("sign flips with zero", new int[]{-5,2,0}, new int[]{0,0,-10});

        // --- added corner cases ---
        check("min length two distinct", new int[]{7,9}, new int[]{9,7});
        check("two zeros among others", new int[]{0,3,0,4}, new int[]{0,0,0,0});
        check("value bound 30", new int[]{30,1,1}, new int[]{1,30,30});
        check("value bound -30", new int[]{-30,1,2}, new int[]{2,-60,-30});
        check("all equal positives", new int[]{2,2,2,2}, new int[]{8,8,8,8});
        check("all equal negatives", new int[]{-2,-2,-2}, new int[]{4,4,4});
        check("single negative among ones", new int[]{1,1,-3,1}, new int[]{-3,-3,1,-3});
        check("zero at end", new int[]{2,3,0}, new int[]{0,0,6});
        check("zero at start", new int[]{0,2,3}, new int[]{6,0,0});
        check("alternating sign", new int[]{1,-1,1,-1}, new int[]{1,-1,1,-1});
        check("contains negative one only", new int[]{-1,5,5}, new int[]{25,-5,-5});
        check("strictly increasing", new int[]{1,2,3,4,5}, new int[]{120,60,40,30,24});
        check("strictly decreasing", new int[]{5,4,3,2,1}, new int[]{24,30,40,60,120});
        check("ones with single big", new int[]{1,1,1,7}, new int[]{7,7,7,1});
        check("product stays in int range", new int[]{6,7,8,9}, new int[]{504,432,378,336});
        check("three zeros", new int[]{0,0,0}, new int[]{0,0,0});

        // --- large / performance cases (oracle + timing) ---
        // n=10^5 of all 1s except boundaries; product stays in int (all 1 and -1 here).
        {
            int n = 100_000;
            int[] big = new int[n];
            Arrays.fill(big, 1);
            // exactly one -1 so products are +/-1 -> always fits 32-bit; oracle confirms
            big[n / 2] = -1;
            checkProp("large 100k ones with single -1", big, 3000);
        }
        // n=10^5 with values in {-1,1} (random) so all prefix/suffix products fit int.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextBoolean() ? 1 : -1;
            checkProp("large 100k +/-1 random", big, 3000);
        }
        // n=10^5 with a single 0 placed randomly (rest +/-1) -> exercises zero handling at scale.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextBoolean() ? 1 : -1;
            big[rnd.nextInt(n)] = 0;
            checkProp("large 100k single zero", big, 3000);
        }
        // n=10^5 with two zeros -> result must be all zeros; oracle confirms.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextBoolean() ? 1 : -1;
            big[100] = 0;
            big[90_000] = 0;
            checkProp("large 100k two zeros all-zero result", big, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
