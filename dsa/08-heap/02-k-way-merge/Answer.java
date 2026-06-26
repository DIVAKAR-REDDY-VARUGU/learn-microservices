// Merge k Sorted Lists  —  Heap (Priority Queue) / K-way Merge
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> pq=new PriorityQueue<>((a,b)->a.val-b.val);
        for(var n:lists)if(n!=null)pq.offer(n);
        
        ListNode temp=new ListNode(0);
        ListNode tail=temp;

        while(pq.size()!=0){
            ListNode minNode=pq.poll();
            tail.next=minNode;
            tail=minNode;
            if(minNode.next!=null)pq.offer(minNode.next);
        }

        return temp.next;
        


    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 08-heap/02-k-way-merge
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
