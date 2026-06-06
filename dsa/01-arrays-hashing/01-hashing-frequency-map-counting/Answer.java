// Top K Frequent Elements  —  Arrays & Hashing (Array/Matrix Manipulation) / Hashing - Frequency Map / Counting
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {
    public int[] topKFrequent(int[] nums, int k) {

        LinkedHashMap<Integer,Integer> keyCountMap=new LinkedHashMap<>((int)(nums.length/0.75)+1);

        for(int val:nums)keyCountMap.merge(Integer.valueOf(val),1,(a,b)->a+b );
        
        ArrayList<Map.Entry<Integer,Integer>> entries=new ArrayList<>(keyCountMap.entrySet());
        entries.sort((b,a)->Integer.compare(a.getValue(),b.getValue()));

        int [] result=new int[k];
        int i=0;
        for(Map.Entry<Integer,Integer> entry:entries){
            if(i==k)break;
            result[i++]=entry.getKey();
        }

       return result;

    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(new Answer().topKFrequent(new int[] { 4, 1, -1, 2, -1, 2, 3 }, 2))); // [-1, 2]
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/01-hashing-frequency-map-counting
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
