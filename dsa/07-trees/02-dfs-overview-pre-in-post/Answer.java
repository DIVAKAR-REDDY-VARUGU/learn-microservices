// Binary Tree Postorder Traversal  —  Tree Traversal (DFS and BFS) / DFS Overview (Pre/In/Post)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> res=new ArrayList<>();

        postorderTraversal(root,res);
        return res;
    }

    public void postorderTraversal(TreeNode root, List<Integer> res){
        if(root==null)return;
        postorderTraversal(root.left,res);
        postorderTraversal(root.right,res);
        res.add(root.val);

    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 07-trees/02-dfs-overview-pre-in-post
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
