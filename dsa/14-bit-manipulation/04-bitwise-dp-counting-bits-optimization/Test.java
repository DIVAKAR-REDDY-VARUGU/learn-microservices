import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ---- existing cases (kept) ----
        check(0, new int[]{0});
        check(1, new int[]{0, 1});
        check(2, new int[]{0, 1, 1});
        check(5, new int[]{0, 1, 1, 2, 1, 2});
        check(7, new int[]{0, 1, 1, 2, 1, 2, 2, 3});
        check(8, new int[]{0, 1, 1, 2, 1, 2, 2, 3, 1});
        check(16, reference(16));
        check(31, reference(31));
        check(32, reference(32));
        check(100, reference(100));
        check(255, reference(255));
        check(256, reference(256));
        check(1000, reference(1000));

        // ---- new corner cases ----
        check(3, new int[]{0, 1, 1, 2});
        check(4, new int[]{0, 1, 1, 2, 1});
        check(6, new int[]{0, 1, 1, 2, 1, 2, 2});
        // boundary shapes around powers of two and (2^k - 1)
        check(15, reference(15));       // 2^4 - 1
        check(63, reference(63));       // 2^6 - 1
        check(64, reference(64));       // 2^6
        check(127, reference(127));     // 2^7 - 1
        check(128, reference(128));     // 2^7
        check(511, reference(511));     // 2^9 - 1
        check(512, reference(512));     // 2^9
        check(1023, reference(1023));   // 2^10 - 1
        check(1024, reference(1024));   // 2^10
        check(1025, reference(1025));   // 2^10 + 1
        // arbitrary mid values
        check(50, reference(50));
        check(99, reference(99));
        check(333, reference(333));
        check(999, reference(999));
        check(9999, reference(9999));

        // ---- large / performance / scale cases ----
        largeAgainstOracle(10000);      // verify whole array vs oracle, timed
        largeAgainstOracle(65536);      // 2^16 boundary, big
        largeAgainstOracle(99999);      // just under 10^5
        largeAgainstOracle(100000);     // constraint upper bound n = 10^5
        recurrenceProperty(100000);     // verify length n+1 and bit recurrence

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent reference using Integer.bitCount (different algorithm than the DP under test).
    static int[] reference(int n) {
        int[] r = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            r[i] = Integer.bitCount(i);
        }
        return r;
    }

    // Build expected via oracle, compare exactly, time the call.
    static void largeAgainstOracle(int n) {
        total++;
        String label = "largeAgainstOracle(n=" + n + ")";
        try {
            int[] expected = reference(n);
            long t0 = System.nanoTime();
            int[] actual = new Answer().countBits(n);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = actual != null && actual.length == expected.length && Arrays.equals(actual, expected);
            if (ok && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " length=" + actual.length + " matches oracle (" + ms + " ms)");
            } else if (!ok) {
                if (actual == null) {
                    System.out.println("\033[31m[FAIL]\033[0m " + label + " returned null (" + ms + " ms)");
                } else if (actual.length != expected.length) {
                    System.out.println("\033[31m[FAIL]\033[0m " + label + " wrong length: expected " + expected.length + " got " + actual.length + " (" + ms + " ms)");
                } else {
                    int firstDiff = -1;
                    for (int i = 0; i < expected.length; i++) if (actual[i] != expected[i]) { firstDiff = i; break; }
                    System.out.println("\033[31m[FAIL]\033[0m " + label + " mismatch at i=" + firstDiff
                            + " expected " + expected[firstDiff] + " got " + actual[firstDiff] + " (" + ms + " ms)");
                }
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + ms + " ms (>3000)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Property check: output length n+1 and each entry obeys the bit recurrence (self-consistency).
    static void recurrenceProperty(int n) {
        total++;
        String label = "recurrenceProperty(n=" + n + ")";
        try {
            long t0 = System.nanoTime();
            int[] a = new Answer().countBits(n);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = a != null && a.length == n + 1;
            int badI = -1;
            if (ok) {
                if (a[0] != 0) { ok = false; badI = 0; }
                for (int i = 1; ok && i <= n; i++) {
                    if (a[i] != a[i >> 1] + (i & 1)) { ok = false; badI = i; }
                }
            }
            if (ok && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " length n+1 and recurrence holds (" + ms + " ms)");
            } else if (!ok) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " violated at i=" + badI
                        + (a == null ? " (null)" : " len=" + a.length) + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " too slow: " + ms + " ms (>3000)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void check(int n, int[] expected) {
        total++;
        try {
            int[] actual = new Answer().countBits(n);
            if (actual != null && actual.length == expected.length && Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m countBits(" + n + ") = " + Arrays.toString(actual));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m countBits(" + n + ") expected " + Arrays.toString(expected)
                        + " but got " + Arrays.toString(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m countBits(" + n + ") expected " + Arrays.toString(expected)
                    + " but threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
