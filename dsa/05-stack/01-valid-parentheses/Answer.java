// Valid Parentheses  —  Stack / Valid Parentheses
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public boolean isValid(String s) {
        HashMap<Character,Character> close=new HashMap<>();
        close.put('(',')');
        close.put('{','}');
        close.put('[',']');

        ArrayDeque<Character> stack=new ArrayDeque<>();
        for(Character c:s.toCharArray()){
            if(!close.containsKey(c)){
                if(close.get(stack.pollLast())!=c)return false;
            }else{
                stack.add(c);
            }
        }
        return stack.isEmpty();

    }

    public static void main(String[] args) {
        System.out.println(new Answer().isValid("(){}[]"));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 05-stack/01-valid-parentheses
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
