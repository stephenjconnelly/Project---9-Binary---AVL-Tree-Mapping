/**
 * Class that implements an AVL tree which implements the MyMap interface.
 * @author Stephen James Connelly - methods: rotateRight Child, rotateLeftChild, insert, balance, remove. 
 * @author Brian S. Borowski - others
 * @version 1.0 October 28, 2022
 */
public class AVLTreeMap<K extends Comparable<K>, V> extends BSTMap<K, V>
        implements MyMap<K, V> {
    private static final int ALLOWED_IMBALANCE = 1;

    /**
     * Creates an empty AVL tree map.
     */
    public AVLTreeMap() { }

    public AVLTreeMap(Pair<K, V>[] elements) {
        insertElements(elements);
    }

    /**
     * Creates a AVL tree map of the given key-value pairs. If
     * sorted is true, a balanced tree will be created via a divide-and-conquer
     * approach. If sorted is false, the pairs will be inserted in the order
     * they are received, and the tree will be rotated to maintain the AVL tree
     * balance property.
     * @param elements an array of key-value pairs
     */
    public AVLTreeMap(Pair<K, V>[] elements, boolean sorted) {
        if (!sorted) {
            insertElements(elements);
        } else {
            root = createBST(elements, 0, elements.length - 1);
        }
    }

    /**
     * Recursively constructs a balanced binary search tree by inserting the
     * elements via a divide-snd-conquer approach. The middle element in the
     * array becomes the root. The middle of the left half becomes the root's
     * left child. The middle element of the right half becomes the root's right
     * child. This process continues until low > high, at which point the
     * method returns a null Node.
     * @param pairs an array of <K, V> pairs sorted by key
     * @param low   the low index of the array of elements
     * @param high  the high index of the array of elements
     * @return      the root of the balanced tree of pairs
     */
    protected Node<K, V> createBST(Pair<K, V>[] pairs, int low, int high) {
        if (low > high) {
            return null;
        }
        int mid = low + (high - low) / 2;
        Pair<K, V> pair = pairs[mid];
        Node<K, V> parent = new Node<>(pair.key, pair.value);
        size++;
        parent.left = createBST(pairs, low, mid - 1);
        if (parent.left != null) {
            parent.left.parent = parent;
        }
        parent.right = createBST(pairs, mid + 1, high);
        if (parent.right != null) {
            parent.right.parent = parent;
        }
        // This line is critical for being able to add additional nodes or to
        // remove nodes. Forgetting this line leads to incorrectly balanced
        // trees.
        parent.height =
                Math.max(avlHeight(parent.left), avlHeight(parent.right)) + 1;
        return parent;
    }


    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is replaced
     * by the specified value.
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    @Override
    public V put(K key, V value) {
        NodeOldValuePair nvp = new NodeOldValuePair(null, null);
        nvp = insertAndBalance(key, value, root, nvp);
        return nvp.oldValue;
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    public V remove(K key) {
        NodeOldValuePair nvp = new NodeOldValuePair(null, null);
        Node<K,V> removed =  removeHelper(key, root, nvp, null).node;
        return removed.value;

    }
    private NodeOldValuePair removeHelper2(K key, Node<K,V> t, NodeOldValuePair nvp){
//        System.out.println(toAsciiDrawing());
//        System.out.println("0 "+t);
//        System.out.println("0.0 "+nvp.node);
        if (t == null){
            return nvp;
        }
        //nvp = t;
        int compareResult = key.compareTo(t.key);

        if(compareResult < 0){
//            System.out.println("1 " + t);
//            System.out.println("1.1 " + nvp.node);
            nvp = removeHelper2(key, t.left, nvp);
            t.left = nvp.node;
            if(t.left != null){
                t.left.parent = t;
            }
        }
//        System.out.println("3 " + nvp);
        if(compareResult > 0) {
//            System.out.println("2 "+t);
//            System.out.println("2.2 "+nvp.node);
            nvp = removeHelper2(key, t.right, nvp);
            t.right = nvp.node;
            if(t.right != null){
                t.right.parent = t;
            }

        } else if( t.left != null && t.right != null ){

            nvp.node = t;
//            System.out.println("3 "+t);
//            System.out.println("3.3 "+nvp.node);
            Node<K,V> min = treeMinimum(t.right);

            t.key = min.key;
            t.value = min.value;
//            if(t.right.left = min.right){
//                t.right
//            }
//            if (min.right != null) {
//                min.parent.left = min.right;
//            }

            //nvp = removeHelper2(key, t.right, nvp);


            t.right = removeHelper2( t.key, t.right, nvp).node;
            //t.right.parent = t;
            System.out.println(toAsciiDrawing());
        }else{
//            System.out.println("4 "+t);
//            System.out.println("4.4 "+nvp.node);
            t = ( t.left != null ) ? t.left : t.right;
            nvp.node = t;

            nvp.node = balance(t);
            size--;
//            System.out.println(toAsciiDrawing());
            return nvp;
        }
//        System.out.println("5 "+t);
//        System.out.println("5.5 "+nvp.node);
        //System.out.println(toAsciiDrawing());

        nvp.node = balance(t);
//        System.out.println(nvp.node);
       // t.right.parent = t;
        return nvp;
    }
    private NodeOldValuePair removeHelper(K key, Node<K, V> t, NodeOldValuePair nvp, Node<K, V> prev) {
        if (t == null) { //insert value is null
            return null;
        }
        System.out.println("t"+t);
        System.out.println("k"+key);
        int comparison = key.compareTo(t.key);
        if(comparison > 0){
            prev = t;
            System.out.println("t0"+t);
            nvp = removeHelper(key, t.right, nvp, prev);
            t.right = nvp.node;
//            if(t.right != null) {
//                t.right.parent = t;
//            }
        }else if(comparison < 0){
            prev = t;
            System.out.println("t2"+t);
            nvp = removeHelper(key, t.left, nvp, prev);
            t.left = nvp.node;
            if(t.left != null){
                t.left.parent = t;
            }
        }else if (comparison == 0){

            System.out.println("t1"+t);
            nvp.node = t;
           // System.out.println(t);
            if(t.left == null && t.right == null) {//is leaf
                System.out.println("tt" + t);
                System.out.println("x");
                Node<K,V> par = t.parent;
                if (prev.key.compareTo(t.key) > 0) {
                    par.left = null;
                } else {
                    System.out.println("z");
                    par.right = null;
                }
                t.parent = null;
                nvp.node = balance(t);
                size--;
                return nvp;
            } else if(t.left != null && t.right != null) {//two children
                System.out.println("4");
                Node<K, V> y = treeMinimum(t.right);
                if (y.parent != t) {
                    transplant(y, y.right);
                    y.right = t.right;
                    y.right.parent = y;
                }
                transplant(t, y);
                y.left = t.left;
                y.left.parent = y;
          } else if (t.left == null) { //one child right
                transplant(t, t.right);
                nvp.node = balance(t);
                size--;
                return nvp;
            }else if(t.right == null) {//one child left
                transplant(t, t.left);
                nvp.node = balance(t);
                size--;
                return nvp;
            }
            size--;
        }
//        System.out.println(toAsciiDrawing());
        Node<K, V> n = balance(t);
        nvp.node = n;
        return nvp;
    }
    protected void transplant(Node<K, V> u, Node<K, V> v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }
    private NodeOldValuePair insertAndBalance(K key, V value, Node<K, V> t, NodeOldValuePair nvp) {
        if (t == null) { //insert value is null
            size++;
            nvp.node = new Node<K, V>(key, value);
            if (root == null) {
                root = nvp.node;
            }
            return nvp;
        }
        int comparison = key.compareTo(t.key);
        if(comparison > 0){
            NodeOldValuePair Nvp = insertAndBalance(key, value, t.right, nvp);
            t.right = Nvp.node;
            if(t.right != null) {
                t.right.parent = t;
            }
        }else if(comparison < 0){
            NodeOldValuePair Nvp = insertAndBalance(key, value, t.left, nvp);
            t.left = Nvp.node;
            if(t.left != null){
                t.left.parent = t;
            }
        }else if (comparison == 0){
            nvp.node = t;
            nvp.oldValue = t.value;
            t.value = value;
        }else{}
       // System.out.println("t in insert" + t);
        Node<K, V> n = balance(t);
       // System.out.println("n in insert" + n);
        nvp.node = n;
        //System.out.println(nvp.node);
        return nvp;
    }


    private Node<K, V> balance(Node<K, V> t) {
        //System.out.println("after call" +t);
      if(t == null){
          return t;
      }
      if( avlHeight( t.left ) - avlHeight( t.right ) > ALLOWED_IMBALANCE) {
          if (avlHeight(t.left.left) >= avlHeight(t.left.right)) {
              t = rotateWithLeftChild(t);
          }else {
              t = doubleWithLeftChild(t);
          }
          //System.out.println("After first if" +t);
      }else if( avlHeight( t.right ) - avlHeight( t.left ) > ALLOWED_IMBALANCE ) {
              if (avlHeight(t.right.right) >= avlHeight(t.right.left)) {
                  t = rotateWithRightChild(t);
              }else {
                  t = doubleWithRightChild(t);
              }
          }
         // System.out.println("After second if" +t);

       // System.out.println("before height" + t);
      t.height = Math.max( avlHeight( t.left ), avlHeight( t.right ) ) + 1;
       // System.out.println("before return" + t);
        System.out.println(t);

      return t;
    }

    private int avlHeight(Node<K, V> t) {
        return t == null ? -1 : t.height;
    }

    private Node<K, V> rotateWithLeftChild(Node<K, V> k2) {
        // TODO
        Node<K, V> Root = k2;
        Node<K, V> k1 = k2.left;
        System.out.println("left child");

//            if (k2.key.compareTo(k2.parent.key) > 0) {
//                k2.parent.right = k1;
//            } else {
//                k2.parent.left = k1;
//            }
            k2.left = k1.right;
            if (k1.right != null){
                k1.right.parent = k2;
            }
            k1.right = k2;
            k2.parent = k1;
//            if (k1.left != null){
//
//            }

//            k2.left = k1.right;
////            k1.right = k2;
//            k1.parent = k2.parent;

             //k2.left becomes y
              // k1.right becomes k2
            k2.height = Math.max(avlHeight(k2.left), avlHeight(k2.right)) + 1;
            k1.height = Math.max(avlHeight(k1.left), k2.height) + 1;
            if (Root == root) {
                root  = k1;
                root.parent = null;
            }
            return k1;

    }
    private Node<K, V> rotateWithRightChild(Node<K, V> k1) {
        // TODO
       // if (k1 != null) {
        System.out.println("right child");
            Node<K,V> Root = k1;
            Node<K,V> k2 = k1.right;
//        if (k1.key.compareTo(k1.parent.key) > 0) {
//            k2.parent = k1.parent.right;
//        } else {
//            k2.parent = k1.parent.left;
//        }
            k1.right = k2.left;
            if (k2.left != null) {
                k2.left.parent = k2;
            }
            k2.left = k1;
            k1.parent = k2;
            k1.height = Math.max(avlHeight(k1.left), avlHeight(k1.right)) + 1;
            k2.height = Math.max(avlHeight(k2.right), k1.height) + 1;
            if (Root == root) {
                root  = k2;
                root.parent = null;
            }
            System.out.println("k2 " + k2);
            return k2 ;
//            System.out.println("k1 " + k1);
//            Node k2 = k1.right;
////            System.out.println("k2 " + k2);
////            System.out.println("k1 parent " + k1.parent);
////            System.out.println("k1 parent " + k2.parent);
////            System.out.println("k2.right " + k2.right);
////            System.out.println("k1.right " + k1.right);
////            System.out.println("k2.left " + k2.right);
////            System.out.println("k1.left " + k1.right);
//            k1.parent = k2;
//            k2.left = k1;
//            k1.right = k2;
//            k1.height = Math.max(avlHeight(k1.left), avlHeight(k1.right)) + 1;
//            k2.height = Math.max(avlHeight(k2.right), k1.height) + 1;
//            return k1;
        }
      //  return null;


    private Node<K, V> doubleWithLeftChild(Node<K, V> k3) {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    private Node<K, V> doubleWithRightChild(Node<K, V> k3) {
        k3.right = rotateWithLeftChild(k3.right);
        return rotateWithRightChild(k3);
    }

    private class NodeOldValuePair {
        Node<K, V> node;
        V oldValue;

        NodeOldValuePair(Node<K, V> n, V oldValue) {
            this.node = n;
            this.oldValue = oldValue;
        }
    }

    public static void main(String[] args) {
        boolean usingInts = true;
        if (args.length > 0) {
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                usingInts = false;
            }
        }

        AVLTreeMap avlTree;
        if (usingInts) {
            @SuppressWarnings("unchecked")
            Pair<Integer, Integer>[] pairs = new Pair[args.length];
            for (int i = 0; i < args.length; i++) {
                try {
                    int val = Integer.parseInt(args[i]);
                    pairs[i] = new Pair<>(val, val);
                } catch (NumberFormatException nfe) {
                    System.err.println("Error: Invalid integer '" + args[i]
                            + "' found at index " + i + ".");
                    System.exit(1);
                }
            }
            avlTree = new AVLTreeMap<Integer, Integer>(pairs);
        } else {
            @SuppressWarnings("unchecked")
            Pair<String, String>[] pairs = new Pair[args.length];
            for (int i = 0; i < args.length; i++) {
                pairs[i] = new Pair<>(args[i], args[i]);
            }
            avlTree = new AVLTreeMap<String, String>(pairs);
        }

        System.out.println(avlTree.toAsciiDrawing());
        System.out.println();
        System.out.println("Height:                   " + avlTree.height());
        System.out.println("Total nodes:              " + avlTree.size());
        System.out.printf("Successful search cost:   %.3f\n",
                avlTree.successfulSearchCost());
        System.out.printf("Unsuccessful search cost: %.3f\n",
                avlTree.unsuccessfulSearchCost());
        avlTree.printTraversal(PREORDER);
        avlTree.printTraversal(INORDER);
        avlTree.printTraversal(POSTORDER);
    }
}
