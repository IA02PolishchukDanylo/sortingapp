import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class SortApp extends JFrame {
    private static final int MAX_NUMBER_VALUE = 1000;
    private static final int ANIMATION_DELAY = 500;

    private JPanel introPanel;
    private JPanel sortPanel;
    private JPanel numbersPanel;
    private JTextField inputField;
    private List<Integer> numbers = new ArrayList<>();
    private boolean descending = true;
    private int numberCount;
    private JButton sortButton;
    private JButton resetButton;
    private int currentPivotIndex = -1;
    private int currentComparingIndex = -1;

    /**
     * Конструктор головного вікна додатку
     */
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

    /**
     * Створює панель введення з кількістю чисел
     */
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
                if (numberCount <= 0) {
                    throw new NumberFormatException();
                }
                if (numberCount > MAX_NUMBER_VALUE) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a number not greater than " + MAX_NUMBER_VALUE);
                    return;
                }
                generateNumbers();
                showSort();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number.");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(inputField, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(enterBtn, gbc);

        return panel;
    }

    /**
     * Створює панель сортування з кнопками та відображенням чисел
     */
    private JPanel createSortPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        numbersPanel = new JPanel();
        numbersPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));

        JPanel buttonPanel = new JPanel();
        sortButton = new JButton("Sort");
        resetButton = new JButton("Reset");

        sortButton.addActionListener(e -> {
            setButtonsEnabled(false);
            new Thread(() -> quickSortAnimate()).start();
        });

        resetButton.addActionListener(e -> {
            showIntro();
        });

        buttonPanel.add(sortButton);
        buttonPanel.add(resetButton);

        panel.add(new JScrollPane(numbersPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Генерує новий масив випадкових чисел
     */
    private void generateNumbers() {
        Random rand = new Random();
        numbers.clear();

        int guaranteed = rand.nextInt(30) + 1;
        numbers.add(guaranteed);

        while (numbers.size() < numberCount) {
            numbers.add(rand.nextInt(MAX_NUMBER_VALUE) + 1);
        }

        refreshNumbers();
    }

    /**
     * Генерує новий масив заданого розміру
     */
    private void generateNewNumbers(int count) {
        if (count <= 0 || count > MAX_NUMBER_VALUE) {
            JOptionPane.showMessageDialog(this,
                    "Invalid number count: " + count);
            return;
        }

        numberCount = count;
        numbers.clear();
        Random rand = new Random();

        int guaranteed = rand.nextInt(30) + 1;
        numbers.add(guaranteed);

        while (numbers.size() < numberCount) {
            numbers.add(rand.nextInt(MAX_NUMBER_VALUE) + 1);
        }

        refreshNumbers();
    }

    /**
     * Оновлює відображення чисел на панелі
     */
    private void refreshNumbers() {
        numbersPanel.removeAll();

        int count = 0;
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        for (int i = 0; i < numbers.size(); i++) {
            Integer num = numbers.get(i);
            JButton btn = new JButton(String.valueOf(num));
            btn.addActionListener(e -> numberClicked(num));

            if (i == currentPivotIndex) {
                btn.setBackground(Color.RED);
                btn.setOpaque(true);
            } else if (i == currentComparingIndex) {
                btn.setBackground(Color.YELLOW);
                btn.setOpaque(true);
            } else {
                btn.setBackground(null);
            }

            column.add(btn);
            count++;

            if (count == 10) {
                numbersPanel.add(column);
                column = new JPanel();
                column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
                count = 0;
            }
        }

        if (count > 0) {
            numbersPanel.add(column);
        }

        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    /**
     * Обробляє клік на числі
     */
    private void numberClicked(int num) {
        if (num <= 30) {
            generateNewNumbers(num);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a value smaller or equal to 30.");
        }
    }

    /**
     * Запускає анімацію сортування
     */
    private void quickSortAnimate() {
        try {
            descending = true;
            quickSort(0, numbers.size() - 1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(() -> {
                setButtonsEnabled(true);
                currentPivotIndex = -1;
                currentComparingIndex = -1;
                refreshNumbers();
            });
        }
    }

    /**
     * Виконує швидке сортування з анімацією
     */
    private void quickSort(int low, int high) throws InterruptedException {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    /**
     * Виконує розділення масиву для швидкого сортування
     */
    private int partition(int low, int high) throws InterruptedException {
        int pivot = numbers.get(high);
        currentPivotIndex = high;
        int i = low - 1;

        for (int j = low; j < high; j++) {
            currentComparingIndex = j;
            refreshNumbers();
            Thread.sleep(ANIMATION_DELAY);

            boolean condition = descending ?
                    numbers.get(j) > pivot : numbers.get(j) < pivot;

            if (condition) {
                i++;
                Collections.swap(numbers, i, j);
                refreshNumbers();
                Thread.sleep(ANIMATION_DELAY);
            }
        }

        Collections.swap(numbers, i + 1, high);
        refreshNumbers();
        Thread.sleep(ANIMATION_DELAY);

        currentPivotIndex = -1;
        currentComparingIndex = -1;
        return i + 1;
    }

    /**
     * Вмикає/вимикає кнопки управління
     */
    private void setButtonsEnabled(boolean enabled) {
        sortButton.setEnabled(enabled);
        resetButton.setEnabled(enabled);
    }

    /**
     * Показує вступну панель
     */
    private void showIntro() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "intro");
    }

    /**
     * Показує панель сортування
     */
    private void showSort() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "sort");
        refreshNumbers();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SortApp::new);
    }
}