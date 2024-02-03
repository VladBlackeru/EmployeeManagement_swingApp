import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingTableDemo {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SwingTableDemo();
        });
    }

    public SwingTableDemo() {
        frame = new JFrame("Swing Table Demo");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Age");

        // Create the table with the model
        table = new JTable(tableModel);

        // Create a scroll pane and add the table to it
        JScrollPane scrollPane = new JScrollPane(table);

        // Create buttons for adding, editing, and deleting rows
        JButton addButton = new JButton("Add Row");
        JButton editButton = new JButton("Edit Row");
        JButton deleteButton = new JButton("Delete Row");
        JButton saveButton = new JButton("Save Changes");

        // Add action listeners to the buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRow();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editRow();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRow();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);

        // Create a panel for the table and add the scroll pane
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add the panels to the frame
        frame.add(tablePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Make the frame visible
        frame.setVisible(true);
    }

    private void addRow() {
        // Prompt the user for input and add a new row to the table
        String name = JOptionPane.showInputDialog(frame, "Enter Name:");
        String age = JOptionPane.showInputDialog(frame, "Enter Age:");

        if (name != null && age != null) {
            tableModel.addRow(new Object[]{name, age});
        }
    }

    private void editRow() {
        // Get the selected row and prompt the user for input to edit the row
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            String name = JOptionPane.showInputDialog(frame, "Enter Name:", tableModel.getValueAt(selectedRow, 0));
            String age = JOptionPane.showInputDialog(frame, "Enter Age:", tableModel.getValueAt(selectedRow, 1));

            if (name != null && age != null) {
                tableModel.setValueAt(name, selectedRow, 0);
                tableModel.setValueAt(age, selectedRow, 1);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a row to edit.");
        }
    }

    private void deleteRow() {
        // Get the selected row and remove it from the table
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a row to delete.");
        }
    }

    private void saveChanges() {
        // In a real application, you would save the changes to a file or database
        JOptionPane.showMessageDialog(frame, "Changes saved successfully.");
    }
}
