import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static String label(int[] a) {
        if (a.length <= 20) return Arrays.toString(a);
        return "<len=" + a.length + ">";
    }

    static void check(int[] input, int[] expected) {
        total++;
        String in = label(input);
        String exp = expected.length <= 20 ? Arrays.toString(expected) : "<len=" + expected.length + ">";
        try {
            int[] actual = new Answer().dailyTemperatures(input.clone());
            if (Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m dailyTemperatures(" + in + ")");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m dailyTemperatures(" + in + ") | expected=" + exp + " actual=" + (actual == null ? "null" : (actual.length <= 20 ? Arrays.toString(actual) : "<len=" + actual.length + ">")));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m dailyTemperatures(" + in + ") | expected=" + exp + " threw: " + t);
        }
    }

    // Independent O(n^2) brute-force oracle (used only on small/medium inputs).
    static int[] brute(int[] t) {
        int n = t.length;
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (t[j] > t[i]) { ans[i] = j - i; break; }
            }
        }
        return ans;
    }

    // Independent O(n) monotonic-stack oracle (used for the large case).
    static int[] oracle(int[] t) {
        int n = t.length;
        int[] ans = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && t[i] > t[st.peek()]) {
                int j = st.pop();
                ans[j] = i - j;
            }
            st.push(i);
        }
        return ans;
    }

    static void checkLarge(String name, int[] input, int[] expected) {
        total++;
        try {
            long t0 = System.nanoTime();
            int[] actual = new Answer().dailyTemperatures(input.clone());
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = Arrays.equals(actual, expected) && ms <= 3000;
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | match=" + Arrays.equals(actual, expected) + " (" + ms + " ms" + (ms > 3000 ? ", OVER BUDGET" : "") + ")");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw: " + t);
        }
    }

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check(new int[]{73,74,75,71,69,72,76,73}, new int[]{1,1,4,2,1,1,0,0});
        check(new int[]{30,40,50,60}, new int[]{1,1,1,0});
        check(new int[]{30,60,90}, new int[]{1,1,0});
        // Single element
        check(new int[]{50}, new int[]{0});
        // Two elements increasing
        check(new int[]{30,31}, new int[]{1,0});
        // Two elements decreasing
        check(new int[]{31,30}, new int[]{0,0});
        // All equal — never warmer
        check(new int[]{50,50,50}, new int[]{0,0,0});
        // Strictly decreasing — all zeros
        check(new int[]{90,80,70,60}, new int[]{0,0,0,0});
        // Strictly increasing — all ones except last
        check(new int[]{30,40,50,60,70}, new int[]{1,1,1,1,0});
        // Min/max bounds of constraint (30..100)
        check(new int[]{30,100}, new int[]{1,0});
        check(new int[]{100,30}, new int[]{0,0});
        check(new int[]{100,100}, new int[]{0,0});
        // Plateau then rise
        check(new int[]{70,70,71}, new int[]{2,1,0});
        // Duplicates with a later warmer day far away
        check(new int[]{70,70,70,70,71}, new int[]{4,3,2,1,0});
        // Dip then recover
        check(new int[]{73,72,71,76}, new int[]{3,2,1,0});
        // Warmer immediately then cold tail
        check(new int[]{55,60,40,30}, new int[]{1,0,0,0});

        // ===== ADDED: more corner cases =====
        // Min bound only (all 30) — never warmer
        check(new int[]{30,30,30,30}, new int[]{0,0,0,0});
        // Max bound only (all 100) — never warmer
        check(new int[]{100,100,100}, new int[]{0,0,0});
        // Min then max — single jump
        check(new int[]{30,30,30,100}, new int[]{3,2,1,0});
        // Max then climbing toward but never exceeding -> all zero
        check(new int[]{100,99,98,97}, new int[]{0,0,0,0});
        // Full bound sweep increasing
        check(new int[]{30,31,32,33,100}, new int[]{1,1,1,1,0});
        // Alternating high/low — warmer is +2 except where blocked
        check(new int[]{40,30,40,30,40}, new int[]{0,1,0,1,0});
        // Alternating low/high
        check(new int[]{30,40,30,40,30}, new int[]{1,0,1,0,0});
        // Single big drop then big rise
        check(new int[]{100,30,100}, new int[]{0,1,0});
        // Strictly decreasing across full range then nothing warmer
        check(new int[]{60,55,50,45,40,35,30}, new int[]{0,0,0,0,0,0,0});
        // Sawtooth where each peak waits a long time
        check(new int[]{50,49,48,47,51}, new int[]{4,3,2,1,0});
        // Last element is the global max so everyone before waits to it (when decreasing prefix)
        check(new int[]{40,39,38,37,41}, new int[]{4,3,2,1,0});
        // Heavy duplicates then a single bump
        check(new int[]{60,60,60,60,60,61}, new int[]{5,4,3,2,1,0});
        // Warmer is the very next day for everyone (strictly increasing length 6)
        check(new int[]{30,40,50,60,70,80}, new int[]{1,1,1,1,1,0});
        // Two-element equal at bounds
        check(new int[]{30,30}, new int[]{0,0});
        // Peak in the middle
        check(new int[]{30,50,100,50,30}, new int[]{1,1,0,0,0});
        // Off-by-one shape: warmer exactly one step beyond a plateau
        check(new int[]{70,70,70,71,70}, new int[]{3,2,1,0,0});
        // Validate a hand array against brute oracle
        {
            int[] in = {73,74,75,71,69,72,76,73};
            check(in, brute(in));
        }

        // ===== ADDED: random small inputs vs brute-force oracle =====
        Random rnd = new Random(42);
        {
            long t0 = System.nanoTime();
            boolean allOk = true;
            int firstBad = -1;
            for (int t = 0; t < 300; t++) {
                int n = 1 + rnd.nextInt(60);
                int[] in = new int[n];
                for (int i = 0; i < n; i++) in[i] = 30 + rnd.nextInt(71); // [30,100]
                int[] exp = brute(in);
                int[] act;
                try { act = new Answer().dailyTemperatures(in.clone()); } catch (Throwable th) { act = null; }
                if (!Arrays.equals(act, exp)) { allOk = false; firstBad = t; break; }
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            total++;
            if (allOk && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m 300 random arrays vs brute oracle (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m random arrays vs brute oracle | firstBad=" + firstBad + " (" + ms + " ms)");
            }
        }

        // ===== ADDED: large / performance / scale =====
        // Large random array near upper bound (10^5). Verify vs O(n) oracle.
        {
            int n = 100000;
            int[] in = new int[n];
            for (int i = 0; i < n; i++) in[i] = 30 + rnd.nextInt(71);
            checkLarge("large random n=" + n, in, oracle(in));
        }
        // Large strictly increasing — worst case for popping (answer all 1s except last).
        {
            int n = 71_000; // values must stay within [30,100]; wrap pattern instead
            // Build an array that is increasing within range by cycling but keep property check via oracle.
            int m = 100000;
            int[] in = new int[m];
            for (int i = 0; i < m; i++) in[i] = 30 + (i % 71); // 30..100 repeating
            checkLarge("large cyclic ramp n=" + m, in, oracle(in));
        }
        // Large strictly decreasing then one big rise — stack grows to full size before draining.
        {
            int n = 100000;
            int[] in = new int[n];
            // decreasing-ish: high to low repeated, then guaranteed property via oracle
            for (int i = 0; i < n - 1; i++) in[i] = 100 - (i % 70); // stays in [31,100]
            in[n - 1] = 100;
            checkLarge("large descending-blocks n=" + n, in, oracle(in));
        }
        // Large all-equal — no warmer day, answer all zeros; cheap but tests no accidental O(n^2).
        {
            int n = 100000;
            int[] in = new int[n];
            Arrays.fill(in, 50);
            int[] exp = new int[n]; // all zeros
            checkLarge("large all-equal n=" + n, in, exp);
        }
        // Large monotonic strictly increasing using only two distinct levels can't represent; use oracle on random with few distinct values (heavy dup).
        {
            int n = 100000;
            int[] in = new int[n];
            for (int i = 0; i < n; i++) in[i] = (rnd.nextInt(3) == 0) ? 100 : 30; // heavy duplicates, two values
            checkLarge("large two-value heavy-dup n=" + n, in, oracle(in));
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
