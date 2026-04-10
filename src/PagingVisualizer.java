import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PagingVisualizer extends JFrame {
    private JTextField referenceStringField;
    private JTextField workingSetField;
    private JComboBox<String> algorithmComboBox;
    private JButton executeButton;
    private JCheckBox stepModeCheckBox;
    private JButton nextStepButton;
    private JLabel pageFaultLabel;
    private VisualizationPanel visualizationPanel;
    private JScrollPane scrollPane;
    private int currentStep = -1;
    private int totalSteps = 0;

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
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Row 1: Inputs
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JLabel refLabel = new JLabel("Reference String:");
        refLabel.setFont(new Font("Arial", Font.BOLD, 14));
        row1.add(refLabel);

        referenceStringField = new JTextField(30);
        referenceStringField.setFont(new Font("Arial", Font.PLAIN, 14));
        referenceStringField.setText("7 0 1 2 0 3 0 4 2 3 0 3 2");
        row1.add(referenceStringField);

        JLabel wsLabel = new JLabel("Working Set:");
        wsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        row1.add(wsLabel);

        workingSetField = new JTextField(3);
        workingSetField.setFont(new Font("Arial", Font.PLAIN, 14));
        workingSetField.setText("3");
        row1.add(workingSetField);

        JLabel algoLabel = new JLabel("Algorithm:");
        algoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        row1.add(algoLabel);

        String[] algorithms = { "FIFO", "LRU", "Second Chance", "Optimal (OPT)" };
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        row1.add(algorithmComboBox);

        // Row 2: Actions
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        executeButton = new JButton("Run Simulation");
        executeButton.setToolTipText("Execute the selected paging algorithm");
        executeButton.setFont(new Font("Arial", Font.BOLD, 14));
        executeButton.setBackground(new Color(100, 150, 255));
        executeButton.setForeground(Color.WHITE);
        executeButton.setFocusPainted(false);
        executeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        executeButton.addActionListener(e -> executeAlgorithm());
        row2.add(executeButton);

        stepModeCheckBox = new JCheckBox("Step Mode");
        stepModeCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        stepModeCheckBox.addActionListener(e -> {
            nextStepButton.setEnabled(false);
            currentStep = -1;
        });
        row2.add(stepModeCheckBox);

        nextStepButton = new JButton("Next Step");
        nextStepButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextStepButton.setEnabled(false);
        nextStepButton.addActionListener(e -> showNextStep());
        row2.add(nextStepButton);

        mainPanel.add(row1);
        mainPanel.add(row2);

        return mainPanel;
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
                showError("Please enter a reference string! (e.g. 7, 0, 1)");
                return;
            }

            String frameInput = workingSetField.getText().trim();
            if (frameInput.isEmpty()) {
                showError("Please enter a working set size (1-10)!");
                return;
            }

            int frameCount;
            try {
                frameCount = Integer.parseInt(frameInput);
            } catch (NumberFormatException e) {
                showError("Working set must be a number!");
                return;
            }

            if (frameCount < 1 || frameCount > 10) {
                showError("Working set must be between 1 and 10!");
                return;
            }

            String[] tokens = input.split("[\\s,]+");
            int[] referenceString = new int[tokens.length];

            for (int i = 0; i < tokens.length; i++) {
                try {
                    referenceString[i] = Integer.parseInt(tokens[i].trim());
                } catch (NumberFormatException e) {
                    showError("Invalid page number: '" + tokens[i] + "'. Please use numbers only.");
                    return;
                }
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
                case "Optimal (OPT)":
                    OPTAlgorithm opt = new OPTAlgorithm(frameCount);
                    result = opt.execute(referenceString);
                    break;
            }

            if (result != null) {
                visualizationPanel.setVisualizationData(result, referenceString, algorithm);
                
                if (stepModeCheckBox.isSelected()) {
                    currentStep = 0;
                    totalSteps = referenceString.length;
                    visualizationPanel.setMaxDisplayedStep(currentStep);
                    nextStepButton.setEnabled(totalSteps > 1);
                    pageFaultLabel.setText("Step Mode Active: Step 1 of " + totalSteps);
                } else {
                    nextStepButton.setEnabled(false);
                    pageFaultLabel.setText("Total Page Faults: " + result.getTotalPageFaults());
                }
            }

        } catch (Exception ex) {
            showError("An unexpected error occurred: " + ex.getMessage());
        }
    }

    private void showNextStep() {
        if (currentStep < totalSteps - 1) {
            currentStep++;
            visualizationPanel.setMaxDisplayedStep(currentStep);
            
            if (currentStep == totalSteps - 1) {
                nextStepButton.setEnabled(false);
            }
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
