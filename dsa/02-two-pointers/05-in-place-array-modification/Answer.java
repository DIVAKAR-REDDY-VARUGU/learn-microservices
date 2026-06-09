// Remove Duplicates from Sorted Array  —  Two Pointers / In-place Array Modification
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {
    private void swap(int[] arr,int i ,int j){
        int t=arr[i];
        arr[i]=arr[j];
        arr[j]=t;
    }
    public int removeDuplicates(int[] nums) {
        int i=0;
        int j=0;
        while (j<nums.length) {
            if(nums[i]<nums[j]){
                i++;
                swap(nums,i,j);
                j++;
            }else{
                j++;
            }
        }
        return i+1;

    }

    public static void main(String[] args) {
        // System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 02-two-pointers/05-in-place-array-modification
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
