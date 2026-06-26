// Serialize and Deserialize Binary Tree  —  Tree Traversal (DFS and BFS) / Serialization and Deserialization
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    int ind=0;


    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        preOrderSerialize(root,sb);
        return sb.toString();
    }
    public void preOrderSerialize(TreeNode node, StringBuilder sb){
        if(node==null){
            sb.append("#,");
            return;
        }
        sb.append(node.val).append(",");
        preOrderSerialize(node.left,sb);
        preOrderSerialize(node.right,sb);
    }

    public TreeNode deserialize(String str){
        String[] arr=str.split(",");
        ind=0;
        return buildTree(arr);

    }
    public TreeNode buildTree(String[] arr){
        String value=arr[ind++];
        if(value.equals("#")){
            return null;
        }
        TreeNode node=new TreeNode(Integer.parseInt(value));
        node.left=buildTree(arr);
        node.right=buildTree(arr);
        return node;
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 07-trees/07-serialization-and-deserialization
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
