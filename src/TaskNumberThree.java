import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberThree {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true"; //тут не трогай, это ссылка для подключения к БД; ?createDatabaseIfNotExist=true надо для автоматического создания БД, если ее еще нету
    public final static String DATABASE_LOGIN = "root"; //сюда логин
    public final static String DATABASE_PASSWORD = "root"; //сюда свой пароль суй
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK3 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "number INT, " +
            "result TEXT)";
    public final static String INSERT_QUERY = "INSERT INTO TASK3 (operation, number, result) VALUES (?, ?, ?)";


    public static void main(String[] args) {
        int choice = 0;

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
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Проверить числа на целочисленность и чётность, результат сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Сохранить все данные из MySQL в Excel и вывести на экран.");
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
                String filePath = "src/resources/task3.csv";
                String query = "SELECT * FROM TASK3";
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
        String query = "SELECT * FROM TASK3";
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
}
