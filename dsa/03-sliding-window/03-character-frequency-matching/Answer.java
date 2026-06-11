// Find All Anagrams in a String  —  Sliding Window / Character Frequency Matching
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public List<Integer> findAnagrams(String s, String p) {

        int[] pAlpCount=new int[26];
        for(char c:p.toCharArray()){
            pAlpCount[c-'a']++;
        }
        int[] tAlpCount=new int[26];
        for(int i=0;i<p.length()&&i<s.length();i++){
            tAlpCount[s.charAt(i)-'a']++;
        }
        
        ArrayList<Integer> res=new ArrayList<>();
        if(Arrays.equals(pAlpCount, tAlpCount))res.add(0);
        
        for(int i=p.length();i<s.length();i++){
            tAlpCount[s.charAt(i)-'a']++;
            tAlpCount[s.charAt(i-p.length())-'a']--;
            
            if(Arrays.equals(pAlpCount, tAlpCount))res.add(i-p.length()+1);

        }
        return res;
    }

    public static void main(String[] args) {
        System.out.println(new Answer().findAnagrams("ab","cd"));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 03-sliding-window/03-character-frequency-matching
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
