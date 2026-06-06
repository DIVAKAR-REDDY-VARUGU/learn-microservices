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
            ListNode result = new Answer().mergeTwoLists(l1, l2);
            int[] actual = toArray(result);
            if (Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + " | list1=" + brief(a)
                        + " list2=" + brief(b)
                        + " expected=" + brief(expected)
                        + " actual=" + brief(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | list1=" + brief(a)
                    + " list2=" + brief(b)
                    + " expected=" + brief(expected)
                    + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    // Independent oracle: merge two sorted arrays in O(n+m).
    static int[] mergeOracle(int[] a, int[] b) {
        int[] out = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) out[k++] = a[i++];
            else out[k++] = b[j++];
        }
        while (i < a.length) out[k++] = a[i++];
        while (j < b.length) out[k++] = b[j++];
        return out;
    }

    // Property check for large/random inputs: output must equal the oracle merge
    // (sorted AND a multiset-permutation of the two inputs). Reports timing.
    static void checkMergeProperty(String name, int[] a, int[] b) {
        total++;
        try {
            ListNode l1 = build(a);
            ListNode l2 = build(b);
            long t0 = System.nanoTime();
            ListNode result = new Answer().mergeTwoLists(l1, l2);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            int[] actual = toArray(result);
            int[] expected = mergeOracle(a, b);
            String label = name + " (n=" + a.length + ", m=" + b.length + ", " + elapsedMs + " ms)";
            if (!Arrays.equals(actual, expected)) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | merged output does not match independent oracle");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> 3000 ms)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | n=" + a.length + ", m=" + b.length
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

    static int[] sortedRandom(Random rnd, int n, int lo, int hi) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = lo + rnd.nextInt(hi - lo + 1);
        Arrays.sort(a);
        return a;
    }

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("example 1", new int[]{1, 2, 4}, new int[]{1, 3, 4}, new int[]{1, 1, 2, 3, 4, 4});
        check("example 2: both empty", new int[]{}, new int[]{}, new int[]{});
        check("example 3: empty + [0]", new int[]{}, new int[]{0}, new int[]{0});

        // Corner cases (original)
        check("[0] + empty", new int[]{0}, new int[]{}, new int[]{0});
        check("single + single, a<b", new int[]{1}, new int[]{2}, new int[]{1, 2});
        check("single + single, a>b", new int[]{5}, new int[]{2}, new int[]{2, 5});
        check("single + single, equal", new int[]{3}, new int[]{3}, new int[]{3, 3});
        check("all of a before all of b", new int[]{1, 2, 3}, new int[]{4, 5, 6}, new int[]{1, 2, 3, 4, 5, 6});
        check("all of b before all of a", new int[]{4, 5, 6}, new int[]{1, 2, 3}, new int[]{1, 2, 3, 4, 5, 6});
        check("interleaved", new int[]{1, 3, 5, 7}, new int[]{2, 4, 6, 8}, new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        check("duplicates across lists", new int[]{2, 2, 2}, new int[]{2, 2}, new int[]{2, 2, 2, 2, 2});
        check("negatives", new int[]{-5, -3, -1}, new int[]{-4, -2, 0}, new int[]{-5, -4, -3, -2, -1, 0});
        check("min/max bounds", new int[]{-100, 0, 100}, new int[]{-100, 50, 100}, new int[]{-100, -100, 0, 50, 100, 100});
        check("different lengths", new int[]{1}, new int[]{0, 2, 3, 4, 5}, new int[]{0, 1, 2, 3, 4, 5});
        check("trailing tail from list1", new int[]{1, 2, 9, 10}, new int[]{3, 4}, new int[]{1, 2, 3, 4, 9, 10});

        // --- NEW corner cases ---
        check("both empty again", new int[]{}, new int[]{}, new int[]{});
        check("empty + multi", new int[]{}, new int[]{-3, 0, 7}, new int[]{-3, 0, 7});
        check("multi + empty", new int[]{-3, 0, 7}, new int[]{}, new int[]{-3, 0, 7});
        check("both all-min", new int[]{-100, -100}, new int[]{-100, -100, -100}, new int[]{-100, -100, -100, -100, -100});
        check("both all-max", new int[]{100, 100, 100}, new int[]{100, 100}, new int[]{100, 100, 100, 100, 100});
        check("all equal across both", new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0, 0, 0, 0});
        check("single min vs single max", new int[]{-100}, new int[]{100}, new int[]{-100, 100});
        check("one list fully less than other (touching)", new int[]{1, 2, 3}, new int[]{3, 4, 5}, new int[]{1, 2, 3, 3, 4, 5});
        check("heavy duplicates interleave", new int[]{1, 1, 1, 5}, new int[]{1, 1, 5, 5}, new int[]{1, 1, 1, 1, 1, 5, 5, 5});
        check("all negative both", new int[]{-50, -40, -30}, new int[]{-45, -35, -25}, new int[]{-50, -45, -40, -35, -30, -25});
        check("strictly increasing both, equal length", new int[]{0, 10, 20, 30}, new int[]{5, 15, 25, 35}, new int[]{0, 5, 10, 15, 20, 25, 30, 35});
        check("long list1, single list2 head", new int[]{-100, -100, 0, 50, 100}, new int[]{-100}, new int[]{-100, -100, -100, 0, 50, 100});
        check("tail from list2", new int[]{1, 2}, new int[]{1, 2, 3, 4, 5}, new int[]{1, 1, 2, 2, 3, 4, 5});
        check("first elements equal then diverge", new int[]{1, 100}, new int[]{1, 2}, new int[]{1, 1, 2, 100});
        check("max length 50 each, disjoint ranges", range(-100, 50, 1), range(-50, 50, 1), mergeOracle(range(-100, 50, 1), range(-50, 50, 1)));
        check("max length 50 + empty", range(-100, 50, 1), new int[]{}, range(-100, 50, 1));

        // --- LARGE / PERFORMANCE cases ---
        // NOTE: problem constraint caps each list at 50, but a correct O(n+m) splice
        // merge must scale; we stress well beyond the stated bound to expose any
        // accidental quadratic behaviour while still verifying by property.
        Random rnd = new Random(42);
        int big = 100_000;

        int[] a1 = sortedRandom(rnd, big, -1_000_000, 1_000_000);
        int[] b1 = sortedRandom(rnd, big, -1_000_000, 1_000_000);
        checkMergeProperty("large random sorted (seed=42)", a1, b1);

        // Fully disjoint: all of a2 < all of b2 (worst case for tail handling).
        int[] a2 = new int[big];
        int[] b2 = new int[big];
        for (int i = 0; i < big; i++) { a2[i] = i; b2[i] = big + i; }
        checkMergeProperty("large disjoint ascending", a2, b2);

        // Highly skewed lengths: long + short.
        int[] a3 = new int[big];
        for (int i = 0; i < big; i++) a3[i] = i * 2;
        int[] b3 = new int[]{-5, 1, 3, 5, 7, big, big * 2 + 1};
        Arrays.sort(b3);
        checkMergeProperty("large + tiny skewed", a3, b3);

        // Heavy duplicate values across both large lists.
        int[] a4 = new int[big];
        int[] b4 = new int[big];
        for (int i = 0; i < big; i++) { a4[i] = (i % 5); b4[i] = (i % 5); }
        Arrays.sort(a4);
        Arrays.sort(b4);
        checkMergeProperty("large heavy-duplicate values", a4, b4);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Ascending range starting at `from`, `count` elements, given `step`.
    static int[] range(int from, int count, int step) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) a[i] = from + i * step;
        return a;
    }
}
