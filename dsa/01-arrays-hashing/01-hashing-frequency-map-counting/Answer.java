// Top K Frequent Elements  —  Arrays & Hashing (Array/Matrix Manipulation) / Hashing - Frequency Map / Counting
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {
    public int[] topKFrequent(int[] nums, int k) {

        HashMap<Integer, Integer> hash = new HashMap<>();
        for (Integer i : nums) {
            if (hash.containsKey(i)) {
                hash.merge(i, 1, Integer::sum);
            } else {
                hash.put(i, 1);
            }
        }
        // System.out.println("Size Of Hash is: "+hash.size());
        int result[] = new int[k];
        // System.out.println("Size Of result is: "+result.length);
        int i = 0;
        for (Integer key : hash.keySet()) {
            // System.out.println("result["+i+"] = "+key);
            result[i++] = key;
            if (i == k)
                break;
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
