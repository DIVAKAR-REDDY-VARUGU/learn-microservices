// Plus One  —  Arrays & Hashing (Array/Matrix Manipulation) / Plus One (Handling Carry)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] plusOne(int[] digits) {
        for(int i=digits.length-1;i>=0;i--){
            if(digits[i]<9){
                digits[i]++;
                return digits;
            }
            digits[i]=0;
        }
        int[] res=new int[digits.length+1];
        res[0]=1;
        return res;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(new Answer().plusOne(new int[]{1, 2, 3})));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/08-plus-one-handling-carry
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
