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

    static List<Integer> list(int... vs) {
        List<Integer> out = new ArrayList<>();
        for (int v : vs) out.add(v);
        return out;
    }

    // Independent oracle: inorder = Left -> Node -> Right (iterative).
    static List<Integer> oracle(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> st = new ArrayDeque<>();
        TreeNode cur = root;
        while (cur != null || !st.isEmpty()) {
            while (cur != null) { st.push(cur); cur = cur.left; }
            cur = st.pop();
            out.add(cur.val);
            cur = cur.right;
        }
        return out;
    }

    static void check(String name, Integer[] arr, List<Integer> expected) {
        total++;
        try {
            List<Integer> actual = new Answer().inorderTraversal(build(arr));
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

    // Property check for big trees: compare against the independent oracle.
    static void checkBig(String name, TreeNode root, long budgetMs) {
        total++;
        try {
            List<Integer> expected = oracle(root);
            long t0 = System.nanoTime();
            List<Integer> actual = new Answer().inorderTraversal(root);
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
                        + "\n     expected size=" + expected.size()
                        + "\n       actual size=" + (actual == null ? "null" : actual.size()));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }
    }

    public static void main(String[] args) {
        // Inorder = Left -> Node -> Right

        // ----- Existing cases (kept) -----
        // Example 1: [1,null,2,3] -> [1,3,2]
        check("example: [1,null,2,3]", new Integer[]{1, null, 2, 3}, list(1, 3, 2));

        // Example 2: empty -> []
        check("empty []", new Integer[]{}, list());
        check("null root", new Integer[]{null}, list());

        // Example 3: BST [4,2,6,1,3,5,7] -> sorted [1,2,3,4,5,6,7]
        check("BST example: [4,2,6,1,3,5,7]",
                new Integer[]{4, 2, 6, 1, 3, 5, 7}, list(1, 2, 3, 4, 5, 6, 7));

        // Single node.
        check("single [42]", new Integer[]{42}, list(42));

        // Left-skewed [1,2,null,3] -> [3,2,1]
        check("left-skewed [1,2,null,3]", new Integer[]{1, 2, null, 3}, list(3, 2, 1));

        // Right-skewed [1,null,2,null,3] -> [1,2,3]
        check("right-skewed [1,null,2,null,3]",
                new Integer[]{1, null, 2, null, 3}, list(1, 2, 3));

        // Perfect tree [1,2,3,4,5,6,7] -> inorder [4,2,5,1,6,3,7]
        check("perfect [1,2,3,4,5,6,7]",
                new Integer[]{1, 2, 3, 4, 5, 6, 7}, list(4, 2, 5, 1, 6, 3, 7));

        // Negatives and zero.
        check("negatives [0,-1,-2]", new Integer[]{0, -1, -2}, list(-1, 0, -2));

        // Duplicates.
        check("duplicates [7,7,7]", new Integer[]{7, 7, 7}, list(7, 7, 7));

        // Min/max bounds.
        check("bounds [-100,100,-100]",
                new Integer[]{-100, 100, -100}, list(100, -100, -100));

        // ----- New corner cases -----
        // Two nodes left only: 1.left=2 -> inorder [2,1]
        check("two nodes left [1,2]", new Integer[]{1, 2}, list(2, 1));
        // Two nodes right only: 1.right=2 -> inorder [1,2]
        check("two nodes right [1,null,2]", new Integer[]{1, null, 2}, list(1, 2));
        // All-equal complete tree.
        check("all-equal [9,9,9,9,9,9,9]", new Integer[]{9, 9, 9, 9, 9, 9, 9},
                list(9, 9, 9, 9, 9, 9, 9));
        // All-negative perfect tree -> inorder [-4,-2,-5,-1,-6,-3,-7]
        check("all-negative [-1,-2,-3,-4,-5,-6,-7]",
                new Integer[]{-1, -2, -3, -4, -5, -6, -7},
                list(-4, -2, -5, -1, -6, -3, -7));
        // Zeros.
        check("zeros [0,0,0,0,0]", new Integer[]{0, 0, 0, 0, 0}, list(0, 0, 0, 0, 0));
        // Min bound single.
        check("min single [-100]", new Integer[]{-100}, list(-100));
        // Max bound single.
        check("max single [100]", new Integer[]{100}, list(100));
        // BST as a sorted ascending check: build [2,1,3] -> inorder [1,2,3]
        check("BST [2,1,3]", new Integer[]{2, 1, 3}, list(1, 2, 3));
        // Larger BST -> inorder must be strictly sorted [1..7].
        check("BST [4,2,6,1,3,5,7] sorted",
                new Integer[]{4, 2, 6, 1, 3, 5, 7}, list(1, 2, 3, 4, 5, 6, 7));
        // Left zigzag: [1,2,null,null,3,4]
        // 1.left=2; 2.right=3; 3.left=4 -> inorder: L(2 subtree) then 1.
        //  inorder(2): left null -> 2 -> right(3): left(4)->4, 3 -> 2-subtree = [2,4,3]
        //  whole: [2,4,3,1]
        check("zigzag [1,2,null,null,3,4]", new Integer[]{1, 2, null, null, 3, 4},
                list(2, 4, 3, 1));
        // Mixed bounds perfect tree.
        // [0,-100,100,-50,50,-25,25] -> inorder [-50,-100,50,0,-25,100,25]
        check("mixed bounds [0,-100,100,-50,50,-25,25]",
                new Integer[]{0, -100, 100, -50, 50, -25, 25},
                list(-50, -100, 50, 0, -25, 100, 25));

        // ----- Large / performance cases (verified against oracle) -----
        Random rnd = new Random(42);

        // Complete tree at node-count upper bound (100).
        {
            int n = 100;
            Integer[] a = new Integer[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(201) - 100;
            checkBig("complete n=100 (random vals)", build(a), 3000);
        }
        // Left-skewed degenerate of 100 nodes (deep recursion stress).
        {
            int n = 100;
            TreeNode root = new TreeNode(rnd.nextInt(201) - 100);
            TreeNode cur = root;
            for (int i = 1; i < n; i++) { cur.left = new TreeNode(rnd.nextInt(201) - 100); cur = cur.left; }
            checkBig("left-skewed n=100", root, 3000);
        }
        // Right-skewed degenerate of 100 nodes.
        {
            int n = 100;
            TreeNode root = new TreeNode(rnd.nextInt(201) - 100);
            TreeNode cur = root;
            for (int i = 1; i < n; i++) { cur.right = new TreeNode(rnd.nextInt(201) - 100); cur = cur.right; }
            checkBig("right-skewed n=100", root, 3000);
        }
        // Sorted-insert BST of 100 unique keys -> inorder must be ascending; verified by oracle.
        {
            int n = 100;
            int[] keys = new int[n];
            for (int i = 0; i < n; i++) keys[i] = i - 50;
            // shuffle insertion order
            for (int i = n - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int t = keys[i]; keys[i] = keys[j]; keys[j] = t; }
            TreeNode root = null;
            for (int k : keys) root = bstInsert(root, k);
            checkBig("BST n=100 (sorted property)", root, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static TreeNode bstInsert(TreeNode root, int key) {
        if (root == null) return new TreeNode(key);
        TreeNode cur = root;
        while (true) {
            if (key < cur.val) {
                if (cur.left == null) { cur.left = new TreeNode(key); return root; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = new TreeNode(key); return root; }
                cur = cur.right;
            }
        }
    }
}
