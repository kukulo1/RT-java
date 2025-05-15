package taskNumberEight;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberEight {
    protected final static Scanner SCANNER = new Scanner(System.in);
    protected final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true";
    protected final static String DATABASE_LOGIN = "root";
    protected final static String DATABASE_PASSWORD = "root";
    protected final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK8 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(255), " +
            "age INT, " +
            "salary DOUBLE)";
    protected final static String INSERT_QUERY = "INSERT INTO TASK8 (name, age, salary) VALUES (?, ?, ?)";
    protected final static List<Worker> workers = new ArrayList<>();
    private final static String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS TASK8";
    protected static boolean tableCreated = false;

    public static void main(String[] args) {
        executeUpdate(DROP_TABLE_QUERY);
        int choice = 0;

        while (choice != -1) {
            printConsoleMenu();

            System.out.print("Ваш выбор: ");
            String input = SCANNER.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Неверный выбор. Повторите.");
                continue;
            }

            try {
                choice = Integer.parseInt(input);

                if (!tableCreated && choice > 2 && choice <= 5) {
                    System.out.println("Ошибка! Таблица еще не создана для выполнения операции. Сперва выполните пункт 2.");
                    continue;
                }

                doAction(choice);
            } catch (NumberFormatException e) {
                System.out.println("Неверный выбор. Повторите.");
            }
        }
    }


    static void printConsoleMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввод значений ВСЕХ полей, сохранить их в MySQL с выводом в консоль.");
        System.out.println("4. Сохранение всех результатов из MySQL с последующим выводом в консоль.");
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

    private static void doAction(int choice) {
        switch (choice) {
            case 1 -> {
                DisplayTables.execute();
            }
            case 2 -> {
                CreateTable.execute();
            }
            case 3 -> {
                AddWorker.execute();
            }
            case 4 -> {
                DisplayAllWorkers.execute();
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

}

