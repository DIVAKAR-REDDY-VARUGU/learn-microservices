import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static void check(String name, int[][] matrix, List<Integer> expected) {
        total++;
        try {
            List<Integer> actual = new Answer().spiralOrder(copy(matrix));
            if (actual != null && actual.equals(expected)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + (expected.size() <= 20 ? expected.toString() : "[size " + expected.size() + "]"));
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | matrix=" + briefM(matrix)
                        + " | expected=" + (expected.size() <= 20 ? expected.toString() : "[size " + expected.size() + "]")
                        + " | actual=" + (actual == null ? "null" : (actual.size() <= 20 ? actual.toString() : "[size " + actual.size() + "]")));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | matrix=" + briefM(matrix)
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: compare against independent oracle and time it
    static void checkProp(String name, int[][] matrix, long budgetMs) {
        total++;
        try {
            List<Integer> expected = oracle(matrix);
            long t0 = System.nanoTime();
            List<Integer> actual = new Answer().spiralOrder(copy(matrix));
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            boolean ok = actual != null && actual.equals(expected);
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " -> [size " + expected.size() + "]");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | " + matrix.length + "x" + matrix[0].length
                        + " | expectedSize=" + expected.size() + " actualSize=" + (actual == null ? -1 : actual.size()));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // independent oracle: boundary-shrinking spiral traversal
    static List<Integer> oracle(int[][] matrix) {
        List<Integer> res = new ArrayList<>();
        int m = matrix.length, n = matrix[0].length;
        int top = 0, bottom = m - 1, left = 0, right = n - 1;
        while (top <= bottom && left <= right) {
            for (int j = left; j <= right; j++) res.add(matrix[top][j]);
            top++;
            for (int i = top; i <= bottom; i++) res.add(matrix[i][right]);
            right--;
            if (top <= bottom) {
                for (int j = right; j >= left; j--) res.add(matrix[bottom][j]);
                bottom--;
            }
            if (left <= right) {
                for (int i = bottom; i >= top; i--) res.add(matrix[i][left]);
                left++;
            }
        }
        return res;
    }

    static int[][] copy(int[][] m) {
        int[][] c = new int[m.length][];
        for (int i = 0; i < m.length; i++) c[i] = m[i].clone();
        return c;
    }

    static List<Integer> L(int... xs) {
        List<Integer> r = new ArrayList<>();
        for (int x : xs) r.add(x);
        return r;
    }

    static String briefM(int[][] m) {
        if (m.length <= 5 && (m.length == 0 || m[0].length <= 6)) return Arrays.deepToString(m);
        return "[" + m.length + "x" + (m.length == 0 ? 0 : m[0].length) + "]";
    }

    public static void main(String[] args) {
        check("example1 3x3", new int[][]{{1,2,3},{4,5,6},{7,8,9}}, L(1,2,3,6,9,8,7,4,5));
        check("example2 3x4", new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12}}, L(1,2,3,4,8,12,11,10,9,5,6,7));
        check("example3 single column", new int[][]{{7},{9},{6}}, L(7,9,6));
        check("single cell", new int[][]{{42}}, L(42));
        check("single row", new int[][]{{1,2,3,4}}, L(1,2,3,4));
        check("2x2", new int[][]{{1,2},{3,4}}, L(1,2,4,3));
        check("4x4", new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}}, L(1,2,3,4,8,12,16,15,14,13,9,5,6,7,11,10));
        check("tall 4x2", new int[][]{{1,2},{3,4},{5,6},{7,8}}, L(1,2,4,6,8,7,5,3));
        check("wide 2x4", new int[][]{{1,2,3,4},{5,6,7,8}}, L(1,2,3,4,8,7,6,5));
        check("negatives", new int[][]{{-1,-2},{-3,-4}}, L(-1,-2,-4,-3));
        check("single row two", new int[][]{{5,6}}, L(5,6));

        // --- added corner cases ---
        check("1x1 negative", new int[][]{{-100}}, L(-100));
        check("single row max width 10", new int[][]{{1,2,3,4,5,6,7,8,9,10}}, L(1,2,3,4,5,6,7,8,9,10));
        check("single col max height 10", new int[][]{{1},{2},{3},{4},{5},{6},{7},{8},{9},{10}}, L(1,2,3,4,5,6,7,8,9,10));
        check("3x2 wide-ish", new int[][]{{1,2},{3,4},{5,6}}, L(1,2,4,6,5,3));
        check("2x3", new int[][]{{1,2,3},{4,5,6}}, L(1,2,3,6,5,4));
        check("5x5 spiral", new int[][]{{1,2,3,4,5},{16,17,18,19,6},{15,24,25,20,7},{14,23,22,21,8},{13,12,11,10,9}}, L(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25));
        check("all equal 3x3", new int[][]{{7,7,7},{7,7,7},{7,7,7}}, L(7,7,7,7,7,7,7,7,7));
        check("bounds min max values", new int[][]{{-100,100},{100,-100}}, L(-100,100,-100,100));
        check("1x2 two only", new int[][]{{8,9}}, L(8,9));
        check("2x1 two only", new int[][]{{8},{9}}, L(8,9));
        check("4x3", new int[][]{{1,2,3},{4,5,6},{7,8,9},{10,11,12}}, L(1,2,3,6,9,12,11,10,7,4,5,8));
        check("3x5 wide", new int[][]{{1,2,3,4,5},{6,7,8,9,10},{11,12,13,14,15}}, L(1,2,3,4,5,10,15,14,13,12,11,6,7,8,9));
        check("5x3 tall", new int[][]{{1,2,3},{4,5,6},{7,8,9},{10,11,12},{13,14,15}}, L(1,2,3,6,9,12,15,14,13,10,7,4,5,8,11));
        check("negatives 3x3", new int[][]{{-1,-2,-3},{-4,-5,-6},{-7,-8,-9}}, L(-1,-2,-3,-6,-9,-8,-7,-4,-5));
        check("single row with duplicates", new int[][]{{2,2,2,2}}, L(2,2,2,2));
        check("single col with duplicates", new int[][]{{3},{3},{3}}, L(3,3,3));

        // --- large / performance cases (oracle + timing; max dims 10x10 per constraints, so build max-size + use oracle) ---
        // Max 10x10 dense -> verify exact order via oracle and time it.
        {
            Random rnd = new Random(42);
            int m = 10, n = 10;
            int[][] big = new int[m][n];
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++) big[i][j] = rnd.nextInt(201) - 100; // [-100,100]
            checkProp("max 10x10 random", big, 3000);
        }
        // Max 10x10 sequential numbers -> spiral over the full grid.
        {
            int m = 10, n = 10;
            int[][] big = new int[m][n];
            int v = 1;
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++) big[i][j] = v++;
            checkProp("max 10x10 sequential", big, 3000);
        }
        // Max width single row 1x10 and single col 10x1 random.
        {
            Random rnd = new Random(42);
            int[][] row = new int[1][10];
            for (int j = 0; j < 10; j++) row[0][j] = rnd.nextInt(201) - 100;
            checkProp("max 1x10 random row", row, 3000);
        }
        {
            Random rnd = new Random(42);
            int[][] col = new int[10][1];
            for (int i = 0; i < 10; i++) col[i][0] = rnd.nextInt(201) - 100;
            checkProp("max 10x1 random col", col, 3000);
        }
        // Repeated spiral calls to stress throughput (still timed under generous budget).
        {
            Random rnd = new Random(42);
            int m = 10, n = 10;
            int[][] big = new int[m][n];
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++) big[i][j] = rnd.nextInt(201) - 100;
            total++;
            try {
                List<Integer> expected = oracle(big);
                long t0 = System.nanoTime();
                boolean allOk = true;
                for (int it = 0; it < 50_000 && allOk; it++) {
                    List<Integer> a = new Answer().spiralOrder(copy(big));
                    if (a == null || !a.equals(expected)) allOk = false;
                }
                long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
                String label = "repeated 50k spiral calls (" + elapsedMs + " ms)";
                if (allOk && elapsedMs > 3000) {
                    System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> 3000 ms)");
                } else if (allOk) {
                    pass++;
                    System.out.println("\033[32m[PASS]\033[0m " + label);
                } else {
                    System.out.println("\033[31m[FAIL]\033[0m " + label + " | a call mismatched oracle");
                }
            } catch (Throwable t) {
                System.out.println("\033[31m[FAIL]\033[0m repeated 50k spiral calls | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
