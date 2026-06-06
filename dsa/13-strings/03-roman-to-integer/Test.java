import java.util.*;

public class Test {
    static int pass = 0;
    static int total = 0;

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check("III", 3);
        check("LVIII", 58);
        check("MCMXCIV", 1994);

        // Single symbols (all seven) (existing)
        check("I", 1);
        check("V", 5);
        check("X", 10);
        check("L", 50);
        check("C", 100);
        check("D", 500);
        check("M", 1000);

        // All six subtractive pairs (existing)
        check("IV", 4);
        check("IX", 9);
        check("XL", 40);
        check("XC", 90);
        check("CD", 400);
        check("CM", 900);

        // Additive runs (existing)
        check("II", 2);
        check("VIII", 8);
        check("XX", 20);
        check("XXX", 30);
        check("CCC", 300);
        check("MMM", 3000);

        // Mixed / boundary cases (existing)
        check("XLII", 42);
        check("XCIX", 99);
        check("CDXLIV", 444);
        check("CMXCIX", 999);
        check("MMXXIV", 2024);
        check("MMMCMXCIX", 3999);          // maximum value
        check("DCCXLIX", 749);
        check("MMMDCCXLIX", 3749);
        check("MCDLXXVI", 1476);
        check("MMDCCCLXXXVIII", 2888);     // many additive symbols

        // ---- NEW corner cases ----
        check("IV", 4);                    // minimum subtractive
        check("VI", 6);                    // additive after V
        check("VII", 7);                   // additive run after V
        check("XIV", 14);                  // subtractive inside
        check("XIX", 19);                  // two subtractive-ish parts
        check("XL", 40);                   // boundary forty
        check("XLIX", 49);                 // 40 + 9, two subtractives
        check("XCIV", 94);                 // 90 + 4
        check("CDXLIV", 444);              // 400 + 40 + 4 all subtractive
        check("CMXCIX", 999);             // 900 + 90 + 9 all subtractive
        check("MMMCMXCIX", 3999);         // max again via explicit construction
        check("MMMDCCCLXXXVIII", 3888);   // longest additive-heavy near max (15 chars)
        check("MCMLXXXIV", 1984);         // classic year
        check("MMXXV", 2025);             // current-ish year
        check("DCCCXC", 890);             // 800 + 90
        check("CDXCIX", 499);             // 400 + 90 + 9
        check("MMMM".substring(0, 3), 3000); // "MMM" via substring, additive max thousands
        check("LXVI", 66);                // 50 + 10 + 5 + 1
        check("DLV", 555);                // 500 + 50 + 5
        check("MMCDXXI", 2421);          // mixed mid-range

        // Property cross-check against an independent reference for ALL valid
        // values 1..3999 (the entire guaranteed input domain). This both adds
        // thorough coverage and acts as the scale/stress test for this problem,
        // since |s| is capped at 15 there is no huge-string regime.
        long start = System.nanoTime();
        int mism = 0;
        for (int n = 1; n <= 3999; n++) {
            String roman = encode(n);
            try {
                int got = new Answer().romanToInt(roman);
                if (got != n) mism++;
            } catch (Throwable e) {
                mism = -1; // mark unimplemented / crash
                break;
            }
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        total++;
        if (mism == 0 && elapsedMs <= 3000) {
            pass++;
            System.out.println("\033[32m[PASS]\033[0m full-domain 1..3999 round-trip (" + elapsedMs + " ms)");
        } else if (mism == 0) {
            System.out.println("\033[31m[FAIL]\033[0m full-domain 1..3999 round-trip too slow: " + elapsedMs + " ms");
        } else if (mism < 0) {
            System.out.println("\033[31m[FAIL]\033[0m full-domain 1..3999 round-trip threw / unimplemented");
        } else {
            System.out.println("\033[31m[FAIL]\033[0m full-domain 1..3999 round-trip: " + mism + " mismatches (" + elapsedMs + " ms)");
        }

        // Repeated-call performance: parse the max-length numeral many times to
        // expose any per-call quadratic behaviour. 200k * 15 chars stays fast.
        String maxNumeral = "MMMDCCCLXXXVIII"; // 3888, 15 chars (max length)
        long start2 = System.nanoTime();
        long acc = 0;
        boolean ok = true;
        try {
            for (int i = 0; i < 200000; i++) acc += new Answer().romanToInt(maxNumeral);
        } catch (Throwable e) {
            ok = false;
        }
        long elapsed2 = (System.nanoTime() - start2) / 1_000_000;
        total++;
        if (ok && acc == 3888L * 200000L && elapsed2 <= 3000) {
            pass++;
            System.out.println("\033[32m[PASS]\033[0m 200k repeated max-length parses (" + elapsed2 + " ms)");
        } else if (!ok) {
            System.out.println("\033[31m[FAIL]\033[0m 200k repeated max-length parses threw / unimplemented");
        } else if (acc != 3888L * 200000L) {
            System.out.println("\033[31m[FAIL]\033[0m 200k repeated max-length parses wrong sum: " + acc + " (" + elapsed2 + " ms)");
        } else {
            System.out.println("\033[31m[FAIL]\033[0m 200k repeated max-length parses too slow: " + elapsed2 + " ms");
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Independent reference: integer -> roman, used only to generate inputs whose
    // expected integer value is known.
    static String encode(int num) {
        int[] vals = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] syms = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            while (num >= vals[i]) { sb.append(syms[i]); num -= vals[i]; }
        }
        return sb.toString();
    }

    static void check(String s, int expected) {
        total++;
        try {
            int actual = new Answer().romanToInt(s);
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m romanToInt(\"" + s + "\") = " + actual);
            } else {
                System.out.println("\033[31m[FAIL]\033[0m romanToInt(\"" + s + "\") expected " + expected + " but got " + actual);
            }
        } catch (Throwable e) {
            System.out.println("\033[31m[FAIL]\033[0m romanToInt(\"" + s + "\") threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
