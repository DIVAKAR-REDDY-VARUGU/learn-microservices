import java.util.*;

// NOTE: The provided Answer.java for this folder is syntactically broken
// (line 8 contains a copy-pasted signature stub, not a valid class). It will
// NOT compile until it is repaired into a proper non-static inner class:
//
//   public class Answer {
//       class MinStack {
//           public MinStack() { ... }
//           public void push(int val) { ... }
//           public void pop() { ... }
//           public int top() { ... }
//           public int getMin() { ... }
//       }
//   }
//
// This harness drives MinStack via the natural inner-class form
// `new Answer().new MinStack()`. Every operation is wrapped in try/catch so an
// unimplemented (UnsupportedOperationException) MinStack prints clean FAILs.
public class Test {
    static int pass = 0, total = 0;

    static void checkInt(String name, int expected, int actual) {
        total++;
        if (expected == actual) {
            pass++;
            System.out.println("\033[32m[PASS]\033[0m " + name + " -> " + actual);
        } else {
            System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected=" + expected + " actual=" + actual);
        }
    }

    static void fail(String name, int expected, Throwable t) {
        total++;
        System.out.println("\033[31m[FAIL]\033[0m " + name + " | expected=" + expected + " threw: " + t);
    }

    public static void main(String[] args) {
        // Scenario 1 (QUESTION.md): push(-2),push(0),push(-3),getMin->-3,pop,top->0,getMin->-2
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(-2); st.push(0); st.push(-3);
            checkInt("S1 getMin after push -2,0,-3", -3, st.getMin());
            st.pop();
            checkInt("S1 top after pop", 0, st.top());
            checkInt("S1 getMin after pop", -2, st.getMin());
        } catch (Throwable t) {
            fail("S1 getMin after push -2,0,-3", -3, t);
            fail("S1 top after pop", 0, t);
            fail("S1 getMin after pop", -2, t);
        }

