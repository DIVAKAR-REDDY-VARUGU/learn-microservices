import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static void check(String name, int[] digits, int[] expected) {
        total++;
        try {
            int[] actual = new Answer().plusOne(digits.clone());
            if (actual != null && Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + brief(actual));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | digits=" + brief(digits)
                        + " | expected=" + brief(expected) + " | actual=" + (actual == null ? "null" : brief(actual)));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | digits=" + brief(digits)
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: compare against independent oracle and time it
    static void checkProp(String name, int[] digits, long budgetMs) {
        total++;
        try {
            int[] expected = oracle(digits);
            long t0 = System.nanoTime();
            int[] actual = new Answer().plusOne(digits.clone());
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            boolean ok = actual != null && Arrays.equals(actual, expected);
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | len=" + digits.length + " | mismatch vs oracle");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // independent oracle: add one with carry over the digit array
    static int[] oracle(int[] digits) {
        int[] d = digits.clone();
        int i = d.length - 1;
        while (i >= 0) {
            if (d[i] < 9) { d[i]++; return d; }
            d[i] = 0;
            i--;
        }
        int[] r = new int[d.length + 1];
        r[0] = 1;
        return r;
    }

    static String brief(int[] a) {
        if (a == null) return "null";
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " first=" + a[0] + " last=" + a[a.length - 1] + "]";
    }

    public static void main(String[] args) {
        check("example1", new int[]{1,2,3}, new int[]{1,2,4});
        check("example2", new int[]{4,3,2,1}, new int[]{4,3,2,2});
        check("example3 all nines", new int[]{9,9,9}, new int[]{1,0,0,0});
        check("single zero", new int[]{0}, new int[]{1});
        check("single nine", new int[]{9}, new int[]{1,0});
        check("single non-nine", new int[]{5}, new int[]{6});
        check("carry in middle stops", new int[]{1,9,9}, new int[]{2,0,0});
        check("no carry", new int[]{1,0,0}, new int[]{1,0,1});
        check("trailing nine only", new int[]{2,9}, new int[]{3,0});
        check("leading one grows", new int[]{9,9,9,9}, new int[]{1,0,0,0,0});
        check("middle zeros", new int[]{1,0,9}, new int[]{1,1,0});

        // --- added corner cases ---
        check("single eight", new int[]{8}, new int[]{9});
        check("two digits no carry", new int[]{1,2}, new int[]{1,3});
        check("two nines", new int[]{9,9}, new int[]{1,0,0});
        check("long no carry", new int[]{1,2,3,4,5,6}, new int[]{1,2,3,4,5,7});
        check("carry propagates two steps", new int[]{4,9,9}, new int[]{5,0,0});
        check("only last is nine", new int[]{1,2,9}, new int[]{1,3,0});
        check("leading one preserved", new int[]{1,0,0,0}, new int[]{1,0,0,1});
        check("alternating nines", new int[]{9,0,9}, new int[]{9,1,0});
        check("all nines length 5", new int[]{9,9,9,9,9}, new int[]{1,0,0,0,0,0});
        check("big number plus one", new int[]{8,7,6,5,4,3,2,1}, new int[]{8,7,6,5,4,3,2,2});
        check("zeros after leading", new int[]{5,0,0,0}, new int[]{5,0,0,1});
        check("second to last nine", new int[]{1,9,0}, new int[]{1,9,1});
        check("carry stops at first", new int[]{8,9,9,9}, new int[]{9,0,0,0});
        check("single digit seven", new int[]{7}, new int[]{8});
        check("two digit ends in eight", new int[]{3,8}, new int[]{3,9});
        check("trailing zero increments", new int[]{9,0}, new int[]{9,1});

        // --- large / performance cases (oracle + timing; length up to 100 per constraints) ---
        // Max length 100, all nines -> grows to 101 digits leading 1.
        {
            int n = 100;
            int[] big = new int[n];
            Arrays.fill(big, 9);
            int[] exp = new int[n + 1];
            exp[0] = 1;
            check("max 100 all nines grows", big, exp);
        }
        // Max length 100, leading 1 then zeros -> simple increment of last digit.
        {
            int n = 100;
            int[] big = new int[n];
            big[0] = 1; // rest zeros
            int[] exp = big.clone();
            exp[n - 1] = 1;
            check("max 100 leading one then zeros", big, exp);
        }
        // Max length 100 random valid digits (no leading zero), oracle decides expected; timed.
        {
            Random rnd = new Random(42);
            int n = 100;
            int[] big = new int[n];
            big[0] = 1 + rnd.nextInt(9); // ensure no leading zero
            for (int i = 1; i < n; i++) big[i] = rnd.nextInt(10);
            checkProp("max 100 random digits", big, 3000);
        }
        // Max length 100, first digit 8 then all nines -> carry stops at first, becomes leading 9.
        {
            int n = 100;
            int[] big = new int[n];
            Arrays.fill(big, 9);
            big[0] = 8;
            checkProp("max 100 carry stops at first", big, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
