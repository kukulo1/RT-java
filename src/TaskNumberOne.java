import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberOne {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true"; //��� �� ������, ��� ������ ��� ����������� � ��; ?createDatabaseIfNotExist=true ���� ��� ��������������� �������� ��, ���� �� ��� ����
    public final static String DATABASE_LOGIN = "root"; //���� �����
    public final static String DATABASE_PASSWORD = "kukulo1"; //���� ���� ������ ���
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK1 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "number1 DOUBLE, " +
            "number2 DOUBLE, " +
            "result DOUBLE)";
    public final static String INSERT_QUERY = "INSERT INTO TASK1 (operation, number1, number2, result) VALUES (?, ?, ?, ?)";


    public static void main(String[] args) {
        int choice = 0;

        while (choice != -1) {
            printConsoleMenu();
            try {
                choice = SCANNER.nextInt();
                doAction(choice);
            } catch (NumberFormatException e) {
                System.out.println("������� ����� ��������!");
            }
        }
    }

    static void printConsoleMenu() {
        System.out.println("1. ������� ��� ������� �� MySQL");
        System.out.println("2. ������� ������� � MySQL");
        System.out.println("3. �������� �����, ��������� ��������� � MySQL � ����������� ������� � �������");
        System.out.println("4. ��������� �����, ��������� ��������� � MySQL � ����������� ������� � �������");
        System.out.println("5. ��������� �����, ��������� ��������� � MySQL � ����������� ������� � �������");
        System.out.println("6. ������� �����, ��������� ��������� � MySQL � ����������� ������� � �������");
        System.out.println("7. ������� ����� �� ������ (�������), ��������� ��������� � MySQL � ����������� ������� � �������");
        System.out.println("8. ���������� ����� � ������, ��������� ��������� � MySQL � ����������� ������� � �������");
        System.out.println("9. ���������� ����� � �������, ��������� ��������� � MySQL � ����������� ������� � �������");
        System.out.println("10. ��������� ��� ������ (�������������� ����������) �� MySQL � Excel � ������� �� �����");
        System.out.println("-1. ��������� ���������� ���������");
        System.out.print("�������� ��������: ");
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
                    System.out.println("������� �� �������.");
                } else {
                    tables.forEach(System.out::println);
                }
            }
            case 2 -> {
                executeUpdate(CREATE_TABLE_QUERY);
                System.out.println("������� �������!");
            }
            case 3 -> {
                System.out.print("������� ������ �����: ");
                double a = scanDouble();
                System.out.print("������� ������ �����: ");
                double b = scanDouble();
                double result = a + b;
                System.out.println("���������: " + result);
                executeUpdate(INSERT_QUERY, "addition", a, b, result);
            }
            case 4 -> {
                System.out.print("������� ������ �����: ");
                double a = scanDouble();
                System.out.print("������� ������ �����: ");
                double b = scanDouble();
                double result = a - b;
                System.out.println("���������: " + result);
                executeUpdate(INSERT_QUERY, "subtraction", a, b, result);
            }
            case 5 -> {
                System.out.print("������� ������ �����: ");
                double a = scanDouble();
                System.out.print("������� ������ �����: ");
                double b = scanDouble();
                double result = a * b;
                System.out.println("���������: " + result);
                executeUpdate(INSERT_QUERY, "multiplication", a, b, result);
            }
            case 6 -> {
                System.out.print("������� ������ �����: ");
                double a = scanDouble();
                System.out.print("������� ������ �����: ");
                double b = scanDouble();
                if (b == 0) {
                    System.out.println("������� �� ����!");
                    break;
                }
                double result = a / b;
                System.out.println("���������: " + result);
                executeUpdate(INSERT_QUERY, "division", a, b, result);
            }
            case 7 -> {
                System.out.print("������� ������ �����: ");
                double a = scanDouble();
                System.out.print("������� ������ �����: ");
                double b = scanDouble();
                if (b == 0) {
                    System.out.println("������� �� ������ �� ����!");
                    break;
                }
                double result = a % b;
                System.out.println("���������: " + result);
                executeUpdate(INSERT_QUERY, "modulus", a, b, result);
            }
            case 8 -> {
                System.out.print("������� ����� ��� ���������� � ������: ");
                double a = scanDouble();
                double result = Math.abs(a);
                System.out.println("���������: " + result);
                executeUpdate(INSERT_QUERY, "abs", a, null, result);
            }
            case 9 -> {
                System.out.print("������� �����: ");
                double a = scanDouble();
                System.out.print("������� �������: ");
                double b = scanDouble();
                double result = Math.pow(a, b);
                System.out.println("���������: " + result);
                executeUpdate(INSERT_QUERY, "pow", a, b, result);
            }
            case 10 -> {
                String filePath = "src/resources/task1.csv";
                String query = "SELECT * FROM TASK1";
                try (FileWriter fileWriter = new FileWriter(filePath);
                    ResultSet resultSet = executeQuery(query)) {                    ResultSetMetaData metaData = resultSet.getMetaData();
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

                    System.out.println("������ ������� �������������� � ���� CSV: " + filePath);

                    printTable();
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    System.out.println("������ ��� �������� ������ � CSV.");
                }
            }
            case -1 -> {
                System.out.println("����� �� ���������...");
            }
            default -> {
                System.out.println("�������� �����. ���������.");
            }
        }
    }
    static double scanDouble() {
        while (!SCANNER.hasNextDouble()) {
            System.out.println("������ �����. ������� �����!");
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
        String query = "SELECT * FROM TASK1";
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
