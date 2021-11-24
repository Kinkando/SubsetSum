import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;

public final class HomeUI {
    private static final Color RANDOM_COLOR = new Color(0, 51, 153), INPUT_COLOR = new Color(255, 0, 102);
    private static final int MIN_SET = 1, MAX_SET = 20, MIN_TARGET = 15, MAX_TARGET = 40;
    
    private final JFrame frame;
    private final JMenuBar menuBar;
    private final JMenu fileMenu, editMenu;
    private final JMenuItem openFile, exit, clear, randomGenerate, backtrackingSolution;
    private final JMenuItem branchAndBoundSolution;
    private final JPanel backgroundPanel, randomPanel, inputPanel;
    private final JLabel setRangeLabel, targetRangeLabel, setNumberLabel, setToPanel, targetToPanel, setLabel, targetLabel;
    private final JTextField inputText;
    private final JButton submitButton, clearButton, generateButton;
    private final JSlider setNumberSlider;
    private final JSpinner minRangeSetSpinner, minRangeTargetSpinner, maxRangeSetSpinner, maxRangeTargetSpinner, targetSpinner;
    private final Font font;
    private long[] set;
    private long target;
    private ArrayList<String> subsetSumSelected;
    private ResultUI result;
    private Trees tree;
    private DefaultTreeForTreeLayout<Node> multipleTree;
    
    public HomeUI() {
        font = new Font("TH SarabunPSK", Font.PLAIN, 24);
        frame = new JFrame("Home");
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
        openFile = new JMenuItem("Open File...");
        exit = new JMenuItem("Exit");
        clear = new JMenuItem("Clear");
        randomGenerate = new JMenuItem("Random Generate");
        backtrackingSolution = new JMenuItem("Backtracking");
        branchAndBoundSolution = new JMenuItem("Branch and Bound");
        backgroundPanel = new JPanel();
        randomPanel = new JPanel();
        setRangeLabel = new JLabel();
        targetRangeLabel = new JLabel();
        setNumberLabel = new JLabel();
        setToPanel = new JLabel();
        targetToPanel = new JLabel();
        setNumberSlider = new JSlider();
        generateButton = new JButton();
        minRangeSetSpinner = new JSpinner();
        minRangeTargetSpinner = new JSpinner();
        maxRangeSetSpinner = new JSpinner();
        maxRangeTargetSpinner = new JSpinner();
        inputPanel = new JPanel();
        setLabel = new JLabel();
        targetLabel = new JLabel();
        inputText = new JTextField();
        targetSpinner = new JSpinner();
        submitButton = new JButton();
        clearButton = new JButton();
        createMenuBar();
        createLayout();
        createGUI();
    }
    
    public void branchAndBoundAlgorithm() {
        subsetSumSelected = new ArrayList<>();
        leastCostSearch();
        if(subsetSumSelected.isEmpty())
            JOptionPane.showMessageDialog(frame.getContentPane(), "Can not find subset sum", "Error", JOptionPane.ERROR_MESSAGE);
        else{
            result = new ResultUI(subsetSumSelected, set, target, multipleTree, "Branch and Bound", this);
            frame.setVisible(false);
        }
    }
    
    public void leastCostSearch() {
        PriorityQueue<Node> queue = new PriorityQueue<>(Collections.reverseOrder(new NodeComparator()));
        Dimension size = getDimension(target+"");
        Node parent = new Node(target, "", size.width, size.height);
        multipleTree = new DefaultTreeForTreeLayout<>(parent);
        queue.add(parent);
        
        while(!queue.isEmpty()) {
            parent = queue.poll();
            int currentIndex = parent.getPath().length()-1;
            long currentSum = parent.getSum();
            if(currentSum==0) {
                String path = parent.getPath();
                for(int k=path.length();k<set.length;k++)
                    path+="0";
                subsetSumSelected.add(path);
                return;
            }
            for(int i=currentIndex+1;i<set.length;i++) {
                long updateSum = currentSum - set[i];
                String text = updateSum < 0 ? '\u221E'+"" : updateSum+"";
                String path = parent.getPath();
                for(int j=path.length();j<i;j++)
                    path += "0";
                path += "1";
                
                size = getDimension(text);
                Node child = new Node(updateSum, path, size.width, size.height);
                
                if(updateSum>=0)
                    queue.add(child);       
                multipleTree.addChild(parent, child);
            }
        }
    }
    
