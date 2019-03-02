import java.util.*;

public class RedBlackBST<Key extends Comparable, Value> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private Node root;

    /* The same code as for elementary Binary Search Trees */
    public Value get(Key target) {
        return get(root, target);
    }

    private Value get(Node root, Key target) {
        if (root == null) return null;
        int cmp = target.compareTo(root.key);
        if (cmp < 0) return get(root.leftChild, target);
        else if (cmp > 0) return get(root.rightChild, target);
        else return root.value;
    }

    public int size() {
        return size(root);
    }

    private int size(Node node) {
        if (node == null) return 0;
        return node.size;
    }

    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("Tree is Empty");
        return min(root).key;
    }

    private Node min(Node root) {
        if (root.leftChild == null)
            return root;
        else
            return min(root.leftChild);
    }

    public Key max() {
        if (isEmpty()) throw new NoSuchElementException("Tree is Empty");
        return max(root).key;
    }

    private Node max(Node root) {
        if (root.rightChild == null)
            return root;
        else
            return max(root.rightChild);
    }

    public boolean isEmpty() {
        return root.size <= 0;
    }

    public void removeMin() {
        if (isEmpty())
            throw new NoSuchElementException("Tree is Empty");
        // if both children of root are black than set root to red
        if (!isRed(root.leftChild) && !isRed(root.rightChild))
            root.color = RED;
        root = removeMin(root);
        if (!isEmpty()) root.color = BLACK;
    }


    private Node removeMin(Node root) {
        if (root.leftChild == null)
            return null;

        if (!isRed(root.leftChild) && !isRed(root.leftChild.leftChild))
            root = moveRedLeft(root);

        root.leftChild = removeMin(root.leftChild);
        return balance(root);
    }

    public void delete(Key key) {
        if (key == null)
            throw new IllegalArgumentException("Null Key");
        if (get(key) == null)
            return;
        root = delete(root, key);
        root.color = BLACK;
    }

    private Node delete(Node root, Key key) {
        if (key.compareTo(root) < 0) {
            if (!isRed(root.leftChild) && !isRed(root.leftChild.leftChild))
                root = moveRedLeft(root);
            root.leftChild = delete(root.leftChild, key);
        } else {
            if (isRed(root.leftChild))
                root = rotateRight(root);
            if (key.compareTo(root) == 0 && root.rightChild == null)
                return null;
            if (!isRed(root.rightChild) && !isRed(root.rightChild.leftChild))
                root = moveRedRight(root);
            if (key.compareTo(root.key) == 0) {
                Node x = min(root.rightChild);
                root.key = x.key;
                root.value = x.value;
                root.rightChild = removeMin(root.rightChild);
            } else
                root.rightChild = delete(root.rightChild, key);
        }
        return balance(root);
    }


    /*Restore black tree invariant*/
    private Node balance(Node root) {
        if (isRed(root.rightChild)) root = rotateLeft(root);
        if (isRed(root.leftChild) && isRed(root.leftChild.leftChild)) root = rotateRight(root);
        if (isRed(root.leftChild) && isRed(root.rightChild)) flipColors(root);

        root.size = size(root.leftChild) + size(root.rightChild) + 1;
        return root;
    }

    private Node moveRedLeft(Node root) {
        flipColors(root);
        if (isRed(root.rightChild.leftChild)) {
            root.rightChild = rotateRight(root.rightChild);
            root = rotateLeft(root);
            flipColors(root);
        }
        return root;
    }


    public void removeMax() {
        if (isEmpty())
            throw new NoSuchElementException("Tree is Empty");
        root = removeMax(root);
        root.color = BLACK;
    }

    private Node removeMax(Node root) {
        if (isRed(root.leftChild))
            root = rotateRight(root);
        if (root.rightChild == null)
            return null;
        if (!isRed(root.rightChild) && !isRed(root.rightChild.leftChild))
            root = moveRedRight(root);
        root.rightChild = removeMax(root.rightChild);
        return balance(root);
    }

    public Key ceiling(Key key) {
        if (isEmpty())
            throw new NoSuchElementException("Tree is Empty");
        if (key == null)
            throw new IllegalArgumentException("Null Key");
        Node answer = ceiling(root, key);
        if (answer == null)
            return null;
        else
            return answer.key;
    }

    private Node ceiling(Node root, Key key){
        if (root == null)
            return null;
        int cmp = key.compareTo(root.key);
        if (cmp == 0)
            return root;
        else if(cmp > 0)
            return ceiling(root.rightChild, key);
        else {
            Node answer = ceiling(root.leftChild, key);
            if (answer == null)
                return root;
            else
                return answer;
        }

    }

    public Key floor(Key key) {
        if (isEmpty())
            throw new NoSuchElementException("Tree is Empty");
        if (key == null)
            throw new IllegalArgumentException("Null Key");
        Node answer = floor(root, key);
        if (answer == null)
            return null;
        else
            return answer.key;
    }

    private Node floor(Node root, Key key) {
        if (root == null) return null;
        int cmp = key.compareTo(root.key);
        if (cmp == 0) return root;
        else if (cmp < 0) return floor(root.leftChild, key);
        else {
            Node temp = floor(root.rightChild, key);
            if (temp == null)
                return root;
            else
                return temp;
        }
    }

    private Node moveRedRight(Node root) {
        flipColors(root);
        if (isRed(root.leftChild.leftChild)) {
            root = rotateRight(root);
            flipColors(root);
        }
        return root;
    }

    public Value put(Key key, Value value) {
        Value oldValue = get(key);
        root = put(root, key, value);
        root.color = BLACK;
        return oldValue;
    }

    private Node put(Node root, Key key, Value value) {
        if (root == null) return new Node(key, value, 1, RED);
        int cmp = key.compareTo(root.key);
        if (cmp < 0) root.leftChild = put(root.leftChild, key, value);
        else if (cmp > 0) root.rightChild = put(root.rightChild, key, value);
        else root.value = value;
        balance(root);
        return root;
    }

    /*  rotations maintain 1 : 1 correspondence with RB-BST*/
    private Node rotateLeft(Node node) {
        Node nodesRightChild = node.rightChild;
        node.rightChild = nodesRightChild.leftChild;
        nodesRightChild.leftChild = node;
        nodesRightChild.color = node.color;
        node.color = RED;
        nodesRightChild.size = node.size;
        node.size = size(node.leftChild) + size(node.rightChild) + 1;
        return nodesRightChild;
    }

    private Node rotateRight(Node node) {
        Node nodesLeftChild = node.leftChild;
        node.leftChild = nodesLeftChild.rightChild;
        nodesLeftChild.rightChild = node;
        nodesLeftChild.color = node.color;
        node.color = RED;
        nodesLeftChild.size = node.size;
        node.size = size(node.leftChild) + size(node.rightChild) + 1;
        return nodesLeftChild;
    }

    private void flipColors(Node node) {
        node.leftChild.color = !node.leftChild.color;
        node.rightChild.color = !node.rightChild.color;
        node.color = !node.color;
    }

    private boolean isRed(Node x) {
        if (x == null) return BLACK;
        return x.color == RED;
    }


    private class Node {
        private Key key;
        private Value value;
        private Node leftChild, rightChild;
        private boolean color;
        private int size;

        Node(Key key, Value value, int size, boolean color) {
            this.key = key;
            this.value = value;
            this.size = size;
            this.color = color;
        }


    }
}
