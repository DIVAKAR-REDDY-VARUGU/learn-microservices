import java.util.*;

// JUnit-free harness for Answer.subsets(int[]).
// Subsets are order-independent on both levels, so we sort each inner list
// and sort the outer collection by a canonical key before comparing.
public class Test {
    static int total = 0, pass = 0;

    public static void main(String[] args) {
        // ----- Existing cases (kept) -----
        // Example 1
        check("[1,2,3]", new int[]{1, 2, 3},
            ll(l(), l(1), l(2), l(1, 2), l(3), l(1, 3), l(2, 3), l(1, 2, 3)));
        // Example 2: single element
        check("[0]", new int[]{0}, ll(l(), l(0)));
        // Example 3
        check("[9,8]", new int[]{9, 8}, ll(l(), l(9), l(8), l(9, 8)));
        // Negatives
        check("[-1,-2]", new int[]{-1, -2}, ll(l(), l(-1), l(-2), l(-1, -2)));
        // Mixed sign incl zero / bounds
        check("[-10,0,10]", new int[]{-10, 0, 10},
            ll(l(), l(-10), l(0), l(10), l(-10, 0), l(-10, 10), l(0, 10), l(-10, 0, 10)));
        // Single negative
        check("[-5]", new int[]{-5}, ll(l(), l(-5)));
        // Size 4 -> 16 subsets (count check)
        checkCount("size4 count", new int[]{1, 2, 3, 4}, 16);
        // Size 5 -> 32 subsets (count check)
        checkCount("size5 count", new int[]{1, 2, 3, 4, 5}, 32);
        // No duplicate subsets check on size 4
        checkNoDup("size4 no-dup", new int[]{2, 4, 6, 8});

        // ----- New corner cases -----
        // Single element at lower constraint bound -10
        check("[-10]", new int[]{-10}, ll(l(), l(-10)));
        // Single element at upper constraint bound 10
        check("[10]", new int[]{10}, ll(l(), l(10)));
        // Single zero
        check("[0]-again", new int[]{0}, ll(l(), l(0)));
        // Two bounds only
        check("[-10,10]", new int[]{-10, 10}, ll(l(), l(-10), l(10), l(-10, 10)));
        // Strictly increasing size 3 of negatives
        check("[-3,-2,-1]", new int[]{-3, -2, -1},
            ll(l(), l(-3), l(-2), l(-1), l(-3, -2), l(-3, -1), l(-2, -1), l(-3, -2, -1)));
        // Strictly decreasing size 3
        check("[3,2,1]", new int[]{3, 2, 1},
            ll(l(), l(3), l(2), l(1), l(3, 2), l(3, 1), l(2, 1), l(3, 2, 1)));
        // Includes zero with positives
        check("[0,1]", new int[]{0, 1}, ll(l(), l(0), l(1), l(0, 1)));
        // Alternating sign size 3
        check("[-2,3,-4]", new int[]{-2, 3, -4},
            ll(l(), l(-2), l(3), l(-4), l(-2, 3), l(-2, -4), l(3, -4), l(-2, 3, -4)));
        // Empty subset is always present (size 6 -> 64 contains [])
        checkHasEmpty("size6 has-empty", new int[]{1, 2, 3, 4, 5, 6});
        // Every singleton subset present (size 5)
        checkSingletons("size5 singletons", new int[]{2, 4, 6, 8, 10});
        // Count check at off-by-one shapes
        checkCount("size6 count=64", new int[]{1, 2, 3, 4, 5, 6}, 64);
        checkCount("size7 count=128", new int[]{1, 2, 3, 4, 5, 6, 7}, 128);
        checkCount("size3 count=8", new int[]{-1, 0, 1}, 8);
        // No-dup on negatives size 5
        checkNoDup("neg size5 no-dup", new int[]{-1, -3, -5, -7, -9});
        // Each subset's elements come from input + no dup, on a mixed set
        checkValidSubsets("size7 valid-subsets", new int[]{-10, -3, -1, 0, 2, 7, 10});

        // ----- Scale / performance case (max constraint n=10 -> 1024 subsets) -----
        // n is capped at 10 by constraints, so the upper bound is the "large" case.
        scaleCheck("scale n=10 (max constraint)", 10);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static void check(String name, int[] nums, List<List<Integer>> expected) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().subsets(nums);
            List<String> a = norm(actual);
            List<String> e = norm(expected);
            if (a.equals(e)) {
                System.out.println("\033[32m[PASS]\033[0m subsets(" + Arrays.toString(nums) + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m subsets(" + Arrays.toString(nums) + ")"
                    + " expected=" + e + " actual=" + a);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m subsets(" + Arrays.toString(nums) + ") threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkCount(String name, int[] nums, int expectedCount) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().subsets(nums);
            int c = actual.size();
            if (c == expectedCount) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + c + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=" + expectedCount + " actual=" + c);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkNoDup(String name, int[] nums) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().subsets(nums);
            Set<String> seen = new HashSet<>(norm(actual));
            if (seen.size() == actual.size()) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " has duplicate subsets: " + norm(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkHasEmpty(String name, int[] nums) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().subsets(nums);
            boolean hasEmpty = false;
            for (List<Integer> s : actual) if (s.isEmpty()) { hasEmpty = true; break; }
            if (hasEmpty) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " missing the empty subset");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkSingletons(String name, int[] nums) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().subsets(nums);
            Set<String> norm = new HashSet<>(norm(actual));
            boolean ok = true;
            for (int v : nums) if (!norm.contains("[" + v + "]")) { ok = false; break; }
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " missing a singleton subset");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: every subset uses only input elements, no element repeated within a subset.
    static void checkValidSubsets(String name, int[] nums) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().subsets(nums);
            Set<Integer> allowed = new HashSet<>();
            for (int v : nums) allowed.add(v);
            boolean ok = true;
            for (List<Integer> s : actual) {
                Set<Integer> seen = new HashSet<>();
                for (int v : s) {
                    if (!allowed.contains(v) || !seen.add(v)) { ok = false; break; }
                }
                if (!ok) break;
            }
            Set<String> distinct = new HashSet<>(norm(actual));
            if (ok && distinct.size() == actual.size() && actual.size() == (1 << nums.length)) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " ok=" + ok
                    + " distinct=" + distinct.size() + " size=" + actual.size()
                    + " expected=" + (1 << nums.length));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Scale case: build the max-constraint input deterministically, verify by property
    // (exactly 2^n distinct subsets covering the full power set) and time it.
    static void scaleCheck(String name, int n) {
        total++;
        try {
            Random rnd = new Random(42);
            // distinct values in [-10,10] up to n picked deterministically
            List<Integer> pool = new ArrayList<>();
            for (int v = -10; v <= 10; v++) pool.add(v);
            Collections.shuffle(pool, rnd);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = pool.get(i);

            long t0 = System.nanoTime();
            List<List<Integer>> actual = new Answer().subsets(nums);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

            Set<String> distinct = new HashSet<>(norm(actual));
            Set<Integer> allowed = new HashSet<>();
            for (int v : nums) allowed.add(v);
            boolean valid = true;
            for (List<Integer> s : actual) {
                Set<Integer> seen = new HashSet<>();
                for (int v : s) if (!allowed.contains(v) || !seen.add(v)) { valid = false; break; }
                if (!valid) break;
            }
            boolean ok = valid
                && actual.size() == (1 << n)
                && distinct.size() == (1 << n)
                && elapsedMs <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " size=" + actual.size()
                    + " distinct=" + distinct.size() + " valid=" + valid
                    + " elapsed=" + elapsedMs + "ms (budget 3000ms, expected size "
                    + (1 << n) + ")");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static List<String> norm(List<List<Integer>> lists) {
        List<String> keys = new ArrayList<>();
        for (List<Integer> inner : lists) {
            List<Integer> copy = new ArrayList<>(inner);
            Collections.sort(copy);
            keys.add(copy.toString());
        }
        Collections.sort(keys);
        return keys;
    }

    static List<Integer> l(int... xs) {
        List<Integer> r = new ArrayList<>();
        for (int x : xs) r.add(x);
        return r;
    }

    @SafeVarargs
    static List<List<Integer>> ll(List<Integer>... xs) {
        return new ArrayList<>(Arrays.asList(xs));
    }
}
