import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static int[][] deepCopy(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = Arrays.copyOf(a[i], a[i].length);
        return c;
    }

    static void check(String name, int n, int[][] flights, int src, int dst, int k, int expected) {
        total++;
        try {
            int actual = new Answer().findCheapestPrice(n, deepCopy(flights), src, dst, k);
            if (actual == expected) { pass++; System.out.println("\033[32m[PASS]\033[0m " + name); }
            else System.out.println("\033[31m[FAIL]\033[0m " + name + " | n=" + n + " flights=" + Arrays.deepToString(flights) + " src=" + src + " dst=" + dst + " k=" + k + " expected=" + expected + " actual=" + actual);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent oracle: Bellman-Ford bounded to k+1 edges, using a snapshot each round.
    static int oracle(int n, int[][] flights, int src, int dst, int k) {
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        for (int round = 0; round <= k; round++) {
            long[] snap = dist.clone();
            for (int[] f : flights) {
                int u = f[0], v = f[1], w = f[2];
                if (snap[u] != INF && snap[u] + w < dist[v]) dist[v] = snap[u] + w;
            }
        }
        return dist[dst] >= INF ? -1 : (int) dist[dst];
    }

    static void checkLarge(String name, int n, int[][] flights, int src, int dst, int k, long budgetMs) {
        total++;
        try {
            int expected = oracle(n, flights, src, dst, k);
            long t0 = System.nanoTime();
            int actual = new Answer().findCheapestPrice(n, deepCopy(flights), src, dst, k);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lbl = name + " | n=" + n + " edges=" + flights.length + " k=" + k + " elapsed=" + elapsedMs + "ms";
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
        check("example1", 4, new int[][]{{0,1,100},{1,2,100},{2,0,100},{1,3,600},{2,3,200}}, 0, 3, 1, 700);
        // Example 2 - 1 stop cheaper
        check("example2_one_stop", 3, new int[][]{{0,1,100},{1,2,100},{0,2,500}}, 0, 2, 1, 200);
        // Example 3 - 0 stops only direct
        check("example3_zero_stops", 3, new int[][]{{0,1,100},{1,2,100},{0,2,500}}, 0, 2, 0, 500);

        // No flights -> unreachable
        check("no_flights", 3, new int[][]{}, 0, 2, 1, -1);
        // Direct flight exists, k=0
        check("direct_k0", 2, new int[][]{{0,1,50}}, 0, 1, 0, 50);
        // Unreachable dst even with stops
        check("unreachable_dst", 4, new int[][]{{0,1,100},{1,2,100}}, 0, 3, 5, -1);
        // k too small to reach via cheaper multi-hop, no direct -> -1
        check("k_too_small_no_direct", 4, new int[][]{{0,1,100},{1,2,100},{2,3,100}}, 0, 3, 1, -1);
        // Enough stops for multi-hop
        check("enough_stops_multihop", 4, new int[][]{{0,1,100},{1,2,100},{2,3,100}}, 0, 3, 2, 300);
        // Cheaper path uses more stops than allowed, must take pricier short path
        check("limited_stops_pricier", 5, new int[][]{{0,1,10},{1,2,10},{2,3,10},{3,4,10},{0,4,100}}, 0, 4, 2, 100);
        // Plenty of stops -> cheapest multi-hop
        check("plenty_stops_cheapest", 5, new int[][]{{0,1,10},{1,2,10},{2,3,10},{3,4,10},{0,4,100}}, 0, 4, 3, 40);
        // Two routes, choose cheaper within stop limit
        check("two_routes", 4, new int[][]{{0,1,5},{1,3,5},{0,2,2},{2,3,2}}, 0, 3, 1, 4);
        // Large k effectively unlimited
        check("large_k_unlimited", 4, new int[][]{{0,1,1},{1,2,1},{2,3,1}}, 0, 3, 100, 3);
        // Cycle present, should not loop forever
        check("cycle_present", 3, new int[][]{{0,1,100},{1,0,100},{1,2,100}}, 0, 2, 1, 200);
        // Single intermediate option vs direct, pick cheaper allowed
        check("direct_vs_one_hop", 3, new int[][]{{0,2,300},{0,1,100},{1,2,100}}, 0, 2, 1, 200);
        // Zero allowed stops with only multi-hop available -> -1
        check("zero_stops_only_multihop", 3, new int[][]{{0,1,100},{1,2,100}}, 0, 2, 0, -1);

        // ===== ADDED CORNER CASES =====
        // Boundary: minimum price 1 on each edge, multi-hop
        check("min_price_edges", 4, new int[][]{{0,1,1},{1,2,1},{2,3,1}}, 0, 3, 2, 3);
        // Boundary: maximum price 10000 single direct hop
        check("max_price_direct", 2, new int[][]{{0,1,10000}}, 0, 1, 0, 10000);
        // k exactly equals needed stops (off-by-one lower bound)
        check("k_exactly_enough", 4, new int[][]{{0,1,1},{1,2,1},{2,3,1}}, 0, 3, 2, 3);
        // k one less than needed (off-by-one) -> -1 (no direct)
        check("k_one_too_few", 4, new int[][]{{0,1,1},{1,2,1},{2,3,1}}, 0, 3, 1, -1);
        // Many cheap small hops vs one pricey direct, k allows all hops
        check("many_hops_beat_direct", 5, new int[][]{{0,1,1},{1,2,1},{2,3,1},{3,4,1},{0,4,10}}, 0, 4, 3, 4);
        // Direct cheaper than any multi-hop
        check("direct_cheapest", 4, new int[][]{{0,3,5},{0,1,3},{1,2,3},{2,3,3}}, 0, 3, 5, 5);
        // Two-node cycle on the way, doesn't help reduce
        check("two_node_cycle_neutral", 4, new int[][]{{0,1,2},{1,0,2},{1,2,2},{2,3,2}}, 0, 3, 3, 6);
        // Cheaper longer path needs more stops than allowed -> take direct expensive
        check("stop_limit_forces_direct", 6, new int[][]{{0,1,1},{1,2,1},{2,3,1},{3,4,1},{4,5,1},{0,5,50}}, 0, 5, 2, 50);
        // Multiple equal-cost routes -> still correct min
        check("equal_cost_routes", 4, new int[][]{{0,1,5},{1,3,5},{0,2,5},{2,3,5}}, 0, 3, 1, 10);
        // src can reach dst directly AND via hop; hop within k cheaper
        check("hop_cheaper_than_direct", 3, new int[][]{{0,2,10},{0,1,2},{1,2,3}}, 0, 2, 1, 5);
        // dst reachable only with the FULL k+1 edges allowed
        check("needs_full_k_edges", 5, new int[][]{{0,1,2},{1,2,2},{2,3,2},{3,4,2}}, 0, 4, 3, 8);
        // Branchy graph: cheaper branch needs 2 stops, allowed exactly
        check("branchy_two_stops", 6, new int[][]{{0,1,1},{1,5,100},{0,2,1},{2,3,1},{3,5,1}}, 0, 5, 2, 3);
        // Self-reachable: src==dst not allowed by constraints, skip; isolated dst -> -1
        check("isolated_dst", 5, new int[][]{{0,1,1},{1,2,1}}, 0, 4, 4, -1);
        // Large k far exceeding n -> behaves as unlimited
        check("k_far_exceeds", 4, new int[][]{{0,1,1},{1,2,1},{2,3,1}}, 0, 3, 99, 3);
        // Convergent cheaper path via a shared mid node
        check("shared_mid_node", 5, new int[][]{{0,1,4},{0,2,1},{2,1,1},{1,3,1},{3,4,1}}, 0, 4, 3, 4);

        // ===== LARGE / PERFORMANCE CASES (seed 42, oracle = bounded Bellman-Ford, timed) =====
        Random rnd = new java.util.Random(42);
        // Dense max graph n=100, up to n*(n-1)/2 = 4950 unique directed edges, prices 1..10000.
        {
            int n = 100;
            HashSet<Long> used = new HashSet<>();
            List<int[]> edges = new ArrayList<>();
            int target = 4950;
            int guard = 0;
            while (edges.size() < target && guard++ < target * 6) {
                int u = rnd.nextInt(n), v = rnd.nextInt(n);
                if (u == v) continue;
                long key = (long) u * n + v;
                if (used.add(key)) edges.add(new int[]{u, v, 1 + rnd.nextInt(10000)});
            }
            int[][] arr = edges.toArray(new int[0][]);
            checkLarge("large_dense_n100_k50", n, arr, 0, n - 1, 50, 3000);
        }
        // Same dense graph but k=0 (direct only).
        {
            int n = 100;
            HashSet<Long> used = new HashSet<>();
            List<int[]> edges = new ArrayList<>();
            int target = 4000;
            int guard = 0;
            while (edges.size() < target && guard++ < target * 6) {
                int u = rnd.nextInt(n), v = rnd.nextInt(n);
                if (u == v) continue;
                long key = (long) u * n + v;
                if (used.add(key)) edges.add(new int[]{u, v, 1 + rnd.nextInt(10000)});
            }
            checkLarge("large_dense_n100_k0", n, edges.toArray(new int[0][]), 0, n - 1, 0, 3000);
        }
        // Long single chain n=100, must use k=n-2 stops to reach the end.
        {
            int n = 100;
            int[][] e = new int[n - 1][3];
            for (int i = 0; i < n - 1; i++) e[i] = new int[]{i, i + 1, 1 + rnd.nextInt(10000)};
            checkLarge("large_chain_n100_full_k", n, e, 0, n - 1, n - 1, 3000);
        }
        // Chain n=100 but k too small to reach -> -1 path.
        {
            int n = 100;
            int[][] e = new int[n - 1][3];
            for (int i = 0; i < n - 1; i++) e[i] = new int[]{i, i + 1, 1 + rnd.nextInt(10000)};
            checkLarge("large_chain_n100_k_too_small", n, e, 0, n - 1, 10, 3000);
        }
        // Graph with a cycle near src; k bounded so it cannot loop forever, dst reachable.
        {
            int n = 100;
            List<int[]> edges = new ArrayList<>();
            for (int i = 0; i < n - 1; i++) edges.add(new int[]{i, i + 1, 1 + rnd.nextInt(100)});
            edges.add(new int[]{5, 0, 1});   // back edge forms a cycle
            edges.add(new int[]{10, 3, 1});  // another cycle
            checkLarge("large_cycles_bounded_k_n100", n, edges.toArray(new int[0][]), 0, n - 1, 60, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
