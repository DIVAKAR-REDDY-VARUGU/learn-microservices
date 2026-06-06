import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        check(new int[]{1,2,5}, 11, 3);   // example: 5+5+1
        check(new int[]{2}, 3, -1);        // example: cannot form 3 from 2
        check(new int[]{1}, 0, 0);         // example: amount 0 -> 0 coins
        check(new int[]{5}, 0, 0);         // amount 0 with any coin -> 0
        check(new int[]{2}, 0, 0);         // amount 0 -> 0
        check(new int[]{1}, 1, 1);         // single coin exact
        check(new int[]{1}, 7, 7);         // only 1s
        check(new int[]{2}, 4, 2);         // 2+2
        check(new int[]{2}, 1, -1);        // odd amount, only even coin
        check(new int[]{1,2,5}, 100, 20);  // greedy works here: 20 fives
        check(new int[]{2,5,10,1}, 27, 4); // 10+10+5+2
        check(new int[]{186,419,83,408}, 6249, 20); // classic tricky case
        check(new int[]{1,5,10,25}, 30, 2); // 25+5
        check(new int[]{3,7}, 5, -1);      // not formable
        check(new int[]{5,10}, 1, -1);     // smaller than smallest coin
        check(new int[]{2147483647}, 2, -1); // huge coin, tiny amount
        check(new int[]{1,3,4}, 6, 2);     // 3+3 better than 4+1+1

        // ===== New corner cases =====
        check(new int[]{2147483647}, 0, 0);          // max-value coin, amount 0
        check(new int[]{2147483647}, 10000, -1);     // max coin, max amount -> not formable
        check(new int[]{1}, 10000, 10000);           // max amount with only 1s
        check(new int[]{1,2,5,10,20,50,100}, 10000, 100); // max amount, greedy-friendly
        check(new int[]{7}, 14, 2);                  // exact multiple
        check(new int[]{7}, 13, -1);                 // just off a multiple
        check(new int[]{3,5}, 7, -1);                // chicken-mcnugget style gap
        check(new int[]{3,5}, 8, 2);                 // 3+5
        check(new int[]{3,5}, 11, 3);                // 3+3+5
        check(new int[]{1,2,3,4,5,6,7,8,9,10,11,12}, 12, 1); // 12 distinct coins, pick the 12
        check(new int[]{5,7,11}, 1, -1);             // amount smaller than smallest coin
        check(new int[]{9,6,5,1}, 11, 2);            // 6+5 beats greedy 9+1+1
        check(new int[]{2,3}, 1, -1);                // unreachable 1
        check(new int[]{2,3}, 7, 3);                 // 2+2+3
        check(new int[]{25,10,5}, 30, 2);            // 25+5 (no pennies)
        check(new int[]{25,10,5}, 3, -1);            // cannot form 3
        check(new int[]{100}, 10000, 100);           // single coin, exact division at max amount

        // ===== ADDED corner cases (distinct from existing) =====
        // --- constraint boundary values ---
        check(new int[]{1}, 0, 0);                   // min coins.length, min coins[i], min amount
        check(new int[]{2147483647}, 1, -1);         // max coins[i], amount=1 -> unreachable
        check(new int[]{1}, 9999, 9999);             // off-by-one below max amount, only 1s
        check(new int[]{1,2,3,4,5,6,7,8,9,10,11,12}, 10000, 834); // max length AND max amount: 833*12 + 4 -> 833 twelves + one 4
        // --- all-equal coins (heavy duplicates) ---
        check(new int[]{5,5,5,5}, 20, 4);            // all equal, exact -> 4 fives
        check(new int[]{5,5,5,5}, 23, -1);           // all equal, not a multiple -> -1
        check(new int[]{3,3,3,3,3,3,3,3,3,3,3,3}, 9, 3); // 12 duplicate coins, 3+3+3
        // --- strictly increasing / decreasing / alternating ---
        check(new int[]{1,2,4,8,16,32,64,128,256,512}, 1000, 6); // increasing powers: 512+256+128+64+32+8
        check(new int[]{512,256,128,64,32,16,8,4,2,1}, 1023, 10); // strictly decreasing, needs all ten
        check(new int[]{1,100,2,99,3,98}, 200, 2);   // alternating-ish: 100+100
        // --- single coin / minimal shapes ---
        check(new int[]{6}, 6, 1);                   // single coin exact, one coin
        check(new int[]{6}, 0, 0);                   // single coin, amount 0
        // --- off-by-one shapes around a formable target ---
        check(new int[]{4,6}, 9, -1);                // 9 unreachable from {4,6} (all even)
        check(new int[]{4,6}, 10, 2);                // 4+6
        check(new int[]{4,6}, 8, 2);                 // 4+4 (off-by-one below 10)
        // --- large coins close to MAX but amount within reach of a small coin ---
        check(new int[]{2147483646,2147483647,1}, 5, 5); // giant coins useless, fall back to 1s
        // --- prime coins, greedy trap ---
        check(new int[]{1,7,10}, 14, 2);             // 7+7 beats 10+1+1+1+1
        check(new int[]{1,7,10}, 20, 2);             // 10+10

        // ===== Large / performance cases =====
        // amount near upper bound; verify by brute-force oracle on random small inputs,
        // and by an independent O(amount * coins) DP on the big ones.
        checkBruteRandom(0);
        checkBruteRandom(1);
        checkBruteRandom(2);
        checkBigDpOracle(new int[]{1,2,5,10,25,50}, 10000);
        checkBigDpOracle(new int[]{7,11,13,17,19,23,29,31}, 10000);
        checkBigDpOracle(new int[]{2,4,6,8}, 9999); // odd amount, all even coins -> -1

        // ===== ADDED large / performance cases (property-verified vs DP oracle, timed) =====
        // Max-length coin sets (12 coins) at the max amount (10^4), property-checked
        // against an independent DP oracle and FAILed only on a generous 3000 ms budget.
        checkBigDpOracle(new int[]{1,3,5,7,9,11,13,15,17,19,21,23}, 10000); // 12 odd coins
        checkBigDpOracle(new int[]{2,4,6,8,10,12,14,16,18,20,22,24}, 10000); // 12 even coins, even amount
        checkBigDpOracle(new int[]{2,4,6,8,10,12,14,16,18,20,22,24}, 9999);  // 12 even coins, odd amount -> -1
        checkBigDpOracle(new int[]{1}, 10000);                               // worst structural case: only 1s
        checkBigDpOracle(new int[]{9973,9974,9975,9999,10000}, 10000);       // all coins near the amount ceiling
        checkBigDpOracle(new int[]{2147483647,2147483646,2147483645,1}, 10000); // huge coins + a single 1
        // Randomized large cases with FIXED seed; verified by PROPERTY against the DP oracle.
        checkBigRandom(42);
        checkBigRandom(43);
        checkBigRandom(44);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent DP oracle (uses long internally, but answer fits int).
    static int dpOracle(int[] coins, int amount) {
        long INF = Integer.MAX_VALUE / 2;
        long[] dp = new long[amount + 1];
        Arrays.fill(dp, INF);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int c : coins) {
                if (c >= 1 && c <= i && dp[i - c] + 1 < dp[i]) {
                    dp[i] = dp[i - c] + 1;
                }
            }
        }
        return dp[amount] >= INF ? -1 : (int) dp[amount];
    }

    static void check(int[] coins, int amount, int expected) {
        total++;
        try {
            int actual = new Answer().coinChange(coins.clone(), amount);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m coinChange(" + brief(coins) + ", " + amount + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m coinChange(" + brief(coins) + ", " + amount + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m coinChange(" + brief(coins) + ", " + amount + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkBruteRandom(long seedOffset) {
        total++;
        String label = "brute-random seed=" + (42 + seedOffset);
        try {
            Random rnd = new Random(42 + seedOffset);
            int nCoins = 1 + rnd.nextInt(5);
            int[] coins = new int[nCoins];
            for (int i = 0; i < nCoins; i++) coins[i] = 1 + rnd.nextInt(15);
            int amount = rnd.nextInt(60); // small, oracle-friendly
            int expected = dpOracle(coins, amount);
            int actual = new Answer().coinChange(coins.clone(), amount);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " coinChange(" + brief(coins) + ", " + amount + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " coinChange(" + brief(coins) + ", " + amount + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkBigDpOracle(int[] coins, int amount) {
        total++;
        String label = "perf coinChange(" + brief(coins) + ", " + amount + ")";
        try {
            int expected = dpOracle(coins, amount);
            long start = System.nanoTime();
            int actual = new Answer().coinChange(coins.clone(), amount);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Large randomized case with FIXED seed. Builds a max-length coin set and a
    // near-upper-bound amount, then verifies the answer by PROPERTY against the
    // independent DP oracle (any valid minimum must match the oracle's minimum).
    // Timed with System.nanoTime(); FAILs only on a generous 3000 ms budget.
    static void checkBigRandom(long seed) {
        total++;
        String label = "big-random seed=" + seed;
        try {
            Random rnd = new Random(seed); // FIXED seed
            int nCoins = 12; // max coins.length
            // Distinct coins in [1, 10000] so the oracle DP stays O(amount * coins).
            LinkedHashSet<Integer> set = new LinkedHashSet<>();
            set.add(1 + rnd.nextInt(5)); // guarantee a small coin so most amounts are formable
            while (set.size() < nCoins) set.add(1 + rnd.nextInt(10000));
            int[] coins = new int[set.size()];
            int idx = 0;
            for (int v : set) coins[idx++] = v;
            int amount = 9000 + rnd.nextInt(1001); // near upper bound (10^4)

            int expected = dpOracle(coins, amount);
            long start = System.nanoTime();
            int actual = new Answer().coinChange(coins.clone(), amount);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            // PROPERTY: optimal count must equal the oracle's optimal count
            // (formability and minimality together).
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " coinChange(" + brief(coins) + ", " + amount + ") expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + elapsedMs + " ms");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " coinChange(" + brief(coins) + ", " + amount + ") = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static String brief(int[] coins) {
        if (coins.length <= 12) return Arrays.toString(coins);
        return "[len=" + coins.length + "]";
    }
}
