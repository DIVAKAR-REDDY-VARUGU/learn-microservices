import java.util.*;
import java.lang.reflect.Method;

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

    // Iterative structural equality (same shape and same vals) — safe for deep trees.
    // ArrayDeque cannot hold null, so we pair nodes inside a TreeNode[] holder.
    static boolean sameTree(TreeNode a, TreeNode b) {
        Deque<TreeNode[]> work = new ArrayDeque<>();
        work.push(new TreeNode[]{a, b});
        while (!work.isEmpty()) {
            TreeNode[] pr = work.pop();
            TreeNode x = pr[0], y = pr[1];
            if (x == null && y == null) continue;
            if (x == null || y == null) return false;
            if (x.val != y.val) return false;
            work.push(new TreeNode[]{x.left, y.left});
            work.push(new TreeNode[]{x.right, y.right});
        }
        return true;
    }

    // Canonical string for printing a tree (level-order with nulls trimmed).
    static String show(TreeNode root) {
        if (root == null) return "[]";
        List<String> out = new ArrayList<>();
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            TreeNode n = q.poll();
            if (n == null) { out.add("null"); continue; }
            out.add(String.valueOf(n.val));
            q.add(n.left);
            q.add(n.right);
        }
        int end = out.size();
        while (end > 0 && out.get(end - 1).equals("null")) end--;
        return out.subList(0, end).toString();
    }

    static int countNodes(TreeNode root) {
        int c = 0;
        Deque<TreeNode> st = new ArrayDeque<>();
        if (root != null) st.push(root);
        while (!st.isEmpty()) {
            TreeNode n = st.pop();
            c++;
            if (n.left != null) st.push(n.left);
            if (n.right != null) st.push(n.right);
        }
        return c;
    }

    // Call deserialize reflectively so this file compiles even against a stub
    // Answer.java that only declares serialize(); a missing/unimplemented method
    // surfaces as a clean FAIL via the catch below.
    static TreeNode deserialize(Answer codec, String data) throws Throwable {
        Method m;
        try {
            m = Answer.class.getMethod("deserialize", String.class);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Answer has no deserialize(String) method");
        }
        try {
            return (TreeNode) m.invoke(codec, data);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw e.getCause() == null ? e : e.getCause();
        }
    }

    static void check(String name, Integer[] arr) {
        check(name, build(arr), arr == null ? "[]" : show(build(arr)));
    }

    static void check(String name, TreeNode original, String inputDesc) {
        total++;
        try {
            Answer codec = new Answer();
            String data = codec.serialize(original);
            TreeNode roundTrip = deserialize(codec, data);
            if (sameTree(original, roundTrip)) {
                pass++;
                String s = data == null ? "null" : (data.length() > 60 ? data.substring(0, 60) + "..." : data);
                System.out.println("\033[32m[PASS]\033[0m " + name + "  (serialized=\"" + s + "\")");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + "\n        input=" + inputDesc
                        + "\n   serialized=\"" + data + "\""
                        + "\n     expected=" + inputDesc
                        + "\n       actual=" + show(roundTrip));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage()
                    + "\n        input=" + inputDesc);
        }
    }

    // Big-tree round-trip property check with timing.
    static void checkBig(String name, TreeNode original, long budgetMs) {
        total++;
        try {
            Answer codec = new Answer();
            long t0 = System.nanoTime();
            String data = codec.serialize(original);
            TreeNode roundTrip = deserialize(codec, data);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1_000_000.0;
            String label = name + String.format(" n=%d (%.1f ms)", countNodes(original), ms);
            boolean ok = sameTree(original, roundTrip);
            if (ok && ms > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " -> exceeded budget " + budgetMs + " ms");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " -> round-trip mismatch");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    public static void main(String[] args) {
        // Round-trip property: deserialize(serialize(root)) must equal root.

        // ----- Existing cases (kept) -----
        // Example 1: [1,2,3,null,null,4,5]
        check("example: [1,2,3,null,null,4,5]",
                new Integer[]{1, 2, 3, null, null, 4, 5});

        // Example 2: empty tree.
        check("empty []", new Integer[]{});
        check("null root", new Integer[]{null});

        // Example 3: [1,2]
        check("example: [1,2]", new Integer[]{1, 2});

        // Single node.
        check("single [42]", new Integer[]{42});

        // Perfect tree.
        check("perfect [1,2,3,4,5,6,7]", new Integer[]{1, 2, 3, 4, 5, 6, 7});

        // Left-skewed.
        check("left-skewed [1,2,null,3,null,4]",
                new Integer[]{1, 2, null, 3, null, 4});

        // Right-skewed.
        check("right-skewed [1,null,2,null,3]",
                new Integer[]{1, null, 2, null, 3});

        // Negatives and zero.
        check("negatives [0,-1,-2,-3,null,null,-4]",
                new Integer[]{0, -1, -2, -3, null, null, -4});

        // Duplicate values (structure still must round-trip exactly).
        check("duplicates [5,5,5,null,5]", new Integer[]{5, 5, 5, null, 5});

        // Min/max bound values.
        check("bounds [-1000,1000,-1000,1000]",
                new Integer[]{-1000, 1000, -1000, 1000});

        // Asymmetric shape to stress null handling.
        check("asymmetric [5,4,7,3,null,2,null,-1,null,9]",
                new Integer[]{5, 4, 7, 3, null, 2, null, -1, null, 9});

        // ----- New corner cases -----
        // Two-node right child only.
        check("two-node right [1,null,2]", new Integer[]{1, null, 2});
        // Single min-bound value.
        check("single min [-1000]", new Integer[]{-1000});
        // Single max-bound value.
        check("single max [1000]", new Integer[]{1000});
        // Single zero.
        check("single zero [0]", new Integer[]{0});
        // All-equal complete tree (heavy duplicates).
        check("all-equal [7,7,7,7,7,7,7]", new Integer[]{7, 7, 7, 7, 7, 7, 7});
        // All-negative perfect tree.
        check("all-negative [-1,-2,-3,-4,-5,-6,-7]",
                new Integer[]{-1, -2, -3, -4, -5, -6, -7});
        // Zeros everywhere.
        check("zeros [0,0,0,0,0]", new Integer[]{0, 0, 0, 0, 0});
        // Internal null gaps mid-tree.
        check("gappy [1,2,3,null,4,null,5]", new Integer[]{1, 2, 3, null, 4, null, 5});
        // Bounds at every node.
        check("bounds spread [0,-1000,1000,-1000,1000,-1000,1000]",
                new Integer[]{0, -1000, 1000, -1000, 1000, -1000, 1000});
        // Left-leaning zigzag with values that could collide with delimiters
        // (negatives, commas in serialized form must be handled).
        check("zigzag negatives [-5,-4,null,null,-3,-2]",
                new Integer[]{-5, -4, null, null, -3, -2});
        // Long single right spine (deep, narrow).
        check("right spine [1,null,2,null,3,null,4,null,5]",
                new Integer[]{1, null, 2, null, 3, null, 4, null, 5});
        // Mixed: root with only left subtree several levels deep.
        check("left only deep [1,2,null,3,4]", new Integer[]{1, 2, null, 3, 4});

        // ----- Large / performance cases (round-trip property, timed) -----
        Random rnd = new Random(42);

        // Random-shaped tree near node-count upper bound (10^4).
        {
            int n = 10000;
            TreeNode[] nodes = new TreeNode[n];
            nodes[0] = new TreeNode(rnd.nextInt(2001) - 1000);
            List<TreeNode> open = new ArrayList<>();
            open.add(nodes[0]);
            for (int i = 1; i < n; i++) {
                nodes[i] = new TreeNode(rnd.nextInt(2001) - 1000);
                TreeNode parent = open.get(rnd.nextInt(open.size()));
                if (parent.left == null && (parent.right != null || rnd.nextBoolean())) parent.left = nodes[i];
                else if (parent.right == null) parent.right = nodes[i];
                else parent.left = nodes[i];
                if (parent.left != null && parent.right != null) {
                    int idx = open.indexOf(parent);
                    open.set(idx, open.get(open.size() - 1));
                    open.remove(open.size() - 1);
                }
                open.add(nodes[i]);
            }
            checkBig("random-shaped", nodes[0], 3000);
        }

        // Perfect/complete tree of 10^4 nodes (array-mapped).
        {
            int n = 10000;
            TreeNode[] nodes = new TreeNode[n];
            for (int i = 0; i < n; i++) nodes[i] = new TreeNode(rnd.nextInt(2001) - 1000);
            for (int i = 0; i < n; i++) {
                int l = 2 * i + 1, r = 2 * i + 2;
                if (l < n) nodes[i].left = nodes[l];
                if (r < n) nodes[i].right = nodes[r];
            }
            checkBig("complete tree", nodes[0], 3000);
        }

        // Left-skewed degenerate (depth 2000 — still a real skew stress, but fits the canonical
        // recursive solution within the default JVM stack; iterative impls handle much deeper).
        {
            int n = 2000;
            TreeNode root = new TreeNode(rnd.nextInt(2001) - 1000);
            TreeNode cur = root;
            for (int i = 1; i < n; i++) { cur.left = new TreeNode(rnd.nextInt(2001) - 1000); cur = cur.left; }
            checkBig("left-skewed", root, 3000);
        }

        // Right-skewed degenerate (depth 2000 — fits the recursive solution's stack).
        {
            int n = 2000;
            TreeNode root = new TreeNode(rnd.nextInt(2001) - 1000);
            TreeNode cur = root;
            for (int i = 1; i < n; i++) { cur.right = new TreeNode(rnd.nextInt(2001) - 1000); cur = cur.right; }
            checkBig("right-skewed", root, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
