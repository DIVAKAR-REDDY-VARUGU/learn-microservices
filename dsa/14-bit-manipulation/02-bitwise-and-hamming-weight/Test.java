import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ---- existing cases (kept) ----
        check(1, 1);
        check(2, 1);
        check(3, 2);
        check(11, 3);            // 1011
        check(128, 1);           // 10000000
        check(2147483645, 30);   // 1111111111111111111111111111101
        check(4, 1);
        check(16, 1);
        check(1024, 1);
        check(1073741824, 1);    // 2^30
        check(Integer.MAX_VALUE, 31); // 2^31 - 1, 31 set bits
        check(5, 2);             // 101
        check(10, 2);            // 1010
        check(0x55555555, 16);   // 0101...0101 -> 16 set bits
        check(0x0F0F0F0F, 16);   // 16 set bits
        check(255, 8);           // 11111111
        check(1023, 10);         // 1111111111
        check(13, 3);            // 1101
        check(7, 3);             // 111

        // ---- new corner cases ----
        check(0x40000000, 1);          // 2^30 single high bit
        check(0x7FFFFFFE, 30);         // MAX_VALUE - 1: bit0 cleared
        check(0x2AAAAAAA, 15);         // alternating, 15 ones
        check(0xAAAAAAAA, 16);         // 1010...10 as int (negative) -> 16 bits over 32
        check(0x80000000, 1);          // sign bit only -> 1 set bit
        check(0xFFFFFFFF, 32);         // all 32 bits set (-1)
        check(96, 2);                  // 1100000
        check(0x10000, 1);             // 2^16
        check(0xFFFF, 16);             // lower 16 bits set
        check(0xFF00FF00, 16);         // 16 set bits, clustered
        check(0x12345678, 13);         // arbitrary mixed value
        check(0x3FFFFFFF, 30);         // 30 consecutive ones
        check(0x55555554, 15);         // alternating with LSB cleared
        check(6, 2);                   // 110
        check(9, 2);                   // 1001
        check(2147483646, 30);         // MAX_VALUE - 1
        check(0x01010101, 4);          // one bit per byte
        check(0xC0000000, 2);          // top two bits set

        // ---- large / performance / scale cases ----
        largeSweep(1, 200000);   // every n in range vs oracle, timed
        randomSweep(200000);     // random values vs oracle, timed
        powersSweep();           // all 31 single-bit powers -> each weight 1
        allOnesSweep();          // (1<<k)-1 has exactly k bits, k=1..31

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // independent oracle: Integer.bitCount (different from the n&(n-1) loop under test)
    static int oracle(int n) { return Integer.bitCount(n); }

    // check every n in [lo, hi] against the oracle in one timed case
    static void largeSweep(int lo, int hi) {
        total++;
        String label = "largeSweep([" + lo + "," + hi + "])";
        try {
            Answer a = new Answer();
            long t0 = System.nanoTime();
            boolean ok = true;
            int badN = -1, badExp = -1, badGot = -1;
            for (int n = lo; n <= hi; n++) {
                int got = a.hammingWeight(n);
                int exp = oracle(n);
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
            int badN = -1, badExp = -1, badGot = -1;
            for (int i = 0; i < count; i++) {
                int n = rnd.nextInt(Integer.MAX_VALUE) + 1; // [1, 2^31 - 1]
                int got = a.hammingWeight(n);
                int exp = oracle(n);
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

    static void powersSweep() {
        total++;
        String label = "powersSweep(2^0..2^30)";
        try {
            Answer a = new Answer();
            boolean ok = true;
            int bad = -1;
            for (int k = 0; k <= 30; k++) {
                int n = 1 << k;
                if (a.hammingWeight(n) != 1) { ok = false; bad = k; break; }
            }
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " each power has exactly 1 set bit");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " failed at k=" + bad);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void allOnesSweep() {
        total++;
        String label = "allOnesSweep((1<<k)-1, k=1..31)";
        try {
            Answer a = new Answer();
            boolean ok = true;
            int bad = -1;
            for (int k = 1; k <= 31; k++) {
                int n = (k == 31) ? Integer.MAX_VALUE : ((1 << k) - 1);
                if (a.hammingWeight(n) != k) { ok = false; bad = k; break; }
            }
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (1<<k)-1 has exactly k set bits");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " failed at k=" + bad);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void check(int n, int expected) {
        total++;
        try {
            int actual = new Answer().hammingWeight(n);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m hammingWeight(" + n + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m hammingWeight(" + n + ") expected " + expected + " but got " + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m hammingWeight(" + n + ") expected " + expected
                    + " but threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
