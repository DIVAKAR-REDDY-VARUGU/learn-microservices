// Sliding Window Maximum  —  Sliding Window / Monotonic Queue for Max/Min
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] maxSlidingWindow(int[] nums, int k) {
        ArrayDeque<Integer> deq=new ArrayDeque<>();
        int[] res=new int[nums.length-k+1];
        for(int i=0;i<nums.length;i++){

            checkAndRemoveSmallElements(deq, nums, i);
            deq.add(i);
            removeOldInd(deq,i,k);
            if(i+1>=k){
                res[i-k+1]=nums[deq.peekFirst()];
            }
        }
        return res;

    }

    public void checkAndRemoveSmallElements(ArrayDeque<Integer> deq,int[] nums,int i){
        while (!deq.isEmpty()&&nums[deq.peekLast()]<nums[i]) {
            deq.pollLast();
        }
    }

    public void removeOldInd(ArrayDeque<Integer> deq,int i,int k){
        if(deq.peekFirst()<=i-k)deq.pollFirst();
    }

    public static void main(String[] args) {
         System.out.println(Arrays.toString(new Answer().maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7},3)));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 03-sliding-window/04-monotonic-queue-for-max-min
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
