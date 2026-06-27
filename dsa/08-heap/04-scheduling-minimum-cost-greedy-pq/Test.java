import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ---- existing cases (kept) ----
        check("doc example 1", "AAABBB", 2, 8);
        check("doc example 2 (no idle)", "ACABDB", 1, 6);
        check("doc example 3", "AAABBB", 3, 10);

        check("n=0 -> length (single)", "A", 0, 1);
        check("n=0 -> length (many)", "AAABBB", 0, 6);
        check("single task, large n", "A", 100, 1);
        check("all same, n=1", "AAAA", 1, 7);          // A _ A _ A _ A
        check("all distinct", "ABCDEF", 2, 6);
        check("two tasks alternating", "AABB", 2, 5);   // A B _ A B  (5 intervals)
        check("dominant task forces idle", "AAAAB", 2, 10); // A B _ A _ _ A _ _ A  (10 intervals)
        check("enough fillers, no idle", "AAABBBCCC", 2, 9);
        check("ties among tops", "AABBCC", 2, 6);       // perfect fill
        check("single repeated pair small gap", "AB", 0, 2);

        // ---- NEW corner cases ----
        check("single task n=0", "A", 0, 1);
        check("two of same, n=0", "AA", 0, 2);
        check("two of same, large n", "AA", 100, 102);   // A + 100 idle + A
        check("all same length 1, max n", "A", 100, 1);
        check("all distinct, n=0", "ABCDEFGHIJ", 0, 10);
        check("all distinct, large n (no idle, all unique)", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", 100, 26);
        check("single most-frequent dominates, n=2", "AAAABBCC", 2, 10); // 3*(A)+... formula
        check("three-way tie at top, n=2", "AAABBBCCC", 2, 9);
        check("max gap with single repeated", "AAA", 100, 203); // A +100idle+ A +100idle+ A
        check("heavy duplicates one letter n=3", "AAAAA", 3, 17); // (5-1)*4 + 1
        check("two tops plus fillers exactly fit", "AAABBBCC", 2, 8); // A B C A B C A B (8)
        check("frequent pair tight gap n=1", "AABB", 1, 4); // A B A B
        check("long run alternating, n=1", "ABABAB", 1, 6);
        check("dominant with idle, n=4", "AAAB", 4, 11); // A B _ _ _ A _ _ _ _ A
        check("all 26 letters once, n=25", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", 25, 26);
        check("two letters, one dominant huge gap", "AAAAB", 3, 13); // (4-1)*4 +1 ... check via formula

        // ---- LARGE / PERFORMANCE / SCALE cases (closed-form oracle, timed) ----
        // constraints: tasks.length <= 10^4, n in [0,100].
        largeRandom("large random tasks=10000 n=0", 10000, 0);
        largeRandom("large random tasks=10000 n=1", 10000, 1);
        largeRandom("large random tasks=10000 n=50", 10000, 50);
        largeRandom("large random tasks=10000 n=100", 10000, 100);
        largeSkewed("large skewed (one dominant) tasks=10000 n=100", 10000, 100);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // closed-form oracle for Task Scheduler:
    // answer = max( tasks.length, (maxFreq - 1) * (n + 1) + (# of letters that reach maxFreq) )
    static int oracle(char[] tasks, int n) {
        int[] cnt = new int[26];
        for (char c : tasks) cnt[c - 'A']++;
        int maxFreq = 0;
        for (int v : cnt) maxFreq = Math.max(maxFreq, v);
        int numMax = 0;
        for (int v : cnt) if (v == maxFreq) numMax++;
        int frame = (maxFreq - 1) * (n + 1) + numMax;
        return Math.max(tasks.length, frame);
    }

    static void check(String label, String taskStr, int n, int expected) {
        total++;
        char[] tasks = taskStr.toCharArray();
        try {
            int actual = new Answer().leastInterval(tasks.clone(), n);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + " | tasks=" + Arrays.toString(tasks) + ", n=" + n
                        + " | expected=" + expected
                        + " | actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label
                    + " | tasks=" + Arrays.toString(tasks) + ", n=" + n
                    + " | expected=" + expected
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // verify against the closed-form oracle (unambiguous: the minimum interval count
    // is a single integer), build a large input, and time the solution.
    static void largeRandom(String label, int len, int n) {
        total++;
        try {
            Random rnd = new Random(42);
            char[] tasks = new char[len];
            for (int i = 0; i < len; i++) tasks[i] = (char) ('A' + rnd.nextInt(26));
            int expected = oracle(tasks, n);
            long t0 = System.nanoTime();
            int actual = new Answer().leastInterval(tasks.clone(), n);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lab = label + " (len=" + len + ") [" + elapsedMs + " ms]";
            boolean ok = (actual == expected) && elapsedMs <= 3000;
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + lab);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + lab
                        + " | expected=" + expected + " | actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // one letter dominates -> forces many idle slots (exercises the idle path at scale)
    static void largeSkewed(String label, int len, int n) {
        total++;
        try {
            Random rnd = new Random(42);
            char[] tasks = new char[len];
            // ~80% letter 'A', the rest a few other letters
            for (int i = 0; i < len; i++) {
                tasks[i] = (rnd.nextInt(100) < 80) ? 'A' : (char) ('B' + rnd.nextInt(3));
            }
            int expected = oracle(tasks, n);
            long t0 = System.nanoTime();
            int actual = new Answer().leastInterval(tasks.clone(), n);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String lab = label + " (len=" + len + ") [" + elapsedMs + " ms]";
            boolean ok = (actual == expected) && elapsedMs <= 3000;
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + lab);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + lab
                        + " | expected=" + expected + " | actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }
}
