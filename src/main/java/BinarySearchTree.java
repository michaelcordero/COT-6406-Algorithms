import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * This Binary Search Tree implementation ensures O(log n) runtime efficiency for most of the common operations
 * associated with trees. Binary Search Trees can be implemented as sets or maps, but to resolve any ambiguity on
 * the order of the objects to be contained therein, the key-value setup was chosen.
 * Clients of this class have the traverser method available at their disposal, which should handle most needs.
 * Since: 3/18/2021
 * Author: Michael Cordero
 * @param <K> - The type of keys
 * @param <V> - The type of values
 */
public class BinarySearchTree<K extends Comparable<K>,V> implements BinaryTree<K,V> {
    ///////////////////////////////////////////////
    // properties
    //////////////////////////////////////////////
    private BinaryTreeNode<K,V> root;

    ///////////////////////////////////////////////
    // constructors
    //////////////////////////////////////////////
    public BinarySearchTree() {
        this.root = null;
    }

    ///////////////////////////////////////////////
    // accessor
    //////////////////////////////////////////////
    public BinaryTreeNode<K, V> root() {
        return root;
    }

    ///////////////////////////////////////////////
    // inner node class
    //////////////////////////////////////////////
    protected static class BinarySearchTreeNode<K,V> implements BinaryTreeNode<K,V> {
        /////////////////////////////////////////////
        // Properties
        ////////////////////////////////////////////
        K key;
        V value;
        protected BinarySearchTreeNode<K,V> parent;
        protected BinarySearchTreeNode<K,V> left;
        protected BinarySearchTreeNode<K,V> right;

        ////////////////////////////////////////////
        // constructors
        ////////////////////////////////////////////
        BinarySearchTreeNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
        }

        BinarySearchTreeNode() {
            this.key = null;
            this.value = null;
            this.left = null;
            this.right = null;
            this.parent = null;
        }


        ////////////////////////////////////////////
        // public api
        ////////////////////////////////////////////
        @Override
        public BinaryTreeNode<K, V> parent() {
            return parent;
        }

        @Override
        public BinaryTreeNode<K, V> left() {
            return left;
        }

        @Override
        public BinaryTreeNode<K, V> right() {
            return right;
        }

        @Override
        public void setLeft(BinaryTreeNode<K, V> left) {
            this.left = (BinarySearchTreeNode<K, V>) left;
        }

        @Override
        public void setRight(BinaryTreeNode<K, V> right) {
            this.right = (BinarySearchTreeNode<K, V>) right;
        }

        @Override
        public void setParent(BinaryTreeNode<K, V> parent) {
            this.parent = (BinarySearchTreeNode<K, V>) parent;
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
    }

    ///////////////////////////////////////////////
    // Private
    //////////////////////////////////////////////
    private int amount(BinaryTreeNode<K,V> node) {
        if (node == null) {
            return 0;
        }
        return 1 + amount(node.left()) + amount(node.right());
    }

    private void innerSwap(BinaryTreeNode<K,V> node) {
        if ( node != null && !node.isLeaf()) {
            BinaryTreeNode<K,V> temp = node.left();
            node.setLeft(node.right());
            node.setRight(temp);
            innerSwap(node.left());
            innerSwap(node.right());
        }
    }

    private int innerHeight(BinaryTreeNode<K,V> node) {
        if (node == null) {
            return -1;
        } else {
            return Math.max(innerHeight(node.left()), innerHeight(node.right())) + 1;
        }
    }

    private void breadth(BinaryTreeNode<K,V> node, Consumer<BinaryTreeNode<K,V>> consumer) {
        Queue<BinaryTreeNode<K,V>> queue = new LinkedBlockingQueue<>();
        if (node != null) {
            queue.add(node);
            while (!queue.isEmpty()) {
                node = queue.poll();
                // visit the node, i.e. perform the operation
                if (consumer != null) {
                    consumer.accept(node);
                }
                // enqueue the children from left to right
                if (node.left() != null) {
                    queue.add(node.left());
                }
                if (node.right() != null) {
                    queue.add(node.right());
                }
            }
        }
    }

