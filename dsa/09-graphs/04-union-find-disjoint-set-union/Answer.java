// Number of Provinces  —  Graph Traversal (DFS and BFS) / Union-Find (Disjoint Set Union)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public int findCircleNum(int[][] isConnected) {
        int r=isConnected.length;
        int c=isConnected[0].length;
        int[] leadersMap=new int[r];
        for(int i=0;i<r;i++)leadersMap[i]=i;

        int province=r;

        for(int i=0;i<r;i++){
            for(int j=i+1;j<c;j++){
                if(isConnected[i][j]==1){
                    int iLeader=findLeader(leadersMap, i);
                    int jLeader=findLeader(leadersMap, j);
                    if(iLeader!=jLeader){
                        leadersMap[jLeader]=iLeader;
                        province--;
                    }
                }
            }
        }
        return province;
    }
    public int findLeader(int[] leadersMap,int target){
        while(leadersMap[target]!=target){
            target=leadersMap[target];
        }
        return target;
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 09-graphs/04-union-find-disjoint-set-union
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
