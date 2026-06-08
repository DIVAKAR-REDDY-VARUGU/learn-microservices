import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    // Property-based check: accepts ANY valid 1-indexed increasing pair whose values sum to
    // target (the `expected` arg is kept for readability but ignored, since several inputs are
    // ambiguous or had miscalculated hardcoded expecteds). A 0-indexed answer still fails here
    // because index 0 is out of the valid 1-based range.
    static void check(String name, int[] numbers, int target, int[] expected) {
        total++;
        try {
            int[] actual = new Answer().twoSum(clone(numbers), target);
            String why = validatePair(numbers, target, actual);
            if (why == null) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | input numbers=" + brief(numbers) + ", target=" + target
                    + " | " + why + " | actual=" + Arrays.toString(actual));
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                + " | input numbers=" + brief(numbers) + ", target=" + target
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Property check: any returned valid 1-indexed increasing pair whose values sum to target
    // is accepted. Used for inputs that admit multiple correct answers.
    static void checkProp(String name, int[] numbers, int target) {
        total++;
        try {
            int[] actual = new Answer().twoSum(clone(numbers), target);
            String why = validatePair(numbers, target, actual);
            if (why == null) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (got " + Arrays.toString(actual) + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | input numbers=" + brief(numbers) + ", target=" + target
                    + " | " + why + " | actual=" + Arrays.toString(actual));
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                + " | input numbers=" + brief(numbers) + ", target=" + target
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Returns null if the returned pair is a valid answer, otherwise a reason string.
    static String validatePair(int[] numbers, int target, int[] actual) {
        if (actual == null || actual.length != 2) return "null or wrong length";
        int i1 = actual[0], i2 = actual[1];
        if (!(i1 >= 1 && i2 <= numbers.length && i1 < i2)) return "indices out of range / not increasing";
        if (numbers[i1 - 1] + numbers[i2 - 1] != target) return "values do not sum to target";
        return null;
    }

    static int[] clone(int[] a) { return Arrays.copyOf(a, a.length); }

    static String brief(int[] a) {
        if (a.length <= 20) return Arrays.toString(a);
        return "[len=" + a.length + " " + a[0] + "," + a[1] + ",...," + a[a.length - 1] + "]";
    }

    public static void main(String[] args) {
        // ---------- Existing cases (kept) ----------
        // Examples from QUESTION.md
        check("example 1", new int[]{2,7,11,15}, 9, new int[]{1,2});
        check("example 2", new int[]{2,3,4}, 6, new int[]{1,3});
        check("example 3 (negatives)", new int[]{-1,0}, -1, new int[]{1,2});

        // Corner: minimum length array (2 elements)
        check("two elements", new int[]{1,2}, 3, new int[]{1,2});

        // Corner: all equal elements
        check("all equal", new int[]{3,3}, 6, new int[]{1,2});
        check("all equal larger", new int[]{5,5,5,5}, 10, new int[]{1,2});

        // Corner: duplicates where answer is not the first pair
        check("duplicates answer at ends", new int[]{1,1,1,4}, 5, new int[]{3,4});

        // Corner: negatives and zeros
        check("negatives sum to zero", new int[]{-5,-3,0,3,5}, 0, new int[]{1,5});
        check("two negatives", new int[]{-1000,-998}, -1998, new int[]{1,2});
        check("zero target with zeros", new int[]{0,0,3,4}, 0, new int[]{1,2});

        // Corner: min/max bounds of values
        check("min/max bounds", new int[]{-1000,-1,0,1000}, 0, new int[]{2,3});
        check("max sum", new int[]{999,1000}, 1999, new int[]{1,2});
        check("min sum", new int[]{-1000,-999}, -1999, new int[]{1,2});

        // Corner: answer at first and last
        check("answer first and last", new int[]{1,2,3,4,5,6}, 7, new int[]{1,6});

        // Corner: answer adjacent in middle
        check("answer in middle", new int[]{1,3,5,7,9}, 12, new int[]{3,5});

        // Corner: large array with answer near front
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = i - 500;
        check("large array near front", big, -999, new int[]{1,2});

        // ---------- New corner cases (property-verified: any valid pair accepted) ----------
        // Answer is the very first pair
        checkProp("answer first pair", new int[]{1,5,9,13}, 6);
        // Answer is the very last pair
        checkProp("answer last pair", new int[]{1,2,3,8,10}, 18);
        // Heavy duplicates, the unique non-dup completes the sum
        checkProp("heavy dups one unique", new int[]{2,2,2,2,2,11}, 13);
        // All negative array
        checkProp("all negative", new int[]{-9,-7,-5,-3,-1}, -8);
        // Strictly increasing, extreme ends sum to target
        checkProp("strictly increasing ends", new int[]{-4,-2,0,2,4,6,8}, 4);
        // Mix straddling zero
        checkProp("straddle zero", new int[]{-3,-1,0,2,4}, 1);
        // Two equal negatives at min bound
        checkProp("min bound equal", new int[]{-1000,-1000}, -2000);
        // Target zero via opposite extremes
        checkProp("opposite extremes zero", new int[]{-1000,-2,0,2,1000}, 0);
        // Large block of duplicates then the partner
        checkProp("dup block then partner", new int[]{-5,-5,-5,-5,3}, -2);
        // Adjacent equal pair forms the answer in the middle
        checkProp("adjacent equal middle", new int[]{1,4,4,9}, 8);
        // Whole range, first and one before last
        checkProp("first and near-last", new int[]{-10,-6,-2,3,7,11}, 5);
        // Small array, answer = two smallest
        checkProp("two smallest", new int[]{-50,-40,100}, -90);
        // Single zero pair amidst values
        checkProp("zero pair amidst", new int[]{-7,0,0,7}, 0);
        // Ascending run where only the extreme ends sum to target
        checkProp("only extremes", new int[]{2,3,4,5,6,7,8}, 10);

        // ---------- Large / performance cases (property-verified) ----------
        Random rnd = new Random(42);

        // Case A: large array (n = 30000, the constraint max). Endpoints at the value bounds
        // (-1000 and 1000) sum to 0; all interior values are >= 1 so no interior pair sums to 0.
        // The unique valid answer is therefore the two extremes.
        {
            int n = 30000;
            int[] arr = new int[n];
            arr[0] = -1000;
            arr[n - 1] = 1000;
            for (int i = 1; i < n - 1; i++) arr[i] = 1 + rnd.nextInt(998); // 1..998
            Arrays.sort(arr); // -1000 first, interior positives sorted, 1000 last
            timedProp("large n=30000 ends sum to 0", arr, 0);
        }

        // Case B: large array, answer is two adjacent interior duplicates.
        {
            int n = 20000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = -1000 + rnd.nextInt(2001); // -1000..1000
            Arrays.sort(arr);
            // pick a valid existing adjacent pair and use its sum as the target
            int idx = n / 2;
            int target = arr[idx] + arr[idx + 1];
            timedProp("large n=20000 interior pair", arr, target);
        }

        // Case C: huge tight array near max with target at extreme front pair.
        {
            int n = 30000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = -1000 + rnd.nextInt(2001);
            Arrays.sort(arr);
            int target = arr[0] + arr[1]; // smallest two, a valid pair
            timedProp("large n=30000 front pair", arr, target);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Times the Answer call and delegates to the property checker.
    static void timedProp(String name, int[] numbers, int target) {
        long t0 = System.nanoTime();
        int[] actual;
        try {
            actual = new Answer().twoSum(clone(numbers), target);
        } catch (Throwable e) {
            long ms = (System.nanoTime() - t0) / 1_000_000;
            total++;
            System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                + " | n=" + numbers.length + ", target=" + target
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return;
        }
        long ms = (System.nanoTime() - t0) / 1_000_000;
        total++;
        String why = validatePair(numbers, target, actual);
        if (why == null && ms > 3000) why = "too slow (" + ms + " ms)";
        if (why == null) {
            System.out.println("\033[32m[PASS]\033[0m " + name + " (" + ms + " ms)");
            pass++;
        } else {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                + " | n=" + numbers.length + ", target=" + target
                + " | " + why + " | actual=" + Arrays.toString(actual));
        }
    }
}
