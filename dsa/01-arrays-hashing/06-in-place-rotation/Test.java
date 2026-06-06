import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static void check(String name, int[] nums, int k, int[] expected) {
        total++;
        int[] in = nums.clone();
        try {
            new Answer().rotate(in, k);
            if (Arrays.equals(in, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + brief(in));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums) + " k=" + k
                        + " | expected=" + brief(expected) + " | actual=" + brief(in));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums) + " k=" + k
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: compare against independent oracle (out-of-place rotate) and time it
    static void checkProp(String name, int[] nums, int k, long budgetMs) {
        total++;
        int[] in = nums.clone();
        try {
            int[] expected = oracle(nums, k);
            long t0 = System.nanoTime();
            new Answer().rotate(in, k);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            boolean ok = Arrays.equals(in, expected);
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | n=" + nums.length + " k=" + k + " | mismatch vs oracle");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | n=" + nums.length + " k=" + k
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // independent oracle: build a fresh rotated-right array
    static int[] oracle(int[] nums, int k) {
        int n = nums.length;
        int[] r = new int[n];
        int s = n == 0 ? 0 : k % n;
        for (int i = 0; i < n; i++) r[(i + s) % n] = nums[i];
        return r;
    }

    static String brief(int[] a) {
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " first=" + a[0] + " last=" + a[a.length - 1] + "]";
    }

    public static void main(String[] args) {
        check("example1", new int[]{1,2,3,4,5,6,7}, 3, new int[]{5,6,7,1,2,3,4});
        check("example2 negatives", new int[]{-1,-100,3,99}, 2, new int[]{3,99,-1,-100});
        check("k greater than n", new int[]{1,2}, 3, new int[]{2,1});
        check("k=0 no change", new int[]{1,2,3}, 0, new int[]{1,2,3});
        check("k equals n", new int[]{1,2,3,4}, 4, new int[]{1,2,3,4});
        check("single element", new int[]{7}, 5, new int[]{7});
        check("k multiple of n", new int[]{1,2,3}, 6, new int[]{1,2,3});
        check("rotate by 1", new int[]{1,2,3,4,5}, 1, new int[]{5,1,2,3,4});
        check("rotate by n-1", new int[]{1,2,3,4,5}, 4, new int[]{2,3,4,5,1});
        check("with duplicates", new int[]{2,2,3,3}, 2, new int[]{3,3,2,2});
        check("large k mod", new int[]{10,20,30,40}, 102, new int[]{30,40,10,20});

        // --- added corner cases ---
        check("single element k=0", new int[]{42}, 0, new int[]{42});
        check("two elements k=0", new int[]{1,2}, 0, new int[]{1,2});
        check("two elements k=1", new int[]{1,2}, 1, new int[]{2,1});
        check("two elements k=2", new int[]{1,2}, 2, new int[]{1,2});
        check("k just over n", new int[]{1,2,3,4,5}, 6, new int[]{5,1,2,3,4});
        check("k equals n minus 1", new int[]{1,2,3,4}, 3, new int[]{2,3,4,1});
        check("all equal unchanged", new int[]{9,9,9,9}, 2, new int[]{9,9,9,9});
        check("int max and min values", new int[]{2147483647,-2147483648,0}, 1, new int[]{0,2147483647,-2147483648});
        check("strictly increasing rotate half", new int[]{1,2,3,4,5,6}, 3, new int[]{4,5,6,1,2,3});
        check("k=100000 on small n", new int[]{1,2,3}, 100000, oracle(new int[]{1,2,3}, 100000));
        check("negatives rotate by 1", new int[]{-5,-4,-3,-2,-1}, 1, new int[]{-1,-5,-4,-3,-2});
        check("duplicates rotate by 1", new int[]{7,7,8,7}, 1, new int[]{7,7,7,8});
        check("alternating values", new int[]{1,-1,1,-1}, 2, new int[]{1,-1,1,-1});
        check("rotate by n+1 wraps to 1", new int[]{1,2,3}, 4, new int[]{3,1,2});
        check("six elements rotate by 5", new int[]{1,2,3,4,5,6}, 5, new int[]{2,3,4,5,6,1});
        check("large value array small k", new int[]{1000000,2000000,3000000}, 2, new int[]{2000000,3000000,1000000});

        // --- large / performance cases (oracle + timing) ---
        // n=10^5, k near n -> reversal trick O(n), a naive shift-by-one-k-times is O(n*k) and would time out.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextInt();
            checkProp("large 100k k=99999", big, 99_999, 3000);
        }
        // n=10^5, k=10^5 (max k) -> exercises modulo handling at scale.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextInt();
            checkProp("large 100k k=100000 max", big, 100_000, 3000);
        }
        // n=10^5, k=n/2.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextInt(2001) - 1000;
            checkProp("large 100k k=n/2", big, n / 2, 3000);
        }
        // n=10^5, k=1 -> single right shift of a huge array.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = rnd.nextInt();
            checkProp("large 100k k=1", big, 1, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
