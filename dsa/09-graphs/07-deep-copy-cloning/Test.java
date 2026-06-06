import java.util.*;

// Node definition (referenced by Answer.cloneGraph but not defined in Answer.java).
class Node {
    public int val;
    public List<Node> neighbors;
    public Node() { val = 0; neighbors = new ArrayList<>(); }
    public Node(int v) { val = v; neighbors = new ArrayList<>(); }
    public Node(int v, ArrayList<Node> ns) { val = v; neighbors = ns; }
}

public class Test {
    static int pass = 0, total = 0;

    // Build a graph from an adjacency list where node value == 1-indexed position.
    // adj[i] holds the 1-indexed neighbor values of node (i+1). Returns node with val==1, or null if empty.
    static Node buildGraph(int[][] adj) {
        if (adj.length == 0) return null;
        int n = adj.length;
        Node[] nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) nodes[i] = new Node(i);
        for (int i = 0; i < n; i++)
            for (int nb : adj[i]) nodes[i + 1].neighbors.add(nodes[nb]);
        return nodes[1];
    }

    // Serialize a graph (reachable from start) into a canonical adjacency map: val -> sorted neighbor vals.
    static Map<Integer, List<Integer>> serialize(Node start) {
        Map<Integer, List<Integer>> result = new TreeMap<>();
        if (start == null) return result;
        Set<Node> visited = new HashSet<>();
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(start);
        visited.add(start);
        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            List<Integer> nbVals = new ArrayList<>();
            for (Node nb : cur.neighbors) {
                nbVals.add(nb.val);
                if (visited.add(nb)) stack.push(nb);
            }
            Collections.sort(nbVals);
            result.put(cur.val, nbVals);
        }
        return result;
    }

    // Collect all node object identities reachable from start.
    static Set<Node> collectIdentities(Node start) {
        Set<Node> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        if (start == null) return visited;
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(start);
        visited.add(start);
        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            for (Node nb : cur.neighbors) if (visited.add(nb)) stack.push(nb);
        }
        return visited;
    }

    static void checkGraph(String name, int[][] adj) {
        total++;
        try {
            Node original = buildGraph(adj);
            Set<Node> originalIds = collectIdentities(original);
            Map<Integer, List<Integer>> originalShape = serialize(original);

            Node clone = new Answer().cloneGraph(original);

            if (original == null) {
                if (clone == null) { pass++; System.out.println("\033[32m[PASS]\033[0m " + name + " (null)"); }
                else System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected null clone, got non-null");
                return;
            }
            // structure must match
            Map<Integer, List<Integer>> cloneShape = serialize(clone);
            if (!originalShape.equals(cloneShape)) {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | shape mismatch. expected=" + originalShape + " actual=" + cloneShape);
                return;
            }
            // deep copy: no clone node may be an original node object
            Set<Node> cloneIds = collectIdentities(clone);
            for (Node cn : cloneIds) {
                if (originalIds.contains(cn)) {
                    System.out.println("\033[31m[FAIL]\033[0m " + name + " | clone reuses original node object (val=" + cn.val + ") - not a deep copy");
                    return;
                }
            }
            // returned node must have val == 1 (the given node)
            if (clone.val != original.val) {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | returned node val=" + clone.val + " expected=" + original.val);
                return;
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m " + name);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Large/perf variant: same property checks plus timing budget.
    static void checkGraphLarge(String name, int[][] adj, long budgetMs) {
        total++;
        try {
            Node original = buildGraph(adj);
            Set<Node> originalIds = collectIdentities(original);
            Map<Integer, List<Integer>> originalShape = serialize(original);

            long t0 = System.nanoTime();
            Node clone = new Answer().cloneGraph(original);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lbl = name + " | nodes=" + adj.length + " elapsed=" + elapsedMs + "ms";

            if (original == null) {
                if (clone == null) { pass++; System.out.println("\033[32m[PASS]\033[0m " + lbl + " (null)"); }
                else System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | expected null clone, got non-null");
                return;
            }
            Map<Integer, List<Integer>> cloneShape = serialize(clone);
            if (!originalShape.equals(cloneShape)) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | shape mismatch");
                return;
            }
            Set<Node> cloneIds = collectIdentities(clone);
            for (Node cn : cloneIds) {
                if (originalIds.contains(cn)) {
                    System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | clone reuses original node object (val=" + cn.val + ")");
                    return;
                }
            }
            if (clone.val != original.val) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | returned node val=" + clone.val + " expected=" + original.val);
                return;
            }
            if (cloneIds.size() != originalIds.size()) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | clone node count=" + cloneIds.size() + " expected=" + originalIds.size());
                return;
            }
            if (elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | exceeded budget " + budgetMs + "ms");
                return;
            }
            pass++;
            System.out.println("\033[32m[PASS]\033[0m " + lbl);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Build an undirected adjacency list (1-indexed, vals==positions) from edges; returns adj[n][].
    static int[][] adjFromEdges(int n, int[][] edges) {
        List<List<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < n; i++) lists.add(new ArrayList<>());
        for (int[] e : edges) {
            lists.get(e[0]).add(e[1] + 1);
            lists.get(e[1]).add(e[0] + 1);
        }
        int[][] adj = new int[n][];
        for (int i = 0; i < n; i++) {
            List<Integer> l = lists.get(i);
            int[] row = new int[l.size()];
            for (int j = 0; j < row.length; j++) row[j] = l.get(j);
            adj[i] = row;
        }
        return adj;
    }

    public static void main(String[] args) {
        // Example 1 - 4 node square
        checkGraph("example1_square", new int[][]{{2,4},{1,3},{2,4},{1,3}});
        // Example 2 - single node no neighbors
        checkGraph("example2_single_no_neighbors", new int[][]{{}});
        // Example 3 - empty graph (null)
        checkGraph("example3_empty_null", new int[][]{});

        // Two nodes connected
        checkGraph("two_nodes", new int[][]{{2},{1}});
        // Triangle (cycle)
        checkGraph("triangle_cycle", new int[][]{{2,3},{1,3},{1,2}});
        // Line graph 1-2-3
        checkGraph("line_three", new int[][]{{2},{1,3},{2}});
        // Star: center 1 connected to 2,3,4,5
        checkGraph("star_five", new int[][]{{2,3,4,5},{1},{1},{1},{1}});
        // Larger ring of 5
        checkGraph("ring_five", new int[][]{{2,5},{1,3},{2,4},{3,5},{4,1}});
        // Complete graph K4
        checkGraph("complete_k4", new int[][]{{2,3,4},{1,3,4},{1,2,4},{1,2,3}});
        // Two nodes with self-free back-and-forth (multi-neighbor)
        checkGraph("two_node_doubled_structure", new int[][]{{2},{1}});
        // Chain of 6
        checkGraph("chain_six", new int[][]{{2},{1,3},{2,4},{3,5},{4,6},{5}});
        // Explicit null input via direct call
        total++;
        try {
            Node res = new Answer().cloneGraph(null);
            if (res == null) { pass++; System.out.println("\033[32m[PASS]\033[0m explicit_null_input"); }
            else System.out.println("\033[31m[FAIL]\033[0m explicit_null_input | expected null, got non-null");
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m explicit_null_input | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        // ===== ADDED CORNER CASES =====
        // Single node self-loop is NOT allowed by constraints; smallest connected = 1 node (covered) -> 2-node again differently
        // Path of 4
        checkGraph("path_four", new int[][]{{2},{1,3},{2,4},{3}});
        // Long chain of 10 (deep DFS, off-by-one ends)
        checkGraph("chain_ten", adjFromEdges(10, chainEdges(10)));
        // Ring of 10
        checkGraph("ring_ten", adjFromEdges(10, ringEdges(10)));
        // Complete K5 (dense neighbors)
        checkGraph("complete_k5", completeAdj(5));
        // Star of 8 (center 1, leaves 2..8)
        {
            int n = 8;
            int[][] adj = new int[n][];
            int[] center = new int[n - 1];
            for (int i = 0; i < n - 1; i++) center[i] = i + 2;
            adj[0] = center;
            for (int i = 1; i < n; i++) adj[i] = new int[]{1};
            checkGraph("star_eight", adj);
        }
        // Two triangles joined by a single bridge edge (3-4)
        checkGraph("two_triangles_bridge", new int[][]{{2,3},{1,3},{1,2,4},{3,5,6},{4,6},{4,5}});
        // Binary-tree-like (connected, node 1 root) - undirected
        checkGraph("tree_seven", adjFromEdges(7, new int[][]{{0,1},{0,2},{1,3},{1,4},{2,5},{2,6}}));
        // Wheel: center connected to a ring of 5
        checkGraph("wheel_six", wheelAdj(6));
        // Two parallel chains joined at both ends (cycle of 6)
        checkGraph("hexagon_cycle", adjFromEdges(6, ringEdges(6)));
        // Grid 3x3 connected (9 nodes)
        checkGraph("grid_3x3", adjFromEdges(9, gridEdges(3, 3)));
        // Densely connected: K3 plus a tail
        checkGraph("k3_with_tail", new int[][]{{2,3},{1,3},{1,2,4},{3}});
        // Node 1 with many neighbors that are themselves interconnected
        checkGraph("hub_interconnected", new int[][]{{2,3,4},{1,3},{1,2,4},{1,3}});
        // Long even ring of 12
        checkGraph("ring_twelve", adjFromEdges(12, ringEdges(12)));
        // Caterpillar: spine of 5 each with one leaf
        checkGraph("caterpillar", caterpillarAdj(5));

        // ===== LARGE / PERFORMANCE CASES (seed 42, property-verified, timed) =====
        // Max constraint: 100 nodes. Complete graph K100 (max edges, densest).
        checkGraphLarge("large_complete_k100", completeAdj(100), 3000);
        // 100-node ring (sparse, long traversal).
        checkGraphLarge("large_ring_100", adjFromEdges(100, ringEdges(100)), 3000);
        // 100-node deep chain (worst case recursion depth).
        checkGraphLarge("large_chain_100", adjFromEdges(100, chainEdges(100)), 3000);
        // 100-node random connected graph (seed 42): spanning tree + extra random edges.
        {
            int n = 100;
            Random rnd = new java.util.Random(42);
            HashSet<Long> used = new HashSet<>();
            List<int[]> edges = new ArrayList<>();
            // random spanning tree to guarantee connectivity
            for (int i = 1; i < n; i++) {
                int j = rnd.nextInt(i);
                edges.add(new int[]{i, j});
                used.add(pairKey(i, j, n));
            }
            // extra random edges (no self-loops, no repeats)
            int extra = 300, guard = 0;
            while (extra > 0 && guard++ < extra * 10) {
                int a = rnd.nextInt(n), b = rnd.nextInt(n);
                if (a == b) continue;
                long key = pairKey(a, b, n);
                if (used.add(key)) { edges.add(new int[]{a, b}); extra--; }
            }
            checkGraphLarge("large_random_connected_100", adjFromEdges(n, edges.toArray(new int[0][])), 3000);
        }
        // 100-node star (one hub with 99 neighbors).
        {
            int n = 100;
            int[][] adj = new int[n][];
            int[] center = new int[n - 1];
            for (int i = 0; i < n - 1; i++) center[i] = i + 2;
            adj[0] = center;
            for (int i = 1; i < n; i++) adj[i] = new int[]{1};
            checkGraphLarge("large_star_100", adj, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // ---- graph-shape builder helpers (undirected edges, 0-indexed node ids) ----
    static int[][] chainEdges(int n) {
        int[][] e = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++) e[i] = new int[]{i, i + 1};
        return e;
    }

    static int[][] ringEdges(int n) {
        int[][] e = new int[n][2];
        for (int i = 0; i < n; i++) e[i] = new int[]{i, (i + 1) % n};
        return e;
    }

    static int[][] gridEdges(int rows, int cols) {
        List<int[]> e = new ArrayList<>();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                int id = r * cols + c;
                if (c + 1 < cols) e.add(new int[]{id, id + 1});
                if (r + 1 < rows) e.add(new int[]{id, id + cols});
            }
        return e.toArray(new int[0][]);
    }

    // Complete graph adjacency (1-indexed vals).
    static int[][] completeAdj(int n) {
        int[][] adj = new int[n][];
        for (int i = 0; i < n; i++) {
            int[] row = new int[n - 1];
            int idx = 0;
            for (int j = 0; j < n; j++) if (j != i) row[idx++] = j + 1;
            adj[i] = row;
        }
        return adj;
    }

    // Wheel: node 0 is hub, nodes 1..n-1 form a ring, hub connects to all ring nodes.
    static int[][] wheelAdj(int n) {
        List<int[]> e = new ArrayList<>();
        int rim = n - 1;
        for (int i = 1; i < n; i++) e.add(new int[]{0, i});
        for (int i = 1; i < n; i++) {
            int a = i, b = (i % rim) + 1;
            e.add(new int[]{a, b});
        }
        return adjFromEdges(n, e.toArray(new int[0][]));
    }

    // Caterpillar: spine nodes 0..spine-1 chained, each spine node gets one leaf.
    static int[][] caterpillarAdj(int spine) {
        int n = spine * 2;
        List<int[]> e = new ArrayList<>();
        for (int i = 0; i < spine - 1; i++) e.add(new int[]{i, i + 1});
        for (int i = 0; i < spine; i++) e.add(new int[]{i, spine + i});
        return adjFromEdges(n, e.toArray(new int[0][]));
    }

    static long pairKey(int a, int b, int n) {
        int lo = Math.min(a, b), hi = Math.max(a, b);
        return (long) lo * n + hi;
    }
}
