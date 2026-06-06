import java.util.*;

// JUnit-free harness for Answer.combinationSum(int[], int).
// Combinations are order-independent across the outer list, and within a single
// combination the numbers form a multiset, so we sort each inner list then
// sort the outer collection by canonical key.
public class Test {
    static int total = 0, pass = 0;

    public static void main(String[] args) {
        // ----- Existing cases (kept) -----
        // Example 1
        check(new int[]{2, 3, 6, 7}, 7, ll(l(2, 2, 3), l(7)));
        // Example 2
        check(new int[]{2, 3, 5}, 8, ll(l(2, 2, 2, 2), l(2, 3, 3), l(3, 5)));
        // Example 3: no combination (target < min candidate)
        check(new int[]{2}, 1, ll());
        // Single candidate that divides target exactly
        check(new int[]{3}, 9, ll(l(3, 3, 3)));
        // Single candidate that does not divide target -> empty
        check(new int[]{5}, 12, ll());
        // Target equals a candidate exactly
        check(new int[]{4, 6, 8}, 8, ll(l(4, 4), l(8)));
        // Reuse-heavy small candidate
        check(new int[]{2}, 6, ll(l(2, 2, 2)));
        // Two candidates, multiple combos
        check(new int[]{2, 4}, 8, ll(l(2, 2, 2, 2), l(2, 2, 4), l(4, 4)));
        // Target smaller than every candidate -> empty
        check(new int[]{7, 11, 13}, 5, ll());
        // No duplicate combinations
        checkNoDup("no-dup [2,3,5] t=10", new int[]{2, 3, 5}, 10);

        // ----- New corner cases -----
        // Target equals smallest candidate (single-element combo)
        check(new int[]{2, 5, 9}, 2, ll(l(2)));
        // Min candidate value (2) reused to reach moderate target
        check(new int[]{2}, 8, ll(l(2, 2, 2, 2)));
        // Single candidate at max value 40 equal to max target 40
        check(new int[]{40}, 40, ll(l(40)));
        // Single candidate value 40, target 39 (just below) -> empty
        check(new int[]{40}, 39, ll());
        // Target 1 (min target) but min candidate is 2 -> empty
        check(new int[]{2, 3, 4}, 1, ll());
        // Candidate equals target among several (note: [3,3,5] also sums to 11)
        check(new int[]{3, 5, 7, 11}, 11, ll(l(11), l(3, 3, 5)));
        // Two candidates where only reuse of one works
        check(new int[]{5, 7}, 10, ll(l(5, 5)));
        // No combination reachable (parity: all even candidates, odd target)
        check(new int[]{2, 4, 6}, 7, ll());
        // Larger target with three small candidates (multiple combos), exact match
        check(new int[]{2, 3, 5}, 10, ll(
            l(2, 2, 2, 2, 2), l(2, 2, 3, 3), l(2, 3, 5), l(5, 5)));
        // Same input verified by property (sums equal target, no duplicates)
        checkSumsAndDistinct("sums [2,3,5] t=10", new int[]{2, 3, 5}, 10);
        // All candidates greater than target -> empty
        check(new int[]{10, 20, 30}, 9, ll());
        // Heavy-reuse single small candidate to near-max target
        check(new int[]{2}, 40, ll(l(2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2)));
        // Distinct candidates, target hits exactly one multi-combo
        check(new int[]{3, 6, 9}, 9, ll(l(3, 3, 3), l(3, 6), l(9)));
        // Property checks (sums equal target, no duplicates)
        checkSumsAndDistinct("sums [2,3,6,7] t=18", new int[]{2, 3, 6, 7}, 18);
        checkSumsAndDistinct("sums [3,4,5] t=20", new int[]{3, 4, 5}, 20);
        checkSumsAndDistinct("sums [2,3,5,7,11] t=15", new int[]{2, 3, 5, 7, 11}, 15);
        // Empty result property: no combo can sum to target
        checkEmpty("empty [5,10] t=3", new int[]{5, 10}, 3);

        // ----- Scale / performance cases -----
        // Worst-case reuse explosion: smallest candidate {2} with max target 40 and
        // also a denser candidate set, near the constraint upper bounds, timed.
        scaleCheck("scale dense candidates near max target", new int[]{2, 3, 4, 5, 6, 7}, 40);
        scaleCheck("scale single-2 to max target", new int[]{2}, 40);
        // Random distinct candidate set (seed 42) within constraints, property-verified.
        scaleRandom("scale random candidates t=40");

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static void check(int[] candidates, int target, List<List<Integer>> expected) {
        total++;
        String in = Arrays.toString(candidates) + ", target=" + target;
        try {
            List<List<Integer>> actual = new Answer().combinationSum(candidates, target);
            List<String> a = norm(actual);
            List<String> e = norm(expected);
            if (a.equals(e)) {
                System.out.println("\033[32m[PASS]\033[0m combinationSum(" + in + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m combinationSum(" + in + ")"
                    + " expected=" + e + " actual=" + a);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m combinationSum(" + in + ") threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkNoDup(String name, int[] candidates, int target) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().combinationSum(candidates, target);
            Set<String> seen = new HashSet<>(norm(actual));
            boolean sumsOk = true;
            for (List<Integer> c : actual) {
                int s = 0;
                for (int v : c) s += v;
                if (s != target) { sumsOk = false; break; }
            }
            if (seen.size() == actual.size() && sumsOk) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " dupOrBadSum actual=" + norm(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property: every combo sums to target, uses only candidates, and combos are distinct.
    static void checkSumsAndDistinct(String name, int[] candidates, int target) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().combinationSum(candidates, target);
            Set<Integer> allowed = new HashSet<>();
            for (int v : candidates) allowed.add(v);
            boolean ok = true;
            for (List<Integer> c : actual) {
                int s = 0;
                for (int v : c) {
                    if (!allowed.contains(v)) { ok = false; break; }
                    s += v;
                }
                if (s != target) { ok = false; }
                if (!ok) break;
            }
            Set<String> distinct = new HashSet<>(norm(actual));
            if (ok && distinct.size() == actual.size()) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + actual.size() + ")");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " ok=" + ok
                    + " distinct=" + distinct.size() + " size=" + actual.size());
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkEmpty(String name, int[] candidates, int target) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().combinationSum(candidates, target);
            if (actual.isEmpty()) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected empty actual=" + norm(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Scale case: property-verify (all combos sum to target, distinct) and time it.
    static void scaleCheck(String name, int[] candidates, int target) {
        total++;
        try {
            long t0 = System.nanoTime();
            List<List<Integer>> actual = new Answer().combinationSum(candidates, target);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

            Set<Integer> allowed = new HashSet<>();
            for (int v : candidates) allowed.add(v);
            boolean ok = true;
            for (List<Integer> c : actual) {
                int s = 0;
                for (int v : c) {
                    if (!allowed.contains(v)) { ok = false; break; }
                    s += v;
                }
                if (s != target) ok = false;
                if (!ok) break;
            }
            Set<String> distinct = new HashSet<>(norm(actual));
            boolean good = ok && distinct.size() == actual.size() && elapsedMs <= 3000;
            if (good) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " ok=" + ok
                    + " distinct=" + distinct.size() + " size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms (budget 3000ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Random distinct candidates in [2,40], target 40, property-verified and timed.
    static void scaleRandom(String name) {
        total++;
        try {
            Random rnd = new Random(42);
            List<Integer> pool = new ArrayList<>();
            for (int v = 2; v <= 40; v++) pool.add(v);
            Collections.shuffle(pool, rnd);
            // Move value 2 to the front (swap, not overwrite) so candidates stay DISTINCT
            // while guaranteeing a small candidate exists so combinations are reachable.
            int idx2 = pool.indexOf(2);
            Collections.swap(pool, 0, idx2);
            int k = 8;
            int[] candidates = new int[k];
            for (int i = 0; i < k; i++) candidates[i] = pool.get(i);
            int target = 40;

            long t0 = System.nanoTime();
            List<List<Integer>> actual = new Answer().combinationSum(candidates, target);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

            Set<Integer> allowed = new HashSet<>();
            for (int v : candidates) allowed.add(v);
            boolean ok = true;
            for (List<Integer> c : actual) {
                int s = 0;
                for (int v : c) {
                    if (!allowed.contains(v)) { ok = false; break; }
                    s += v;
                }
                if (s != target) ok = false;
                if (!ok) break;
            }
            Set<String> distinct = new HashSet<>(norm(actual));
            boolean good = ok && distinct.size() == actual.size() && elapsedMs <= 3000;
            if (good) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " candidates=" + Arrays.toString(candidates)
                    + " size=" + actual.size() + " elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " ok=" + ok
                    + " distinct=" + distinct.size() + " size=" + actual.size()
                    + " elapsed=" + elapsedMs + "ms (budget 3000ms)");
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
