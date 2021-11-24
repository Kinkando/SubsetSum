import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Node {
    public static Dimension screenSize;
    public static int maxWidth, maxHeight;
    public static final int SPACE_WIDTH = 20, SPACE_HEIGHT = 10, HORIZONTAL_GAP = 10, VERTICAL_GAP = 50;
    public static final Font FONT = new Font("TH Sarabun New", Font.PLAIN, 24);
    public static final Color NORMAL_COLOR = Color.BLACK, HIGHLIGHT_COLOR = Color.BLUE, HIGHLIGHT_NODE = Color.GREEN, NORMAL_NODE = Color.ORANGE;
    public static final BasicStroke NORMAL_STROKE = new BasicStroke(2), HIGHLIGHT_STROKE = new BasicStroke(4);
    private int x, y;
    private long number;
    private final long sum;
    private final int arcWidth, arcHeight, level, nameWidth, nameHeight, width, height;
    private final String name, path;
    public Node(long sum, String path, int nameWidth, int nameHeight) {
        this.sum = sum;
        this.name = sum+"";
        this.path = path;
        this.nameWidth = nameWidth;
        this.nameHeight = nameHeight;
        level = path.length();
        width = nameWidth + SPACE_WIDTH;
        height = nameHeight + SPACE_HEIGHT;
        arcWidth = 10;
        arcHeight = 10;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getName() {
        return name;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getLevel() {
        return level;
    }
    public String getPath() {
        return path;
    }
    public long getSum() {
        return sum;
    }
    public int getNameWidth() {
        return nameWidth;
    }
    public int getNameHeight() {
        return nameHeight;
    }
    public static void setScale(int length, String text) {
        BufferedImage image = new BufferedImage(900, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        maxWidth = g.getFontMetrics(FONT).stringWidth(text);
        maxHeight = g.getFontMetrics(FONT).getHeight();
        screenSize = new Dimension((int) Math.pow(2, length) * (maxWidth+SPACE_WIDTH+HORIZONTAL_GAP), 
                                        (length+1) * (maxHeight + SPACE_HEIGHT + VERTICAL_GAP));
        if(screenSize.width <= 0 || length > 20)
            screenSize = new Dimension(0, 0);
    }
    public int setPointX() {
        int block = (int) screenSize.width / (int) (Math.pow(2, level + 1));
        int start = (int) screenSize.width / (int) (Math.pow(2, level));
        int positionX = block + ((int)number * start);
        return positionX;
    }
    public int setPointY() {
        return ((level+1) * (maxHeight + SPACE_HEIGHT)) + (level * VERTICAL_GAP);
    }
    public void setPosition() {
        setNodeNumber();
        x = setPointX();
        y = setPointY();
    }
    public void setNodeNumber() {
        try {
            number = !path.isEmpty() ? (int) (Math.pow(2, path.length()) - Long.parseLong(path, 2))-1 : 0;
        }
        catch(NumberFormatException e) {
            number = 0;
        }
    }
    public void draw(Graphics2D g, boolean highlight) {
        g.setFont(FONT);
        g.setStroke(highlight ? HIGHLIGHT_STROKE : NORMAL_STROKE);
        g.setColor(highlight ? HIGHLIGHT_NODE : NORMAL_NODE);
        g.fillRoundRect(x-width/2, y-height/2, width, height, arcWidth, arcHeight);
        g.setColor(highlight ? HIGHLIGHT_COLOR : NORMAL_COLOR);
        g.drawRoundRect(x-width/2, y-height/2, width, height, arcWidth, arcHeight);
        g.drawString(name, x-nameWidth/2, y+nameHeight/4);
    }
}