import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static void check(String name, int[] nums1, int m, int[] nums2, int n, int[] expected) {
        total++;
        int[] in1 = nums1.clone();
        int[] in2 = nums2.clone();
        try {
            new Answer().merge(in1, m, in2, n);
            if (Arrays.equals(in1, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + brief(in1));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums1=" + brief(nums1) + " m=" + m
                        + " nums2=" + brief(nums2) + " n=" + n
                        + " | expected=" + brief(expected) + " | actual=" + brief(in1));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums1=" + brief(nums1) + " m=" + m
                    + " nums2=" + brief(nums2) + " n=" + n
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: compare against independent oracle (sort of first m + all n) and time it
    static void checkProp(String name, int[] nums1, int m, int[] nums2, int n, long budgetMs) {
        total++;
        int[] in1 = nums1.clone();
        int[] in2 = nums2.clone();
        try {
            int[] expected = oracle(nums1, m, nums2, n);
            long t0 = System.nanoTime();
            new Answer().merge(in1, m, in2, n);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            boolean ok = Arrays.equals(in1, expected);
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | m=" + m + " n=" + n + " | mismatch vs oracle");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | m=" + m + " n=" + n
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // independent oracle: collect first m of nums1 + first n of nums2, sort
    static int[] oracle(int[] nums1, int m, int[] nums2, int n) {
        int[] r = new int[m + n];
        int idx = 0;
        for (int i = 0; i < m; i++) r[idx++] = nums1[i];
        for (int i = 0; i < n; i++) r[idx++] = nums2[i];
        Arrays.sort(r);
        return r;
    }

    static String brief(int[] a) {
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " first=" + a[0] + " last=" + a[a.length - 1] + "]";
    }

    public static void main(String[] args) {
        check("example1", new int[]{1,2,3,0,0,0}, 3, new int[]{2,5,6}, 3, new int[]{1,2,2,3,5,6});
        check("example2 n=0", new int[]{1}, 1, new int[]{}, 0, new int[]{1});
        check("example3 m=0", new int[]{0}, 0, new int[]{1}, 1, new int[]{1});
        check("nums2 all smaller", new int[]{4,5,6,0,0,0}, 3, new int[]{1,2,3}, 3, new int[]{1,2,3,4,5,6});
        check("interleaved", new int[]{1,3,5,0,0,0}, 3, new int[]{2,4,6}, 3, new int[]{1,2,3,4,5,6});
        check("negatives", new int[]{-5,-2,0,0}, 2, new int[]{-3,-1}, 2, new int[]{-5,-3,-2,-1});
        check("duplicates across", new int[]{2,2,0,0}, 2, new int[]{2,2}, 2, new int[]{2,2,2,2});
        check("single each", new int[]{2,0}, 1, new int[]{1}, 1, new int[]{1,2});
        check("nums1 all larger", new int[]{7,8,9,0,0}, 3, new int[]{1,2}, 2, new int[]{1,2,7,8,9});
        check("zeros as values", new int[]{0,0,0,0}, 2, new int[]{0,0}, 2, new int[]{0,0,0,0});
        check("both empty into len0", new int[]{}, 0, new int[]{}, 0, new int[]{});

        // --- added corner cases ---
        check("m+n=1 from nums1", new int[]{5}, 1, new int[]{}, 0, new int[]{5});
        check("m+n=1 from nums2", new int[]{0}, 0, new int[]{5}, 1, new int[]{5});
        check("all of nums2 go first", new int[]{10,0,0,0}, 1, new int[]{1,2,3}, 3, new int[]{1,2,3,10});
        check("all of nums1 stay first", new int[]{1,2,3,0}, 3, new int[]{9}, 1, new int[]{1,2,3,9});
        check("single overlap equal", new int[]{5,0}, 1, new int[]{5}, 1, new int[]{5,5});
        check("nums2 entirely negative", new int[]{1,2,0,0}, 2, new int[]{-9,-9}, 2, new int[]{-9,-9,1,2});
        check("alternating merge longer", new int[]{1,3,5,7,0,0,0,0}, 4, new int[]{2,4,6,8}, 4, new int[]{1,2,3,4,5,6,7,8});
        check("heavy duplicates both", new int[]{3,3,3,0,0,0}, 3, new int[]{3,3,3}, 3, new int[]{3,3,3,3,3,3});
        check("min and max int", new int[]{-2147483648,0,0}, 1, new int[]{0,2147483647}, 2, new int[]{-2147483648,0,2147483647});
        check("nums1 empty slots only n", new int[]{0,0,0}, 0, new int[]{1,2,3}, 3, new int[]{1,2,3});
        check("last elem from nums2", new int[]{1,2,0}, 2, new int[]{9}, 1, new int[]{1,2,9});
        check("first elem from nums2", new int[]{5,6,0}, 2, new int[]{1}, 1, new int[]{1,5,6});
        check("strictly decreasing into increasing", new int[]{2,4,6,0,0,0}, 3, new int[]{1,3,5}, 3, new int[]{1,2,3,4,5,6});
        check("equal halves interleave", new int[]{1,1,1,0,0,0}, 3, new int[]{1,1,1}, 3, new int[]{1,1,1,1,1,1});
        check("nums2 larger tail", new int[]{1,2,3,0,0}, 3, new int[]{100,200}, 2, new int[]{1,2,3,100,200});
        check("nums2 smaller head", new int[]{100,200,0,0}, 2, new int[]{1,2}, 2, new int[]{1,2,100,200});

        // --- large / performance cases (oracle + timing, m+n up to 200 per constraints) ---
        // Max combined size 200: m=100, n=100, interleaved sorted halves.
        {
            int m = 100, n = 100;
            int[] nums1 = new int[m + n];
            int[] nums2 = new int[n];
            for (int i = 0; i < m; i++) nums1[i] = 2 * i;       // even numbers 0,2,4,...
            for (int i = 0; i < n; i++) nums2[i] = 2 * i + 1;   // odd numbers 1,3,5,...
            checkProp("max 100+100 interleaved", nums1, m, nums2, n, 3000);
        }
        // Max size, nums2 entirely smaller (forces full shift of nums1 block).
        {
            int m = 100, n = 100;
            int[] nums1 = new int[m + n];
            int[] nums2 = new int[n];
            for (int i = 0; i < m; i++) nums1[i] = 1000 + i;
            for (int i = 0; i < n; i++) nums2[i] = i;
            checkProp("max 100+100 nums2 all smaller", nums1, m, nums2, n, 3000);
        }
        // Max size with random sorted halves (seed 42), oracle decides expected.
        {
            Random rnd = new Random(42);
            int m = 120, n = 80;
            int[] a = new int[m];
            int[] b = new int[n];
            for (int i = 0; i < m; i++) a[i] = rnd.nextInt(2001) - 1000;
            for (int i = 0; i < n; i++) b[i] = rnd.nextInt(2001) - 1000;
            Arrays.sort(a);
            Arrays.sort(b);
            int[] nums1 = new int[m + n];
            System.arraycopy(a, 0, nums1, 0, m);
            checkProp("max 120+80 random sorted", nums1, m, b, n, 3000);
        }
        // Max size, all duplicates of one value across both arrays.
        {
            int m = 100, n = 100;
            int[] nums1 = new int[m + n];
            int[] nums2 = new int[n];
            for (int i = 0; i < m; i++) nums1[i] = 7;
            for (int i = 0; i < n; i++) nums2[i] = 7;
            checkProp("max 100+100 all equal", nums1, m, nums2, n, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
