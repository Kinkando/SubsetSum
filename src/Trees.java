import java.awt.Graphics2D;

public class Trees {
    private Node node;
    private Trees left;
    private Trees right;
    public Trees(Node node, Trees left, Trees right) {
        this.node = node;
        this.left = left;
        this.right = right;
    }
    public Trees(Node node) {
        this(node, null, null);
    }
    public Node getNode() {
        return node;
    }
    public void setNode(Node node) {
        this.node = node;
    }
    public Trees getLeft() {
        return left;
    }
    public void setLeft(Trees left) {
        this.left = left;
    }
    public Trees getRight() {
        return right;
    }
    public void setRight(Trees right) {
        this.right = right;
    }
    public static void insert(Trees tree, Node node, String path) { 
        char side = path.charAt(0);
        if(path.length()==1) {
            if(side == '1')
                tree.setLeft(new Trees(node));
            else 
                tree.setRight(new Trees(node));
        }
        else {
            if(side == '1')
                insert(tree.getLeft(), node, path.substring(1));
            else
                insert(tree.getRight(), node, path.substring(1));
        }
    }
    public void traverse(Trees tree, Graphics2D g, String highlight, String currentPath) {
        if (tree != null) {
            Node parent = tree.getNode();
            int level = parent.getLevel();
            if(tree.getLeft() != null) {
                Node leftNode = tree.getLeft().getNode();
                drawLine(g, parent, leftNode, highlight.length() >= level+1 && highlight.substring(0, level+1).equals(currentPath+"1"));
                traverse(tree.getLeft(), g, highlight, currentPath+"1");
            }
            if(tree.getRight() != null) {
                Node rightNode = tree.getRight().getNode();
                drawLine(g, parent, rightNode, highlight.length() >= level+1 && highlight.substring(0, level+1).equals(currentPath+"0"));
                traverse(tree.getRight(), g, highlight, currentPath+"0");
            }
            parent.draw(g, (highlight.length() >= level && highlight.substring(0, level).equals(currentPath)));
        }
    }
    public void traverseInOrder(Trees tree) {
        if(tree!=null) {
            Node parent = tree.getNode();
            if(tree.getLeft()!=null) 
                traverseInOrder(tree.getLeft());
            if(tree.getRight()!=null) 
                traverseInOrder(tree.getRight());
        }
    }
    public void drawLine(Graphics2D g, Node parent, Node child, boolean highlight) {
        String childSide = (child.getPath().charAt(child.getPath().length()-1) - '0')+"";
        int parentX = parent.getX();
        int parentY = parent.getY();
        int childX = child.getX();
        int childY = child.getY();
        int lineCenterX = childX - ((childX-parentX)/2);
        int lineCenterY = childY - ((childY-parentY)/2);
        int rectWidth = g.getFontMetrics(Node.FONT).stringWidth(childSide) + 4;
        int rectHeight = 20;
        
        g.setFont(Node.FONT);
        g.setColor(highlight ? Node.HIGHLIGHT_COLOR : Node.NORMAL_COLOR);
        g.setStroke(highlight ? Node.HIGHLIGHT_STROKE : Node.NORMAL_STROKE);
        g.drawLine(parentX, parentY, childX, childY);
        
        g.setColor(TreeUI.BACKGROUND_COLOR);
        g.fillRect(lineCenterX-rectWidth/2 - 2, lineCenterY-rectHeight/2, rectWidth, rectHeight);
        
        g.setColor(highlight ? Node.HIGHLIGHT_COLOR : Node.NORMAL_COLOR);
        g.drawString(childSide, lineCenterX-rectWidth/2, lineCenterY+rectHeight/4);
    }
}
