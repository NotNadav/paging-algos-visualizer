import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VisualizationPanel extends JPanel {
    private AlgorithmResult result;
    private int[] referenceString;
    private String algorithmName;
    private static final int CELL_WIDTH = 60;
    private static final int CELL_HEIGHT = 50;
    private static final int PADDING = 40;
    private static final int COLUMN_SPACING = 10;

    public VisualizationPanel() {
        setPreferredSize(new Dimension(1000, 500));
        setBackground(Color.WHITE);
    }

    public void setVisualizationData(AlgorithmResult result, int[] referenceString, String algorithmName) {
        this.result = result;
        this.referenceString = referenceString;
        this.algorithmName = algorithmName;

        int width = PADDING * 2 + referenceString.length * (CELL_WIDTH + COLUMN_SPACING);
        int height = 500;
        setPreferredSize(new Dimension(Math.max(width, 1000), height));

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (result == null || referenceString == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<PageFrame[]> steps = result.getSteps();
        List<Boolean> pageFaults = result.getPageFaults();
        int frameCount = steps.get(0).length;

        int labelY = PADDING;
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Page", 10, labelY);
        g2d.drawString("reference", 10, labelY + 20);

        StringBuilder refStr = new StringBuilder();
        for (int i = 0; i < referenceString.length; i++) {
            if (i > 0)
                refStr.append(", ");
            refStr.append(referenceString[i]);
        }
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString(refStr.toString(), 120, labelY + 10);

        int startX = 120;
        int startY = PADDING + 60;

        for (int i = 0; i < referenceString.length; i++) {
            int columnX = startX + i * (CELL_WIDTH + COLUMN_SPACING);

            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.setColor(new Color(0, 128, 0));
            String pageNum = String.valueOf(referenceString[i]);
            FontMetrics fm = g2d.getFontMetrics();
            int pageNumX = columnX + (CELL_WIDTH - fm.stringWidth(pageNum)) / 2;
            g2d.drawString(pageNum, pageNumX, startY);

            for (int frameIdx = 0; frameIdx < frameCount; frameIdx++) {
                int frameY = startY + 20 + frameIdx * CELL_HEIGHT;
                PageFrame frame = steps.get(i)[frameIdx];

                g2d.setColor(Color.WHITE);
                g2d.fillRect(columnX, frameY, CELL_WIDTH, CELL_HEIGHT);

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRect(columnX, frameY, CELL_WIDTH, CELL_HEIGHT);

                if (frame != null) {
                    g2d.setFont(new Font("Arial", Font.PLAIN, 16));
                    g2d.setColor(new Color(0, 100, 0));
                    String text = String.valueOf(frame.getPageNumber());
                    fm = g2d.getFontMetrics();
                    int textX = columnX + (CELL_WIDTH - fm.stringWidth(text)) / 2;
                    int textY = frameY + (CELL_HEIGHT + fm.getAscent()) / 2 - 2;
                    g2d.drawString(text, textX, textY);
                }
            }

            int indicatorY = startY + 20 + frameCount * CELL_HEIGHT + 25;
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            String indicator = pageFaults.get(i) ? "Miss" : "Hit";
            g2d.setColor(new Color(0, 128, 0));
            fm = g2d.getFontMetrics();
            int indicatorX = columnX + (CELL_WIDTH - fm.stringWidth(indicator)) / 2;
            g2d.drawString(indicator, indicatorX, indicatorY);
        }

        int totalY = startY + 20 + frameCount * CELL_HEIGHT + 60;
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.setColor(new Color(0, 128, 0));
        String totalText = "Total Page Fault = " + result.getTotalPageFaults();
        g2d.drawString(totalText, 10, totalY);
    }
}
