import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Leave extends JFrame{
    private JPanel panel1;
    private JTextField textField1;
    private JComboBox comboBox1;
    private JButton searchButton;
    private JTable table1;
    private JButton backButton;
    private JButton addButton;




        public static void main(String[] args){
            new Leave("da");
        }

        public Leave(String username ){
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension size = toolkit.getScreenSize();
            setLocation(size.width / 2 - getWidth() / 2- 100, size.height / 2 - getHeight() / 2 - 400);

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setContentPane(panel1);
            setSize(600, 700);
            setVisible(true);
            setTitle("BLANA");
            createTable();
            createCombo();

            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedValue = (String) comboBox1.getSelectedItem();
                    System.out.println("Selected Item: " + selectedValue);
                }
            });

            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new HomePage(username);
                }
            });
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addNewRow();
                }
            });
        }

    private void createTable() {
        Object[][] data = {
                {"aaa", "bb", "ccc", "ddd", "K"},
        };

        DefaultTableModel model = new DefaultTableModel(data, new String[]{"P", "L", "M", "1", "0"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make column 3 (index 2) unmodifiable
                return column != 2;
            }
        };
        table1.setModel(model);
    }

    private void addNewRow() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        // Add a new row with sample data, you can replace this with your actual data
        Object[] newRowData = {"new", "row", "data", "1", "2"};
        model.addRow(newRowData);
    }


        private void createCombo(){
            comboBox1.setModel(new DefaultComboBoxModel(new String[]{"x","D","K"}));
        }

    }



