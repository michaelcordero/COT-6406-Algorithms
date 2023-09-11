import java.util.Map;

/**
 * This AVLTree implementation inherits it's operations from the BinarySearchTree. The two major differences in this
 * class is that AVLTrees are defined as "self-balancing" trees. Meaning that after every insertion or deletion, the
 * tree will re-balance itself, without the user's request. The second major difference is that it's AVLTreeNode use
 * the computed size property to solve the kth smallest element problem in O(log n) time.[
 * @param <K> - type of keys
 * @param <V> - type of values
 */
public class AVLTree<K extends Comparable<K>,V> extends BinarySearchTree<K,V> {
    ///////////////////////////////////////////////
    // properties
    //////////////////////////////////////////////

    ///////////////////////////////////////////////
    // constructors
    //////////////////////////////////////////////
    public AVLTree() {
    }

    ///////////////////////////////////////////////
    // inner node class
    //////////////////////////////////////////////
    static class AVLTreeNode<K,V> implements BinaryTreeNode<K,V> {
        /////////////////////////////////////////////
        // Properties
        ////////////////////////////////////////////
        K key;
        V value;
        protected AVLTreeNode<K,V> parent;
        protected AVLTreeNode<K,V> left;
        protected AVLTreeNode<K,V> right;
        ////////////////////////////////////////////
        // constructors
        ////////////////////////////////////////////
        AVLTreeNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        ////////////////////////////////////////////
        // Methods
        ///////////////////////////////////////////
        @Override
        public AVLTreeNode<K, V> parent() {
            return parent;
        }

        @Override
        public AVLTreeNode<K, V> left() {
            return left;
        }

        @Override
        public AVLTreeNode<K, V> right() {
            return right;
        }

        @Override
        public void setLeft(BinaryTreeNode<K, V> left) {
            this.left = (AVLTreeNode<K, V>) left;
        }

        @Override
        public void setRight(BinaryTreeNode<K, V> right) {
            this.right = (AVLTreeNode<K, V>) right;
        }

        @Override
        public void setParent(BinaryTreeNode<K, V> parent) {
            this.parent = (AVLTreeNode<K, V>) parent;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            return value;
        }

        /**
         * The size method computes the size of a node, which is one plus the sum of it's left and right
         * child nodes. The text suggests modifying the insert and deletes to update the size, but this
         * method proves that is unnecessary.
         * @return - the number of nodes in the subtree rooted at the node plus 1.
         */
        public int size() {
            int leftSize = left != null ? left().size() : 0;
            int rightSize = right != null ? right().size() : 0;
            return 1 + leftSize + rightSize;
        }
    }
    ///////////////////////////////////////////////
    // Private
    //////////////////////////////////////////////

    /**
     * This solution finds the kth smallest element key in O(log n) time.
     * The algorithm is basically leveraging the new size property to predict
     * if there are any other subtree nodes to be checked for the kth key's
     * placement.
     *
     * Credits go to #5 Ch. 25 of Intro to Java Programming by Liang
     * @param kth - the kth ordered element in the sequence of the AVL tree.
     * @param node - the current node being iterated upon
     * @return - returns null if kth < 1 or kth > the size of the tree, otherwise the
     * kth smallest element.
     */
    private K find(int kth, AVLTreeNode<K,V> node) {
        // kth is in one of the current node's children
        if (node.left() == null && kth == 1) {
            return node.getKey();
        }
        if (node.left() == null && kth == 2) {
            return node.right().getKey();
        }
        // check left tree
        if (kth <= node.left().size() ) {
            return find(kth,node.left());
        }
        // kth is the current node
        if (kth == node.left().size() + 1 ) {
            return node.getKey();
        }
        // kth is in the right subtree
        if (kth > node.left().size() + 1) {
            return find(kth - node.left().size() - 1, node.right());
        }
        return null;
    }

    ///////////////////////////////////////////////
    // Map contract
    //////////////////////////////////////////////

    @Override
    public V put(K key, V value) {
        V to_return = super.put(key,value);
        if (!isBalanced()) {
            balance();
        }
        return to_return;
    }

    @Override
    public V remove(Object key) {
        V to_return = super.remove(key);
        if (!isBalanced()) {
            balance();
        }
        return to_return;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        balance();
    }

    ///////////////////////////////////////////////
    // Public API
    //////////////////////////////////////////////
    public K find(int kth) {
        AVLTreeNode<K,V> local_root = ((AVLTreeNode<K, V>) root());
        // check once before running the algorithm, if kth key is out of bounds
        if (kth < 1 || kth > local_root.size()) {
            throw new IllegalArgumentException("kth key out of bounds");
        }
        return find(kth, local_root);
    }

}
