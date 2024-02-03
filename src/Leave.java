import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Leave extends JFrame {
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
        new Leave("jane_smith");
    }

    public Leave(String username) {
        initializeUI();
        Connection connect = establishConnection();
        int id = getUserId(username, connect);
        String role = getUserRole(username, connect);


        Object[][] tableContent = fetchData(connect, id);
        Object[][] newTableContent = fetchNewData(connect, id);

        int l1 = newTableContent.length, l2 = tableContent.length;
        originalTableData = new Object[l1 + l2][];
        System.arraycopy(tableContent, 0, originalTableData, 0, l2);
        System.arraycopy(newTableContent, 0, originalTableData, l2, l1);


        int enable = (role.equals("Employee")) ? 5 : 0;

        DefaultTableModel model = createTableModel(tableContent, newTableContent, enable);
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
            addNewRow(connect, id);
            update(username, model, connect);
        });
        saveButton.addActionListener(e->{
            try {
                save(connect);
            }catch (SQLException d){
                d.printStackTrace();
            }
        });

        delButton.addActionListener(e -> {
            int selectedRow = table1.getSelectedRow();

            if (selectedRow != -1) {
                int leaveId = (Integer) table1.getValueAt(selectedRow, 6); // Assuming leaveid is represented as a string in the JTable
                deleteRow(leaveId, connect);
                update(username, model, connect); // Refresh the table after deletion
            } else {
                JOptionPane.showMessageDialog(delButton, "Please select a row to delete.");
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

    private Object[][] fetchData(Connection connect, int id) {
        String query = "SELECT  firstname, lastname, startdate, enddate, leavetype,leaveid, status FROM leave " +
                "JOIN employee ON leave.employeeid = employee.employeeid " +
                "WHERE leave.employeeid = ?";
        return fetchTableData(connect, query, id);
    }

    private Object[][] fetchNewData(Connection connect, int id) {
        String query = "SELECT E.FirstName, E.LastName, L.StartDate, L.EndDate, L.LeaveType, L.Status, L.leaveid FROM Leave L " +
                "JOIN Employee E ON L.EmployeeID = E.EmployeeID " +
                "JOIN Employee m ON e.ManagerID = m.EmployeeID " +
                "WHERE m.EmployeeID = ?";
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

            Object[][] tableContent = new Object[rowCount][7];

            int rowIndex = 0;
            while (resultSet.next()) {
                tableContent[rowIndex][0] = resultSet.getString("firstname");
                tableContent[rowIndex][1] = resultSet.getString("lastname");
                tableContent[rowIndex][2] = resultSet.getDate("startdate");
                tableContent[rowIndex][3] = resultSet.getDate("enddate");
                tableContent[rowIndex][4] = resultSet.getString("leavetype");
                tableContent[rowIndex][5] = resultSet.getString("status");
                tableContent[rowIndex][6] = resultSet.getInt("leaveid");
                rowIndex++;
            }

            return tableContent;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private DefaultTableModel createTableModel(Object[][] tableContent, Object[][] newTableContent, int enable) {
        int l1 = newTableContent.length, l2 = tableContent.length;
        Object[][] tableFinal = new Object[l1 + l2][];
        System.arraycopy(tableContent, 0, tableFinal, 0, l2);
        System.arraycopy(newTableContent, 0, tableFinal, l2, l1);

        return new DefaultTableModel(tableFinal, new String[]{"first name", "last name", "start date", "end date", "leave type", "status","leaveid"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 1 && column != 6 && column != enable;
            }
        };
    }

    private void createCombo() {
        comboBox1.setModel(new DefaultComboBoxModel(new String[]{"first name", "last name", "start date", "end date", "leave type", "status"}));
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
            case "start date":
                return 2;
            case "end date":
                return 3;
            case "leave type":
                return 4;
            case "status":
                return 5;
            default:
                return -1;
        }
    }

    private void createTable(DefaultTableModel model, Object[][] data) {
        model.setDataVector(data, new String[]{"first name", "last name", "start date", "end date", "leave type", "status"});
    }

    private void addNewRow(Connection connect, int id) {
        String query = "INSERT INTO leave (employeeid, startdate, enddate, leavetype, status) VALUES (?,'2022-02-22', '2022-02-23','leave_type', 'Pending')";
        try {
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(String username, DefaultTableModel model, Connection connect) {
        int id = getUserId(username, connect);
        String role = getUserRole(username, connect);

        Object[][] tableContent = fetchData(connect, id);
        Object[][] newTableContent = fetchNewData(connect, id);

        int l1 = newTableContent.length, l2 = tableContent.length;
        Object[][] tableFinal = new Object[l1 + l2][];
        System.arraycopy(tableContent, 0, tableFinal, 0, l2);
        System.arraycopy(newTableContent, 0, tableFinal, l2, l1);

        model.setDataVector(tableFinal, new String[]{"first name", "last name", "start date", "end date", "leave type", "status","LEAVEID"});
        updateDatabase(tableFinal, connect, username);
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
        String updateQuery = "UPDATE leave SET startdate=?, enddate=?, leavetype=?, status=? WHERE leaveid=?";

        try {
            PreparedStatement updateStatement = connect.prepareStatement(updateQuery);
            //int i = 1;
            for (Object[] rowData : tableData) {
                updateStatement.setDate(1, (Date) rowData[2]); // assuming startdate is at index 2
                updateStatement.setDate(2, (Date) rowData[3]); // assuming enddate is at index 3
                updateStatement.setString(3, (String) rowData[4]); // assuming leavetype is at index 4
                updateStatement.setString(4, (String) rowData[5]); // assuming status is at index 5
                updateStatement.setInt(5, (Integer) rowData[6]);
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

        String sql = "INSERT INTO leave (leaveid, startdate, enddate, leavetype, status)\n" +
                "VALUES (?,?,?,?,?)\n" +
                "ON CONFLICT (leaveid) DO UPDATE\n" +
                "SET startdate = EXCLUDED.startdate,\n" +
                "    enddate = EXCLUDED.enddate,\n" +
                "    leavetype = EXCLUDED.leavetype,\n" +
                "    status = EXCLUDED.status;";

        try (PreparedStatement statement = connect.prepareStatement(sql)) {
            connect.setAutoCommit(false);
            System.out.println(table1.getColumnCount());
            for (int i = 0; i < table1.getRowCount(); i++) {
                String d1 =table1.getValueAt(i, 2).toString();
                String d2 = table1.getValueAt(i, 3).toString();


                Date date1 = convertStringToSqlDate(d1,"yyyy-MM-dd");
                Date date2 = convertStringToSqlDate(d2, "yyyy-MM-dd");

                 statement.setInt(1, (Integer) table1.getValueAt(i, 6));  // Assuming leaveid is represented as a string in the JTable
                statement.setDate(2, date1);  // Assuming startdate is represented as a string in the JTable
                statement.setDate(3, date2);  // Assuming enddate is represented as a string in the JTable
                statement.setString(4, (String) table1.getValueAt(i, 4));
                statement.setString(5, (String) table1.getValueAt(i, 5));
                statement.addBatch();
            }

            statement.executeBatch();
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
        String deleteQuery = "DELETE FROM leave WHERE leaveid = ?";

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



