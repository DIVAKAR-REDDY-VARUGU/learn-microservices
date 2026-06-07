// Merge Sorted Array  —  Arrays & Hashing (Array/Matrix Manipulation) / Merge Sorted Array (In-place from End)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i= m+n-1;
        m--;
        n--;
        while(n>=0){
            nums1[i--]=(m>=0&&nums1[m]>=nums2[n]) ?nums1[m--]:nums2[n--];
        }
    }

    public static void main(String[] args) {
        var answer = new Answer();
        int[] nums1={1,2,3,0,0,0},nums2={2,5,6};
        int m=3, n=3;
        answer.merge(nums1, m, nums2, n);
        System.out.println(Arrays.toString(nums1));  // [1,2,2,3,5,6]
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/07-merge-sorted-array-in-place-from-end
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
