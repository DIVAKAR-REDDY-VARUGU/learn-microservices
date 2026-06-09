// Backspace String Compare  —  Two Pointers / String Comparison with Backspaces
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public boolean backspaceCompare(String s, String t) {
        if(s.equals(t)||(s.length()==0&&t.length()==0))return true;
        int i=s.length()-1;
        int j=t.length()-1;
        while (i>=0 || j>=0) {
            i=nextValidIndex(s,i);
            j=nextValidIndex(t,j);
            if(i<0 && j<0)return true;
            if(i<0||j<0)return false;
            else if(s.charAt(i)==t.charAt(j)){
                i--;
                j--;
            }else return false;
        }
        return true;
    }
    public int nextValidIndex(String s,int i){
        int skip=0;
        while(i>=0){
            if(s.charAt(i)=='#'){
                skip++;
                i--;
            }else if(skip>0){
                skip--;
                i--;
            }else break;
        }
        return i;
    }

    public static void main(String[] args) {
        System.out.println(new Answer().backspaceCompare("#abc","abc"));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 02-two-pointers/06-string-comparison-with-backspaces
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
