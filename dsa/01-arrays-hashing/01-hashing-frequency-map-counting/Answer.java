// Top K Frequent Elements  —  Arrays & Hashing (Array/Matrix Manipulation) / Hashing - Frequency Map / Counting
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] topKFrequent2(int[] nums, int k) {

        HashMap<Integer, Integer> hash = new HashMap<>();
        for (int i : nums) hash.merge(Integer.valueOf(i), 1, Integer::sum);




        int result[] = new int[k];
        return result;
    }


    private void bucketSort(HashMap<Integer,Integer> hash,int result[]){
        

    }


    public int[] topKFrequent1(int[] nums, int k) {

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
        System.out.println(Arrays.toString(new Answer().topKFrequent2(new int[] { 4, 1, -1, 2, -1, 2, 3 }, 2))); // [-1, 2]
    }
}
