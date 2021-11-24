import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;

public final class ResultUI {
    private final JFrame frame;
    private final JMenuBar menuBar;
    private final JMenu fileMenu, editMenu;
    private final JMenuItem saveFile, exit, backHome, showTree;
    private final JPanel backgroundPanel, actionPanel;
    private final JButton homeButton, showTreeButton;
    private final JComboBox selectTreeComboBox;
    private final JLabel resultLabel, subsetLabel, allSubsetLabel;
    private final JScrollPane tableScrollPane, labelScrollPane;
    private final JTable resultTable;
    private final Font font;
    
    private String columnInTable[], comboBoxData[];
    private Object resultInTable[][];
    
    private final ArrayList<ArrayList<Long>> answerForLabel;
    private final ArrayList<TreeUI> checkUI;
    private final ArrayList<String> subsetSum;
    private final String algorithmName;
    private final long inputSet[], target;
    private final HomeUI home;
    private final DefaultTreeForTreeLayout<Node> multipleTree;
    private final Trees tree;
    private final Graphics g;
    
    private String forEnterAns = "All answer subset: ";
    private String forEnterInset = "User input set: (";
    
    public ResultUI(ArrayList<String> subsetSum, long[] inputSet, long target, 
                      String algorithmName, HomeUI home, Trees tree, DefaultTreeForTreeLayout<Node> multipleTree) {
        this.subsetSum = subsetSum;
        this.inputSet = inputSet;
        this.target = target;
        this.algorithmName = algorithmName;
        this.home = home;
        this.tree = tree;
        this.multipleTree = multipleTree;
        answerForLabel = new ArrayList<>();
        checkUI = new ArrayList<>();
        g = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB).createGraphics();
        font = new Font("TH SarabunPSK", 0, 20);
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
        saveFile = new JMenuItem("Save File...");
        exit = new JMenuItem("Exit");
        backHome = new JMenuItem("Home");
        showTree = new JMenuItem("Show Tree");
        frame = new JFrame("Result");
        backgroundPanel = new JPanel();
        actionPanel = new JPanel();
        homeButton = new JButton();
        showTreeButton = new JButton();
        selectTreeComboBox = new JComboBox<>();
        resultLabel = new JLabel();
        subsetLabel = new JLabel();
        allSubsetLabel = new JLabel();
        tableScrollPane = new JScrollPane();
        labelScrollPane = new JScrollPane();
        columnInTable = new String[inputSet.length+1];
        resultInTable = new String[subsetSum.size()][inputSet.length+1];
        comboBoxData = new String[subsetSum.size()];
        resultTable = new JTable(){
            @Override
            public boolean isCellEditable(int row, int column) {    
                return false;      
            }
            
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();{
                center.setHorizontalAlignment(SwingConstants.CENTER);
            }
            
            @Override
            public TableCellRenderer getCellRenderer(int i, int j){
                return center;
            }
        };
        setTableAndComboBox();
        setAnswerLabel();
        createMenuBar();
        createLayout();
        createGUI();
    }

    public ResultUI(ArrayList<String> subsetSum, long[] inputSet, long target, Trees tree, 
                      String algorithmName, HomeUI home) {
        this(subsetSum, inputSet, target, algorithmName, home, tree, null);
    }

    public ResultUI(ArrayList<String> subsetSum, long[] inputSet, long target, 
            DefaultTreeForTreeLayout<Node> multipleTree, String algorithmName, HomeUI home) {
        this(subsetSum, inputSet, target, algorithmName, home, null, multipleTree);
    }
    
    public void saveFile(ActionEvent e) {
        FileDialog fd = new FileDialog(frame, "Save File", FileDialog.SAVE);
        fd.setFile("*.txt");
        fd.setVisible(true);
        if (fd.getFile() != null) {
            BufferedWriter bw = null;
            String filePath = fd.getDirectory()+fd.getFile();
            if(fd.getFile().lastIndexOf(".txt") != fd.getFile().indexOf(".txt"))
                filePath = filePath.substring(0, filePath.length()-4);
            else if(!fd.getFile().contains(".txt"))
                filePath += ".txt";
            try {
                bw = new BufferedWriter(new FileWriter(filePath));
                for(int i=0;i<inputSet.length;i++) {
                    bw.write(inputSet[i]+"");
                    if(i != inputSet.length-1)
                        bw.write(" ");
                }
                bw.write(System.getProperty("line.separator"));
                bw.write(target+System.getProperty("line.separator"));
                for(int j=0;j<subsetSum.size();j++) {
                    String p = subsetSum.get(j);
                    for(int i=0;i<p.length();i++) {
                        bw.write(p.charAt(i));
                        if(i != p.length()-1)
                            bw.write(" ");
                    }
                    if(j!=subsetSum.size()-1)
                        bw.write(System.getProperty("line.separator"));
                }
            } 
            catch (IOException ex) { }
            finally {
                try {
                    if(bw != null)
                        bw.close();
                } 
                catch (IOException ex) { }
            }
        }
    }
    
    public void exit(ActionEvent e) {
        System.exit(0);
    }
    
    public void homeAction(ActionEvent e){
        frame.setVisible(false);
        home.show();
    }
    
    public void showTreeAction(ActionEvent e){
        if(algorithmName.equals("Backtracking") && Node.screenSize.width == 0) {
            JOptionPane.showMessageDialog(null, "Can not display tree visualization because number of node overflow", "ERROR", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String item = selectTreeComboBox.getSelectedItem().toString();
        item = item.substring(item.indexOf(' ')+1);
        boolean check = false;
        for(int i=0; i<checkUI.size(); i++){
            TreeUI ui = checkUI.get(i);
            if(ui.getSubsetSum().equals(item)){
                if(!ui.getVisible())
                    checkUI.remove(i);
                check = ui.getVisible();
                break;
            }
        }
        if(!check) {
            if(algorithmName.equals("Backtracking"))
                checkUI.add(new TreeUI(tree, item));
            else
                checkUI.add(new TreeUI(multipleTree, item));
        }
        else
            JOptionPane.showMessageDialog(null, "This path is being shown", "ERROR", JOptionPane.WARNING_MESSAGE);
    }
    
    private String enter(String text, int select){
        int lineWidth;
        switch(select){
            case 1:
                forEnterInset += text;
                lineWidth = g.getFontMetrics(font).stringWidth(forEnterInset); 

                if(lineWidth > 675){
                    forEnterInset = "";
                    return "<br>";
                }
                return "";
            case 2:
                forEnterAns += text;
                lineWidth = g.getFontMetrics(font).stringWidth(forEnterAns); 

                if(lineWidth > 675){
                    forEnterAns = "";
                    return "<br>";
                }
                return "";
            default:
                return null;
        }
    }
    
    private void setTableAndComboBox() {
        columnInTable[0] = "Subset";
        for(int i=1; i<inputSet.length+1; i++)
            columnInTable[i] = inputSet[i-1]+"";
        
        for(int i=0; i<subsetSum.size(); i++){
            resultInTable[i][0] = "Ans"+(i+1);
            for(int j=0; j<subsetSum.get(i).length(); j++)
                resultInTable[i][j+1] = subsetSum.get(i).charAt(j)+"";
        }
        for(int i=0; i<subsetSum.size(); i++)
            comboBoxData[i] = "(Ans"+(i+1)+") "+subsetSum.get(i);
    }
    
    private void setAnswerLabel() {
        for(int i=0; i<subsetSum.size(); i++){
            ArrayList<Long> subAns = new ArrayList<>();
            for(int j=0; j<subsetSum.get(i).length(); j++){
                if(subsetSum.get(i).charAt(j) == '1')
                    subAns.add(inputSet[j]);
            }
            answerForLabel.add(subAns);
        }
        
        String answerText = "<html>User input set: (";
        for(int i=0; i<inputSet.length; i++){
            answerText += inputSet[i]+"";
            forEnterInset += inputSet[i]+"";
            
            if(i != inputSet.length-1)
                answerText += ", " + enter(", ",1);
        }
        answerText += ")<br>";
        
        answerText += "Target: "+target;
        
        answerText += "<br>All answer subset: ";
        for(int i=0; i<answerForLabel.size(); i++){
            answerText += "(Ans"+(i+1)+" | " + enter("(Ans"+(i+1)+" | ",2);
            for(int j=0; j<answerForLabel.get(i).size(); j++){
                answerText += answerForLabel.get(i).get(j)+"";
                forEnterAns += answerForLabel.get(i).get(j)+"";
                
                if(j != answerForLabel.get(i).size()-1){
                    answerText += ", " + enter(", ",2);
                }
            }
            answerText += ")";
            forEnterAns += ")";
            
            if(i != answerForLabel.size()-1){
                answerText += ", " + enter(", ",2);
            }
        }
        
        allSubsetLabel.setText(answerText);
        labelScrollPane.setViewportView(allSubsetLabel);
    }
    
    private void createMenuBar() {
        saveFile.addActionListener(this::saveFile);
        exit.addActionListener(this::exit);
        backHome.addActionListener(this::homeAction);
        showTree.addActionListener((e) -> this.showTreeAction(e));
        fileMenu.add(saveFile);
        fileMenu.add(exit);
        editMenu.add(backHome); 
        editMenu.add(showTree);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        frame.setJMenuBar(menuBar);
    }
    
    private void createGUI() {
        backgroundPanel.setBackground(new Color(102, 0, 153));

        actionPanel.setBackground(new Color(255, 204, 0));

        homeButton.setFont(font); 
        homeButton.setFocusable(false);
        homeButton.setText("HOME");
        homeButton.addActionListener(this::homeAction);

        selectTreeComboBox.setFont(font); 
        selectTreeComboBox.setModel(new DefaultComboBoxModel<>(comboBoxData));
        selectTreeComboBox.setFocusable(false);
        
        subsetLabel.setFont(font); 
        subsetLabel.setForeground(new Color(0, 0, 0));
        subsetLabel.setText("Select path to show:");

        showTreeButton.setFont(font); 
        showTreeButton.setText("SHOW TREE");
        showTreeButton.setFocusable(false);
        showTreeButton.addActionListener(this::showTreeAction);

        resultLabel.setFont(new Font("TH SarabunPSK", Font.BOLD, 20));
        resultLabel.setForeground(new Color(255, 255, 255));
        resultLabel.setText("Result Table ("+algorithmName+" Algorithm)");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        resultTable.setFont(font);
        resultTable.setModel(new DefaultTableModel(resultInTable, columnInTable));
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        resultTable.setCellSelectionEnabled(false);
        resultTable.getTableHeader().setReorderingAllowed(false);
        resultTable.setRowHeight(30);
        tableScrollPane.setViewportView(resultTable);

        labelScrollPane.setBackground(new Color(255, 255, 255));
        labelScrollPane.setForeground(new Color(255, 255, 255));
        labelScrollPane.setOpaque(false);

        allSubsetLabel.setBackground(new Color(255, 255, 255));
        allSubsetLabel.setFont(font);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBackground(new Color(255, 255, 255));
        frame.pack();
        frame.setSize(770, 550);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void createLayout() {
        GroupLayout actionPanelLayout = new GroupLayout(actionPanel);
        actionPanel.setLayout(actionPanelLayout);
        actionPanelLayout.setHorizontalGroup(
            actionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(actionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(homeButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(subsetLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectTreeComboBox, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(showTreeButton)
                .addContainerGap())
        );
        actionPanelLayout.setVerticalGroup(
            actionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(actionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(actionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(actionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(selectTreeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(subsetLabel))
                    .addComponent(homeButton)
                    .addComponent(showTreeButton))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        GroupLayout backgroundPanelLayout = new GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(actionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(labelScrollPane)
                    .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
                    .addComponent(resultLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addComponent(actionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelScrollPane, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(resultLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }
}