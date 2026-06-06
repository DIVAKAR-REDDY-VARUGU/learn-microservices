import java.util.*;

public class Test {
    static int pass = 0, total = 0;

    static String label(String[] tokens) {
        if (tokens.length <= 25) return Arrays.toString(tokens);
        return "<tokens=" + tokens.length + ">";
    }

    static void check(String[] tokens, int expected) {
        total++;
        String in = label(tokens);
        try {
            int actual = new Answer().evalRPN(tokens.clone());
            if (actual == expected) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m evalRPN(" + in + ")");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m evalRPN(" + in + ") | expected=" + expected + " actual=" + actual);
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m evalRPN(" + in + ") | expected=" + expected + " threw: " + t);
        }
    }

    // Independent oracle: evaluate RPN with a stack (truncate toward zero).
    static int oracle(String[] tokens) {
        Deque<Integer> st = new ArrayDeque<>();
        for (String tk : tokens) {
            if (tk.equals("+") || tk.equals("-") || tk.equals("*") || tk.equals("/")) {
                int b = st.pop();
                int a = st.pop();
                int r;
                switch (tk) {
                    case "+": r = a + b; break;
                    case "-": r = a - b; break;
                    case "*": r = a * b; break;
                    default:  r = a / b; break; // Java int division truncates toward zero
                }
                st.push(r);
            } else {
                st.push(Integer.parseInt(tk));
            }
        }
        return st.pop();
    }

    static void checkLarge(String name, String[] tokens, int expected) {
        total++;
        try {
            long t0 = System.nanoTime();
            int actual = new Answer().evalRPN(tokens.clone());
            long ms = (System.nanoTime() - t0) / 1_000_000;
            boolean ok = (actual == expected) && ms <= 3000;
            if (ok) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + actual + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected=" + expected + " actual=" + actual + " (" + ms + " ms" + (ms > 3000 ? ", OVER BUDGET" : "") + ")");
            }
        } catch (Throwable t) {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected=" + expected + " threw: " + t);
        }
    }

    public static void main(String[] args) {
        // Examples from QUESTION.md
        check(new String[]{"2","1","+","3","*"}, 9);
        check(new String[]{"4","13","5","/","+"}, 6);
        check(new String[]{"10","6","9","3","+","-11","*","/","*","17","+","5","+"}, 22);
        // Single operand (length 1) — positive
        check(new String[]{"42"}, 42);
        // Single operand — negative
        check(new String[]{"-5"}, -5);
        // Single operand — zero
        check(new String[]{"0"}, 0);
        // Simple addition
        check(new String[]{"1","2","+"}, 3);
        // Subtraction order: 5 - 3 = 2 (first popped is right operand)
        check(new String[]{"5","3","-"}, 2);
        // Subtraction giving negative: 3 - 5 = -2
        check(new String[]{"3","5","-"}, -2);
        // Multiplication
        check(new String[]{"6","7","*"}, 42);
        // Multiply two negatives
        check(new String[]{"-3","-2","*"}, 6);
        // Division exact
        check(new String[]{"6","3","/"}, 2);
        // Division truncates toward zero (positive)
        check(new String[]{"7","2","/"}, 3);
        // Division truncates toward zero (negative numerator)
        check(new String[]{"-7","2","/"}, -3);
        // Division truncates toward zero (negative denominator)
        check(new String[]{"7","-2","/"}, -3);
        // Division both negative
        check(new String[]{"-7","-2","/"}, 3);
        // Bounds operands (range [-200,200])
        check(new String[]{"200","-200","+"}, 0);
        check(new String[]{"200","-200","*"}, -40000);
        // Chained operations
        check(new String[]{"15","7","1","1","+","-","/","3","*"}, 9);

        // ===== ADDED: more corner cases =====
        // Operand bounds: max + max, min + min
        check(new String[]{"200","200","+"}, 400);
        check(new String[]{"-200","-200","+"}, -400);
        // Max * max
        check(new String[]{"200","200","*"}, 40000);
        // Subtraction at bounds: 200 - (-200) = 400 ; -200 - 200 = -400
        check(new String[]{"200","-200","-"}, 400);
        check(new String[]{"-200","200","-"}, -400);
        // Division truncation edge: 1/2 = 0, -1/2 = 0
        check(new String[]{"1","2","/"}, 0);
        check(new String[]{"-1","2","/"}, 0);
        check(new String[]{"1","-2","/"}, 0);
        // Division yielding exactly -1
        check(new String[]{"-200","200","/"}, -1);
        check(new String[]{"200","-200","/"}, -1);
        // Zero numerator
        check(new String[]{"0","5","/"}, 0);
        // Multiply by zero
        check(new String[]{"0","200","*"}, 0);
        check(new String[]{"200","0","*"}, 0);
        // Add zero / subtract zero identity
        check(new String[]{"137","0","+"}, 137);
        check(new String[]{"137","0","-"}, 137);
        // Left-associative chain of subtraction: ((10-3)-2)-1 = 4
        check(new String[]{"10","3","-","2","-","1","-"}, 4);
        // Chain of divisions truncating each step: 100/3=33, 33/3=11
        check(new String[]{"100","3","/","3","/"}, 11);
        // Deep right-leaning expression
        check(new String[]{"2","3","4","*","+"}, 14); // 2 + (3*4)
        // Mixed signs net to a specific value: (-5 + 3) * -4 = 8
        check(new String[]{"-5","3","+","-4","*"}, 8);
        // Single negative-bound operand
        check(new String[]{"-200"}, -200);
        check(new String[]{"200"}, 200);
        // Verify a hand expression against the oracle
        {
            String[] e = {"10","6","9","3","+","-11","*","/","*","17","+","5","+"};
            check(e, oracle(e));
        }

        // ===== ADDED: random small expressions vs oracle =====
        Random rnd = new Random(42);
        {
            long t0 = System.nanoTime();
            boolean allOk = true;
            int firstBad = -1;
            for (int t = 0; t < 500; t++) {
                String[] toks = buildRandomRPN(rnd, 1 + rnd.nextInt(40));
                int exp = oracle(toks);
                int act;
                try { act = new Answer().evalRPN(toks.clone()); } catch (Throwable th) { act = exp + 1; }
                if (act != exp) { allOk = false; firstBad = t; break; }
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            total++;
            if (allOk && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m 500 random RPN expr vs oracle (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m random RPN vs oracle | firstBad=" + firstBad + " (" + ms + " ms)");
            }
        }

        // ===== ADDED: large / performance / scale (near 10^4 tokens) =====
        // Build a long left-folded additive/subtractive expression.
        {
            // tokens = num num op num op num op ... keeping running value safe (small ops).
            int operations = 4999; // total tokens = 1 + 2*operations = 9999 (<= 10^4)
            String[] toks = new String[1 + 2 * operations];
            int idx = 0;
            toks[idx++] = "0";
            // Alternate +1 then -1 to keep the running value bounded but force full stack churn.
            for (int i = 0; i < operations; i++) {
                if (i % 2 == 0) { toks[idx++] = "1"; toks[idx++] = "+"; }
                else { toks[idx++] = "1"; toks[idx++] = "-"; }
            }
            checkLarge("large alternating +/- (" + toks.length + " tokens)", toks, oracle(toks));
        }
        // Large nested multiply/divide that stays bounded: repeatedly *2 then /2.
        {
            int operations = 4999;
            String[] toks = new String[1 + 2 * operations];
            int idx = 0;
            toks[idx++] = "7";
            for (int i = 0; i < operations; i++) {
                if (i % 2 == 0) { toks[idx++] = "3"; toks[idx++] = "*"; }
                else { toks[idx++] = "3"; toks[idx++] = "/"; }
            }
            checkLarge("large *3 /3 churn (" + toks.length + " tokens)", toks, oracle(toks));
        }
        // Large random valid RPN near the bound; verified by oracle.
        {
            String[] toks = buildRandomRPN(rnd, 3333); // ~ up to 2*3333+1 tokens
            checkLarge("large random RPN (" + toks.length + " tokens)", toks, oracle(toks));
        }
        // Deeply nested addition tree built as: many operands pushed then folded.
        {
            int operands = 5000; // 5000 operands + 4999 '+' = 9999 tokens
            String[] toks = new String[operands + (operands - 1)];
            int idx = 0;
            toks[idx++] = "1";
            for (int i = 1; i < operands; i++) { toks[idx++] = "0"; toks[idx++] = "+"; }
            checkLarge("large fold-add tree (" + toks.length + " tokens) expect 1", toks, 1);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }

    // Build a random VALID postfix expression that yields `targetOperands`-ish operands,
    // guaranteed to keep all intermediate results within int range by using small operands
    // and skipping division by zero. Returns a syntactically valid RPN token array.
    static String[] buildRandomRPN(Random rnd, int extraOps) {
        // Start with one operand, then repeatedly either push an operand or apply an operator
        // (only when at least 2 values are on the virtual stack). End with exactly one value.
        List<String> toks = new ArrayList<>();
        int stackSize = 0;
        // first operand
        toks.add(Integer.toString(rnd.nextInt(401) - 200)); // [-200,200]
        stackSize = 1;
        for (int i = 0; i < extraOps; i++) {
            boolean canOp = stackSize >= 2;
            boolean doOp = canOp && rnd.nextBoolean();
            if (doOp) {
                int o = rnd.nextInt(4);
                String op = (o == 0) ? "+" : (o == 1) ? "-" : (o == 2) ? "*" : "/";
                if (op.equals("/")) {
                    // ensure the right operand (last pushed value) is non-zero; if the
                    // immediately preceding token is the literal "0", switch to '+'.
                    String lastTok = toks.get(toks.size() - 1);
                    boolean lastIsZeroLiteral = lastTok.equals("0");
                    if (lastIsZeroLiteral) op = "+";
                }
                toks.add(op);
                stackSize -= 1; // two popped, one pushed
            } else {
                int v = rnd.nextInt(401) - 200;
                if (v == 0) v = 1; // avoid trailing zero operands that could feed a later '/'
                toks.add(Integer.toString(v));
                stackSize += 1;
            }
        }
        // collapse to a single value with additions (safe, no div-by-zero)
        while (stackSize >= 2) {
            toks.add("+");
            stackSize -= 1;
        }
        return toks.toArray(new String[0]);
    }
}
