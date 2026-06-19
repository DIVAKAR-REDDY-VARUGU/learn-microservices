// Min Stack  —  Stack / Min Stack Design
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    class MinStack {
         private Deque<Integer> s   = new ArrayDeque<>();
    private Deque<Integer> min = new ArrayDeque<>();
    public void push(int x) {
        s.push(x);
        min.push(min.isEmpty() ? x : Math.min(x, min.peek()));
    }
    public void pop()   { s.pop(); min.pop(); }
    public int  top()   { return s.peek(); }
    public int  getMin(){ return min.peek(); }
    }

    

    public static void main(String[] args) {
        Answer solution = new Answer();

        System.out.println("MinStack: " + solution.new MinStack());
        System.out.println("push(1)");
        solution.new MinStack().push(1);
        System.out.println("push(2)");
        solution.new MinStack().push(2);
        System.out.println("getMin(): " + solution.new MinStack().getMin());
        System.out.println("pop()");
        solution.new MinStack().pop();
        System.out.println("top(): " + solution.new MinStack().top());
        System.out.println("getMin(): " + solution.new MinStack().getMin());

    }
}

/*
 * ===== Run the tests =====
 * In this folder: javac Answer.java Test.java && java Test
 * From dsa/ : bash run-tests.sh 05-stack/03-min-stack-design
 * -> Test.java runs many corner cases and prints [PASS]/[FAIL] +
 * "Summary: X/Y passed"
 * =========================
 */
