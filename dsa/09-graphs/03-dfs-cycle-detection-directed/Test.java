import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static int[][] deepCopy(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = Arrays.copyOf(a[i], a[i].length);
        return c;
    }

    static void check(String name, int numCourses, int[][] prereqs, boolean expected) {
        total++;
        try {
            boolean actual = new Answer().canFinish(numCourses, deepCopy(prereqs));
            if (actual == expected) { pass++; System.out.println("\033[32m[PASS]\033[0m " + name); }
            else System.out.println("\033[31m[FAIL]\033[0m " + name + " | n=" + numCourses + " prereqs=" + Arrays.deepToString(prereqs) + " expected=" + expected + " actual=" + actual);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent oracle: Kahn's algorithm to detect acyclicity (true if no cycle).
    static boolean oracleCanFinish(int numCourses, int[][] prereqs) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        int[] indeg = new int[numCourses];
        for (int[] p : prereqs) { adj.get(p[1]).add(p[0]); indeg[p[0]]++; }
        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) if (indeg[i] == 0) q.add(i);
        int done = 0;
        while (!q.isEmpty()) {
            int u = q.poll(); done++;
            for (int v : adj.get(u)) if (--indeg[v] == 0) q.add(v);
        }
        return done == numCourses;
    }

    static void checkLarge(String name, int numCourses, int[][] prereqs, long budgetMs) {
        total++;
        try {
            boolean expected = oracleCanFinish(numCourses, prereqs);
            long t0 = System.nanoTime();
            boolean actual = new Answer().canFinish(numCourses, deepCopy(prereqs));
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lbl = name + " | n=" + numCourses + " edges=" + prereqs.length + " elapsed=" + elapsedMs + "ms";
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | expected=" + expected + " actual=" + actual);
            } else if (elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | exceeded budget " + budgetMs + "ms");
            } else {
                pass++; System.out.println("\033[32m[PASS]\033[0m " + lbl);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    public static void main(String[] args) {
        // Example 1
        check("example1_simple", 2, new int[][]{{1,0}}, true);
        // Example 2 - mutual cycle
        check("example2_cycle", 2, new int[][]{{1,0},{0,1}}, false);
        // Example 3 - chain
        check("example3_chain", 3, new int[][]{{1,0},{2,1}}, true);

        // No prerequisites
        check("no_prereqs", 1, new int[][]{}, true);
        check("many_no_prereqs", 5, new int[][]{}, true);
        // Self loop is a cycle
        check("self_loop", 1, new int[][]{{0,0}}, false);
        // Three node cycle
        check("three_node_cycle", 3, new int[][]{{0,1},{1,2},{2,0}}, false);
        // Diamond DAG -> finishable
        check("diamond_dag", 4, new int[][]{{1,0},{2,0},{3,1},{3,2}}, true);
        // Linear chain longer
        check("long_chain", 5, new int[][]{{1,0},{2,1},{3,2},{4,3}}, true);
        // Cycle hidden among many edges
        check("hidden_cycle", 5, new int[][]{{1,0},{2,1},{3,2},{1,3}}, false);
        // Disconnected: one DAG and one cycle
        check("disconnected_with_cycle", 6, new int[][]{{1,0},{3,2},{2,3}}, false);
        // Disconnected all DAGs
        check("disconnected_all_dags", 6, new int[][]{{1,0},{3,2},{5,4}}, true);
        // Tree shaped
        check("tree_shaped", 4, new int[][]{{1,0},{2,0},{3,0}}, true);
        // Two-node, reverse direction, no cycle
        check("reverse_no_cycle", 2, new int[][]{{0,1}}, true);
        // Large cycle of 4
        check("four_node_cycle", 4, new int[][]{{0,1},{1,2},{2,3},{3,0}}, false);
        // Many nodes no edges
        check("ten_nodes_no_edges", 10, new int[][]{}, true);

        // ===== ADDED CORNER CASES =====
        // Boundary minimum n=1, no edges
        check("min_single_no_edge", 1, new int[][]{}, true);
        // Self-loop on high index node among many
        check("self_loop_high_index", 30, new int[][]{{29,29}}, false);
        // Complete DAG: i depends on all j<i (max dense acyclic)
        {
            int n = 8;
            List<int[]> e = new ArrayList<>();
            for (int a = 0; a < n; a++) for (int b = 0; b < a; b++) e.add(new int[]{a, b});
            check("complete_dag_8", n, e.toArray(new int[0][]), true);
        }
        // Two-cycle hidden at tail of long valid chain
        check("chain_tail_cycle", 6, new int[][]{{1,0},{2,1},{3,2},{4,5},{5,4}}, false);
        // Big back-edge closes entire chain into cycle
        check("big_back_edge", 8, new int[][]{{1,0},{2,1},{3,2},{4,3},{5,4},{6,5},{7,6},{0,7}}, false);
        // Two disjoint cycles
        check("two_disjoint_cycles", 6, new int[][]{{0,1},{1,0},{3,4},{4,3}}, false);
        // Wide diamond width 5, no cycle
        check("wide_diamond", 7, new int[][]{{1,0},{2,0},{3,0},{4,0},{5,0},{6,1},{6,2},{6,3},{6,4},{6,5}}, true);
        // Cross dependencies but acyclic (two roots two sinks)
        check("cross_acyclic", 4, new int[][]{{2,0},{2,1},{3,0},{3,1}}, true);
        // Almost-cycle: A->B->C and A->C (DAG, shared sink)
        check("shared_sink_dag", 3, new int[][]{{1,0},{2,1},{2,0}}, true);
        // Long chain n=15 strictly valid
        {
            int n = 15;
            int[][] p = new int[n - 1][2];
            for (int i = 1; i < n; i++) p[i - 1] = new int[]{i, i - 1};
            check("long_chain_15", n, p, true);
        }
        // Cycle only between two of many isolated nodes
        check("isolated_plus_one_cycle", 10, new int[][]{{0,1},{1,0}}, false);
        // Multiple components, last one cyclic
        check("multi_component_last_cyclic", 9, new int[][]{{1,0},{4,3},{6,7},{7,8},{8,6}}, false);
        // Fan-in to single sink (all valid)
        check("fan_in_sink", 6, new int[][]{{5,0},{5,1},{5,2},{5,3},{5,4}}, true);
        // Fan-out from single root (all valid)
        check("fan_out_root", 6, new int[][]{{1,0},{2,0},{3,0},{4,0},{5,0}}, true);

        // ===== LARGE / PERFORMANCE CASES (seed 42, oracle = Kahn, timed) =====
        Random rnd = new java.util.Random(42);
        // Large acyclic random graph: edges always low->high index. n=2000, ~5000 edges (max edges constraint).
        {
            int n = 2000;
            HashSet<Long> used = new HashSet<>();
            List<int[]> edges = new ArrayList<>();
            int guard = 0;
            while (edges.size() < 5000 && guard++ < 50000) {
                int a = rnd.nextInt(n), b = rnd.nextInt(n);
                if (a <= b) continue;
                long key = (long) a * n + b;
                if (used.add(key)) edges.add(new int[]{a, b});
            }
            checkLarge("large_acyclic_n2000", n, edges.toArray(new int[0][]), 3000);
        }
        // Large graph WITH cycle: acyclic base + one back edge.
        {
            int n = 2000;
            List<int[]> edges = new ArrayList<>();
            for (int i = 1; i < n; i++) edges.add(new int[]{i, i - 1});
            edges.add(new int[]{0, n - 1});
            checkLarge("large_cyclic_chain_n2000", n, edges.toArray(new int[0][]), 3000);
        }
        // Large pure linear chain (deep) n=2000, finishable.
        {
            int n = 2000;
            int[][] p = new int[n - 1][2];
            for (int i = 1; i < n; i++) p[i - 1] = new int[]{i, i - 1};
            checkLarge("large_linear_chain_n2000", n, p, 3000);
        }
        // Large layered DAG (40x50) finishable.
        {
            int layers = 40, width = 50, n = layers * width;
            HashSet<Long> used = new HashSet<>();
            List<int[]> edges = new ArrayList<>();
            for (int L = 1; L < layers; L++)
                for (int w = 0; w < width; w++) {
                    int a = L * width + w;
                    for (int t = 0; t < 2; t++) {
                        int b = (L - 1) * width + rnd.nextInt(width);
                        long key = (long) a * n + b;
                        if (used.add(key)) edges.add(new int[]{a, b});
                    }
                }
            checkLarge("large_layered_dag_2000", n, edges.toArray(new int[0][]), 3000);
        }
        // Large isolated nodes, no edges, n=2000.
        checkLarge("large_no_edges_n2000", 2000, new int[0][], 3000);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
