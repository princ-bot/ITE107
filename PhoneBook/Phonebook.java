package PhoneBook;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Phonebook extends JFrame implements ActionListener {
    private JTextField nameField, phoneField;
    private JTable phonebookTable;
    private DefaultTableModel tableModel;
    private ArrayList<Contact> contactList;
    private JButton addButton, updateButton, deleteButton, clearButton;
    String FILE_NAME = System.getProperty("user.dir") + File.separator + "PhoneBook" + File.separator + "log.txt";


    public Phonebook() {
        setTitle("Phonebook Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        getContentPane().setBackground(new Color(31, 31, 31));
        contactList = new ArrayList<>();
        loadContactsFromFile();

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(31, 31, 31));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        inputPanel.add(nameLabel, gbc);
        nameField = DarkModeField();
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(Color.WHITE);
        inputPanel.add(phoneLabel, gbc);
        phoneField = DarkModeField();
        gbc.gridx = 1;
        inputPanel.add(phoneField, gbc);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(31, 31, 31));

        addButton = DarkModeButton("Add Contact");
        updateButton = DarkModeButton("Update Contact");
        deleteButton = DarkModeButton("Delete Contact");
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

        tableModel = new DefaultTableModel(new String[] { "Name", "Phone" }, 0);
        phonebookTable = new JTable(tableModel);
        phonebookTable.setBackground(new Color(45, 45, 45));
        phonebookTable.setForeground(Color.WHITE);
        phonebookTable.setGridColor(Color.DARK_GRAY);
        phonebookTable.setSelectionBackground(new Color(70, 70, 70));
        phonebookTable.setSelectionForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        phonebookTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        phonebookTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JTableHeader tableHeader = phonebookTable.getTableHeader();
        tableHeader.setBackground(new Color(31, 31, 31));
        tableHeader.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(phonebookTable);
        scrollPane.getViewport().setBackground(new Color(31, 31, 31));
        add(scrollPane, BorderLayout.CENTER);

        populateTable();
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

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }

            public void mouseExited(MouseEvent evt) {
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
            String name = nameField.getText();
            String phone = phoneField.getText();

            if (findContact(name) != -1) {
                JOptionPane.showMessageDialog(this, "Contact name already exists!");
                return;
            }

            Contact contact = new Contact(name, phone);
            contactList.add(contact);
            tableModel.addRow(new Object[] { name, phone });
            saveContactsToFile();
            clearFields();
        } else if (e.getSource() == updateButton) {
            String name = nameField.getText();
            int selectedRow = findContact(name);
            if (selectedRow >= 0) {
                String phone = phoneField.getText();

                Contact contact = new Contact(name, phone);
                contactList.set(selectedRow, contact);
                tableModel.setValueAt(phone, selectedRow, 1);
                saveContactsToFile();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No contact found with the given name!");
            }
        } else if (e.getSource() == deleteButton) {
            String name = nameField.getText();
            int selectedRow = findContact(name);
            if (selectedRow >= 0) {
                contactList.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                saveContactsToFile();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No contact found with the given name!");
            }
        } else if (e.getSource() == clearButton) {
            clearFields();
        }
    }

    private int findContact(String name) {
        for (int i = 0; i < contactList.size(); i++) {
            if (contactList.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
    }

    private void loadContactsFromFile() {
        File directory = new File("PhoneBook");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    contactList.add(new Contact(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("No previous phonebook file found. Starting fresh.");
        }
    }

    private void saveContactsToFile() {
        File directory = new File("PhoneBook");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Contact contact : contactList) {
                writer.write(contact.getName() + "," + contact.getPhone());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving phonebook to file!");
        }
    }

    private void populateTable() {
        for (Contact contact : contactList) {
            tableModel.addRow(new Object[] { contact.getName(), contact.getPhone() });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Phonebook app = new Phonebook();
            app.setVisible(true);
        });
    }
}

class Contact {
    private String name, phone;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
