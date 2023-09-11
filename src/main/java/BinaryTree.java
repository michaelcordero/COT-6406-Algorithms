import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public interface BinaryTree<K, V> extends Map<K, V> {
    ///////////////////////////////////////////////
    // inner node class
    //////////////////////////////////////////////
    interface BinaryTreeNode<K, V> extends Map.Entry<K, V> {
        // dutiful methods
        BinaryTreeNode<K, V> parent();

        BinaryTreeNode<K, V> left();

        BinaryTreeNode<K, V> right();
        // setters
        void setLeft(BinaryTreeNode<K,V> left);
        void setRight(BinaryTreeNode<K,V> right);
        void setParent(BinaryTreeNode<K,V> parent);

        // quality of life methods
        default boolean isSingleParent() {
            return left() != null ^ right() != null;
        }

        default boolean isParent() {
            return left() != null || right() != null;
        }

        default boolean isChild() {
            return parent() != null;
        }

        default boolean isRoot() {
            return parent() == null;
        }

        default boolean isLeaf() {
            return left() == null && right() == null;
        }
    }

    ///////////////////////////////////////////////
    // traversal types enumeration
    //////////////////////////////////////////////
    enum TraversalType {
        BREADTH, DEPTH, PREORDER, INORDER, POSTORDER
    }
    ///////////////////////////////////////////////
    // definition methods
    ///////////////////////////////////////////////
    boolean isBalanced();
    void balance();
    int height();
    V min();
    V max();
    void traverser(TraversalType traversalType, Consumer<BinaryTreeNode<K, V>> function);


    //////////////////////////////////////////////
    // CISC-503 assignment-6 methods
    //////////////////////////////////////////////
    int nodeCount();
    int leavesCount();
    void swapTrees();
    int singleParent();
    void nodeHeight();
    //////////////////////////////////////////////
    // CISC-503 assignment-7 methods
    //////////////////////////////////////////////
    void displayItemsInRange(K lesser, K greater);
    K split(K theKey, BinaryTree<K,V> lesser, BinaryTree<K,V> greater);
    //////////////////////////////////////////////
    // COT-6406 method
    //////////////////////////////////////////////
    void iyengar();
    void traverse_bind(BinaryTreeNode<K,V> root, List<BinaryTreeNode<K,V>> link, AtomicInteger node_count);
    void grow(AtomicInteger low, AtomicInteger high, List<BinaryTreeNode<K,V>> link, AtomicInteger m, AtomicReference<BinaryTreeNode<K, V>> ansl, AtomicReference<BinaryTreeNode<K,V>> ansr);
}
