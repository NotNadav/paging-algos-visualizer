import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VisualizationPanel extends JPanel {
    private AlgorithmResult result;
    private int[] referenceString;
    private String algorithmName;
    private int maxDisplayedStep = -1;
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
        this.maxDisplayedStep = -1; // -1 means show all

        int width = PADDING * 2 + referenceString.length * (CELL_WIDTH + COLUMN_SPACING);
        int height = 500;
        setPreferredSize(new Dimension(Math.max(width, 1000), height));

        revalidate();
        repaint();
    }

    public void setMaxDisplayedStep(int step) {
        this.maxDisplayedStep = step;
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

        int limit = (maxDisplayedStep == -1) ? referenceString.length : (maxDisplayedStep + 1);

        for (int i = 0; i < limit; i++) {
            int columnX = startX + i * (CELL_WIDTH + COLUMN_SPACING);
            boolean isMiss = pageFaults.get(i);
            boolean isActive = (i == maxDisplayedStep);

            // Draw current reference page number
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2d.setColor(isActive ? new Color(243, 156, 18) : new Color(41, 128, 185)); // Orange if active, blue otherwise
            String pageNum = String.valueOf(referenceString[i]);
            FontMetrics fm = g2d.getFontMetrics();
            int pageNumX = columnX + (CELL_WIDTH - fm.stringWidth(pageNum)) / 2;
            g2d.drawString(pageNum, pageNumX, startY);

            // Draw frames
            for (int frameIdx = 0; frameIdx < frameCount; frameIdx++) {
                int frameY = startY + 20 + frameIdx * CELL_HEIGHT;
                PageFrame frame = steps.get(i)[frameIdx];

                // Background for the cell
                if (isMiss) {
                    g2d.setColor(new Color(255, 235, 238)); // Very light red
                } else {
                    g2d.setColor(new Color(232, 245, 233)); // Very light green
                }
                g2d.fillRoundRect(columnX, frameY, CELL_WIDTH - 2, CELL_HEIGHT - 2, 8, 8);

                // Border
                g2d.setStroke(new BasicStroke(isActive ? 3.0f : 1.5f)); // Thicker border for active step
                if (isActive) {
                    g2d.setColor(new Color(243, 156, 18)); // Sun Flower Orange
                } else if (isMiss) {
                    g2d.setColor(new Color(231, 76, 60)); // Alizarin red
                } else {
                    g2d.setColor(new Color(46, 204, 113)); // Emerald green
                }
                g2d.drawRoundRect(columnX, frameY, CELL_WIDTH - 2, CELL_HEIGHT - 2, 8, 8);

                if (frame != null) {
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    g2d.setColor(new Color(44, 62, 80)); // Dark blue-grey
                    String text = String.valueOf(frame.getPageNumber());
                    fm = g2d.getFontMetrics();
                    int textX = columnX + (CELL_WIDTH - fm.stringWidth(text)) / 2;
                    int textY = frameY + (CELL_HEIGHT + fm.getAscent()) / 2 - 2;
                    g2d.drawString(text, textX, textY);
                }
            }

            // Miss/Hit Indicator
            int indicatorY = startY + 20 + frameCount * CELL_HEIGHT + 25;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String indicator = isMiss ? "MISS" : "HIT";
            g2d.setColor(isMiss ? new Color(192, 57, 43) : new Color(39, 174, 96));
            fm = g2d.getFontMetrics();
            int indicatorX = columnX + (CELL_WIDTH - fm.stringWidth(indicator)) / 2;
            g2d.drawString(indicator, indicatorX, indicatorY);
        }

        // Summary Line (only show if finished)
        if (maxDisplayedStep == -1 || maxDisplayedStep == referenceString.length - 1) {
            int totalY = startY + 20 + frameCount * CELL_HEIGHT + 60;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2d.setColor(new Color(44, 62, 80));
            String totalText = "Final Result: " + result.getTotalPageFaults() + " Page Faults";
            g2d.drawString(totalText, startX, totalY);
        }
    }
}
