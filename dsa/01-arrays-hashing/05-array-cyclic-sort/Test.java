import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static void check(String name, int[] nums, List<Integer> expected) {
        total++;
        try {
            List<Integer> actual = new Answer().findDisappearedNumbers(nums.clone());
            if (actual != null && actual.equals(expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums)
                        + " | expected=" + expected + " | actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums)
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: compare against independent boolean-presence oracle and time it
    static void checkProp(String name, int[] nums, long budgetMs) {
        total++;
        try {
            List<Integer> expected = oracle(nums);
            long t0 = System.nanoTime();
            List<Integer> actual = new Answer().findDisappearedNumbers(nums.clone());
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            boolean ok = actual != null && actual.equals(expected);
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " -> " + (expected.size() <= 10 ? expected.toString() : "[" + expected.size() + " missing]"));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | expectedSize=" + expected.size()
                        + " actualSize=" + (actual == null ? -1 : actual.size()));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // independent oracle: presence array, missing numbers in ascending order
    static List<Integer> oracle(int[] nums) {
        int n = nums.length;
        boolean[] present = new boolean[n + 1];
        for (int x : nums) if (x >= 1 && x <= n) present[x] = true;
        List<Integer> r = new ArrayList<>();
        for (int v = 1; v <= n; v++) if (!present[v]) r.add(v);
        return r;
    }

    static List<Integer> L(int... xs) {
        List<Integer> r = new ArrayList<>();
        for (int x : xs) r.add(x);
        return r;
    }

    static String brief(int[] a) {
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " first=" + a[0] + "...]";
    }

    public static void main(String[] args) {
        check("example1", new int[]{4,3,2,7,8,2,3,1}, L(5,6));
        check("example2", new int[]{1,1}, L(2));
        check("example3", new int[]{2,2,2}, L(1,3));
        check("single present", new int[]{1}, L());
        check("single missing dup", new int[]{2,2}, L(1));
        check("already complete sorted", new int[]{1,2,3,4,5}, L());
        check("reverse complete", new int[]{5,4,3,2,1}, L());
        check("all same", new int[]{3,3,3}, L(1,2));
        check("two missing", new int[]{1,1,2,2}, L(3,4));
        check("missing first", new int[]{2,2,3,4}, L(1));
        check("missing last", new int[]{1,2,3,3}, L(4));

        // --- added corner cases ---
        check("n=1 always present", new int[]{1}, L());
        check("all ones large n", new int[]{1,1,1,1,1}, L(2,3,4,5));
        check("all n value", new int[]{4,4,4,4}, L(1,2,3));
        check("only first present rest dup", new int[]{1,1,1,1,1,1}, L(2,3,4,5,6));
        check("missing middle", new int[]{1,2,2,4,5}, L(3));
        check("two adjacent missing", new int[]{1,1,4,4,5}, L(2,3));
        check("heavy dup of two values", new int[]{2,3,2,3,2,3}, L(1,4,5,6));
        check("alternating present", new int[]{1,3,1,3,5,5}, L(2,4,6));
        check("already sorted with gap", new int[]{1,2,4,4}, L(3));
        check("reverse with dup", new int[]{5,5,3,2,1}, L(4));
        check("missing first and last", new int[]{2,3,3,2}, L(1,4));
        check("max value repeated, ones missing", new int[]{5,5,5,5,5}, L(1,2,3,4));
        check("every odd missing", new int[]{2,2,4,4,6,6}, L(1,3,5));
        check("every even missing", new int[]{1,1,3,3,5,5}, L(2,4,6));
        check("none missing scrambled", new int[]{3,1,4,2,6,5}, L());
        check("two present rest one value", new int[]{1,2,1,1}, L(3,4));

        // --- large / performance cases (oracle + timing) ---
        // n=10^5 where half the values are duplicated -> many missing; quadratic would blow up.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            // fill each slot with a random value in [1,n]; oracle computes the true missing set
            for (int i = 0; i < n; i++) big[i] = 1 + rnd.nextInt(n);
            checkProp("large 100k random in [1,n]", big, 3000);
        }
        // n=10^5 all equal to 1 -> missing = {2..n}; stresses output building.
        {
            int n = 100_000;
            int[] big = new int[n];
            Arrays.fill(big, 1);
            checkProp("large 100k all ones", big, 3000);
        }
        // n=10^5 perfect permutation (no missing) -> exercises the in-place path fully.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = i + 1;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int t = big[i]; big[i] = big[j]; big[j] = t;
            }
            checkProp("large 100k full permutation no missing", big, 3000);
        }
        // n=10^5 permutation but with one value duplicated (one missing) -> single-element output at scale.
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = i + 1;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int t = big[i]; big[i] = big[j]; big[j] = t;
            }
            // overwrite one slot to duplicate another value -> exactly one missing
            big[12345] = big[54321];
            checkProp("large 100k one missing", big, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
