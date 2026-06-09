// Reverse String  —  Two Pointers / String Reversal
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {
    public void swap(char[] arr,int i,int j){
        char t=arr[i];
        arr[i]=arr[j];
        arr[j]=t;
    }
    public void reverseString(char[] s) {
        int i=0;
        int j=s.length-1;
        while (i<j) {
            swap(s, i++, j--);
        }
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 02-two-pointers/07-string-reversal
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
