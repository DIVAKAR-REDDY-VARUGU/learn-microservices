// Minimum Size Subarray Sum  —  Sliding Window / Variable Size
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int minSubArrayLen(int target, int[] nums) {
       int len=Integer.MAX_VALUE;
       int i=0;
       int j=0;
       int sum=0;
       while(i<nums.length){

            sum+=nums[i];
            while(sum>=target){
                len=Math.min(len,i-j+1);
                sum-=nums[j++];
            }
            i++;
       }
       return len==Integer.MAX_VALUE?0:len;
    }

    public static void main(String[] args) {
        System.out.println(new Answer().minSubArrayLen(7,new int[]{2,3,1,2,4,5}));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 03-sliding-window/02-variable-size
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
