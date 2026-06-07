// Rotate Array  —  Arrays & Hashing (Array/Matrix Manipulation) / In-place Rotation
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {
    public static void swap(int[] arr, int i , int j){
        int temp=arr[i];
        arr[i]=arr[j];
        arr[j]=temp;
    }
    public static void reverse(int[] arr,int start,int end){
       while(start<end){
        swap(arr,start++,end--);
       }
    }
    public static void rotate(int[] nums, int k) {
        int len=nums.length;
        k%=len;
        if(k==0)return;
        reverse(nums,0,len-1);
        reverse(nums,0,k-1);
        reverse(nums,k,len-1);

    }

    public static void main(String[] args) {
        int arr[]=new int[]{1,2,3,4,5,6,7};
        rotate(arr, 3);
        System.out.println(Arrays.toString(arr));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/06-in-place-rotation
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
