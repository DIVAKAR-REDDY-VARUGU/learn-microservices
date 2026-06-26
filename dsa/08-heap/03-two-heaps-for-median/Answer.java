// Find Median from Data Stream  —  Heap (Priority Queue) / Two Heaps for Median
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    class MedianFinder {
        PriorityQueue<Integer> left_max_heap=new PriorityQueue<>((a,b)->b-a);
        PriorityQueue<Integer> right_min_heap=new PriorityQueue<>();
        
        public MedianFinder() {}
        
        public void addNum(int num) {
            left_max_heap.offer(Integer.valueOf(num));
            right_min_heap.offer(left_max_heap.poll());
            if(left_max_heap.size()<right_min_heap.size())left_max_heap.offer(right_min_heap.poll());
        }
        
        public double findMedian() {
            return (left_max_heap.size()>right_min_heap.size())?left_max_heap.peek():(left_max_heap.peek()+right_min_heap.peek())/2.0;
        }
    } 
    

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 08-heap/03-two-heaps-for-median
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
