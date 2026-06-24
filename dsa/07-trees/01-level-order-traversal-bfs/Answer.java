// Binary Tree Level Order Traversal  —  Tree Traversal (DFS and BFS) / Level Order Traversal (BFS)
// Problem & test cases: see QUESTION.md

import java.util.*;

public class Answer {

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> store=new ArrayList<>();
        storeValuesByLevel(root,store,0);
        return store;
    }

    public void storeValuesByLevel(TreeNode node,List<List<Integer>> store,int level){
        if(node==null)return;
        if(store.size()<=level){
            store.add(level, new ArrayList<>());
        }

        store.get(level).add(Integer.valueOf(node.val));
        storeValuesByLevel(node.left,store,level+1);
        storeValuesByLevel(node.right,store,level+1);
    }

    public static void main(String[] args) {
        // TODO: test with the examples in QUESTION.md
        // e.g.  System.out.println(new Answer()....);
    }
}

/* ===== Run the tests =====
 *   In this folder:   javac Answer.java Test.java && java Test
 *   From dsa/      :   bash run-tests.sh 07-trees/01-level-order-traversal-bfs
 *   -> Test.java runs many corner cases and prints [PASS]/[FAIL] + "Summary: X/Y passed"
 * ========================= */
