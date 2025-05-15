import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberThree {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true";
    public final static String DATABASE_LOGIN = "root";
    public final static String DATABASE_PASSWORD = "root";
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK3 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "number INT, " +
            "result TEXT)";
    public final static String INSERT_QUERY = "INSERT INTO TASK3 (operation, number, result) VALUES (?, ?, ?)";
    public static boolean tableCreated = false;
    public static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS TASK3";


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

                if (!tableCreated && choice > 2 && choice <= 4) {
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

    static void printConsoleMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Проверить числа на целочисленность и чётность, результат сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Сохранить все данные из MySQL в Excel и вывести на экран.");
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
                System.out.println("Введите числа через пробел (для завершения введите 'q'):");
                while (true) {
                    String input = SCANNER.next();
                    if (input.equalsIgnoreCase("q")) {
                        break;
                    }
                    try {
                        double number = Double.parseDouble(input);
                        if (number % 1 != 0) {
                            System.out.println("Ошибка! " + number + " — нецелое число");
                            continue;
                        }
                        if (number > Integer.MAX_VALUE || number < Integer.MIN_VALUE) {
                            System.out.println("Ошибка: число выходит за пределы int.");
                            continue;
                        }
                        int intValue = (int) number;
                        boolean even = intValue % 2 == 0;
                        String result = even ? "Even" : "Odd";
                        System.out.println(intValue + " — целое " + (even ? "четное" : "нечетное") + " число");
                        executeUpdate(INSERT_QUERY, "input", intValue, result);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: '" + input + "' не является числом");
                    }
                }
            }
            case 4 -> {
                System.out.print("Введите название файла с расширением (.xls): ");
                String fileName = SCANNER.nextLine().trim();

                while (!fileName.toLowerCase().endsWith(".xls")) {
                    System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
                    fileName = SCANNER.nextLine().trim();
                }

                String filePath = "C:/Users/User/Desktop/" + fileName;

                String exportQuery =
                        "SELECT 'id', 'operation', 'number', 'result' " +
                                "UNION ALL " +
                                "SELECT id, operation, number, result FROM TASK3 " +
                                "INTO OUTFILE '" + filePath + "' " +
                                "CHARACTER SET cp1251";

                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(exportQuery)) {

                    stmt.executeQuery();
                    System.out.println("Данные успешно экспортированы в файл: " + filePath);

                    printTable(); // метод для вывода таблицы

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
    static double scanDouble() {
        while (!SCANNER.hasNextDouble()) {
            System.out.println("Ошибка ввода. Введите число!");
            SCANNER.next();
        }
        return SCANNER.nextDouble();
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM TASK3")) {

            System.out.printf("%-3s | %-20s | %-10s | %-20s%n",
                    "ID", "Operation", "Number", "Result");
            System.out.println("------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String operation = rs.getString("operation");
                int number = rs.getInt("number");
                String result = rs.getString("result");

                System.out.printf("%-3d | %-20s | %-10d | %-20s%n",
                        id, operation, number, result);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
