import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PagingVisualizer extends JFrame {
    private JTextField referenceStringField;
    private JTextField workingSetField;
    private JComboBox<String> algorithmComboBox;
    private JButton executeButton;
    private JLabel pageFaultLabel;
    private VisualizationPanel visualizationPanel;
    private JScrollPane scrollPane;

    public PagingVisualizer() {
        setTitle("Paging Replacement Algorithm Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        visualizationPanel = new VisualizationPanel();
        scrollPane = new JScrollPane(visualizationPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JPanel resultPanel = createResultPanel();
        add(resultPanel, BorderLayout.SOUTH);

        setSize(1200, 600);
        setLocationRelativeTo(null);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel refLabel = new JLabel("Reference String:");
        refLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(refLabel);

        referenceStringField = new JTextField(30);
        referenceStringField.setFont(new Font("Arial", Font.PLAIN, 14));
        referenceStringField.setText("7 0 1 2 0 3 0 4 2 3 0 3 2");
        panel.add(referenceStringField);

        JLabel wsLabel = new JLabel("Working Set:");
        wsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(wsLabel);

        workingSetField = new JTextField(3);
        workingSetField.setFont(new Font("Arial", Font.PLAIN, 14));
        workingSetField.setText("3");
        panel.add(workingSetField);

        JLabel algoLabel = new JLabel("Algorithm:");
        algoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(algoLabel);

        String[] algorithms = { "FIFO", "LRU", "Second Chance" };
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(algorithmComboBox);

        executeButton = new JButton("asado");
        executeButton.setFont(new Font("Arial", Font.BOLD, 14));
        executeButton.setBackground(new Color(100, 150, 255));
        executeButton.setForeground(Color.WHITE);
        executeButton.setFocusPainted(false);
        executeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        executeButton.addActionListener(e -> executeAlgorithm());
        panel.add(executeButton);

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pageFaultLabel = new JLabel("Total Page Faults: -");
        pageFaultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pageFaultLabel.setForeground(new Color(200, 0, 0));
        panel.add(pageFaultLabel);

        return panel;
    }

    private void executeAlgorithm() {
        try {
            String input = referenceStringField.getText().trim();
            if (input.isEmpty()) {
                showError("Please enter a reference string!");
                return;
            }

            int frameCount = Integer.parseInt(workingSetField.getText().trim());
            if (frameCount < 1 || frameCount > 10) {
                showError("Working set must be between 1 and 10!");
                return;
            }

            String[] tokens = input.split("[\\s,]+");
            int[] referenceString = new int[tokens.length];

            for (int i = 0; i < tokens.length; i++) {
                referenceString[i] = Integer.parseInt(tokens[i].trim());
            }

            String algorithm = (String) algorithmComboBox.getSelectedItem();

            AlgorithmResult result = null;
            switch (algorithm) {
                case "FIFO":
                    FIFOAlgorithm fifo = new FIFOAlgorithm(frameCount);
                    result = fifo.execute(referenceString);
                    break;
                case "LRU":
                    LRUAlgorithm lru = new LRUAlgorithm(frameCount);
                    result = lru.execute(referenceString);
                    break;
                case "Second Chance":
                    SecondChanceAlgorithm secondChance = new SecondChanceAlgorithm(frameCount);
                    result = secondChance.execute(referenceString);
                    break;
            }

            if (result != null) {
                visualizationPanel.setVisualizationData(result, referenceString, algorithm);
                pageFaultLabel.setText("Total Page Faults: " + result.getTotalPageFaults());
            }

        } catch (NumberFormatException ex) {
            showError("Invalid reference string! Please enter numbers separated by spaces or commas.");
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            PagingVisualizer frame = new PagingVisualizer();
            frame.setVisible(true);
        });
    }
}
