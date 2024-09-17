package contentDealer;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost/Info";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "12345";

    public static Connection connection;

    private Database() {}

    public static Connection getConnection() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void insertQuery(Update update) {
        Date date = new Date();
        Long chatId = update.getMessage().getChatId();
        String query = update.getMessage().getText();
        String type;
        if (update.getMessage().getText().startsWith("/")) type = "Start"; else type = "Query";
        String sqlCommand = "INSERT INTO updates (chatid, type, output, date, status) " +
                "VALUES ('" + chatId + "', '" + type + "', '" + query + "', '" + date + "', '" + true + "');";
        go(sqlCommand);
    }

    public static void insertButton(Update update, int buttonNumber, String fileName, Long chatId, Boolean status) {

        Date date = new Date();
        String type = "Button: " + buttonNumber;
        String sqlCommand = "INSERT INTO updates (chatid, type, output, date) " +
                "VALUES ('" + chatId + "', '" + type + "', '" + fileName + "', '" + date + "', '" + status +"');";
        go(sqlCommand);
    }

    public static void go(String command) {
        Connection connection = Database.getConnection();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(command);
            System.out.println("statement done!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
