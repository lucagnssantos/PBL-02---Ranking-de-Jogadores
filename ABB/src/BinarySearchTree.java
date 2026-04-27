public class BinarySearchTree {
    private Node root;

    public void insert(Player player) {
        root = insert(root, player);
    }

    private Node insert(Node current, Player player) {
        if (current == null) return new Node(player);
        if (player.getRanking() < current.player.getRanking())
            current.left = insert(current.left, player);
        else if (player.getRanking() > current.player.getRanking())
            current.right = insert(current.right, player);
        return current;
    }

    public boolean search(String name) {
        return search(root, name) != null;
    }

    private Node search(Node current, String name) {
        if (current == null) return null;
        if (current.player.getNickname().equals(name)) return current;
        Node found = search(current.left, name);
        if (found != null) return found;
        return search(current.right, name);
    }

    public Player remove(String name) {
        Node found = search(root, name);
        if (found == null) return null;
        Player removed = found.player;
        root = remove(root, name);
        return removed;
    }

    private Node remove(Node current, String name) {
        if (current == null) return null;
        if (current.player.getNickname().equals(name)) {
            if (current.left == null) return current.right;
            if (current.right == null) return current.left;
            Node successor = findMin(current.right);
            current.player = successor.player;
            current.right = remove(current.right, successor.player.getNickname());
            return current;
        }
        current.left = remove(current.left, name);
        current.right = remove(current.right, name);
        return current;
    }

    private Node findMin(Node node) {
        if (node.left == null) return node;
        return findMin(node.left);
    }

    public void inOrder() {
        inOrder(root);
        System.out.println();
    }

    private void inOrder(Node node) {
        if (node == null) return;
        inOrder(node.left);
        System.out.print(node.player.getNickname() + "(" + node.player.getRanking() + ") ");
        inOrder(node.right);
    }

    public Node getRoot() { return root; }

    public int getHeight() { return getHeight(root); }

    private int getHeight(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    public int countNodes() { return countNodes(root); }

    private int countNodes(Node node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }

    public void assignInorderPositions() {
        int[] counter = {0};
        assignInorderPos(root, counter);
    }

    private void assignInorderPos(Node node, int[] counter) {
        if (node == null) return;
        assignInorderPos(node.left, counter);
        node.inorderPos = counter[0]++;
        assignInorderPos(node.right, counter);
    }
}
