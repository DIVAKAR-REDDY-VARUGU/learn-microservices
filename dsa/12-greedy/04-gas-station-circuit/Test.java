import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        // Example 1
        check(new int[]{1,2,3,4,5}, new int[]{3,4,5,1,2}, 3);
        // Example 2: total gas < total cost -> impossible
        check(new int[]{2,3,4}, new int[]{3,4,3}, -1);
        // Example 3
        check(new int[]{5,1,2,3,4}, new int[]{4,4,1,5,1}, 4);

        // Single station, gas == cost -> start at 0
        check(new int[]{5}, new int[]{5}, 0);
        // Single station, gas > cost
        check(new int[]{3}, new int[]{1}, 0);
        // Single station, gas < cost -> impossible
        check(new int[]{1}, new int[]{3}, -1);
        // Single station, both zero -> start at 0 (0 cost loop)
        check(new int[]{0}, new int[]{0}, 0);
        // Two stations, start must be index 1
        check(new int[]{1,2}, new int[]{2,1}, 1);
        // Two stations, start at 0
        check(new int[]{4,1}, new int[]{1,4}, 0);
        // Total gas exactly equals total cost, valid start exists
        check(new int[]{2,3,4}, new int[]{3,4,2}, 2);
        // All gas == all cost -> any start works; unique guaranteed -> 0
        check(new int[]{2,2,2}, new int[]{2,2,2}, 0);
        // Start must wrap around to a later index
        check(new int[]{3,1,1}, new int[]{1,2,2}, 0);
        // Impossible: deficit at one station too large
        check(new int[]{1,1,1}, new int[]{2,2,2}, -1);
        // Larger valid case. NOTE: this input actually has TWO valid starts (3 and 4),
        // which violates the problem's uniqueness guarantee, so any standard greedy
        // returns 3. Kept verbatim as input but verified by property (returns *a* start
        // that completes the circuit) instead of a brittle, non-unique exact value.
        checkProperty(new int[]{6,1,4,3,5}, new int[]{3,5,5,3,3});
        // Zeros mixed in, still solvable
        check(new int[]{0,0,0,4}, new int[]{1,1,1,1}, 3);
        // Tight: only one valid start near the end
        check(new int[]{2,0,2}, new int[]{0,2,2}, 0);
        // Max-bound-ish values, surplus at start
        check(new int[]{10000,0}, new int[]{0,10000}, 0);

        // ===== NEW corner cases =====
        // Min boundary n=1, gas=0 cost>0 -> impossible
        check(new int[]{0}, new int[]{1}, -1);
        // Single station, max gas value, zero cost -> start 0
        check(new int[]{10000}, new int[]{0}, 0);
        // Single station, gas just below cost -> impossible
        check(new int[]{9999}, new int[]{10000}, -1);
        // All zeros, n>1 -> every step is free -> start 0
        check(new int[]{0,0,0,0,0}, new int[]{0,0,0,0,0}, 0);
        // All-equal positive gas and cost -> unique start 0
        check(new int[]{7,7,7,7}, new int[]{7,7,7,7}, 0);
        // Strictly increasing gas vs constant cost; surplus builds, start 0
        check(new int[]{1,2,3,4,5}, new int[]{1,1,1,1,1}, 0);
        // Strictly decreasing gas vs constant cost -> deficit early forces wrap to 0 (total surplus, unique 0)
        check(new int[]{5,4,3,2,1}, new int[]{1,1,1,1,1}, 0);
        // Alternating surplus/deficit, balanced totals, start where run begins
        check(new int[]{3,0,3,0}, new int[]{0,3,0,3}, 0);
        // Heavy duplicates with one surplus station -> must start at the surplus
        check(new int[]{1,1,1,1,5}, new int[]{2,2,2,2,1}, 4);
        // Off-by-one deficit: total cost exactly one more than total gas -> impossible
        check(new int[]{1,2,3}, new int[]{2,2,3}, -1);
        // Exact-balance large values, start at the high-gas index
        check(new int[]{10000,0,0}, new int[]{0,5000,5000}, 0);
        // No-solution: single huge deficit station, totals tie elsewhere but gap unbridgeable
        check(new int[]{2,2,2,2}, new int[]{1,1,1,9}, -1);
        // Start forced to last index (only place tank survives the loop)
        check(new int[]{1,1,1,10}, new int[]{2,2,2,3}, 3);
        // Big surplus at index 0 carries the whole loop
        check(new int[]{100,1,1,1}, new int[]{1,1,1,98}, 0);
        // Two stations max-bound both zero -> start 0
        check(new int[]{0,0}, new int[]{0,0}, 0);
        // Totals equal but distributed so middle start required
        check(new int[]{1,5,1,1}, new int[]{2,1,2,3}, 1);

        // ===== Large / performance cases (property-verified) =====
        largeSolvableCase(100000);
        largeImpossibleCase(100000);
        largeRandomCase(50000);
        largeAllMaxGasZeroCost(100000);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // ---- Exact-match check for small deterministic cases ----
    static void check(int[] gas, int[] cost, int expected) {
        total++;
        int[] gasCopy = gas == null ? null : gas.clone();
        int[] costCopy = cost == null ? null : cost.clone();
        try {
            int actual = new Answer().canCompleteCircuit(gas, cost);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m canCompleteCircuit(" + Arrays.toString(gasCopy) + ", " + Arrays.toString(costCopy) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m canCompleteCircuit(" + Arrays.toString(gasCopy) + ", " + Arrays.toString(costCopy) + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m canCompleteCircuit(" + Arrays.toString(gasCopy) + ", " + Arrays.toString(costCopy) + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---- Property check for ambiguous inputs (multiple valid starts) ----
    // Passes if the answer is -1 exactly when no start works, otherwise if the
    // returned start genuinely completes the circuit.
    static void checkProperty(int[] gas, int[] cost) {
        total++;
        int[] gasCopy = gas.clone();
        int[] costCopy = cost.clone();
        try {
            int actual = new Answer().canCompleteCircuit(gas, cost);
            boolean solvable = hasSolution(gas, cost);
            boolean ok;
            if (!solvable) {
                ok = (actual == -1);
            } else {
                ok = (actual != -1) && completesFrom(gas, cost, actual);
            }
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m canCompleteCircuit(" + Arrays.toString(gasCopy) + ", " + Arrays.toString(costCopy) + ") = " + actual + " (valid start)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m canCompleteCircuit(" + Arrays.toString(gasCopy) + ", " + Arrays.toString(costCopy) + ") = " + actual + " (not a valid start)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m canCompleteCircuit(" + Arrays.toString(gasCopy) + ", " + Arrays.toString(costCopy) + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---- Independent O(n) oracle: returns the unique valid start or -1 ----
    // Verifies by directly simulating: a start is valid iff the running tank
    // never drops below zero across the full loop. With the problem's uniqueness
    // guarantee, the greedy answer (if any) is the only valid start.
    static int oracle(int[] gas, int[] cost) {
        int n = gas.length;
        long totalGas = 0, totalCost = 0;
        for (int i = 0; i < n; i++) { totalGas += gas[i]; totalCost += cost[i]; }
        if (totalGas < totalCost) return -1;
        int start = 0;
        long tank = 0;
        for (int i = 0; i < n; i++) {
            tank += gas[i] - cost[i];
            if (tank < 0) { start = i + 1; tank = 0; }
        }
        return start == n ? -1 : start;
    }

    // ---- Property: does starting at `start` complete the full circuit? ----
    static boolean completesFrom(int[] gas, int[] cost, int start) {
        int n = gas.length;
        if (start < 0 || start >= n) return false;
        long tank = 0;
        for (int step = 0; step < n; step++) {
            int i = (start + step) % n;
            tank += gas[i] - cost[i];
            if (tank < 0) return false;
        }
        return true;
    }

    static boolean hasSolution(int[] gas, int[] cost) {
        long totalGas = 0, totalCost = 0;
        for (int i = 0; i < gas.length; i++) { totalGas += gas[i]; totalCost += cost[i]; }
        return totalGas >= totalCost;
    }

    // ---- Performance case runner: validates output by problem property + timing budget ----
    static void perfCase(String label, int[] gas, int[] cost) {
        total++;
        try {
            long t0 = System.nanoTime();
            int actual = new Answer().canCompleteCircuit(gas, cost);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;

            boolean solvable = hasSolution(gas, cost);
            boolean ok;
            String reason;
            if (!solvable) {
                ok = (actual == -1);
                reason = ok ? "correctly returned -1" : ("expected -1 but got " + actual);
            } else if (actual == -1) {
                ok = false;
                reason = "returned -1 but a valid start exists";
            } else {
                ok = completesFrom(gas, cost, actual);
                reason = ok ? ("start=" + actual + " completes the circuit") : ("start=" + actual + " does NOT complete the circuit");
            }
            if (ok && ms > 3000.0) {
                ok = false;
                reason = "exceeded time budget";
            }
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (n=" + gas.length + ", " + String.format(Locale.ROOT, "%.1f", ms) + " ms) " + reason);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " (n=" + gas.length + ", " + String.format(Locale.ROOT, "%.1f", ms) + " ms) " + reason);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " (n=" + gas.length + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Large case engineered to have a valid start (overall surplus guaranteed),
    // verified against the oracle too.
    static void largeSolvableCase(int n) {
        Random rnd = new Random(42);
        int[] gas = new int[n];
        int[] cost = new int[n];
        for (int i = 0; i < n; i++) {
            cost[i] = rnd.nextInt(10001);
            // gas drawn from [cost[i] .. 10000] so every station is non-negative:
            // total gas >= total cost, so a solution is guaranteed to exist.
            gas[i] = cost[i] + (cost[i] >= 10000 ? 0 : rnd.nextInt(10001 - cost[i]));
        }
        perfCase("large random with guaranteed surplus", gas, cost);
        crossCheckOracle("large solvable oracle cross-check", gas, cost);
    }

    // Large case engineered to be impossible (strict overall deficit).
    static void largeImpossibleCase(int n) {
        Random rnd = new Random(42);
        int[] gas = new int[n];
        int[] cost = new int[n];
        for (int i = 0; i < n; i++) {
            gas[i] = rnd.nextInt(5000);       // gas in [0,4999]
            cost[i] = 5000 + rnd.nextInt(5001); // cost in [5000,10000] -> guaranteed deficit
        }
        perfCase("large guaranteed-impossible (overall deficit)", gas, cost);
    }

    // Large fully-random case: may or may not have a solution; verified by property.
    static void largeRandomCase(int n) {
        Random rnd = new Random(42);
        int[] gas = new int[n];
        int[] cost = new int[n];
        for (int i = 0; i < n; i++) {
            gas[i] = rnd.nextInt(10001);
            cost[i] = rnd.nextInt(10001);
        }
        perfCase("large fully-random (property-verified)", gas, cost);
        crossCheckOracle("large random oracle cross-check", gas, cost);
    }

    // Large degenerate case: max gas everywhere, zero cost -> start 0 is the unique answer.
    static void largeAllMaxGasZeroCost(int n) {
        int[] gas = new int[n];
        int[] cost = new int[n];
        Arrays.fill(gas, 10000);
        Arrays.fill(cost, 0);
        perfCase("large all-max-gas zero-cost", gas, cost);
    }

    // Cross-check the implementation's exact return value against the independent oracle.
    // Safe because the problem guarantees a unique valid start, so the oracle's index
    // (when it returns one) is THE answer.
    static void crossCheckOracle(String label, int[] gas, int[] cost) {
        total++;
        try {
            int expected = oracle(gas, cost);
            int actual = new Answer().canCompleteCircuit(gas, cost);
            // Both -1, or both a valid completing start (uniqueness => equal index).
            boolean ok;
            if (expected == -1) {
                ok = (actual == -1);
            } else {
                ok = (actual == expected) && completesFrom(gas, cost, actual);
            }
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (n=" + gas.length + ") oracle=" + expected + " actual=" + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " (n=" + gas.length + ") oracle=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " (n=" + gas.length + ") threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
