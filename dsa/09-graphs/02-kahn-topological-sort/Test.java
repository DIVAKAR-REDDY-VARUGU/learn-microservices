import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    // Validate that 'order' is a valid topological ordering of all numCourses nodes.
    static boolean isValidTopo(int numCourses, int[][] prereqs, int[] order) {
        if (order == null) return false;
        if (order.length != numCourses) return false;
        boolean[] seen = new boolean[numCourses];
        int[] position = new int[numCourses];
        Arrays.fill(position, -1);
        for (int i = 0; i < order.length; i++) {
            int c = order[i];
            if (c < 0 || c >= numCourses) return false;
            if (seen[c]) return false; // duplicate
            seen[c] = true;
            position[c] = i;
        }
        for (boolean s : seen) if (!s) return false; // all present
        // prereq [a,b]: b must come before a
        for (int[] p : prereqs) {
            int a = p[0], b = p[1];
            if (position[b] > position[a]) return false;
        }
        return true;
    }

    static void checkValid(String name, int numCourses, int[][] prereqs) {
        total++;
        try {
            int[] actual = new Answer().findOrder(numCourses, deepCopy(prereqs));
            if (isValidTopo(numCourses, prereqs, actual)) { pass++; System.out.println("\033[32m[PASS]\033[0m " + name + " | order=" + Arrays.toString(actual)); }
            else System.out.println("\033[31m[FAIL]\033[0m " + name + " | not a valid topo order. got=" + Arrays.toString(actual) + " prereqs=" + Arrays.deepToString(prereqs));
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkEmpty(String name, int numCourses, int[][] prereqs) {
        total++;
        try {
            int[] actual = new Answer().findOrder(numCourses, deepCopy(prereqs));
            if (actual != null && actual.length == 0) { pass++; System.out.println("\033[32m[PASS]\033[0m " + name); }
            else System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected empty array (cycle), got=" + Arrays.toString(actual));
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Large helper: validate topo order (or empty for cyclic) and time it.
    static void checkValidLarge(String name, int numCourses, int[][] prereqs, long budgetMs) {
        total++;
        try {
            long t0 = System.nanoTime();
            int[] actual = new Answer().findOrder(numCourses, deepCopy(prereqs));
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lbl = name + " | n=" + numCourses + " edges=" + prereqs.length + " elapsed=" + elapsedMs + "ms";
            if (!isValidTopo(numCourses, prereqs, actual)) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | not a valid topo order (got len=" + (actual == null ? "null" : actual.length) + ")");
            } else if (elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | exceeded budget " + budgetMs + "ms");
            } else {
                pass++; System.out.println("\033[32m[PASS]\033[0m " + lbl);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkEmptyLarge(String name, int numCourses, int[][] prereqs, long budgetMs) {
        total++;
        try {
            long t0 = System.nanoTime();
            int[] actual = new Answer().findOrder(numCourses, deepCopy(prereqs));
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lbl = name + " | n=" + numCourses + " edges=" + prereqs.length + " elapsed=" + elapsedMs + "ms";
            if (actual == null || actual.length != 0) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | expected empty (cycle), got len=" + (actual == null ? "null" : actual.length));
            } else if (elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | exceeded budget " + budgetMs + "ms");
            } else {
                pass++; System.out.println("\033[32m[PASS]\033[0m " + lbl);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static int[][] deepCopy(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = Arrays.copyOf(a[i], a[i].length);
        return c;
    }

    public static void main(String[] args) {
        // Example 1
        checkValid("example1_two_courses", 2, new int[][]{{1,0}});
        // Example 2
        checkValid("example2_four_courses", 4, new int[][]{{1,0},{2,0},{3,1},{3,2}});
        // Example 3 - cycle
        checkEmpty("example3_cycle", 2, new int[][]{{1,0},{0,1}});

        // Single course, no prereqs
        checkValid("single_course_no_prereq", 1, new int[][]{});
        // Multiple independent courses, no prereqs
        checkValid("independent_no_prereqs", 5, new int[][]{});
        // Linear chain
        checkValid("linear_chain", 4, new int[][]{{1,0},{2,1},{3,2}});
        // Self loop -> cycle
        checkEmpty("self_loop_cycle", 3, new int[][]{{0,0}});
        // Three node cycle
        checkEmpty("three_node_cycle", 3, new int[][]{{0,1},{1,2},{2,0}});
        // Diamond DAG
        checkValid("diamond_dag", 4, new int[][]{{1,0},{2,0},{3,1},{3,2}});
        // Larger DAG
        checkValid("larger_dag", 6, new int[][]{{2,0},{2,1},{3,2},{4,2},{5,3},{5,4}});
        // Disconnected components both DAGs
        checkValid("disconnected_dags", 6, new int[][]{{1,0},{3,2},{5,4}});
        // Cycle hidden in larger graph
        checkEmpty("hidden_cycle", 5, new int[][]{{1,0},{2,1},{3,2},{1,3}});
        // Two courses reverse direction
        checkValid("reverse_dependency", 2, new int[][]{{0,1}});
        // Tree shaped (one root many children)
        checkValid("tree_one_root", 4, new int[][]{{1,0},{2,0},{3,0}});
        // Many nodes no edges (all in-degree 0)
        checkValid("ten_nodes_no_edges", 10, new int[][]{});

        // ===== ADDED CORNER CASES =====
        // Boundary: minimum numCourses = 1 with no edges
        checkValid("min_one_course", 1, new int[][]{});
        // Long single linear chain 0->1->...->19 (strictly increasing dependency)
        {
            int n = 20;
            int[][] p = new int[n - 1][2];
            for (int i = 1; i < n; i++) p[i - 1] = new int[]{i, i - 1};
            checkValid("long_linear_chain_20", n, p);
        }
        // Reverse linear chain (strictly decreasing labels)
        {
            int n = 20;
            int[][] p = new int[n - 1][2];
            for (int i = 0; i < n - 1; i++) p[i] = new int[]{i, i + 1};
            checkValid("reverse_linear_chain_20", n, p);
        }
        // Complete DAG: every i depends on every j<i (max edges, dense, unique order)
        {
            int n = 8;
            List<int[]> e = new ArrayList<>();
            for (int a = 0; a < n; a++) for (int b = 0; b < a; b++) e.add(new int[]{a, b});
            checkValid("complete_dag_8", n, e.toArray(new int[0][]));
        }
        // Two-node cycle hidden in a long otherwise-valid chain at the end
        checkEmpty("chain_with_tail_cycle", 6, new int[][]{{1,0},{2,1},{3,2},{4,5},{5,4}});
        // Large self-loop on a high-index node
        checkEmpty("high_index_self_loop", 50, new int[][]{{49,49}});
        // Bipartite-ish: nodes 0,1,2 are roots; 3,4,5 each depend on all roots
        checkValid("bipartite_layers", 6, new int[][]{{3,0},{3,1},{3,2},{4,0},{4,1},{4,2},{5,0},{5,1},{5,2}});
        // Single node self-loop only (n=1) -> cycle
        checkEmpty("single_node_self_loop", 1, new int[][]{{0,0}});
        // Wide fan-out then fan-in (diamond of width 5)
        checkValid("wide_diamond", 7, new int[][]{{1,0},{2,0},{3,0},{4,0},{5,0},{6,1},{6,2},{6,3},{6,4},{6,5}});
        // Cycle of length 4 among isolated nodes
        checkEmpty("four_cycle_with_isolated", 8, new int[][]{{0,1},{1,2},{2,3},{3,0}});
        // Multiple disconnected chains all valid
        checkValid("three_disconnected_chains", 9, new int[][]{{1,0},{2,1},{4,3},{5,4},{7,6},{8,7}});
        // Two separate cycles -> empty
        checkEmpty("two_disjoint_cycles", 6, new int[][]{{0,1},{1,0},{3,4},{4,3}});
        // Back edge from last to first creating big cycle
        checkEmpty("big_back_edge_cycle", 10, new int[][]{{1,0},{2,1},{3,2},{4,3},{5,4},{6,5},{7,6},{8,7},{9,8},{0,9}});
        // Star where center depends on all leaves
        checkValid("center_depends_on_leaves", 5, new int[][]{{0,1},{0,2},{0,3},{0,4}});
        // Many isolated nodes plus one small DAG
        checkValid("isolated_plus_small_dag", 12, new int[][]{{1,0},{2,1}});

        // ===== LARGE / PERFORMANCE CASES (seed 42, validated by property, timed) =====
        Random rnd = new java.util.Random(42);
        // Large valid DAG: edges only from lower index to higher (guaranteed acyclic). n=2000 (max).
        {
            int n = 2000;
            HashSet<Long> used = new HashSet<>();
            List<int[]> edges = new ArrayList<>();
            int target = 8000;
            int guard = 0;
            while (edges.size() < target && guard++ < target * 5) {
                int b = rnd.nextInt(n);
                int a = rnd.nextInt(n);
                if (a <= b) continue; // ensure a depends on smaller-index b => acyclic
                long key = (long) a * n + b;
                if (used.add(key)) edges.add(new int[]{a, b}); // [a,b]: take b before a
            }
            checkValidLarge("large_dag_n2000_acyclic", n, edges.toArray(new int[0][]), 3000);
        }
        // Large pure linear chain n=2000 (deep dependency, stresses recursion / queue ordering)
        {
            int n = 2000;
            int[][] p = new int[n - 1][2];
            for (int i = 1; i < n; i++) p[i - 1] = new int[]{i, i - 1};
            checkValidLarge("large_linear_chain_n2000", n, p, 3000);
        }
        // Large layered DAG: 40 layers of 50 nodes, each node depends on a few in prior layer
        {
            int layers = 40, width = 50, n = layers * width;
            HashSet<Long> used = new HashSet<>();
            List<int[]> edges = new ArrayList<>();
            for (int L = 1; L < layers; L++) {
                for (int w = 0; w < width; w++) {
                    int a = L * width + w;
                    for (int t = 0; t < 3; t++) {
                        int b = (L - 1) * width + rnd.nextInt(width);
                        long key = (long) a * n + b;
                        if (used.add(key)) edges.add(new int[]{a, b});
                    }
                }
            }
            checkValidLarge("large_layered_dag_2000", n, edges.toArray(new int[0][]), 3000);
        }
        // Large graph WITH a cycle inserted -> must return empty. Acyclic base + one back edge.
        {
            int n = 2000;
            List<int[]> edges = new ArrayList<>();
            for (int i = 1; i < n; i++) edges.add(new int[]{i, i - 1}); // chain
            edges.add(new int[]{0, n - 1}); // back edge closes a giant cycle
            checkEmptyLarge("large_chain_with_cycle_n2000", n, edges.toArray(new int[0][]), 3000);
        }
        // Large set of fully independent nodes (all in-degree 0), n=2000, no edges
        {
            checkValidLarge("large_no_edges_n2000", 2000, new int[0][], 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
