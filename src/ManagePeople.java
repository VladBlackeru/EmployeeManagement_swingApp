import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ManagePeople extends JFrame {
    private JButton okButton;
    private JPanel panel1;
    private JTextField textField1;
    private JComboBox comboBox1;
    private JButton searchButton;
    private JTable table1;
    private JButton backButton;
    private JButton addButton;
    private JButton saveButton;
    private JButton delButton;
    private Object[][] originalTableData;
    public static void main(String[] args) {
        new ManagePeople("jane_smith");
    }

    public ManagePeople(String username) {
        initializeUI();
        Connection connect = establishConnection();
        int id = getUserId(username, connect);
        String role = getUserRole(username, connect);

        Object[][] tableContent;
        int enable = (role.equals("Admin")) ? 1 : 0;
        if(enable == 0) {
            tableContent = fetchNewData(connect, id);
        }else{
            tableContent = fetchData(connect);
        }
        originalTableData = new Object[tableContent.length][];
        System.arraycopy(tableContent, 0, originalTableData, 0, tableContent.length);

        int employ = (role.equals("Employee")) ? 1:0;
        int mang = (role.equals("Manager")) ? 1 : 0;
        DefaultTableModel model = createTableModel(tableContent, mang);
        table1.setModel(model);

        createCombo();

        searchButton.addActionListener(e -> {
            String text = textField1.getText();
            String selectedValue = (String) comboBox1.getSelectedItem();
            searchTable(selectedValue, text, enable, model);
            if (text.equals("")) {
                createTable(model, originalTableData);
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new HomePage(username);
        });

        addButton.addActionListener(e -> {
            if(employ != 1) {
                addNewRow(connect, id);
                update(username, model, connect);
                int rows = (int) table1.getRowCount();
                int new_id = (int) (table1.getValueAt(rows-1, 11));
                //System.out.println(new_id);
                String usernamn = find_new_username(connect);
                System.out.println(usernamn);
                addUserCred(connect,new_id, usernamn);
            }
        });
        saveButton.addActionListener(e->{
            try {
                save(connect);
            }catch (SQLException d){
                d.printStackTrace();
            }
        });

        delButton.addActionListener(e -> {
            if(employ != 1) {
                int selectedRow = table1.getSelectedRow();

                if (selectedRow != -1) {
                    int leaveId = (Integer) table1.getValueAt(selectedRow, 11); // Assuming leaveid is represented as a string in the JTable
                    deleteRow(leaveId, connect);
                    update(username, model, connect); // Refresh the table after deletion
                } else {
                    JOptionPane.showMessageDialog(delButton, "Please select a row to delete.");
                }
            }
        });

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

    private Connection establishConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/Employee_Management", "postgres", "177013");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getUserId(String username, Connection connect) {
        String query = "SELECT employeeid FROM usercredentials WHERE username = ?";
        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt("employeeid") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String getUserRole(String username, Connection connect) {
        String query = "SELECT role FROM usercredentials WHERE username = ?";
        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getString("role") : "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Object[][] fetchData(Connection connect) {
        String query = "SELECT firstname,lastname,dateofbirth,gender,contactphone,contactemail,address,joiningdate,position,salary,managerid,employeeid from employee";
        try {
            PreparedStatement statement = connect.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet resultSet = statement.executeQuery();

            resultSet.last();
            int rowCount = resultSet.getRow();
            resultSet.beforeFirst();

            Object[][] tableContent = new Object[rowCount][12];

            int rowIndex = 0;
            while (resultSet.next()) {
                tableContent[rowIndex][0] = resultSet.getString("firstname");
                tableContent[rowIndex][1] = resultSet.getString("lastname");
                tableContent[rowIndex][2] = resultSet.getDate("dateofbirth");
                tableContent[rowIndex][3] = resultSet.getString("gender");
                tableContent[rowIndex][4] = resultSet.getString("contactphone");
                tableContent[rowIndex][5] = resultSet.getString("contactemail");
                tableContent[rowIndex][6] = resultSet.getString("address");
                tableContent[rowIndex][7] = resultSet.getDate("joiningdate");
                tableContent[rowIndex][8] = resultSet.getString("position");
                tableContent[rowIndex][9] = resultSet.getInt("salary");
                tableContent[rowIndex][10] = resultSet.getInt("managerid");
                tableContent[rowIndex][11] = resultSet.getInt("employeeid");

                rowIndex++;
            }

            return tableContent;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String find_new_username(Connection connect){
        int i = 1, k = 0;
        String username="";
        while(k == 0){
            String s = "Username" + i;
            if(check(connect, s)){
                k =1 ;
                username = s;
            }
            i++;
        }
        return username;
    }

    private boolean check(Connection connection, String username){
        try {
            String query = "Select userid from usercredentials where username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            int k = 0;
            while(resultSet.next())
                k++;
            if(k == 0)
                return true;
            return false;
        }catch(SQLException d){
            d.printStackTrace();
        }
        return false;
    }

    private Object[][] fetchNewData(Connection connect, int id) {
        String query =
                "SELECT d.firstname,d.lastname,d.dateofbirth,d.gender,d.contactphone,d.contactemail,d.address,d.joiningdate,d.position,d.salary,d.managerid,d.employeeid from employee d\n" +
                "join employee  E on d.managerid = E.employeeid\n" +
                "where E.employeeid = ?;";
        return fetchTableData(connect, query, id);
    }

    private Object[][] fetchTableData(Connection connect, String query, int id) {
        try {
            PreparedStatement statement = connect.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            resultSet.last();
            int rowCount = resultSet.getRow();
            resultSet.beforeFirst();

            Object[][] tableContent = new Object[rowCount][12];

            int rowIndex = 0;
            while (resultSet.next()) {
                tableContent[rowIndex][0] = resultSet.getString("firstname");
                tableContent[rowIndex][1] = resultSet.getString("lastname");
                tableContent[rowIndex][2] = resultSet.getDate("dateofbirth");
                tableContent[rowIndex][3] = resultSet.getString("gender");
                tableContent[rowIndex][4] = resultSet.getString("contactphone");
                tableContent[rowIndex][5] = resultSet.getString("contactemail");
                tableContent[rowIndex][6] = resultSet.getString("address");
                tableContent[rowIndex][7] = resultSet.getDate("joiningdate");
                tableContent[rowIndex][8] = resultSet.getString("position");
                tableContent[rowIndex][9] = resultSet.getInt("salary");
                tableContent[rowIndex][10] = resultSet.getInt("managerid");
                tableContent[rowIndex][11] = resultSet.getInt("employeeid");

                rowIndex++;
            }

            return tableContent;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private DefaultTableModel createTableModel(Object[][] tableContent, int enable) {

        return new DefaultTableModel(tableContent, new String[]{"first name", "last name", "dateofbirth","gender","contactphone","contactemail","address","joiningdate","position","salary","managerid","employeeid"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if(enable == 1){
                    return column != 10 && column != 11;
                }
                return  column != 11;
            }
        };
    }

    private void createCombo() {
        comboBox1.setModel(new DefaultComboBoxModel(new String[]{"first name", "last name", "dateofbirth","gender","contactphone","contactemail","address","joiningdate","position","salary"}));
    }

    private void searchTable(String selectedValue, String text, int enable, DefaultTableModel model) {
        Object[][] table2 = new Object[model.getRowCount()][model.getColumnCount()];
        int rowCount = 0;

        Object[][] searchData = (text.equals("")) ? originalTableData : modelTo2DArray(model);

        for (int i = 0; i < model.getRowCount(); i++) {
            if (text.equals(model.getValueAt(i, getColumnIndex(selectedValue)).toString())) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    table2[rowCount][j] = model.getValueAt(i, j);
                }
                rowCount++;
            }
        }

        Object[][] result = new Object[rowCount][model.getColumnCount()];
        System.arraycopy(table2, 0, result, 0, rowCount);
        createTable(model, result);
    }


    private int getColumnIndex(String columnName) {
        switch (columnName) {
            case "first name":
                return 0;
            case "last name":
                return 1;
            case "dateofbirth":
                return 2;
            case "gender":
                return 3;
            case "contactphone":
                return 4;
            case "contactemail":
                return 5;
            case "address":
                return 6;
            case "joiningdate":
                return 7;
            case "position":
                return 8;
            case "salary":
                return 9;
            default:
                return -1;
        }

    }

    private void createTable(DefaultTableModel model, Object[][] data) {
        model.setDataVector(data, new String[]{"first name", "last name", "dateofbirth","gender","contactphone","contactemail","address","joiningdate","position","salary","managerid","employeeid"});
    }

    private void addNewRow(Connection connect, int id) {
        String query = "INSERT INTO employee (departmentid,firstname, lastname, dateofbirth, gender, contactphone, contactemail, address, joiningdate, position, salary, managerid) " +
                                            "VALUES ('1','First Name','Last Name','2022-02-22', 'Male','0743', 'email@mail.com','Norway','2022-02-02','Trainee','40000',?)";
        try {
            ///System.out.println(id);
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addUserCred(Connection connect, int id,String username){
        String query = "INSERT INTO usercredentials(employeeid, username, password, role) VALUES (?,?,'NEW','TRAINEE')";
        try {
            ///System.out.println(id);
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(String username, DefaultTableModel model, Connection connect) {
        int id = getUserId(username, connect);
        String role = getUserRole(username, connect);

        Object[][] tableContent = fetchNewData(connect, id);
       // Object[][] newTableContent = fetchNewData(connect, id);

        /*int l1 = newTableContent.length, l2 = tableContent.length;
        Object[][] tableFinal = new Object[l1 + l2][];
        System.arraycopy(tableContent, 0, tableFinal, 0, l2);
        System.arraycopy(newTableContent, 0, tableFinal, l2, l1);
*/
        model.setDataVector(tableContent, new String[]{"first name", "last name", "dateofbirth","gender","contactphone","contactemail","address","joiningdate","position","salary","managerid","employeeid"});
        ///updateDatabase(tableContent, connect, username);
    }

    private Object[][] modelTo2DArray(DefaultTableModel model) {
        Object[][] array = new Object[model.getRowCount()][model.getColumnCount()];
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                array[i][j] = model.getValueAt(i, j);
            }
        }
        return array;
    }

    private void updateDatabase(Object[][] tableData, Connection connect, String username) {
        String updateQuery = "UPDATE employee SET firstname=?, lastname=?, dateofbirth=?, gender=?, contactphone=?, contactemail=?, address=?, joiningdate=?, position=?, salary=?, managerid=? WHERE employeeid=?";

        try {
            PreparedStatement updateStatement = connect.prepareStatement(updateQuery);
            //int i = 1;
            for (Object[] rowData : tableData) {
                updateStatement.setString(1,(String) rowData[2]); // assuming startdate is at index 2
                updateStatement.setString(2,(String)  rowData[3]); // assuming enddate is at index 3
                updateStatement.setDate(3,(Date)  rowData[4]); // assuming leavetype is at index 4
                updateStatement.setString(4,(String)  rowData[5]); // assuming status is at index 5
                updateStatement.setString(5,(String)  rowData[6]);
                updateStatement.setString(6,(String)  rowData[7]);
                updateStatement.setString(7,(String)  rowData[8]);
                updateStatement.setDate(8,(Date)  rowData[9]);
                updateStatement.setString(9,(String)  rowData[10]);
                updateStatement.setString(10,(String)  rowData[11]);
                updateStatement.setString(11,(String)  rowData[12]);
                updateStatement.setInt(12,(int)  rowData[13]);

                // i++;
                updateStatement.addBatch();
            }

            // Execute the batch update
            updateStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void save(Connection connect) throws SQLException {

        String sql = "UPDATE employee SET firstname =?, lastname=?, dateofbirth=?, gender=?, contactphone=?, contactemail=?, address=?, joiningdate=?, position=?, salary=?, managerid=? WHERE employeeid=?";

        try (PreparedStatement updateStatement = connect.prepareStatement(sql)) {
            connect.setAutoCommit(false);
            System.out.println(table1.getColumnCount());
            for (int i = 0; i < table1.getRowCount(); i++) {
                String d1 =table1.getValueAt(i, 2).toString();
                String d2 = table1.getValueAt(i, 7).toString();


                Date date1 = convertStringToSqlDate(d1,"yyyy-MM-dd");
                Date date2 = convertStringToSqlDate(d2, "yyyy-MM-dd");
                String da =   table1.getValueAt(i,9).toString();
                String manger = table1.getValueAt(i,10).toString();
                int da1 = Integer.parseInt(da);
                int manger1 = Integer.parseInt(manger);
                updateStatement.setString(1,(String) table1.getValueAt(i,0)); // assuming startdate is at index 2
                updateStatement.setString(2,(String)  table1.getValueAt(i,1)); // assuming enddate is at index 3
                updateStatement.setDate(3,(Date)  date1); // assuming leavetype is at index 4
                updateStatement.setString(4,(String)  table1.getValueAt(i,3)); // assuming status is at index 5
                updateStatement.setString(5,(String)  table1.getValueAt(i,4));
                updateStatement.setString(6,(String)  table1.getValueAt(i,5));
                updateStatement.setString(7,(String)  table1.getValueAt(i,6));
                updateStatement.setDate(8,(Date)  date2);
                updateStatement.setString(9,(String)  table1.getValueAt(i,8));
                updateStatement.setInt(10,da1);
                updateStatement.setInt(11,manger1);
                updateStatement.setInt(12,(int) table1.getValueAt(i, 11));
                updateStatement.addBatch();
            }

            updateStatement.executeBatch();
            connect.commit();
            connect.setAutoCommit(true);

            JOptionPane.showMessageDialog(okButton, "Changes saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            connect.rollback(); // Rollback changes if an exception occurs
            JOptionPane.showMessageDialog(okButton, "Error saving changes to the database.");
            throw e; // Re-throw the exception to signal the failure
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static Date convertStringToSqlDate(String dateString, String dateFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        java.util.Date utilDate = sdf.parse(dateString);
        return new Date(utilDate.getTime());
    }
    private void deleteRow(int leaveId, Connection connect) {
        String deleteQuery = "DELETE FROM employee WHERE employeeid = ?";

        try {
            PreparedStatement deleteStatement = connect.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, leaveId);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(delButton, "Error deleting the row from the database.");
        }
    }
}



