import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Example cases from QUESTION.md =====
        check(new int[]{3, 6, 7, 11}, 8, 4);
        check(new int[]{30, 11, 23, 4, 20}, 5, 30);
        check(new int[]{30, 11, 23, 4, 20}, 6, 23);

        // Single pile
        // h == number of piles -> must eat the whole pile in 1 hour -> k = pile size
        check(new int[]{10}, 1, 10);
        // Lots of hours for a single pile -> minimum speed is 1
        check(new int[]{10}, 10, 1);
        check(new int[]{10}, 100, 1);
        // Single pile of size 1
        check(new int[]{1}, 1, 1);

        // h equals piles.length (tightest): k must be max(piles)
        check(new int[]{1, 2, 3, 4, 5}, 5, 5);
        check(new int[]{7, 7, 7}, 3, 7);
        check(new int[]{1000000000}, 1, 1000000000); // large pile, h=1

        // All equal piles
        check(new int[]{4, 4, 4, 4}, 4, 4);   // h == length -> k = 4
        check(new int[]{4, 4, 4, 4}, 8, 2);   // double the hours -> k = 2
        check(new int[]{4, 4, 4, 4}, 16, 1);  // plenty of hours -> k = 1

        // Very large h relative to piles -> minimum speed 1
        check(new int[]{1, 1, 1, 1}, 1000000000, 1);
        check(new int[]{2, 2}, 1000000000, 1);

        // Mixed sizes
        check(new int[]{312884470}, 968709470, 1); // single huge pile, tons of hours
        check(new int[]{332484035, 524908576, 855865114, 632922376, 222257295, 690155293, 112677673, 679580077, 337406589, 290818316, 877337160, 901728858, 679284947, 688210097, 692137887, 718203285, 629455728, 941802108}, 823855818, 14);

        // Two piles, ample time
        check(new int[]{3, 6}, 3, 3);  // h > length, speed 3 finishes 6 in 2h + 3 in 1h = 3h
        check(new int[]{3, 6}, 2, 6);  // h == length -> k = max = 6

        // Known LeetCode-style case
        check(new int[]{805306368, 805306368, 805306368}, 1000000000, 3);

        // ===== NEW CORNER CASES =====
        // h == length always forces k = max(piles) (tightest deadline)
        check(new int[]{1}, 1, 1);                         // smallest possible everything
        check(new int[]{9, 1, 1, 1}, 4, 9);               // max dominates
        check(new int[]{1, 1, 1, 9}, 4, 9);               // max at end
        check(new int[]{1000000000, 1}, 2, 1000000000);   // extreme max with h==length

        // Off-by-one around the deadline: one extra hour can drop k below max.
        check(new int[]{8}, 2, 4);     // 8 bananas, 2 hours -> 4 per hour
        check(new int[]{8}, 3, 3);     // 8 bananas, 3 hours -> ceil(8/3)=3
        check(new int[]{8}, 4, 2);     // 8 bananas, 4 hours -> 2
        check(new int[]{8}, 8, 1);     // 8 bananas, 8 hours -> 1
        check(new int[]{8}, 7, 2);     // 8 bananas, 7 hours -> ceil(8/7)=2 (off-by-one)

        // Strictly increasing piles
        check(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 10, 10); // h==length -> max
        check(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 20, 5);

        // All-equal large value, h slightly bigger than length
        check(new int[]{6, 6, 6}, 4, 6);   // still need 6 (each pile is its own hour at k=6, 3h <= 4)
        check(new int[]{6, 6, 6}, 6, 3);   // 6 hours -> k=3 (each pile 2h => 6h)

        // Maximum constraint values: piles[i] up to 1e9, h up to 1e9
        check(new int[]{1000000000, 1000000000, 1000000000}, 3, 1000000000); // h==length
        check(new int[]{1000000000}, 1000000000, 1);                          // max h, single pile
        check(new int[]{1000000000, 1000000000}, 1000000000, 3);              // 2e9 bananas in 1e9 h -> ceil(1e9/(1e9-1))*? property-checked below too

        // Two piles forcing a non-trivial mid speed
        check(new int[]{3, 6, 7, 11}, 5, 11);   // tight: h==length-1+? -> needs 11
        check(new int[]{3, 6, 7, 11}, 10, 4);    // matches example monotonicity (slower deadline)
        check(new int[]{25, 10, 23, 4}, 4, 25);  // h==length -> max

        // ===== LARGE / PERFORMANCE CASES =====
        Random rnd = new Random(42);

        // Big random pile set near the upper bound n ~ 10^4, values up to 1e9.
        // Verify result by PROPERTY: k feasible within h, and (k-1) NOT feasible (true minimum),
        // and k is within [1, max(piles)]. hoursNeeded computed by an independent O(n) oracle.
        int N = 10000;
        int[] big = new int[N];
        long maxPile = 0;
        for (int i = 0; i < N; i++) {
            big[i] = 1 + rnd.nextInt(1_000_000_000);
            maxPile = Math.max(maxPile, big[i]);
        }
        // pick an h comfortably above N so the answer is an interesting interior value
        long hLarge = (long) N * 50; // 500000 hours
        long t0 = System.nanoTime();
        boolean ok0;
        try {
            int k = new Answer().minEatingSpeed(big, (int) Math.min(hLarge, 1_000_000_000L));
            int hh = (int) Math.min(hLarge, 1_000_000_000L);
            boolean inRange = k >= 1 && k <= maxPile;
            boolean feasible = hoursNeeded(big, k) <= hh;
            boolean minimal = (k == 1) || hoursNeeded(big, k - 1) > hh;
            ok0 = inRange && feasible && minimal;
        } catch (Throwable t) { ok0 = false; }
        long e0 = (System.nanoTime() - t0) / 1_000_000;
        propertyCase("LARGE random n=" + N + " h=" + Math.min(hLarge, 1_000_000_000L)
                + " feasible&minimal (" + e0 + " ms)", ok0 && e0 <= 3000);

        // h == length on a big array -> answer MUST equal max(piles).
        long t1 = System.nanoTime();
        boolean ok1;
        try {
            int k = new Answer().minEatingSpeed(big, N);
            ok1 = (k == (int) maxPile);
        } catch (Throwable t) { ok1 = false; }
        long e1 = (System.nanoTime() - t1) / 1_000_000;
        propertyCase("LARGE h==length n=" + N + " -> max(piles)=" + maxPile + " (" + e1 + " ms)",
                ok1 && e1 <= 3000);

        // h enormous (1e9) relative to a big array of small-ish piles -> k must be 1.
        int[] smallPiles = new int[N];
        for (int i = 0; i < N; i++) smallPiles[i] = 1 + rnd.nextInt(5); // 1..5
        long t2 = System.nanoTime();
        boolean ok2;
        try {
            int k = new Answer().minEatingSpeed(smallPiles, 1_000_000_000);
            ok2 = (k == 1);
        } catch (Throwable t) { ok2 = false; }
        long e2 = (System.nanoTime() - t2) / 1_000_000;
        propertyCase("LARGE huge-h tiny-piles n=" + N + " -> k=1 (" + e2 + " ms)",
                ok2 && e2 <= 3000);

        // Many independent random instances, each validated by feasible & minimal property.
        long t3 = System.nanoTime();
        boolean ok3 = true;
        try {
            for (int t = 0; t < 200; t++) {
                int n = 1 + rnd.nextInt(2000);
                int[] p = new int[n];
                long mx = 0;
                for (int i = 0; i < n; i++) { p[i] = 1 + rnd.nextInt(1_000_000); mx = Math.max(mx, p[i]); }
                int h = n + rnd.nextInt(5 * n + 1); // h in [n, 6n]
                int k = new Answer().minEatingSpeed(p, h);
                boolean inRange = k >= 1 && k <= mx;
                boolean feasible = hoursNeeded(p, k) <= h;
                boolean minimal = (k == 1) || hoursNeeded(p, k - 1) > h;
                if (!(inRange && feasible && minimal)) { ok3 = false; break; }
            }
        } catch (Throwable t) { ok3 = false; }
        long e3 = (System.nanoTime() - t3) / 1_000_000;
        propertyCase("LARGE 200 random instances feasible&minimal (" + e3 + " ms)",
                ok3 && e3 <= 3000);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent O(n) oracle: hours required to finish all piles at speed k (ceil division).
    static long hoursNeeded(int[] piles, long k) {
        if (k <= 0) return Long.MAX_VALUE;
        long h = 0;
        for (int p : piles) h += (p + k - 1) / k; // ceil(p/k)
        return h;
    }

    static void propertyCase(String label, boolean ok) {
        total++;
        if (ok) {
            pass++;
            System.out.println("\033[32m[PASS]\033[0m " + label);
        } else {
            System.out.println("\033[31m[FAIL]\033[0m " + label);
        }
    }

    static void check(int[] piles, int h, int expected) {
        total++;
        try {
            int actual = new Answer().minEatingSpeed(piles, h);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m minEatingSpeed(" + brief(piles) + ", h=" + h + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m minEatingSpeed(" + brief(piles) + ", h=" + h + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m minEatingSpeed(" + brief(piles) + ", h=" + h + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Keep output readable for large arrays
    static String brief(int[] a) {
        if (a.length <= 8) return Arrays.toString(a);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 6; i++) sb.append(a[i]).append(", ");
        sb.append("... len=").append(a.length).append("]");
        return sb.toString();
    }
}
