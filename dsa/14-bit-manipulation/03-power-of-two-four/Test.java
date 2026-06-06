import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ---- existing cases (kept) ----
        check(1, true);          // 2^0
        check(16, true);         // 2^4
        check(3, false);
        check(0, false);
        check(-1, false);
        check(-16, false);
        check(-2, false);
        check(Integer.MIN_VALUE, false); // -2^31
        check(2, true);
        check(4, true);
        check(8, true);
        check(1024, true);
        check(1073741824, true); // 2^30
        check(Integer.MAX_VALUE, false); // 2^31 - 1
        check(5, false);
        check(6, false);
        check(15, false);
        check(17, false);
        check(1023, false);
        check(1025, false);
        check(6, false);
        check(12, false);

        // ---- new corner cases ----
        // remaining powers of two not already covered
        check(32, true);
        check(64, true);
        check(128, true);
        check(256, true);
        check(512, true);
        check(2048, true);
        check(65536, true);          // 2^16
        check(536870912, true);      // 2^29
        check(1 << 30, true);        // 2^30 via shift
        // negative magnitudes are NOT powers of two
        check(-1024, false);
        check(-1073741824, false);   // -(2^30)
        // values with exactly two set bits -> false
        check(48, false);            // 110000
        check(0x30000000, false);    // two high bits set
        // power of two plus/minus one
        check(2047, false);          // 2^11 - 1
        check(33554431, false);      // 2^25 - 1 (all ones)
        check(33554433, false);      // 2^25 + 1
        // small non-powers
        check(7, false);
        check(9, false);
        check(10, false);

        // ---- large / performance / scale cases ----
        allPowersTrue();             // all 31 powers 2^0..2^30 must be true
        nonPowerSweep(2, 200000);    // dense sweep vs oracle, timed
        randomSweep(200000);         // random ints (incl negatives/zero) vs oracle, timed

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // independent oracle: a positive int is a power of two iff it has exactly one set bit.
    static boolean oracle(int n) {
        if (n <= 0) return false;
        return Integer.bitCount(n) == 1;
    }

    static void allPowersTrue() {
        total++;
        String label = "allPowersTrue(2^0..2^30)";
        try {
            Answer a = new Answer();
            boolean ok = true;
            int bad = -1;
            for (int k = 0; k <= 30; k++) {
                if (!a.isPowerOfTwo(1 << k)) { ok = false; bad = k; break; }
            }
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " every power returns true");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " failed at k=" + bad);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // check a dense range against the oracle in one timed case
    static void nonPowerSweep(int lo, int hi) {
        total++;
        String label = "nonPowerSweep([" + lo + "," + hi + "])";
        try {
            Answer a = new Answer();
            long t0 = System.nanoTime();
            boolean ok = true;
            int badN = -1; boolean badExp = false, badGot = false;
            for (int n = lo; n <= hi; n++) {
                boolean got = a.isPowerOfTwo(n);
                boolean exp = oracle(n);
                if (got != exp) { ok = false; badN = n; badExp = exp; badGot = got; break; }
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            if (ok && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " all match oracle (" + ms + " ms)");
            } else if (!ok) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " mismatch at n=" + badN + " expected " + badExp + " got " + badGot + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + ms + " ms (>3000)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void randomSweep(int count) {
        total++;
        String label = "randomSweep(count=" + count + ")";
        try {
            Random rnd = new Random(42);
            Answer a = new Answer();
            long t0 = System.nanoTime();
            boolean ok = true;
            int badN = -1; boolean badExp = false, badGot = false;
            for (int i = 0; i < count; i++) {
                int n = rnd.nextInt(); // full int range incl negatives and zero
                boolean got = a.isPowerOfTwo(n);
                boolean exp = oracle(n);
                if (got != exp) { ok = false; badN = n; badExp = exp; badGot = got; break; }
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            if (ok && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " all match oracle (" + ms + " ms)");
            } else if (!ok) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " mismatch at n=" + badN + " expected " + badExp + " got " + badGot + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + ms + " ms (>3000)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void check(int n, boolean expected) {
        total++;
        try {
            boolean actual = new Answer().isPowerOfTwo(n);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m isPowerOfTwo(" + n + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m isPowerOfTwo(" + n + ") expected " + expected + " but got " + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m isPowerOfTwo(" + n + ") expected " + expected
                    + " but threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
