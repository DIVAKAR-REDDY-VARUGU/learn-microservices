// Course Schedule  —  Graph Traversal (DFS and BFS) / DFS Cycle Detection (Directed)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // keep all status as unvisited 
        int[] state=new int[numCourses]; // by default 0 - unvisited , 1 - in process , 2 - done

        // prepare graph
        List<List<Integer>> graph=new ArrayList<>();
        for(int i=0;i<numCourses;i++) graph.add(i,new ArrayList<>());   // initializing graph 
        
        for(int[] courses:prerequisites){
            int first=courses[1];
            int later=courses[0];
            graph.get(first).add(later);
        }

        
        for(int i=0;i<numCourses;i++){
            if(foundCycle(state,i,graph))return false;
        }
        return true;
    }
    public boolean foundCycle(int[] state,int currentCourse,List<List<Integer>> graph){
        if(state[currentCourse]==2)return false;
        if(state[currentCourse]==1)return true;
        state[currentCourse]=1;
        for(int childC: graph.get(Integer.valueOf(currentCourse))){
            if(foundCycle(state,childC,graph))return true;
            
        }
        state[currentCourse]=2;

        return false;
    }


    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 09-graphs/03-dfs-cycle-detection-directed
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
