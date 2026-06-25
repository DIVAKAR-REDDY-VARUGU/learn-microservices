// Lowest Common Ancestor of a Binary Tree  —  Tree Traversal (DFS and BFS) / Lowest Common Ancestor (LCA)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    // public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    //     if(root==null||root==p||root==q)return root;
    //     TreeNode l=lowestCommonAncestor(root.left,p,q);
    //     TreeNode r=lowestCommonAncestor(root.right,p,q);
    //     return (l!=null&&r!=null)?root:(l!=null)?l:r;
    // }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        HashMap<TreeNode,TreeNode> childToParent=new HashMap<>();
        ArrayDeque<TreeNode> stack=new ArrayDeque<>();
        childToParent.put(root,null);
        stack.add(root);

        while(!childToParent.containsKey(p)||!childToParent.containsKey(q)){
            TreeNode node=stack.pollLast();
            if(node.left!=null){
                childToParent.put(node.left,node);
                stack.add(node.left);
            }
            if(node.right!=null){
                childToParent.put(node.right,node);
                stack.add(node.right);
            }
        }
        HashMap<TreeNode,TreeNode> ancestors=new HashMap<>();
        while (p!=null) {
            ancestors.put(p,childToParent.get(p));
            p=childToParent.get(p);
        }

        while (!ancestors.containsKey(q)) {
            q=childToParent.get(q);
        }
        return q;



    }




    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 07-trees/06-lowest-common-ancestor-lca
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
