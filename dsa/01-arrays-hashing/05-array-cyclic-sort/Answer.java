// Find All Numbers Disappeared in an Array  —  Arrays & Hashing (Array/Matrix Manipulation) / Array - Cyclic Sort
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {
    private void swap(int[] arr, int i , int j){
        int temp=arr[i];
        arr[i]=arr[j];
        arr[j]=temp;
    }
    public List<Integer> findDisappearedNumbers(int[] nums) {
        int len=nums.length;
        int i=0;
        int home;
        while(i<len){
            home=nums[i]-1;
            if(nums[i]!=nums[home]){
                swap(nums,i,home);
            }
            else{
                i++;
            }
        }

        ArrayList<Integer> arr=new ArrayList<>();
        for(i=0;i<len;i++){
            if(nums[i]!=i+1){
                arr.add(i+1);
            }
        }
        return arr;
    }

    public static void main(String[] args) {
        System.out.println(new Answer().findDisappearedNumbers(new int[]{4,3,2,7,8,2,3,1}));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/05-array-cyclic-sort
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
