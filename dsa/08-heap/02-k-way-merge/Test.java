import java.util.*;

// NOTE: This test relies on the `ListNode` class that you define alongside your
// solution (see QUESTION.md). Do NOT redefine ListNode here.
// Compile with:  javac Answer.java Test.java
public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ---- existing cases (kept) ----
        check("doc example: 3 lists",
                new int[][]{{1,4,5},{1,3,4},{2,6}},
                new int[]{1,1,2,3,4,4,5,6});
        check("empty array of lists", new int[][]{}, new int[]{});
        check("single empty list [[]]", new int[][]{{}}, new int[]{});
        check("single non-empty list", new int[][]{{1,2,3}}, new int[]{1,2,3});
        check("two lists, one empty", new int[][]{{},{2,5,7}}, new int[]{2,5,7});
        check("all lists empty", new int[][]{{},{},{}}, new int[]{});
        check("negatives and duplicates",
                new int[][]{{-3,-1,2},{-2,-2,0},{-10000,10000}},
                new int[]{-10000,-3,-2,-2,-1,0,2,10000});
        check("single element lists", new int[][]{{5},{1},{3}}, new int[]{1,3,5});
        check("already-merged ordering", new int[][]{{1,2,3},{4,5,6}}, new int[]{1,2,3,4,5,6});
        check("reverse-block ordering", new int[][]{{4,5,6},{1,2,3}}, new int[]{1,2,3,4,5,6});
        check("all equal across lists", new int[][]{{2,2},{2},{2,2}}, new int[]{2,2,2,2,2});

        // ---- NEW corner cases ----
        check("k=0 (empty -> empty)", new int[][]{}, new int[]{});
        check("single list single element", new int[][]{{7}}, new int[]{7});
        check("single list at min bound", new int[][]{{-10000}}, new int[]{-10000});
        check("single list at max bound", new int[][]{{10000}}, new int[]{10000});
        check("min and max only", new int[][]{{-10000},{10000}}, new int[]{-10000,10000});
        check("many empty lists with one real",
                new int[][]{{},{},{},{},{1,2,3},{}}, new int[]{1,2,3});
        check("all-equal single value across many",
                new int[][]{{0},{0},{0},{0}}, new int[]{0,0,0,0});
        check("interleaving heavy duplicates",
                new int[][]{{1,1,1},{1,1},{1}}, new int[]{1,1,1,1,1,1});
        check("strictly increasing single big list",
                new int[][]{{-5,-4,-3,-2,-1,0,1,2,3,4,5}},
                new int[]{-5,-4,-3,-2,-1,0,1,2,3,4,5});
        check("lists with disjoint ranges",
                new int[][]{{1,2,3},{10,11,12},{20,21}},
                new int[]{1,2,3,10,11,12,20,21});
        check("perfectly interleaved",
                new int[][]{{1,4,7},{2,5,8},{3,6,9}},
                new int[]{1,2,3,4,5,6,7,8,9});
        check("one long, many singletons",
                new int[][]{{1,2,3,4,5},{0},{6},{-1}},
                new int[]{-1,0,1,2,3,4,5,6});
        check("negatives only, descending blocks",
                new int[][]{{-1,-1},{-3,-2},{-5,-4}},
                new int[]{-5,-4,-3,-2,-1,-1});
        check("duplicate boundary values across lists",
                new int[][]{{-10000,-10000},{-10000,10000},{10000,10000}},
                new int[]{-10000,-10000,-10000,10000,10000,10000});
        check("two lists fully overlapping values",
                new int[][]{{1,2,3,4,5},{1,2,3,4,5}},
                new int[]{1,1,2,2,3,3,4,4,5,5});
        check("empty between non-empty",
                new int[][]{{1,5},{},{3,9},{}}, new int[]{1,3,5,9});

        // ---- LARGE / PERFORMANCE / SCALE cases (property-verified via oracle) ----
        // constraints: k <= 10^4, each list <= 500, vals in [-10000,10000].
        largeRandom("large k=10000 lists, ~5 each (N~50000)", 10000, 5);
        largeRandom("large k=2000 lists, len 25 each (N~50000)", 2000, 25);
        largeRandom("large k=200 lists, len 500 each (N~100000)", 200, 500);
        largeRandom("large k=1000 lists, mixed lengths", 1000, 100);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

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
        List<Integer> out = new ArrayList<>();
        for (ListNode n = head; n != null; n = n.next) {
            out.add(n.val);
        }
        int[] arr = new int[out.size()];
        for (int i = 0; i < arr.length; i++) { arr[i] = out.get(i); }
        return arr;
    }

    static void check(String label, int[][] listVals, int[] expected) {
        total++;
        try {
            ListNode[] lists = new ListNode[listVals.length];
            for (int i = 0; i < listVals.length; i++) {
                lists[i] = build(listVals[i]);
            }
            ListNode merged = new Answer().mergeKLists(lists);
            int[] actual = toArray(merged);
            if (Arrays.equals(actual, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + " | input=" + Arrays.deepToString(listVals)
                        + " | expected=" + Arrays.toString(expected)
                        + " | actual=" + Arrays.toString(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label
                    + " | input=" + Arrays.deepToString(listVals)
                    + " | expected=" + Arrays.toString(expected)
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // build k random sorted lists each up to maxLen; verify the merge equals
    // the independently sorted concatenation of all values (the unambiguous oracle),
    // and time it on a generous budget.
    static void largeRandom(String label, int k, int maxLen) {
        total++;
        try {
            Random rnd = new Random(42);
            int[][] listVals = new int[k][];
            List<Integer> all = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                int len = maxLen <= 1 ? maxLen : 1 + rnd.nextInt(maxLen); // 1..maxLen
                if (rnd.nextInt(20) == 0) len = 0; // occasional empty list
                int[] vals = new int[len];
                int cur = -10000;
                for (int j = 0; j < len; j++) {
                    cur += rnd.nextInt(40); // non-decreasing steps keep it sorted & in range-ish
                    if (cur > 10000) cur = 10000;
                    vals[j] = cur;
                    all.add(cur);
                }
                listVals[i] = vals;
            }
            int[] expected = new int[all.size()];
            for (int i = 0; i < expected.length; i++) expected[i] = all.get(i);
            Arrays.sort(expected);

            ListNode[] lists = new ListNode[k];
            for (int i = 0; i < k; i++) lists[i] = build(listVals[i]);

            long t0 = System.nanoTime();
            ListNode merged = new Answer().mergeKLists(lists);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lab = label + " (N=" + expected.length + ") [" + elapsedMs + " ms]";

            int[] actual = toArray(merged);
            boolean ok = Arrays.equals(actual, expected);
            if (elapsedMs > 3000) ok = false;
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + lab);
            } else {
                String aStr = actual.length > 30 ? Arrays.toString(Arrays.copyOf(actual, 30)) + "...len=" + actual.length
                        : Arrays.toString(actual);
                System.out.println("\033[31m[FAIL]\033[0m " + lab
                        + " | expectedLen=" + expected.length
                        + " | actualLen=" + actual.length
                        + " | actualHead=" + aStr);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
