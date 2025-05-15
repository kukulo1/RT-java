package taskNumberSeven;

import java.sql.*;
import java.util.Scanner;

public class TaskNumberSeven {
    protected final static Scanner SCANNER = new Scanner(System.in);
    private final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true";
    private final static String DATABASE_LOGIN = "root";
    private final static String DATABASE_PASSWORD = "root";
    protected final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK7 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "array_name VARCHAR(255), " +
            "index_pos INT, " +
            "value INT)";
    protected final static String INSERT_QUERY = "INSERT INTO TASK7 (array_name, index_pos, value) VALUES (?, ?, ?)";
    public static Sort sort;
    protected static boolean tableCreated = false;
    protected final static String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS TASK7";


    public static void main(String[] args) {
        executeUpdate(DROP_TABLE_QUERY);
        int choice = 0;

        while (choice != -1) {
            printConsoleMenu();

            try {
                String input = SCANNER.next();

                if (!input.matches("-?\\d+")) {
                    System.out.println("Неверный выбор. Повторите.");
                    continue;
                }

                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Неверный выбор. Повторите.");
                    continue;
                }

                if (!tableCreated && choice > 2 && choice <= 5) {
                    System.out.println("Ошибка! Таблица еще не создана для выполнения операции. Сперва выполните пункт 2.");
                    continue;
                }

                doAction(choice);

            } catch (Exception e) {
                System.out.println("Неожиданная ошибка: " + e.getMessage());
            }
        }
    }


    static void printConsoleMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввести массив и сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Отсортировать массив, сохранить в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести их в консоль.");
        System.out.println("-1. Закончить выполнение программы");
        System.out.print("Выберите действие: ");
    }

    static Connection getConnection() {
        try {
            return DriverManager.getConnection(DATABASE_CONNECTION_URL, DATABASE_LOGIN, DATABASE_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при попытке подключения к БД: " + e.getMessage());
        }
        return null;
    }

    static void doAction(int choice) {
        switch (choice) {
            case 1 -> {
                DisplayTables.execute();
            }
            case 2 -> {
                CreateTable.execute();
            }
            case 3 -> {
                InputArray.execute();
            }
            case 4 -> {
                SortArray.execute();
            }
            case 5 -> {
                SQLToExcel.execute();
            }
            case -1 -> {
                System.out.println("Выход из программы...");
            }
            default -> {
                System.out.println("Неверный выбор. Повторите.");
            }
        }
    }
    static ResultSet executeQuery(String query, Object... params) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    static int executeUpdate(String query, Object... params) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    static void printTable() {
        String tablename = "TASK7";

        try (Connection con = getConnection();
             Statement stmt1 = con.createStatement();
             ResultSet rs = stmt1.executeQuery("SELECT * FROM " + tablename)) {

            System.out.printf("%-3s | %-20s | %-10s | %-100s%n", "ID", "Array Name", "Index Pos", "Value");
            System.out.println("----------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String arrayName = rs.getString("array_name");
                int indexPos = rs.getInt("index_pos");
                int value = rs.getInt("value");

                System.out.printf("%-3d | %-20s | %-10d | %-100d%n", id, arrayName, indexPos, value);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

