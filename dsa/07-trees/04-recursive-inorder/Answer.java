// Binary Tree Inorder Traversal  —  Tree Traversal (DFS and BFS) / Recursive Inorder
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res=new ArrayList<>();
        inorderTraversal(root,res);
        return res;
    }
    public void inorderTraversal(TreeNode root,List<Integer> res){
        if(root==null)return;
        inorderTraversal(root.left,res);
        res.add(root.val);
        inorderTraversal(root.right,res);
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 07-trees/04-recursive-inorder
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
