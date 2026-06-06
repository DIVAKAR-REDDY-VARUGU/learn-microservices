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

    // reorderList is void + in-place: build, call, then assert on the mutated list.
    static void check(String name, int[] input, int[] expected) {
        total++;
        try {
            ListNode head = build(input);
            new Answer().reorderList(head);
            // After reorder, the original head node stays the head (L0 first).
            int[] actual = toArray(head);
            if (Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + " | input=" + brief(input)
                        + " expected=" + brief(expected)
                        + " actual=" + brief(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | input=" + brief(input)
                    + " expected=" + brief(expected)
                    + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    // Compute the expected reordering L0 -> Ln -> L1 -> L(n-1) -> ...
    static int[] expectedReorder(int[] in) {
        int n = in.length;
        int[] out = new int[n];
        int lo = 0, hi = n - 1, idx = 0;
        boolean takeFront = true;
        while (lo <= hi) {
            if (takeFront) {
                out[idx++] = in[lo++];
            } else {
                out[idx++] = in[hi--];
            }
            takeFront = !takeFront;
        }
        return out;
    }

    // Property check for large inputs: mutated list must match expectedReorder,
    // i.e. same length AND exact interleave order. Reports timing.
    static void checkReorderProperty(String name, int[] input) {
        total++;
        try {
            ListNode head = build(input);
            long t0 = System.nanoTime();
            new Answer().reorderList(head);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            int[] actual = toArray(head);
            int[] expected = expectedReorder(input);
            String label = name + " (n=" + input.length + ", " + elapsedMs + " ms)";
            if (!Arrays.equals(actual, expected)) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | reordering does not match independent reference");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> 3000 ms) -- likely O(n^2)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | n=" + input.length
                    + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    static String brief(int[] a) {
        if (a.length <= 20) return Arrays.toString(a);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) sb.append(a[i]).append(", ");
        sb.append("... (").append(a.length).append(" elems) ...");
        for (int i = a.length - 5; i < a.length; i++) sb.append(", ").append(a[i]);
        return sb.append("]").toString();
    }

    static int[] seq(int from, int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = from + i;
        return a;
    }

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("example 1: [1,2,3,4]", new int[]{1, 2, 3, 4}, new int[]{1, 4, 2, 3});
        check("example 2: [1,2,3,4,5]", new int[]{1, 2, 3, 4, 5}, new int[]{1, 5, 2, 4, 3});
        check("example 3: single", new int[]{1}, new int[]{1});

        // Corner cases (original)
        check("two elements", new int[]{1, 2}, new int[]{1, 2});
        check("three elements", new int[]{1, 2, 3}, new int[]{1, 3, 2});
        check("all equal", new int[]{7, 7, 7, 7}, new int[]{7, 7, 7, 7});
        check("duplicates", new int[]{1, 2, 2, 1}, new int[]{1, 1, 2, 2});
        check("six elements (even)", new int[]{1, 2, 3, 4, 5, 6}, new int[]{1, 6, 2, 5, 3, 4});
        check("seven elements (odd)", new int[]{1, 2, 3, 4, 5, 6, 7}, new int[]{1, 7, 2, 6, 3, 5, 4});
        check("max value bound 1000", new int[]{1000, 1, 1000, 1}, new int[]{1000, 1, 1, 1000});

        // --- NEW corner cases ---
        check("single min value bound", new int[]{1}, new int[]{1});
        check("single max value bound", new int[]{1000}, new int[]{1000});
        check("two equal", new int[]{5, 5}, new int[]{5, 5});
        check("two max bound", new int[]{1000, 1000}, new int[]{1000, 1000});
        check("all equal odd length", new int[]{3, 3, 3, 3, 3}, new int[]{3, 3, 3, 3, 3});
        check("heavy duplicates", new int[]{2, 2, 2, 2, 2, 2}, new int[]{2, 2, 2, 2, 2, 2});
        check("strictly increasing 8 (even)", seq(1, 8), expectedReorder(seq(1, 8)));
        check("strictly increasing 9 (odd)", seq(1, 9), expectedReorder(seq(1, 9)));
        check("strictly decreasing 6", new int[]{6, 5, 4, 3, 2, 1}, new int[]{6, 1, 5, 2, 4, 3});
        check("alternating values even", new int[]{1, 1000, 1, 1000, 1, 1000}, expectedReorder(new int[]{1, 1000, 1, 1000, 1, 1000}));
        check("alternating values odd", new int[]{1, 1000, 1, 1000, 1}, expectedReorder(new int[]{1, 1000, 1, 1000, 1}));
        check("off-by-one even 10", seq(10, 10), expectedReorder(seq(10, 10)));
        check("off-by-one odd 11", seq(10, 11), expectedReorder(seq(10, 11)));
        check("palindrome values", new int[]{1, 2, 3, 2, 1}, expectedReorder(new int[]{1, 2, 3, 2, 1}));
        check("all max bound length 7", new int[]{1000, 1000, 1000, 1000, 1000, 1000, 1000}, new int[]{1000, 1000, 1000, 1000, 1000, 1000, 1000});
        check("original 100 retained", seq(1, 100), expectedReorder(seq(1, 100)));
        check("original 101 retained", seq(1, 101), expectedReorder(seq(1, 101)));

        // --- LARGE / PERFORMANCE cases ---
        // Constraint upper bound n = 5 * 10^4. A correct O(n) approach
        // (find middle -> reverse second half -> merge) must stay fast;
        // a quadratic merge would blow the budget. Verified by property, timed.
        int max = 50_000;

        // Even length at the max bound, sequential values.
        checkReorderProperty("max-size even sequential", seq(1, max));

        // Odd length (max-1) sequential.
        checkReorderProperty("max-size odd sequential", seq(1, max - 1));

        // Random values within [1,1000], fixed seed.
        Random rnd = new Random(42);
        int[] big = new int[max];
        for (int i = 0; i < max; i++) big[i] = 1 + rnd.nextInt(1000);
        checkReorderProperty("max-size random seed=42", big);

        // All-equal large list (stresses dup handling at scale).
        int[] allEq = new int[max];
        Arrays.fill(allEq, 500);
        checkReorderProperty("max-size all-equal", allEq);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
