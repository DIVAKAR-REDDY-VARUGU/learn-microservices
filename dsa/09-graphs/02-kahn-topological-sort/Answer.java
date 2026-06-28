// Course Schedule II  —  Graph Traversal (DFS and BFS) / Kahn Topological Sort
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        ArrayList<ArrayList<Integer>> graph=new ArrayList<>();
        for(int i=0;i<numCourses;i++)graph.add(i,new ArrayList<>());
        int[] inDegree=new int[numCourses];
        for(int[] edge:prerequisites){
            int pre=edge[1];
            int course=edge[0];
            graph.get(pre).add(Integer.valueOf(course));
            inDegree[course]++;
        }

        ArrayDeque<Integer> queue=new ArrayDeque<>();
        for(int i=0;i<inDegree.length;i++)if(inDegree[i]==0)queue.offer(Integer.valueOf(i));

        int[] result=new int[numCourses];
        int ind=0;

        while(!queue.isEmpty()){
            Integer preCourse=queue.poll();
            result[ind++]=(int)preCourse;
            ArrayList<Integer> courses=graph.get(preCourse);
            for(Integer course:courses){
                inDegree[(int)course]--;
                if(inDegree[(int)course]==0)queue.offer(course);
            }
        }

        return (ind==numCourses)?result:new int[0];

    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 09-graphs/02-kahn-topological-sort
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
