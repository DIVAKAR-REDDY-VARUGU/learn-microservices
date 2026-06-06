import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check(3749, "MMMDCCXLIX");
        check(58, "LVIII");
        check(1994, "MCMXCIV");

        // Boundaries (existing)
        check(1, "I");                  // minimum
        check(3999, "MMMCMXCIX");       // maximum

        // Single base symbols (existing)
        check(5, "V");
        check(10, "X");
        check(50, "L");
        check(100, "C");
        check(500, "D");
        check(1000, "M");

        // All six subtractive forms (existing)
        check(4, "IV");
        check(9, "IX");
        check(40, "XL");
        check(90, "XC");
        check(400, "CD");
        check(900, "CM");

        // Additive runs / repeats (existing)
        check(3, "III");
        check(8, "VIII");
        check(30, "XXX");
        check(300, "CCC");
        check(3000, "MMM");

        // Mixed cases (existing)
        check(42, "XLII");
        check(99, "XCIX");
        check(444, "CDXLIV");
        check(999, "CMXCIX");
        check(2024, "MMXXIV");
        check(749, "DCCXLIX");
        check(1476, "MCDLXXVI");
        check(2888, "MMDCCCLXXXVIII");
        check(14, "XIV");
        check(19, "XIX");
        check(2421, "MMCDXXI");

        // ---- NEW corner cases ----
        check(2, "II");                 // smallest additive run
        check(6, "VI");                 // 5 + 1
        check(7, "VII");               // 5 + 1 + 1
        check(11, "XI");               // 10 + 1
        check(13, "XIII");             // 10 + 3
        check(44, "XLIV");            // 40 + 4, two subtractive forms
        check(49, "XLIX");            // 40 + 9
        check(94, "XCIV");            // 90 + 4
        check(449, "CDXLIX");         // 400 + 40 + 9
        check(494, "CDXCIV");         // 400 + 90 + 4
        check(888, "DCCCLXXXVIII");   // additive-heavy (12 chars)
        check(3888, "MMMDCCCLXXXVIII"); // longest output, 15 chars
        check(1984, "MCMLXXXIV");     // classic year
        check(2025, "MMXXV");         // year
        check(890, "DCCCXC");         // 800 + 90
        check(499, "CDXCIX");         // 400 + 90 + 9
        check(40, "XL");              // boundary forty again
        check(400, "CD");             // boundary four hundred
        check(3994, "MMMCMXCIV");     // near-max with subtractives
        check(3000, "MMM");           // max thousands additive

        // Round-trip property: intToRoman matches a known reference for 1..400 (existing)
        for (int n = 1; n <= 400; n++) {
            check(n, reference(n));
        }

        // ---- FULL-DOMAIN + PERFORMANCE: verify EVERY value 1..3999 ----
        // The entire valid input domain is small, so we exhaustively cross-check
        // against the independent reference and, additionally, verify the output
        // round-trips back to the input via an independent decoder (a strong
        // property check that does not depend on exact string form). Timed.
        long start = System.nanoTime();
        int refMism = 0;
        int rtMism = 0;
        boolean crashed = false;
        for (int n = 1; n <= 3999; n++) {
            try {
                String got = new Answer().intToRoman(n);
                if (!reference(n).equals(got)) refMism++;
                if (decode(got) != n) rtMism++;
            } catch (Throwable e) {
                crashed = true;
                break;
            }
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        total++;
        if (!crashed && refMism == 0 && rtMism == 0 && elapsedMs <= 3000) {
            pass++;
            System.out.println("\033[32m[PASS]\033[0m full-domain 1..3999 (ref + round-trip) (" + elapsedMs + " ms)");
        } else if (crashed) {
            System.out.println("\033[31m[FAIL]\033[0m full-domain 1..3999 threw / unimplemented");
        } else if (refMism != 0) {
            System.out.println("\033[31m[FAIL]\033[0m full-domain 1..3999 reference mismatches: " + refMism + " (" + elapsedMs + " ms)");
        } else if (rtMism != 0) {
            System.out.println("\033[31m[FAIL]\033[0m full-domain 1..3999 round-trip mismatches: " + rtMism + " (" + elapsedMs + " ms)");
        } else {
            System.out.println("\033[31m[FAIL]\033[0m full-domain 1..3999 too slow: " + elapsedMs + " ms");
        }

        // Repeated-call performance: encode the max value many times.
        long start2 = System.nanoTime();
        boolean ok = true;
        long lenAcc = 0;
        try {
            for (int i = 0; i < 200000; i++) lenAcc += new Answer().intToRoman(3888).length();
        } catch (Throwable e) {
            ok = false;
        }
        long elapsed2 = (System.nanoTime() - start2) / 1_000_000;
        total++;
        if (ok && lenAcc == 15L * 200000L && elapsed2 <= 3000) {
            pass++;
            System.out.println("\033[32m[PASS]\033[0m 200k repeated max-value encodes (" + elapsed2 + " ms)");
        } else if (!ok) {
            System.out.println("\033[31m[FAIL]\033[0m 200k repeated max-value encodes threw / unimplemented");
        } else if (lenAcc != 15L * 200000L) {
            System.out.println("\033[31m[FAIL]\033[0m 200k repeated max-value encodes wrong length sum: " + lenAcc + " (" + elapsed2 + " ms)");
        } else {
            System.out.println("\033[31m[FAIL]\033[0m 200k repeated max-value encodes too slow: " + elapsed2 + " ms");
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent reference implementation for cross-checking.
    static String reference(int num) {
        int[] vals = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] syms = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            while (num >= vals[i]) { sb.append(syms[i]); num -= vals[i]; }
        }
        return sb.toString();
    }

    // Independent decoder roman -> int, for the round-trip property check.
    static int decode(String s) {
        Map<Character, Integer> m = new HashMap<>();
        m.put('I', 1); m.put('V', 5); m.put('X', 10); m.put('L', 50);
        m.put('C', 100); m.put('D', 500); m.put('M', 1000);
        int total = 0;
        for (int i = 0; i < s.length(); i++) {
            int cur = m.get(s.charAt(i));
            if (i + 1 < s.length() && cur < m.get(s.charAt(i + 1))) total -= cur;
            else total += cur;
        }
        return total;
    }

    static void check(int num, String expected) {
        total++;
        try {
            String actual = new Answer().intToRoman(num);
            if (expected.equals(actual)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m intToRoman(" + num + ") = \"" + actual + "\"");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m intToRoman(" + num + ") expected \"" + expected + "\" but got \"" + actual + "\"");
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m intToRoman(" + num + ") threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
