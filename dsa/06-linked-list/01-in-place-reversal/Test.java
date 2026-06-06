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

    // Build a linked list from an int[]; empty array -> null head.
    static ListNode build(int[] vals) {
        ListNode dummy = new ListNode();
        ListNode cur = dummy;
        for (int v : vals) {
            cur.next = new ListNode(v);
            cur = cur.next;
        }
        return dummy.next;
    }

    // Convert a linked list back to int[] (null -> empty array).
    static int[] toArray(ListNode head) {
        ArrayList<Integer> out = new ArrayList<>();
        ListNode cur = head;
        // Guard against accidental cycles in a buggy solution.
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

    static void check(String name, int[] input, int[] expected) {
        total++;
        try {
            ListNode head = build(input);
            ListNode result = new Answer().reverseList(head);
            int[] actual = toArray(result);
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

    // Property-based check for large inputs: result must equal the reverse of input.
    // Verifies (1) length preserved, (2) exact reverse order, and reports timing.
    static void checkReverseProperty(String name, int[] input) {
        total++;
        try {
            ListNode head = build(input);
            long t0 = System.nanoTime();
            ListNode result = new Answer().reverseList(head);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            int[] actual = toArray(result);
            String label = name + " (n=" + input.length + ", " + elapsedMs + " ms)";
            boolean ok = actual.length == input.length;
            if (ok) {
                for (int i = 0; i < input.length; i++) {
                    if (actual[i] != input[input.length - 1 - i]) { ok = false; break; }
                }
            }
            if (!ok) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | output is not the exact reverse of input");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> 3000 ms) -- likely worse than O(n)");
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

    // Compact array printer so huge inputs don't flood the console.
    static String brief(int[] a) {
        if (a.length <= 20) return Arrays.toString(a);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) sb.append(a[i]).append(", ");
        sb.append("... (").append(a.length).append(" elems) ...");
        for (int i = a.length - 5; i < a.length; i++) sb.append(", ").append(a[i]);
        return sb.append("]").toString();
    }

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("example 1: [1,2,3,4,5]", new int[]{1, 2, 3, 4, 5}, new int[]{5, 4, 3, 2, 1});
        check("example 2: [1,2]", new int[]{1, 2}, new int[]{2, 1});
        check("example 3: empty", new int[]{}, new int[]{});

        // Corner cases (original)
        check("single element", new int[]{7}, new int[]{7});
        check("two equal elements", new int[]{4, 4}, new int[]{4, 4});
        check("all equal", new int[]{9, 9, 9, 9}, new int[]{9, 9, 9, 9});
        check("with duplicates", new int[]{1, 2, 2, 3, 1}, new int[]{1, 3, 2, 2, 1});
        check("negatives", new int[]{-1, -2, -3}, new int[]{-3, -2, -1});
        check("mixed signs and zero", new int[]{0, -5, 5, 0}, new int[]{0, 5, -5, 0});
        check("already reversed-looking", new int[]{5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5});
        check("min/max bounds", new int[]{-5000, 5000, 0}, new int[]{0, 5000, -5000});
        check("three elements", new int[]{10, 20, 30}, new int[]{30, 20, 10});

        // --- NEW corner cases ---
        check("single zero", new int[]{0}, new int[]{0});
        check("single min bound", new int[]{-5000}, new int[]{-5000});
        check("single max bound", new int[]{5000}, new int[]{5000});
        check("two elements descending", new int[]{2, 1}, new int[]{1, 2});
        check("two min/max", new int[]{-5000, 5000}, new int[]{5000, -5000});
        check("all zeros", new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0});
        check("all negative", new int[]{-1, -2, -3, -4, -5}, new int[]{-5, -4, -3, -2, -1});
        check("strictly increasing", new int[]{1, 2, 3, 4, 5, 6, 7, 8}, new int[]{8, 7, 6, 5, 4, 3, 2, 1});
        check("strictly decreasing", new int[]{8, 7, 6, 5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        check("alternating signs", new int[]{1, -1, 1, -1, 1}, new int[]{1, -1, 1, -1, 1});
        check("alternating values", new int[]{5, 9, 5, 9, 5, 9}, new int[]{9, 5, 9, 5, 9, 5});
        check("heavy duplicates", new int[]{3, 3, 3, 1, 1, 3, 3}, new int[]{3, 3, 1, 1, 3, 3, 3});
        check("palindrome stays palindrome", new int[]{1, 2, 3, 2, 1}, new int[]{1, 2, 3, 2, 1});
        check("four element off-by-one", new int[]{1, 2, 3, 4}, new int[]{4, 3, 2, 1});
        check("both bounds repeated", new int[]{-5000, -5000, 5000, 5000}, new int[]{5000, 5000, -5000, -5000});
        check("original longer list of 1000 retained", buildRange(0, 1000), buildRangeRev(0, 1000));

        // --- LARGE / PERFORMANCE cases ---
        // Max constraint n = 5000.
        int n1 = 5000;
        int[] big1 = new int[n1];
        for (int i = 0; i < n1; i++) big1[i] = i - 2500; // spans negative..positive within bound
        checkReverseProperty("max-size sequential 5000", big1);

        // Random within value bounds, fixed seed.
        Random rnd = new Random(42);
        int n2 = 5000;
        int[] big2 = new int[n2];
        for (int i = 0; i < n2; i++) big2[i] = rnd.nextInt(10001) - 5000; // [-5000, 5000]
        checkReverseProperty("max-size random seed=42", big2);

        // All-equal large list.
        int n3 = 5000;
        int[] big3 = new int[n3];
        Arrays.fill(big3, 1234);
        checkReverseProperty("max-size all-equal", big3);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static int[] buildRange(int from, int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = from + i;
        return a;
    }

    static int[] buildRangeRev(int from, int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = from + (n - 1 - i);
        return a;
    }
}
