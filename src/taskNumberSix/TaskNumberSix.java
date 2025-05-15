package taskNumberSix;

import java.sql.*;
import java.util.Scanner;

public class TaskNumberSix {
    protected final static Scanner SCANNER = new Scanner(System.in);
    private final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true";
    private final static String DATABASE_LOGIN = "root";
    private final static String DATABASE_PASSWORD = "root";
    protected final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK6 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "matrix_name VARCHAR(255), " +
            "row_index INT, " +
            "col_index INT, " +
            "value DOUBLE)";
    protected final static String INSERT_QUERY = "INSERT INTO TASK6  (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
    protected static Matrix matrix;
    protected static boolean tableCreated = false;
    protected final static String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS TASK6";


    public static void main(String[] args) {
        executeUpdate(DROP_TABLE_QUERY);
        int choice = 0;

        while (choice != -1) {
            printConsoleMenu();

            try {
                if (!SCANNER.hasNextInt()) {
                    System.out.println("Неверный выбор. Повторите.");
                    SCANNER.next();
                    continue;
                }

                choice = SCANNER.nextInt();

                if (!tableCreated && choice > 2 && choice <= 5) {
                    System.out.println("Ошибка! Таблица еще не создана для выполнения операции. Сперва выполните пункт 2.");
                    continue;
                }

                doAction(choice);

            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }


    static Connection getConnection() {
        try {
            return DriverManager.getConnection(DATABASE_CONNECTION_URL, DATABASE_LOGIN, DATABASE_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при попытке подключения к БД: " + e.getMessage());
        }
        return null;
    }

    static void printConsoleMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввести две матрицы с клавиатуры и сохранить их в MySQL.");
        System.out.println("4. Перемножить матрицы, сохранить результат в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести в консоль.");
        System.out.println("-1. Закончить выполнение программы");
        System.out.print("Выберите действие: ");
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
                InputMatrices.execute();
            }
            case 4 -> {
                MultiplyMatrices.execute();
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
    static void insertMatrixIntoDB(int[][] matrixData, String name) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                executeUpdate(INSERT_QUERY, name, i, j, matrixData[i][j]);
            }
        }
    }
    static void printMatrix(int[][] matrixData, String name) {
        System.out.println(name + ": ");
        for (int[] row : matrixData) {
            for (int value : row) {
                System.out.printf("%4d ", value);
            }
            System.out.println();
        }
        System.out.println();
    }

    static void printTable() {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM TASK6")) {

            System.out.printf("%-3s | %-20s | %-10s | %-10s | %-50s%n",
                    "ID", "Matrix Name", "Row Index", "Col Index", "Value");
            System.out.println("-----------------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String matrixName = rs.getString("matrix_name");
                int rowIndex = rs.getInt("row_index");
                int colIndex = rs.getInt("col_index");
                double value = rs.getDouble("value");

                System.out.printf("%-3d | %-20s | %-10d | %-10d | %-50.2f%n",
                        id, matrixName, rowIndex, colIndex, value);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

