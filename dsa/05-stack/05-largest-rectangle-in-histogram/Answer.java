// Largest Rectangle in Histogram  —  Stack / Largest Rectangle in Histogram
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int largestRectangleArea(int[] hts) {
        int[] heights=new int[hts.length+1];
        for(int i=0;i<hts.length;i++)heights[i]=hts[i];
        ArrayDeque<Integer> stack=new ArrayDeque<>(heights.length);
        int area=0;
        for(int i=0;i<heights.length;i++){
            if(stack.isEmpty()){
                stack.add(Integer.valueOf(i));
                continue;
            }
            
            while(!stack.isEmpty()&&heights[i]<heights[stack.peekLast()]){
                int height=heights[stack.pollLast()];
                int width=(stack.isEmpty())?i:(i-1-(int)stack.peekLast());
                area=Math.max(area, width*height);
            }
            stack.add(Integer.valueOf(i));
        }
        return area;
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 05-stack/05-largest-rectangle-in-histogram
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
