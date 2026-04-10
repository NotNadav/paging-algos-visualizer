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
        setTitle("paging replacement algorithm visualizer");
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

        // row 1: inputs
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JLabel refLabel = new JLabel("reference string:");
        refLabel.setFont(new Font("Arial", Font.BOLD, 14));
        row1.add(refLabel);

        referenceStringField = new JTextField(30);
        referenceStringField.setFont(new Font("Arial", Font.PLAIN, 14));
        referenceStringField.setText("7 0 1 2 0 3 0 4 2 3 0 3 2");
        row1.add(referenceStringField);

        JLabel wsLabel = new JLabel("working set:");
        wsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        row1.add(wsLabel);

        workingSetField = new JTextField(3);
        workingSetField.setFont(new Font("Arial", Font.PLAIN, 14));
        workingSetField.setText("3");
        row1.add(workingSetField);

        JLabel algoLabel = new JLabel("algorithm:");
        algoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        row1.add(algoLabel);

        String[] algorithms = { "fifo", "lru", "second chance", "optimal (opt)" };
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        row1.add(algorithmComboBox);

        // row 2: actions
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        executeButton = new JButton("run simulation");
        executeButton.setToolTipText("Execute the selected paging algorithm");
        executeButton.setFont(new Font("Arial", Font.BOLD, 14));
        executeButton.setBackground(new Color(100, 150, 255));
        executeButton.setForeground(Color.BLACK);
        executeButton.setFocusPainted(false);
        executeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        executeButton.addActionListener(e -> executeAlgorithm());
        row2.add(executeButton);

        stepModeCheckBox = new JCheckBox("step mode");
        stepModeCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        stepModeCheckBox.addActionListener(e -> {
            nextStepButton.setEnabled(false);
            currentStep = -1;
        });
        row2.add(stepModeCheckBox);

        nextStepButton = new JButton("next step");
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

        pageFaultLabel = new JLabel("total page faults: -");
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
                case "fifo":
                    FIFOAlgorithm fifo = new FIFOAlgorithm(frameCount);
                    result = fifo.execute(referenceString);
                    break;
                case "lru":
                    LRUAlgorithm lru = new LRUAlgorithm(frameCount);
                    result = lru.execute(referenceString);
                    break;
                case "second chance":
                    SecondChanceAlgorithm secondChance = new SecondChanceAlgorithm(frameCount);
                    result = secondChance.execute(referenceString);
                    break;
                case "optimal (opt)":
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
                    pageFaultLabel.setText("step mode active: step 1 of " + totalSteps);
                } else {
                    nextStepButton.setEnabled(false);
                    pageFaultLabel.setText("total page faults: " + result.getTotalPageFaults());
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
