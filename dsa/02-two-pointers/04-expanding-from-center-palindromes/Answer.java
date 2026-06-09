// Longest Palindromic Substring  —  Two Pointers / Expanding From Center (Palindromes)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public String longestPalindrome(String s) {

        int oddStrLen,evenStrLen,maxLen=1,strStart=0;
        for(int i=0;i<s.length();i++){
            oddStrLen=palindrome(s,i,i);
            evenStrLen=palindrome(s,i,i+1);
            int len=Math.max(oddStrLen, evenStrLen);
            if(maxLen<len){
                maxLen=len;
                strStart=i-((len-1)/2);         // remember this , you will forget this 
            }
        }
        return s.substring(strStart,strStart+maxLen);
    }
    public int palindrome(String s, int i, int j){
        while (i>=0&&j<s.length()) {
            if(s.charAt(i)==s.charAt(j)){
                i--;
                j++;
            }else {
                break;
            }
        }
        return j-i-1;       // Remember this too , 
    }

    public static void main(String[] args) {
        System.out.println(new Answer().longestPalindrome("racecar"));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 02-two-pointers/04-expanding-from-center-palindromes
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
