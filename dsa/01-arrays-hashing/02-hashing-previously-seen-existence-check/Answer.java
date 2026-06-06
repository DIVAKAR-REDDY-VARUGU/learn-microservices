// Two Sum  —  Arrays & Hashing (Array/Matrix Manipulation) / Hashing - Previously Seen / Existence Check
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] twoSum(int[] nums, int target) {

        LinkedHashMap<Integer,Integer> valueIndexMap=new LinkedHashMap<>();

        for(int i=0;i<nums.length;i++){
            Integer need=target-nums[i];
            if(valueIndexMap.containsKey(need)) return new int[]{valueIndexMap.get(need),i};
            valueIndexMap.put(nums[i],i);
        }

        return new int[]{};
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        System.out.println(Arrays.toString(new Answer().twoSum(new int[] { 2, 7, 11, 15 }, 9)));

    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/02-hashing-previously-seen-existence-check
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
