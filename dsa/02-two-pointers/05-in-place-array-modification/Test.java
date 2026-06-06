import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    // Validates both the returned count k AND the in-place mutation of the first k slots.
    static void check(String name, int[] nums, int expectedK, int[] expectedPrefix) {
        total++;
        int[] input = Arrays.copyOf(nums, nums.length);
        try {
            int k = new Answer().removeDuplicates(nums);
            int[] actualPrefix = Arrays.copyOf(nums, Math.max(0, Math.min(k, nums.length)));
            boolean ok = (k == expectedK) && Arrays.equals(actualPrefix, expectedPrefix);
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | input=" + brief(input)
                    + " | expected k=" + expectedK + ", prefix=" + brief(expectedPrefix)
                    + " | actual k=" + k + ", prefix=" + brief(actualPrefix)
                    + " (full nums now " + brief(nums) + ")");
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                + " | input=" + brief(input)
                + " | expected k=" + expectedK + ", prefix=" + brief(expectedPrefix)
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Independent oracle: the sorted distinct values in original order.
    static int[] uniques(int[] nums) {
        int[] tmp = new int[nums.length];
        int k = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i == 0 || nums[i] != nums[i - 1]) tmp[k++] = nums[i];
        }
        return Arrays.copyOf(tmp, k);
    }

    // Large/timed variant: derives expected k and prefix from the oracle, flags catastrophic slowness.
    static void checkTimed(String name, int[] nums) {
        total++;
        int[] input = Arrays.copyOf(nums, nums.length);
        int[] expectedPrefix = uniques(input);
        int expectedK = expectedPrefix.length;
        long t0 = System.nanoTime();
        try {
            int k = new Answer().removeDuplicates(nums);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            int[] actualPrefix = Arrays.copyOf(nums, Math.max(0, Math.min(k, nums.length)));
            boolean ok = (k == expectedK) && Arrays.equals(actualPrefix, expectedPrefix) && ms <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (k=" + k + ", " + ms + " ms)");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                    + " | n=" + input.length
                    + " | expected k=" + expectedK + " | actual k=" + k
                    + (ms > 3000 ? " | too slow" : " | prefix mismatch"));
            }
        } catch (Throwable e) {
            long ms = (System.nanoTime() - t0) / 1_000_000;
            System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                + " | n=" + input.length
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static String brief(int[] a) {
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " " + a[0] + "," + a[1] + ",...," + a[a.length - 1] + "]";
    }

    public static void main(String[] args) {
        // ---------- Existing cases (kept) ----------
        // Examples from QUESTION.md
        check("example 1", new int[]{1,1,2}, 2, new int[]{1,2});
        check("example 2", new int[]{0,0,1,1,1,2,2,3,3,4}, 5, new int[]{0,1,2,3,4});
        check("example 3 (no dups)", new int[]{1,2,3}, 3, new int[]{1,2,3});

        // Corner: single element
        check("single element", new int[]{5}, 1, new int[]{5});

        // Corner: all equal
        check("all equal", new int[]{7,7,7,7,7}, 1, new int[]{7});
        check("two equal", new int[]{2,2}, 1, new int[]{2});

        // Corner: no duplicates at all
        check("strictly increasing", new int[]{-3,-1,0,2,4,9}, 6, new int[]{-3,-1,0,2,4,9});

        // Corner: negatives with duplicates
        check("negatives with dups", new int[]{-2,-2,-1,-1,0,0,0}, 3, new int[]{-2,-1,0});

        // Corner: duplicates only at the end
        check("dups at end", new int[]{1,2,3,3,3}, 3, new int[]{1,2,3});

        // Corner: duplicates only at the start
        check("dups at start", new int[]{1,1,1,2,3}, 3, new int[]{1,2,3});

        // Corner: value boundaries (-100 and 100)
        check("value bounds", new int[]{-100,-100,0,100,100}, 3, new int[]{-100,0,100});

        // Corner: pairs of duplicates
        check("pairs", new int[]{1,1,2,2,3,3,4,4}, 4, new int[]{1,2,3,4});

        // Corner: large array all distinct
        int[] big = new int[201];
        for (int i = 0; i < big.length; i++) big[i] = i - 100; // -100..100
        int[] bigExpected = Arrays.copyOf(big, big.length);
        check("large all distinct", big, 201, bigExpected);

        // Corner: large array all same
        int[] same = new int[1000];
        Arrays.fill(same, 50);
        check("large all same", same, 1, new int[]{50});

        // ---------- New corner cases ----------
        // Two distinct elements (minimal non-trivial)
        check("two distinct", new int[]{1,2}, 2, new int[]{1,2});
        // Two equal at the value lower bound
        check("two equal min bound", new int[]{-100,-100}, 1, new int[]{-100});
        // Two equal at the value upper bound
        check("two equal max bound", new int[]{100,100}, 1, new int[]{100});
        // All negative, all distinct
        check("all negative distinct", new int[]{-5,-4,-3,-2,-1}, 5, new int[]{-5,-4,-3,-2,-1});
        // All negative with heavy duplicates
        check("all negative dups", new int[]{-9,-9,-9,-7,-7,-3}, 3, new int[]{-9,-7,-3});
        // Zeros only
        check("zeros only", new int[]{0,0,0,0}, 1, new int[]{0});
        // Crossing zero with one duplicate each
        check("crossing zero dups", new int[]{-1,-1,0,0,1,1}, 3, new int[]{-1,0,1});
        // Single duplicate pair in the middle
        check("single dup middle", new int[]{1,2,2,3,4}, 4, new int[]{1,2,3,4});
        // Long run of one value then a tail
        check("long run then tail", new int[]{3,3,3,3,3,3,7}, 2, new int[]{3,7});
        // Heavy duplicates spanning the full value range edges
        check("range edges heavy dups", new int[]{-100,-100,-100,100,100,100}, 2, new int[]{-100,100});
        // Every value duplicated exactly twice, including negatives
        check("each twice with negatives", new int[]{-2,-2,-1,-1,3,3,5,5}, 4, new int[]{-2,-1,3,5});
        // Increasing by one with a single late duplicate
        check("late single duplicate", new int[]{1,2,3,4,4}, 4, new int[]{1,2,3,4});
        // Alternating-looking but sorted with grouped dups
        check("grouped triples", new int[]{1,1,1,5,5,5,9,9,9}, 3, new int[]{1,5,9});
        // Off-by-one shape: only the first two are equal
        check("only first two equal", new int[]{4,4,5,6,7,8}, 5, new int[]{4,5,6,7,8});
        // Off-by-one shape: only the last two are equal
        check("only last two equal", new int[]{4,5,6,7,8,8}, 5, new int[]{4,5,6,7,8});

        // ---------- Large / performance cases (oracle-verified, generous time budget) ----------
        // Constraint upper bound is nums.length == 3*10^4. The optimal slow/fast solution is O(n);
        // these expose any accidental O(n^2) (e.g. shifting on each duplicate).
        Random rnd = new Random(42);

        // Case A: maximum length, all identical -> k must be 1.
        {
            int n = 30000;
            int[] arr = new int[n];
            Arrays.fill(arr, 50);
            checkTimed("large n=30000 all identical", arr);
        }

        // Case B: maximum length sorted with heavy random duplication across the full value range.
        {
            int n = 30000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(201) - 100; // -100..100
            Arrays.sort(arr);
            checkTimed("large n=30000 heavy dups sorted", arr);
        }

        // Case C: maximum length where every distinct value in [-100,100] appears, sorted.
        // With only 201 possible values, the unique count caps at 201.
        {
            int n = 30000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = -100 + (i % 201);
            Arrays.sort(arr);
            checkTimed("large n=30000 full value coverage", arr);
        }

        // Case D: maximum length almost-all-distinct-looking but values constrained,
        // built as a sorted run repeating each value a few times.
        {
            int n = 30000;
            int[] arr = new int[n];
            int v = -100, count = 0;
            for (int i = 0; i < n; i++) {
                arr[i] = v;
                if (++count == 3) { count = 0; if (v < 100) v++; }
            }
            // arr is non-decreasing by construction
            checkTimed("large n=30000 triples ascending", arr);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
