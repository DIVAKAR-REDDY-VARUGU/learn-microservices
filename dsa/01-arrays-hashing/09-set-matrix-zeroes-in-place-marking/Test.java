import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static int[][] copy(int[][] m) {
        int[][] c = new int[m.length][];
        for (int i = 0; i < m.length; i++) c[i] = m[i].clone();
        return c;
    }

    static void check(String name, int[][] matrix, int[][] expected) {
        total++;
        int[][] in = copy(matrix);
        try {
            new Answer().setZeroes(in);
            if (Arrays.deepEquals(in, expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + briefM(in));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | matrix=" + briefM(matrix)
                        + " | expected=" + briefM(expected) + " | actual=" + briefM(in));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | matrix=" + briefM(matrix)
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: compare against independent oracle and time it
    static void checkProp(String name, int[][] matrix, long budgetMs) {
        total++;
        int[][] in = copy(matrix);
        try {
            int[][] expected = oracle(matrix);
            long t0 = System.nanoTime();
            new Answer().setZeroes(in);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            boolean ok = Arrays.deepEquals(in, expected);
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | " + matrix.length + "x" + matrix[0].length + " | mismatch vs oracle");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // independent oracle using extra O(m+n) marker arrays
    static int[][] oracle(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean[] zr = new boolean[m];
        boolean[] zc = new boolean[n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (matrix[i][j] == 0) { zr[i] = true; zc[j] = true; }
        int[][] r = new int[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                r[i][j] = (zr[i] || zc[j]) ? 0 : matrix[i][j];
        return r;
    }

    static String briefM(int[][] m) {
        if (m.length <= 6 && (m.length == 0 || m[0].length <= 8)) return Arrays.deepToString(m);
        return "[" + m.length + "x" + (m.length == 0 ? 0 : m[0].length) + "]";
    }

    public static void main(String[] args) {
        check("example1", new int[][]{{1,1,1},{1,0,1},{1,1,1}}, new int[][]{{1,0,1},{0,0,0},{1,0,1}});
        check("example2", new int[][]{{0,1,2,0},{3,4,5,2},{1,3,1,5}}, new int[][]{{0,0,0,0},{0,4,5,0},{0,3,1,0}});
        check("example3 no zero", new int[][]{{1,2,3}}, new int[][]{{1,2,3}});
        check("single zero cell", new int[][]{{0}}, new int[][]{{0}});
        check("single nonzero cell", new int[][]{{5}}, new int[][]{{5}});
        check("first row zero", new int[][]{{1,0,3},{4,5,6}}, new int[][]{{0,0,0},{4,0,6}});
        check("first col zero", new int[][]{{0,2},{3,4},{5,6}}, new int[][]{{0,0},{0,4},{0,6}});
        check("all zeros", new int[][]{{0,0},{0,0}}, new int[][]{{0,0},{0,0}});
        check("corner zero", new int[][]{{0,1},{1,1}}, new int[][]{{0,0},{0,1}});
        check("negatives preserved", new int[][]{{-1,-2,-3},{-4,0,-6},{-7,-8,-9}}, new int[][]{{-1,0,-3},{0,0,0},{-7,0,-9}});
        check("single column", new int[][]{{1},{0},{3}}, new int[][]{{0},{0},{0}});

        // --- added corner cases ---
        check("single row with zero", new int[][]{{1,0,3,4}}, new int[][]{{0,0,0,0}});
        check("single col no zero", new int[][]{{1},{2},{3}}, new int[][]{{1},{2},{3}});
        check("zero only at last cell", new int[][]{{1,2},{3,0}}, new int[][]{{1,0},{0,0}});
        check("zero only at top-right", new int[][]{{1,0},{3,4}}, new int[][]{{0,0},{3,0}});
        check("two zeros same row", new int[][]{{1,0,0},{4,5,6},{7,8,9}}, new int[][]{{0,0,0},{4,0,0},{7,0,0}});
        check("two zeros same col", new int[][]{{0,2,3},{0,5,6},{7,8,9}}, new int[][]{{0,0,0},{0,0,0},{0,8,9}});
        check("diagonal zeros", new int[][]{{0,2,3},{4,0,6},{7,8,0}}, new int[][]{{0,0,0},{0,0,0},{0,0,0}});
        check("no zeros 3x3", new int[][]{{1,2,3},{4,5,6},{7,8,9}}, new int[][]{{1,2,3},{4,5,6},{7,8,9}});
        check("int min and max with zero", new int[][]{{-2147483648,2147483647},{0,1}}, new int[][]{{0,2147483647},{0,0}});
        check("zero in middle col only", new int[][]{{1,0,3},{4,0,6}}, new int[][]{{0,0,0},{0,0,0}});
        check("wide single row no zero", new int[][]{{1,2,3,4,5,6}}, new int[][]{{1,2,3,4,5,6}});
        check("tall single col with zero mid", new int[][]{{1},{2},{0},{4}}, new int[][]{{0},{0},{0},{0}});
        check("zero at very first cell", new int[][]{{0,2,3},{4,5,6},{7,8,9}}, new int[][]{{0,0,0},{0,5,6},{0,8,9}});
        check("bottom row all become zero", new int[][]{{1,2},{3,4},{0,6}}, new int[][]{{1,0},{3,0},{0,0}});
        check("2x2 with both flags", new int[][]{{1,2},{3,0}}, new int[][]{{1,0},{0,0}});
        check("large value preserved no zero", new int[][]{{100,200},{300,400}}, new int[][]{{100,200},{300,400}});

        // --- large / performance cases (oracle + timing; m,n up to 200 per constraints) ---
        // Max 200x200 dense no zeros (sparse changes) -> verifies in-place doesn't corrupt.
        {
            Random rnd = new Random(42);
            int m = 200, n = 200;
            int[][] big = new int[m][n];
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++) big[i][j] = 1 + rnd.nextInt(1000); // strictly nonzero
            checkProp("max 200x200 no zeros", big, 3000);
        }
        // Max 200x200 with scattered zeros (~1%) -> oracle decides expected.
        {
            Random rnd = new Random(42);
            int m = 200, n = 200;
            int[][] big = new int[m][n];
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++) big[i][j] = (rnd.nextInt(100) == 0) ? 0 : (1 + rnd.nextInt(1000));
            checkProp("max 200x200 sparse zeros", big, 3000);
        }
        // Max 200x200 single zero in the very middle -> exactly one row and col zeroed.
        {
            int m = 200, n = 200;
            int[][] big = new int[m][n];
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++) big[i][j] = i * n + j + 1; // all nonzero distinct
            big[100][100] = 0;
            checkProp("max 200x200 single middle zero", big, 3000);
        }
        // Non-square 1x200 and 200x1 stress thin shapes.
        {
            int n = 200;
            int[][] row = new int[1][n];
            for (int j = 0; j < n; j++) row[0][j] = j + 1;
            row[0][123] = 0;
            checkProp("thin 1x200 single zero", row, 3000);
        }
        {
            int m = 200;
            int[][] col = new int[m][1];
            for (int i = 0; i < m; i++) col[i][0] = i + 1;
            col[77][0] = 0;
            checkProp("thin 200x1 single zero", col, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
