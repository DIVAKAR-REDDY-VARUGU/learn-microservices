import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ---- existing cases (kept) ----
        // order-independent answers: sort both before comparing
        check("doc example 1", new int[]{1,1,1,2,2,3}, 2, new int[]{1,2});
        check("doc example 2 (single element)", new int[]{1}, 1, new int[]{1});
        check("doc example 3", new int[]{4,4,4,6,6,2,2,2,2}, 2, new int[]{2,4});

        check("k = all distinct", new int[]{5,3,1,5,3,1,9}, 4, new int[]{1,3,5,9});
        check("all equal, k=1", new int[]{7,7,7,7}, 1, new int[]{7});
        check("negatives and zeros", new int[]{0,0,-1,-1,-1,-2}, 2, new int[]{-1,0});
        check("min/max bounds", new int[]{-10000,-10000,10000,10000,10000,3}, 2, new int[]{-10000,10000});
        check("two distinct, both returned", new int[]{2,2,1}, 2, new int[]{1,2});
        check("already grouped", new int[]{8,8,8,9,9,4}, 1, new int[]{8});
        check("k equals distinct count single each", new int[]{1,2,3}, 3, new int[]{1,2,3});
        check("larger frequency spread", new int[]{1,1,1,1,2,2,2,3,3,4}, 3, new int[]{1,2,3});

        // ---- NEW corner cases ----
        check("single element array, k=1", new int[]{0}, 1, new int[]{0});
        check("single min bound element", new int[]{-10000}, 1, new int[]{-10000});
        check("single max bound element", new int[]{10000}, 1, new int[]{10000});
        check("all distinct, k=1 (each freq 1, unique by value)", new int[]{10,20,30,40}, 1, new int[]{10});
        check("strictly increasing all once, k=all", new int[]{1,2,3,4,5}, 5, new int[]{1,2,3,4,5});
        check("strictly decreasing all once, k=all", new int[]{5,4,3,2,1}, 5, new int[]{1,2,3,4,5});
        check("alternating two values", new int[]{1,2,1,2,1,2,1}, 2, new int[]{1,2});
        check("heavy duplicates one dominant", new int[]{9,9,9,9,9,9,1,2,3}, 1, new int[]{9});
        check("all negative distinct freqs", new int[]{-1,-1,-1,-2,-2,-3}, 2, new int[]{-1,-2});
        check("zeros dominate", new int[]{0,0,0,0,5,5,7}, 2, new int[]{0,5});
        check("clear frequency ladder pick top2", new int[]{4,4,4,4,3,3,3,2,2,1}, 2, new int[]{3,4});
        check("mixed signs distinct freqs", new int[]{-5,-5,-5,3,3,7}, 2, new int[]{-5,3});
        check("k = distinct count returns all", new int[]{8,8,2,2,2,5}, 3, new int[]{2,5,8});
        check("two equal-large blocks plus tail", new int[]{1,1,1,2,2,2,3}, 2, new int[]{1,2});
        check("min and max both frequent", new int[]{-10000,-10000,-10000,10000,10000,0}, 2, new int[]{-10000,10000});
        check("off-by-one ladder top3", new int[]{7,7,7,6,6,5}, 3, new int[]{5,6,7});

        // ---- LARGE / PERFORMANCE / SCALE cases (property-verified via oracle) ----
        largeRandom("large random n=100000 k=10", 100000, 10);
        largeRandom("large random n=100000 k=50", 100000, 50);
        largeManyDistinct("large many-distinct n=100000 (each unique-ish) k=20", 100000, 20);
        largeSkewed("large skewed (few hot keys) n=100000 k=5", 100000, 5);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // independent oracle: the correct set of k most frequent values.
    // Returns null if the top-k set is NOT uniquely determined (a tie at the
    // boundary), in which case property checks fall back accordingly.
    static Set<Integer> oracleTopK(int[] nums, int k) {
        Map<Integer,Integer> freq = new HashMap<>();
        for (int v : nums) freq.merge(v, 1, Integer::sum);
        List<Map.Entry<Integer,Integer>> es = new ArrayList<>(freq.entrySet());
        es.sort((a,b) -> b.getValue() - a.getValue());
        // ensure uniqueness at the boundary
        if (k < es.size()) {
            int boundaryFreq = es.get(k - 1).getValue();
            int nextFreq = es.get(k).getValue();
            if (boundaryFreq == nextFreq) return null; // not unique
        }
        Set<Integer> top = new HashSet<>();
        for (int i = 0; i < k && i < es.size(); i++) top.add(es.get(i).getKey());
        return top;
    }

    // A returned set is a VALID top-k iff: exactly k distinct values, every returned value's
    // frequency >= the boundary (k-th largest) frequency, and every value strictly ABOVE the
    // boundary is included. Accepts any valid answer when the boundary frequency has ties.
    static boolean isValidTopK(int[] nums, int k, int[] actual) {
        if (actual == null || actual.length != k) return false;
        Map<Integer,Integer> freq = new HashMap<>();
        for (int v : nums) freq.merge(v, 1, Integer::sum);
        Set<Integer> got = new HashSet<>();
        for (int v : actual) got.add(v);
        if (got.size() != k) return false;
        List<Integer> freqs = new ArrayList<>(freq.values());
        freqs.sort(Collections.reverseOrder());
        int boundary = freqs.get(k - 1);
        for (int v : got) { Integer f = freq.get(v); if (f == null || f < boundary) return false; }
        for (Map.Entry<Integer,Integer> e : freq.entrySet())
            if (e.getValue() > boundary && !got.contains(e.getKey())) return false;
        return true;
    }

    static void check(String label, int[] nums, int k, int[] expected) {
        total++;
        try {
            int[] actual = new Answer().topKFrequent(nums.clone(), k);
            if (isValidTopK(nums, k, actual)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + " | input=" + Arrays.toString(nums) + ", k=" + k
                        + " | expected(any order)=" + Arrays.toString(expected)
                        + " | actual=" + Arrays.toString(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label
                    + " | input=" + Arrays.toString(nums) + ", k=" + k
                    + " | expected(any order)=" + Arrays.toString(expected)
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // build large input with a guaranteed-unique top-k, verify by property + time it.
    static void largeRandom(String label, int n, int k) {
        Random rnd = new Random(42);
        // assign distinct "hot" values with strictly separated frequencies to keep top-k unique
        int[] nums = new int[n];
        // hot values: -1..-k (frequencies decreasing & well separated), rest random cold filler
        int idx = 0;
        int baseFreq = 200;
        for (int h = 0; h < k; h++) {
            int count = baseFreq + (k - h) * 50; // strictly decreasing, gaps of 50
            for (int c = 0; c < count && idx < n; c++) nums[idx++] = -(h + 1);
        }
        // fill the rest with cold positive singletons-ish (freq 1 or 2, well below hot)
        while (idx < n) nums[idx++] = 10000 - (rnd.nextInt(9000)); // 1000..10000 range, sparse
        // shuffle
        for (int i = n - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int tmp = nums[i]; nums[i] = nums[j]; nums[j] = tmp; }
        runProperty(label, nums, k);
    }

    static void largeManyDistinct(String label, int n, int k) {
        Random rnd = new Random(42);
        int[] nums = new int[n];
        // hot values distinctly more frequent than the many distinct cold ones
        int idx = 0;
        for (int h = 0; h < k; h++) {
            int count = 50 + (k - h) * 5; // strictly decreasing
            for (int c = 0; c < count && idx < n; c++) nums[idx++] = -(h + 1);
        }
        while (idx < n) nums[idx++] = rnd.nextInt(20001) - 10000; // wide spread, mostly low freq
        for (int i = n - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int tmp = nums[i]; nums[i] = nums[j]; nums[j] = tmp; }
        // top-k uniqueness verified inside runProperty via oracle (skips if non-unique)
        runProperty(label, nums, k);
    }

    static void largeSkewed(String label, int n, int k) {
        Random rnd = new Random(42);
        int[] nums = new int[n];
        int idx = 0;
        // k hot keys taking the vast majority, strictly separated
        for (int h = 0; h < k; h++) {
            int count = (n / (k + 2)) - h * 100; // strictly decreasing
            for (int c = 0; c < count && idx < n; c++) nums[idx++] = -(h + 1);
        }
        while (idx < n) nums[idx++] = 1 + rnd.nextInt(50); // small cold pool, each well below hot
        // cold keys may individually reach moderate freq; ensure hot still dominate by sizing above
        for (int i = n - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int tmp = nums[i]; nums[i] = nums[j]; nums[j] = tmp; }
        runProperty(label, nums, k);
    }

    static void runProperty(String label, int[] nums, int k) {
        total++;
        try {
            Set<Integer> expected = oracleTopK(nums, k);
            long t0 = System.nanoTime();
            int[] actual = new Answer().topKFrequent(nums.clone(), k);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lab = label + " [" + elapsedMs + " ms]";
            boolean ok = actual != null && actual.length == k;
            // no duplicates in output
            if (ok) {
                Set<Integer> got = new HashSet<>();
                for (int v : actual) { if (!got.add(v)) { ok = false; break; } }
                if (ok && expected != null && !got.equals(expected)) ok = false;
                if (ok && expected == null) {
                    // boundary tie: at minimum every returned value must have frequency
                    // >= the k-th highest frequency. Verify that property instead.
                    ok = satisfiesFreqThreshold(nums, k, got);
                }
            }
            if (elapsedMs > 3000) ok = false; // catastrophic complexity guard
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + lab);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + lab
                        + " | n=" + nums.length + ", k=" + k
                        + " | expectedSet=" + expected
                        + " | actual=" + (actual == null ? "null" : Arrays.toString(actual)));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label
                    + " | n=" + nums.length + ", k=" + k
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // every value in got must have frequency >= the k-th largest frequency
    static boolean satisfiesFreqThreshold(int[] nums, int k, Set<Integer> got) {
        Map<Integer,Integer> freq = new HashMap<>();
        for (int v : nums) freq.merge(v, 1, Integer::sum);
        List<Integer> fs = new ArrayList<>(freq.values());
        fs.sort(Collections.reverseOrder());
        if (k > fs.size()) return false;
        int threshold = fs.get(k - 1);
        for (int v : got) {
            Integer f = freq.get(v);
            if (f == null || f < threshold) return false;
        }
        return got.size() == k;
    }
}
