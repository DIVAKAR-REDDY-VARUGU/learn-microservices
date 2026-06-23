import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static String label(int[] a) {
        if (a.length <= 20) return Arrays.toString(a);
        return "<len=" + a.length + ">";
    }

    static void check(int[] heights, int expected) {
        total++;
        String in = label(heights);
        try {
            int actual = new Answer().largestRectangleArea(heights.clone());
            long want = brute(heights); // validate vs the correct O(n^2) oracle (some typed-in expecteds were wrong)
            if (actual == want) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m largestRectangleArea(" + in + ")");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m largestRectangleArea(" + in + ") | expected=" + want + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m largestRectangleArea(" + in + ") | expected=" + expected + " threw: " + t);
        }
    }

    // Independent O(n^2) brute-force oracle (small/medium inputs).
    static long brute(int[] h) {
        int n = h.length;
        long best = 0;
        for (int i = 0; i < n; i++) {
            int minH = h[i];
            for (int j = i; j < n; j++) {
                if (h[j] < minH) minH = h[j];
                long area = (long) minH * (j - i + 1);
                if (area > best) best = area;
            }
        }
        return best;
    }

    // Independent O(n) monotonic-stack oracle (large inputs).
    static long oracle(int[] h) {
        int n = h.length;
        Deque<Integer> st = new ArrayDeque<>();
        long best = 0;
        for (int i = 0; i <= n; i++) {
            int cur = (i == n) ? 0 : h[i];
            while (!st.isEmpty() && h[st.peek()] >= cur) {
                int height = h[st.pop()];
                int left = st.isEmpty() ? -1 : st.peek();
                long width = i - left - 1;
                long area = (long) height * width;
                if (area > best) best = area;
            }
            st.push(i);
        }
        return best;
    }

    static void checkLarge(String name, int[] heights, long expected) {
        total++;
        try {
            long t0 = System.nanoTime();
            int actual = new Answer().largestRectangleArea(heights.clone());
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = ((long) actual == expected) && ms <= 3000;
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + actual + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected=" + expected + " actual=" + actual + " (" + ms + " ms" + (ms > 3000 ? ", OVER BUDGET" : "") + ")");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected=" + expected + " threw: " + t);
        }
    }

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check(new int[]{2,1,5,6,2,3}, 10);
        check(new int[]{2,4}, 4);
        check(new int[]{6,2,5,4,5,1,6}, 12);
        // Single bar
        check(new int[]{5}, 5);
        // Single zero-height bar (min bound 0)
        check(new int[]{0}, 0);
        // All zeros
        check(new int[]{0,0,0}, 0);
        // All equal
        check(new int[]{3,3,3}, 9);
        check(new int[]{4,4,4,4}, 16);
        // Strictly increasing — best is the tallest few; [1,2,3,4,5] -> 3*3=9
        check(new int[]{1,2,3,4,5}, 9);
        // Strictly decreasing — symmetric -> 9
        check(new int[]{5,4,3,2,1}, 9);
        // Zeros interspersed
        check(new int[]{0,1,0,1}, 1);
        check(new int[]{1,0,1,0}, 1);
        // Valley: flanked tall bars cannot merge across the dip
        check(new int[]{5,1,5}, 5);
        // Two bars descending
        check(new int[]{4,2}, 4);
        // Plateau in the middle
        check(new int[]{2,3,3,3,2}, 10);
        // Tall single spike among shorts
        check(new int[]{1,1,1,10,1,1,1}, 10);
        // Large height bound
        check(new int[]{10000,10000}, 20000);
        // Two equal then taller
        check(new int[]{2,2,2,6}, 6);

        // ===== ADDED: more corner cases =====
        // Single bar at max height bound
        check(new int[]{10000}, 10000);
        // Single bar at min (zero)
        check(new int[]{0}, 0);
        // Two zeros
        check(new int[]{0,0}, 0);
        // Leading and trailing zeros around a block
        check(new int[]{0,0,3,3,0,0}, 6);
        // Zero in the middle splits the histogram
        check(new int[]{4,4,0,4,4}, 8);
        // Strictly increasing longer: [1,2,3,4,5,6] -> best 3*4=12
        check(new int[]{1,2,3,4,5,6}, 12);
        // Strictly decreasing longer mirror -> 12
        check(new int[]{6,5,4,3,2,1}, 12);
        // Alternating high/low — each tall bar isolated
        check(new int[]{9,1,9,1,9}, 9);
        // Alternating but wide enough lows to matter
        check(new int[]{2,1,2,1,2,1}, 6);
        // Ascending then descending (mountain) -> 4 wide of height 3 etc
        check(new int[]{1,2,3,2,1}, 6);
        // Plateau spanning whole array of equal tall bars
        check(new int[]{7,7,7,7,7}, 35);
        // Single tall surrounded by zeros
        check(new int[]{0,10000,0}, 10000);
        // Wide low plateau beats narrow tall spike
        check(new int[]{1,1,1,1,1,1,5}, 7);
        // Narrow tall spike beats wide low (height 100 width 1 vs height 1 width 6)
        check(new int[]{1,1,1,1,1,1,100}, 100);
        // Step pyramid
        check(new int[]{2,4,6,4,2}, 12);
        // Off-by-one: best rectangle uses exactly the two tallest adjacent bars
        check(new int[]{1,100,100,1}, 200);
        // Heavy duplicates of mid height with single taller
        check(new int[]{3,3,3,3,9}, 12);
        // Validate a hand array against brute oracle
        {
            int[] in = {6,2,5,4,5,1,6};
            check(in, (int) brute(in));
        }

        // ===== ADDED: random small inputs vs brute-force oracle =====
        Random rnd = new Random(42);
        {
            long t0 = System.nanoTime();
            boolean allOk = true;
            int firstBad = -1;
            for (int t = 0; t < 400; t++) {
                int n = 1 + rnd.nextInt(40);
                int[] in = new int[n];
                for (int i = 0; i < n; i++) in[i] = rnd.nextInt(20); // small heights incl 0
                long exp = brute(in);
                long act;
                try { act = new Answer().largestRectangleArea(in.clone()); } catch (Throwable th) { act = exp + 1; }
                if (act != exp) { allOk = false; firstBad = t; break; }
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            total++;
            if (allOk && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m 400 random histograms vs brute oracle (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m random histograms vs brute oracle | firstBad=" + firstBad + " (" + ms + " ms)");
            }
        }

        // ===== ADDED: large / performance / scale (near 10^5) =====
        // Large random heights; verified against O(n) oracle.
        {
            int n = 100000;
            int[] in = new int[n];
            for (int i = 0; i < n; i++) in[i] = rnd.nextInt(10001); // [0,10^4]
            checkLarge("large random n=" + n, in, oracle(in));
        }
        // Large strictly increasing — forces stack to grow to full length (worst case).
        {
            int n = 100000;
            int[] in = new int[n];
            for (int i = 0; i < n; i++) in[i] = i % 10001; // ramps 0..10000 repeating
            checkLarge("large ramp n=" + n, in, oracle(in));
        }
        // Large all-equal at max height — single huge rectangle = 10000 * n.
        {
            int n = 100000;
            int[] in = new int[n];
            Arrays.fill(in, 10000);
            long exp = (long) 10000 * n; // exceeds int -> must be compared as long via oracle
            // NOTE: optimal answer here overflows int (1e9), but n*10000=1e9 fits in int? 1e5*1e4=1e9 < 2.147e9, fits.
            checkLarge("large all-equal max n=" + n, in, oracle(in));
        }
        // Large strictly decreasing — symmetric worst case (stack pops every step).
        {
            int n = 100000;
            int[] in = new int[n];
            for (int i = 0; i < n; i++) in[i] = 10000 - (i % 10001);
            checkLarge("large descending n=" + n, in, oracle(in));
        }
        // Large mountain: increase to a peak then decrease.
        {
            int n = 100000;
            int[] in = new int[n];
            int half = n / 2;
            for (int i = 0; i < n; i++) {
                int d = Math.min(i, n - 1 - i); // distance to nearer end
                in[i] = Math.min(10000, d); // grows then shrinks
            }
            checkLarge("large mountain n=" + n, in, oracle(in));
        }
        // Large with many zeros breaking it into tiny segments (no giant rectangle).
        {
            int n = 100000;
            int[] in = new int[n];
            for (int i = 0; i < n; i++) in[i] = (i % 2 == 0) ? 0 : rnd.nextInt(10001);
            checkLarge("large zero-broken n=" + n, in, oracle(in));
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
