import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static void check(String name, int[] nums, int k, int expected) {
        total++;
        try {
            int actual = new Answer().subarraySum(nums.clone(), k);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums) + " k=" + k
                        + " | expected=" + expected + " | actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums) + " k=" + k
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: compare against independent O(n) prefix-sum oracle and time it
    static void checkProp(String name, int[] nums, int k, long budgetMs) {
        total++;
        try {
            int expected = oracle(nums, k);
            long t0 = System.nanoTime();
            int actual = new Answer().subarraySum(nums.clone(), k);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            if (actual == expected && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " -> " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | k=" + k + " | expected=" + expected + " | actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | k=" + k
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // independent O(n) prefix-sum + hashmap oracle (counts subarrays summing to k)
    static int oracle(int[] nums, int k) {
        HashMap<Long, Integer> seen = new HashMap<>();
        seen.put(0L, 1);
        long prefix = 0;
        int count = 0;
        for (int x : nums) {
            prefix += x;
            count += seen.getOrDefault(prefix - k, 0);
            seen.merge(prefix, 1, Integer::sum);
        }
        return count;
    }

    static String brief(int[] a) {
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " first=" + a[0] + "...]";
    }

    public static void main(String[] args) {
        check("example1", new int[]{1,1,1}, 2, 2);
        check("example2", new int[]{1,2,3}, 3, 2);
        check("example3 with zero", new int[]{1,-1,0}, 0, 3);
        check("single element matches", new int[]{5}, 5, 1);
        check("single element no match", new int[]{5}, 3, 0);
        check("no subarray equals k", new int[]{1,2,3}, 100, 0);
        check("all negatives", new int[]{-1,-1,-1}, -2, 2);
        check("zeros only k=0", new int[]{0,0,0}, 0, 6);
        check("mixed sign", new int[]{3,4,7,2,-3,1,4,2}, 7, 4);
        check("whole array", new int[]{2,2,2,2}, 8, 1);
        check("k zero with cancel", new int[]{1,-1,1,-1}, 0, 4);
        check("negative k", new int[]{-1,-1,1}, -2, 1);

        // --- added corner cases ---
        check("single element zero k=0", new int[]{0}, 0, 1);
        check("single element nonzero k=0", new int[]{5}, 0, 0);
        check("min value bound element", new int[]{-1000}, -1000, 1);
        check("max value bound element", new int[]{1000}, 1000, 1);
        check("two zeros k=0", new int[]{0,0}, 0, 3);
        check("all equal k single", new int[]{5,5,5,5}, 5, 4);
        check("all equal k=10", new int[]{5,5,5,5}, 10, 3);
        check("strictly increasing prefix", new int[]{1,2,3,4}, 6, 1);
        check("k larger than any subarray", new int[]{1,2,3,4}, 1000000, 0);
        check("k smaller than any reachable", new int[]{1,2,3}, -5, 0);
        check("heavy duplicate negatives", new int[]{-2,-2,-2,-2}, -4, 3);
        check("alternating cancel long", new int[]{1,-1,1,-1,1,-1}, 0, 9);
        check("single big positive k matches", new int[]{1000,1000,1000}, 2000, 2);
        check("prefix repeats produce many", new int[]{0,0,0,0}, 0, 10);
        check("mixed needs internal window", new int[]{1,2,1,2,1}, 3, 4);
        check("no zero subarray with nonzero k", new int[]{2,4,6}, 1, 0);

        // --- large / performance cases (oracle + timing) ---
        // n=2*10^4 (upper bound), all ones, k engineered; quadratic O(n^2) would be ~4e8 ops.
        {
            int n = 20_000;
            int[] big = new int[n];
            Arrays.fill(big, 1);
            // count of subarrays summing to k = number of (i,j) windows of length k -> n-k+1
            checkProp("large 20k all ones k=10000", big, 10_000, 3000);
        }
        // n=2*10^4 all zeros, k=0 -> huge count of subarrays; result may exceed int? n*(n+1)/2 = ~2e8 fits int.
        {
            int n = 20_000;
            int[] big = new int[n];
            // all zeros, k=0: every subarray sums to 0 -> n*(n+1)/2 = 200010000 (fits int)
            checkProp("large 20k all zeros k=0", big, 0, 3000);
        }
        // n=2*10^4 random values in [-1000,1000], k=0; oracle decides expected.
        {
            Random rnd = new Random(42);
            int n = 20_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextInt(2001) - 1000;
            checkProp("large 20k random k=0", big, 0, 3000);
        }
        // n=2*10^4 random values, arbitrary k; oracle decides expected.
        {
            Random rnd = new Random(42);
            int n = 20_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextInt(2001) - 1000;
            checkProp("large 20k random k=137", big, 137, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
