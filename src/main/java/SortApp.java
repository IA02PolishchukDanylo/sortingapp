import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SortApp extends JFrame {
    private JPanel introPanel, sortPanel, numbersPanel;
    private JTextField inputField;
    private java.util.List<Integer> numbers = new ArrayList<>();
    private boolean descending = true;
    private int numberCount;

    public SortApp() {
        setTitle("Sort Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new CardLayout());

        introPanel = createIntroPanel();
        sortPanel = createSortPanel();

        add(introPanel, "intro");
        add(sortPanel, "sort");

        showIntro();
        setVisible(true);
    }

    private JPanel createIntroPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Enter number count:");
        inputField = new JTextField(10);
        JButton enterBtn = new JButton("Enter");

        enterBtn.addActionListener(e -> {
            try {
                numberCount = Integer.parseInt(inputField.getText());
                if (numberCount <= 0) throw new NumberFormatException();
                generateNumbers();
                showSort();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number.");
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(inputField, gbc);
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(enterBtn, gbc);

        return panel;
    }

    private JPanel createSortPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        numbersPanel = new JPanel();
        numbersPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));

        JPanel buttonPanel = new JPanel();
        JButton sortBtn = new JButton("Sort");
        JButton resetBtn = new JButton("Reset");

        sortBtn.addActionListener(e -> new Thread(() -> quickSortAnimate()).start());
        resetBtn.addActionListener(e -> showIntro());

        buttonPanel.add(sortBtn);
        buttonPanel.add(resetBtn);

        panel.add(new JScrollPane(numbersPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void generateNumbers() {
        Random rand = new Random();
        numbers.clear();

        // ensure at least one ≤ 30
        int guaranteed = rand.nextInt(30) + 1;
        numbers.add(guaranteed);

        while (numbers.size() < numberCount) {
            numbers.add(rand.nextInt(1000) + 1);
        }

        refreshNumbers();
    }

    private void refreshNumbers() {
        numbersPanel.removeAll();

        int count = 0;
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        for (Integer num : numbers) {
            JButton btn = new JButton(String.valueOf(num));
            btn.addActionListener(e -> numberClicked(num));
            column.add(btn);
            count++;
            if (count == 10) {
                numbersPanel.add(column);
                column = new JPanel();
                column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
                count = 0;
            }
        }
        numbersPanel.add(column);
        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    private void numberClicked(int num) {
        if (num <= 30) generateNumbers();
        else JOptionPane.showMessageDialog(this, "Please select a value smaller or equal to 30.");
    }

    private void quickSortAnimate() {
        try {
            quickSort(0, numbers.size() - 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        descending = !descending;
    }

    private void quickSort(int low, int high) throws InterruptedException {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) throws InterruptedException {
        int pivot = numbers.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            boolean condition = descending ? numbers.get(j) > pivot : numbers.get(j) < pivot;
            if (condition) {
                i++;
                Collections.swap(numbers, i, j);
                refreshNumbers();
                Thread.sleep(200);
            }
        }
        Collections.swap(numbers, i + 1, high);
        refreshNumbers();
        Thread.sleep(200);
        return i + 1;
    }

    private void showIntro() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "intro");
    }

    private void showSort() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "sort");
        refreshNumbers();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SortApp::new);
    }
}

