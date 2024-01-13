import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame{
    private JPanel panel1;
    private JLabel HelloUser;
    private JLabel Departament;
    private JButton trainingButton;
    private JButton payrollButton;
    private JButton performanceButton;
    private JButton attendenceButton;
    private JButton Leave;
    private JButton manageSubordinatesButton;

    public static void main(String[] args){
       new HomePage("da");
    }

    public HomePage(String username){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width / 2 - getWidth() / 2 - 100, size.height / 2 - getHeight() / 2 - 400);



        ///setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        setSize(600, 700);
        setVisible(true);
        setTitle("BLANA");
        HelloUser.setText("Hello " + username);


        trainingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Training(username);
            }
        });

        payrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Payroll(username);
            }
        });

        performanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Performance(username);
            }
        });


        attendenceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Attendence(username);
            }
        });


        Leave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Leave(username);
            }
        });

        manageSubordinatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Payroll(username);
            }
        });

    }

}
