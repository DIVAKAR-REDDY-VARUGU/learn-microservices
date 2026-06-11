import java.util.*;

// JUnit-free test harness for Answer.findAnagrams(String s, String p)
// Find All Anagrams in a String. Answer may be in ANY order, so normalize (sort) before compare.
// Compile: javac Answer.java Test.java   Run: java Test
public class Test {

    static int pass = 0;
    static int total = 0;

    static List<Integer> norm(List<Integer> in) {
        List<Integer> copy = new ArrayList<>(in);
        Collections.sort(copy);
        return copy;
    }

    static List<Integer> list(int... xs) {
        List<Integer> l = new ArrayList<>();
        for (int x : xs) l.add(x);
        return l;
    }

    static void check(String name, String s, String p, List<Integer> expected) {
        total++;
        try {
            List<Integer> actual = new Answer().findAnagrams(s, p);
            List<Integer> aNorm = actual == null ? null : norm(actual);
            List<Integer> eNorm = norm(oracle(s, p)); // validate vs the correct oracle (several hardcoded expecteds were wrong)
            if (aNorm != null && aNorm.equals(eNorm)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                        + " | s=\"" + briefS(s) + "\" p=\"" + briefS(p) + "\""
                        + " | expected(sorted)=" + briefL(eNorm) + " actual=" + briefL(actual));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | s=\"" + briefS(s) + "\" p=\"" + briefS(p) + "\""
                    + " | expected(sorted)=" + briefL(norm(expected)) + " threw "
                    + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Large/perf case: verify against an independent O(n*26) sliding-frequency oracle and time it.
    static void checkProp(String name, String s, String p) {
        total++;
        try {
            long t0 = System.nanoTime();
            List<Integer> actual = new Answer().findAnagrams(s, p);
            long t1 = System.nanoTime();
            double ms = (t1 - t0) / 1e6;
            List<Integer> expected = oracle(s, p);
            String label = name + " [" + String.format("%.1f", ms) + " ms]";
            if (ms > 3000.0) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | TOO SLOW (>3000ms) possible quadratic");
            } else if (actual != null && norm(actual).equals(norm(expected))) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (matches=" + expected.size() + ")");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label
                        + " | expected size=" + expected.size()
                        + " actual=" + (actual == null ? "null" : ("size=" + actual.size())));
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | s.len=" + s.length() + " p.len=" + p.length()
                    + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Independent O(n*26) oracle for anagram start indices.
    static List<Integer> oracle(String s, String p) {
        List<Integer> res = new ArrayList<>();
        int n = s.length(), m = p.length();
        if (m > n) return res;
        int[] need = new int[26];
        int[] win = new int[26];
        for (int i = 0; i < m; i++) need[p.charAt(i) - 'a']++;
        for (int i = 0; i < n; i++) {
            win[s.charAt(i) - 'a']++;
            if (i >= m) win[s.charAt(i - m) - 'a']--;
            if (i >= m - 1 && Arrays.equals(win, need)) res.add(i - m + 1);
        }
        return res;
    }

    static String briefS(String s) {
        if (s.length() <= 30) return s;
        return "[len=" + s.length() + "]";
    }

    static String briefL(List<Integer> l) {
        if (l == null) return "null";
        if (l.size() <= 20) return l.toString();
        return "[size=" + l.size() + "]";
    }

    public static void main(String[] args) {
        // Provided examples
        check("example1", "cbaebabacd", "abc", list(0, 6));
        check("example2 overlapping", "abab", "ab", list(0, 1, 2));
        check("example3 no anagram", "af", "be", list());

        // Corner: p longer than s -> no possible window
        check("p longer than s", "a", "ab", list());
        // Corner: equal length, exact anagram match at 0
        check("equal length match", "ab", "ba", list(0));
        // Corner: equal length, no match
        check("equal length no match", "ab", "cd", list());
        // Corner: s equals p exactly
        check("s equals p", "abc", "abc", list(0));

        // Corner: single char p, multiple occurrences
        check("single char p", "aXaXa".toLowerCase(), "a", list(0, 2, 4));
        check("single char p simple", "aaaa", "a", list(0, 1, 2, 3));

        // Corner: all same char, p of length 2
        check("all same len2", "aaaa", "aa", list(0, 1, 2));
        // Corner: all same char but p has different char -> none
        check("all same wrong p", "aaaa", "ab", list());

        // Corner: anagram only at the end
        check("match at end", "xyzabc", "cab", list(3));
        // Corner: anagram only at the start
        check("match at start", "cabxyz", "abc", list(0));

        // Corner: multiple non-overlapping matches
        check("multiple matches", "abccba", "abc", list(0, 1, 2, 3));

        // Corner: duplicates in p must match counts exactly
        check("dup counts in p", "baa", "aab", list(0));
        check("dup counts not enough", "aba", "aab", list());

        // Corner: window with extra char between matches
        check("extra between", "abacbabc", "abc", list(1, 3, 5));

        // Corner: p length equals full s but reversed
        check("full s reversed", "hello", "olleh", list(0));

        // Corner: no matches anywhere in longer string
        check("none in longer", "abcdefg", "xyz", list());

        // ===== ADDED CORNER CASES =====

        // Boundary: both strings length 1, match
        check("len1 match", "a", "a", list(0));
        // Boundary: both strings length 1, no match
        check("len1 no match", "a", "b", list());

        // p equals s, single char repeated
        check("s==p single char", "zzzz", "zzzz", list(0));

        // Every position is a match (s all same, p all same shorter)
        check("all same many windows", "aaaaa", "aaa", list(0, 1, 2));

        // Full alphabet anagram, exactly one window
        check("full alphabet once", "abcdefghijklmnopqrstuvwxyz", "zyxwvutsrqponmlkjihgfedcba", list(0));
        // Full alphabet present twice contiguous -> matches at 0 and 26
        check("alphabet twice",
                "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz",
                "qwertyuiopasdfghjklzxcvbnm", list(0, 26));

        // Heavy duplicates: p needs specific counts, s has shifting surplus
        check("dup heavy shift", "aaabbbaaabbb", "aaabbb", list(0, 6));
        check("dup heavy partial", "aaabbb", "aabbb", list());

        // Match at the very last possible window only
        check("only last window", "zzzzab", "ba", list(4));
        // Match at the very first window only
        check("only first window", "abzzzz", "ba", list(0));

        // Alternating chars, p length 2 -> every window matches
        check("alternating len2", "ababab", "ab", list(0, 1, 2, 3, 4));
        // Alternating, p length 3 needs 2 of one char -> no match
        check("alternating len3 mismatch", "ababab", "abc", list());

        // Off-by-one: p exactly the size of s minus surrounding noise
        check("off by one tail", "xabc", "abc", list(1));
        check("off by one head", "abcx", "abc", list(0));

        // One missing letter prevents all matches
        check("missing letter", "aabbccdd", "abce", list());

        // Consecutive overlapping anagrams of length 3
        check("overlap len3", "abcabc", "cba", list(0, 1, 2, 3));

        // p uses a letter that never appears in s
        check("letter never in s", "aaaaaa", "aaz", list());

        // ===== LARGE / PERFORMANCE CASES (verified by independent oracle) =====
        Random rnd = new Random(42);

        // Large s of all 'a', p of all 'a' length 100 -> dense matches (n - m + 1)
        int n1 = 30000;
        StringBuilder sbA = new StringBuilder();
        for (int i = 0; i < n1; i++) sbA.append('a');
        StringBuilder pA = new StringBuilder();
        for (int i = 0; i < 100; i++) pA.append('a');
        checkProp("large all-a dense matches", sbA.toString(), pA.toString());

        // Large random over small alphabet (a-d) -> frequent matches, stresses freq compares
        int n2 = 30000;
        StringBuilder sbR = new StringBuilder();
        for (int i = 0; i < n2; i++) sbR.append((char) ('a' + rnd.nextInt(4)));
        String pR = "abcd";
        checkProp("large small-alphabet random", sbR.toString(), pR);

        // Large random over full alphabet, longer p -> sparse/no matches
        int n3 = 30000;
        StringBuilder sbF = new StringBuilder();
        for (int i = 0; i < n3; i++) sbF.append((char) ('a' + rnd.nextInt(26)));
        StringBuilder pF = new StringBuilder();
        for (int i = 0; i < 50; i++) pF.append((char) ('a' + rnd.nextInt(26)));
        checkProp("large full-alphabet sparse", sbF.toString(), pF.toString());

        // Large p nearly equal to s length (few windows) -> exercises edge window math at scale
        int n4 = 30000;
        StringBuilder sbE = new StringBuilder();
        for (int i = 0; i < n4; i++) sbE.append((char) ('a' + rnd.nextInt(5)));
        // p is a shuffled copy of a prefix of length n4-1 (guaranteed >=1 match at index 0 only if anagram)
        StringBuilder pE = new StringBuilder(sbE.substring(0, n4 - 1));
        for (int i = pE.length() - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            char tmp = pE.charAt(i); pE.setCharAt(i, pE.charAt(j)); pE.setCharAt(j, tmp);
        }
        checkProp("large p near s-length", sbE.toString(), pE.toString());

        // Large worst-case for naive re-sort: s and p share a common prefix forcing many near-matches
        int n5 = 30000;
        StringBuilder sbW = new StringBuilder();
        for (int i = 0; i < n5; i++) sbW.append((char) ('a' + (i % 2))); // ababab...
        checkProp("large alternating p=ab", sbW.toString(), "ab");
        checkProp("large alternating p=aab", sbW.toString(), "aab");

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
