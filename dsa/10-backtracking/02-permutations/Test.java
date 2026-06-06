import java.util.*;

// JUnit-free harness for Answer.permute(int[]).
// The permutations themselves are order-independent across the outer list,
// but each permutation is an ORDERED sequence, so we do NOT sort inner lists.
public class Test {
    static int total = 0, pass = 0;

    public static void main(String[] args) {
        // ----- Existing cases (kept) -----
        // Example 1
        check(new int[]{1, 2, 3},
            ll(l(1, 2, 3), l(1, 3, 2), l(2, 1, 3), l(2, 3, 1), l(3, 1, 2), l(3, 2, 1)));
        // Example 2
        check(new int[]{0, 1}, ll(l(0, 1), l(1, 0)));
        // Example 3: single element
        check(new int[]{1}, ll(l(1)));
        // Negatives / zero
        check(new int[]{-1, 0, 1},
            ll(l(-1, 0, 1), l(-1, 1, 0), l(0, -1, 1), l(0, 1, -1), l(1, -1, 0), l(1, 0, -1)));
        // Single negative
        check(new int[]{-7}, ll(l(-7)));
        // Bounds two-element
        check(new int[]{-10, 10}, ll(l(-10, 10), l(10, -10)));
        // Size 4 -> 24 permutations (count + distinctness)
        checkCount("size4 count=24", new int[]{1, 2, 3, 4}, 24);
        // Size 5 -> 120 permutations
        checkCount("size5 count=120", new int[]{5, 4, 3, 2, 1}, 120);
        // Each permutation must be a valid arrangement of the input multiset
        checkValid("size4 valid perms", new int[]{2, 4, 6, 8});

        // ----- New corner cases -----
        // Single element at lower bound
        check(new int[]{-10}, ll(l(-10)));
        // Single element at upper bound
        check(new int[]{10}, ll(l(10)));
        // Single zero
        check(new int[]{0}, ll(l(0)));
        // Two negatives
        check(new int[]{-2, -1}, ll(l(-2, -1), l(-1, -2)));
        // Strictly increasing size 3
        check(new int[]{-5, 0, 5},
            ll(l(-5, 0, 5), l(-5, 5, 0), l(0, -5, 5), l(0, 5, -5), l(5, -5, 0), l(5, 0, -5)));
        // Both constraint bounds in a 3-set
        check(new int[]{-10, 0, 10},
            ll(l(-10, 0, 10), l(-10, 10, 0), l(0, -10, 10), l(0, 10, -10), l(10, -10, 0), l(10, 0, -10)));
        // Count: size 3 -> 6
        checkCount("size3 count=6", new int[]{7, 8, 9}, 6);
        // Count: size 6 (max constraint) -> 720
        checkCount("size6 count=720", new int[]{-10, -5, 0, 3, 7, 10}, 720);
        // Distinctness on size 5 negatives
        checkCount("neg size5 count=120", new int[]{-1, -2, -3, -4, -5}, 120);
        // Valid arrangement on size 5 mixed
        checkValid("size5 valid perms", new int[]{-3, 0, 1, 8, -10});
        // Valid arrangement on size 6 (max constraint)
        checkValid("size6 valid perms", new int[]{6, -6, 2, -2, 9, -9});
        // First-element coverage: each value appears as the first element of some permutation
        checkFirstCoverage("size4 first-coverage", new int[]{1, 2, 3, 4});
        checkFirstCoverage("size3 first-coverage", new int[]{-1, 5, 9});
        // Each permutation has exactly n elements
        checkLengths("size5 lengths", new int[]{2, 4, 6, 8, 10});
        // Two distinct permutations for size 2 at bounds
        check(new int[]{-10, -9}, ll(l(-10, -9), l(-9, -10)));

        // ----- Scale / performance case (max constraint n=6 -> 720 permutations) -----
        // n is capped at 6 by constraints, so the upper bound is the "large" case.
        scaleCheck("scale n=6 (max constraint)", 6);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static void check(int[] nums, List<List<Integer>> expected) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().permute(nums);
            List<String> a = norm(actual);
            List<String> e = norm(expected);
            if (a.equals(e)) {
                System.out.println("\033[32m[PASS]\033[0m permute(" + Arrays.toString(nums) + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m permute(" + Arrays.toString(nums) + ")"
                    + " expected=" + e + " actual=" + a);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m permute(" + Arrays.toString(nums) + ") threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkCount(String name, int[] nums, int expectedCount) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().permute(nums);
            Set<String> distinct = new HashSet<>(norm(actual));
            if (actual.size() == expectedCount && distinct.size() == expectedCount) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=" + expectedCount
                    + " size=" + actual.size() + " distinct=" + distinct.size());
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkValid(String name, int[] nums) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().permute(nums);
            List<Integer> sortedInput = new ArrayList<>();
            for (int x : nums) sortedInput.add(x);
            Collections.sort(sortedInput);
            boolean ok = true;
            for (List<Integer> p : actual) {
                List<Integer> copy = new ArrayList<>(p);
                Collections.sort(copy);
                if (!copy.equals(sortedInput)) { ok = false; break; }
            }
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " a permutation did not match input multiset "
                    + sortedInput + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: each input value appears as the first element of at least one permutation.
    static void checkFirstCoverage(String name, int[] nums) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().permute(nums);
            Set<Integer> firsts = new HashSet<>();
            for (List<Integer> p : actual) if (!p.isEmpty()) firsts.add(p.get(0));
            boolean ok = true;
            for (int v : nums) if (!firsts.contains(v)) { ok = false; break; }
            if (ok && firsts.size() == nums.length) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " firsts=" + firsts);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: every permutation has exactly n elements.
    static void checkLengths(String name, int[] nums) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().permute(nums);
            boolean ok = !actual.isEmpty();
            for (List<Integer> p : actual) if (p.size() != nums.length) { ok = false; break; }
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " a permutation had wrong length");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Scale case: build the max-constraint input deterministically, verify by property
    // (n! distinct, each a valid arrangement of the input) and time it.
    static void scaleCheck(String name, int n) {
        total++;
        try {
            Random rnd = new Random(42);
            List<Integer> pool = new ArrayList<>();
            for (int v = -10; v <= 10; v++) pool.add(v);
            Collections.shuffle(pool, rnd);
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = pool.get(i);

            long t0 = System.nanoTime();
            List<List<Integer>> actual = new Answer().permute(nums);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

            long fact = 1;
            for (int i = 2; i <= n; i++) fact *= i;
            List<Integer> sortedInput = new ArrayList<>();
            for (int x : nums) sortedInput.add(x);
            Collections.sort(sortedInput);
            boolean valid = true;
            for (List<Integer> p : actual) {
                List<Integer> copy = new ArrayList<>(p);
                Collections.sort(copy);
                if (!copy.equals(sortedInput)) { valid = false; break; }
            }
            Set<String> distinct = new HashSet<>(norm(actual));
            boolean ok = valid && actual.size() == fact && distinct.size() == fact && elapsedMs <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " size=" + actual.size()
                    + " distinct=" + distinct.size() + " valid=" + valid
                    + " elapsed=" + elapsedMs + "ms (budget 3000ms, expected " + fact + ")");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Sort only the OUTER collection; keep each permutation's inner order.
    static List<String> norm(List<List<Integer>> lists) {
        List<String> keys = new ArrayList<>();
        for (List<Integer> inner : lists) keys.add(inner.toString());
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
