import java.util.*;

// ListNode is referenced by Answer.removeNthFromEnd but not defined in Answer.java,
// so we define it here. Matches QUESTION.md definition with three constructors.
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

    // Convert list to array; guards against accidental cycles to avoid infinite loop.
    static int[] toArray(ListNode head) {
        List<Integer> out = new ArrayList<>();
        int guard = 0;
        ListNode cur = head;
        while (cur != null && guard < 1000000) {
            out.add(cur.val);
            cur = cur.next;
            guard++;
        }
        int[] arr = new int[out.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = out.get(i);
        return arr;
    }

    // Independent oracle: array with the (len - n)-th element (0-based from front) removed.
    static int[] expectedAfterRemoval(int[] vals, int n) {
        int removeIdx = vals.length - n;
        int[] out = new int[vals.length - 1];
        int j = 0;
        for (int i = 0; i < vals.length; i++) if (i != removeIdx) out[j++] = vals[i];
        return out;
    }

    static void check(String name, int[] vals, int n, int[] expected) {
        total++;
        try {
            ListNode head = build(vals);
            ListNode res = new Answer().removeNthFromEnd(head, n);
            int[] actual = toArray(res);
            if (Arrays.equals(actual, expected)) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | input vals=" + brief(vals) + ", n=" + n
                    + " | expected=" + Arrays.toString(expected)
                    + " | actual=" + Arrays.toString(actual));
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                + " | input vals=" + brief(vals) + ", n=" + n
                + " | expected=" + Arrays.toString(expected)
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Large/timed variant: verifies against the independent oracle and flags catastrophic slowness.
    static void checkTimed(String name, int[] vals, int n) {
        total++;
        int[] expected = expectedAfterRemoval(vals, n);
        ListNode head = build(vals);
        long t0 = System.nanoTime();
        try {
            ListNode res = new Answer().removeNthFromEnd(head, n);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            int[] actual = toArray(res);
            boolean ok = Arrays.equals(actual, expected) && ms <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + ms + " ms)");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                    + " | n_list=" + vals.length + ", n=" + n
                    + (ms > 3000 ? " | too slow" : " | result mismatch")
                    + " | expectedLen=" + expected.length + ", actualLen=" + actual.length);
            }
        } catch (Throwable e) {
            long ms = (System.nanoTime() - t0) / 1_000_000;
            System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                + " | n_list=" + vals.length + ", n=" + n
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static String brief(int[] a) {
        if (a.length <= 30) return Arrays.toString(a);
        return "[len=" + a.length + " " + a[0] + "," + a[1] + ",...," + a[a.length - 1] + "]";
    }

    public static void main(String[] args) {
        // ---------- Existing cases (kept) ----------
        // Examples from QUESTION.md
        check("example 1 (remove 2nd from end)", new int[]{1,2,3,4,5}, 2, new int[]{1,2,3,5});
        check("example 2 (single node removed)", new int[]{1}, 1, new int[]{});
        check("example 3 (remove last of two)", new int[]{1,2}, 1, new int[]{1});

        // Corner: remove the head (n == size)
        check("remove head of two", new int[]{1,2}, 2, new int[]{2});
        check("remove head of five", new int[]{1,2,3,4,5}, 5, new int[]{2,3,4,5});

        // Corner: remove the tail (n == 1)
        check("remove tail of five", new int[]{1,2,3,4,5}, 1, new int[]{1,2,3,4});

        // Corner: remove a middle node
        check("remove middle", new int[]{1,2,3,4,5}, 3, new int[]{1,2,4,5});

        // Corner: three-element variations
        check("three remove head", new int[]{10,20,30}, 3, new int[]{20,30});
        check("three remove middle", new int[]{10,20,30}, 2, new int[]{10,30});
        check("three remove tail", new int[]{10,20,30}, 1, new int[]{10,20});

        // Corner: duplicates in values
        check("duplicates remove from end", new int[]{7,7,7,7}, 2, new int[]{7,7,7});
        check("duplicates remove head", new int[]{7,7,7,7}, 4, new int[]{7,7,7});

        // Corner: value boundaries (0 and 100)
        check("value bounds", new int[]{0,100,0,100}, 2, new int[]{0,100,100});

        // Corner: max size (sz = 30), remove middle
        int[] thirty = new int[30];
        for (int i = 0; i < 30; i++) thirty[i] = i + 1;
        int[] expectedThirty = new int[29];
        int idx = 0;
        for (int i = 0; i < 30; i++) if (i != 14) expectedThirty[idx++] = i + 1; // removing 15th from end -> index 14 (0-based) from front since n=16
        check("max size remove (n=16)", thirty, 16, expectedThirty);

        // ---------- New corner cases ----------
        // Two elements, remove the head (n == size)
        check("two remove head", new int[]{9,8}, 2, new int[]{8});
        // Single element edge (only valid n is 1) -> empty
        check("single remove only", new int[]{55}, 1, new int[]{});
        // Four elements, remove second-from-end
        check("four remove 2nd from end", new int[]{1,2,3,4}, 2, new int[]{1,2,4});
        // Remove head of a longer list
        check("remove head of seven", new int[]{1,2,3,4,5,6,7}, 7, new int[]{2,3,4,5,6,7});
        // Remove tail of a longer list
        check("remove tail of seven", new int[]{1,2,3,4,5,6,7}, 1, new int[]{1,2,3,4,5,6});
        // All-equal values, remove the tail
        check("all equal remove tail", new int[]{5,5,5,5,5}, 1, new int[]{5,5,5,5});
        // All-equal values, remove the head
        check("all equal remove head", new int[]{5,5,5,5,5}, 5, new int[]{5,5,5,5});
        // Value bounds with zeros and 100s, remove head
        check("value bounds remove head", new int[]{0,0,100,100}, 4, new int[]{0,100,100});
        // Strictly increasing, remove a near-front element
        check("increasing remove near front", new int[]{1,2,3,4,5,6}, 5, new int[]{1,3,4,5,6});
        // Strictly decreasing values, remove near tail
        check("decreasing remove near tail", new int[]{6,5,4,3,2,1}, 2, new int[]{6,5,4,3,1});
        // Two-element duplicate, remove tail
        check("two dup remove tail", new int[]{7,7}, 1, new int[]{7});
        // Max size (sz=30), remove the head (n == 30)
        int[] thirtyHead = new int[29];
        for (int i = 0; i < 29; i++) thirtyHead[i] = i + 2;
        check("max size remove head (n=30)", thirty, 30, thirtyHead);
        // Max size (sz=30), remove the tail (n == 1)
        int[] thirtyTail = new int[29];
        for (int i = 0; i < 29; i++) thirtyTail[i] = i + 1;
        check("max size remove tail (n=1)", thirty, 1, thirtyTail);
        // Off-by-one: remove the 2nd node from the end on a 3-list
        check("three remove 2nd from end", new int[]{4,5,6}, 2, new int[]{4,6});
        // Alternating values, remove a middle node
        check("alternating remove middle", new int[]{1,0,1,0,1}, 3, new int[]{1,0,0,1});

        // ---------- Large / performance cases (oracle-verified, generous time budget) ----------
        // Note: the OPTIMAL solution is one pass with a fixed-gap two-pointer; it must scale
        // linearly. We build lists far larger than the tiny stated bound to expose any
        // accidental quadratic behavior (e.g. recomputing length per step).
        Random rnd = new Random(42);

        // Case A: very large list, remove the tail (n = 1) -> longest walk for the lead pointer.
        {
            int n = 100000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(101); // 0..100
            checkTimed("large n=100000 remove tail", arr, 1);
        }

        // Case B: very large list, remove the head (n = list length).
        {
            int n = 100000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(101);
            checkTimed("large n=100000 remove head", arr, n);
        }

        // Case C: very large list, remove a random interior node.
        {
            int n = 100000;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(101);
            int nth = 1 + rnd.nextInt(n); // 1..n
            checkTimed("large n=100000 remove random nth", arr, nth);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
