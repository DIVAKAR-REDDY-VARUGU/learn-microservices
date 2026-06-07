// Set Matrix Zeroes  —  Arrays & Hashing (Array/Matrix Manipulation) / Set Matrix Zeroes (In-place Marking)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public void setZeroes(int[][] matrix) {
        int r=matrix.length;
        int c=matrix[0].length;
        boolean col0=false;
        // marking flags 
        for(int i=0;i<r;i++){
            if(matrix[i][0]==0&&!col0)col0=true;
            for(int j=1;j<c;j++){
                if(matrix[i][j]==0){
                    matrix[i][0]=0;     // making row start = 0
                    matrix[0][j]=0;     // making col start = 0
                }
            }
        }


        for(int i=r-1;i>=0;i--){
            for(int j=c-1;j>=1;j--){
                if(matrix[i][0]==0||matrix[0][j]==0){
                    matrix[i][j]=0;
                }
            }
            if(col0)matrix[i][0]=0;
        }

    }

    public static void main(String[] args) {
        int [][] matrix = {
            {1, 1, 1},
            {1, 0, 1},
            {1, 1, 1}
        };
        new Answer().setZeroes(matrix);
        System.out.println(Arrays.deepToString(matrix));
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/09-set-matrix-zeroes-in-place-marking
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
