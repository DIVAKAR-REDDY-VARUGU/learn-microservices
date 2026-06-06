import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // NumArray is a non-static inner class of Answer -> build via answer.new NumArray(...)
        Answer answer = new Answer();

        // ===== Existing cases (preserved) =====
        // Example 1 from QUESTION.md
        {
            int[] nums = {1, 3, 5};
            Object na = newNumArray(answer, nums);
            checkSum("ex1 sumRange(0,2)=9", na, 0, 2, 9);
            doUpdate("ex1 update(1,2)", na, 1, 2);
            checkSum("ex1 sumRange(0,2)=8", na, 0, 2, 8);
        }

        // Example 2 from QUESTION.md
        {
            int[] nums = {7, 2, 7, 2, 0};
            Object na = newNumArray(answer, nums);
            checkSum("ex2 sumRange(1,3)=11", na, 1, 3, 11);
            doUpdate("ex2 update(0,6)", na, 0, 6);
            doUpdate("ex2 update(3,4)", na, 3, 4);
            checkSum("ex2 sumRange(2,4)=13", na, 2, 4, 13);
        }

        // Single element array
        {
            int[] nums = {42};
            Object na = newNumArray(answer, nums);
            checkSum("single sumRange(0,0)=42", na, 0, 0, 42);
            doUpdate("single update(0,-7)", na, 0, -7);
            checkSum("single sumRange(0,0)=-7", na, 0, 0, -7);
        }

        // Single-index ranges (left == right) across an array
        {
            int[] nums = {-1, 0, 5, -3, 2};
            Object na = newNumArray(answer, nums);
            checkSum("point range [0,0]", na, 0, 0, -1);
            checkSum("point range [2,2]", na, 2, 2, 5);
            checkSum("point range [3,3]", na, 3, 3, -3);
            checkSum("point range [4,4]", na, 4, 4, 2);
            checkSum("full range [0,4]", na, 0, 4, 3);
        }

        // Negatives only + full range + update to negative
        {
            int[] nums = {-5, -10, -3, -100};
            Object na = newNumArray(answer, nums);
            checkSum("neg full [0,3]", na, 0, 3, -118);
            doUpdate("neg update(1,-1)", na, 1, -1);
            checkSum("neg after update [0,3]", na, 0, 3, -109);
            checkSum("neg sub [1,2]", na, 1, 2, -4);
        }

        // All-equal array
        {
            int[] nums = {4, 4, 4, 4, 4};
            Object na = newNumArray(answer, nums);
            checkSum("equal full [0,4]", na, 0, 4, 20);
            checkSum("equal sub [1,3]", na, 1, 3, 12);
            doUpdate("equal update(2,0)", na, 2, 0);
            checkSum("equal after update [0,4]", na, 0, 4, 16);
        }

        // Update to the SAME value (no-op semantics)
        {
            int[] nums = {1, 2, 3};
            Object na = newNumArray(answer, nums);
            doUpdate("noop update(1,2)", na, 1, 2);
            checkSum("noop sumRange(0,2)=6", na, 0, 2, 6);
        }

        // Repeated updates on the same index
        {
            int[] nums = {10, 20, 30};
            Object na = newNumArray(answer, nums);
            doUpdate("rep update(0,1)", na, 0, 1);
            doUpdate("rep update(0,5)", na, 0, 5);
            doUpdate("rep update(0,-5)", na, 0, -5);
            checkSum("rep sumRange(0,2)=45", na, 0, 2, 45);
            checkSum("rep sumRange(0,0)=-5", na, 0, 0, -5);
        }

        // Min/max value bounds (-100 .. 100 per constraints)
        {
            int[] nums = {100, -100, 100, -100};
            Object na = newNumArray(answer, nums);
            checkSum("bounds full [0,3]", na, 0, 3, 0);
            doUpdate("bounds update(0,100)", na, 0, 100);
            doUpdate("bounds update(1,100)", na, 1, 100);
            checkSum("bounds after [0,1]", na, 0, 1, 200);
            checkSum("bounds after [0,3]", na, 0, 3, 200);
        }

        // Larger deterministic mirror-verified sequence of operations
        {
            int n = 50;
            int[] nums = new int[n];
            int seed = 98765;
            for (int i = 0; i < n; i++) {
                seed = (seed * 1103515245 + 12345) & 0x7fffffff;
                nums[i] = (seed % 201) - 100; // -100..100
            }
            int[] mirror = nums.clone();
            Object na = newNumArray(answer, nums);
            // interleave updates and range queries, comparing to brute mirror
            int ok = 0, tries = 0;
            for (int step = 0; step < 40; step++) {
                seed = (seed * 1103515245 + 12345) & 0x7fffffff;
                if (step % 3 == 0) {
                    int idx = seed % n;
                    int val = (seed % 201) - 100;
                    mirror[idx] = val;
                    if (!tryUpdate(na, idx, val)) { tries++; continue; }
                } else {
                    int a = seed % n;
                    seed = (seed * 1103515245 + 12345) & 0x7fffffff;
                    int b = seed % n;
                    int l = Math.min(a, b), r = Math.max(a, b);
                    int exp = 0;
                    for (int k = l; k <= r; k++) exp += mirror[k];
                    tries++;
                    Integer act = trySum(na, l, r);
                    if (act != null && act == exp) ok++;
                    else {
                        System.out.println("\033[31m[FAIL]\033[0m large step " + step + " sumRange(" + l + "," + r
                                + ") expected=" + exp + " actual=" + act);
                    }
                }
            }
            total++;
            if (ok == tries && tries > 0) {
                System.out.println("\033[32m[PASS]\033[0m large interleaved (" + ok + "/" + tries + " range queries matched)");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m large interleaved (" + ok + "/" + tries + " range queries matched)");
            }
        }

        // ===== NEW corner cases =====
        // Two-element array: every range shape
        {
            int[] nums = {3, 7};
            Object na = newNumArray(answer, nums);
            checkSum("two [0,0]=3", na, 0, 0, 3);
            checkSum("two [1,1]=7", na, 1, 1, 7);
            checkSum("two [0,1]=10", na, 0, 1, 10);
            doUpdate("two update(1,-7)", na, 1, -7);
            checkSum("two after [0,1]=-4", na, 0, 1, -4);
        }

        // All zeros, update one then back to zero
        {
            int[] nums = {0, 0, 0, 0};
            Object na = newNumArray(answer, nums);
            checkSum("zeros full=0", na, 0, 3, 0);
            doUpdate("zeros update(2,100)", na, 2, 100);
            checkSum("zeros after [0,3]=100", na, 0, 3, 100);
            doUpdate("zeros revert(2,0)", na, 2, 0);
            checkSum("zeros reverted [0,3]=0", na, 0, 3, 0);
        }

        // Boundary ranges: leftmost, rightmost element on a longer array
        {
            int[] nums = {5, -4, 3, -2, 1, 0, 9};
            Object na = newNumArray(answer, nums);
            checkSum("edge left [0,0]=5", na, 0, 0, 5);
            checkSum("edge right [6,6]=9", na, 6, 6, 9);
            checkSum("edge near-left [0,1]=1", na, 0, 1, 1);
            checkSum("edge near-right [5,6]=9", na, 5, 6, 9);
            checkSum("edge full [0,6]=12", na, 0, 6, 12);
        }

        // All max then all min via updates, exercise full-range sums
        {
            int[] nums = {1, 1, 1, 1, 1, 1};
            Object na = newNumArray(answer, nums);
            for (int i = 0; i < 6; i++) doUpdate("max update(" + i + ",100)", na, i, 100);
            checkSum("all max [0,5]=600", na, 0, 5, 600);
            for (int i = 0; i < 6; i++) doUpdate("min update(" + i + ",-100)", na, i, -100);
            checkSum("all min [0,5]=-600", na, 0, 5, -600);
            checkSum("all min sub [2,4]=-300", na, 2, 4, -300);
        }

        // Update only affects its own index (locality) -> prefix before index unchanged
        {
            int[] nums = {10, 20, 30, 40, 50};
            Object na = newNumArray(answer, nums);
            checkSum("loc prefix [0,2]=60", na, 0, 2, 60);
            doUpdate("loc update(4,-50)", na, 4, -50);
            checkSum("loc prefix unchanged [0,2]=60", na, 0, 2, 60);
            checkSum("loc full [0,4]=50", na, 0, 4, 50);
            checkSum("loc suffix [3,4]=-10", na, 3, 4, -10);
        }

        // Odd-length size that is not a power of two (segment-tree padding stress)
        {
            int[] nums = {2, 4, 6, 8, 10, 12, 14}; // length 7
            Object na = newNumArray(answer, nums);
            checkSum("len7 full [0,6]=56", na, 0, 6, 56);
            checkSum("len7 mid [2,4]=24", na, 2, 4, 24);
            doUpdate("len7 update(6,-14)", na, 6, -14);
            checkSum("len7 after [0,6]=28", na, 0, 6, 28);
            doUpdate("len7 update(0,-2)", na, 0, -2);
            checkSum("len7 after2 [0,6]=24", na, 0, 6, 24);
        }

        // Power-of-two length size 8
        {
            int[] nums = {1, -1, 2, -2, 3, -3, 4, -4}; // sum 0
            Object na = newNumArray(answer, nums);
            checkSum("pow2 full [0,7]=0", na, 0, 7, 0);
            checkSum("pow2 left half [0,3]=0", na, 0, 3, 0);
            checkSum("pow2 right half [4,7]=0", na, 4, 7, 0);
            doUpdate("pow2 update(7,100)", na, 7, 100);
            checkSum("pow2 after [0,7]=104", na, 0, 7, 104);
        }

        // Alternating +100/-100 long, sub-range parity
        {
            int n = 11;
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) nums[i] = (i % 2 == 0) ? 100 : -100;
            Object na = newNumArray(answer, nums);
            checkSum("alt full [0,10]=100", na, 0, 10, 100); // 6*100 - 5*100
            checkSum("alt even pair [0,1]=0", na, 0, 1, 0);
            checkSum("alt single [4,4]=100", na, 4, 4, 100);
        }

        // Many independent NumArray instances do not interfere (fresh answer reuse)
        {
            Object a1 = newNumArray(answer, new int[]{1, 1, 1});
            Object a2 = newNumArray(answer, new int[]{9, 9, 9});
            doUpdate("iso a1 update(0,100)", a1, 0, 100);
            checkSum("iso a1 [0,2]=102", a1, 0, 2, 102);
            checkSum("iso a2 untouched [0,2]=27", a2, 0, 2, 27);
        }

        // ===== LARGE / PERFORMANCE / SCALE cases =====
        // n = 30000 (upper bound) with ~30000 interleaved ops, mirror-verified + timed.
        largeInterleaved("large n=30000 ~30000 ops (mirror-verified)", answer, 30000, 30000, 42L);
        // Worst-case shape: all-point updates then all full-range queries, timed.
        largeAllRange("large n=30000 full-range stress (oracle-verified)", answer, 30000, 42L);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Build a big array, run a fixed-seed interleaved mix of update/sumRange against a brute mirror.
    // Times the whole op stream; fails on any wrong answer or a catastrophic (>3000 ms) budget overrun.
    static void largeInterleaved(String name, Answer answer, int n, int ops, long seedVal) {
        total++;
        try {
            java.util.Random rnd = new java.util.Random(seedVal);
            long[] mirror = new long[n];
            int[] init = new int[n];
            for (int i = 0; i < n; i++) {
                init[i] = rnd.nextInt(201) - 100; // -100..100
                mirror[i] = init[i];
            }
            // Fenwick mirror for O(log n) brute range sums (independent oracle, not the SUT).
            long[] bit = new long[n + 1];
            for (int i = 0; i < n; i++) bitAdd(bit, i, init[i]);

            Object na = newNumArray(answer, init);
            long t0 = System.nanoTime();
            boolean okAll = true;
            int checked = 0;
            String fail = null;
            for (int op = 0; op < ops && okAll; op++) {
                if (rnd.nextInt(2) == 0) {
                    int idx = rnd.nextInt(n);
                    int val = rnd.nextInt(201) - 100;
                    long delta = val - mirror[idx];
                    mirror[idx] = val;
                    bitAdd(bit, idx, delta);
                    ((Answer.NumArray) na).update(idx, val);
                } else {
                    int a = rnd.nextInt(n), b = rnd.nextInt(n);
                    int l = Math.min(a, b), r = Math.max(a, b);
                    long exp = bitRange(bit, l, r);
                    int act = ((Answer.NumArray) na).sumRange(l, r);
                    checked++;
                    if (act != exp) {
                        okAll = false;
                        fail = "sumRange(" + l + "," + r + ") expected=" + exp + " actual=" + act;
                    }
                }
            }
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            String label = name + " (n=" + n + ", ops=" + ops + ", checked=" + checked
                    + ", " + String.format(Locale.US, "%.1f", ms) + " ms)";
            if (!okAll) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + "  " + fail);
            } else if (ms > 3000.0) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " exceeded 3000 ms budget (likely O(n) per op)");
            } else {
                System.out.println("\033[32m[PASS]\033[0m " + label);
                pass++;
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Point-update every index, then issue many random ranges; full range must equal the known total.
    static void largeAllRange(String name, Answer answer, int n, long seedVal) {
        total++;
        try {
            java.util.Random rnd = new java.util.Random(seedVal);
            int[] init = new int[n]; // start all zero
            Object na = newNumArray(answer, init);
            long[] bit = new long[n + 1];
            long t0 = System.nanoTime();
            // set each index to a value in [-100,100]
            long totalSum = 0;
            for (int i = 0; i < n; i++) {
                int val = rnd.nextInt(201) - 100;
                totalSum += val;
                bitAdd(bit, i, val);
                ((Answer.NumArray) na).update(i, val);
            }
            boolean okAll = true;
            String fail = null;
            // full range
            int full = ((Answer.NumArray) na).sumRange(0, n - 1);
            if (full != totalSum) { okAll = false; fail = "sumRange(0," + (n - 1) + ") expected=" + totalSum + " actual=" + full; }
            // random sub-ranges verified against the Fenwick oracle
            int checked = 0;
            for (int q = 0; q < 5000 && okAll; q++) {
                int a = rnd.nextInt(n), b = rnd.nextInt(n);
                int l = Math.min(a, b), r = Math.max(a, b);
                long exp = bitRange(bit, l, r);
                int act = ((Answer.NumArray) na).sumRange(l, r);
                checked++;
                if (act != exp) { okAll = false; fail = "sumRange(" + l + "," + r + ") expected=" + exp + " actual=" + act; }
            }
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            String label = name + " (n=" + n + ", checked=" + checked
                    + ", " + String.format(Locale.US, "%.1f", ms) + " ms)";
            if (!okAll) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + "  " + fail);
            } else if (ms > 3000.0) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " exceeded 3000 ms budget (likely O(n) per op)");
            } else {
                System.out.println("\033[32m[PASS]\033[0m " + label);
                pass++;
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // --- Independent Fenwick (BIT) oracle helpers (point-set via delta, prefix/range sum) ---
    static void bitAdd(long[] bit, int i, long delta) {
        for (int x = i + 1; x < bit.length; x += x & (-x)) bit[x] += delta;
    }
    static long bitPrefix(long[] bit, int i) { // sum of [0..i]
        long s = 0;
        for (int x = i + 1; x > 0; x -= x & (-x)) s += bit[x];
        return s;
    }
    static long bitRange(long[] bit, int l, int r) {
        return bitPrefix(bit, r) - (l == 0 ? 0 : bitPrefix(bit, l - 1));
    }

    // --- Construction / call helpers (NumArray is a non-static inner class of Answer) ---
    static Object newNumArray(Answer answer, int[] nums) {
        return answer.new NumArray(nums.clone());
    }

    static void checkSum(String name, Object na, int left, int right, int expected) {
        total++;
        try {
            int actual = ((Answer.NumArray) na).sumRange(left, right);
            if (actual == expected) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + "  sumRange(" + left + "," + right
                        + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + "  sumRange(" + left + "," + right
                    + ") expected=" + expected + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    static void doUpdate(String name, Object na, int index, int val) {
        total++;
        try {
            ((Answer.NumArray) na).update(index, val);
            System.out.println("\033[32m[PASS]\033[0m " + name);
            pass++;
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + "  update(" + index + "," + val + ") threw "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static boolean tryUpdate(Object na, int index, int val) {
        try {
            ((Answer.NumArray) na).update(index, val);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    static Integer trySum(Object na, int left, int right) {
        try {
            return ((Answer.NumArray) na).sumRange(left, right);
        } catch (Throwable t) {
            return null;
        }
    }
}
