import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // ===== Existing cases (kept) =====
        check("leetcode", Arrays.asList("leet","code"), true);   // example
        check("applepenapple", Arrays.asList("apple","pen"), true); // example: reuse apple
        check("catsandog", Arrays.asList("cats","dog","sand","and","cat"), false); // example
        check("a", Arrays.asList("a"), true);                      // single char in dict
        check("a", Arrays.asList("b"), false);                     // single char not in dict
        check("aaaa", Arrays.asList("a","aa"), true);              // multiple ways
        check("aaaaaaa", Arrays.asList("aaaa","aaa"), true);       // 3+4
        check("aaaaaaab", Arrays.asList("aaaa","aaa"), false);     // trailing b not coverable
        check("cars", Arrays.asList("car","ca","rs"), true);       // ca+rs
        check("abcd", Arrays.asList("a","abc","b","cd"), true);    // a+b+cd
        check("abcd", Arrays.asList("a","b","c"), false);          // missing d
        check("bb", Arrays.asList("a","b","bbb","bbbb"), true);    // b+b
        check("goalspecial", Arrays.asList("go","goal","goals","special"), true); // goal+special
        check("ab", Arrays.asList("a"), false);                    // partial coverage only
        check("aab", Arrays.asList("a","b"), true);                // a+a+b

        // ===== New corner cases =====
        check("z", Arrays.asList("a"), false);                     // single char, dict miss
        check("abcdefg", Arrays.asList("abc","defg","de"), true);  // abc+defg
        check("aaaaaaaaaa", Arrays.asList("a","aa","aaa"), true);  // heavy duplicates, many ways
        check("catsanddog", Arrays.asList("cat","cats","and","sand","dog"), true); // cats+and+dog
        check("pineapplepenapple", Arrays.asList("apple","pen","applepen","pine","pineapple"), true); // overlapping prefixes
        check("abcd", Arrays.asList("ab","cd","abc"), true);       // ab+cd
        check("aaaab", Arrays.asList("a","aa"), false);            // trailing b not in dict
        check("xxxxx", Arrays.asList("xx","xxx"), true);           // 2+3
        check("xxxx", Arrays.asList("xxx"), false);                // 4 not coverable by 3 only
        check("ababab", Arrays.asList("ab","abab"), true);         // alternating segments
        check("abc", Arrays.asList("abc"), true);                  // whole string is a single word
        check("abc", Arrays.asList("abcd"), false);                // dict word longer than s
        check("abcabc", Arrays.asList("abc"), true);               // exact repeats
        check("abcabca", Arrays.asList("abc"), false);             // one extra char
        check("b", Arrays.asList("a","b","c"), true);              // single char hit among many
        check("aaaaaaaaaaaaab", Arrays.asList("a","aa","aaa","aaaa"), false); // trailing b across long run
        check("leetcodeleet", Arrays.asList("leet","code"), true); // leet+code+leet

        // ===== Large / performance cases (pathological for naive O(2^n) recursion) =====
        // s = "a"*300 with prefix dict 1..10 of 'a's -> segmentable (true), forces DP.
        checkConstructed("a*300 segmentable", buildRepeat('a', 300), prefixDict('a', 10), true);
        // s = "a"*300 + "b" with NO 'b' word -> not segmentable (false): classic DP-killer for naive.
        checkConstructed("a*300+b unsegmentable", buildRepeat('a', 300) + "b", prefixDict('a', 10), false);
        // s = "a"*250 + "b" with the 'b' present at the end -> true.
        {
            List<String> d = prefixDict('a', 10);
            d.add("b");
            checkConstructed("a*250+b with b in dict", buildRepeat('a', 250) + "b", d, true);
        }
        // Long alternating "ab" repeated, dict {"ab"} -> true at max-ish length.
        checkConstructed("ab*150", buildRepeat2("ab", 150), Arrays.asList("ab"), true);
        // Same but one trailing 'a' -> false.
        checkConstructed("ab*150+a", buildRepeat2("ab", 150) + "a", Arrays.asList("ab"), false);

        // ===== NEW corner cases (constraint boundaries & shapes) =====
        // Constraint boundary: s.length == 1 (minimum) hit and miss already above; here word.length == 20 (max) exact match.
        check(buildRepeat('a', 20), Arrays.asList(buildRepeat('a', 20)), true);   // single 20-char word == s (max word len)
        // word.length == 20 word, s slightly longer/shorter than 20 -> false (no other coverage).
        check(buildRepeat('a', 21), Arrays.asList(buildRepeat('a', 20)), false);  // 21 'a's, only a 20-word -> false (off-by-one)
        check(buildRepeat('a', 19), Arrays.asList(buildRepeat('a', 20)), false);  // 19 'a's, only a 20-word -> false (too short)
        // All-equal chars, dict only has the exact-length word -> exact divisibility.
        check(buildRepeat('a', 12), Arrays.asList("aaa"), true);                  // 12 = 4*3 divisible -> true
        check(buildRepeat('a', 13), Arrays.asList("aaa"), false);                 // 13 not divisible by 3 -> false
        // Strictly increasing length words forming an alphabet run.
        check("abcdef", Arrays.asList("a","bc","def"), true);                     // 1+2+3 partition -> true
        check("abcdef", Arrays.asList("a","bc","de"), false);                     // leftover 'f' -> false
        // Alternating pattern with a competing decoy word that must NOT be used.
        check("abababa", Arrays.asList("aba","ab"), false);                       // greedy aba/ab cannot finish 7 -> false
        check("abababab", Arrays.asList("aba","ab"), true);                       // ab repeated 4x -> true
        // Heavy duplicates with only the long word present (forces specific split).
        check("aaaaaa", Arrays.asList("aaaaaa","aaaaa"), true);                   // whole string is the word -> true
        check("aaaaaaa", Arrays.asList("aaaaaa","aaaaa"), false);                 // 7 cannot be 6+? or 5+? -> false
        // Empty-leaning: shortest possible non-trivial with unmatched single char among multi-char dict.
        check("c", Arrays.asList("aa","bb","cc"), false);                         // 1-char s, all dict words 2-char -> false
        // No-solution despite rich dictionary (every prefix coverable except the tail).
        check("aaaaaaaaaab", Arrays.asList("a","aa","aaa","aaaa","aaaaa"), false);// long run then orphan 'b' -> false
        // Prefix trap: a long dict word matches a prefix but blocks completion; correct path uses short words.
        check("aaaaab", Arrays.asList("aaaaa","a","ab"), true);                   // a*4 + ab -> true (must avoid eating the 'b' wrong)
        // Word longer than entire string present alongside a valid short word.
        check("ab", Arrays.asList("abcde","a","b"), true);                        // a+b -> true (oversized word ignored)
        // Distinct alphabet, every char its own word -> always true.
        check("abcdefghij", oneCharDict("abcdefghij"), true);                     // each letter a word -> true
        // Same string but remove one needed letter from dict -> false.
        check("abcdefghij", oneCharDict("abcdefghi"), false);                     // 'j' missing -> false

        // ===== NEW large / performance cases (random, property-verified via O(n*L) oracle) =====
        // Random 300-char string over small alphabet with short random dict; verify decision matches oracle.
        runRandomProperty("rnd300-a", 300, 3, 12, 4);
        // Larger-than-constraint stress (n ~ 5000) to exercise true O(n*L) DP, oracle-verified.
        runRandomProperty("rnd5000", 5000, 4, 60, 5);
        // Big stress near 10^4 with longer words (length up to 20), oracle-verified for correctness + timing.
        runRandomProperty("rnd10000", 10000, 5, 120, 6);
        // Guaranteed-true construction: concatenation of random dict words at n ~ 8000 -> must be true.
        runConcatTrue("concat8000", 8000, 6, 5);
        // Guaranteed-true concatenation but inject one orphan char in the middle -> must be false.
        runConcatBrokenFalse("concatBroken8000", 8000, 6, 5);
        // Single-char alphabet at the absolute max s.length (300) with full prefix dict 1..20 -> true, fast DP.
        checkConstructed("a*300 fullPrefix20", buildRepeat('a', 300), prefixDict('a', 20), true);

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // ---------- builders ----------

    static String buildRepeat(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    static String buildRepeat2(String unit, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(unit);
        return sb.toString();
    }

    static List<String> prefixDict(char c, int maxLen) {
        List<String> d = new ArrayList<>();
        for (int len = 1; len <= maxLen; len++) d.add(buildRepeat(c, len));
        return d;
    }

    // One single-character word per distinct letter in the given string.
    static List<String> oneCharDict(String letters) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (int i = 0; i < letters.length(); i++) set.add(String.valueOf(letters.charAt(i)));
        return new ArrayList<>(set);
    }

    // ---------- independent O(n*L) oracle ----------
    // Standard 1D DP using a HashSet of words; independent reference for property verification.
    static boolean oracle(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String w : words) maxLen = Math.max(maxLen, w.length());
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int i = 1; i <= n; i++) {
            int lo = Math.max(0, i - maxLen);
            for (int j = i - 1; j >= lo; j--) {
                if (dp[j] && words.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
    }

    // ---------- random property runners ----------

    // Generate a random string and a random dictionary, then assert Answer matches the oracle decision.
    static void runRandomProperty(String label, int n, int alphabet, int dictSize, int maxWordLen) {
        total++;
        try {
            Random rng = new Random(42);
            String s = randomString(rng, n, alphabet);
            List<String> dict = randomDict(rng, dictSize, alphabet, Math.min(maxWordLen, 20));
            boolean expected = oracle(s, dict);
            long start = System.nanoTime();
            boolean actual = new Answer().wordBreak(s, new ArrayList<>(dict));
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m rnd " + label + " (len=" + n + ") oracle=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m rnd " + label + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m rnd " + label + " (len=" + n + ") = " + actual + " matches oracle (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m rnd " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Build a long string by concatenating random dict words: result MUST be segmentable (true).
    static void runConcatTrue(String label, int approxLen, int alphabet, int maxWordLen) {
        total++;
        try {
            Random rng = new Random(42);
            List<String> dict = randomDict(rng, 8, alphabet, Math.min(maxWordLen, 20));
            // ensure non-empty dict with at least one non-empty word
            StringBuilder sb = new StringBuilder();
            while (sb.length() < approxLen) {
                sb.append(dict.get(rng.nextInt(dict.size())));
            }
            String s = sb.toString();
            boolean expected = true; // by construction it is a concatenation of dict words
            // sanity: oracle must agree
            boolean oracleSays = oracle(s, dict);
            long start = System.nanoTime();
            boolean actual = new Answer().wordBreak(s, new ArrayList<>(dict));
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (!oracleSays) {
                // builder logic error guard (should never happen) -> count as fail to surface it
                System.out.println("\033[31m[FAIL]\033[0m concat " + label + " harness bug: oracle disagrees with construction");
            } else if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m concat " + label + " (len=" + s.length() + ") expected=true actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m concat " + label + " too slow: " + elapsedMs + " ms");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m concat " + label + " (len=" + s.length() + ") = true (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m concat " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // Concatenate random dict words, then inject an orphan char that no word covers -> MUST be false.
    static void runConcatBrokenFalse(String label, int approxLen, int alphabet, int maxWordLen) {
        total++;
        try {
            Random rng = new Random(42);
            // Use alphabet of 'a'.. (alphabet-1) for the dict; reserve a fresh char 'z' as the orphan.
            List<String> dict = randomDict(rng, 8, alphabet, Math.min(maxWordLen, 20));
            StringBuilder sb = new StringBuilder();
            while (sb.length() < approxLen) {
                sb.append(dict.get(rng.nextInt(dict.size())));
            }
            // insert an orphan 'z' (never in dict, since dict alphabet starts at 'a'..'a'+alphabet-1 < 'z') in the middle.
            int mid = sb.length() / 2;
            sb.insert(mid, 'z');
            String s = sb.toString();
            boolean expected = false; // orphan char cannot be covered
            boolean oracleSays = oracle(s, dict);
            long start = System.nanoTime();
            boolean actual = new Answer().wordBreak(s, new ArrayList<>(dict));
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (oracleSays) {
                System.out.println("\033[31m[FAIL]\033[0m concatBroken " + label + " harness bug: oracle says segmentable but orphan injected");
            } else if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m concatBroken " + label + " (len=" + s.length() + ") expected=false actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m concatBroken " + label + " too slow: " + elapsedMs + " ms");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m concatBroken " + label + " (len=" + s.length() + ") = false (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m concatBroken " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ---------- random generators (alphabet over 'a'..'a'+alphabet-1) ----------

    static String randomString(Random rng, int n, int alphabet) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append((char) ('a' + rng.nextInt(alphabet)));
        return sb.toString();
    }

    static List<String> randomDict(Random rng, int size, int alphabet, int maxWordLen) {
        if (maxWordLen < 1) maxWordLen = 1;
        LinkedHashSet<String> set = new LinkedHashSet<>();
        int guard = 0;
        while (set.size() < size && guard < size * 20 + 50) {
            guard++;
            int len = 1 + rng.nextInt(maxWordLen);
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) sb.append((char) ('a' + rng.nextInt(alphabet)));
            set.add(sb.toString());
        }
        // dictionary must be non-empty per constraints
        if (set.isEmpty()) set.add(String.valueOf((char) ('a')));
        return new ArrayList<>(set);
    }

    // ---------- core checks ----------

    static void check(String s, List<String> dict, boolean expected) {
        total++;
        try {
            boolean actual = new Answer().wordBreak(s, new ArrayList<>(dict));
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m wordBreak(\"" + briefStr(s) + "\", " + briefDict(dict) + ") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m wordBreak(\"" + briefStr(s) + "\", " + briefDict(dict) + ") expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m wordBreak(\"" + briefStr(s) + "\", " + briefDict(dict) + ") expected=" + expected + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static void checkConstructed(String label, String s, List<String> dict, boolean expected) {
        total++;
        try {
            long start = System.nanoTime();
            boolean actual = new Answer().wordBreak(s, new ArrayList<>(dict));
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (actual != expected) {
                System.out.println("\033[31m[FAIL]\033[0m perf " + label + " (len=" + s.length() + ") expected=" + expected + " actual=" + actual + " (" + elapsedMs + " ms)");
            } else if (elapsedMs > 3000) {
                System.out.println("\033[31m[FAIL]\033[0m perf " + label + " too slow: " + elapsedMs + " ms (possible exponential)");
            } else {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m perf " + label + " (len=" + s.length() + ") = " + actual + " (" + elapsedMs + " ms)");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m perf " + label + " threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static String briefStr(String s) {
        if (s.length() <= 30) return s;
        return s.substring(0, 12) + "...(len=" + s.length() + ")";
    }

    static String briefDict(List<String> dict) {
        if (dict.size() <= 8) return dict.toString();
        return "[size=" + dict.size() + "]";
    }
}