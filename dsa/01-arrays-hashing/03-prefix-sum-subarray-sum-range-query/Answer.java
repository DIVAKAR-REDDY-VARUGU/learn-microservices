// Subarray Sum Equals K  —  Arrays & Hashing (Array/Matrix Manipulation) / Prefix Sum - Subarray Sum / Range Query
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int subarraySum(int[] nums, int k) {

        int res=0;
        int sum=0;
        int need;
        LinkedHashMap<Integer,Integer> prefixSum_visitedCount_map=new LinkedHashMap<>((int)(nums.length/0.75)+1);
        prefixSum_visitedCount_map.put(0,1);

        for(int i=0;i<nums.length;i++){
            sum+=nums[i];
            need=sum-k;
            res+=prefixSum_visitedCount_map.getOrDefault(need,0); // alter we can write like below 
            // if(prefixSum_visitedCount_map.containsKey(need)){
            //     res+=prefixSum_visitedCount_map.get(need);
            // }
            prefixSum_visitedCount_map.merge(sum, 1, (a,b)->a+b);
        }
        return res;
    }

    public static void main(String[] args) {
        Answer obj = new Answer();
        System.out.println(obj.subarraySum(new int[] { 3, -4, 1, 4, 1, -5, 2, -6, 2, 7, -3, 8, 1 }, 6));
    }
}

/*
 * ===== Run the tests =====
 * In this folder: javac Answer.java Test.java && java Test
 * From dsa/ : bash run-tests.sh
 * 01-arrays-hashing/03-prefix-sum-subarray-sum-range-query
 * -> Test.java runs many corner cases and prints [PASS]/[FAIL] +
 * "Summary: X/Y passed"
 * =========================
 */
