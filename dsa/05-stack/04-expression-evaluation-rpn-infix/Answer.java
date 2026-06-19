// Evaluate Reverse Polish Notation  —  Stack / Expression Evaluation (RPN/Infix)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int evalRPN(String[] tokens) {
        ArrayDeque<Integer> stack=new ArrayDeque<>();

        for(String s:tokens){
            calculateAndPush(stack, s);
        }
        return (int)stack.pop();

    }
    public void calculateAndPush(ArrayDeque stack,String op){
        switch (op) {
            case "+":
            case "-":
            case "*":
            case "/":{
                int b=(int)stack.pop();
                int a=(int)stack.pop();
                stack.push(
                    Integer.valueOf(switch (op) {
                        case "+" -> a+b;
                        case "-" -> a-b;
                        case "*" -> a*b;
                        default -> a/b;
                    })
                );
            }
                
                break;
        
            default:
                stack.push(Integer.parseInt(op));
        }
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 05-stack/04-expression-evaluation-rpn-infix
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
