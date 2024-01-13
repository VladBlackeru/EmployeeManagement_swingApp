import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame{
    private JPanel panel1;
    private JTextField Username;
    private JButton okButton;
    private JPasswordField Password;

    public static void main(String[] args){

        Login l = new Login();


        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension size = toolkit.getScreenSize();
        l.setLocation(size.width / 2 - l.getWidth() / 2- 100, size.height / 2 - l.getHeight() / 2 - 200);


        l.setContentPane(l.panel1);
        l.setTitle("YAHALLO");
        l.setSize(300, 300);
        l.setVisible(true);
        l.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Login(){

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = Username.getText();
                String pass = Password.getText();
                ///JOptionPane.showMessageDialog(okButton,username + pass);
                if(username.equals("VLOD") && pass.equals("BOMBA")){
                   //l.setVisible(false);
                    dispose();
                    HomePage homa = new HomePage(username);
                    homa.setVisible(true);
                    homa.setTitle("2");
                }
            }
        });
    }


}