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

    // Find the (unique) node holding a given value (iterative, BFS).
    static TreeNode find(TreeNode root, int val) {
        if (root == null) return null;
        Deque<TreeNode> st = new ArrayDeque<>();
        st.push(root);
        while (!st.isEmpty()) {
            TreeNode n = st.pop();
            if (n.val == val) return n;
            if (n.left != null) st.push(n.left);
            if (n.right != null) st.push(n.right);
        }
        return null;
    }

    // Build an iterative parent map for the whole tree (handles 10^5 deep trees).
    static Map<TreeNode, TreeNode> parentMap(TreeNode root) {
        Map<TreeNode, TreeNode> par = new HashMap<>();
        par.put(root, null);
        Deque<TreeNode> st = new ArrayDeque<>();
        st.push(root);
        while (!st.isEmpty()) {
            TreeNode n = st.pop();
            if (n.left != null) { par.put(n.left, n); st.push(n.left); }
            if (n.right != null) { par.put(n.right, n); st.push(n.right); }
        }
        return par;
    }

    // Independent LCA oracle via ancestor chains from a parent map.
    static TreeNode oracleLCA(TreeNode root, TreeNode p, TreeNode q) {
        Map<TreeNode, TreeNode> par = parentMap(root);
        Set<TreeNode> anc = new HashSet<>();
        for (TreeNode n = p; n != null; n = par.get(n)) anc.add(n);
        for (TreeNode n = q; n != null; n = par.get(n)) if (anc.contains(n)) return n;
        return null;
    }

    static void check(String name, Integer[] arr, int pVal, int qVal, int expectedVal) {
        total++;
        try {
            TreeNode root = build(arr);
            TreeNode p = find(root, pVal);
            TreeNode q = find(root, qVal);
            TreeNode ans = new Answer().lowestCommonAncestor(root, p, q);
            if (ans != null && ans.val == expectedVal) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + "\n        input=" + Arrays.toString(arr) + " p=" + pVal + " q=" + qVal
                        + "\n     expected node val=" + expectedVal
                        + "\n       actual node val=" + (ans == null ? "null" : ans.val));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage()
                    + "\n        input=" + Arrays.toString(arr) + " p=" + pVal + " q=" + qVal
                    + "\n     expected node val=" + expectedVal);
        }
    }

    // Big-tree property check: verify Answer's LCA against the parent-map oracle.
    static void checkBig(String name, TreeNode root, TreeNode p, TreeNode q, long budgetMs) {
        total++;
        try {
            TreeNode expected = oracleLCA(root, p, q);
            long t0 = System.nanoTime();
            TreeNode ans = new Answer().lowestCommonAncestor(root, p, q);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            String label = name + String.format(" (%.1f ms)", ms);
            boolean ok = ans == expected; // identity: must return the exact node
            if (ok && ms > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " -> exceeded budget " + budgetMs + " ms");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + "\n     expected node val=" + (expected == null ? "null" : expected.val)
                        + "\n       actual node val=" + (ans == null ? "null" : ans.val));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    public static void main(String[] args) {
        // ----- Existing cases (kept) -----
        // Reference tree from the problem: [3,5,1,6,2,0,8,null,null,7,4]
        Integer[] ref = new Integer[]{3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};

        // Example 1: p=5, q=1 -> LCA 3 (root, in different subtrees).
        check("example: p=5,q=1 -> 3", ref, 5, 1, 3);

        // Example 2: p=5, q=4 -> 5 (node is descendant of itself).
        check("example: p=5,q=4 -> 5", ref, 5, 4, 5);

        // Example 3: tiny tree [1,2], p=1,q=2 -> 1.
        check("tiny [1,2]: p=1,q=2 -> 1", new Integer[]{1, 2}, 1, 2, 1);

        // p and q both in left subtree, deeper LCA: p=6,q=4 -> 5.
        check("p=6,q=4 -> 5", ref, 6, 4, 5);

        // p=7,q=4 -> 2 (both children of 2).
        check("p=7,q=4 -> 2", ref, 7, 4, 2);

        // p=6,q=7 -> 5.
        check("p=6,q=7 -> 5", ref, 6, 7, 5);

        // p=0,q=8 -> 1 (both in right subtree).
        check("p=0,q=8 -> 1", ref, 0, 8, 1);

        // p is an ancestor of q: p=5,q=7 -> 5.
        check("p=5,q=7 (ancestor) -> 5", ref, 5, 7, 5);

        // Symmetric: order of p,q swapped should not matter. p=1,q=5 -> 3.
        check("swapped p=1,q=5 -> 3", ref, 1, 5, 3);

        // Left-skewed line [1,2,null,3,null,4]: p=3,q=4 -> 3 (ancestor of itself).
        check("left-skewed p=3,q=4 -> 3",
                new Integer[]{1, 2, null, 3, null, 4}, 3, 4, 3);

        // Negative values: [-1,-2,-3], p=-2,q=-3 -> -1.
        check("negatives p=-2,q=-3 -> -1",
                new Integer[]{-1, -2, -3}, -2, -3, -1);

        // Large-magnitude values allowed by constraints.
        check("big values p=-1000000000,q=1000000000 -> 0",
                new Integer[]{0, -1000000000, 1000000000}, -1000000000, 1000000000, 0);

        // ----- New corner cases -----
        // Two-node right child only [1,null,2]: p=1,q=2 -> 1.
        check("two-node right [1,null,2] p=1,q=2 -> 1", new Integer[]{1, null, 2}, 1, 2, 1);
        // Root is one of the targets and is ancestor of the other: p=3,q=7 -> 3.
        check("root ancestor p=3,q=7 -> 3", ref, 3, 7, 3);
        // Two deep leaves in different subtrees of root: p=7,q=8 -> 3.
        check("deep cross-subtree p=7,q=8 -> 3", ref, 7, 8, 3);
        // p=6,q=2 -> 5 (6 is left of 5, 2 is right of 5).
        check("p=6,q=2 -> 5", ref, 6, 2, 5);
        // p=0,q=4 -> 3 (0 in right subtree, 4 in left subtree).
        check("p=0,q=4 -> 3", ref, 0, 4, 3);
        // Right-skewed line [1,null,2,null,3,null,4]: p=2,q=4 -> 2.
        check("right-skewed p=2,q=4 -> 2",
                new Integer[]{1, null, 2, null, 3, null, 4}, 2, 4, 2);
        // Right-skewed line: p=3,q=4 -> 3.
        check("right-skewed p=3,q=4 -> 3",
                new Integer[]{1, null, 2, null, 3, null, 4}, 3, 4, 3);
        // BST-shaped [6,2,8,0,4,7,9,null,null,3,5]: p=0,q=5 -> 2.
        Integer[] bst = new Integer[]{6, 2, 8, 0, 4, 7, 9, null, null, 3, 5};
        check("bst p=0,q=5 -> 2", bst, 0, 5, 2);
        // BST: p=3,q=5 -> 4.
        check("bst p=3,q=5 -> 4", bst, 3, 5, 4);
        // BST: p=0,q=9 -> 6 (root).
        check("bst p=0,q=9 -> 6", bst, 0, 9, 6);
        // BST: p=7,q=9 -> 8.
        check("bst p=7,q=9 -> 8", bst, 7, 9, 8);
        // All-equal not allowed (vals unique). Bounds: lower bound value pair.
        check("min/zero bounds p=-1000000000,q=0 -> 0",
                new Integer[]{0, -1000000000, 1000000000}, -1000000000, 0, 0);
        // Both targets in deepest left chain of ref: p=6 only-child path. p=6,q=5 -> 5.
        check("p=6,q=5 -> 5", ref, 6, 5, 5);

        // ----- Large / performance cases (verified against parent-map oracle) -----
        Random rnd = new Random(42);

        // Helper-built random tree near upper bound; pick random distinct p,q.
        // Use unique values via a running counter shuffled by index.
        // Balanced-ish random tree of 100000 nodes.
        {
            int n = 100000;
            TreeNode[] nodes = new TreeNode[n];
            nodes[0] = new TreeNode(0);
            int free = 0; // index of node list traversal for slotting
            // Attach each node i to a random earlier node with a free slot.
            List<TreeNode> open = new ArrayList<>();
            open.add(nodes[0]);
            for (int i = 1; i < n; i++) {
                nodes[i] = new TreeNode(i);
                TreeNode parent = open.get(rnd.nextInt(open.size()));
                if (parent.left == null) parent.left = nodes[i];
                else parent.right = nodes[i];
                if (parent.left != null && parent.right != null) {
                    // remove a full parent from the open list (swap-remove)
                    int idx = open.indexOf(parent);
                    open.set(idx, open.get(open.size() - 1));
                    open.remove(open.size() - 1);
                }
                open.add(nodes[i]);
            }
            TreeNode p = nodes[rnd.nextInt(n)];
            TreeNode q;
            do { q = nodes[rnd.nextInt(n)]; } while (q == p);
            checkBig("random tree n=100000", nodes[0], p, q, 3000);
        }

        // Left-skewed degenerate of 100000 nodes; targets near the two ends.
        {
            int n = 100000;
            TreeNode root = new TreeNode(0);
            TreeNode cur = root;
            TreeNode deep = root;
            for (int i = 1; i < n; i++) { cur.left = new TreeNode(i); cur = cur.left; deep = cur; }
            // p is shallow (root.left), q is deepest -> LCA should be root.left.
            TreeNode p = root.left;
            TreeNode q = deep;
            checkBig("left-skewed n=100000", root, p, q, 3000);
        }

        // Right-skewed degenerate of 100000 nodes; p ancestor of q.
        {
            int n = 100000;
            TreeNode root = new TreeNode(0);
            TreeNode cur = root;
            TreeNode mid = root;
            TreeNode deep = root;
            for (int i = 1; i < n; i++) {
                cur.right = new TreeNode(i);
                cur = cur.right;
                if (i == n / 2) mid = cur;
                deep = cur;
            }
            checkBig("right-skewed n=100000 (ancestor)", root, mid, deep, 3000);
        }

        // Two leaves in opposite subtrees of the root in a balanced tree -> LCA is root.
        {
            int n = 100000;
            TreeNode[] nodes = new TreeNode[n];
            for (int i = 0; i < n; i++) nodes[i] = new TreeNode(i);
            for (int i = 0; i < n; i++) {
                int l = 2 * i + 1, r = 2 * i + 2;
                if (l < n) nodes[i].left = nodes[l];
                if (r < n) nodes[i].right = nodes[r];
            }
            // deepest node in left subtree and deepest in right subtree.
            TreeNode p = nodes[1];
            while (p.right != null) p = p.right;
            TreeNode q = nodes[2];
            while (q.left != null) q = q.left;
            checkBig("balanced n=100000 cross-subtree (LCA=root)", nodes[0], p, q, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
