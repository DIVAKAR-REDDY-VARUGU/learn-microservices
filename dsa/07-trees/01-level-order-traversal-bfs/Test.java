import java.util.*;

// TreeNode is referenced by Answer.java but not defined there; define it here so
// `javac Answer.java Test.java` compiles both together.
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

public class Test {
    static int pass = 0;
    static int total = 0;

    // Build a tree from LeetCode-style level-order array (null = absent child).
    static TreeNode build(Integer[] a) {
        if (a == null || a.length == 0 || a[0] == null) return null;
        TreeNode root = new TreeNode(a[0]);
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < a.length) {
            TreeNode cur = q.poll();
            if (i < a.length) {
                if (a[i] != null) { cur.left = new TreeNode(a[i]); q.add(cur.left); }
                i++;
            }
            if (i < a.length) {
                if (a[i] != null) { cur.right = new TreeNode(a[i]); q.add(cur.right); }
                i++;
            }
        }
        return root;
    }

    // Independent oracle: level-order traversal via BFS (used to verify big inputs).
    static List<List<Integer>> oracle(TreeNode root) {
        List<List<Integer>> out = new ArrayList<>();
        if (root == null) return out;
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            int sz = q.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < sz; i++) {
                TreeNode n = q.poll();
                level.add(n.val);
                if (n.left != null) q.add(n.left);
                if (n.right != null) q.add(n.right);
            }
            out.add(level);
        }
        return out;
    }

    static void check(String name, Integer[] arr, List<List<Integer>> expected) {
        total++;
        try {
            List<List<Integer>> actual = new Answer().levelOrder(build(arr));
            if (expected.equals(actual)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + "\n        input=" + Arrays.toString(arr)
                        + "\n     expected=" + expected
                        + "\n       actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage()
                    + "\n        input=" + Arrays.toString(arr)
                    + "\n     expected=" + expected);
        }
    }

    // Property check for big trees: compare Answer's output against the BFS oracle.
    static void checkBig(String name, TreeNode root, long budgetMs) {
        total++;
        try {
            List<List<Integer>> expected = oracle(root);
            long t0 = System.nanoTime();
            List<List<Integer>> actual = new Answer().levelOrder(root);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            String label = name + String.format(" (%.1f ms)", ms);
            boolean ok = expected.equals(actual);
            if (ok && ms > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " -> exceeded budget " + budgetMs + " ms");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + "\n     expected #levels=" + expected.size()
                        + "\n       actual #levels=" + (actual == null ? "null" : actual.size()));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    // Convenience builders for expected nested lists.
    static List<List<Integer>> ll(int[]... rows) {
        List<List<Integer>> out = new ArrayList<>();
        for (int[] r : rows) {
            List<Integer> row = new ArrayList<>();
            for (int v : r) row.add(v);
            out.add(row);
        }
        return out;
    }

    public static void main(String[] args) {
        // ----- Existing cases (kept) -----
        // Example 1: [3,9,20,null,null,15,7] -> [[3],[9,20],[15,7]]
        check("example: [3,9,20,null,null,15,7]",
                new Integer[]{3, 9, 20, null, null, 15, 7},
                ll(new int[]{3}, new int[]{9, 20}, new int[]{15, 7}));

        // Example 2: single node
        check("single node [1]", new Integer[]{1}, ll(new int[]{1}));

        // Example 3: empty tree
        check("empty tree []", new Integer[]{}, new ArrayList<List<Integer>>());
        check("null root", new Integer[]{null}, new ArrayList<List<Integer>>());

        // Full perfect tree.
        check("perfect tree [1,2,3,4,5,6,7]",
                new Integer[]{1, 2, 3, 4, 5, 6, 7},
                ll(new int[]{1}, new int[]{2, 3}, new int[]{4, 5, 6, 7}));

        // Left-skewed tree.
        check("left-skewed [1,2,null,3,null,4]",
                new Integer[]{1, 2, null, 3, null, 4},
                ll(new int[]{1}, new int[]{2}, new int[]{3}, new int[]{4}));

        // Right-skewed tree.
        check("right-skewed [1,null,2,null,3]",
                new Integer[]{1, null, 2, null, 3},
                ll(new int[]{1}, new int[]{2}, new int[]{3}));

        // Negatives and zero.
        check("negatives/zero [0,-1,-2,-3,null,null,-4]",
                new Integer[]{0, -1, -2, -3, null, null, -4},
                ll(new int[]{0}, new int[]{-1, -2}, new int[]{-3, -4}));

        // Duplicate values.
        check("duplicates [5,5,5,5,5]",
                new Integer[]{5, 5, 5, 5, 5},
                ll(new int[]{5}, new int[]{5, 5}, new int[]{5, 5}));

        // Min/max bound values.
        check("bounds [-1000,1000,-1000]",
                new Integer[]{-1000, 1000, -1000},
                ll(new int[]{-1000}, new int[]{1000, -1000}));

        // Uneven shape: left child present, right missing at various levels.
        check("uneven [1,2,3,4,null,null,5]",
                new Integer[]{1, 2, 3, 4, null, null, 5},
                ll(new int[]{1}, new int[]{2, 3}, new int[]{4, 5}));

        // ----- New corner cases -----
        // Two nodes, left only.
        check("two nodes left only [1,2]", new Integer[]{1, 2},
                ll(new int[]{1}, new int[]{2}));
        // Two nodes, right only.
        check("two nodes right only [1,null,2]", new Integer[]{1, null, 2},
                ll(new int[]{1}, new int[]{2}));
        // All-equal complete tree.
        check("all-equal [7,7,7,7,7,7,7]", new Integer[]{7, 7, 7, 7, 7, 7, 7},
                ll(new int[]{7}, new int[]{7, 7}, new int[]{7, 7, 7, 7}));
        // All-negative perfect tree.
        check("all-negative [-1,-2,-3,-4,-5,-6,-7]",
                new Integer[]{-1, -2, -3, -4, -5, -6, -7},
                ll(new int[]{-1}, new int[]{-2, -3}, new int[]{-4, -5, -6, -7}));
        // Zeros everywhere.
        check("zeros [0,0,0,0,0]", new Integer[]{0, 0, 0, 0, 0},
                ll(new int[]{0}, new int[]{0, 0}, new int[]{0, 0}));
        // Lower bound only.
        check("min bound single [-1000]", new Integer[]{-1000}, ll(new int[]{-1000}));
        // Upper bound only.
        check("max bound single [1000]", new Integer[]{1000}, ll(new int[]{1000}));
        // Zigzag-ish: each level one node, alternating sides.
        check("alternating sides [1,2,null,null,3,4]",
                new Integer[]{1, 2, null, null, 3, 4},
                ll(new int[]{1}, new int[]{2}, new int[]{3}, new int[]{4}));
        // Wide last level with gaps.
        check("gappy last level [1,2,3,4,null,6,null]",
                new Integer[]{1, 2, 3, 4, null, 6, null},
                ll(new int[]{1}, new int[]{2, 3}, new int[]{4, 6}));
        // Deeper unbalanced: long right spine with single left at the bottom.
        check("right spine then left [1,null,2,null,3,4]",
                new Integer[]{1, null, 2, null, 3, 4},
                ll(new int[]{1}, new int[]{2}, new int[]{3}, new int[]{4}));
        // Mixed bounds across levels.
        check("mixed bounds [0,-1000,1000,500,-500,250,-250]",
                new Integer[]{0, -1000, 1000, 500, -500, 250, -250},
                ll(new int[]{0}, new int[]{-1000, 1000}, new int[]{500, -500, 250, -250}));

        // ----- Large / performance cases (verified against BFS oracle) -----
        Random rnd = new Random(42);

        // Perfect-ish complete tree near node-count upper bound (2000 nodes allowed;
        // build a complete tree of 2000 nodes from a value array).
        {
            int n = 2000;
            Integer[] a = new Integer[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(2001) - 1000;
            checkBig("complete tree n=2000 (random vals)", build(a), 3000);
        }

        // Left-skewed deep tree of 2000 nodes (degenerate; stresses per-level work).
        {
            int n = 2000;
            TreeNode root = new TreeNode(rnd.nextInt(2001) - 1000);
            TreeNode cur = root;
            for (int i = 1; i < n; i++) {
                cur.left = new TreeNode(rnd.nextInt(2001) - 1000);
                cur = cur.left;
            }
            checkBig("left-skewed n=2000", root, 3000);
        }

        // Right-skewed deep tree of 2000 nodes.
        {
            int n = 2000;
            TreeNode root = new TreeNode(rnd.nextInt(2001) - 1000);
            TreeNode cur = root;
            for (int i = 1; i < n; i++) {
                cur.right = new TreeNode(rnd.nextInt(2001) - 1000);
                cur = cur.right;
            }
            checkBig("right-skewed n=2000", root, 3000);
        }

        // Random-shaped tree of 2000 nodes (each new node attached to a random
        // existing node's free child slot) -> irregular level widths.
        {
            int n = 2000;
            List<TreeNode> nodes = new ArrayList<>();
            TreeNode root = new TreeNode(rnd.nextInt(2001) - 1000);
            nodes.add(root);
            int made = 1;
            while (made < n) {
                TreeNode parent = nodes.get(rnd.nextInt(nodes.size()));
                if (parent.left != null && parent.right != null) continue;
                TreeNode child = new TreeNode(rnd.nextInt(2001) - 1000);
                if (parent.left == null && (parent.right != null || rnd.nextBoolean()))
                    parent.left = child;
                else if (parent.right == null)
                    parent.right = child;
                else
                    parent.left = child;
                nodes.add(child);
                made++;
            }
            checkBig("random-shaped n=2000", root, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
