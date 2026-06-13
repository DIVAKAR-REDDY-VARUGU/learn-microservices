// Find Minimum in Rotated Sorted Array  —  Binary Search / Find Min/Max in Rotated Sorted Array
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int findMin(int[] nums) {
        int s=0,e=nums.length-1,mid;
        while (s<e) {
            mid=s+(e-s)/2;
            if(nums[mid]>nums[e]){
                s=mid+1;
            }else{
                e=mid;
            }
        }
        return nums[s];
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 04-binary-search/04-find-min-max-in-rotated-sorted-array
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
