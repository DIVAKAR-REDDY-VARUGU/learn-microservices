// Remove Nth Node From End of List  —  Two Pointers / Fixed Separation (Nth from End)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode temp=new ListNode(0,head);
        ListNode fast=temp;
       
        // push fast to n nodes
        int i=0;
        while (i<=n) {
            fast=fast.next;
            i++;            
        }


        ListNode cur=temp;
        while (fast!=null) {
            cur=cur.next;
            fast=fast.next;
        }
        cur.next=cur.next.next;
        return temp.next;   // instead of direct head , send by temp reference ;

    }
    
    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 02-two-pointers/03-fixed-separation-nth-from-end
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
