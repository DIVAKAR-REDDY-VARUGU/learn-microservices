import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    // reverseString is an in-place void method: call it, then assert on the mutated array.
    static void check(String name, char[] input, char[] expected) {
        total++;
        char[] original = Arrays.copyOf(input, input.length);
        char[] work = Arrays.copyOf(input, input.length);
        try {
            new Answer().reverseString(work);
            if (Arrays.equals(work, expected)) {
                System.out.println("\033[32m[PASS]\033[0m " + name);
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name
                    + " | input=" + brief(original)
                    + " | expected=" + brief(expected)
                    + " | actual=" + brief(work));
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m " + name
                + " | input=" + brief(original)
                + " | expected=" + brief(expected)
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Independent oracle: the reverse of the input array.
    static char[] reversed(char[] a) {
        char[] r = new char[a.length];
        for (int i = 0; i < a.length; i++) r[a.length - 1 - i] = a[i];
        return r;
    }

    // Large/timed variant: validates against the oracle (property: result == reverse(input))
    // and flags catastrophic slowness.
    static void checkTimed(String name, char[] input) {
        total++;
        char[] expected = reversed(input);
        char[] work = Arrays.copyOf(input, input.length);
        long t0 = System.nanoTime();
        try {
            new Answer().reverseString(work);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = Arrays.equals(work, expected) && ms <= 3000;
            if (ok) {
                System.out.println("\033[32m[PASS]\033[0m " + name + " (" + ms + " ms)");
                pass++;
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                    + " | n=" + input.length
                    + (ms > 3000 ? " | too slow" : " | result is not the reverse of input"));
            }
        } catch (Throwable e) {
            long ms = (System.nanoTime() - t0) / 1_000_000;
            System.out.println("\033[31m[FAIL]\033[0m " + name + " (" + ms + " ms)"
                + " | n=" + input.length
                + " | threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    static String brief(char[] a) {
        if (a.length <= 40) return Arrays.toString(a);
        return "[len=" + a.length + " " + a[0] + "," + a[1] + ",...," + a[a.length - 1] + "]";
    }

    public static void main(String[] args) {
        // ---------- Existing cases (kept) ----------
        // Examples from QUESTION.md
        check("example 1 (hello)",
            new char[]{'h','e','l','l','o'}, new char[]{'o','l','l','e','h'});
        check("example 2 (Hannah)",
            new char[]{'H','a','n','n','a','h'}, new char[]{'h','a','n','n','a','H'});
        check("example 3 (single)",
            new char[]{'A'}, new char[]{'A'});

        // Corner: single character (already minimal)
        check("single char", new char[]{'z'}, new char[]{'z'});

        // Corner: two characters
        check("two chars", new char[]{'a','b'}, new char[]{'b','a'});

        // Corner: even length
        check("even length", new char[]{'a','b','c','d'}, new char[]{'d','c','b','a'});

        // Corner: odd length (middle stays put)
        check("odd length", new char[]{'1','2','3','4','5'}, new char[]{'5','4','3','2','1'});

        // Corner: all equal characters (reversal is identical)
        check("all equal", new char[]{'x','x','x','x'}, new char[]{'x','x','x','x'});

        // Corner: palindrome input (reversal equals input)
        check("palindrome", new char[]{'a','b','a'}, new char[]{'a','b','a'});

        // Corner: printable ASCII symbols and digits
        check("symbols and digits", new char[]{'!','@','#','1','2'}, new char[]{'2','1','#','@','!'});

        // Corner: spaces included
        check("with spaces", new char[]{'a',' ','b',' ','c'}, new char[]{'c',' ','b',' ','a'});

        // Corner: mixed case
        check("mixed case", new char[]{'A','b','C','d'}, new char[]{'d','C','b','A'});

        // Corner: large array
        int n = 10000;
        char[] big = new char[n];
        char[] bigExpected = new char[n];
        for (int i = 0; i < n; i++) {
            char c = (char) ('a' + (i % 26));
            big[i] = c;
            bigExpected[n - 1 - i] = c;
        }
        check("large array", big, bigExpected);

        // ---------- New corner cases ----------
        // Two equal characters (reversal identical)
        check("two equal", new char[]{'q','q'}, new char[]{'q','q'});
        // Two different digits
        check("two digits", new char[]{'9','0'}, new char[]{'0','9'});
        // Even-length palindrome (reversal identical)
        check("even palindrome", new char[]{'a','b','b','a'}, new char[]{'a','b','b','a'});
        // Odd-length palindrome (reversal identical)
        check("odd palindrome", new char[]{'r','a','c','e','c','a','r'},
            new char[]{'r','a','c','e','c','a','r'});
        // Strictly ascending letters
        check("ascending letters", new char[]{'a','b','c','d','e','f','g'},
            new char[]{'g','f','e','d','c','b','a'});
        // Strictly descending digits
        check("descending digits", new char[]{'9','8','7','6','5'},
            new char[]{'5','6','7','8','9'});
        // Alternating two characters, even length
        check("alternating even", new char[]{'a','b','a','b'}, new char[]{'b','a','b','a'});
        // Alternating two characters, odd length (middle fixed)
        check("alternating odd", new char[]{'a','b','a','b','a'}, new char[]{'a','b','a','b','a'});
        // All spaces
        check("all spaces", new char[]{' ',' ',' '}, new char[]{' ',' ',' '});
        // Punctuation mix
        check("punctuation mix", new char[]{'.',',','!','?',';'}, new char[]{';','?','!',',','.'});
        // Boundary printable ASCII: space (32) and tilde (126)
        check("ascii bounds", new char[]{' ','~',' ','~'}, new char[]{'~',' ','~',' '});
        // Single space
        check("single space", new char[]{' '}, new char[]{' '});
        // Single tilde (max printable)
        check("single tilde", new char[]{'~'}, new char[]{'~'});
        // Numbers and letters interleaved
        check("interleaved alnum", new char[]{'a','1','b','2','c','3'},
            new char[]{'3','c','2','b','1','a'});
        // Repeated block with a distinct center (odd)
        check("block with center", new char[]{'x','x','y','x','x'},
            new char[]{'x','x','y','x','x'});

        // ---------- Large / performance cases (oracle-verified, generous time budget) ----------
        // Constraint upper bound is s.length == 10^5. The optimal converging two-pointer swap
        // is O(n); these expose any accidental quadratic behavior.
        Random rnd = new Random(42);

        // Case A: maximum length (10^5), even length, random printable ASCII (32..126).
        {
            int sz = 100000;
            char[] arr = new char[sz];
            for (int i = 0; i < sz; i++) arr[i] = (char) (32 + rnd.nextInt(95)); // 32..126
            checkTimed("large n=100000 random printable", arr);
        }

        // Case B: maximum length minus one (odd length) so the middle element stays fixed.
        {
            int sz = 99999;
            char[] arr = new char[sz];
            for (int i = 0; i < sz; i++) arr[i] = (char) ('a' + rnd.nextInt(26));
            checkTimed("large n=99999 odd length", arr);
        }

        // Case C: maximum length all-identical -> reversal must equal input.
        {
            int sz = 100000;
            char[] arr = new char[sz];
            Arrays.fill(arr, 'k');
            checkTimed("large n=100000 all identical", arr);
        }

        // Case D: maximum length palindrome -> reversal equals input (built by mirroring).
        {
            int half = 50000;
            char[] arr = new char[2 * half];
            for (int i = 0; i < half; i++) {
                char c = (char) ('a' + rnd.nextInt(26));
                arr[i] = c;
                arr[2 * half - 1 - i] = c;
            }
            checkTimed("large n=100000 palindrome", arr);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
