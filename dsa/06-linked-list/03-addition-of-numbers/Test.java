import java.util.*;

// Top-level ListNode so that `javac Answer.java Test.java` compiles
// (Answer.java references ListNode but does not define it).
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

public class Test {
    static int pass = 0;
    static int total = 0;

    static ListNode build(int[] vals) {
        ListNode dummy = new ListNode();
        ListNode cur = dummy;
        for (int v : vals) {
            cur.next = new ListNode(v);
            cur = cur.next;
        }
        return dummy.next;
    }

    static int[] toArray(ListNode head) {
        ArrayList<Integer> out = new ArrayList<>();
        ListNode cur = head;
        long guard = 0;
        while (cur != null && guard < 2000000) {
            out.add(cur.val);
            cur = cur.next;
            guard++;
        }
        int[] arr = new int[out.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = out.get(i);
        return arr;
    }

    static void check(String name, int[] a, int[] b, int[] expected) {
        total++;
        try {
            ListNode l1 = build(a);
            ListNode l2 = build(b);
            ListNode result = new Answer().addTwoNumbers(l1, l2);
            int[] actual = toArray(result);
            if (Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + " | l1=" + brief(a)
                        + " l2=" + brief(b)
                        + " expected=" + brief(expected)
                        + " actual=" + brief(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | l1=" + brief(a)
                    + " l2=" + brief(b)
                    + " expected=" + brief(expected)
                    + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    // Independent oracle: add two reverse-order digit lists using BigInteger,
    // then emit the sum back as a reverse-order digit array (no leading zeros,
    // 0 itself -> [0]).
    static int[] addOracle(int[] a, int[] b) {
        java.math.BigInteger x = fromReversedDigits(a);
        java.math.BigInteger y = fromReversedDigits(b);
        return toReversedDigits(x.add(y));
    }

    static java.math.BigInteger fromReversedDigits(int[] d) {
        StringBuilder sb = new StringBuilder();
        for (int i = d.length - 1; i >= 0; i--) sb.append(d[i]);
        return new java.math.BigInteger(sb.toString());
    }

    static int[] toReversedDigits(java.math.BigInteger v) {
        String s = v.toString(); // never negative here, no sign
        int[] out = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            out[i] = s.charAt(s.length() - 1 - i) - '0';
        }
        return out;
    }

    // Property check for large inputs: compare against BigInteger oracle, report timing.
    static void checkAddProperty(String name, int[] a, int[] b) {
        total++;
        try {
            ListNode l1 = build(a);
            ListNode l2 = build(b);
            long t0 = System.nanoTime();
            ListNode result = new Answer().addTwoNumbers(l1, l2);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            int[] actual = toArray(result);
            int[] expected = addOracle(a, b);
            String label = name + " (len1=" + a.length + ", len2=" + b.length + ", " + elapsedMs + " ms)";
            if (!Arrays.equals(actual, expected)) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | sum does not match BigInteger oracle");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> 3000 ms)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | len1=" + a.length + ", len2=" + b.length
                    + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    static String brief(int[] a) {
        if (a.length <= 20) return Arrays.toString(a);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) sb.append(a[i]).append(", ");
        sb.append("... (").append(a.length).append(" digits) ...");
        for (int i = a.length - 5; i < a.length; i++) sb.append(", ").append(a[i]);
        return sb.append("]").toString();
    }

    // Build the reverse-order digit list of n nines: [9,9,...,9].
    static int[] nines(int count) {
        int[] a = new int[count];
        Arrays.fill(a, 9);
        return a;
    }

    // Random valid number of `len` digits (no leading zero in most-significant pos,
    // i.e. last array element != 0 when len > 1). Digits stored reversed.
    static int[] randomNumber(Random rnd, int len) {
        int[] a = new int[len];
        for (int i = 0; i < len; i++) a[i] = rnd.nextInt(10);
        if (len > 1 && a[len - 1] == 0) a[len - 1] = 1 + rnd.nextInt(9);
        return a;
    }

    public static void main(String[] args) {
        // Examples from QUESTION.md (digits stored in reverse order)
        check("example 1: 342+465=807", new int[]{2, 4, 3}, new int[]{5, 6, 4}, new int[]{7, 0, 8});
        check("example 2: 0+0=0", new int[]{0}, new int[]{0}, new int[]{0});
        check("example 3: 9999999+9999=10009998",
                new int[]{9, 9, 9, 9, 9, 9, 9}, new int[]{9, 9, 9, 9},
                new int[]{8, 9, 9, 9, 0, 0, 0, 1});

        // Corner cases (original)
        check("single digit, no carry: 5+3=8", new int[]{5}, new int[]{3}, new int[]{8});
        check("final carry: 5+5=10", new int[]{5}, new int[]{5}, new int[]{0, 1});
        check("carry propagation: 1+99=100",
                new int[]{1}, new int[]{9, 9}, new int[]{0, 0, 1});
        check("all nines short: 9+1=10", new int[]{9}, new int[]{1}, new int[]{0, 1});
        check("chained carries: 99+99=198",
                new int[]{9, 9}, new int[]{9, 9}, new int[]{8, 9, 1});
        check("different lengths: 1+999=1000",
                new int[]{1}, new int[]{9, 9, 9}, new int[]{0, 0, 0, 1});
        check("one operand zero: 0+123=123",
                new int[]{0}, new int[]{3, 2, 1}, new int[]{3, 2, 1});
        check("no carry multi-digit: 123+456=579",
                new int[]{3, 2, 1}, new int[]{6, 5, 4}, new int[]{9, 7, 5});
        check("longer + shorter with carry: 999+1=1000",
                new int[]{9, 9, 9}, new int[]{1}, new int[]{0, 0, 0, 1});
        check("both long all nines: 99999+99999=199998",
                new int[]{9, 9, 9, 9, 9}, new int[]{9, 9, 9, 9, 9},
                new int[]{8, 9, 9, 9, 9, 1});
        check("asymmetric no overflow: 5000+55=5055",
                new int[]{0, 0, 0, 5}, new int[]{5, 5}, new int[]{5, 5, 0, 5});

        // --- NEW corner cases ---
        check("0+1=1 (min lengths)", new int[]{0}, new int[]{1}, new int[]{1});
        check("1+0=1 (zero second)", new int[]{1}, new int[]{0}, new int[]{1});
        check("max single digit no carry: 4+5=9", new int[]{4}, new int[]{5}, new int[]{9});
        check("max single digit carry: 9+9=18", new int[]{9}, new int[]{9}, new int[]{8, 1});
        check("carry ripples whole number: 1+999999=1000000",
                new int[]{1}, new int[]{9, 9, 9, 9, 9, 9}, new int[]{0, 0, 0, 0, 0, 0, 1});
        check("no carry but unequal length: 21+345=366",
                new int[]{1, 2}, new int[]{5, 4, 3}, new int[]{6, 6, 3});
        check("internal carries not final: 555+555=1110",
                new int[]{5, 5, 5}, new int[]{5, 5, 5}, new int[]{0, 1, 1, 1});
        check("add to zero keeps length: 0+9=9", new int[]{0}, new int[]{9}, new int[]{9});
        check("sparse digits with one carry: 100+900=1000",
                new int[]{0, 0, 1}, new int[]{0, 0, 9}, new int[]{0, 0, 0, 1});
        check("alternating digits: 1010+0101=1111",
                new int[]{0, 1, 0, 1}, new int[]{1, 0, 1, 0}, new int[]{1, 1, 1, 1});
        check("mostly zeros plus one: 10000+1=10001",
                new int[]{0, 0, 0, 0, 1}, new int[]{1}, new int[]{1, 0, 0, 0, 1});
        check("longer all nines + 1 grows length",
                nines(10), new int[]{1}, oneFollowedByZeros(10));
        check("two equal large all-nines via oracle",
                nines(20), nines(20), addOracle(nines(20), nines(20)));
        check("single max digit chain to long: 9+9999999999",
                new int[]{9}, nines(10), addOracle(new int[]{9}, nines(10)));
        check("unequal lengths big diff: 7 + 50-digit nines",
                new int[]{7}, nines(50), addOracle(new int[]{7}, nines(50)));
        check("max length 100 nines + 100 nines",
                nines(100), nines(100), addOracle(nines(100), nines(100)));

        // --- LARGE / PERFORMANCE cases ---
        // Problem caps each list at 100 nodes. A correct O(n+m) digit-add must
        // scale linearly; we push far past the bound (BigInteger oracle) to flag
        // any accidental super-linear behaviour. Verified by property, timed.
        Random rnd = new Random(42);
        int big = 100_000;

        int[] a1 = randomNumber(rnd, big);
        int[] b1 = randomNumber(rnd, big);
        checkAddProperty("large random equal-length (seed=42)", a1, b1);

        // Worst-case carry chain: all nines + a single 1 forces carry through every digit.
        checkAddProperty("large all-nines + 1 (full carry ripple)", nines(big), new int[]{1});

        // Two all-nines lists of max length: every column overflows.
        checkAddProperty("large all-nines + all-nines", nines(big), nines(big));

        // Highly skewed lengths.
        int[] a4 = randomNumber(rnd, big);
        int[] b4 = randomNumber(rnd, 5);
        checkAddProperty("large + tiny skewed lengths", a4, b4);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Reverse-order digits of 10^count, i.e. [0,0,...,0,1] of length count+1.
    static int[] oneFollowedByZeros(int count) {
        int[] a = new int[count + 1];
        a[count] = 1;
        return a;
    }
}
