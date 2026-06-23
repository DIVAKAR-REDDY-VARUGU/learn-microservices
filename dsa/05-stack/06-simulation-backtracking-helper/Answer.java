// Decode String  —  Stack / Simulation/Backtracking Helper
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public String decodeString(String s) {
        ArrayDeque<Integer> countStack=new ArrayDeque<>();
        ArrayDeque<String> strStack=new ArrayDeque<>();

        StringBuilder currentStr=new StringBuilder();
        int currentCount=0;
        for(char c:s.toCharArray()){
            if(Character.isDigit(c)){
                currentCount=currentCount*10 + (c-'0');
            }
            else if(c=='['){
                countStack.add(Integer.valueOf(currentCount));
                strStack.add(currentStr.toString());
                currentCount=0;
                currentStr=new StringBuilder();
            }else if(c==']'){
                int repeat=countStack.pollLast();
                StringBuilder preStr=new StringBuilder(strStack.pollLast());
                while (repeat!=0) {
                    preStr.append(currentStr);
                    repeat--;
                }
                currentStr=preStr;
            }else{
                currentStr.append(c);
            }
        }
        return currentStr.toString();
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 05-stack/06-simulation-backtracking-helper
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