    public void backtrackingAlgorithm() {
        subsetSumSelected = new ArrayList<>();
        Node.setScale(set.length, target+"");
        backtracking(0, 0, "");
        if(subsetSumSelected.isEmpty())
            JOptionPane.showMessageDialog(frame.getContentPane(), "Can not find subset sum", "Error", JOptionPane.ERROR_MESSAGE);
        else{
            result = new ResultUI(subsetSumSelected, set, target, tree, "Backtracking", this);
            frame.setVisible(false);
        }
    }

    public void backtracking(int level, long sum, String selectPath) {
        String name = sum+"";
        Dimension size = getDimension(name);
        Node node = new Node(sum, selectPath, size.width, size.height);
        node.setPosition();
        if (selectPath.isEmpty())
            tree = new Trees(node);
        else
            Trees.insert(tree, node, selectPath);
        if (sum == target) {
            if (level < set.length)
                backtracking(level + 1, sum, selectPath + "0");
            else
                subsetSumSelected.add(selectPath);
        } else if (sum < target && level < set.length) {
            if(sum+set[level] <= target)
                backtracking(level + 1, sum + set[level], selectPath + "1");
            backtracking(level + 1, sum, selectPath + "0");
        }
    }
    
    public Dimension getDimension(String text) {
        BufferedImage image = new BufferedImage(900, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        int width = g.getFontMetrics(font).stringWidth(text);
        int height = g.getFontMetrics(font).getHeight();
        return new Dimension(width, height);
    }
    
    public void show(){
        frame.setVisible(true);
        result = null;
    }
    
    public String[] input(String setInput, String separate) {
        if (setInput.length() == 0)
            return null;
        long sum = 0;
        String[] input = setInput.split(separate);
        if (input.length == 0) {
            JOptionPane.showMessageDialog(frame.getContentPane(), "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        for (String str : input) {
            try {
                long numeric = Long.parseLong(str.trim());
                sum += numeric;
                if (str.length() == 0 || numeric <=0 || sum<0) {
                    String typeError = sum<0 ? "Summation of these positive integer exceeds the upper limit" :
                                       numeric <= 0 ? "Only positive integer greater than 0 within input set" :
                                       "Invalid Input";
                    JOptionPane.showMessageDialog(frame.getContentPane(), typeError, "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame.getContentPane(), "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return input;
    }
    
    private void setInputText(String[] inputs, long targets) {
        String inputSet = "";
        for (String str : inputs) 
            inputSet += Long.parseLong(str.trim())+", ";
        inputSet = inputSet.substring(0, inputSet.length()-2);
        inputText.setText(inputSet);
        targetSpinner.setValue(targets);
    }

    public void setInputToVariable(String[] inputs, String targets) {
        set = new long[inputs.length];
        int count = 0;
        for (String str : inputs)
            set[count++] = Long.parseLong(str.trim());
        target = Long.parseLong(targets);
    }

    public void clear(ActionEvent e) {
        inputText.setText("");
        targetSpinner.setValue(1);
        minRangeSetSpinner.setValue(MIN_SET);
        minRangeTargetSpinner.setValue(MIN_TARGET);
        maxRangeSetSpinner.setValue(MAX_SET);
        maxRangeTargetSpinner.setValue(MAX_TARGET);
        setNumberSlider.setValue(setNumberSlider.getMinimum());
    }
    
    public void submit(ActionEvent e, String algoSelected) {
        String[] inputs = input(inputText.getText(), ",");
        if(inputs == null)
            return;
        setInputToVariable(inputs, targetSpinner.getValue().toString());
        if(algoSelected == null) {
            Object[] algoName = {"Backtracking","Branch and Bound"};
            algoSelected = (String)JOptionPane.showInputDialog(frame.getContentPane(),"Select algorithm to calculate"
                    ,"Find Subset Sum",JOptionPane.PLAIN_MESSAGE,null,algoName,null);
            if(algoSelected == null)
                return;
        }
        if(algoSelected.equals("Backtracking"))
            backtrackingAlgorithm();
        else if(algoSelected.equals("Branch and Bound"))
            branchAndBoundAlgorithm();
    }

    public void generate(ActionEvent e) {
        int minset = Integer.parseInt(minRangeSetSpinner.getValue().toString());
        int mintarget = Integer.parseInt(minRangeTargetSpinner.getValue().toString());
        int maxset = Integer.parseInt(maxRangeSetSpinner.getValue().toString());
        int maxtarget = Integer.parseInt(maxRangeTargetSpinner.getValue().toString());
        int number = setNumberSlider.getValue();
        if (minset > maxset || mintarget > maxtarget) {
            JOptionPane.showMessageDialog(frame.getContentPane(), "Invalid Range", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Random rand = new Random();
        long targetRandom = rand.nextInt((maxtarget - mintarget) + 1) + mintarget;
        String[] setRandom = new String[number];
        for (int i = 0; i < number; i++) 
            setRandom[i] = rand.nextInt((maxset - minset) + 1) + minset + "";
        setInputText(setRandom, targetRandom);
    }
    
    public void openFile(ActionEvent e) {
        FileDialog fd = new FileDialog(frame, "Open File", FileDialog.LOAD);
        fd.setFile("*.txt");
        fd.setVisible(true);
        if (fd.getFile() != null) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(fd.getDirectory()+fd.getFile()));
                Object[] lines = br.lines().toArray();
                if(lines.length >= 2) {
                    String[] inputs = input(lines[0].toString(), " ");
                    String inputTarget = lines[1].toString().trim();
                    try {
                        if(inputs != null) {
                            long targets = Long.parseLong(inputTarget);
                            setInputText(inputs, targets);
                        }
                    }
                    catch(NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame.getContentPane(), "Can not open file \' "+fd.getFile()+" \'", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else 
                    JOptionPane.showMessageDialog(frame.getContentPane(), "Can not open file \' "+fd.getFile()+" \'", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch(FileNotFoundException ex) {}
            finally {
                try {
                    if(br != null)
                        br.close();
                } catch (IOException ex) { }
            }
        }
    }
    
    public void exit(ActionEvent e) {
        System.exit(0);
    }
    
    public void backtrackingSolution(ActionEvent e) {
        submit(e,"Backtracking");
    }
    
    public void branchAndBoundSolution(ActionEvent e) {
        submit(e,"Branch and Bound");
    }
    
    private void createMenuBar() {
        openFile.addActionListener(this::openFile);
        exit.addActionListener(this::exit);
        clear.addActionListener(this::clear);
        randomGenerate.addActionListener(this::generate);
        backtrackingSolution.addActionListener(this::backtrackingSolution);
        branchAndBoundSolution.addActionListener(this::branchAndBoundSolution);
        fileMenu.add(openFile);
        fileMenu.add(exit);
        editMenu.add(clear);
        editMenu.add(randomGenerate);
        editMenu.add(backtrackingSolution);
        editMenu.add(branchAndBoundSolution);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        frame.setJMenuBar(menuBar);
    }
    
    private void createGUI() {
        backgroundPanel.setBackground(new Color(102, 0, 153));
        backgroundPanel.setPreferredSize(new Dimension(1080, 620));

        randomPanel.setBackground(new Color(255, 204, 0));
        randomPanel.setBorder(BorderFactory.createTitledBorder(null, "Random", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, font, RANDOM_COLOR)); // NOI18N
//        randomPanel.setToolTipText("Random Set and Target");

        setRangeLabel.setBackground(new Color(255, 204, 0));
        setRangeLabel.setFont(font); // NOI18N
        setRangeLabel.setForeground(RANDOM_COLOR);
        setRangeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        setRangeLabel.setText("Range of Set");

        targetRangeLabel.setFont(font); // NOI18N
        targetRangeLabel.setForeground(RANDOM_COLOR);
        targetRangeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        targetRangeLabel.setText("Range of Target");

        setNumberLabel.setFont(font); // NOI18N
        setNumberLabel.setForeground(RANDOM_COLOR);
        setNumberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        setNumberLabel.setText("Number of set");

        setToPanel.setFont(font); // NOI18N
        setToPanel.setForeground(RANDOM_COLOR);
        setToPanel.setText("to");

        targetToPanel.setFont(font); // NOI18N
        targetToPanel.setForeground(RANDOM_COLOR);
        targetToPanel.setText("to");

        setNumberSlider.setFont(font); // NOI18N
        setNumberSlider.setMajorTickSpacing(5);
        setNumberSlider.setMaximum(20);
        setNumberSlider.setMinimum(5);
        setNumberSlider.setMinorTickSpacing(1);
        setNumberSlider.setPaintLabels(true);
        setNumberSlider.setPaintTicks(true);
        setNumberSlider.setSnapToTicks(true);
        setNumberSlider.setToolTipText("");
        setNumberSlider.setValue(5);

        generateButton.setBackground(new Color(204, 0, 204));
        generateButton.setFont(font); // NOI18N
        generateButton.setText("Generate");
        generateButton.addActionListener(this::generate);

        minRangeSetSpinner.setFont(font); // NOI18N
        minRangeSetSpinner.setModel(new SpinnerNumberModel(MIN_SET, 1, null, 1));

        minRangeTargetSpinner.setFont(font); // NOI18N
        minRangeTargetSpinner.setModel(new SpinnerNumberModel(MIN_TARGET, 1, null, 1));

        maxRangeSetSpinner.setFont(font); // NOI18N
        maxRangeSetSpinner.setModel(new SpinnerNumberModel(MAX_SET, 1, null, 1));

        maxRangeTargetSpinner.setFont(font); // NOI18N
        maxRangeTargetSpinner.setModel(new SpinnerNumberModel(MAX_TARGET, 1, null, 1));
        
        inputPanel.setBackground(new Color(255, 204, 0));
        inputPanel.setBorder(BorderFactory.createTitledBorder(null, "Input", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, font, INPUT_COLOR)); // NOI18N
        
        setLabel.setFont(font); // NOI18N
        setLabel.setForeground(INPUT_COLOR);
        setLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        setLabel.setText("Set");

        targetLabel.setFont(font); // NOI18N
        targetLabel.setForeground(INPUT_COLOR);
        targetLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        targetLabel.setText("Target");

        inputText.setFont(font); // NOI18N

        targetSpinner.setFont(font); // NOI18N
        targetSpinner.setModel(new SpinnerNumberModel(1, 1, null, 1));

        submitButton.setBackground(new Color(51, 255, 51));
        submitButton.setFont(font); // NOI18N
        submitButton.setForeground(new Color(0, 0, 0));
        submitButton.setText("Submit");
        submitButton.addActionListener((ActionEvent ae) -> {
            submit(ae, null);
        });

        clearButton.setBackground(new Color(255, 0, 51));
        clearButton.setFont(font); // NOI18N
        clearButton.setForeground(new Color(0, 0, 0));
        clearButton.setText("Clear");
        clearButton.addActionListener(this::clear);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void createLayout() {
        GroupLayout randomPanelLayout = new GroupLayout(randomPanel);
        randomPanel.setLayout(randomPanelLayout);
        randomPanelLayout.setHorizontalGroup(
            randomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(randomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(randomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(setRangeLabel, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetRangeLabel, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
                    .addComponent(setNumberLabel, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(randomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(randomPanelLayout.createSequentialGroup()
                        .addGroup(randomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(minRangeTargetSpinner, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(minRangeSetSpinner))
                        .addGap(18, 18, 18)
                        .addGroup(randomPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(setToPanel)
                            .addComponent(targetToPanel))
                        .addGap(18, 18, 18)
                        .addGroup(randomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(maxRangeTargetSpinner, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                            .addComponent(maxRangeSetSpinner, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)))
                    .addComponent(setNumberSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(generateButton)
                .addGap(25, 25, 25))
        );
        randomPanelLayout.setVerticalGroup(
            randomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(randomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(randomPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(maxRangeSetSpinner, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(setToPanel)
                    .addComponent(minRangeSetSpinner, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(setRangeLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(randomPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(maxRangeTargetSpinner, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetToPanel)
                    .addComponent(minRangeTargetSpinner, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetRangeLabel))
                .addGap(18, 18, 18)
                .addGroup(randomPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(setNumberSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(setNumberLabel)
                    .addComponent(generateButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        GroupLayout inputPanelLayout = new GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(setLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(targetLabel, GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(inputPanelLayout.createSequentialGroup()
                        .addComponent(targetSpinner, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                        .addGap(147, 147, 147)
                        .addComponent(clearButton)
                        .addGap(18, 18, 18)
                        .addComponent(submitButton))
                    .addComponent(inputText, GroupLayout.PREFERRED_SIZE, 507, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(inputPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(inputText, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(setLabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(inputPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(targetSpinner, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetLabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        GroupLayout backgroundPanelLayout = new GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(inputPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(randomPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(inputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(randomPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
        );
    }
    
    public static void main(String[] args) {
        try {             
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) { }
        HomeUI homeui = new HomeUI();
    }
}

class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node n1, Node n2) {
        if(n1.getSum() < n2.getSum())
            return 1;
        else if(n1.getSum() == n2.getSum()) {
            if(n1.getPath().length() < n2.getPath().length())
                return 1;
            else if(n1.getPath().length() > n2.getPath().length())
                return -1;
            return 0;
        }
        return -1;
    }
}