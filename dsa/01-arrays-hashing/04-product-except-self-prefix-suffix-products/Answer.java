// Product of Array Except Self  —  Arrays & Hashing (Array/Matrix Manipulation) / Product Except Self (Prefix/Suffix Products)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] productExceptSelf(int[] nums) {
        int len=nums.length;
        int []result=new int[len];
        result[0]=nums[0];
        for(int i=1;i<len;i++)result[i]=result[i-1]*nums[i];
        
        int right=1;
        for(int i=len-1;i>0;i--){
            result[i]=right*result[i-1];
            right*=nums[i];
        }
        result[0]=right;
        return result;   
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(new Answer().productExceptSelf(new int[]{2,4,1,3,2})));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/04-product-except-self-prefix-suffix-products
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
