import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class TestBinarySearchTree {

    @Test
    public void test_traverse_bind() {
        BinarySearchTree<Integer,Integer> tree = new BinarySearchTree<>();
        // add left and right children
        tree.put(5,5);
        tree.put(6,6);
        tree.put(4,4);
        tree.put(7,7);
        tree.put(3, 3);
        tree.put(8, 8);
        tree.put(2, 2);
        tree.put(1,1);
        tree.put(9, 9);
        tree.put(0, 0);
        tree.put(10, 10);
        int actual_size = 11;
        List<BinaryTree.BinaryTreeNode<Integer, Integer>> link = new ArrayList<>();
        AtomicInteger node_count = new AtomicInteger(0);
        tree.traverse_bind(tree.root(), link, node_count);
        assert link.size() == actual_size;
    }

    @Test
    public void test_iyengar() {
        BinarySearchTree<Integer,Integer> tree = new BinarySearchTree<>();
        // create unbalanced tree
        for (int i = 0; i < 12; i++) {
            System.out.printf("putting: %d -> ", i);
            tree.put(i,i);
        }
        System.out.println();
        tree.chang_iyengar();
        tree.nodeHeight();
        // tree.values().forEach(System.out::println);
        assert tree.isBalanced();
    }
}
