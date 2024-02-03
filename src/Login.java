import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//// jdbc:postgresql://localhost:5432/Employee_Management

public class Login extends JFrame{
    private JPanel panel1;
    private JTextField Username;
    private JButton okButton;
    private JPasswordField Password;

    public static void main(String[] args){


         new Login();

    }

    public Login(){

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width / 2 - getWidth() / 2- 100, size.height / 2 - getHeight() / 2 - 200);
        setContentPane(panel1);
        setTitle("YAHALLO");
        setSize(300, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

/*
        try {
            Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Employee_Management", "postgres", "177013");
            String query = "SELECT * FROM usercredentials";
            try {PreparedStatement statement = connect.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery();
                 while (resultSet.next()){
                     int userid = resultSet.getInt("userid");
                      int employeeid = resultSet.getInt("employeeid");
                      String username = resultSet.getString("username");
                      String pass = resultSet.getString("password");
                      System.out.println(userid + " " + employeeid + " " + username + " " + pass);
                 }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                try {
                    // Close the connection when done
                    if (connect != null && !connect.isClosed()) {
                        connect.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        };
*/

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent d) {
                String username = Username.getText();
                String pass = Password.getText();
                ///JOptionPane.showMessageDialog(okButton,username + pass);
                /*
                if(username.equals("VLOD") && pass.equals("BOMBA")){
                   //l.setVisible(false);
                    dispose();
                    HomePage homa = new HomePage(username);
                    homa.setVisible(true);
                    homa.setTitle("2");
                }
                */

                try {
                        Connection connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Employee_Management", "postgres", "177013");
                    String query = "SELECT username, password FROM usercredentials where username = ?" ;
                    try {PreparedStatement statement = connect.prepareStatement(query);
                        statement.setString(1,username);
                        ResultSet resultSet = statement.executeQuery();
                        String usernameE="",passS="";
                        while (resultSet.next()){
                             usernameE = resultSet.getString("username");
                             passS = resultSet.getString("password");
                        ///System.out.println(usernameE);
                            //System.out.println(userid + " " + employeeid + " " + username + " " + pass);
                        }
                        if(username.equals (usernameE)){
                            if(pass.equals(passS)){
                                dispose();
                                HomePage homa = new HomePage(username);
                                homa.setVisible(true);
                                homa.setTitle("2");
                            }
                            else{
                                System.out.println("BAD_pass");
                            }
                        }else
                            System.out.println("BAD");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            // Close the connection when done
                            if (connect != null && !connect.isClosed()) {
                                connect.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                };


            }
        });
    }
}