import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    static void check(String name, String s, String t, boolean expected) {
        total++;
        try {
            boolean actual = new Answer().backspaceCompare(s, t);
            if (actual == expected) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | s=\"" + brief(s) + "\", t=\"" + brief(t) + "\""
                    + " | expected=" + expected + " | actual=" + actual);
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                + " | s=\"" + brief(s) + "\", t=\"" + brief(t) + "\""
                + " | expected=" + expected
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Independent oracle: apply backspaces with a StringBuilder used as a stack.
    static String typed(String x) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < x.length(); i++) {
            char c = x.charAt(i);
            if (c == '#') { if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1); }
            else sb.append(c);
        }
        return sb.toString();
    }

    // Large/timed variant: expected derived from the oracle, flags catastrophic slowness.
    static void checkTimed(String name, String s, String t) {
        total++;
        boolean expected = typed(s).equals(typed(t));
        long t0 = System.nanoTime();
        try {
            boolean actual = new Answer().backspaceCompare(s, t);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = (actual == expected) && ms <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + ms + " ms)");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                    + " | s.len=" + s.length() + ", t.len=" + t.length()
                    + " | expected=" + expected + " | actual=" + actual
                    + (ms > 3000 ? " | too slow" : ""));
            }
        } catch (Throwable e) {
            long ms = (System.nanoTime() - t0) / 1_000_000;
            System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                + " | s.len=" + s.length() + ", t.len=" + t.length()
                + " | expected=" + expected
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static String brief(String s) {
        if (s == null) return "null";
        if (s.length() <= 40) return s;
        return s.substring(0, 20) + "...(" + s.length() + ")...";
    }

    public static void main(String[] args) {
        // ---------- Existing cases (kept) ----------
        // Examples from QUESTION.md
        check("example 1 (ab#c vs ad#c)", "ab#c", "ad#c", true);
        check("example 2 (both empty)", "ab##", "c#d#", true);
        check("example 3 (c vs b)", "a#c", "b", false);

        // Corner: no backspaces, equal
        check("no backspaces equal", "abc", "abc", true);

        // Corner: no backspaces, unequal
        check("no backspaces unequal", "abc", "abd", false);

        // Corner: backspace on empty stays empty
        check("leading backspaces both empty", "#", "#", true);
        check("many backspaces on empty", "###", "#", true);

        // Corner: backspaces fully clear one side only
        check("one becomes empty other not", "a#", "a", false);
        check("both fully cleared different lengths", "abc###", "x#", true);

        // Corner: backspace at start does nothing
        check("leading backspace ignored", "#a#b#c", "c", true); // "#a#b#c" -> "c" == "c"
        check("leading backspace then chars", "#abc", "abc", true);

        // Corner: trailing backspace removes last typed char
        check("trailing backspace", "ab#", "a", true);

        // Corner: consecutive backspaces
        check("consecutive backspaces", "abcd##", "ab", true);
        check("consecutive backspaces unequal", "abcd##", "abc", false);

        // Corner: backspaces produce same result via different paths
        check("different paths same result", "xywrrmp", "xywrrmu#p", true);

        // Corner: only backspaces vs single char
        check("only backspaces vs char", "####", "a", false);

        // Corner: equal single characters
        check("single equal", "y", "y", true);
        check("single unequal", "y", "z", false);

        // Corner: a longer matching pair with interior deletions
        check("interior deletions match", "bxj##tw", "bxo#j##tw", true);
        check("interior deletions mismatch", "bxj##tw", "bxj###tw", false);

        // ---------- New corner cases ----------
        // Both are single '#': both empty -> equal
        check("both single hash", "#", "#", true);
        // One side a single char, other a single '#': "a" vs "" -> not equal
        check("char vs hash", "a", "#", false);
        // Many backspaces clearing a long string vs an empty-producing string
        check("clear long vs clear short", "abcdef######", "z#", true);
        // Backspace exactly cancels each char -> empty, vs another empty
        check("cancel each vs empty produce", "a#b#c#", "x#y#", true);
        // Heavy '#' interleaving that nets the same surviving text
        check("interleaved hashes match", "a##c", "#a#c", true);
        // Result differs only in one surviving char
        check("one char differs after edits", "ab#c", "ab#d", false);
        // All characters identical, no backspaces, long
        check("all same long equal", "aaaaaaaaaa", "aaaaaaaaaa", true);
        // All identical but different lengths -> unequal
        check("all same different length", "aaaaa", "aaaa", false);
        // Backspaces reduce a longer to match a shorter exactly
        check("reduce to match", "abcde##", "abc", true);
        // Trailing backspace on both deletes the differing last char -> both "ab"
        check("trailing backspace both", "abc#", "abx#", true);
        // Surviving prefixes equal after differing deletions
        check("differing deletions same survivor", "ppppp#####q", "q", true);
        // One string all backspaces, other empty-equivalent
        check("all hashes vs cancel pair", "######", "a#", true);
        // Single char survives on each, equal
        check("single survivor equal", "zz#", "z", true);
        // Single char survives on each, unequal
        check("single survivor unequal", "zz#", "y", false);
        // Backspace right after a char repeatedly -> nothing types, both empty
        check("type-delete loops both empty", "a#a#a#a#", "b#b#b#", true);
        // Same survivor "ace" reached via different leading edits
        check("repeated blocks same survivor", "ab#cd#ef#", "a#ab#cd#ef#", true);

        // ---------- Large / performance cases (oracle-verified, generous time budget) ----------
        // Constraint upper bound is length 200. We build strings at and beyond that bound to
        // expose any accidental quadratic behavior (the optimal solution scans from the end in O(n)).
        Random rnd = new Random(42);

        // Case A: two strings near the max length (200) that both reduce to the same text.
        {
            // base text typed normally, plus matched "type-then-#" noise that cancels out.
            StringBuilder a = new StringBuilder();
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 60; i++) {
                char c = (char) ('a' + rnd.nextInt(26));
                a.append(c);
                b.append(c);
            }
            // append canceling noise (each char immediately deleted) of differing amounts
            for (int i = 0; i < 30; i++) a.append((char) ('a' + rnd.nextInt(26))).append('#');
            for (int i = 0; i < 35; i++) b.append((char) ('a' + rnd.nextInt(26))).append('#');
            checkTimed("large ~200 same after canceling noise", a.toString(), b.toString());
        }

        // Case B: two long strings that differ in exactly one surviving character.
        {
            StringBuilder a = new StringBuilder();
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 90; i++) {
                char c = (char) ('a' + rnd.nextInt(26));
                a.append(c);
                b.append(c);
            }
            // flip the last surviving char of b
            b.setCharAt(b.length() - 1, (char) ('a' + ((b.charAt(b.length() - 1) - 'a' + 1) % 26)));
            checkTimed("large differ one char", a.toString(), b.toString());
        }

        // Case C: pathological '#'-heavy strings (mostly backspaces) of substantial length.
        // Built beyond the stated bound purely to stress complexity; correctness via oracle.
        {
            StringBuilder a = new StringBuilder();
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 5000; i++) {
                // type a char then sometimes delete it
                char c = (char) ('a' + rnd.nextInt(3));
                a.append(c);
                if (rnd.nextBoolean()) a.append('#');
                b.append(c);
                if (rnd.nextBoolean()) b.append('#');
            }
            checkTimed("large hash-heavy random", a.toString(), b.toString());
        }

        // Case D: both strings collapse entirely to empty via long backspace tails.
        {
            StringBuilder a = new StringBuilder();
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 2000; i++) { a.append((char) ('a' + rnd.nextInt(26))); }
            for (int i = 0; i < 2000; i++) a.append('#');
            for (int i = 0; i < 1500; i++) { b.append((char) ('a' + rnd.nextInt(26))); }
            for (int i = 0; i < 1500; i++) b.append('#');
            checkTimed("large both collapse to empty", a.toString(), b.toString());
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
