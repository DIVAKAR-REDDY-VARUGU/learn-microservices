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

    // Independent oracle: postorder = Left -> Right -> Node (iterative two-stack).
    static List<Integer> oracle(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> s1 = new ArrayDeque<>();
        Deque<TreeNode> s2 = new ArrayDeque<>();
        s1.push(root);
        while (!s1.isEmpty()) {
            TreeNode n = s1.pop();
            s2.push(n);
            if (n.left != null) s1.push(n.left);
            if (n.right != null) s1.push(n.right);
        }
        while (!s2.isEmpty()) out.add(s2.pop().val);
        return out;
    }

    static void check(String name, Integer[] arr, List<Integer> expected) {
        total++;
        try {
            List<Integer> actual = new Answer().postorderTraversal(build(arr));
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
            List<Integer> actual = new Answer().postorderTraversal(root);
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
        // Postorder = Left -> Right -> Node

        // ----- Existing cases (kept) -----
        // Example 1: [1,null,2,3] -> [3,2,1]
        check("example: [1,null,2,3]", new Integer[]{1, null, 2, 3}, list(3, 2, 1));

        // Example 2: empty -> []
        check("empty []", new Integer[]{}, list());
        check("null root", new Integer[]{null}, list());

        // Example 3: [1,2,3,4,5] -> [4,5,2,3,1]
        check("example: [1,2,3,4,5]", new Integer[]{1, 2, 3, 4, 5}, list(4, 5, 2, 3, 1));

        // Single node.
        check("single [42]", new Integer[]{42}, list(42));

        // Perfect tree [1,2,3,4,5,6,7] -> postorder [4,5,2,6,7,3,1]
        check("perfect [1,2,3,4,5,6,7]",
                new Integer[]{1, 2, 3, 4, 5, 6, 7}, list(4, 5, 2, 6, 7, 3, 1));

        // Left-skewed [1,2,null,3] -> [3,2,1]
        check("left-skewed [1,2,null,3]", new Integer[]{1, 2, null, 3}, list(3, 2, 1));

        // Right-skewed [1,null,2,null,3] -> [3,2,1]
        check("right-skewed [1,null,2,null,3]",
                new Integer[]{1, null, 2, null, 3}, list(3, 2, 1));

        // Negatives and zero.
        check("negatives [0,-1,-2]", new Integer[]{0, -1, -2}, list(-1, -2, 0));

        // Duplicates.
        check("duplicates [7,7,7]", new Integer[]{7, 7, 7}, list(7, 7, 7));

        // Min/max bounds.
        check("bounds [-100,100,-100]",
                new Integer[]{-100, 100, -100}, list(100, -100, -100));

        // ----- New corner cases -----
        // Two nodes left only: 1.left=2 -> [2,1]
        check("two nodes left [1,2]", new Integer[]{1, 2}, list(2, 1));
        // Two nodes right only: 1.right=2 -> [2,1]
        check("two nodes right [1,null,2]", new Integer[]{1, null, 2}, list(2, 1));
        // All-equal complete tree.
        check("all-equal [9,9,9,9,9,9,9]", new Integer[]{9, 9, 9, 9, 9, 9, 9},
                list(9, 9, 9, 9, 9, 9, 9));
        // All-negative perfect tree -> postorder [-4,-5,-2,-6,-7,-3,-1]
        check("all-negative [-1,-2,-3,-4,-5,-6,-7]",
                new Integer[]{-1, -2, -3, -4, -5, -6, -7},
                list(-4, -5, -2, -6, -7, -3, -1));
        // Zeros.
        check("zeros [0,0,0,0,0]", new Integer[]{0, 0, 0, 0, 0}, list(0, 0, 0, 0, 0));
        // Min bound single.
        check("min single [-100]", new Integer[]{-100}, list(-100));
        // Max bound single.
        check("max single [100]", new Integer[]{100}, list(100));
        // Zigzag [1,2,null,null,3,4]: 1.left=2;2.right=3;3.left=4
        // postorder: L of 1 = post(2) = [post(2.left=none), post(2.right=3), 2]
        //   post(3) = [post(4), post(none), 3] = [4,3]; so post(2)=[4,3,2]; whole=[4,3,2,1]
        check("zigzag [1,2,null,null,3,4]", new Integer[]{1, 2, null, null, 3, 4},
                list(4, 3, 2, 1));
        // Root right subtree [1,null,2,3,4]: 1.right=2;2.left=3;2.right=4
        // post(2)=[3,4,2]; whole=[3,4,2,1]
        check("root right subtree [1,null,2,3,4]", new Integer[]{1, null, 2, 3, 4},
                list(3, 4, 2, 1));
        // Mixed bounds perfect tree.
        // [0,-100,100,-50,50,-25,25] -> postorder [-50,50,-100,-25,25,100,0]
        check("mixed bounds [0,-100,100,-50,50,-25,25]",
                new Integer[]{0, -100, 100, -50, 50, -25, 25},
                list(-50, 50, -100, -25, 25, 100, 0));
        // Off-by-one complete tree of 6 nodes [1,2,3,4,5,6]
        // post: post(2)=[4,5,2]; post(3)=[6,3]; whole=[4,5,2,6,3,1]
        check("complete 6 [1,2,3,4,5,6]", new Integer[]{1, 2, 3, 4, 5, 6},
                list(4, 5, 2, 6, 3, 1));

        // ----- Large / performance cases (verified against oracle) -----
        Random rnd = new Random(42);

        // Complete tree at node-count upper bound (100).
        {
            int n = 100;
            Integer[] a = new Integer[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(201) - 100;
            checkBig("complete n=100 (random vals)", build(a), 3000);
        }
        // Left-skewed degenerate of 100 nodes.
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
        // Random-shaped tree of 100 nodes.
        {
            int n = 100;
            List<TreeNode> nodes = new ArrayList<>();
            TreeNode root = new TreeNode(rnd.nextInt(201) - 100);
            nodes.add(root);
            int made = 1;
            while (made < n) {
                TreeNode parent = nodes.get(rnd.nextInt(nodes.size()));
                if (parent.left != null && parent.right != null) continue;
                TreeNode child = new TreeNode(rnd.nextInt(201) - 100);
                if (parent.left == null && (parent.right != null || rnd.nextBoolean())) parent.left = child;
                else if (parent.right == null) parent.right = child;
                else parent.left = child;
                nodes.add(child);
                made++;
            }
            checkBig("random-shaped n=100", root, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