        // Scenario 2 (QUESTION.md): push(5),push(2),getMin->2,pop,getMin->5
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(5); st.push(2);
            checkInt("S2 getMin after push 5,2", 2, st.getMin());
            st.pop();
            checkInt("S2 getMin after pop", 5, st.getMin());
            checkInt("S2 top after pop", 5, st.top());
        } catch (Throwable t) {
            fail("S2 getMin after push 5,2", 2, t);
            fail("S2 getMin after pop", 5, t);
            fail("S2 top after pop", 5, t);
        }

        // Scenario 3 (QUESTION.md): single element push(1),top->1,getMin->1
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(1);
            checkInt("S3 top single", 1, st.top());
            checkInt("S3 getMin single", 1, st.getMin());
        } catch (Throwable t) {
            fail("S3 top single", 1, t);
            fail("S3 getMin single", 1, t);
        }

        // Scenario 4: duplicate minimums — min must survive one pop
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(3); st.push(3); st.push(3);
            checkInt("S4 getMin dup", 3, st.getMin());
            st.pop();
            checkInt("S4 getMin after popping one dup", 3, st.getMin());
            checkInt("S4 top after pop", 3, st.top());
        } catch (Throwable t) {
            fail("S4 getMin dup", 3, t);
            fail("S4 getMin after popping one dup", 3, t);
            fail("S4 top after pop", 3, t);
        }

        // Scenario 5: ascending pushes — min stays the first; popping does not change min until last
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(1); st.push(2); st.push(3); st.push(4);
            checkInt("S5 getMin ascending", 1, st.getMin());
            st.pop(); st.pop();
            checkInt("S5 getMin after two pops", 1, st.getMin());
            checkInt("S5 top after two pops", 2, st.top());
        } catch (Throwable t) {
            fail("S5 getMin ascending", 1, t);
            fail("S5 getMin after two pops", 1, t);
            fail("S5 top after two pops", 2, t);
        }

        // Scenario 6: descending pushes — each new value is the new min; pop restores previous min
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(5); st.push(4); st.push(3); st.push(2);
            checkInt("S6 getMin descending", 2, st.getMin());
            st.pop();
            checkInt("S6 getMin after pop -> 3", 3, st.getMin());
            st.pop();
            checkInt("S6 getMin after pop -> 4", 4, st.getMin());
        } catch (Throwable t) {
            fail("S6 getMin descending", 2, t);
            fail("S6 getMin after pop -> 3", 3, t);
            fail("S6 getMin after pop -> 4", 4, t);
        }

        // Scenario 7: negatives and extreme bounds
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(Integer.MAX_VALUE); st.push(Integer.MIN_VALUE); st.push(0);
            checkInt("S7 getMin with MIN_VALUE", Integer.MIN_VALUE, st.getMin());
            checkInt("S7 top", 0, st.top());
            st.pop(); st.pop();
            checkInt("S7 getMin after pops -> MAX_VALUE", Integer.MAX_VALUE, st.getMin());
        } catch (Throwable t) {
            fail("S7 getMin with MIN_VALUE", Integer.MIN_VALUE, t);
            fail("S7 top", 0, t);
            fail("S7 getMin after pops -> MAX_VALUE", Integer.MAX_VALUE, t);
        }

        // ===== ADDED: more corner cases =====

        // Scenario 8: push then pop everything, push again (reuse after empty)
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(10); st.push(5);
            st.pop(); st.pop();
            st.push(7);
            checkInt("S8 top after reuse", 7, st.top());
            checkInt("S8 getMin after reuse", 7, st.getMin());
        } catch (Throwable t) {
            fail("S8 top after reuse", 7, t);
            fail("S8 getMin after reuse", 7, t);
        }

        // Scenario 9: new minimum then pop it -> min reverts to previous
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(8); st.push(3); st.push(1);
            checkInt("S9 getMin = 1", 1, st.getMin());
            st.pop();
            checkInt("S9 getMin reverts to 3", 3, st.getMin());
            st.pop();
            checkInt("S9 getMin reverts to 8", 8, st.getMin());
            checkInt("S9 top = 8", 8, st.top());
        } catch (Throwable t) {
            fail("S9 getMin = 1", 1, t);
            fail("S9 getMin reverts to 3", 3, t);
            fail("S9 getMin reverts to 8", 8, t);
            fail("S9 top = 8", 8, t);
        }

        // Scenario 10: duplicate minimums interleaved — popping one copy keeps min
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(2); st.push(0); st.push(3); st.push(0);
            checkInt("S10 getMin = 0 (two zeros)", 0, st.getMin());
            st.pop(); // remove last 0
            checkInt("S10 getMin still 0", 0, st.getMin());
            st.pop(); // remove 3
            checkInt("S10 getMin still 0 after removing 3", 0, st.getMin());
            st.pop(); // remove first 0
            checkInt("S10 getMin reverts to 2", 2, st.getMin());
        } catch (Throwable t) {
            fail("S10 getMin = 0 (two zeros)", 0, t);
            fail("S10 getMin still 0", 0, t);
            fail("S10 getMin still 0 after removing 3", 0, t);
            fail("S10 getMin reverts to 2", 2, t);
        }

        // Scenario 11: zeros and negatives mixed
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(0); st.push(-1); st.push(0); st.push(-2);
            checkInt("S11 getMin = -2", -2, st.getMin());
            checkInt("S11 top = -2", -2, st.top());
            st.pop();
            checkInt("S11 getMin -> -1", -1, st.getMin());
        } catch (Throwable t) {
            fail("S11 getMin = -2", -2, t);
            fail("S11 top = -2", -2, t);
            fail("S11 getMin -> -1", -1, t);
        }

        // Scenario 12: both 32-bit extremes pushed together
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(Integer.MIN_VALUE);
            checkInt("S12 getMin = MIN_VALUE", Integer.MIN_VALUE, st.getMin());
            checkInt("S12 top = MIN_VALUE", Integer.MIN_VALUE, st.top());
            st.push(Integer.MAX_VALUE);
            checkInt("S12 getMin still MIN_VALUE", Integer.MIN_VALUE, st.getMin());
            checkInt("S12 top = MAX_VALUE", Integer.MAX_VALUE, st.top());
            st.pop();
            checkInt("S12 getMin after pop = MIN_VALUE", Integer.MIN_VALUE, st.getMin());
        } catch (Throwable t) {
            fail("S12 getMin = MIN_VALUE", Integer.MIN_VALUE, t);
            fail("S12 top = MIN_VALUE", Integer.MIN_VALUE, t);
            fail("S12 getMin still MIN_VALUE", Integer.MIN_VALUE, t);
            fail("S12 top = MAX_VALUE", Integer.MAX_VALUE, t);
            fail("S12 getMin after pop = MIN_VALUE", Integer.MIN_VALUE, t);
        }

        // Scenario 13: V-shape — decrease to a trough then increase; min tracks the trough
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(5); st.push(3); st.push(1); st.push(3); st.push(5);
            checkInt("S13 getMin trough = 1", 1, st.getMin());
            st.pop(); st.pop(); // remove the two ascending after trough
            checkInt("S13 getMin still 1 at trough", 1, st.getMin());
            checkInt("S13 top = 1", 1, st.top());
        } catch (Throwable t) {
            fail("S13 getMin trough = 1", 1, t);
            fail("S13 getMin still 1 at trough", 1, t);
            fail("S13 top = 1", 1, t);
        }

        // Scenario 14: single element repeatedly checked is stable across many top/getMin
        try {
            Answer.MinStack st = new Answer().new MinStack();
            st.push(-42);
            checkInt("S14 top a", -42, st.top());
            checkInt("S14 getMin a", -42, st.getMin());
            checkInt("S14 top b (no mutation)", -42, st.top());
            checkInt("S14 getMin b (no mutation)", -42, st.getMin());
        } catch (Throwable t) {
            fail("S14 top a", -42, t);
            fail("S14 getMin a", -42, t);
            fail("S14 top b (no mutation)", -42, t);
            fail("S14 getMin b (no mutation)", -42, t);
        }

        // ===== ADDED: large / performance / scale (near 3*10^4 ops) =====
        // Drive a long randomized op sequence and validate top()/getMin() against
        // an independent reference java.util.ArrayDeque + running-min array oracle.
        try {
            final int OPS = 30000; // constraint upper bound on total calls
            Random rnd = new Random(42);
            Answer.MinStack st = new Answer().new MinStack();
            Deque<Integer> ref = new ArrayDeque<>();   // reference stack of values
            Deque<Integer> refMin = new ArrayDeque<>(); // reference running min
            boolean allOk = true;
            int firstBad = -1;
            long t0 = System.nanoTime();
            int ops = 0;
            while (ops < OPS) {
                int choice = rnd.nextInt(4);
                if (ref.isEmpty() || choice == 0) {
                    // push
                    int v = rnd.nextInt(2001) - 1000; // [-1000,1000]
                    st.push(v);
                    ref.push(v);
                    refMin.push(refMin.isEmpty() ? v : Math.min(v, refMin.peek()));
                    ops++;
                } else if (choice == 1) {
                    // pop
                    st.pop();
                    ref.pop();
                    refMin.pop();
                    ops++;
                } else if (choice == 2) {
                    // top check
                    int got = st.top();
                    int exp = ref.peek();
                    ops++;
                    if (got != exp) { allOk = false; firstBad = ops; break; }
                } else {
                    // getMin check
                    int got = st.getMin();
                    int exp = refMin.peek();
                    ops++;
                    if (got != exp) { allOk = false; firstBad = ops; break; }
                }
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            total++;
            if (allOk && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m large randomized " + OPS + " ops vs oracle (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m large randomized ops vs oracle | allOk=" + allOk + " firstBadOp=" + firstBad + " (" + ms + " ms" + (ms > 3000 ? ", OVER BUDGET" : "") + ")");
            }
        } catch (Throwable t) {
            total++;
            System.out.println("\033[31m[FAIL]\033[0m large randomized ops vs oracle | threw: " + t);
        }

        // Large: push a strictly decreasing run then pop all, checking getMin each step.
        try {
            final int N = 10000;
            Answer.MinStack st = new Answer().new MinStack();
            for (int i = 0; i < N; i++) st.push(N - i); // values N..1, each a new min
            long t0 = System.nanoTime();
            boolean ok = true;
            // After all pushes the min is 1.
            if (st.getMin() != 1) ok = false;
            // Pop down: after removing the top (which is the current min on a descending push),
            // min should increase by 1 each pop.
            for (int expectedMin = 1; expectedMin <= N && ok; expectedMin++) {
                if (st.getMin() != expectedMin) { ok = false; break; }
                st.pop();
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            total++;
            if (ok && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m large descending push/pop min-tracking N=" + N + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m large descending push/pop min-tracking | ok=" + ok + " (" + ms + " ms)");
            }
        } catch (Throwable t) {
            total++;
            System.out.println("\033[31m[FAIL]\033[0m large descending push/pop min-tracking | threw: " + t);
        }

        // Large: all-equal pushes — getMin and top constant, O(1) per op.
        try {
            final int N = 15000;
            Answer.MinStack st = new Answer().new MinStack();
            for (int i = 0; i < N; i++) st.push(7);
            long t0 = System.nanoTime();
            boolean ok = (st.getMin() == 7) && (st.top() == 7);
            for (int i = 0; i < N - 1; i++) {
                st.pop();
                if (st.getMin() != 7) { ok = false; break; }
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            total++;
            if (ok && ms <= 3000) {
                pass++;
                System.out.println("\033[32m[PASS]\033[0m large all-equal N=" + N + " (" + ms + " ms)");
            } else {
                System.out.println("\033[31m[FAIL]\033[0m large all-equal | ok=" + ok + " (" + ms + " ms)");
            }
        } catch (Throwable t) {
            total++;
            System.out.println("\033[31m[FAIL]\033[0m large all-equal | threw: " + t);
        }

        System.out.println("Summary: " + pass + "/" + total + " passed");
    }
}
