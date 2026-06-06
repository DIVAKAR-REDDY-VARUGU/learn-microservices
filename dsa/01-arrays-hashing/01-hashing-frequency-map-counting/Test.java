import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    // top-k is order independent: normalize by sorting before compare
    static int[] norm(int[] a) {
        int[] c = a.clone();
        Arrays.sort(c);
        return c;
    }

    static void check(String name, int[] nums, int k, int[] expected) {
        total++;
        try {
            int[] actual = new Answer().topKFrequent(nums.clone(), k);
            boolean ok = actual != null && norm(actual).length == norm(expected).length
                    && Arrays.equals(norm(actual), norm(expected));
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums) + " k=" + k
                        + " | expected(any order)=" + Arrays.toString(expected)
                        + " | actual=" + Arrays.toString(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums) + " k=" + k
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // property check (no exact expected): result must be exactly the true top-k set
    static void checkProp(String name, int[] nums, int k, long budgetMs) {
        total++;
        try {
            long t0 = System.nanoTime();
            int[] actual = new Answer().topKFrequent(nums.clone(), k);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            int[] expected = oracle(nums, k);
            boolean ok = actual != null && actual.length == k && norm(actual).length == norm(expected).length
                    && Arrays.equals(norm(actual), norm(expected));
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | nums=" + brief(nums) + " k=" + k
                        + " | expected(any order)=" + Arrays.toString(expected)
                        + " | actual=" + Arrays.toString(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | nums=" + brief(nums) + " k=" + k
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // independent O(n log n) oracle: true top-k by frequency (answer guaranteed unique by constraints)
    static int[] oracle(int[] nums, int k) {
        HashMap<Integer, Integer> f = new HashMap<>();
        for (int x : nums) f.merge(x, 1, Integer::sum);
        List<int[]> ents = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : f.entrySet()) ents.add(new int[]{e.getKey(), e.getValue()});
        ents.sort((a, b) -> b[1] - a[1]);
        int[] r = new int[k];
        for (int i = 0; i < k; i++) r[i] = ents.get(i)[0];
        return r;
    }

    static String brief(int[] a) {
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " first=" + a[0] + "...]";
    }

    public static void main(String[] args) {
        check("example1", new int[]{1,1,1,2,2,3}, 2, new int[]{1,2});
        check("single element k=1", new int[]{1}, 1, new int[]{1});
        check("example3 negatives", new int[]{4,1,-1,2,-1,2,3}, 2, new int[]{-1,2});
        check("all distinct k=all", new int[]{5,6,7}, 3, new int[]{5,6,7});
        check("all equal", new int[]{9,9,9,9}, 1, new int[]{9});
        check("k=1 clear winner", new int[]{3,3,3,1,2}, 1, new int[]{3});
        check("zeros and negatives", new int[]{0,0,-5,-5,-5,7}, 2, new int[]{0,-5});
        check("two groups k=2", new int[]{1,1,2,2,3}, 2, new int[]{1,2});
        check("large dup k=2", new int[]{4,4,4,4,2,2,2,8,8,1}, 2, new int[]{4,2});
        check("bounds values", new int[]{10000,-10000,10000,-10000,-10000}, 2, new int[]{10000,-10000});
        check("k equals distinct count", new int[]{1,2,2,3,3,3}, 3, new int[]{1,2,3});

        // --- added corner cases ---
        check("constraint max value only", new int[]{10000,10000,10000}, 1, new int[]{10000});
        check("constraint min value only", new int[]{-10000,-10000}, 1, new int[]{-10000});
        check("min and max bounds k=2", new int[]{10000,-10000,-10000}, 2, new int[]{10000,-10000});
        check("strictly increasing distinct", new int[]{1,2,3,4,5}, 5, new int[]{1,2,3,4,5});
        check("strictly decreasing distinct", new int[]{9,8,7,6}, 4, new int[]{9,8,7,6});
        check("single element k=1 zero", new int[]{0}, 1, new int[]{0});
        check("heavy duplicates one group", new int[]{7,7,7,7,7,7,7}, 1, new int[]{7});
        check("all negative k=2", new int[]{-1,-1,-2,-2,-2,-3}, 2, new int[]{-2,-1});
        check("zeros dominate", new int[]{0,0,0,0,1,2}, 1, new int[]{0});
        check("clear freq tiers k=3", new int[]{5,5,5,5,4,4,4,3,3,2}, 3, new int[]{5,4,3});
        check("negatives distinct tiers", new int[]{-1,-1,-1,-2,-2,-3}, 2, new int[]{-1,-2});
        check("k equals one of many", new int[]{8,8,8,1,2,3,4}, 1, new int[]{8});
        check("off-by-one k = distinct-1", new int[]{1,1,1,2,2,3,3,4}, 3, new int[]{1,2,3});
        check("alternating two values", new int[]{1,2,1,2,1}, 1, new int[]{1});
        check("wide spread small counts", new int[]{10,20,30,30,20,10,5}, 3, new int[]{10,20,30});

        // --- large / performance cases (property + timing) ---
        // Distinct-dominated large input: ~50k entries, clear top-k by construction.
        {
            Random rnd = new Random(42);
            int n = 80_000;
            int[] big = new int[n];
            // tiers: a few super-frequent winners, rest filler with small counts
            int[] winners = {1234, -1234, 4321, -4321, 9999};
            int idx = 0;
            // each winner appears a large, strictly-separated number of times
            int base = 9000;
            for (int w = 0; w < winners.length; w++) {
                int cnt = base - w * 1000; // 9000, 8000, 7000, 6000, 5000 -> all distinct
                for (int c = 0; c < cnt && idx < n; c++) big[idx++] = winners[w];
            }
            // filler: values in a range that each appear at most a handful of times (< 5000)
            while (idx < n) {
                big[idx++] = rnd.nextInt(2000) - 1000; // [-1000,999]
            }
            // shuffle so winners aren't contiguous
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int tmp = big[i]; big[i] = big[j]; big[j] = tmp;
            }
            checkProp("large 80k k=5 separated tiers", big, 5, 3000);
        }
        // Near upper-bound length with one overwhelming element, k=1
        {
            Random rnd = new Random(42);
            int n = 100_000;
            int[] big = new int[n];
            for (int i = 0; i < n; i++) big[i] = (i < n / 2) ? 4242 : (rnd.nextInt(1999) - 999);
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int tmp = big[i]; big[i] = big[j]; big[j] = tmp;
            }
            checkProp("large 100k k=1 dominant", big, 1, 3000);
        }
        // Many distinct values with engineered descending counts, k=10
        {
            int distinct = 400;
            List<Integer> list = new ArrayList<>();
            for (int v = 0; v < distinct; v++) {
                int cnt = distinct - v; // 400,399,...,1 all distinct counts
                for (int c = 0; c < cnt; c++) list.add(v - 200); // shift into negatives too
            }
            Random rnd = new Random(42);
            Collections.shuffle(list, rnd);
            int[] big = new int[list.size()];
            for (int i = 0; i < big.length; i++) big[i] = list.get(i);
            checkProp("large descending-count distinct k=10", big, 10, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
