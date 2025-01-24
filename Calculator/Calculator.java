package Calculator;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class RoundedButton extends JButton {
    private int radius;

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        setBackground(new Color(60, 60, 60));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(new Color(180, 180, 180));
            g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, radius, radius);
        } else {
            g2.setColor(new Color(100, 100, 100));
            g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, radius, radius);
        }

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.setColor(getForeground());
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 2;
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}

class RoundedTextField extends JTextField {
    private int radius;

    public RoundedTextField(String text, int radius) {
        super(text);
        this.radius = radius;
        setOpaque(false);
        setBorder(null);
        setForeground(Color.WHITE);
        setBackground(new Color(37, 37, 37));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        super.paintComponent(g);

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
    }
}

public class Calculator extends JFrame implements ActionListener {
    private JTextField textField;
    private RoundedButton[] numberButtons = new RoundedButton[10];
    private RoundedButton[] functionButtons = new RoundedButton[8];
    private RoundedButton addButton, subButton, mulButton, divButton;
    private RoundedButton decButton, equButton, delButton, clrButton;
    private JPanel panel;
    private JTextArea historyArea;

    private double num1 = 0, num2 = 0, result = 0;
    private char operator;
    private ArrayList<String> history;
    private static final String HISTORY_FILE = "log.txt";

    public Calculator() {
        this.setTitle("Calculator");
        this.setSize(400, 500);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(31, 31, 31));
        this.setResizable(false);

        history = new ArrayList<>();
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Arial", Font.PLAIN, 14));
        historyArea.setBackground(new Color(37, 37, 37));
        historyArea.setForeground(Color.WHITE);
        historyArea.setBounds(50, 80, 300, 100);
        this.add(historyArea);

        textField = new RoundedTextField("", 20);
        textField.setBounds(50, 20, 300, 50);
        textField.setFont(new Font("Arial", Font.BOLD, 20));
        this.add(textField);

        addButton = new RoundedButton("+", 20);
        subButton = new RoundedButton("-", 20);
        mulButton = new RoundedButton("*", 20);
        divButton = new RoundedButton("/", 20);
        decButton = new RoundedButton(".", 20);
        equButton = new RoundedButton("=", 20);
        delButton = new RoundedButton("Del", 20);
        clrButton = new RoundedButton("Clr", 20);

        functionButtons[0] = addButton;
        functionButtons[1] = subButton;
        functionButtons[2] = mulButton;
        functionButtons[3] = divButton;
        functionButtons[4] = decButton;
        functionButtons[5] = equButton;
        functionButtons[6] = delButton;
        functionButtons[7] = clrButton;

        for (RoundedButton button : functionButtons) {
            button.addActionListener(this);
            button.setFont(new Font("Arial", Font.BOLD, 18));
        }

        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new RoundedButton(String.valueOf(i), 20);
            numberButtons[i].addActionListener(this);
            numberButtons[i].setFont(new Font("Arial", Font.BOLD, 18));
        }

        panel = new JPanel();
        panel.setBounds(50, 190, 300, 200);
        panel.setLayout(new GridLayout(4, 4, 10, 10));
        panel.setBackground(new Color(31, 31, 31));

        panel.add(numberButtons[1]);
        panel.add(numberButtons[2]);
        panel.add(numberButtons[3]);
        panel.add(addButton);
        panel.add(numberButtons[4]);
        panel.add(numberButtons[5]);
        panel.add(numberButtons[6]);
        panel.add(subButton);
        panel.add(numberButtons[7]);
        panel.add(numberButtons[8]);
        panel.add(numberButtons[9]);
        panel.add(mulButton);
        panel.add(decButton);
        panel.add(numberButtons[0]);
        panel.add(equButton);
        panel.add(divButton);

        this.add(panel);
        this.add(delButton).setBounds(50, 400, 145, 50);
        this.add(clrButton).setBounds(205, 400, 145, 50);

        this.revalidate();
        this.repaint();

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 10; i++) {
            if (e.getSource() == numberButtons[i]) {
                textField.setText(textField.getText().concat(String.valueOf(i)));
            }
        }

        if (e.getSource() == decButton) {
            textField.setText(textField.getText().concat("."));
        }

        if (e.getSource() == addButton) {
            num1 = Double.parseDouble(textField.getText());
            operator = '+';
            textField.setText("");
        }

        if (e.getSource() == subButton) {
            num1 = Double.parseDouble(textField.getText());
            operator = '-';
            textField.setText("");
        }

        if (e.getSource() == mulButton) {
            num1 = Double.parseDouble(textField.getText());
            operator = '*';
            textField.setText("");
        }

        if (e.getSource() == divButton) {
            num1 = Double.parseDouble(textField.getText());
            operator = '/';
            textField.setText("");
        }

        if (e.getSource() == equButton) {
            num2 = Double.parseDouble(textField.getText());

            switch (operator) {
                case '+':
                    result = num1 + num2;
                    break;
                case '-':
                    result = num1 - num2;
                    break;
                case '*':
                    result = num1 * num2;
                    break;
                case '/':
                    result = num1 / num2;
                    break;
            }

            String resultText = (result % 1 == 0) ? String.format("%d", (int) result) : String.format("%.2f", result);
            textField.setText(resultText);

            String historyEntry = (num1 % 1 == 0 && num2 % 1 == 0)
                    ? String.format("%d %s %d = %d", (int) num1, operator, (int) num2, (int) result)
                    : String.format("%.2f %s %.2f = %.2f", num1, operator, num2, result);
            history.add(historyEntry);
            updateHistory();

            num1 = result;
        }


        if (e.getSource() == clrButton) {
            textField.setText("");
        }

        if (e.getSource() == delButton) {
            String currentText = textField.getText();
            if (!currentText.isEmpty()) {
                textField.setText(currentText.substring(0, currentText.length() - 1));
            }
        }
    }

    private void updateHistory() {
        StringBuilder historyText = new StringBuilder();
        for (String entry : history) {
            historyText.append(entry).append("\n");
        }
        historyArea.setText(historyText.toString());
        saveHistoryToFile();
    }

    private void saveHistoryToFile() {
        String projectDir = System.getProperty("user.dir");
        String historyDir = projectDir + File.separator + "Calculator" + File.separator + HISTORY_FILE;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyDir, true))) {
            writer.write(history.get(history.size() - 1));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving history to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Calculator();
    }
}
