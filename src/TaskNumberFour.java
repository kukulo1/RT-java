import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberFour {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true"; //тут не трогай, это ссылка для подключения к БД; ?createDatabaseIfNotExist=true надо для автоматического создания БД, если ее еще нету
    public final static String DATABASE_LOGIN = "root"; //сюда логин
    public final static String DATABASE_PASSWORD = "root"; //сюда свой пароль суй
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK4 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "string1 TEXT, " +
            "string2 TEXT, " +
            "result TEXT)";
    public final static String INSERT_QUERY = "INSERT INTO TASK4 (operation, string1, string2, result) VALUES (?, ?, ?, ?)";
    public static String string1, string2;


    public static void main(String[] args) {
        int choice = 0;

        System.out.print("Введите первую строку (не менее 50 символов): ");
        string1 = scanString();
        System.out.print("Введите вторую строку (не менее 50 символов): ");
        string2 = scanString();
        
        while (choice != -1) {
            printConsoleMenu();
            try {
                choice = SCANNER.nextInt();
                doAction(choice);
            } catch (NumberFormatException e) {
                System.out.println("Введите номер действия!");
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
                System.out.println("Таблица создана!");
            }
            case 3 -> {
                System.out.print("Введите начальный индекс подстроки: ");
                int start = readIndex();
                System.out.print("Введите конечный индекс подстроки: ");
                int end = readIndex();
                String substr1 = getSubstring(string1, start, end);
                String substr2 = getSubstring(string2, start, end);
                if (substr1.isEmpty() && substr2.isEmpty()) break;
                String result = String.format("substrings from %d to %d: substr1: " + substr1 + "; substr2: " + substr2, start, end);
                System.out.println(result);

                executeUpdate(INSERT_QUERY, "substrings", substr1, substr2, result);
            }
            case 4 -> {
                String result = "UPPERCASE1: " + string1.toUpperCase() + ", LOWERCASE1: " + string1.toLowerCase() +
                        ", UPPERCASE2: " + string2.toUpperCase() + ", LOWERCASE2: " + string2.toLowerCase();
                System.out.println(result);
                executeUpdate(INSERT_QUERY, "case_change", string1, string2, result);
            }
            case 5 -> {
                SCANNER.nextLine();
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
                String filePath = "src/resources/task4.csv";
                String query = "SELECT * FROM TASK4";
                try (FileWriter fileWriter = new FileWriter(filePath);
                     ResultSet resultSet = executeQuery(query)) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        fileWriter.append('"').append(metaData.getColumnName(i)).append('"');
                        if (i < columnCount) fileWriter.append(";");
                    }
                    fileWriter.append("\n");

                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String value = resultSet.getString(i);
                            fileWriter.append('"');
                            if (value != null) {
                                fileWriter.append(value.replace("\"", "\"\""));
                            }
                            fileWriter.append('"');
                            if (i < columnCount) fileWriter.append(";");
                        }
                        fileWriter.append("\n");
                    }

                    System.out.println("Данные успешно экспортированы в файл CSV: " + filePath);

                    printTable();
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    System.out.println("Ошибка при экспорте данных в CSV.");
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
        String query = "SELECT * FROM TASK4";
        try (ResultSet resultSet = executeQuery(query)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static int readIndex() {
        int value;
        while (true) {
            if (SCANNER.hasNextInt()) {
                value = SCANNER.nextInt();
                SCANNER.nextLine();
                return value;
            } else {
                System.out.println("Введите целое число.");
                SCANNER.next();
            }
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
