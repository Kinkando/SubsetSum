import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import org.abego.treelayout.TreeLayout;

public class MultipleTree extends JPanel {

    private final String highlight;
    private final TreeLayout<Node> treeLayout;
    private final static int ARC_SIZE = 10;
    
    public MultipleTree(TreeLayout<Node> treeLayout, String highlight) {
        this.treeLayout = treeLayout;
        this.highlight = highlight;
        setPreferredSize(treeLayout.getBounds().getBounds().getSize());
        setBackground(TreeUI.BACKGROUND_COLOR);
    }
    
    private void paintEdges(Graphics2D g, Node parent) {
        if (!treeLayout.getTree().isLeaf(parent)) {
            Rectangle2D.Double b1 = treeLayout.getNodeBounds().get(parent);
            int x1 = (int) b1.getCenterX();
            int y1 = (int) (b1.getCenterY() + b1.getHeight()/2);
            for (Node child : treeLayout.getTree().getChildren(parent)) {
                Rectangle2D.Double b2 = treeLayout.getNodeBounds().get(child);
                
                boolean highlights = (highlight.length() >= child.getPath().length() && highlight.substring(0, child.getPath().length()).equals(child.getPath()));
                int x2 = (int) b2.getCenterX();
                int y2 = (int) (b2.getCenterY() - b2.getHeight()/2);
                
                g.setFont(Node.FONT);
                g.setColor(highlights ? Node.HIGHLIGHT_COLOR : Node.NORMAL_COLOR);
                g.drawLine(x1, y1, x2, y2);
                
                int index = child.getPath().length();
                String text = index + "";
                int lineCenterX = x2 - ((x2 - x1) / 2);
                int lineCenterY = y2 - ((y2 - y1) / 2);
                int textWidth = g.getFontMetrics(Node.FONT).stringWidth(text) + 6;
                int textHeight = 20;

                g.setColor(this.getBackground());
                g.fillRect(lineCenterX - textWidth / 2, lineCenterY - textHeight / 2, textWidth, textHeight);

                g.setColor(highlights ? Node.HIGHLIGHT_COLOR : Node.NORMAL_COLOR);
                g.drawString(text, lineCenterX - textWidth / 2 + 3, lineCenterY + textHeight / 4);

                paintEdges(g, child);
            }
        }
    }

    private void paintNode(Graphics2D g) {
        g.setFont(Node.FONT);
        for(Node node : treeLayout.getNodeBounds().keySet()) {
            Rectangle2D.Double box = treeLayout.getNodeBounds().get(node);
            boolean highlights = (highlight.length() >= node.getPath().length() && highlight.substring(0, node.getPath().length()).equals(node.getPath()));

            g.setColor(highlights ? Node.HIGHLIGHT_NODE : Node.NORMAL_NODE);
            g.fillRoundRect((int) box.x, (int) box.y, (int) box.width - 1, (int) box.height - 1, ARC_SIZE, ARC_SIZE);

            g.setColor(highlights ? Node.HIGHLIGHT_COLOR : Node.NORMAL_COLOR);
            g.drawRoundRect((int) box.x, (int) box.y, (int) box.width - 1, (int) box.height - 1, ARC_SIZE, ARC_SIZE);

            g.setColor(highlights ? Node.HIGHLIGHT_COLOR : Node.NORMAL_COLOR);
            String text = node.getName();
            if(Long.parseLong(text) < 0)
                text = '\u221E'+"";
            FontMetrics m = getFontMetrics(Node.FONT);
            int textWidth = m.stringWidth(text);
            int textHeight = m.getHeight();
            int x = (int) box.getCenterX();
            int y = (int) box.getCenterY();
            g.drawString(text, x - textWidth/2, y + textHeight/4);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        paintEdges(g2, treeLayout.getTree().getRoot());
        paintNode(g2);
    }
}
