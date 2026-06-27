// Task Scheduler  —  Heap (Priority Queue) / Scheduling / Minimum Cost (Greedy + PQ)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int leastInterval(char[] tasks, int n) {
        HashMap<Character,Integer> taskCountMap=new HashMap<>();
        for(Character c:tasks)taskCountMap.merge(c,1,(a,b)->a+b);

        PriorityQueue<Integer> pq=new PriorityQueue<>((a,b)->b-a); // max heap
        for(Integer count:taskCountMap.values())pq.offer(count);

        int time=0;

        while (pq.size()!=0) {
            ArrayList<Integer> temp=new ArrayList<>();
            int carry=n+1;
            while(carry>0&&pq.size()>0){
                Integer top=pq.poll();
                if(top>1)temp.add(top-1);
                carry--;
                time++;
            }
            for(var i:temp){
                pq.offer(i);
            }
            if(pq.size()>0)time+=carry;
        }

        return time;



    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 08-heap/04-scheduling-minimum-cost-greedy-pq
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
