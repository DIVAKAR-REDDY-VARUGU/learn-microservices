import java.util.*;

// ListNode is referenced by Answer.hasCycle but not defined in Answer.java,
// so we define it here. Matches QUESTION.md definition: ListNode(int x).
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; next = null; }
}

public class Test {
    static int pass = 0;
    static int total = 0;

    // Build a list from values; if pos >= 0 the tail.next connects to node at index pos (cycle).
    static ListNode build(int[] vals, int pos) {
        if (vals.length == 0) return null;
        ListNode[] nodes = new ListNode[vals.length];
        for (int i = 0; i < vals.length; i++) nodes[i] = new ListNode(vals[i]);
        for (int i = 0; i < vals.length - 1; i++) nodes[i].next = nodes[i + 1];
        if (pos >= 0 && pos < vals.length) nodes[vals.length - 1].next = nodes[pos];
        return nodes[0];
    }

    static void check(String name, int[] vals, int pos, boolean expected) {
        total++;
        try {
            ListNode head = build(vals, pos);
            boolean actual = new Answer().hasCycle(head);
            if (actual == expected) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | input vals=" + brief(vals) + ", pos=" + pos
                    + " | expected=" + expected + " | actual=" + actual);
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                + " | input vals=" + brief(vals) + ", pos=" + pos
                + " | expected=" + expected
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Timed variant for large inputs: validates result equals expected AND flags catastrophic slowness.
    static void checkTimed(String name, int[] vals, int pos, boolean expected) {
        total++;
        ListNode head = build(vals, pos);
        long t0 = System.nanoTime();
        try {
            boolean actual = new Answer().hasCycle(head);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = (actual == expected) && ms <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + ms + " ms)");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                    + " | n=" + vals.length + ", pos=" + pos
                    + " | expected=" + expected + " | actual=" + actual
                    + (ms > 3000 ? " | too slow" : ""));
            }
        } catch (Throwable e) {
            long ms = (System.nanoTime() - t0) / 1_000_000;
            System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                + " | n=" + vals.length + ", pos=" + pos
                + " | expected=" + expected
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static String brief(int[] a) {
        if (a.length <= 20) return Arrays.toString(a);
        return "[len=" + a.length + " " + a[0] + "," + a[1] + ",...," + a[a.length - 1] + "]";
    }

    public static void main(String[] args) {
        // ---------- Existing cases (kept) ----------
        // Examples from QUESTION.md
        check("example 1 (cycle to index 1)", new int[]{3,2,0,-4}, 1, true);
        check("example 2 (cycle to index 0)", new int[]{1,2}, 0, true);
        check("example 3 (single, no cycle)", new int[]{1}, -1, false);

        // Corner: empty list
        check("empty list", new int[]{}, -1, false);

        // Corner: single node self-loop
        check("single node self-cycle", new int[]{1}, 0, true);

        // Corner: two nodes no cycle
        check("two nodes no cycle", new int[]{1,2}, -1, false);

        // Corner: cycle back to last node (self loop at tail)
        check("tail self loop", new int[]{1,2,3}, 2, true);

        // Corner: longer list, cycle to middle
        check("cycle to middle", new int[]{1,2,3,4,5,6}, 3, true);

        // Corner: longer list, no cycle
        check("long no cycle", new int[]{1,2,3,4,5,6,7,8,9,10}, -1, false);

        // Corner: cycle to head from end
        check("cycle to head", new int[]{1,2,3,4,5}, 0, true);

        // Corner: all equal values with cycle
        check("all equal with cycle", new int[]{7,7,7,7}, 2, true);

        // Corner: all equal values no cycle
        check("all equal no cycle", new int[]{7,7,7,7}, -1, false);

        // Corner: negative values, no cycle
        check("negatives no cycle", new int[]{-1,-2,-3}, -1, false);

        // Corner: large list no cycle
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        check("large no cycle", big, -1, false);

        // Corner: large list with cycle near the end
        check("large with cycle", Arrays.copyOf(big, 1000), 998, true);

        // ---------- New corner cases ----------
        // Two nodes, cycle to head (tail.next -> node 0)
        check("two nodes cycle to head", new int[]{1,2}, 0, true);
        // Single node, no cycle (already above but reaffirm minimal no-cycle)
        check("single no cycle reaffirm", new int[]{42}, -1, false);
        // Two-node tail self loop (tail points to itself, index 1)
        check("two nodes tail self loop", new int[]{1,2}, 1, true);
        // Cycle that returns to the second-to-last node
        check("cycle to second-to-last", new int[]{1,2,3,4,5}, 3, true);
        // Long list, cycle to index 1 (skips head)
        check("long cycle skip head", new int[]{10,20,30,40,50,60,70}, 1, true);
        // Value boundaries: min and max node values, no cycle
        check("value bounds no cycle", new int[]{-100000,0,100000}, -1, false);
        // Value boundaries with a cycle to head
        check("value bounds with cycle", new int[]{-100000,100000,-100000}, 0, true);
        // All identical large values, cycle to middle
        check("all max values cycle", new int[]{100000,100000,100000,100000,100000}, 2, true);
        // Strictly increasing, no cycle
        check("strictly increasing no cycle", new int[]{1,2,3,4,5,6,7,8}, -1, false);
        // Strictly decreasing, cycle to head
        check("strictly decreasing cycle", new int[]{8,7,6,5,4,3,2,1}, 0, true);
        // Alternating signs, no cycle
        check("alternating no cycle", new int[]{1,-1,1,-1,1,-1}, -1, false);
        // Alternating signs, cycle to a late node
        check("alternating cycle late", new int[]{1,-1,1,-1,1,-1}, 4, true);
        // Three nodes, cycle to middle
        check("three nodes cycle middle", new int[]{5,6,7}, 1, true);
        // Zeros only, no cycle
        check("zeros no cycle", new int[]{0,0,0,0,0}, -1, false);
        // Zeros only, cycle to head
        check("zeros cycle to head", new int[]{0,0,0,0,0}, 0, true);

        // ---------- Large / performance cases ----------
        Random rnd = new Random(42);

        // Case A: maximum-size list (n = 10000, the constraint upper bound), NO cycle.
        {
            int n = 10000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(200001) - 100000; // -1e5..1e5
            checkTimed("large n=10000 no cycle", arr, -1, false);
        }

        // Case B: maximum-size list with a cycle back to the head (worst case for traversal).
        {
            int n = 10000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(200001) - 100000;
            checkTimed("large n=10000 cycle to head", arr, 0, true);
        }

        // Case C: maximum-size list with a cycle to the last node only (smallest possible loop at tail).
        {
            int n = 10000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(200001) - 100000;
            checkTimed("large n=10000 tail self loop", arr, n - 1, true);
        }

        // Case D: large list with a cycle entering near the middle (slow/fast meet inside the loop).
        {
            int n = 10000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i;
            checkTimed("large n=10000 cycle to middle", arr, n / 2, true);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
