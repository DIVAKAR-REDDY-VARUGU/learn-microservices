// Binary Tree Preorder Traversal  —  Tree Traversal (DFS and BFS) / Recursive Preorder
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> res=new ArrayList<>();
        preorderTraversal(root,res);
        return res;
    }
    public void preorderTraversal(TreeNode root,List<Integer> res){
        if(root==null)return;
        res.add(root.val);
        preorderTraversal(root.left,res);
        preorderTraversal(root.right,res);
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 07-trees/03-recursive-preorder
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
