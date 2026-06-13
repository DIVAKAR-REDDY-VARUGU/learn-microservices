// Find First and Last Position of Element in Sorted Array  —  Binary Search / Find First/Last Occurrence
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] searchRange(int[] nums, int target) {
        int s=0,e=nums.length-1,mid;
        while (s<=e) {
            mid=s+(e-s)/2;
            if(nums[mid]==target){
                return new int[]{findBetween(nums,s,mid,target,true),findBetween(nums,mid,e,target,false)};
            }else if(nums[mid]<target){
                s=mid+1;
            }else e=mid-1;
            
        }
        return new int[]{-1,-1};
    }
    public int findBetween(int[] nums,int s,int e,int target,boolean min){
        int mid;
        while (s<=e) {
            mid=s+(e-s)/2;
            if(nums[mid]==target){
                if(min)e=mid-1;
                else s=mid+1;

            }else if(nums[mid]<target){
                s=mid+1;
            }else e=mid-1;
            
        }
        return min?s:e;
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 04-binary-search/02-find-first-last-occurrence
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
