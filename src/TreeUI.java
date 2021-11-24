import java.awt.*;
import javax.swing.*;
import org.abego.treelayout.*;
import org.abego.treelayout.util.DefaultConfiguration;

public class TreeUI {
    public static final Color BACKGROUND_COLOR = Color.WHITE;
    private final String subsetSum;
    private final JFrame frame;
    private final JScrollPane frameScrollPane;

    public TreeUI(Trees tree, String subsetSum) {
        this.subsetSum = subsetSum;
        frame = new JFrame("State Space Tree (backtracking) : "+subsetSum);
        JPanel backgroundPanel = new JPanel(){
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                tree.traverse(tree, g2, subsetSum, "");
            }
        };
        backgroundPanel.setPreferredSize(Node.screenSize);
        backgroundPanel.setBackground(Color.WHITE);
        
        frameScrollPane = new JScrollPane(backgroundPanel);
        frameScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frameScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frameScrollPane.setBorder(null);
        
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1280, 720));
        frame.setLayout(new BorderLayout());
        frame.add(frameScrollPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public TreeUI(TreeForTreeLayout<Node> multipleTree, String subsetSum) {
        this.subsetSum = subsetSum;
        frame = new JFrame("State Space Tree (branch and bound) : "+subsetSum);
        DefaultConfiguration<Node> configuration = new DefaultConfiguration<>(Node.VERTICAL_GAP, Node.HORIZONTAL_GAP);
        TextInNodeExtendProvider nodeExtentProvider = new TextInNodeExtendProvider();
        TreeLayout<Node> treeLayout = new TreeLayout<>(multipleTree, nodeExtentProvider, configuration);
        MultipleTree panel = new MultipleTree(treeLayout, subsetSum);
        
        frameScrollPane = new JScrollPane(panel);
        frameScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frameScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frameScrollPane.setBorder(null);
        
        Dimension size = panel.getPreferredSize().width<=1280&&panel.getPreferredSize().height<=720 
                ? new Dimension(panel.getPreferredSize().width+16, panel.getPreferredSize().height+39) 
                : new Dimension(1280, 720);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setMinimumSize(size);
        frame.setLayout(new BorderLayout());
        frame.add(frameScrollPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public String getSubsetSum(){
        return subsetSum;
    }
    public boolean getVisible(){
        return frame.isVisible();
    }
}

class TextInNodeExtendProvider implements NodeExtentProvider<Node> {
    @Override
    public double getWidth(Node treeNode) {
        return treeNode.getWidth();
    }
    @Override
    public double getHeight(Node treeNode) {
        return treeNode.getHeight();
    }
}