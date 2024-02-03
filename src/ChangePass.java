import javax.swing.*;
import java.awt.*;
import java.sql.*;


public class ChangePass extends JFrame {

    private JPanel panel1;
    private JTextField textField1;
    private JButton saveButton;
    private JButton backButton;
    private JButton okButton;
    public static void main(){
        new ChangePass("A");
    }
    public ChangePass(String username){
            initializeUI();
            Connection connect = establishConnection();
            int id_to_change_pass = getUserId(username, connect);

            backButton.addActionListener(e -> {
                dispose();
                new HomePage(username);
            });

            saveButton.addActionListener(e -> {
                String new_pass = textField1.getText();
                if(new_pass.equals("")){
                    JOptionPane.showMessageDialog(okButton,"invalid password");
                }
                else{
                    try {
                        change_pass(new_pass, connect, id_to_change_pass);
                    }catch(SQLException d){
                        d.printStackTrace();
                    }
                }
            });
    }

    private void change_pass(String new_pass, Connection connect, int id) throws SQLException {
            String query = "UPDATE usercredentials Set password = ? where userid = ?";
            try(PreparedStatement statement = connect.prepareStatement(query)){
                    statement.setString(1, new_pass);
                    statement.setInt(2, id);
                    statement.executeUpdate();
            }

    }
    private int getUserId(String username, Connection connect) {
        String query = "SELECT userid FROM usercredentials WHERE username = ?";
        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt("userid") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private Connection establishConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/Employee_Management", "postgres", "177013");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void initializeUI() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width / 2 - getWidth() / 2 - 100, size.height / 2 - getHeight() / 2 - 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        setSize(600, 700);
        setVisible(true);
        setTitle("BLANA");
    }


}
