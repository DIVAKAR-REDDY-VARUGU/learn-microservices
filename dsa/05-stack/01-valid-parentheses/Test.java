import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static String label(String s) {
        if (s.length() <= 40) return s;
        return "<len=" + s.length() + ">";
    }

    static void check(String input, boolean expected) {
        total++;
        try {
            boolean actual = new Answer().isValid(input);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m isValid(\"" + label(input) + "\")");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m isValid(\"" + label(input) + "\") | expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m isValid(\"" + label(input) + "\") | expected=" + expected + " threw: " + t);
        }
    }

    // Independent O(n) oracle stack implementation used for large/random property checks.
    static boolean oracle(String s) {
        Deque<Character> st = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == '[' || c == '{') {
                st.push(c);
            } else {
                if (st.isEmpty()) return false;
                char o = st.pop();
                if (c == ')' && o != '(') return false;
                if (c == ']' && o != '[') return false;
                if (c == '}' && o != '{') return false;
            }
        }
        return st.isEmpty();
    }

    // Build a guaranteed-valid balanced string of length 2*pairs using random nesting.
    static String buildBalanced(int pairs, Random rnd) {
        char[][] kinds = {{'(', ')'}, {'[', ']'}, {'{', '}'}};
        StringBuilder sb = new StringBuilder();
        Deque<Character> open = new ArrayDeque<>();
        int remaining = pairs;
        // Greedy: at each step either open (if budget left) or close (if something open).
        while (remaining > 0 || !open.isEmpty()) {
            boolean canOpen = remaining > 0;
            boolean canClose = !open.isEmpty();
            boolean doOpen;
            if (canOpen && canClose) doOpen = rnd.nextBoolean();
            else doOpen = canOpen;
            if (doOpen) {
                char[] k = kinds[rnd.nextInt(3)];
                sb.append(k[0]);
                open.push(k[1]);
                remaining--;
            } else {
                sb.append(open.pop());
            }
        }
        return sb.toString();
    }

    static void checkLarge(String label, String input, boolean expected) {
        total++;
        try {
            long t0 = System.nanoTime();
            boolean actual = new Answer().isValid(input);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = (actual == expected) && ms <= 3000;
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | expected=" + expected + " actual=" + actual + " (" + ms + " ms" + (ms > 3000 ? ", OVER BUDGET" : "") + ")");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + label + " | expected=" + expected + " threw: " + t);
        }
    }

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("()[]{}", true);
        check("(]", false);
        check("([{}])", true);
        // Basic valid pairs
        check("()", true);
        check("[]", true);
        check("{}", true);
        check("{[]}", true);
        // Wrong order / interleaved
        check("([)]", false);
        check("(}", false);
        check("{(])}", false);
        // Single character (min length 1) — always unbalanced
        check("(", false);
        check(")", false);
        check("[", false);
        check("]", false);
        check("{", false);
        check("}", false);
        // Only opens
        check("(((", false);
        check("((((((((((", false);
        // Only closes
        check(")))", false);
        // Extra trailing open
        check("()(", false);
        // Extra leading close
        check(")()", false);
        // Close before any open
        check("]()", false);
        // Deeply nested valid
        check("((((((((((()))))))))))", true);
        // Long valid mixed
        check("{[()()]}{}[]", true);
        // Long invalid mixed (one wrong type)
        check("{[()()]}{}[)", false);
        // Same type many
        check("()()()()", true);

        // ===== ADDED: more corner cases =====
        // Mismatched same-direction pair types
        check("(]", false);
        check("[}", false);
        check("{)", false);
        // Closing type mismatch on otherwise-balanced nesting
        check("([{}]]", false);
        check("([{}})", false);
        // Two valid pairs but second closer wrong order
        check("()]", false);
        check("()}", false);
        // All three types nested correctly
        check("{[()]}", true);
        check("([{}])()[]{}", true);
        // Open count exceeds close by one (unbalanced tail)
        check("(()", false);
        check("{[}", false);
        // Close count exceeds open by one (extra close)
        check("())", false);
        check("[]]", false);
        // Alternating opens then alternating closes in WRONG order -> false
        check("([{)]}", false);
        // Alternating opens then closes in CORRECT (reverse) order -> true
        check("([{}])", true);
        // Long run of identical valid pairs
        check("(((((((((())))))))))", true);
        // Long run of identical valid pairs of one type repeated side by side
        check("[][][][][][]", true);
        // Balanced length-2 of each type
        check("()", true);
        check("{}{}{}{}{}{}{}{}{}{}", true);
        // Single mismatch buried deep in a long valid prefix
        check("(((((((((())))))))))(", false);
        // Reversed brackets (close-then-open) never valid
        check(")(", false);
        check("][", false);
        check("}{", false);
        // Even length but fundamentally crossed
        check("([)]([)]", false);

        // Deterministic constructed deep nesting near a sizable depth (still small).
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 500; i++) sb.append('(');
            for (int i = 0; i < 500; i++) sb.append(')');
            check(sb.toString(), true);
            // Break it: flip last char to wrong type.
            sb.setCharAt(sb.length() - 1, ']');
            check(sb.toString(), false);
        }

        // ===== ADDED: large / performance / scale =====
        Random rnd = new Random(42);

        // Large guaranteed-valid balanced string near upper bound (<= 10^4 chars).
        {
            String big = buildBalanced(5000, rnd); // 10000 chars exactly
            // Property: oracle agrees it is valid.
            checkLarge("large random balanced (len=" + big.length() + ") expect VALID", big, oracle(big));
        }

        // Large valid then corrupt one bracket near the end -> must become invalid.
        {
            String big = buildBalanced(5000, rnd);
            char[] arr = big.toCharArray();
            // Find a closing bracket and change it to a different closing type.
            for (int i = arr.length - 1; i >= 0; i--) {
                char c = arr[i];
                if (c == ')') { arr[i] = ']'; break; }
                if (c == ']') { arr[i] = '}'; break; }
                if (c == '}') { arr[i] = ')'; break; }
            }
            String corrupt = new String(arr);
            checkLarge("large corrupted balanced (len=" + corrupt.length() + ")", corrupt, oracle(corrupt));
        }

        // Large deeply nested single-type valid string near the bound.
        {
            int depth = 5000;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; i++) sb.append('(');
            for (int i = 0; i < depth; i++) sb.append(')');
            checkLarge("large deep nest (len=" + sb.length() + ") expect VALID", sb.toString(), true);
        }

        // Large string of all opens — invalid, must stay fast (no quadratic scan).
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) sb.append('(');
            checkLarge("large all-opens (len=" + sb.length() + ") expect INVALID", sb.toString(), false);
        }

        // Large random mix of all six characters; compare against oracle (any answer).
        {
            char[] alphabet = {'(', ')', '[', ']', '{', '}'};
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) sb.append(alphabet[rnd.nextInt(6)]);
            String s = sb.toString();
            checkLarge("large random mix (len=" + s.length() + ") vs oracle", s, oracle(s));
        }

        // Many independent random small strings vs oracle in one timed batch.
        {
            long t0 = System.nanoTime();
            boolean allOk = true;
            char[] alphabet = {'(', ')', '[', ']', '{', '}'};
            for (int t = 0; t < 2000; t++) {
                int len = 1 + rnd.nextInt(20);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < len; i++) sb.append(alphabet[rnd.nextInt(6)]);
                String s = sb.toString();
                boolean exp = oracle(s);
                boolean act;
                try { act = new Answer().isValid(s); } catch (Throwable th) { act = !exp; }
                if (act != exp) { allOk = false; break; }
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            total++;
            if (allOk && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m 2000 random strings vs oracle (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m 2000 random strings vs oracle | allMatched=" + allOk + " (" + ms + " ms)");
            }
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
