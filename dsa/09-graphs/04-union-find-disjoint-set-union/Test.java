import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static int[][] deepCopy(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = Arrays.copyOf(a[i], a[i].length);
        return c;
    }

    static void check(String name, int[][] isConnected, int expected) {
        total++;
        try {
            int actual = new Answer().findCircleNum(deepCopy(isConnected));
            if (actual == expected) { pass++; System.out.println("\033[32m[PASS]\033[0m " + name); }
            else System.out.println("\033[31m[FAIL]\033[0m " + name + " | matrix=" + Arrays.deepToString(isConnected) + " expected=" + expected + " actual=" + actual);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // build n x n identity matrix then add the given undirected edges (symmetric)
    static int[][] build(int n, int[][] edges) {
        int[][] m = new int[n][n];
        for (int i = 0; i < n; i++) m[i][i] = 1;
        for (int[] e : edges) { m[e[0]][e[1]] = 1; m[e[1]][e[0]] = 1; }
        return m;
    }

    // Independent oracle: count connected components via BFS over the adjacency matrix.
    static int oracle(int[][] m) {
        int n = m.length;
        boolean[] seen = new boolean[n];
        int count = 0;
        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int s = 0; s < n; s++) {
            if (!seen[s]) {
                count++;
                seen[s] = true;
                q.add(s);
                while (!q.isEmpty()) {
                    int u = q.poll();
                    for (int v = 0; v < n; v++)
                        if (m[u][v] == 1 && !seen[v]) { seen[v] = true; q.add(v); }
                }
            }
        }
        return count;
    }

    static void checkLarge(String name, int[][] m, long budgetMs) {
        total++;
        try {
            int expected = oracle(m);
            long t0 = System.nanoTime();
            int actual = new Answer().findCircleNum(deepCopy(m));
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lbl = name + " | n=" + m.length + " elapsed=" + elapsedMs + "ms";
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
        check("example1_two_provinces", new int[][]{{1,1,0},{1,1,0},{0,0,1}}, 2);
        // Example 2 - all isolated
        check("example2_three_isolated", new int[][]{{1,0,0},{0,1,0},{0,0,1}}, 3);
        // Example 3 - all connected
        check("example3_all_connected", new int[][]{{1,1,1},{1,1,1},{1,1,1}}, 1);

        // Single city
        check("single_city", new int[][]{{1}}, 1);
        // Two cities connected
        check("two_connected", new int[][]{{1,1},{1,1}}, 1);
        // Two cities isolated
        check("two_isolated", new int[][]{{1,0},{0,1}}, 2);
        // Chain a-b-c-d -> 1 province (transitive)
        check("chain_transitive", build(4, new int[][]{{0,1},{1,2},{2,3}}), 1);
        // Two pairs -> 2 provinces
        check("two_pairs", build(4, new int[][]{{0,1},{2,3}}), 2);
        // Star graph -> 1 province
        check("star_graph", build(5, new int[][]{{0,1},{0,2},{0,3},{0,4}}), 1);
        // 6 isolated cities
        check("six_isolated", build(6, new int[][]{}), 6);
        // 6 cities all connected
        check("six_all_connected", build(6, new int[][]{{0,1},{0,2},{0,3},{0,4},{0,5},{1,2},{1,3},{1,4},{1,5},{2,3},{2,4},{2,5},{3,4},{3,5},{4,5}}), 1);
        // Two triangles -> 2 provinces
        check("two_triangles", build(6, new int[][]{{0,1},{1,2},{0,2},{3,4},{4,5},{3,5}}), 2);
        // Mixed: a-b, c alone, d-e-f chain -> 3 provinces
        check("mixed_three", build(6, new int[][]{{0,1},{3,4},{4,5}}), 3);
        // Larger: 7 nodes, edges forming 3 groups {0,1,2},{3,4},{5},{6}? -> recount
        check("seven_nodes_groups", build(7, new int[][]{{0,1},{1,2},{3,4}}), 4);

        // ===== ADDED CORNER CASES =====
        // Boundary min n=1
        check("min_single_city", build(1, new int[][]{}), 1);
        // Long chain n=10 -> 1 province (transitive closure)
        {
            int n = 10;
            int[][] e = new int[n - 1][2];
            for (int i = 0; i < n - 1; i++) e[i] = new int[]{i, i + 1};
            check("long_chain_10", build(n, e), 1);
        }
        // Ring of 8 -> 1 province
        {
            int n = 8;
            int[][] e = new int[n][2];
            for (int i = 0; i < n; i++) e[i] = new int[]{i, (i + 1) % n};
            check("ring_8", build(n, e), 1);
        }
        // n=10 all isolated -> 10 provinces (only diagonal set)
        check("ten_all_isolated", build(10, new int[][]{}), 10);
        // Complete graph K6 -> 1
        {
            int n = 6;
            List<int[]> e = new ArrayList<>();
            for (int a = 0; a < n; a++) for (int b = a + 1; b < n; b++) e.add(new int[]{a, b});
            check("complete_k6", build(n, e.toArray(new int[0][])), 1);
        }
        // Three equal-size triangles -> 3 provinces
        check("three_triangles", build(9, new int[][]{{0,1},{1,2},{0,2},{3,4},{4,5},{3,5},{6,7},{7,8},{6,8}}), 3);
        // Two big chains -> 2 provinces
        check("two_chains", build(10, new int[][]{{0,1},{1,2},{3,4},{4,5},{5,6},{7,8},{8,9}}), 3);
        // Star with extra cross edges still 1
        check("star_with_cross", build(6, new int[][]{{0,1},{0,2},{0,3},{0,4},{0,5},{1,2}}), 1);
        // Off-by-one: edge connects only last two of many
        check("only_last_pair_connected", build(7, new int[][]{{5,6}}), 6);
        // Alternating pairs (0-1),(2-3),(4-5) -> 3 provinces
        check("alternating_pairs", build(6, new int[][]{{0,1},{2,3},{4,5}}), 3);
        // Dense but missing one bridge -> 2 provinces
        check("two_cliques_no_bridge", build(6, new int[][]{{0,1},{0,2},{1,2},{3,4},{3,5},{4,5}}), 2);
        // Same but WITH a bridge -> 1 province
        check("two_cliques_with_bridge", build(6, new int[][]{{0,1},{0,2},{1,2},{3,4},{3,5},{4,5},{2,3}}), 1);
        // One central hub connecting otherwise separate triangles -> 1
        check("hub_joins_triangles", build(7, new int[][]{{0,1},{1,2},{0,2},{3,4},{4,5},{3,5},{6,0},{6,3}}), 1);
        // Self-only diagonal large -> n provinces (n=12)
        check("twelve_isolated", build(12, new int[][]{}), 12);
        // Two nodes connected indirectly via a third (path) -> 1
        check("path_three", build(3, new int[][]{{0,1},{1,2}}), 1);

        // ===== LARGE / PERFORMANCE CASES (seed 42, oracle = BFS, timed) =====
        Random rnd = new java.util.Random(42);
        // Max constraint n=200: fully connected -> 1 province.
        {
            int n = 200;
            int[][] m = new int[n][n];
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) m[i][j] = 1;
            checkLarge("large_full_200_one_province", m, 3000);
        }
        // Max n=200: all isolated -> 200 provinces (only diagonal).
        {
            int n = 200;
            int[][] m = new int[n][n];
            for (int i = 0; i < n; i++) m[i][i] = 1;
            checkLarge("large_isolated_200", m, 3000);
        }
        // Max n=200: random sparse symmetric edges (~p=3%).
        {
            int n = 200;
            int[][] m = new int[n][n];
            for (int i = 0; i < n; i++) m[i][i] = 1;
            for (int i = 0; i < n; i++)
                for (int j = i + 1; j < n; j++)
                    if (rnd.nextInt(100) < 3) { m[i][j] = 1; m[j][i] = 1; }
            checkLarge("large_random_sparse_200", m, 3000);
        }
        // Max n=200: one long chain 0-1-2-...-199 -> 1 province.
        {
            int n = 200;
            int[][] m = new int[n][n];
            for (int i = 0; i < n; i++) m[i][i] = 1;
            for (int i = 0; i < n - 1; i++) { m[i][i + 1] = 1; m[i + 1][i] = 1; }
            checkLarge("large_chain_200_one_province", m, 3000);
        }
        // Max n=200: 50 disjoint cliques of size 4 -> 50 provinces.
        {
            int n = 200;
            int[][] m = new int[n][n];
            for (int i = 0; i < n; i++) m[i][i] = 1;
            for (int g = 0; g < 50; g++) {
                int base = g * 4;
                for (int a = 0; a < 4; a++) for (int b = 0; b < 4; b++) m[base + a][base + b] = 1;
            }
            checkLarge("large_50_cliques_of_4", m, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
