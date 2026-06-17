// Daily Temperatures  —  Stack / Monotonic Stack
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] dailyTemperatures(int[] temperatures) {
        int[] arr=new int[temperatures.length];
        ArrayDeque<Integer> stack=new ArrayDeque<>();
        for(int i=0;i<temperatures.length;i++){
            while(!stack.isEmpty()&&temperatures[i]>temperatures[stack.peekLast()]){
                int j=stack.pollLast();
                arr[j]=i-j;
            }

            stack.add(Integer.valueOf(i));

        }
        return arr;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(new Answer().dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73})));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 05-stack/02-monotonic-stack
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
