import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static char[][] grid(String... rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) g[i] = rows[i].toCharArray();
        return g;
    }

    static void check(String name, char[][] g, int expected) {
        total++;
        try {
            // defensive copy so a flood-fill mutating impl doesn't break later cases
            char[][] copy = new char[g.length][];
            for (int i = 0; i < g.length; i++) copy[i] = Arrays.copyOf(g[i], g[i].length);
            int actual = new Answer().numIslands(copy);
            if (actual == expected) { pass++; System.out.println("\033[32m[PASS]\033[0m " + name); }
            else System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected=" + expected + " actual=" + actual);
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent oracle: count connected components of '1' cells (4-directional) via BFS.
    static int oracle(char[][] g) {
        int m = g.length, n = g[0].length;
        boolean[][] seen = new boolean[m][n];
        int count = 0;
        int[] dr = {1, -1, 0, 0}, dc = {0, 0, 1, -1};
        ArrayDeque<int[]> q = new ArrayDeque<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (g[i][j] == '1' && !seen[i][j]) {
                    count++;
                    seen[i][j] = true;
                    q.add(new int[]{i, j});
                    while (!q.isEmpty()) {
                        int[] c = q.poll();
                        for (int d = 0; d < 4; d++) {
                            int nr = c[0] + dr[d], nc = c[1] + dc[d];
                            if (nr >= 0 && nr < m && nc >= 0 && nc < n && g[nr][nc] == '1' && !seen[nr][nc]) {
                                seen[nr][nc] = true;
                                q.add(new int[]{nr, nc});
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    static void checkLarge(String name, char[][] g, long budgetMs) {
        total++;
        try {
            int expected = oracle(g);
            char[][] copy = new char[g.length][];
            for (int i = 0; i < g.length; i++) copy[i] = Arrays.copyOf(g[i], g[i].length);
            long t0 = System.nanoTime();
            int actual = new Answer().numIslands(copy);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lbl = name + " | elapsed=" + elapsedMs + "ms";
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | expected=" + expected + " actual=" + actual);
            } else if (elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + lbl + " | exceeded budget " + budgetMs + "ms (catastrophic complexity)");
            } else {
                pass++; System.out.println("\033[32m[PASS]\033[0m " + lbl);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    public static void main(String[] args) {
        // Example 1
        check("example1_one_big_island", grid("11110","11010","11000","00000"), 1);
        // Example 2
        check("example2_three_islands", grid("11000","11000","00100","00011"), 3);
        // Example 3 - single row
        check("example3_single_row_alternating", grid("10101"), 3);

        // Corner: single cell water
        check("single_cell_water", grid("0"), 0);
        // Corner: single cell land
        check("single_cell_land", grid("1"), 1);
        // All water
        check("all_water_3x3", grid("000","000","000"), 0);
        // All land -> one island
        check("all_land_3x3", grid("111","111","111"), 1);
        // Single column alternating
        check("single_column_alternating", grid("1","0","1","0","1"), 3);
        // Diagonals are NOT connected
        check("diagonal_not_connected", grid("101","010","101"), 5);
        // Checkerboard 4x4
        check("checkerboard_4x4", grid("1010","0101","1010","0101"), 8);
        // U-shape single island
        check("u_shape_one_island", grid("101","101","111"), 1);
        // Two separated vertical strips
        check("two_vertical_strips", grid("1010","1010","1010"), 2);
        // Ring island (hollow center)
        check("ring_island", grid("1111","1001","1001","1111"), 1);
        // Land only on borders, water center counted as not island
        check("single_land_corner", grid("00000","00000","00001"), 1);
        // Larger mixed
        check("mixed_grid", grid("11000","01000","00011","00010","10001"), 4);
        // Wide single row all land
        check("single_row_all_land", grid("1111111"), 1);
        // Tall single column all land
        check("single_column_all_land", grid("1","1","1","1"), 1);

        // ===== ADDED CORNER CASES =====
        // Min 1x1 grid both states already above; add 1xN all water
        check("single_row_all_water", grid("0000000"), 0);
        // Single column all water
        check("single_column_all_water", grid("0","0","0","0"), 0);
        // Spiral-ish single connected island
        check("spiral_one_island", grid("11111","00001","11101","10001","11111"), 1);
        // Each corner a separate single-cell island
        check("four_corner_islands", grid("10001","00000","00000","00000","10001"), 4);
        // Plus/cross shape one island
        check("plus_shape", grid("010","111","010"), 1);
        // Two L shapes touching at a diagonal -> 2 islands (diagonal not connected)
        check("two_diagonal_touch", grid("110","100","001"), 2);
        // Long snake winding -> single island
        check("snake_one_island", grid("11111","00001","11111","10000","11111"), 1);
        // Maximum-ish dense checkerboard count on odd grid
        check("checkerboard_5x5", grid("10101","01010","10101","01010","10101"), 13);
        // Tall thin alternating in single column already; wide alternating long row
        check("long_alternating_row", grid("1010101010101"), 7);
        // Island only along the full border (frame), interior water -> 1 island
        check("full_frame_island", grid("11111","10001","10001","10001","11111"), 1);
        // Two stacked frames sharing nothing
        check("two_separate_blocks", grid("1100","1100","0011","0011"), 2);
        // Comb pattern: vertical teeth joined at bottom -> 1 island
        check("comb_joined", grid("10101","10101","11111"), 1);
        // Comb pattern teeth NOT joined -> 3 islands
        check("comb_separate", grid("10101","10101","10101"), 3);
        // Single land in a sea, off-by-one position (top-left)
        check("single_land_topleft", grid("10000","00000","00000"), 1);
        // Two cells connected horizontally only
        check("two_horizontal", grid("11"), 1);
        // Two cells connected vertically only
        check("two_vertical", grid("1","1"), 1);

        // ===== LARGE / PERFORMANCE CASES (verified via independent BFS oracle, timed) =====
        Random rnd = new java.util.Random(42);
        // Big all-land grid (single island) ~ 300x300 (max constraint)
        {
            int m = 300, n = 300;
            char[][] g = new char[m][n];
            for (char[] row : g) Arrays.fill(row, '1');
            checkLarge("large_all_land_300x300_one_island", g, 3000);
        }
        // Big checkerboard ~ 300x300 -> ~45000 islands, exposes per-cell quadratic visited scans
        {
            int m = 300, n = 300;
            char[][] g = new char[m][n];
            for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) g[i][j] = ((i + j) % 2 == 0) ? '1' : '0';
            checkLarge("large_checkerboard_300x300", g, 3000);
        }
        // Big random grid, ~50% land, seed 42 (many irregular islands)
        {
            int m = 300, n = 300;
            char[][] g = new char[m][n];
            for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) g[i][j] = rnd.nextBoolean() ? '1' : '0';
            checkLarge("large_random_300x300_p50", g, 3000);
        }
        // Big sparse random grid (~10% land) -> many tiny islands
        {
            int m = 300, n = 300;
            char[][] g = new char[m][n];
            for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) g[i][j] = (rnd.nextInt(10) == 0) ? '1' : '0';
            checkLarge("large_random_300x300_sparse", g, 3000);
        }
        // Single long thin grid 1x90000 alternating (forces shallow recursion / wide BFS)
        {
            int n = 90000;
            char[][] g = new char[1][n];
            for (int j = 0; j < n; j++) g[0][j] = (j % 2 == 0) ? '1' : '0';
            checkLarge("large_single_row_90000_alternating", g, 3000);
        }
        // Deep single column 90000x1 all land (worst case for recursive DFS depth)
        {
            int m = 90000;
            char[][] g = new char[m][1];
            for (int i = 0; i < m; i++) g[i][0] = '1';
            checkLarge("large_single_column_90000_all_land", g, 3000);
        }
        // Big spiral-ish dense with internal holes (one giant connected island), 300x300 frame-rings
        {
            int m = 300, n = 300;
            char[][] g = new char[m][n];
            for (char[] row : g) Arrays.fill(row, '1');
            // punch isolated water holes that keep land connected
            for (int i = 1; i < m; i += 2) for (int j = 1; j < n; j += 2) g[i][j] = '0';
            checkLarge("large_300x300_holes_one_island", g, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
