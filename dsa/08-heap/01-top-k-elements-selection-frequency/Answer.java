// Top K Frequent Elements  —  Heap (Priority Queue) / Top K Elements (Selection/Frequency)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] topKFrequent(int[] nums, int k) {
       HashMap<Integer,Integer> numberCountMap=new HashMap<>();
       for(int i:nums)numberCountMap.merge(Integer.valueOf(i), 1, (a,b)->a+b);

       PriorityQueue<int[]> queueOfK=new PriorityQueue<>((a,b)->a[1]-b[1]);

       for(var entry:numberCountMap.entrySet()){
            queueOfK.add(new int[]{entry.getKey(),entry.getValue()});
            if(queueOfK.size()>k)queueOfK.poll();
       }
       int[] res=new int[k];
       while(queueOfK.size()>0){
            res[--k]=queueOfK.poll()[0];
       }
       return res;
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 08-heap/01-top-k-elements-selection-frequency
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
