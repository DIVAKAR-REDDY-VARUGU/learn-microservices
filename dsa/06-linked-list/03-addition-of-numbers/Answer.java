// Add Two Numbers  —  Linked List / Addition of Numbers
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode head=new ListNode(0);
        ListNode tail=head;
        int carry=0;
        while (l1!=null||l2!=null) {
            int sum=0;
            if(l1!=null){
                sum+=l1.val;
                l1=l1.next;
            }
            if(l2!=null){
                sum+=l2.val;
                l2=l2.next;
            }
            sum+=carry;
            carry=sum/10;
            tail.next=new ListNode(sum%10);
            tail=tail.next;
        }
        if(carry!=0)tail.next=new ListNode(carry);
        return head.next;
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 06-linked-list/03-addition-of-numbers
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
