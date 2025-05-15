import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberOne {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true";
    public final static String DATABASE_LOGIN = "root";
    public final static String DATABASE_PASSWORD = "root";
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK1 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "number1 DOUBLE, " +
            "number2 DOUBLE, " +
            "result DOUBLE)";
    public final static String INSERT_QUERY = "INSERT INTO TASK1 (operation, number1, number2, result) VALUES (?, ?, ?, ?)";
    public static boolean tableCreated = false;
    public static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS TASK1";


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

                if (!tableCreated && choice > 2 && choice <= 10) {
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
        System.out.println("3. Сложение чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("4. Вычитание чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("5. Умножение чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("6. Деление чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("7. Деление чисел по модулю (остаток), результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("8. Возведение числа в модуль, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("9. Возведение числа в степень, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("10. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран");
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
                System.out.print("Введите первое число: ");
                double a = scanDouble();
                System.out.print("Введите второе число: ");
                double b = scanDouble();
                double result = a + b;
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "addition", a, b, result);
            }
            case 4 -> {
                System.out.print("Введите первое число: ");
                double a = scanDouble();
                System.out.print("Введите второе число: ");
                double b = scanDouble();
                double result = a - b;
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "subtraction", a, b, result);
            }
            case 5 -> {
                System.out.print("Введите первое число: ");
                double a = scanDouble();
                System.out.print("Введите второе число: ");
                double b = scanDouble();
                double result = a * b;
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "multiplication", a, b, result);
            }
            case 6 -> {
                System.out.print("Введите первое число: ");
                double a = scanDouble();
                System.out.print("Введите второе число: ");
                double b = scanDouble();
                if (b == 0) {
                    System.out.println("Деление на ноль!");
                    break;
                }
                double result = a / b;
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "division", a, b, result);
            }
            case 7 -> {
                System.out.print("Введите первое число: ");
                double a = scanDouble();
                System.out.print("Введите второе число: ");
                double b = scanDouble();
                if (b == 0) {
                    System.out.println("Деление по модулю на ноль!");
                    break;
                }
                double result = a % b;
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "modulus", a, b, result);
            }
            case 8 -> {
                System.out.print("Введите число для возведения в модуль: ");
                double a = scanDouble();
                double result = Math.abs(a);
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "abs", a, null, result);
            }
            case 9 -> {
                System.out.print("Введите число: ");
                double a = scanDouble();
                System.out.print("Введите степень: ");
                double b = scanDouble();
                double result = Math.pow(a, b);
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "pow", a, b, result);
            }
            case 10 -> {
                System.out.print("Введите название файла с расширением (.xls): ");
                String fileName = SCANNER.nextLine().trim();

                while (!fileName.toLowerCase().endsWith(".xls")) {
                    System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
                    fileName = SCANNER.nextLine().trim();
                }

                String filePath = "C:/Users/User/Desktop/" + fileName;

                String exportQuery =
                        "SELECT 'id', 'operation', 'number1', 'number2', 'result' " +
                                "UNION ALL " +
                                "SELECT id, operation, number1, number2, result FROM TASK1 " +
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
        try (Connection conn = getConnection();
                Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM TASK1")) {

            System.out.printf("%-3s | %-20s | %-15s | %-15s | %-20s%n",
                    "ID", "Operation", "Number1", "Number2", "Result");
            System.out.println("-------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String op = rs.getString("operation");
                double n1 = rs.getDouble("number1");
                double n2 = rs.getDouble("number2");
                double result = rs.getDouble("result");

                System.out.printf("%-3d | %-20s | %-15.4f | %-15.4f | %-20.4f%n",
                        id, op, n1, n2, result);
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при выводе таблицы: " + e.getMessage());
        }
    }
}
