// Reorder List  —  Linked List / Reordering/Partitioning
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public void reorderList(ListNode head) {
        // find middle 
        ListNode mid=findMiddleNode(head);
        // reverse second Half
        ListNode secondHalf=mid.next;
        mid.next=null;
        secondHalf=reverseOrder(secondHalf);
        // merger first and second half 

        mergeNodes(head,secondHalf);

    }
    public ListNode findMiddleNode(ListNode head){
        ListNode slow,fast;
        slow=head;
        fast=head;
        while(fast.next!=null&&fast.next.next!=null){
            slow=slow.next;
            fast=fast.next.next;
        }
        return slow;
    }

    public ListNode reverseOrder(ListNode head){
        ListNode startNode=null;
        while (head!=null) {
            ListNode cur=head;
            head=head.next;
            cur.next=startNode;
            startNode=cur;
        }
        return startNode;
    }
    
    public void mergeNodes(ListNode l1,ListNode l2){
        while(l2!=null){
            ListNode t1=l1.next;
            ListNode t2=l2.next;

            l1.next=l2;
            l2.next=t1;

            l1=t1;
            l2=t2;

        

        }
    }

    public static void main(String[] args) {
        Answer solution = new Answer();
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);
        solution.reorderList(head);
        // Print the reordered list
        ListNode current = head;
        while (current != null) {
            System.out.print(current.val + " ");
            current = current.next;
        }
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 06-linked-list/04-reordering-partitioning
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
