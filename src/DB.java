import javax.swing.plaf.basic.BasicButtonUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class DB {
    public static Connection connection;
    public static ResultSet resultSet;
    public static Statement statement;

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        String pathDB = "database.db";
        String connectionURL = "jdbc:sqlite:" + pathDB;
        connection = DriverManager.getConnection(connectionURL);
        return connection;
    }

    public static String getNick(String login, String password) throws SQLException {
        statement = connection.createStatement();
        //resultSet = statement.executeQuery("SELECT nickname FROM users WHERE login='" + login + "' AND password = '" + password +"'");
        PreparedStatement ps = connection.prepareStatement("SELECT nickname FROM users WHERE login=? AND password = ?");
        ps.setString(1, login);
        ps.setString(2, password);
        resultSet = ps.executeQuery();
        while (resultSet.next()){
            return resultSet.getString("nickname");
        }
        return null;
    }

    public static void changeNick(String oldNick,String newNick) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT nickname FROM users WHERE nickname=?");
        ps.setString(1, newNick);
        resultSet = ps.executeQuery();
        if (!resultSet.next()) {
            statement.execute("UPDATE users SET nickname = '" + newNick + "' WHERE nickname = '" + oldNick + "'");
            System.out.println("Ник успешно поменян");
        }else System.out.println("Этот ник уже занят");
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        String login, password, currentUser, newNick;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        getConnection();
        System.out.println("Введите ник (login1)");
        login = reader.readLine();
        System.out.println("введите пароль (pass1)");
        password = reader.readLine();
        currentUser = getNick(login, password);
        if (currentUser == null) {
            System.out.println("такого пользователя не найдено");
        } else {
            System.out.println("Вы авторизовались под ником " + currentUser);
            System.out.println("Введите новый ник");
            newNick = reader.readLine();
            changeNick(currentUser, newNick);

        }
        resultSet.close();
        statement.close();
        connection.close();
    }
}
