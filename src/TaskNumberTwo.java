import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberTwo {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true";
    public final static String DATABASE_LOGIN = "root";
    public final static String DATABASE_PASSWORD = "root";
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK2 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "string1 TEXT, " +
            "string2 TEXT, " +
            "result TEXT)";
    public final static String INSERT_QUERY = "INSERT INTO TASK2 (operation, string1, string2, result) VALUES (?, ?, ?, ?)";
    public static String string1, string2;
    public static boolean tableCreated = false;
    public static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS TASK2";


    public static void main(String[] args) {
        int choice = 0;
        executeUpdate(DROP_TABLE_QUERY);

        while (choice != -1) {
            printConsoleMenu();

            try {
                if (!SCANNER.hasNextInt()) {
                    System.out.println("Неверный выбор. Повторите.");
                    SCANNER.next();
                    continue;
                }

                choice = SCANNER.nextInt();

                if (!tableCreated && choice > 2 && choice <= 7) {
                    System.out.println("Ошибка! Таблица еще не создана для выполнения операции. Сперва выполните пункт 2.");
                    continue;
                }
                SCANNER.nextLine();
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
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Ввести две строки с клавиатуры, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("4. Подсчитать размер ранее введенных строк, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("5. Объединить две строки в единое целое, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("6. Сравнить две ранее введенные строки, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("7. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран.");
        System.out.println("-1. Закончить выполнение программы");
        System.out.print("Выберите действие: ");
    }

    static void doAction(int choice) {
        switch (choice) {
            case 1 -> {
                List<String> tables = new ArrayList<>();
                try (ResultSet resultSet = executeQuery("SHOW TABLES")) {
                    while (resultSet.next()) {
                        tables.add(resultSet.getString(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (tables.isEmpty()) {
                    System.out.println("Таблицы не найдены.");
                } else {
                    tables.forEach(System.out::println);
                }
            }
            case 2 -> {
                executeUpdate(CREATE_TABLE_QUERY);
                tableCreated = true;
                System.out.println("Таблица создана!");
            }
            case 3 -> {
                System.out.print("Введите первую строку (не менее 50 символов): ");
                string1 = scanString();
                System.out.print("Введите вторую строку (не менее 50 символов): ");
                string2 = scanString();
                executeUpdate(INSERT_QUERY, "input", string1, string2, null);
                System.out.println("Строки сохранены:");
                System.out.println("1: " + string1);
                System.out.println("2: " + string2);
            }
            case 4 -> {
                if (!stringsEntered()) break;
                int length1 = string1.length();
                int length2 = string2.length();
                String result = "Length1: " + length1 + ", Length2: " + length2;
                executeUpdate(INSERT_QUERY, "length", string1, string2, result);
                System.out.println("Длины строк: " + result);
            }
            case 5 -> {
                if (!stringsEntered()) break;
                String result = string1 + string2;
                System.out.println("Объединение строк: " + result);
                executeUpdate(INSERT_QUERY, "concatenation", string1, string2, result);
            }
            case 6 -> {
                if (!stringsEntered()) break;
                String result = string1.equals(string2) ? "equal" : "not equal";
                System.out.println("Сравнение: " + result);
                executeUpdate(INSERT_QUERY, "comparison", string1, string2, result);
            }
            case 7 -> {
                System.out.print("Введите название файла с расширением (.xls): ");
                String fileName = SCANNER.nextLine().trim();

                while (!fileName.toLowerCase().endsWith(".xls")) {
                    System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
                    fileName = SCANNER.nextLine().trim();
                }

                String filePath = "C:/Users/User/Desktop/" + fileName;

                String exportQuery =
                        "SELECT 'id', 'operation', 'string1', 'string2', 'result' " +
                                "UNION ALL " +
                                "SELECT id, operation, string1, string2, result FROM TASK2 " +
                                "INTO OUTFILE '" + filePath + "' " +
                                "CHARACTER SET cp1251";

                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(exportQuery)) {

                    stmt.executeQuery();
                    System.out.println("Данные успешно экспортированы в файл: " + filePath);

                    printTable();

                } catch (SQLException e) {
                    if (e.getMessage().contains("already exists")) {
                        System.out.println("Файл уже существует! Удалите его и попробуйте ещё раз.");
                    } else {
                        System.out.println("Ошибка при экспорте: " + e.getMessage());
                    }
                }
            }
            case -1 -> {
                System.out.println("Выход из программы...");
            }
            default -> {
                System.out.println("Неверный выбор. Повторите.");
            }
        }
    }
    static String scanString() {
        String input;
        do {
            input = SCANNER.nextLine();
            if (input.length() < 50) {
                System.out.println("Строка слишком короткая. Нужно не менее 50 символов.");
            }
        } while (input.length() < 50);
        return input;
    }

    static ResultSet executeQuery(String query, Object... params) {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_CONNECTION_URL, DATABASE_LOGIN, DATABASE_PASSWORD);
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
            Connection connection = DriverManager.getConnection(DATABASE_CONNECTION_URL, DATABASE_LOGIN, DATABASE_PASSWORD);
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
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM TASK2")) {

            System.out.printf("%-3s | %-20s | %-100s | %-100s | %-200s%n",
                    "ID", "Operation", "String1", "String2", "Result");
            System.out.println("----------------------------------------------------------------------------------------------------" +
                    "------------------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String operation = rs.getString("operation");
                String string1 = rs.getString("string1");
                String string2 = rs.getString("string2");
                String result = rs.getString("result");

                System.out.printf("%-3d | %-20s | %-100s | %-100s | %-200s%n",
                        id, operation, string1, string2, result);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static boolean stringsEntered() {
        if (string1 == null || string2 == null) {
            System.out.println("Сначала введите строки (пункт 3).");
            return false;
        }
        return true;
    }
}
