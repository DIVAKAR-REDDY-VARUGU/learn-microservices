// Maximum Average Subarray I  —  Sliding Window / Fixed Size
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public double findMaxAverage(int[] nums, int k) {
        int sum=0;
        // first window
        for(int i=0;i<k;i++)sum+=nums[i];
        
        int max=sum;
       for(int i=k;i<nums.length;i++){
            sum+=nums[i]-nums[i-k];
            max=Math.max(max, sum);
       }
       return (double)max/(double)k;
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 03-sliding-window/01-fixed-size
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
