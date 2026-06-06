import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static String label(String s) {
        if (s.length() <= 40) return s;
        return "<len=" + s.length() + ">";
    }

    static void check(String input, String expected) {
        total++;
        try {
            String actual = new Answer().decodeString(input);
            if (expected.equals(actual)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m decodeString(\"" + label(input) + "\")");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m decodeString(\"" + label(input) + "\") | expected=\"" + label(expected) + "\" actual=\"" + (actual == null ? "null" : label(actual)) + "\"");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m decodeString(\"" + label(input) + "\") | expected=\"" + label(expected) + "\" threw: " + t);
        }
    }

    static String repeat(String s, int k) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < k; i++) sb.append(s);
        return sb.toString();
    }

    // Independent recursive oracle decoder (single left-to-right pass with an index cursor).
    static int[] pos = new int[1];
    static String oracleDecode(String s) {
        pos[0] = 0;
        return decodeFrom(s);
    }
    static String decodeFrom(String s) {
        StringBuilder sb = new StringBuilder();
        while (pos[0] < s.length()) {
            char c = s.charAt(pos[0]);
            if (c == ']') {
                break; // let caller consume the ']'
            } else if (Character.isDigit(c)) {
                int k = 0;
                while (pos[0] < s.length() && Character.isDigit(s.charAt(pos[0]))) {
                    k = k * 10 + (s.charAt(pos[0]) - '0');
                    pos[0]++;
                }
                // current char is '['
                pos[0]++; // consume '['
                String inner = decodeFrom(s);
                pos[0]++; // consume ']'
                for (int i = 0; i < k; i++) sb.append(inner);
            } else {
                sb.append(c);
                pos[0]++;
            }
        }
        return sb.toString();
    }

    // Build a valid random encoded string within the length cap and a small decoded size.
    // Returns {encoded, decoded}.
    static int gDepth;
    static String[] buildRandom(Random rnd, int budget) {
        gDepth = 0;
        StringBuilder enc = new StringBuilder();
        StringBuilder dec = new StringBuilder();
        gen(rnd, enc, dec, budget);
        return new String[]{enc.toString(), dec.toString()};
    }
    // Generate a sequence of plain letters and k[...] blocks; keep decoded length bounded.
    static void gen(Random rnd, StringBuilder enc, StringBuilder dec, int budget) {
        int items = 1 + rnd.nextInt(3);
        for (int it = 0; it < items; it++) {
            int choice = rnd.nextInt(3);
            if (choice == 0 || gDepth > 4 || dec.length() > budget) {
                // plain letters
                int len = 1 + rnd.nextInt(3);
                for (int i = 0; i < len; i++) {
                    char c = (char) ('a' + rnd.nextInt(26));
                    enc.append(c);
                    dec.append(c);
                }
            } else {
                int k = 1 + rnd.nextInt(3);
                enc.append(k).append('[');
                StringBuilder innerDec = new StringBuilder();
                gDepth++;
                // recurse into a sub-builder for the inner decoded text
                StringBuilder innerEnc = new StringBuilder();
                gen(rnd, innerEnc, innerDec, Math.max(1, budget / Math.max(1, k)));
                gDepth--;
                enc.append(innerEnc);
                enc.append(']');
                for (int r = 0; r < k; r++) dec.append(innerDec);
            }
        }
    }

    static void checkLarge(String name, String input, String expected) {
        total++;
        try {
            long t0 = System.nanoTime();
            String actual = new Answer().decodeString(input);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = expected.equals(actual) && ms <= 3000;
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " (decoded len=" + (actual == null ? -1 : actual.length()) + ", " + ms + " ms)");
            } else {
                String adesc = (actual == null) ? "null" : ("len=" + actual.length());
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected len=" + expected.length() + " actual " + adesc + " match=" + expected.equals(actual) + " (" + ms + " ms" + (ms > 3000 ? ", OVER BUDGET" : "") + ")");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw: " + t);
        }
    }

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("3[a]2[bc]", "aaabcbc");
        check("3[a2[c]]", "accaccacc");
        check("2[abc]3[cd]ef", "abcabccdcdcdef");
        // No encoding — plain letters (single char)
        check("a", "a");
        check("abc", "abc");
        // Single repetition
        check("1[a]", "a");
        // Multi-digit count
        check("10[a]", repeat("a", 10));
        check("12[z]", repeat("z", 12));
        // Repeat a multi-char block
        check("2[abc]", "abcabc");
        // Nested triple
        check("2[2[2[a]]]", repeat("a", 8));
        // Trailing plain letters after a block
        check("2[a]bc", "aabc");
        // Leading plain letters before a block
        check("ab2[c]", "abcc");
        // Letters surrounding a block
        check("a2[b]c", "abbc");
        // Nested with sibling content
        check("3[a]2[b]", "aaabb");
        // Nested where inner has multiple chars
        check("2[a2[bc]]", "abcbcabcbc");
        // Adjacent nested blocks
        check("2[ab3[c]]", "abcccabccc");

        // ===== ADDED: more corner cases =====
        // Minimum length string (single letter) already covered; single digit-less plain max-ish letters
        check("abcdefghij", "abcdefghij");
        // Repeat count 1 with multi-char
        check("1[abc]", "abc");
        // Repeat empty-effect via nested 1's
        check("1[1[1[a]]]", "a");
        // Two-level nesting with siblings inside
        check("2[a2[b]c]", "abbcabbc");
        // Multi-digit at the outer level
        check("20[a]", repeat("a", 20));
        // Multi-digit nested
        check("2[3[a]]", repeat("a", 6));
        // Block followed immediately by another block (no separator)
        check("2[a]3[b]", "aabbb");
        // Letters between two blocks
        check("2[a]z3[b]", "aazbbb");
        // Deep single-letter nesting (depth 4)
        check("2[2[2[2[a]]]]", repeat("a", 16));
        // Inner has trailing letters after nested block
        check("3[a2[c]d]", "accdaccdaccd");
        // Outer letters, then nested, then outer letters
        check("xy2[p3[q]]z", "xypqqqpqqqz");
        // Repeat count exactly hitting a 2-digit boundary value
        check("11[x]", repeat("x", 11));
        // Whole string is one big block
        check("5[ab]", "ababababab");
        // Nested where both levels are multi-char
        check("2[ab2[cd]ef]", "abcdcdefabcdcdef");
        // Single block producing a longer decoded string
        check("9[z]", repeat("z", 9));
        // Validate a hand string against the oracle
        check("2[ab3[c]]", oracleDecode("2[ab3[c]]"));
        check("3[a2[c]]", oracleDecode("3[a2[c]]"));

        // ===== ADDED: random valid inputs vs oracle =====
        Random rnd = new Random(42);
        {
            long t0 = System.nanoTime();
            boolean allOk = true;
            String firstBad = null;
            for (int t = 0; t < 500; t++) {
                String[] pair = buildRandom(rnd, 200);
                String enc = pair[0];
                if (enc.isEmpty()) continue;
                String exp = pair[1];
                // sanity: oracle agrees with our constructed decoded text
                String oexp = oracleDecode(enc);
                if (!oexp.equals(exp)) exp = oexp; // trust the oracle
                String act;
                try { act = new Answer().decodeString(enc); } catch (Throwable th) { act = null; }
                if (!exp.equals(act)) { allOk = false; firstBad = enc; break; }
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            total++;
            if (allOk && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m 500 random encoded strings vs oracle (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m random encoded vs oracle | firstBad=\"" + firstBad + "\" (" + ms + " ms)");
            }
        }

        // ===== ADDED: large / performance / scale =====
        // The input string s itself is constrained to length <= 30, but the DECODED
        // output may be up to ~10^5. These cases keep the source short while forcing
        // a large decoded result, exercising efficient string building.

        // Big single repeat: "100000[a]" -> 100000 'a's. Source length = 9 (<= 30).
        {
            String src = "100000[a]";
            String exp = repeat("a", 100000);
            checkLarge("large flat 100000[a] (src len=" + src.length() + ")", src, exp);
        }
        // Nested multiplication reaching ~10^5 with a very short source.
        // 10[10[10[10[10[a]]]]] -> a repeated 10^5 times. Source length is small.
        {
            String src = "10[10[10[10[10[a]]]]]";
            String exp = repeat("a", 100000);
            checkLarge("large nested 10^5 (src len=" + src.length() + ")", src, exp);
        }
        // Nested with a multi-char inner: 5[5[5[ab]]] -> (ab)*125 -> 250 chars, short source,
        // verify against oracle (exact string identity matters here).
        {
            String src = "5[5[5[ab]]]";
            checkLarge("nested multi-char 5[5[5[ab]]]", src, oracleDecode(src));
        }
        // Large via 2-digit multi-level: 50[50[ab]] -> (ab)*2500 -> 5000 chars. Source short.
        {
            String src = "50[50[ab]]";
            checkLarge("large 50[50[ab]] (src len=" + src.length() + ")", src, oracleDecode(src));
        }
        // Source at/under the 30-char cap mixing several blocks; verify against oracle.
        {
            String src = "2[ab]3[cd]4[ef]5[gh]ij"; // length 22 <= 30
            checkLarge("mixed multi-block (src len=" + src.length() + ")", src, oracleDecode(src));
        }
        // Big repeat of a 2-char block: 9999[ab] -> 19998 chars. Source length 8 (<= 30).
        {
            String src = "9999[ab]";
            checkLarge("large 9999[ab] (src len=" + src.length() + ")", src, oracleDecode(src));
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
