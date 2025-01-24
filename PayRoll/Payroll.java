package PayRoll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Payroll extends JFrame implements ActionListener {
    private JTextField idField, nameField, rateField, hoursField;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private ArrayList<Employee> employeeList;
    private JButton addButton, updateButton, deleteButton, clearButton;

    // Define a fixed tax rate (e.g., 10%)
    private static final double TAX_RATE = 0.10;

    public Payroll() {
        setTitle("Payroll System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        getContentPane().setBackground(new Color(31, 31, 31));
        employeeList = new ArrayList<>();

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(31, 31, 31));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setForeground(Color.WHITE);
        inputPanel.add(idLabel, gbc);
        idField = DarkModeField();
        gbc.gridx = 1;
        inputPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        inputPanel.add(nameLabel, gbc);
        nameField = DarkModeField();
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel rateLabel = new JLabel("Rate:");
        rateLabel.setForeground(Color.WHITE);
        inputPanel.add(rateLabel, gbc);
        rateField = DarkModeField();
        gbc.gridx = 1;
        inputPanel.add(rateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel hoursLabel = new JLabel("Hours:");
        hoursLabel.setForeground(Color.WHITE);
        inputPanel.add(hoursLabel, gbc);
        hoursField = DarkModeField();
        gbc.gridx = 1;
        inputPanel.add(hoursField, gbc);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(31, 31, 31));

        addButton = DarkModeButton("Add Employee");
        updateButton = DarkModeButton("Update Employee");
        deleteButton = DarkModeButton("Delete Employee");
        clearButton = DarkModeButton("Clear Fields");

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Rate", "Hours", "Total", "Tax Deduction", "Net Total" }, 0);
        employeeTable = new JTable(tableModel);
        employeeTable.setBackground(new Color(45, 45, 45));
        employeeTable.setForeground(Color.WHITE);
        employeeTable.setGridColor(Color.DARK_GRAY);
        employeeTable.setSelectionBackground(new Color(70, 70, 70));
        employeeTable.setSelectionForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader tableHeader = employeeTable.getTableHeader();
        tableHeader.setBackground(new Color(31, 31, 31));
        tableHeader.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.getViewport().setBackground(new Color(31, 31, 31));

        add(scrollPane, BorderLayout.CENTER);

        loadFromFile();
    }

    private JButton DarkModeButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        button.setPreferredSize(new Dimension(120, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 50, 50));
            }
        });

        return button;
    }

    private JTextField DarkModeField() {
        JTextField textField = new JTextField(12);
        textField.setBackground(new Color(45, 45, 45));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        return textField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String id = idField.getText();
            String name = nameField.getText();
            double rate = Double.parseDouble(rateField.getText());
            double hours = Double.parseDouble(hoursField.getText());
            double total = rate * hours;

            double tax = total * TAX_RATE;
            double netTotal = total - tax;

            if (findEmployeeById(id) != -1) {
                JOptionPane.showMessageDialog(this, "Employee ID already exists! Please enter a unique ID.");
                return;
            }

            Employee emp = new Employee(id, name, rate, hours, total, tax, netTotal);
            employeeList.add(emp);
            tableModel.addRow(new Object[] { id, name, rate, hours, total, tax, netTotal });
            clearFields();
            saveToFile();
        } else if (e.getSource() == updateButton) {
            String id = idField.getText();
            int selectedRow = findEmployeeById(id);
            if (selectedRow >= 0) {
                String name = nameField.getText();
                double rate = Double.parseDouble(rateField.getText());
                double hours = Double.parseDouble(hoursField.getText());
                double total = rate * hours;

                double tax = total * TAX_RATE;
                double netTotal = total - tax;

                Employee emp = new Employee(id, name, rate, hours, total, tax, netTotal);
                employeeList.set(selectedRow, emp);
                tableModel.setValueAt(name, selectedRow, 1);
                tableModel.setValueAt(rate, selectedRow, 2);
                tableModel.setValueAt(hours, selectedRow, 3);
                tableModel.setValueAt(total, selectedRow, 4);
                tableModel.setValueAt(tax, selectedRow, 5);
                tableModel.setValueAt(netTotal, selectedRow, 6);
                clearFields();
                saveToFile();
            } else {
                JOptionPane.showMessageDialog(this, "No employee found with the given ID!");
            }
        } else if (e.getSource() == deleteButton) {
            String id = idField.getText();
            int selectedRow = findEmployeeById(id);
            if (selectedRow >= 0) {
                employeeList.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                clearFields();
                saveToFile();
            } else {
                JOptionPane.showMessageDialog(this, "No employee found with the given ID!");
            }
        } else if (e.getSource() == clearButton) {
            clearFields();
        }
    }

    private int findEmployeeById(String id) {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        rateField.setText("");
        hoursField.setText("");
    }

    private void saveToFile() {
        String projectDir = System.getProperty("user.dir");
        String payrollDir = projectDir + File.separator + "PayRoll" + File.separator + "log.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(payrollDir))) {
            for (Employee emp : employeeList) {
                writer.println(emp);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        String projectDir = System.getProperty("user.dir");
        String payrollDir = projectDir + File.separator + "PayRoll" + File.separator + "employees.txt";
        File file = new File(payrollDir);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    String id = data[0];
                    String name = data[1];
                    double rate = Double.parseDouble(data[2]);
                    double hours = Double.parseDouble(data[3]);
                    double total = Double.parseDouble(data[4]);
                    double tax = Double.parseDouble(data[5]);
                    double netTotal = Double.parseDouble(data[6]);
                    Employee emp = new Employee(id, name, rate, hours, total, tax, netTotal);
                    employeeList.add(emp);
                    tableModel.addRow(new Object[] { id, name, rate, hours, total, tax, netTotal });
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Payroll app = new Payroll();
            app.setVisible(true);
        });
    }
}

class Employee {
    private String id, name;
    private double rate, hours, total, tax, netTotal;

    public Employee(String id, String name, double rate, double hours, double total, double tax, double netTotal) {
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.hours = hours;
        this.total = total;
        this.tax = tax;
        this.netTotal = netTotal;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + "," + name + "," + rate + "," + hours + "," + total + "," + tax + "," + netTotal;
    }
}
