import java.util.*;

// JUnit-free harness for Answer.exist(char[][], String). Plain boolean comparison.
public class Test {
    static int total = 0, pass = 0;

    public static void main(String[] args) {
        char[][] board = {
            {'A', 'B', 'C', 'E'},
            {'S', 'F', 'C', 'S'},
            {'A', 'D', 'E', 'E'}
        };
        // ----- Existing cases (kept) -----
        // Example 1
        check(board, "ABCCED", true);
        // Example 2
        check(board, "SEE", true);
        // Example 3
        check(board, "ABCB", false);
        // Single matching letter
        check(board, "A", true);
        // Single non-matching letter
        check(board, "Z", false);
        // Word longer than total cells -> impossible
        check(board, "ABCESFDABCESEEEXTRA", false);
        // Cannot reuse a cell: 'AA' would need two distinct adjacent A cells (none adjacent here)
        check(board, "AA", false);
        // 1x1 grid match
        check(new char[][]{{'a'}}, "a", true);
        // 1x1 grid no match
        check(new char[][]{{'a'}}, "b", false);
        // Single row, full traversal
        check(new char[][]{{'a', 'b', 'c', 'd'}}, "abcd", true);
        // Single column, reverse traversal
        check(new char[][]{{'x'}, {'y'}, {'z'}}, "zyx", true);
        // Snake path through whole grid
        char[][] snake = {
            {'a', 'b'},
            {'d', 'c'}
        };
        check(snake, "abcd", true);   // (0,0)->(0,1)->(1,1)->(1,0)
        // Prefix exists but full word does not
        check(snake, "abca", false);
        // Repeated letters requiring distinct cells (valid path)
        char[][] grid = {
            {'a', 'a'},
            {'a', 'a'}
        };
        check(grid, "aaa", true);
        check(grid, "aaaa", true);
        check(grid, "aaaaa", false); // only 4 cells, can't reuse

        // ----- New corner cases -----
        // Diagonal adjacency is NOT allowed: B (0,1) and C (1,2)... craft a no-diagonal case
        char[][] diag = {
            {'a', 'b'},
            {'c', 'd'}
        };
        check(diag, "ad", false);  // a(0,0) and d(1,1) are diagonal -> not adjacent
        check(diag, "ab", true);   // horizontally adjacent
        check(diag, "ac", true);   // vertically adjacent
        // Word must backtrack through a fork
        char[][] fork = {
            {'a', 'b', 'e'},
            {'x', 'c', 'd'}
        };
        check(fork, "bcd", true);   // b(0,1)->c(1,1)->d(1,2)
        check(fork, "abe", true);   // a(0,0)->b(0,1)->e(0,2)
        check(fork, "abx", false);  // b is not adjacent to x
        // Full spiral covering a 3x3 grid (uses every cell exactly once)
        char[][] g3 = {
            {'a', 'b', 'c'},
            {'h', 'i', 'd'},
            {'g', 'f', 'e'}
        };
        check(g3, "abcdefghi", true);  // spiral inward
        check(g3, "abcdefghix", false); // 10 chars, only 9 cells
        // L-shaped run of a's: top row (3) + right column below (2) = 5 distinct a-cells
        char[][] trap = {
            {'a', 'a', 'a'},
            {'b', 'b', 'a'},
            {'c', 'c', 'a'}
        };
        check(trap, "aaaaa", true);     // a(0,0..2) then a(1,2),a(2,2)
        check(trap, "aaaaaa", false);   // only 5 a-cells exist, can't reuse
        // Word length 15 (max constraint) on a 6x6 grid filled with a single repeated path
        char[][] big = new char[6][6];
        for (int r = 0; r < 6; r++) for (int c = 0; c < 6; c++) big[r][c] = (char) ('a' + ((r * 6 + c) % 26));
        // Build a known boustrophedon path word of length 15 and assert it exists
        check(big, boustrophedonWord(big, 15), true);
        // First char not present anywhere -> false fast
        check(board, "Q", false);
        // Word equals a full single row
        check(board, "ABCE", true);
        // Word equals a full single column (top to bottom)
        check(board, "ASA", true);  // col 0: A,S,A
        // Reuse needed but impossible (single cell, length 2)
        check(new char[][]{{'a'}}, "aa", false);
        // Empty-ish: 6x6 all same letter, word of 15 same letters exists (no reuse, 36 cells)
        char[][] allx = new char[6][6];
        for (char[] row : allx) Arrays.fill(row, 'x');
        check(allx, repeat('x', 15), true);
        check(allx, repeat('x', 36), true);   // exactly fills the grid via Hamiltonian path
        check(allx, repeat('x', 37), false);  // more than 36 cells, must reuse -> impossible
        // Property: any word the method says exists must be reconstructable (we trust the example set)
        check(allx, repeat('y', 1), false);

        // ----- Scale / performance cases -----
        // Worst case for backtracking: 6x6 all 'a' searching a 15-long 'a' word that DOES exist.
        scaleCheck("scale 6x6 all-a, len-15 exists", allBoard(6, 'a'), repeat('a', 15), true);
        // 6x6 all 'a' searching a max-length (15) word whose final char is absent: the DFS must
        // explore many self-avoiding a-paths up to depth 15 before failing -> stresses pruning,
        // but the bounded depth keeps the OPTIMAL solution fast (a quadratic/no-prune one will blow up).
        scaleCheck("scale 6x6 all-a, len-15 absent tail", allBoard(6, 'a'), repeat('a', 14) + "z", false);
        // Random 6x6 board (seed 42) over a tiny alphabet, word guaranteed absent -> exhaustive search timed.
        scaleRandomAbsent("scale random 6x6 absent word");

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    static char[][] allBoard(int n, char ch) {
        char[][] b = new char[n][n];
        for (char[] row : b) Arrays.fill(row, ch);
        return b;
    }

    static String repeat(char ch, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(ch);
        return sb.toString();
    }

    // Build a word of length len that follows a row-by-row boustrophedon (snake) path,
    // which is always a valid adjacency path that reuses no cell.
    static String boustrophedonWord(char[][] b, int len) {
        int m = b.length, n = b[0].length;
        StringBuilder sb = new StringBuilder();
        outer:
        for (int r = 0; r < m; r++) {
            if (r % 2 == 0) {
                for (int c = 0; c < n; c++) { sb.append(b[r][c]); if (sb.length() == len) break outer; }
            } else {
                for (int c = n - 1; c >= 0; c--) { sb.append(b[r][c]); if (sb.length() == len) break outer; }
            }
        }
        return sb.toString();
    }

    static void check(char[][] board, String word, boolean expected) {
        total++;
        try {
            boolean actual = new Answer().exist(deepCopy(board), word);
            if (actual == expected) {
                System.out.println("\033[32m[PASS]\033[0m exist(" + dim(board) + ", \"" + word + "\") = " + actual);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m exist(" + dim(board) + ", \"" + word + "\")"
                    + " expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m exist(" + dim(board) + ", \"" + word + "\") threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Scale case: same boolean assertion but timed against a generous budget.
    static void scaleCheck(String name, char[][] board, String word, boolean expected) {
        total++;
        try {
            long t0 = System.nanoTime();
            boolean actual = new Answer().exist(deepCopy(board), word);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            if (actual == expected && elapsedMs <= 3000) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " = " + actual + " elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=" + expected + " actual=" + actual
                    + " elapsed=" + elapsedMs + "ms (budget 3000ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Random 6x6 board over {a,b} and a word whose final letter never appears -> must be false.
    static void scaleRandomAbsent(String name) {
        total++;
        try {
            Random rnd = new Random(42);
            char[][] b = new char[6][6];
            for (int r = 0; r < 6; r++)
                for (int c = 0; c < 6; c++)
                    b[r][c] = rnd.nextBoolean() ? 'a' : 'b';
            // word of all 'a' then a 'z' that is not on the board -> guaranteed absent,
            // forcing the search to explore many paths before failing.
            String word = repeat('a', 14) + "z";

            long t0 = System.nanoTime();
            boolean actual = new Answer().exist(deepCopy(b), word);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            if (!actual && elapsedMs <= 3000) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " = false elapsed=" + elapsedMs + "ms");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " expected=false actual=" + actual
                    + " elapsed=" + elapsedMs + "ms (budget 3000ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " threw "
                + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Defensive copy so a solution that mutates the board in place can't taint later cases.
    static char[][] deepCopy(char[][] b) {
        char[][] c = new char[b.length][];
        for (int i = 0; i < b.length; i++) c[i] = Arrays.copyOf(b[i], b[i].length);
        return c;
    }

    static String dim(char[][] b) {
        return b.length + "x" + (b.length > 0 ? b[0].length : 0) + " grid";
    }
}
