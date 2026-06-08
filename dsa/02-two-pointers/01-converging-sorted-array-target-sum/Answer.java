// Two Sum II - Input Array Is Sorted  —  Two Pointers / Converging (Sorted Array Target Sum)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] twoSum(int[] numbers, int target) {
        int i=0;
        int j=numbers.length-1;
        int currentSum;
        while(i<j){
            currentSum=numbers[i]+numbers[j];
            if(currentSum==target)return new int[]{i+1,j+1};
            else if(currentSum>target) j--;
            else i++;
        }
        return new int[]{-1,-1};
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(new Answer().twoSum(new int[]{2, 7, 11, 15}, 9)));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 02-two-pointers/01-converging-sorted-array-target-sum
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