    private void depth(BinaryTreeNode<K,V> node, Consumer<BinaryTreeNode<K,V>> consumer) {
        Stack<BinaryTreeNode<K,V>> stack = new Stack<>();
        if (node != null) {
            stack.push(node);
            while (!stack.empty()) {
                node = stack.pop();
                // visit the node, i.e. perform the operation
                if (consumer != null) {
                    consumer.accept(node);
                }
                // push the children from right to left
                if (node.right() != null) {
                    stack.push(node.right());
                }
                if (node.left() != null) {
                    stack.push(node.left());
                }
            }
        }
    }

    private void preorder(BinaryTreeNode<K,V> node, Consumer<BinaryTreeNode<K,V>> consumer) {
        // Root. Left. Right.
        if (node != null) {
            if (consumer != null) {
                consumer.accept(node);
            }
            preorder(node.left(), consumer);
            preorder(node.right(), consumer);
        }
    }

    private void inorder(BinaryTreeNode<K,V> node, Consumer<BinaryTreeNode<K,V>> consumer) {
        // Left. Root. Right.
        if (node != null) {
            inorder(node.left(),consumer);
            if (consumer != null) {
                consumer.accept(node);
            }
            inorder(node.right(),consumer);
        }
    }

    private void postorder(BinaryTreeNode<K,V> node, Consumer<BinaryTreeNode<K,V>> consumer) {
        // Left. Right. Root.
        if (node != null) {
            postorder(node.left(),consumer);
            postorder(node.right(),consumer);
            if (consumer != null) {
                consumer.accept(node);
            }
        }
    }

    /**
     * Inspired by Geeks for Geeks
     * @param low - starting point index
     * @param high - end point index
     * @param list - structure to be indexed
     */
    private BinaryTreeNode<K,V> internalBalance(int low, int high, List<BinaryTreeNode<K,V>> list ) {
        if (low > high) {
            return null;
        }
        // get the middle element and make it root
        int mid = (low + high) /2;
        BinaryTreeNode<K,V> current = list.get(mid);
        // recursively construct the left subtree and make it left child of root
        current.setLeft(internalBalance(low, mid - 1, list ));
        if (current.left() != null) {
            current.left().setParent(current);
        }
        // recursively construct the right subtree and make it right child of the root
        current.setRight(internalBalance(mid + 1, high, list));
        if (current.right() != null) {
            current.right().setParent(current);
        }
        return current;
    }

    /**
     * This method exists to allow class implementers to specify the node for which to check the balance factor,
     * usually the node will be root, but for recursive tree rotations, such as LR-RL in the AVLTree, this
     * parameterization will be needed.
     * @param node - the node for which to check the balance factor
     * @return balance factor
     */
    private int balanceFactor(BinaryTreeNode<K,V> node) {
        int left_height = node.left() != null ? innerHeight(node.left()) : 0;
        int right_height = node.right() != null ? innerHeight(node.right()) : 0;
        return left_height - right_height;
    }

    /**
     * Problem:
     * Extend the class BinarySearchTree by adding a public method displayItemsInRange (l, w) that outputs in ascending
     * order of node value, all the nodes in a BST whose values, v lie in the range l < v < w . Use recursion and avoid
     * entering any subtrees that can't contain any elements in the desired range. You must also write a test program
     * that builds a BST and tests your method (see below).
     *
     * Requirement #1: elements to be printed must fall within the range. l < v < w.
     * Requirement #2: output elements in ascending order.
     * Requirement #3: use recursion.
     * Requirement #4: avoid entering subtrees that can't contain elements in the desired range.
     *
     * Solution #1: Range check before an item is printed.
     * Solution #2: Inorder traversal satisfies the ascending order requirement.
     * Solution #3: This inner method accepts a BinaryTreeNode as an argument, with each successive call,
     * it visits that node.
     * Solution #4: Lower bounds checking occurs before traversal into the left subtree. Upper bounds checking
     * before entering nodes of the right subtree. Using >= & <= because, the problem says the value must fall between
     * the range. If you have a range of 4 < value < 10, then the valid values are: 5,6,7,8,9, not 4 and 10! Because
     * 4 is not greater than 4 and 10 is not less than 10.
     *
     * @param node  - currently being traversed node, root is expected to be passed in first.
     * @param lower - represents the lower limit of the range
     * @param upper - represents the upper limit of the range
     */
    private void innerDisplayItemsInRange(BinaryTreeNode<K,V> node, K lower, K upper ) {
        // comparators have to be re-computed for each node's key
        if (node != null) {
            Comparable<K> currentKey = node.getKey();
            int lessThan = currentKey.compareTo(lower);
            int greaterThan = currentKey.compareTo(upper);
            // check left, but first answer the question: Is this currentKey less than or equal to the lower bound?
            // If no, then the algorithm is free to traverse.
            if (!(lessThan <= 0)) {
                innerDisplayItemsInRange(node.left(), lower, upper);
            }
            // check root, does this currentKey falls within the range of the upper and lower bound?
            // If yes, then go ahead and print it.
            if (!(lessThan <= 0) && !(greaterThan >= 0)) {
                System.out.println(node.getValue());
            }
            // check right, but first answer the question: Is this currentKey greater than or equal to the upper bound?
            // If no, then the algorithm is free to traverse.
            if (!(greaterThan >= 0)) {
                innerDisplayItemsInRange(node.right(), lower, upper);
            }
        }
    }

