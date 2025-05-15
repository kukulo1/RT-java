import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberFive {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true";
    public final static String DATABASE_LOGIN = "root";
    public final static String DATABASE_PASSWORD = "root";
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK5 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "string1 TEXT, " +
            "string2 TEXT, " +
            "result TEXT)";
    public final static String INSERT_QUERY = "INSERT INTO TASK5 (operation, string1, string2, result) VALUES (?, ?, ?, ?)";
    public static StringBuffer string1, string2;
    public static boolean tableCreated = false;
    public final static String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS TASK5";


    public static void main(String[] args) {
        executeUpdate(DROP_TABLE_QUERY);
        int choice = 0;

        System.out.print("Введите первую строку (не менее 50 символов): ");
        string1 = new StringBuffer(scanString());
        System.out.print("Введите вторую строку (не менее 50 символов): ");
        string2 = new StringBuffer(scanString());

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
        System.out.println("1. Вывести все таблицы из MySQL");
        System.out.println("2. Создать таблицу в MySQL");
        System.out.println("3. Изменить порядок символов строк на обратный, сохранить в MySQL и вывести в консоль");
        System.out.println("4. Добавить одну строку в другую, сохранить в MySQL и вывести в консоль");
        System.out.println("5. Сохранить все данные из MySQL в Excel и вывести на экран");
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
                String reversed1 = new StringBuffer(string1).reverse().toString();
                String reversed2 = new StringBuffer(string2).reverse().toString();

                System.out.println("Реверс первой строки: " + reversed1);
                System.out.println("Реверс второй строки: " + reversed2);

                String result = "reversed1: " + reversed1 + "; reversed2: " + reversed2;


                executeUpdate(INSERT_QUERY, "reversed", string1.toString(), string2.toString(), result);
            }
            case 4 -> {
                String beforeReverse = string1.toString();
                string1.append(string2);
                String result = "string1 + string2: " + string1;

                System.out.println(result);
                executeUpdate(INSERT_QUERY, "append", beforeReverse, string2.toString(), result);
            }
            case 5 -> {
                System.out.print("Введите название файла с расширением (.xls): ");
                SCANNER.nextLine();
                String fileName = SCANNER.nextLine().trim();

                while (!fileName.toLowerCase().endsWith(".xls")) {
                    System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
                    fileName = SCANNER.nextLine().trim();
                }

                String filePath = "C:/Users/User/Desktop/" + fileName;

                String exportQuery =
                        "SELECT 'id', 'operation', 'string1', 'string2', 'result' " +
                                "UNION ALL " +
                                "SELECT id, operation, string1, string2, result FROM TASK5 " +
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
        String tablename = "TASK5";

        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename)) {

            System.out.printf("%-3s | %-20s | %-100s | %-100s | %-200s%n",
                    "ID", "Operation", "string1", "string2", "result");
            System.out.println("-----------------------------------------------------------------------------------------------------" +
                    "------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String operation = rs.getString("operation");
                String value1 = rs.getString("string1");
                String value2 = rs.getString("string2");
                String result = rs.getString("result");

                System.out.printf("%-3d | %-20s | %-100s | %-100s | %-200s%n",
                        id, operation, value1, value2, result);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
