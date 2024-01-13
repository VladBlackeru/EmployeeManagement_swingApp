import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Attendence extends JFrame{

    private JTable table1;
    private JTextField textField1;
    private JPanel Panel;
    private JButton backButton;
    private JButton searchButton;
    private JComboBox comboBox1;
    private JButton ADDButton;

    public static void main(String[] args){
        new Attendence("name");
    }

    public Attendence(String username){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width / 2 - getWidth() / 2- 100, size.height / 2 - getHeight() / 2 - 400);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(Panel);
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


    }

    private void createTable(){
        Object[][] data = {
                {"aaa","bb","ccc","ddd","K"},
        };
        table1.setModel(new DefaultTableModel(data, new String[]{"P","L","M","1","0"}));
    }

    private void createCombo(){
        comboBox1.setModel(new DefaultComboBoxModel(new String[]{"x","D","K"}));
    }

}
