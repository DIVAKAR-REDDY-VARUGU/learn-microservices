// Number of Islands  —  Graph Traversal (DFS and BFS) / DFS/BFS Connected Components
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    int[][] directions=new int[][]{
        {-1,0},
        {1,0},
        {0,1},
        {0,-1}
    };
    int rL,cL,count;
    

    public int numIslands(char[][] grid) {
        rL=grid.length;
        cL=grid[0].length;
        count=0;
        
        for(int i=0;i<rL;i++){
            for(int j=0;j<cL;j++){
                if(grid[i][j]=='1')submergeIsland(i,j,grid);
            }
        }
        return count;
    }
    public void submergeIsland(int r,int c,char[][] grid){
        count+=1;
        ArrayDeque<int[]> queue=new ArrayDeque<>();
        queue.offer(new int[]{r,c});
        grid[r][c]='0';
        while (!queue.isEmpty()) {
            int[] cur=queue.pollFirst();
            for(var dir:directions){
                int nr=cur[0]+dir[0];
                int nc=cur[1]+dir[1];
                if((nr)<rL && (nr>=0) && (nc<cL) && (nc>=0) && grid[nr][nc]=='1'){
                    grid[nr][nc]='0';
                    queue.offer(new int[]{nr,nc});
                }
            }
        }
    }
    

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 09-graphs/01-dfs-bfs-connected-components
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
