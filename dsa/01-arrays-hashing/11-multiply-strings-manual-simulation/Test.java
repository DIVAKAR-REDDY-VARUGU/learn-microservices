import java.util.*;
import java.math.BigInteger;

public class Test {
    static int pass = 0, total = 0;

    static void check(String name, String num1, String num2, String expected) {
        total++;
        try {
            String actual = new Answer().multiply(num1, num2);
            if (expected.equals(actual)) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> \"" + brief(actual) + "\"");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | num1=\"" + brief(num1) + "\" num2=\"" + brief(num2)
                        + "\" | expected=\"" + brief(expected) + "\" | actual=\"" + (actual == null ? "null" : brief(actual)) + "\"");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | num1=\"" + brief(num1) + "\" num2=\"" + brief(num2)
                    + "\" | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // large/perf: oracle = BigInteger (used ONLY in the test, never in Answer) + timing
    static void checkProp(String name, String num1, String num2, long budgetMs) {
        total++;
        try {
            String expected = new BigInteger(num1).multiply(new BigInteger(num2)).toString();
            long t0 = System.nanoTime();
            String actual = new Answer().multiply(num1, num2);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            String label = name + " (" + elapsedMs + " ms)";
            boolean ok = expected.equals(actual);
            if (ok && elapsedMs > budgetMs) {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | too slow (> " + budgetMs + " ms)");
            } else if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + label + " -> [len " + expected.length() + "]");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + label + " | len1=" + num1.length() + " len2=" + num2.length()
                        + " | expectedLen=" + expected.length() + " actual=\"" + (actual == null ? "null" : brief(actual)) + "\"");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | threw " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    static String repeat(char c, int n) {
        char[] a = new char[n];
        Arrays.fill(a, c);
        return new String(a);
    }

    static String brief(String s) {
        if (s == null) return "null";
        if (s.length() <= 40) return s;
        return s.substring(0, 12) + "...(" + s.length() + ")..." + s.substring(s.length() - 12);
    }

    public static void main(String[] args) {
        check("example1", "2", "3", "6");
        check("example2", "123", "456", "56088");
        check("example3 zero operand", "0", "52", "0");
        check("both zero", "0", "0", "0");
        check("multiply by one", "1", "99999", "99999");
        check("identity reversed", "99999", "1", "99999");
        check("carry heavy", "99", "99", "9801");
        check("different lengths", "123456789", "987654321", "121932631112635269");
        check("trailing zeros result", "100", "100", "10000");
        check("single digits max", "9", "9", "81");
        check("large equal", "9999999999", "9999999999", "99999999980000000001");
        check("zero times big", "0", "123456789123456789", "0");

        // --- added corner cases ---
        check("one times one", "1", "1", "1");
        check("two times zero", "2", "0", "0");
        check("single digit pair", "7", "8", "56");
        check("power of ten times power of ten", "1000", "1000", "1000000");
        check("leading-result-no-zero", "9", "11", "99");
        check("max single carry", "9", "99999999", "899999991");
        check("all nines times one digit", "99999", "9", "899991");
        check("ten times ten", "10", "10", "100");
        check("asymmetric lengths", "2", "100000000000000000000", "200000000000000000000");
        check("both end in zero", "120", "120", "14400");
        check("one is multi-zero string vs num", "0", "1000", "0");
        check("twelve times twelve", "12", "12", "144");
        check("result exactly k digits", "50", "2", "100");
        check("mid-size product", "99999", "99999", "9999800001");
        check("long times single", "11111111111111111111", "3", "33333333333333333333");
        check("single times long", "3", "11111111111111111111", "33333333333333333333");

        // --- large / performance cases (BigInteger oracle in TEST only + timing; lengths up to 200) ---
        // Max length 200 x 200 all nines -> heaviest carry propagation.
        {
            String a = repeat('9', 200);
            String b = repeat('9', 200);
            checkProp("max 200x200 all nines", a, b, 3000);
        }
        // Max length 200 x 200 random digits (no leading zero), seed 42.
        {
            Random rnd = new Random(42);
            StringBuilder sa = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            sa.append((char) ('1' + rnd.nextInt(9)));
            sb.append((char) ('1' + rnd.nextInt(9)));
            for (int i = 1; i < 200; i++) sa.append((char) ('0' + rnd.nextInt(10)));
            for (int i = 1; i < 200; i++) sb.append((char) ('0' + rnd.nextInt(10)));
            checkProp("max 200x200 random digits", sa.toString(), sb.toString(), 3000);
        }
        // Asymmetric 200 x 1 and 1 x 200.
        {
            Random rnd = new Random(42);
            StringBuilder sa = new StringBuilder();
            sa.append((char) ('1' + rnd.nextInt(9)));
            for (int i = 1; i < 200; i++) sa.append((char) ('0' + rnd.nextInt(10)));
            checkProp("max 200 x 1", sa.toString(), "7", 3000);
            checkProp("max 1 x 200", "7", sa.toString(), 3000);
        }
        // Max length with one operand zero -> result must be "0" regardless of size.
        {
            String big = repeat('9', 200);
            checkProp("max 200 nines times zero", big, "0", 3000);
        }
        // Power-of-ten heavy trailing zeros at max length.
        {
            String a = "1" + repeat('0', 199);
            String b = "1" + repeat('0', 199);
            checkProp("max 1e199 x 1e199 trailing zeros", a, b, 3000);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
