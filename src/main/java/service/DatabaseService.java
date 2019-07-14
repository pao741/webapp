package service;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseService {

    enum TestTableColumns {
        username, password;
        // todo: add name and last name
    }

    private final String jdbcDriverStr;
    private final String jdbcURL;

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;

    public DatabaseService() throws SQLException, ClassNotFoundException {
        this.jdbcDriverStr = "com.mysql.jdbc.Driver";
        this.jdbcURL = "jdbc:mysql://localhost/webapp_ooc?"
                + "user=webapp&password=ooc";
        Class.forName(jdbcDriverStr);
        connection = DriverManager.getConnection(jdbcURL);
        statement = connection.createStatement();
        createDatabase();
    }

    public void createDatabase() throws SQLException {
        statement.execute("create table if not exists user_table (username varchar(40) not null , password varchar(200) not null)");
        resultSet = statement.executeQuery("select * from webapp_ooc.user_table;");
        if (!resultSet.next()){
            String hased = BCrypt.hashpw("admin", BCrypt.gensalt());
            preparedStatement = connection.prepareStatement("insert into webapp_ooc.user_table values ('admin','"+ hased +"')");
            preparedStatement.execute();
        }
    }

    public void readData() throws Exception {
        try {
            resultSet = statement.executeQuery("select * from webapp_ooc.user_table;");
            getResultSet(resultSet);
        } finally {
            close();
        }
    }

    private void getResultSet(ResultSet resultSet) throws Exception {
        while (resultSet.next()) {
//            Integer id = resultSet.getInt(TestTableColumns.id.toString());
            String username = resultSet.getString(TestTableColumns.username.toString());
            String password = resultSet.getString(TestTableColumns.password.toString());
            System.out.println("username: " + username);
            System.out.println("password: " + password);
        }
    }

    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
        }
    }

    public boolean createUser(String username, String password) throws SQLException{
        String hashed = BCrypt.hashpw(password,BCrypt.gensalt());
        if (!containUser(username)){
            statement.execute("insert into user_table(username, password) values ( '" + username + "', '" + hashed + "')");
            System.out.println("user:" + username + " had been added");
            return true;
        }
        System.out.println("user:" + username + " is already existed");
        return false;
    }
    public boolean containUser(String username) throws  SQLException{
        ArrayList<String> nameList = getAllUser();
        if (nameList.contains(username)){
            return true;
        }
        return false;
    }

    public boolean delUser(String username) throws SQLException{
        System.out.println(containUser(username));
        if (containUser(username)) {
            statement.execute("delete from user_table where username ='" + username + "';");
            return true;
        }
        return false;
    }

    public boolean authenticate(String username, String password) throws SQLException {
        if (containUser(username)) {
            resultSet = statement.executeQuery("select * from user_table where(username = '" + username +"')");
            if (resultSet.next()) {
                if (BCrypt.checkpw(password, resultSet.getString(TestTableColumns.password.toString()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<String> getAllUser() throws SQLException {
        resultSet = statement.executeQuery("select  * from user_table");
        ArrayList<String> nameList = new ArrayList<>();
        while (resultSet.next()){
            nameList.add(resultSet.getString(TestTableColumns.username.toString()));
        }
        return nameList;
    }
}
