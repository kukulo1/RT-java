import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberFour {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true";
    public final static String DATABASE_LOGIN = "root";
    public final static String DATABASE_PASSWORD = "root";
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK4 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "string1 TEXT, " +
            "string2 TEXT, " +
            "result TEXT)";
    public final static String INSERT_QUERY = "INSERT INTO TASK4 (operation, string1, string2, result) VALUES (?, ?, ?, ?)";
    public static String string1, string2;
    public static boolean tableCreated = false;
    public static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS TASK4";


    public static void main(String[] args) {
        int choice = 0;
        executeUpdate(DROP_TABLE_QUERY);

        System.out.print("Введите первую строку (не менее 50 символов): ");
        string1 = scanString();
        System.out.print("Введите вторую строку (не менее 50 символов): ");
        string2 = scanString();

        while (choice != -1) {
            printConsoleMenu();

            try {
                if (!SCANNER.hasNextInt()) {
                    System.out.println("Неверный выбор. Повторите.");
                    SCANNER.next();
                    continue;
                }

                choice = SCANNER.nextInt();

                if (!tableCreated && choice > 2 && choice <= 6) {
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
        System.out.println("1. Вывести все таблицы из MySQL");
        System.out.println("2. Создать таблицу в MySQL");
        System.out.println("3. Возвращение подстроки по индексам, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("4. Перевод строк в верхний и нижний регистры, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("5. Поиск подстроки и определение окончания подстроки, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("6. Сохранить все данные из MySQL в Excel и вывести на экран");
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
                System.out.println("Из какой строки извлекать подстроку: 1 или 2?");
                int selected = readSafeIndex("Выберите номер строки (1 или 2): ", 1, 2);
                String selectedInput = (selected == 1) ? string1 : string2;
                int start = readSafeIndex("Введите начальный индекс подстроки: ", 0, selectedInput.length());
                int end = readSafeIndex("Введите конечный индекс подстроки: ", start, selectedInput.length());

                String sub = getSubstring(selectedInput, start, end);

                String result = sub;
                System.out.println(result);

                executeUpdate(INSERT_QUERY, "substring", string1, string1, result);
            }
            case 4 -> {
                String result = "UPPERCASE1: " + string1.toUpperCase() + ", LOWERCASE1: " + string1.toLowerCase() +
                        ", UPPERCASE2: " + string2.toUpperCase() + ", LOWERCASE2: " + string2.toLowerCase();
                System.out.println(result);
                executeUpdate(INSERT_QUERY, "case_change", string1, string2, result);
            }
            case 5 -> {
                System.out.print("Введите подстроку для поиска: ");
                String substr = SCANNER.nextLine();

                boolean contains1 = string1.contains(substr);
                boolean ends1 = string1.endsWith(substr);
                boolean contains2 = string2.contains(substr);
                boolean ends2 = string2.endsWith(substr);

                String result = String.format(
                        "for substring: %s / string1: contains=%b, endsWith=%b / string2: contains=%b, endsWith=%b",
                        substr, contains1, ends1, contains2, ends2
                );
                System.out.println(result);
                executeUpdate(INSERT_QUERY,"contains_and_ends", string1, string2, result);
            }
            case 6 -> {
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

    static int readSafeIndex(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine();

            if (!input.matches("\\d+")) {
                System.out.println("Ошибка: введите неотрицательное целое число.");
                continue;
            }

            try {
                int value = Integer.parseInt(input);

                if (value < min || value > max) {
                    System.out.printf("Ошибка: индекс должен быть в диапазоне от %d до %d.%n", min, max);
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: число выходит за пределы допустимого диапазона int.");
            }
        }
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM TASK4")) {

            System.out.printf("%-3s | %-20s | %-100s | %-100s | %-200s%n",
                    "ID", "Operation", "String1", "String2", "Result");
            System.out.println("--------------------------------------------------------------------------------------------------------" +
                    "------------------------------------------------------------------------");

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
    static String getSubstring(String input, int start, int end) {
        if (start >= 0 && end <= input.length() && start < end) {
            return input.substring(start, end);
        } else {
            System.out.println("Недопустимые индексы для подстроки в строке длиной " + input.length());
            return "";
        }
    }
}
