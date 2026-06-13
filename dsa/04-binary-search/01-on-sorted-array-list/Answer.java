// Binary Search  —  Binary Search / On Sorted Array/List
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int search(int[] nums, int target) {
        
        int s=0,e=nums.length-1;
        int mid=-1;
        while (s<=e) {
            mid=s+(e-s)/2;
            if(nums[mid]==target)return mid;
            else if(nums[mid]>target)e=mid -1;
            else s=mid+1;
        }

        return -1;
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 04-binary-search/01-on-sorted-array-list
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
