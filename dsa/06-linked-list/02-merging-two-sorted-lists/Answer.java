// Merge Two Sorted Lists  —  Linked List / Merging Two Sorted Lists
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        
        ListNode head=new ListNode(0);
        ListNode tail=head;


        while (list1!=null&&list2!=null) {
            if(list1.val<list2.val){
                tail.next=list1;
                tail=list1;
                list1=list1.next;
            }else{
                tail.next=list2;
                tail=list2;
                list2=list2.next;
            }
        }
        tail.next=(list1==null)?list2:list1;
        return head.next;

    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 06-linked-list/02-merging-two-sorted-lists
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
