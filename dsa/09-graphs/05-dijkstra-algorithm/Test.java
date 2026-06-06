import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static int[][] deepCopy(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = Arrays.copyOf(a[i], a[i].length);
        return c;
    }

    static void check(String name, int[][] times, int n, int k, int expected) {
        total++;
        try {
            int actual = new Answer().networkDelayTime(deepCopy(times), n, k);
            if (actual == expected) { pass++; System.out.println("\033[32m[PASS]\033[0m " + name); }
            else System.out.println("\033[31m[FAIL]\033[0m " + name + " | times=" + Arrays.deepToString(times) + " n=" + n + " k=" + k + " expected=" + expected + " actual=" + actual);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent oracle: Bellman-Ford from k over nodes 1..n, answer = max dist or -1 if any unreachable.
    static int oracle(int[][] times, int n, int k) {
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n + 1];
        Arrays.fill(dist, INF);
        dist[k] = 0;
        for (int iter = 0; iter < n - 1; iter++) {
            boolean changed = false;
            for (int[] e : times) {
                int u = e[0], v = e[1], w = e[2];
                if (dist[u] + w < dist[v]) { dist[v] = dist[u] + w; changed = true; }
            }
            if (!changed) break;
        }
        long max = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] >= INF) return -1;
            max = Math.max(max, dist[i]);
        }
        return (int) max;
    }

    static void checkLarge(String name, int[][] times, int n, int k, long budgetMs) {
        total++;
        try {
            int expected = oracle(times, n, k);
            long t0 = System.nanoTime();
            int actual = new Answer().networkDelayTime(deepCopy(times), n, k);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lbl = name + " | n=" + n + " edges=" + times.length + " elapsed=" + elapsedMs + "ms";
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
        check("example1", new int[][]{{2,1,1},{2,3,1},{3,4,1}}, 4, 2, 2);
        // Example 2
        check("example2_direct", new int[][]{{1,2,1}}, 2, 1, 1);
        // Example 3 - unreachable
        check("example3_unreachable", new int[][]{{1,2,1}}, 2, 2, -1);

        // Single node, no edges -> 0 (already received)
        check("single_node_no_edges", new int[][]{}, 1, 1, 0);
        // Two nodes, no path to second
        check("two_nodes_no_edge", new int[][]{}, 2, 1, -1);
        // Self all reachable, pick shortest among multiple paths
        check("shorter_path_wins", new int[][]{{1,2,4},{1,3,1},{3,2,1}}, 3, 1, 2);
        // Zero weight edges
        check("zero_weight_edges", new int[][]{{1,2,0},{2,3,0}}, 3, 1, 0);
        // Linear chain delay accumulates
        check("linear_chain", new int[][]{{1,2,3},{2,3,4},{3,4,5}}, 4, 1, 12);
        // Star from source
        check("star_from_source", new int[][]{{1,2,5},{1,3,2},{1,4,7}}, 4, 1, 7);
        // Disconnected node makes it -1
        check("disconnected_node", new int[][]{{1,2,1},{2,3,1}}, 4, 1, -1);
        // Parallel paths different lengths
        check("parallel_paths", new int[][]{{1,2,1},{2,4,1},{1,3,1},{3,4,10}}, 4, 1, 2);
        // Backward edges should not help forward unreachable
        check("backward_only", new int[][]{{2,1,1},{3,1,1}}, 3, 1, -1);
        // Cycle in graph still computes shortest
        check("cycle_graph", new int[][]{{1,2,1},{2,3,1},{3,1,1}}, 3, 1, 2);
        // k is a middle node
        check("source_middle", new int[][]{{2,1,2},{2,3,3}}, 3, 2, 3);
        // Larger graph
        check("larger_graph", new int[][]{{1,2,2},{1,3,5},{2,3,1},{3,4,2},{2,4,7}}, 4, 1, 5);

        // ===== ADDED CORNER CASES =====
        // Boundary: max weight 100 on a single edge
        check("max_weight_edge", new int[][]{{1,2,100}}, 2, 1, 100);
        // All zero weights, full reachability -> 0
        check("all_zero_full", new int[][]{{1,2,0},{1,3,0},{1,4,0}}, 4, 1, 0);
        // Two parallel paths, relaxation must pick the cheaper multi-hop
        check("relax_multi_hop", new int[][]{{1,2,5},{1,3,1},{3,2,1},{2,4,1}}, 4, 1, 3);
        // Long chain near boundary weights
        check("long_chain_heavy", new int[][]{{1,2,100},{2,3,100},{3,4,100}}, 4, 1, 300);
        // Source unreachable from itself? No - source dist 0, but one node isolated
        check("one_isolated_among_reachable", new int[][]{{1,2,1},{1,3,1}}, 4, 1, -1);
        // Diamond: two routes to a node, shorter wins
        check("diamond_shorter_wins", new int[][]{{1,2,1},{1,3,4},{2,4,1},{3,4,1}}, 4, 1, 2);
        // Complete-ish small graph, max is the farthest node
        check("farthest_node_decides", new int[][]{{1,2,1},{2,3,1},{3,4,1},{4,5,1}}, 5, 1, 4);
        // k at the end of a chain, only itself reachable -> -1 for others
        check("k_at_chain_end", new int[][]{{1,2,1},{2,3,1}}, 3, 3, -1);
        // Single edge, k equals dst node (source isolated other way) -> -1
        check("k_is_target_only", new int[][]{{1,2,7}}, 2, 2, -1);
        // Many edges into one sink, that sink is farthest
        check("converge_to_sink", new int[][]{{1,2,3},{1,3,3},{2,4,5},{3,4,2}}, 4, 1, 5);
        // Zero-weight shortcut beats positive direct
        check("zero_shortcut", new int[][]{{1,2,10},{1,3,0},{3,2,0}}, 3, 1, 0);
        // Cycle with heavy back edge does not shorten
        check("heavy_back_edge_cycle", new int[][]{{1,2,1},{2,3,1},{3,1,100}}, 3, 1, 2);
        // Two components, k in the larger reachable one but a node isolated -> -1
        check("isolated_extra_node", new int[][]{{1,2,2},{2,3,2}}, 5, 1, -1);
        // Tie between two equal paths
        check("equal_path_tie", new int[][]{{1,2,2},{1,3,2},{2,4,2},{3,4,2}}, 4, 1, 4);
        // Source is highest-index node
        check("source_highest_index", new int[][]{{4,3,1},{3,2,1},{2,1,1}}, 4, 4, 3);

        // ===== LARGE / PERFORMANCE CASES (seed 42, oracle = Bellman-Ford, timed) =====
        Random rnd = new java.util.Random(42);
        // Dense max graph n=100, up to 6000 unique directed edges (constraint max), weights 0..100.
        {
            int n = 100;
            HashSet<Long> used = new HashSet<>();
            List<int[]> edges = new ArrayList<>();
            int target = 6000;
            int guard = 0;
            while (edges.size() < target && guard++ < target * 6) {
                int u = rnd.nextInt(n) + 1, v = rnd.nextInt(n) + 1;
                if (u == v) continue;
                long key = (long) u * (n + 1) + v;
                if (used.add(key)) edges.add(new int[]{u, v, rnd.nextInt(101)});
            }
            checkLarge("large_dense_n100_6000edges", edges.toArray(new int[0][]), 100, 1, 3000);
        }
        // Large linear chain n=100, all reachable from node 1, weights 1..100.
        {
            int n = 100;
            int[][] e = new int[n - 1][3];
            for (int i = 1; i < n; i++) e[i - 1] = new int[]{i, i + 1, 1 + rnd.nextInt(100)};
            checkLarge("large_chain_n100", e, n, 1, 3000);
        }
        // Large star: node 1 -> every other node, weights vary. Max is the heaviest spoke.
        {
            int n = 100;
            int[][] e = new int[n - 1][3];
            for (int v = 2; v <= n; v++) e[v - 2] = new int[]{1, v, 1 + rnd.nextInt(100)};
            checkLarge("large_star_n100", e, n, 1, 3000);
        }
        // Large graph with an unreachable node (node n has no incoming) -> oracle yields -1.
        {
            int n = 100;
            List<int[]> edges = new ArrayList<>();
            for (int i = 1; i < n - 1; i++) edges.add(new int[]{i, i + 1, 1 + rnd.nextInt(100)});
            // node n (index 100) intentionally left with no incoming edge
            checkLarge("large_unreachable_node_n100", edges.toArray(new int[0][]), 100, 1, 3000);
        }
        // Large all-zero-weight dense-ish graph (full reachability => answer 0).
        {
            int n = 100;
            List<int[]> edges = new ArrayList<>();
            for (int v = 2; v <= n; v++) edges.add(new int[]{1, v, 0});
            for (int t = 0; t < 2000; t++) {
                int u = rnd.nextInt(n) + 1, v = rnd.nextInt(n) + 1;
                if (u != v) edges.add(new int[]{u, v, 0});
            }
            // dedupe to satisfy uniqueness for oracle stability (oracle is robust to dups anyway)
            checkLarge("large_all_zero_full_n100", edges.toArray(new int[0][]), 100, 1, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