    ///////////////////////////////////////////////
    // Map contract
    //////////////////////////////////////////////
    @Override
    public int size() {
        return amount(root);
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean found = false;
        BinaryTreeNode<K,V> itr = root;
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        while (!found && itr != null) {
            int comparison = k.compareTo(itr.getKey());
            if (comparison == 0) {
                found = true;
            }
            if (comparison < 0) {
                itr = itr.left();
            } else if (comparison > 0) {
                itr = itr.right();
            }
        }
        return found;
    }

    @Override
    public boolean containsValue(Object value) {
        AtomicBoolean found = new AtomicBoolean(false);
        traverser(TraversalType.PREORDER, (node) -> {
            if (node.getValue() == value) found.set(true);
        });
        return found.get();
    }

    @Override
    public V get(Object key) {
        BinaryTreeNode<K,V> itr = root;
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        while (itr != null) {
            int comparison = k.compareTo(itr.getKey());
            if (comparison < 0) {
                itr = itr.left();
            } else if (comparison > 0) {
                itr = itr.right();
            } else {
                return itr.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        // Item 52: Refer to objects by their Interfaces. Effective Java 2nd edition by Josh Bloch.
        BinaryTreeNode<K,V> node;
        // So this class can be extended by other types of trees and their nodes
        if (this instanceof AVLTree ) {
            node = new AVLTree.AVLTreeNode<>(key, value);
        } else {
            node = new BinarySearchTreeNode<>(key,value);
        }
        if (root == null) {
            root = node;
            return root.getValue();
        }
        boolean inserted = false;
        BinaryTreeNode<K,V> itr = root;
        V previous = null;
        while (!inserted && itr != null) {
            int comparison = key.compareTo(itr.getKey());
            if (comparison < 0) {
                if (itr.left() == null) {
                    itr.setLeft(node);
                    node.setParent(itr);
                    inserted = true;
                } else {
                    itr = itr.left();
                }
            } else if( comparison > 0) {
                if (itr.right() == null) {
                    itr.setRight(node);
                    node.setParent(itr);
                    inserted = true;
                } else {
                    itr = itr.right();
                }
            } else {
                // keep key, overwrite previous value
                previous = itr.setValue(node.getValue());
                inserted = true;
            }
        }
        return previous;
    }

    @Override
    public V remove(Object key) {
        boolean found = false;
        BinaryTreeNode<K,V> itr = root;
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        while (!found && itr != null) {
            int comparison = k.compareTo(itr.getKey());
            if (comparison < 0) {
                itr = itr.left();
            } else if (comparison > 0) {
                itr = itr.right();
            }
            // key object found
            else {
                found = true;
                // special case for root
                if (itr.isRoot()) {
                    List<BinaryTreeNode<K,V>> list =  new ArrayList<>();
                    traverser(TraversalType.INORDER, list::add);
                    list.remove(root);
                    this.root = null;
                    internalBalance(0, list.size() - 1, list);
                } else {
                    BinaryTreeNode<K,V> parent = itr.parent();
                    BinaryTreeNode<K,V> left = itr.left();
                    BinaryTreeNode<K,V> right = itr.right();
                    // re-attaching the child nodes to the node to be removed parent.
                    if (right != null) {
                        if (itr == parent.left()) {
                            parent.setLeft(right);
                        } else {
                            parent.setRight(right);
                        }
                    } else {
                        if (itr == parent.right()) {
                            parent.setRight(left);
                        } else {
                            parent.setLeft(left);
                        }
                    }
                }
            }
        }
        return itr != null ? itr.getValue() : null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry: m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.root = null;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        traverser(TraversalType.INORDER, (node) -> keys.add(node.getKey()));
        return keys;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();
        traverser(TraversalType.INORDER, (node) -> values.add(node.getValue()) );
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K,V>> entries = new HashSet<>();
        traverser(TraversalType.INORDER, (node) -> entries.add(new Map.Entry<K, V>() {
            @Override
            public K getKey() {
                return node.getKey();
            }

            @Override
            public V getValue() {
                return node.getValue();
            }

            @Override
            public V setValue(V value) {
                return node.setValue(value);
            }
        }));
        return entries;
    }

    ///////////////////////////////////////////////
    // End Map contract
    //////////////////////////////////////////////

    ///////////////////////////////////////////////
    // Public API
    //////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinarySearchTree<?, ?> that = (BinarySearchTree<?, ?>) o;
        return Objects.equals(root, that.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }

    @Override
    public boolean isBalanced() {
        int balance_factor = balanceFactor(root);
        return -1 <= balance_factor && balance_factor <=1;
    }

    @Override
    public void balance() {
        List<BinaryTreeNode<K,V>> list =  new ArrayList<>();
        traverser(TraversalType.INORDER, list::add);
        this.root = internalBalance(0, list.size() - 1, list);
    }

    /**
     * This method computes the height of the tree.
     * Definition: The height of a node is the number of nodes (excluding the node) on the longest path from the node
     * to a leaf.
     * @return - an int representing the height of the tree.
     */
    @Override
    public int height() {
        return innerHeight(root);
    }

    @Override
    public V min() {
        BinaryTreeNode<K,V> itr = root;
        while (itr.left() != null) {
            itr = itr.left();
        }
        return itr.getValue();
    }

    @Override
    public V max() {
        BinaryTreeNode<K,V> itr = root;
        while (itr.right() != null) {
            itr = itr.right();
        }
        return itr.getValue();
    }

    /**
     * This solution basically makes use of the fact that since we are traversing the Tree anyway, we might as well
     * perform the operation at that time. The textbook's solution wastes time & space. When creating a reusable
     * iterator, it has to traverse the elements, then store the elements in a data structure. Effectively doubling the
     * memory footprint and time cost.
     * @param traversalType - The client gets to choose what type of traversal order they want.
     * @param consumer - The consumer function to be executed when a node is visited with respect to order.
     */
    @Override
    public void traverser(TraversalType traversalType, Consumer<BinaryTreeNode<K, V>> consumer ) {
        switch (traversalType) {
            case BREADTH: {
                breadth(root,consumer);
                break;
            }
            case DEPTH:
                depth(root,consumer);
                break;
            case PREORDER: {
                preorder(root, consumer);
                break;
            }
            case INORDER:
                inorder(root,consumer);
                break;
            case POSTORDER:
                postorder(root,consumer);
                break;
        }
    }

    @Override
    public int nodeCount() {
        AtomicInteger counter = new AtomicInteger();
        traverser(TraversalType.PREORDER, (node) -> counter.getAndIncrement());
        return counter.get();
    }

    @Override
    public int leavesCount() {
        AtomicInteger counter = new AtomicInteger();
        traverser(TraversalType.PREORDER, (node) -> {
            if (node.isLeaf()) counter.getAndIncrement();
        });
        return counter.get();
    }

    @Override
    public void swapTrees() {
        innerSwap(root);
    }

    @Override
    public int singleParent() {
        AtomicInteger single_parent_count = new AtomicInteger();
        traverser(TraversalType.POSTORDER, (node) -> {
            if (node.isSingleParent()) single_parent_count.getAndIncrement();
        });
        return single_parent_count.get();
    }

    /**
     * This method computes and prints each element and it's height at each level.
     * If you need proof, uncomment the parent selection and you can draw out the tree and see for yourself.
     */
    @Override
    public void nodeHeight() {
        traverser(TraversalType.BREADTH, (node) -> {
            V element = node.getValue();
//            Optional<BinaryTreeNode<K,V>> parent = Optional.ofNullable(node.parent());
            int height = innerHeight(node);
            System.out.print("Element: " + element + " Height: " + height);
//            parent.ifPresent(kvBinaryTreeNode -> System.out.print(" Parent " + kvBinaryTreeNode.getValue().toString()));
            System.out.println();
        });
    }

    @Override
    public void displayItemsInRange(K lower, K upper) {
        innerDisplayItemsInRange(root, lower, upper);
    }

    @Override
    public K split(K key, BinaryTree<K,V> lesser, BinaryTree<K,V> greater) {
        AtomicBoolean includeKey = new AtomicBoolean(false);
        traverser(TraversalType.PREORDER, node -> {
            if (key.compareTo(node.getKey()) < 0) {
                greater.put(node.getKey(), node.getValue());
            } else if (key.compareTo(node.getKey()) > 0) {
                lesser.put(node.getKey(), node.getValue());
            } else {
                includeKey.set(true);
            }
        });
        clear();
        return includeKey.get() ? key : null;
    }

    @Override
    public void iyengar() {
        // if n <= 2 return
        AtomicReference<BinaryTreeNode<K,V>> ansl = new AtomicReference<>(new BinarySearchTreeNode<>());
        AtomicReference<BinaryTreeNode<K,V>> ansr = new AtomicReference<>(new BinarySearchTreeNode<>());
        AtomicInteger node_count = new AtomicInteger(0);
        List<BinaryTreeNode<K,V>> link = new ArrayList<>();
        traverse_bind(this.root, link, node_count);
        AtomicInteger m = new AtomicInteger((int) Math.floor((double) (node_count.get() + 1) / 2)); // folding value
        root = link.get(m.get()); // new root
        if(node_count.get() == 2 * m.get()) {
            // node_count is even
            m.getAndIncrement(); // adjust folding value
            grow(new AtomicInteger(0), new AtomicInteger(m.get() - 2), link, m, ansl, ansr);
            link.get(m.get()).setLeft(null);
            link.get(m.get()).setRight(null);
            link.get(m.get()+1).setLeft(link.get(m.get()));
        } else {
            // node_count is odd
            grow(new AtomicInteger(0), new AtomicInteger(m.get() - 1), link, new AtomicInteger(m.get() - 1), ansl, ansr);
        }
        root.setLeft(ansl.get());
        root.setRight(ansr.get());
    }

    @Override
    public void traverse_bind(BinaryTreeNode<K, V> ptr, List<BinaryTreeNode<K, V>> link, AtomicInteger node_count) {
        if(ptr == null) return;
        traverse_bind(ptr.left(), link, node_count);
        link.add(node_count.getAndIncrement(), ptr);
        traverse_bind(ptr.right(), link, node_count);
    }

    @Override
    public void grow(AtomicInteger low, AtomicInteger high, List<BinaryTreeNode<K,V>> link, AtomicInteger m, AtomicReference<BinaryTreeNode<K, V>> ansl, AtomicReference<BinaryTreeNode<K,V>> ansr) {
        AtomicInteger mid = new AtomicInteger(0);
        BinaryTreeNode<K,V> tl;
        BinaryTreeNode<K,V> tr;
        if(low.get() > high.get()) {
            ansl.set(null);
            ansr.set(null);
        } else if (low.get() == high.get()) {
            try {
                ansl.set(link.get(low.get()));
            } catch (IndexOutOfBoundsException e) {
                // do nothing!
                ansl.set(null);
            }
            try {
                ansr.set(link.get(low.get()+m.get()));
            } catch (IndexOutOfBoundsException e) {
                // do nothing!
                ansr.set(null);
            }
            if (ansl.get() != null) {
                ansl.get().setLeft(null);
                ansl.get().setRight(null);
            }
            if (ansr.get() != null) {
                ansr.get().setLeft(null);
                ansr.get().setRight(null);
            }
        } else {
            mid.set((int)  Math.floor(  (double) (low.get() + high.get()) / 2));
            tl = link.get(mid.get());
            tr = link.get(mid.get()+m.get());
            grow(low,new AtomicInteger(mid.get() - 1), link, m, ansl, ansr);  // forms left subtree
            tl.setLeft(ansl.get());
            tr.setLeft(ansr.get());
            grow(new AtomicInteger(mid.get() + 1), high, link, m, ansl, ansr);  // forms right subtree
            tl.setRight(ansl.get());
            tr.setRight(ansr.get());
            ansl.set(tl);
            ansr.set(tr);
        }
    }

}
