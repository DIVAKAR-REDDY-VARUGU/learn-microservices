
import java.util.*;

public class Answer {

    public List<Integer> spiralOrder(int[][] matrix) {
        int top=0;
        int left=0;
        int bottom = matrix.length-1;
        int right=matrix[0].length-1;

        ArrayList<Integer> res=new ArrayList<>();

        while(top<=bottom && left<=right){
            for(int col=left;col<=right;col++){
                res.add(matrix[top][col]);
            }
            top++;
            for(int row=top; row<=bottom;row++){
                res.add(matrix[row][right]);
            }
            right--;

            if(top<=bottom){
                for(int col=right;col>=left;col--){
                    res.add(matrix[bottom][col]);
                }
                bottom--;
            }
            if(left<=right){
                for(int row=bottom;row>=top;row--){
                    res.add(matrix[row][left]);
                }
                left++;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        Answer sol = new Answer();
        int[][] matrix = {
            { 1, 2, 3 },
            { 4, 5, 6 },
            { 7, 8, 9 }
        };
        System.out.println(sol.spiralOrder(matrix));  // expected: [1, 2, 3, 6, 9, 8, 7, 4, 5]
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 01-arrays-hashing/10-spiral-traversal
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
