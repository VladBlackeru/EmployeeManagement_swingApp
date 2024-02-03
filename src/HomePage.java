import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HomePage extends JFrame{
    JButton okButton;
    private JPanel panel1;
    private JLabel HelloUser;
    private JLabel Departament;
    private JButton trainingButton;
    private JButton payrollButton;
    private JButton performanceButton;
    private JButton attendenceButton;
    private JButton Leave;
    private JButton manageSubordinatesButton;
    private JButton logOutButton;
    private JButton changePassButton;

    public static void main(String[] args){
       new HomePage("A");
    }

    public HomePage(String username){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width / 2 - getWidth() / 2 - 100, size.height / 2 - getHeight() / 2 - 400);

        Connection connect= null;
        try {
            connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Employee_Management", "postgres", "177013");
        }catch (SQLException bhehe){
            bhehe.printStackTrace();
        }

        String query = "SELECT\n" + "    E.FirstName || ' ' || E.LastName AS EmployeeName,\n" +
                "    D.DepartmentName\n" +
                "FROM\n" +
                "    UserCredentials UC\n" +
                "JOIN\n" +
                "    Employee E ON UC.EmployeeID = E.EmployeeID\n" +
                "JOIN\n" +
                "    Department D ON E.DepartmentID = D.DepartmentID\n" +
                "WHERE\n" +
                "    UC.Username = ?" ;
        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            String Name="",Dep="";
            while(resultSet.next()){
                Name = resultSet.getString("employeename");
                Dep = resultSet.getString("departmentname");
            }
            HelloUser.setText("Hello " + Name);
            Departament.setText("Departament "+Dep);
        }catch (SQLException d){
            d.printStackTrace();
        }
       /* String role = null;
        query = "Select role from usercredentials where username = ?";
        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                role = resultSet.getString("role");
            }
        }catch (SQLException d){
            d.printStackTrace();
            role = "cacat";
        }*/

        ///setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        setSize(600, 700);
        setVisible(true);
        setTitle("BLANA");



        trainingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(okButton,"Not yet implemented");
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


       // System.out.println(role);
       // if(role != null && !role.equals("Employee")) {
            //System.out.println("muie");
            manageSubordinatesButton.addActionListener(e ->  {
                    dispose();
                    new ManagePeople(username);
            });
        //}

        logOutButton.addActionListener(e -> {
            dispose();
            new Login();
        });
        changePassButton.addActionListener(e -> {
            dispose();
            new ChangePass(username);
        });
    }


}
